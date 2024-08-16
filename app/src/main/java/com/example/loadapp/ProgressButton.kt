package com.example.loadapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes

class ProgressButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var text = "Text"
    private var textActive = "Text"
    private var width = 32f
    private var height = 32f
    private var inProgress = false
    private var progress = 0f
    private var backgroundcolor = 0
    private var activeColor = 0
    private var textColor = 0
    private var textActiveColor = 0
    private var circleColor = 0
    private var circleRadius = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }
    private val textRect = Rect()

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.ProgressButton) {
            text = getString(R.styleable.ProgressButton_text) ?: text
            textActive = getString(R.styleable.ProgressButton_textActive) ?: textActive
            backgroundcolor = getColor(R.styleable.ProgressButton_backgroundColor, resources.getColor(R.color.primary, null))
            activeColor = getColor(R.styleable.ProgressButton_activeColor, resources.getColor(R.color.secondary, null))
            textColor = getColor(R.styleable.ProgressButton_textColor, resources.getColor(R.color.white, null))
            textActiveColor = getColor(R.styleable.ProgressButton_textActiveColor, resources.getColor(R.color.white, null))
            circleColor = getColor(R.styleable.ProgressButton_circleColor, resources.getColor(R.color.tertiary, null))
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        width = w.toFloat()
        height = h.toFloat()
        paint.textSize = height / 2
        circleRadius = height * 3 / 8
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = activeColor
        canvas.drawRect(0f, 0f, progress * width, height, paint)
        paint.color = backgroundcolor
        canvas.drawRect(progress * width, 0f, width, height, paint)

        val label: String
        when {
            inProgress -> {
                label = textActive
                paint.color = textActiveColor
            }
            else -> {
                label = text
                paint.color = textColor
            }
        }
        val metric = paint.getFontMetrics()
        val textHeight = metric.bottom - metric.top
        val y = height - (height - textHeight) / 2 - metric.bottom
        canvas.drawText(label, width / 2, y, paint)

        paint.getTextBounds(label, 0, label.length, textRect)
        val circleX = width / 2 + textRect.width() / 2 + circleRadius + 32
        paint.color = circleColor
        canvas.drawArc(
            circleX - circleRadius,
            height / 2 - circleRadius,
            circleX + circleRadius,
            height / 2 + circleRadius,
            0f,
            progress * 360,
            true,
            paint
        )
    }

    fun setProgress(progress: Float) {
        if(progress >= 1f) {
            inProgress = false
            this.progress = 0f
        } else {
            inProgress = true
            this.progress = progress
        }
        invalidate()
    }
}