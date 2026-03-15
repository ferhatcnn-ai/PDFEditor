package com.pdfeditor.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class PDFEditorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Sistem temasına göre açık/koyu mod
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}
