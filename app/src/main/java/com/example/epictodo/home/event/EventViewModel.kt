package com.example.epictodo.home.event

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class EventViewModel(private val eventDao: EventDao) : ViewModel() {

    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()

    fun getEventsBetweenDates(startDate: Long, endDate: Long): LiveData<List<Event>> {
        return eventDao.getEventsBetweenDates(startDate, endDate)
    }

    fun getAllEventsBetweenDates(startDate: Long, endDate: Long): LiveData<List<Event>> {
        return eventDao.getEventsBetweenDates(startDate, endDate)
    }

    fun insert(event: Event) = viewModelScope.launch {
        eventDao.insert(event)
    }

    fun update(event: Event) = viewModelScope.launch {
        eventDao.update(event)
    }

    fun delete(event: Event) = viewModelScope.launch {
        eventDao.delete(event)
    }
}

class EventViewModelFactory(private val eventDao: EventDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(eventDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}