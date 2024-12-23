package com.example.epictodo.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.epictodo.R
import java.util.*
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator

class AppleStyleCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val calendar = Calendar.getInstance()
    private val today = Calendar.getInstance()
    private var selectedDate: Calendar? = null
    private var cellSize = 0f
    private var dayClickListener: ((Calendar) -> Unit)? = null

    private val daysInWeek = 7
    private val weeksToShow = 6

    private var currentViewMode = ViewMode.MONTH
    private var transitionProgress = 1f
    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 300
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            transitionProgress = animation.animatedValue as Float
            invalidate()
        }
    }

    init {
        paint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        cellSize = (width / daysInWeek).toFloat()
        val height = (cellSize * weeksToShow).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (currentViewMode) {
            ViewMode.MONTH -> drawMonthView(canvas)
            ViewMode.WEEK -> drawWeekView(canvas)
        }
    }

    private fun drawMonthView(canvas: Canvas) {
        val monthDays = getMonthDays()
        var day = 1
        for (week in 0 until weeksToShow) {
            for (weekDay in 0 until daysInWeek) {
                if (day <= monthDays) {
                    val x = weekDay * cellSize + cellSize / 2
                    val y = week * cellSize + cellSize / 2
                    drawDay(canvas, day, x, y)
                    day++
                }
            }
        }
    }

    private fun drawWeekView(canvas: Canvas) {
        // Implement week view drawing
        // This will be similar to the month view, but only draw one week
    }


    private fun drawDay(canvas: Canvas, day: Int, x: Float, y: Float) {
        val isToday = isToday(day)
        val isSelected = isSelected(day)

        if (isSelected) {
            paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            canvas.drawCircle(x, y, cellSize / 2.5f, paint)
        } else if (isToday) {
            paint.color = ContextCompat.getColor(context, R.color.red_light)
            canvas.drawCircle(x, y, cellSize / 2.5f, paint)
        }

        paint.color = if (isSelected || isToday) {
            ContextCompat.getColor(context, android.R.color.white)
        } else {
            ContextCompat.getColor(context, android.R.color.black)
        }
        paint.textSize = cellSize / 3
        canvas.drawText(day.toString(), x, y + paint.textSize / 3, paint)
    }

    private fun isToday(day: Int): Boolean {
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                day == today.get(Calendar.DAY_OF_MONTH)
    }

    private fun isSelected(day: Int): Boolean {
        return selectedDate?.let {
            it.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                    it.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    it.get(Calendar.DAY_OF_MONTH) == day
        } ?: false
    }

    private fun getMonthDays(): Int {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val column = (event.x / cellSize).toInt()
            val row = (event.y / cellSize).toInt()
            val day = row * daysInWeek + column + 1
            if (day <= getMonthDays()) {
                selectedDate = Calendar.getInstance().apply {
                    set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day)
                }
                dayClickListener?.invoke(selectedDate!!)
                invalidate()
            }
        }
        return true
    }

    fun setOnDayClickListener(listener: (Calendar) -> Unit) {
        dayClickListener = listener
    }

    fun setMonth(year: Int, month: Int) {
        calendar.set(year, month, 1)
        invalidate()
    }

    fun switchToWeekView() {
        if (currentViewMode == ViewMode.MONTH) {
            currentViewMode = ViewMode.WEEK
            animator.start()
        }
    }

    fun switchToMonthView() {
        if (currentViewMode == ViewMode.WEEK) {
            currentViewMode = ViewMode.MONTH
            animator.reverse()
        }
    }

    fun previousMonth() {
        calendar.add(Calendar.MONTH, -1)
        invalidate()
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        invalidate()
    }

    fun getCurrentDate(): Calendar = calendar.clone() as Calendar

    enum class ViewMode {
        MONTH, WEEK
    }
}