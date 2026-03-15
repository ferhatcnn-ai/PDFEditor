package com.pdfeditor.app.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.widget.*
import com.pdfeditor.app.R

class TextInputDialog(
    context: Context,
    private val onTextReady: (String, Float, Int) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_text_input)

        val etText = findViewById<EditText>(R.id.etText)
        val seekTextSize = findViewById<SeekBar>(R.id.seekTextSize)
        val tvTextSizeValue = findViewById<TextView>(R.id.tvTextSizeValue)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val rgColors = findViewById<RadioGroup>(R.id.rgColors)

        var selectedColor = Color.BLACK
        var textSize = 40f

        seekTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSize = 20f + progress.toFloat()
                tvTextSizeValue.text = "${textSize.toInt()}sp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        rgColors.setOnCheckedChangeListener { _, checkedId ->
            selectedColor = when (checkedId) {
                R.id.rbBlack -> Color.BLACK
                R.id.rbRed -> Color.RED
                R.id.rbBlue -> Color.BLUE
                else -> Color.BLACK
            }
        }

        btnCancel.setOnClickListener { dismiss() }
        btnConfirm.setOnClickListener {
            val text = etText.text.toString()
            if (text.isNotEmpty()) {
                onTextReady(text, textSize, selectedColor)
                dismiss()
            } else {
                etText.error = context.getString(R.string.text_required)
            }
        }

        window?.setLayout(
            android.view.WindowManager.LayoutParams.MATCH_PARENT,
            android.view.WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}
