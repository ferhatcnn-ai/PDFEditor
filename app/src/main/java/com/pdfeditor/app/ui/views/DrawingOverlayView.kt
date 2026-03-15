package com.pdfeditor.app.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.sqrt

class DrawingOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    enum class Mode { NONE, PEN, HIGHLIGHTER, ERASER, TAP, RECTANGLE, CIRCLE, LINE, ARROW }

    // --- Çizim veri yapıları ---
    data class DrawPath(val path: Path, val paint: Paint)
    data class TextAnnotation(val text: String, val x: Float, val y: Float, val paint: Paint)
    data class ShapeAnnotation(val type: Mode, val startX: Float, val startY: Float,
                               val endX: Float, val endY: Float, val paint: Paint)
    data class BitmapAnnotation(val bitmap: Bitmap, var x: Float, var y: Float,
                                val width: Int, val height: Int)

    private val paths = mutableListOf<DrawPath>()
    private val redoPaths = mutableListOf<DrawPath>()
    private val texts = mutableListOf<TextAnnotation>()
    private val shapes = mutableListOf<ShapeAnnotation>()
    private val bitmaps = mutableListOf<BitmapAnnotation>()

    private var currentPath: Path? = null
    private var currentShapeStart: PointF? = null
    private var currentShapeEnd: PointF? = null

    private var currentMode = Mode.NONE
    private var currentColor = Color.BLACK
    private var currentStrokeWidth = 5f
    private var tapListener: ((Float, Float) -> Unit)? = null

    // Çizim için paint
    private val currentPaint: Paint get() = Paint().apply {
        color = currentColor
        strokeWidth = currentStrokeWidth
        isAntiAlias = true
        style = when (currentMode) {
            Mode.PEN, Mode.HIGHLIGHTER -> Paint.Style.STROKE
            Mode.ERASER -> Paint.Style.STROKE
            else -> Paint.Style.STROKE
        }
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        if (currentMode == Mode.ERASER) {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            strokeWidth = 40f
        }
    }

    // Önbellek bitmap
    private var cacheBitmap: Bitmap? = null
    private var cacheCanvas: Canvas? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cacheBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        cacheCanvas = Canvas(cacheBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Bitmap annotasyonları çiz
        bitmaps.forEach { ann ->
            canvas.drawBitmap(
                Bitmap.createScaledBitmap(ann.bitmap, ann.width, ann.height, true),
                ann.x, ann.y, null
            )
        }

        // Tamamlanan yolları çiz
        paths.forEach { dp -> canvas.drawPath(dp.path, dp.paint) }

        // Devam eden yolu çiz
        currentPath?.let { path ->
            canvas.drawPath(path, currentPaint)
        }

        // Şekilleri çiz
        shapes.forEach { shape -> drawShape(canvas, shape) }

        // Anlık şekil önizlemesi
        if (currentShapeStart != null && currentShapeEnd != null) {
            val previewPaint = Paint(currentPaint).apply { alpha = 180 }
            drawShapePreview(canvas, currentShapeStart!!, currentShapeEnd!!, previewPaint)
        }

        // Metin annotasyonları çiz
        texts.forEach { ann ->
            canvas.drawText(ann.text, ann.x, ann.y, ann.paint)
        }
    }

    private fun drawShape(canvas: Canvas, shape: ShapeAnnotation) {
        when (shape.type) {
            Mode.RECTANGLE -> canvas.drawRect(shape.startX, shape.startY, shape.endX, shape.endY, shape.paint)
            Mode.CIRCLE -> {
                val cx = (shape.startX + shape.endX) / 2
                val cy = (shape.startY + shape.endY) / 2
                val rx = abs(shape.endX - shape.startX) / 2
                val ry = abs(shape.endY - shape.startY) / 2
                canvas.drawOval(cx - rx, cy - ry, cx + rx, cy + ry, shape.paint)
            }
            Mode.LINE -> canvas.drawLine(shape.startX, shape.startY, shape.endX, shape.endY, shape.paint)
            Mode.ARROW -> drawArrow(canvas, shape.startX, shape.startY, shape.endX, shape.endY, shape.paint)
            else -> {}
        }
    }

    private fun drawShapePreview(canvas: Canvas, start: PointF, end: PointF, paint: Paint) {
        when (currentMode) {
            Mode.RECTANGLE -> canvas.drawRect(start.x, start.y, end.x, end.y, paint)
            Mode.CIRCLE -> {
                val cx = (start.x + end.x) / 2
                val cy = (start.y + end.y) / 2
                val rx = abs(end.x - start.x) / 2
                val ry = abs(end.y - start.y) / 2
                canvas.drawOval(cx - rx, cy - ry, cx + rx, cy + ry, paint)
            }
            Mode.LINE -> canvas.drawLine(start.x, start.y, end.x, end.y, paint)
            Mode.ARROW -> drawArrow(canvas, start.x, start.y, end.x, end.y, paint)
            else -> {}
        }
    }

    private fun drawArrow(canvas: Canvas, x1: Float, y1: Float, x2: Float, y2: Float, paint: Paint) {
        canvas.drawLine(x1, y1, x2, y2, paint)
        val angle = Math.atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())
        val arrowLen = 40f
        val arrowAngle = Math.PI / 6
        val ax1 = (x2 - arrowLen * Math.cos(angle - arrowAngle)).toFloat()
        val ay1 = (y2 - arrowLen * Math.sin(angle - arrowAngle)).toFloat()
        val ax2 = (x2 - arrowLen * Math.cos(angle + arrowAngle)).toFloat()
        val ay2 = (y2 - arrowLen * Math.sin(angle + arrowAngle)).toFloat()
        canvas.drawLine(x2, y2, ax1, ay1, paint)
        canvas.drawLine(x2, y2, ax2, ay2, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (currentMode == Mode.NONE) return false

        val x = event.x
        val y = event.y

        when (currentMode) {
            Mode.PEN, Mode.HIGHLIGHTER, Mode.ERASER -> handleFreeDrawing(event, x, y)
            Mode.RECTANGLE, Mode.CIRCLE, Mode.LINE, Mode.ARROW -> handleShapeDrawing(event, x, y)
            Mode.TAP -> {
                if (event.action == MotionEvent.ACTION_UP) {
                    tapListener?.invoke(x, y)
                }
            }
            else -> {}
        }
        invalidate()
        return true
    }

    private fun handleFreeDrawing(event: MotionEvent, x: Float, y: Float) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path().apply { moveTo(x, y) }
                redoPaths.clear()
            }
            MotionEvent.ACTION_MOVE -> currentPath?.lineTo(x, y)
            MotionEvent.ACTION_UP -> {
                currentPath?.let {
                    it.lineTo(x, y)
                    paths.add(DrawPath(it, currentPaint))
                }
                currentPath = null
            }
        }
    }

    private fun handleShapeDrawing(event: MotionEvent, x: Float, y: Float) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentShapeStart = PointF(x, y)
                currentShapeEnd = PointF(x, y)
            }
            MotionEvent.ACTION_MOVE -> currentShapeEnd = PointF(x, y)
            MotionEvent.ACTION_UP -> {
                currentShapeStart?.let { start ->
                    shapes.add(ShapeAnnotation(currentMode, start.x, start.y, x, y, currentPaint))
                }
                currentShapeStart = null
                currentShapeEnd = null
            }
        }
    }

    // --- Public API ---

    fun setMode(mode: Mode) {
        currentMode = mode
        invalidate()
    }

    fun setColor(color: Int) { currentColor = color }
    fun setStrokeWidth(width: Float) { currentStrokeWidth = width }

    fun setOnTapListener(listener: (Float, Float) -> Unit) {
        tapListener = listener
    }

    fun addText(text: String, x: Float, y: Float, textSize: Float, color: Int) {
        val paint = Paint().apply {
            this.color = color
            this.textSize = textSize
            isAntiAlias = true
        }
        texts.add(TextAnnotation(text, x, y, paint))
        invalidate()
    }

    fun addSignature(bitmap: Bitmap) {
        val w = (width * 0.4f).toInt()
        val h = (bitmap.height * w.toFloat() / bitmap.width).toInt()
        bitmaps.add(BitmapAnnotation(bitmap, (width - w) / 2f, (height - h) / 2f, w, h))
        invalidate()
    }

    fun undo() {
        if (paths.isNotEmpty()) {
            redoPaths.add(paths.removeLast())
            invalidate()
        }
    }

    fun redo() {
        if (redoPaths.isNotEmpty()) {
            paths.add(redoPaths.removeLast())
            invalidate()
        }
    }

    fun clear() {
        paths.clear()
        redoPaths.clear()
        texts.clear()
        shapes.clear()
        bitmaps.clear()
        invalidate()
    }

    fun exportBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(
            if (width > 0) width else 1,
            if (height > 0) height else 1,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}
