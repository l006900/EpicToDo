package com.example.epictodo.find.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.epictodo.find.m.FindDao
import com.example.epictodo.find.m.FindDatabase
import com.example.epictodo.find.m.FindEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FindViewModel(application: Application) : AndroidViewModel(application) {
    private val database: FindDatabase = FindDatabase.getInstance(application)
    private val findDao: FindDao = database.findDao()
    val allItems: LiveData<List<FindEntity>> = findDao.getAllItems()

    fun insert(findEntity: FindEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            findDao.insert(findEntity)
        }
    }

    fun delete(findEntity: FindEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            findDao.delete(findEntity)
        }
    }

    fun getItemByTimestamp(timestamp: Long): LiveData<FindEntity> {
        return findDao.getItemByTimestamp(timestamp)
    }
}