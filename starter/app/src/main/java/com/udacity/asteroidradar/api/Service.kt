package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface AsteroidService {
    @GET("/neo/rest/v1/feed")
    fun getAsteroid(
        @Query("start_date") start: String,
        @Query("end_date") end: String,
        @Query("api_key") api: String
    ): Deferred<String>

    @GET("/planetary/apod")
    fun getImageOfTheDay(@Query("api_key") api: String): Deferred<PictureOfDay>
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {
    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val httpClient = OkHttpClient.Builder().addInterceptor(logging)

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(httpClient.build())
        .build()

    val asteroids = retrofit.create(AsteroidService::class.java)
}