package com.sunnat629.vatcalculator.model.api

import com.sunnat629.vatcalculator.model.entities.JSonVAT
import com.sunnat629.vatcalculator.utils.DeclareConstant.GET_ENDPOINT_RAW_DATA
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

/**
 * In this an Api interface, we will create all the endpoint functions
 * */
interface Api {
    /**
     * This endpoint will help to get the Raw Data from the server.
     * Here we will have use Deferred<T>. Deferred<T> mainly uses with 'await' suspending function.
     * This will wait for the result without blocking the main or current thread
     * */
    @GET(GET_ENDPOINT_RAW_DATA)
    fun getJSonVATAsync(): Deferred<Response<JSonVAT>>
}