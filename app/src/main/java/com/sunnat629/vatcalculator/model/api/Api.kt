package com.sunnat629.vatcalculator.model.api

import com.sunnat629.vatcalculator.model.entities.JSonVAT
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface Api{
    @GET("/")
    fun getJSonVATAsync(): Deferred<Response<JSonVAT>>
}