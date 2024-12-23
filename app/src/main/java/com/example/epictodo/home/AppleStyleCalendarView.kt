package com.example.epictodo.home

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.epictodo.R
import java.util.*
import kotlin.math.max
import kotlin.math.min

class AppleStyleCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val calendar = Calendar.getInstance()
    private val today = Calendar.getInstance()
    private var selectedDate: Calendar = today.clone() as Calendar
    private var cellSize = 0f
    private var dayClickListener: ((Calendar) -> Unit)? = null
    private var viewChangeListener: ((ViewMode, Calendar) -> Unit)? = null

    private val daysInWeek = 7
    private val maxWeeksToShow = 6
    private var currentWeeksToShow = maxWeeksToShow

    private var currentViewMode = ViewMode.MONTH

    fun getCurrentViewMode(): ViewMode {
        return currentViewMode
    }

    private var transitionProgress = 1f
    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 300
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            transitionProgress = animation.animatedValue as Float
            requestLayout()
            invalidate()
        }
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.black)
        strokeWidth = 2f
    }

    private val dragBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.black)
    }

    private var dragBarHeight = 20f
    private var dragBarMargin = 10f
    private var isDragging = false
    private var lastTouchY = 0f
    private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (kotlin.math.abs(velocityX) > kotlin.math.abs(velocityY)) {
                if (velocityX > 0) {
                    previousPeriod()
                } else {
                    nextPeriod()
                }
                return true
            }
            return false
        }
    })

    private var initialTouchY = 0f
    private var isSwiping = false
    private val swipeThreshold = 50f

    private val weekDays = arrayOf("日", "一", "二", "三", "四", "五", "六")

    init {
        paint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        cellSize = (width / daysInWeek).toFloat()
        val height = (cellSize * (currentWeeksToShow + 1) + dragBarHeight + 2 * dragBarMargin).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)
        drawWeekDays(canvas)
        when (currentViewMode) {
            ViewMode.MONTH -> drawMonthView(canvas)
            ViewMode.WEEK -> drawWeekView(canvas)
        }
        drawDragBar(canvas)
    }

    private fun drawWeekDays(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, android.R.color.black)
        paint.textSize = cellSize / 3
        for (i in 0 until daysInWeek) {
            val x = i * cellSize + cellSize / 2
            val y = cellSize / 2
            canvas.drawText(weekDays[i], x, y + paint.textSize / 3, paint)
        }
    }

    private fun drawMonthView(canvas: Canvas) {
        val monthDays = getMonthDays()
        val firstDayOfWeek = getFirstDayOfWeek()
        var day = 1
        var nextMonthDay = 1
        val prevMonthDays = getPreviousMonthDays()
        var prevMonthDay = prevMonthDays - firstDayOfWeek + 1

        for (week in 0 until currentWeeksToShow) {
            for (weekDay in 0 until daysInWeek) {
                val x = weekDay * cellSize + cellSize / 2
                val y = (week + 1) * cellSize + cellSize / 2

                when {
                    week == 0 && weekDay < firstDayOfWeek -> {
                        drawDay(canvas, prevMonthDay, x, y, true)
                        prevMonthDay++
                    }
                    day <= monthDays -> {
                        drawDay(canvas, day, x, y, false)
                        day++
                    }
                    else -> {
                        drawDay(canvas, nextMonthDay, x, y, true)
                        nextMonthDay++
                    }
                }
            }
        }
    }

    private fun drawWeekView(canvas: Canvas) {
        val weekStart = getWeekStart()
        for (dayOffset in 0 until daysInWeek) {
            val day = weekStart.clone() as Calendar
            day.add(Calendar.DAY_OF_MONTH, dayOffset)
            val x = dayOffset * cellSize + cellSize / 2
            val y = cellSize * 1.5f
            drawDay(canvas, day.get(Calendar.DAY_OF_MONTH), x, y, day.get(Calendar.MONTH) != calendar.get(Calendar.MONTH))
        }
    }

    private fun drawDay(canvas: Canvas, day: Int, x: Float, y: Float, isOtherMonth: Boolean) {
        val isToday = isToday(day)
        val isSelected = isSelected(day)

        if (isSelected && !isOtherMonth) {
            paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            canvas.drawCircle(x, y, cellSize / 2.5f, paint)
        } else if (isToday && !isOtherMonth) {
            paint.color = ContextCompat.getColor(context, R.color.red_light)
            canvas.drawCircle(x, y, cellSize / 2.5f, paint)
        }

        paint.color = when {
            isOtherMonth -> ContextCompat.getColor(context, android.R.color.darker_gray)
            isSelected || isToday -> ContextCompat.getColor(context, android.R.color.white)
            else -> ContextCompat.getColor(context, android.R.color.black)
        }
        paint.textSize = cellSize / 3
        canvas.drawText(day.toString(), x, y + paint.textSize / 3, paint)
    }

    private fun drawDragBar(canvas: Canvas) {
        val barWidth = width * 0.2f
        val barLeft = (width - barWidth) / 2
        val barTop = height - dragBarHeight - dragBarMargin
        canvas.drawRoundRect(
            barLeft,
            barTop,
            barLeft + barWidth,
            barTop + dragBarHeight,
            dragBarHeight / 2,
            dragBarHeight / 2,
            dragBarPaint
        )
    }

    private fun isToday(day: Int): Boolean {
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                day == today.get(Calendar.DAY_OF_MONTH)
    }

    private fun isSelected(day: Int): Boolean {
        return selectedDate.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                selectedDate.get(Calendar.DAY_OF_MONTH) == day
    }

    private fun getMonthDays(): Int {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun getFirstDayOfWeek(): Int {
        val temp = calendar.clone() as Calendar
        temp.set(Calendar.DAY_OF_MONTH, 1)
        return temp.get(Calendar.DAY_OF_WEEK) - 1
    }

    private fun getPreviousMonthDays(): Int {
        val temp = calendar.clone() as Calendar
        temp.add(Calendar.MONTH, -1)
        return temp.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchY = event.y
                isSwiping = false
                if (event.y > height - dragBarHeight - 2 * dragBarMargin) {
                    isDragging = true
                    lastTouchY = event.y
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = event.y - initialTouchY
                if (isDragging) {
                    if (kotlin.math.abs(deltaY) > touchSlop) {
                        isSwiping = true
                        updateViewMode(deltaY)
                    }
                } else if (kotlin.math.abs(deltaY) > swipeThreshold) {
                    isSwiping = true
                    if (deltaY < 0) {
                        switchToWeekView()
                    } else {
                        switchToMonthView()
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    isDragging = false
                    snapToView()
                } else if (!isSwiping) {
                    handleDateClick(event)
                }
                isSwiping = false
            }
        }

        if (gestureDetector.onTouchEvent(event)) {
            return true
        }

        return true
    }

    private fun updateViewMode(deltaY: Float) {
        val targetWeeks = if (deltaY < 0) 1 else maxWeeksToShow
        currentViewMode = if (targetWeeks == 1) ViewMode.WEEK else ViewMode.MONTH
        animator.setFloatValues(currentWeeksToShow.toFloat(), targetWeeks.toFloat())
        animator.start()
        viewChangeListener?.invoke(currentViewMode, calendar)
    }

    private fun snapToView() {
        val targetWeeks = if (currentWeeksToShow > maxWeeksToShow / 2) maxWeeksToShow else 1
        currentViewMode = if (targetWeeks == maxWeeksToShow) ViewMode.MONTH else ViewMode.WEEK
        animator.setFloatValues(currentWeeksToShow.toFloat(), targetWeeks.toFloat())
        animator.start()
        viewChangeListener?.invoke(currentViewMode, calendar)
    }

    private fun switchToWeekView() {
        if (currentViewMode != ViewMode.WEEK) {
            currentViewMode = ViewMode.WEEK
            currentWeeksToShow = 1
            requestLayout()
            invalidate()
            viewChangeListener?.invoke(currentViewMode, calendar)
        }
    }

    private fun switchToMonthView() {
        if (currentViewMode != ViewMode.MONTH) {
            currentViewMode = ViewMode.MONTH
            currentWeeksToShow = maxWeeksToShow
            requestLayout()
            invalidate()
            viewChangeListener?.invoke(currentViewMode, calendar)
        }
    }

    private fun handleDateClick(event: MotionEvent) {
        val row = ((event.y - cellSize) / cellSize).toInt()
        val column = (event.x / cellSize).toInt()
        if (row >= 0 && row < currentWeeksToShow && column >= 0 && column < daysInWeek) {
            val clickedDate = getDateFromPosition(row, column)
            selectedDate = clickedDate
            dayClickListener?.invoke(clickedDate)
            invalidate()
        }
    }

    private fun getDateFromPosition(row: Int, column: Int): Calendar {
        val clickedDate = calendar.clone() as Calendar
        clickedDate.set(Calendar.DAY_OF_MONTH, 1)
        clickedDate.add(Calendar.DAY_OF_MONTH, row * 7 + column - getFirstDayOfWeek())
        return clickedDate
    }

    fun setOnDayClickListener(listener: (Calendar) -> Unit) {
        dayClickListener = listener
    }

    fun setOnViewChangeListener(listener: (ViewMode, Calendar) -> Unit) {
        viewChangeListener = listener
    }

    fun setMonth(year: Int, month: Int) {
        calendar.set(year, month, 1)
        invalidate()
    }

    fun previousPeriod() {
        when (currentViewMode) {
            ViewMode.MONTH -> calendar.add(Calendar.MONTH, -1)
            ViewMode.WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
        }
        adjustSelectedDate()
        invalidate()
        viewChangeListener?.invoke(currentViewMode, calendar)
    }

    fun nextPeriod() {
        when (currentViewMode) {
            ViewMode.MONTH -> calendar.add(Calendar.MONTH, 1)
            ViewMode.WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }
        adjustSelectedDate()
        invalidate()
        viewChangeListener?.invoke(currentViewMode, calendar)
    }

    private fun adjustSelectedDate() {
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (selectedDate.get(Calendar.DAY_OF_MONTH) > maxDays) {
            selectedDate.set(Calendar.DAY_OF_MONTH, maxDays)
        }
        selectedDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        selectedDate.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
    }

    fun getCurrentDate(): Calendar = calendar.clone() as Calendar

    private fun getWeekStart(): Calendar {
        val weekStart = selectedDate.clone() as Calendar
        weekStart.set(Calendar.DAY_OF_WEEK, weekStart.firstDayOfWeek)
        return weekStart
    }

    enum class ViewMode {
        MONTH, WEEK
    }
}

