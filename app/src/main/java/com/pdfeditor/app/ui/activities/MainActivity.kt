package com.pdfeditor.app.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pdfeditor.app.R
import com.pdfeditor.app.databinding.ActivityMainBinding
import com.pdfeditor.app.ui.adapters.RecentFilesAdapter
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recentAdapter: RecentFilesAdapter
    private val recentFiles = mutableListOf<File>()

    private val openPdfLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { openPdfInEditor(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupClickListeners()
        loadRecentFiles()

        // Dışarıdan açılan PDF (intent ile)
        intent?.data?.let { uri ->
            if (intent.action == Intent.ACTION_VIEW) {
                openPdfInEditor(uri)
            }
        }
    }

    private fun setupRecyclerView() {
        recentAdapter = RecentFilesAdapter(recentFiles) { file ->
            openPdfFromFile(file)
        }
        binding.rvRecentFiles.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = recentAdapter
        }
    }

    private fun setupClickListeners() {
        binding.fabOpenPdf.setOnClickListener {
            openPdfLauncher.launch("application/pdf")
        }

        binding.btnOpenFile.setOnClickListener {
            openPdfLauncher.launch("application/pdf")
        }
    }

    private fun openPdfInEditor(uri: Uri) {
        try {
            // URI'yi önbelleğe kopyala
            val fileName = getFileNameFromUri(uri) ?: "document_${System.currentTimeMillis()}.pdf"
            val cacheFile = File(cacheDir, fileName)

            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }
            openPdfFromFile(cacheFile)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_opening_file), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openPdfFromFile(file: File) {
        val intent = Intent(this, EditorActivity::class.java).apply {
            putExtra(EditorActivity.EXTRA_PDF_PATH, file.absolutePath)
        }
        startActivity(intent)
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var name: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }

    private fun loadRecentFiles() {
        val cacheFiles = cacheDir.listFiles { file ->
            file.name.endsWith(".pdf")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()

        recentFiles.clear()
        recentFiles.addAll(cacheFiles)
        recentAdapter.notifyDataSetChanged()

        binding.tvEmptyState.visibility =
            if (recentFiles.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    override fun onResume() {
        super.onResume()
        loadRecentFiles()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_recents -> {
                clearRecentFiles()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearRecentFiles() {
        cacheDir.listFiles { file -> file.name.endsWith(".pdf") }?.forEach { it.delete() }
        loadRecentFiles()
        Toast.makeText(this, getString(R.string.recents_cleared), Toast.LENGTH_SHORT).show()
    }
}
