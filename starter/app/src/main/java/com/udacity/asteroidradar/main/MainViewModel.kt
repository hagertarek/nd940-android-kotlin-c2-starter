package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.repository.Asteroidfilter
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : ViewModel() {

    private val database = getDatabase(app)
    private val asteroidRepository = AsteroidRepository(database)
    private val filter = MutableLiveData(Asteroidfilter.SAVED)
    var asteroidsList = filter.map {
        when (it) {
            Asteroidfilter.TODAY -> asteroidRepository.todayAsteroids
            Asteroidfilter.WEEK -> asteroidRepository.weekAsteroids
            else -> asteroidRepository.asteroids
        }
    }

    val todayImage = asteroidRepository.todayImage


    init {
        viewModelScope.launch {
            try {
                asteroidRepository.refreshAsteroid()
                asteroidRepository.getImageOfTheDay()
            } catch (e: Exception) {
                println("Exception refreshing data: $e.message")
            }
        }
    }

    fun updateFilter(filter: Asteroidfilter) {
        this.filter.value = filter
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}