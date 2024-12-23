package com.example.epictodo.home

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epictodo.R
import com.example.epictodo.database.EventDatabase
import com.example.epictodo.home.event.AddEventActivity
import com.example.epictodo.home.event.EventAdapter
import com.example.epictodo.home.event.EventViewModel
import com.example.epictodo.home.event.EventViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var calendarView: AppleStyleCalendarView
    private lateinit var monthYearText: TextView
    private lateinit var eventList: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventViewModel: EventViewModel
    private lateinit var addEventButton: FloatingActionButton
    private lateinit var previousMonthButton: ImageButton
    private lateinit var nextMonthButton: ImageButton
    private var currentViewMode = ViewMode.MONTH

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        monthYearText = view.findViewById(R.id.monthYearText)
        eventList = view.findViewById(R.id.eventList)
        addEventButton = view.findViewById(R.id.addEventButton)
        previousMonthButton = view.findViewById(R.id.previousMonthButton)
        nextMonthButton = view.findViewById(R.id.nextMonthButton)

        setupCalendar()
        setupEventList()
        setupAddEventButton()
        setupMonthNavigation()
        setupSwipeGestures()

        return view
    }

    private fun setupCalendar() {
        val currentDate = Calendar.getInstance()
        updateMonthYearText(currentDate)
        calendarView.setMonth(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH))

        calendarView.setOnDayClickListener { selectedDate ->
            updateEventList(selectedDate)
        }
    }

    private fun updateMonthYearText(date: Calendar) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearText.text = sdf.format(date.time)
    }

    private fun setupEventList() {
        eventList.layoutManager = LinearLayoutManager(context)
        eventAdapter = EventAdapter(emptyList())
        eventList.adapter = eventAdapter

        val dao = EventDatabase.getDatabase(requireContext()).eventDao()
        val viewModelFactory = EventViewModelFactory(dao)
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        eventViewModel.allEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.updateEvents(events)
        }
    }

    private fun updateEventList(selectedDate: Calendar) {
        val startOfDay = Calendar.getInstance().apply {
            time = selectedDate.time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endOfDay = Calendar.getInstance().apply {
            time = selectedDate.time
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        eventViewModel.getEventsBetweenDates(startOfDay.timeInMillis, endOfDay.timeInMillis)
            .observe(viewLifecycleOwner) { events ->
                eventAdapter.updateEvents(events)
            }
    }

    private fun setupAddEventButton() {
        addEventButton.setOnClickListener {
            val intent = Intent(context, AddEventActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupMonthNavigation() {
        previousMonthButton.setOnClickListener {
            calendarView.previousMonth()
            updateMonthYearText(calendarView.getCurrentDate())
        }

        nextMonthButton.setOnClickListener {
            calendarView.nextMonth()
            updateMonthYearText(calendarView.getCurrentDate())
        }
    }

    private fun setupSwipeGestures() {
        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (kotlin.math.abs(velocityY) > kotlin.math.abs(velocityX)) {
                    if (velocityY < 0) {
                        // Swipe up
                        if (currentViewMode == ViewMode.MONTH) {
                            switchToWeekView()
                        }
                    } else {
                        // Swipe down
                        if (currentViewMode == ViewMode.WEEK) {
                            switchToMonthView()
                        }
                    }
                    return true
                }
                return false
            }
        })

        calendarView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }
    private fun switchToWeekView() {
        currentViewMode = ViewMode.WEEK
        calendarView.switchToWeekView()
        // Update UI for week view
    }

    private fun switchToMonthView() {
        currentViewMode = ViewMode.MONTH
        calendarView.switchToMonthView()
        // Update UI for month view
    }

    // ... (other methods)

    enum class ViewMode {
        MONTH, WEEK
    }
}