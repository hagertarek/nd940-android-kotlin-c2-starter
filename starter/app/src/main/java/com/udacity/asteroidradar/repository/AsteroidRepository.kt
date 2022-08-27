package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

enum class AsteroidFilter { SAVED, TODAY, WEEK }

class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    val todayAsteroids = Transformations.map(
        database.asteroidDao.getAsteroidsByDates(startDate = startDate, endDate = startDate)
    ) {
        it.asDomainModel()
    }

    val weekAsteroids = Transformations.map(
        database.asteroidDao.getAsteroidsByDates(startDate = startDate, endDate = endDate)
    ) {
        it.asDomainModel()
    }

    private val _todayImage = MutableLiveData<PictureOfDay>()
    var todayImage: LiveData<PictureOfDay> = _todayImage

    suspend fun refreshAsteroid() {
        withContext(Dispatchers.IO) {
            val asteroidJson = Network.asteroids.getAsteroid(startDate, endDate, API_KEY).await()
            val asteroidList = parseAsteroidsJsonResult(JSONObject(asteroidJson))
            database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
        }
    }

    suspend fun deleteAsteroidsBeforeToday() {
        withContext(Dispatchers.IO){
            database.asteroidDao.deleteAsteroidsBeforeToday(startDate)
        }
    }

    suspend fun getImageOfTheDay() {
        _todayImage.value = Network.asteroids.getImageOfTheDay(API_KEY).await()
    }
}