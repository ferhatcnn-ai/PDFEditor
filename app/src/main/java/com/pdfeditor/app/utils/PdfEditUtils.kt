package com.pdfeditor.app.utils

import android.content.Context
import android.graphics.Bitmap
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import java.io.ByteArrayOutputStream
import java.io.File

object PdfEditUtils {

    /**
     * Overlay bitmap'i PDF'in belirtilen sayfasına uygular ve yeni bir dosya döndürür.
     */
    fun applyOverlayToPdf(
        context: Context,
        inputPath: String,
        overlayBitmap: Bitmap,
        pageIndex: Int
    ): File {
        val outputFile = File(
            context.cacheDir,
            "edited_${System.currentTimeMillis()}.pdf"
        )

        val reader = PdfReader(inputPath)
        val writer = PdfWriter(outputFile)
        val pdfDoc = PdfDocument(reader, writer)
        val document = Document(pdfDoc)

        try {
            val page = pdfDoc.getPage(pageIndex + 1) // iText 1-tabanlı
            val pageSize = page.pageSize

            // Bitmap'i byte array'e çevir
            val bos = ByteArrayOutputStream()
            overlayBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
            val imageData = ImageDataFactory.create(bos.toByteArray())

            val image = Image(imageData).apply {
                setFixedPosition(pageIndex + 1, 0f, 0f)
                setWidth(pageSize.width)
                setHeight(pageSize.height)
                setOpacity(1f)
            }

            document.add(image)
        } finally {
            document.close()
            pdfDoc.close()
        }

        return outputFile
    }
}
