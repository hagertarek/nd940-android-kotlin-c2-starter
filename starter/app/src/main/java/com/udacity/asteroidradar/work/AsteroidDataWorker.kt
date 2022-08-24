package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class AsteroidDataWorker (appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "AsteroidDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            repository.refreshAsteroid()
            repository.deleteAsteroidsBeforeToday()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}