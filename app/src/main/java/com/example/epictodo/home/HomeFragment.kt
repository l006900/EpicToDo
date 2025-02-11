package com.example.epictodo.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
    private lateinit var previousMonthButton: ImageView
    private lateinit var nextMonthButton: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        monthYearText = view.findViewById(R.id.monthYearText)
        eventList = view.findViewById(R.id.eventList)
        addEventButton = view.findViewById(R.id.addEventButton)
        previousMonthButton = view.findViewById(R.id.start)
        nextMonthButton = view.findViewById(R.id.end)

        setupCalendar()
        setupEventList()
        setupAddEventButton()
        setupMonthNavigation()

        return view
    }

    override fun onResume() {
        super.onResume()
        val currentDate = Calendar.getInstance()
        updateEventList(currentDate)
    }

    private fun setupCalendar() {
        val currentDate = Calendar.getInstance()
        updateMonthYearText(currentDate)
        calendarView.setMonth(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH))

        calendarView.setOnDayClickListener { selectedDate ->
            updateEventList(selectedDate)
        }

        calendarView.setOnViewChangeListener { viewMode, date ->
            updateMonthYearText(date)
        }
    }

    private fun updateMonthYearText(date: Calendar) {
        val sdf = when (calendarView.getCurrentViewMode()) {
            AppleStyleCalendarView.ViewMode.MONTH -> SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            AppleStyleCalendarView.ViewMode.WEEK -> SimpleDateFormat("MMM d - ", Locale.getDefault())
        }
        val formattedDate = sdf.format(date.time)

        if (calendarView.getCurrentViewMode() == AppleStyleCalendarView.ViewMode.WEEK) {
            val endOfWeek = date.clone() as Calendar
            endOfWeek.add(Calendar.DAY_OF_WEEK, 6)
            val endDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            monthYearText.text = formattedDate + endDateFormat.format(endOfWeek.time)
        } else {
            monthYearText.text = formattedDate
        }
    }

    private fun setupEventList() {
        eventList.layoutManager = LinearLayoutManager(context)
        eventAdapter = EventAdapter(emptyList())
        eventList.adapter = eventAdapter

        val dao = EventDatabase.getDatabase(requireContext()).eventDao()
        val viewModelFactory = EventViewModelFactory(dao)
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        val currentDate = Calendar.getInstance()
        updateEventList(currentDate)
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
            calendarView.previousPeriod()
        }

        nextMonthButton.setOnClickListener {
            calendarView.nextPeriod()
        }
    }
}