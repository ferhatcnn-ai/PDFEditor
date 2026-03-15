package com.pdfeditor.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdfeditor.app.databinding.ItemRecentFileBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecentFilesAdapter(
    private val files: List<File>,
    private val onClick: (File) -> Unit
) : RecyclerView.Adapter<RecentFilesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRecentFileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(file: File) {
            binding.tvFileName.text = file.nameWithoutExtension
            val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date(file.lastModified()))
            binding.tvFileDate.text = date
            val sizeKb = file.length() / 1024
            binding.tvFileSize.text = if (sizeKb > 1024) "${sizeKb / 1024} MB" else "$sizeKb KB"
            binding.root.setOnClickListener { onClick(file) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(files[position])
    }

    override fun getItemCount() = files.size
}
