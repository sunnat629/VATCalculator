package com.sunnat629.vatcalculator.model.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.sunnat629.vatcalculator.model.api.Api
import com.sunnat629.vatcalculator.utils.Constance.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * This object is created for the  Retrofit Instance. Here we have created the  Retrofit Builder.
 * */
object RetrofitFactory {

    fun makeRetrofitService(): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(makeOkHttpClient())
            //  GsonConverterFactory, which is going to map the JSON data.
            .addConverterFactory(GsonConverterFactory.create())
            // CoroutineCallAdapterFactory is a call adapter of kotlin coroutines when building a Retrofit instance.
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create(Api::class.java)
    }

    /**
     * This function will take care of connecting to the server and the sending and retrieval of information.
     * */
    private fun makeOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(makeLoggingInterceptor())
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    /**
     * This Interceptor function will help to monitor the http calls and show it with logs.
     * */
    private fun makeLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }
}
