package com.pdfeditor.app.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import com.github.gcacace.signaturepad.views.SignaturePad
import com.pdfeditor.app.R

class SignatureDialog(
    context: Context,
    private val onSignatureReady: (Bitmap) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_signature, null)
        setContentView(view)

        val signaturePad = view.findViewById<SignaturePad>(R.id.signaturePad)
        val btnClear = view.findViewById<Button>(R.id.btnClear)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {}
            override fun onSigned() { btnConfirm.isEnabled = true }
            override fun onClear() { btnConfirm.isEnabled = false }
        })

        btnClear.setOnClickListener { signaturePad.clear() }
        btnCancel.setOnClickListener { dismiss() }
        btnConfirm.setOnClickListener {
            if (!signaturePad.isEmpty) {
                onSignatureReady(signaturePad.transparentSignatureBitmap)
                dismiss()
            }
        }

        btnConfirm.isEnabled = false
        window?.setLayout(
            android.view.WindowManager.LayoutParams.MATCH_PARENT,
            android.view.WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}
