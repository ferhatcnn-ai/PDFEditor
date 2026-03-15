package com.pdfeditor.app.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.pdfeditor.app.R
import com.pdfeditor.app.databinding.ActivityEditorBinding
import com.pdfeditor.app.ui.dialogs.SignatureDialog
import com.pdfeditor.app.ui.dialogs.TextInputDialog
import com.pdfeditor.app.ui.views.DrawingOverlayView
import com.pdfeditor.app.utils.PdfEditUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class EditorActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PDF_PATH = "extra_pdf_path"
    }

    private lateinit var binding: ActivityEditorBinding
    private var pdfPath: String = ""
    private var currentPage: Int = 0
    private var currentTool: Tool = Tool.NONE
    private var drawingColor: Int = Color.BLACK
    private var strokeWidth: Float = 5f

    enum class Tool { NONE, PEN, HIGHLIGHTER, TEXT, SIGNATURE, SHAPES, ERASER }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pdfPath = intent.getStringExtra(EXTRA_PDF_PATH) ?: run {
            finish()
            return
        }

        val file = File(pdfPath)
        supportActionBar?.title = file.nameWithoutExtension
        loadPdf(file)
        setupToolbar()
        setupDrawingOverlay()
    }

    private fun loadPdf(file: File) {
        binding.pdfView.fromFile(file)
            .defaultPage(0)
            .scrollHandle(DefaultScrollHandle(this))
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .onPageChange { page, _ -> currentPage = page }
            .onError { binding.tvError.visibility = View.VISIBLE }
            .load()
    }

    private fun setupToolbar() {
        // Araç butonları
        binding.btnPen.setOnClickListener { selectTool(Tool.PEN) }
        binding.btnHighlighter.setOnClickListener { selectTool(Tool.HIGHLIGHTER) }
        binding.btnText.setOnClickListener { selectTool(Tool.TEXT) }
        binding.btnSignature.setOnClickListener { showSignatureDialog() }
        binding.btnShapes.setOnClickListener { showShapesMenu() }
        binding.btnEraser.setOnClickListener { selectTool(Tool.ERASER) }
        binding.btnUndo.setOnClickListener { binding.drawingOverlay.undo() }
        binding.btnRedo.setOnClickListener { binding.drawingOverlay.redo() }
        binding.btnColor.setOnClickListener { showColorPicker() }
        binding.btnStrokeWidth.setOnClickListener { showStrokeWidthPicker() }
    }

    private fun setupDrawingOverlay() {
        binding.drawingOverlay.setOnTapListener { x, y ->
            when (currentTool) {
                Tool.TEXT -> showTextInputDialog(x, y)
                else -> {}
            }
        }
    }

    private fun selectTool(tool: Tool) {
        currentTool = if (currentTool == tool) Tool.NONE else tool
        updateToolUI()

        when (tool) {
            Tool.PEN -> {
                binding.drawingOverlay.setMode(DrawingOverlayView.Mode.PEN)
                binding.drawingOverlay.setColor(drawingColor)
                binding.drawingOverlay.setStrokeWidth(strokeWidth)
            }
            Tool.HIGHLIGHTER -> {
                binding.drawingOverlay.setMode(DrawingOverlayView.Mode.HIGHLIGHTER)
                binding.drawingOverlay.setColor(Color.argb(100, 255, 235, 59))
                binding.drawingOverlay.setStrokeWidth(30f)
            }
            Tool.ERASER -> {
                binding.drawingOverlay.setMode(DrawingOverlayView.Mode.ERASER)
            }
            Tool.TEXT -> {
                binding.drawingOverlay.setMode(DrawingOverlayView.Mode.TAP)
            }
            Tool.NONE -> {
                binding.drawingOverlay.setMode(DrawingOverlayView.Mode.NONE)
            }
            else -> {}
        }
    }

    private fun updateToolUI() {
        val buttons = listOf(
            Tool.PEN to binding.btnPen,
            Tool.HIGHLIGHTER to binding.btnHighlighter,
            Tool.TEXT to binding.btnText,
            Tool.ERASER to binding.btnEraser
        )
        buttons.forEach { (tool, btn) ->
            btn.isSelected = currentTool == tool
            btn.alpha = if (currentTool == tool) 1.0f else 0.6f
        }
    }

    private fun showSignatureDialog() {
        SignatureDialog(this) { signatureBitmap ->
            binding.drawingOverlay.addSignature(signatureBitmap)
        }.show()
    }

    private fun showTextInputDialog(x: Float, y: Float) {
        TextInputDialog(this) { text, textSize, color ->
            binding.drawingOverlay.addText(text, x, y, textSize, color)
        }.show()
    }

    private fun showShapesMenu() {
        val shapes = arrayOf(
            getString(R.string.shape_rectangle),
            getString(R.string.shape_circle),
            getString(R.string.shape_line),
            getString(R.string.shape_arrow)
        )
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_shape))
            .setItems(shapes) { _, which ->
                val mode = when (which) {
                    0 -> DrawingOverlayView.Mode.RECTANGLE
                    1 -> DrawingOverlayView.Mode.CIRCLE
                    2 -> DrawingOverlayView.Mode.LINE
                    3 -> DrawingOverlayView.Mode.ARROW
                    else -> DrawingOverlayView.Mode.PEN
                }
                currentTool = Tool.SHAPES
                binding.drawingOverlay.setMode(mode)
                binding.drawingOverlay.setColor(drawingColor)
                binding.drawingOverlay.setStrokeWidth(strokeWidth)
                updateToolUI()
            }.show()
    }

    private fun showColorPicker() {
        val colors = intArrayOf(
            Color.BLACK, Color.RED, Color.BLUE, Color.GREEN,
            Color.rgb(255, 165, 0), Color.MAGENTA, Color.CYAN,
            Color.rgb(139, 69, 19)
        )
        val colorNames = arrayOf("Siyah", "Kırmızı", "Mavi", "Yeşil", "Turuncu", "Mor", "Cyan", "Kahverengi")

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_color))
            .setItems(colorNames) { _, which ->
                drawingColor = colors[which]
                binding.drawingOverlay.setColor(drawingColor)
                binding.btnColor.setBackgroundColor(drawingColor)
            }.show()
    }

    private fun showStrokeWidthPicker() {
        val widths = arrayOf("İnce (2px)", "Normal (5px)", "Kalın (10px)", "Çok Kalın (20px)")
        val widthValues = floatArrayOf(2f, 5f, 10f, 20f)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_stroke_width))
            .setItems(widths) { _, which ->
                strokeWidth = widthValues[which]
                binding.drawingOverlay.setStrokeWidth(strokeWidth)
            }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { onBackPressed(); true }
            R.id.action_save -> { savePdf(); true }
            R.id.action_share -> { sharePdf(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun savePdf() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            try {
                val overlay = binding.drawingOverlay.exportBitmap()
                val outputFile = withContext(Dispatchers.IO) {
                    PdfEditUtils.applyOverlayToPdf(
                        context = this@EditorActivity,
                        inputPath = pdfPath,
                        overlayBitmap = overlay,
                        pageIndex = currentPage
                    )
                }
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@EditorActivity, getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show()
                // Kaydedilen dosyayı yeniden yükle
                pdfPath = outputFile.absolutePath
                loadPdf(outputFile)
                binding.drawingOverlay.clear()
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@EditorActivity, getString(R.string.error_saving), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sharePdf() {
        val file = File(pdfPath)
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_pdf)))
    }
}
