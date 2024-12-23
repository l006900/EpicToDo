package com.example.epictodo.home.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.epictodo.R
import com.example.epictodo.database.EventDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity(){

    private lateinit var titleEditText: EditText
    private lateinit var startTimeButton: Button
    private lateinit var endTimeButton: Button
    private lateinit var frequencySpinner: Spinner
    private lateinit var dailyCheckInsEditText: EditText
    private lateinit var checkInRecordsEditText: EditText
    private lateinit var reminderSwitch: Switch
    private lateinit var journalSwitch: Switch
    private lateinit var saveButton: Button

    private lateinit var eventViewModel: EventViewModel

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        initViews()
        setupViewModel()
        setupListeners()
    }

    private fun initViews() {
        titleEditText = findViewById(R.id.titleEditText)
        startTimeButton = findViewById(R.id.startTimeButton)
        endTimeButton = findViewById(R.id.endTimeButton)
        frequencySpinner = findViewById(R.id.frequencySpinner)
        dailyCheckInsEditText = findViewById(R.id.dailyCheckInsEditText)
        checkInRecordsEditText = findViewById(R.id.checkInRecordsEditText)
        reminderSwitch = findViewById(R.id.reminderSwitch)
        journalSwitch = findViewById(R.id.journalSwitch)
        saveButton = findViewById(R.id.saveButton)

        // Set up frequency spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.frequency_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            frequencySpinner.adapter = adapter
        }
    }

    private fun setupViewModel() {
        val dao = EventDatabase.getDatabase(application).eventDao()
        val viewModelFactory = EventViewModelFactory(dao)
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)
    }

    private fun setupListeners() {
        startTimeButton.setOnClickListener { showDateTimePicker(true) }
        endTimeButton.setOnClickListener { showDateTimePicker(false) }

        saveButton.setOnClickListener {
            saveEvent()
        }
    }

    private fun showDateTimePicker(isStartTime: Boolean) {
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateTimeButton(isStartTime)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateTimeButton(isStartTime: Boolean) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDateTime = sdf.format(calendar.time)
        if (isStartTime) {
            startTimeButton.text = formattedDateTime
        } else {
            endTimeButton.text = formattedDateTime
        }
    }

    private fun saveEvent() {
        val title = titleEditText.text.toString()
        val startTime = parseDateTime(startTimeButton.text.toString())
        val endTime = parseDateTime(endTimeButton.text.toString())
        val frequency = frequencySpinner.selectedItem.toString()
        val dailyCheckIns = dailyCheckInsEditText.text.toString().toIntOrNull() ?: 0
        val checkInRecords = checkInRecordsEditText.text.toString()
        val reminder = if (reminderSwitch.isChecked) "On" else "Off"
        val journalEntry = if (journalSwitch.isChecked) "Enabled" else "Disabled"

        val event = Event(
            title = title,
            description = "",
            date = startTime,
            startTime = startTime,
            endTime = endTime,
            frequency = frequency,
            dailyCheckIns = dailyCheckIns,
            checkInRecords = checkInRecords,
            reminder = reminder,
            journalEntry = journalEntry
        )

        eventViewModel.insert(event)
        finish()
    }

    private fun parseDateTime(dateTimeString: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.parse(dateTimeString)?.time ?: System.currentTimeMillis()
    }
}