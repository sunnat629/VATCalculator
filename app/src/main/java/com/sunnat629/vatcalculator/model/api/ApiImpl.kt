package com.sunnat629.vatcalculator.model.api

import android.util.Log
import com.sunnat629.vatcalculator.model.entities.Rate
import com.sunnat629.vatcalculator.model.network.NetworkResult
import com.sunnat629.vatcalculator.model.network.RetrofitFactory
import com.sunnat629.vatcalculator.utils.Constance.TAG_APIIMPL
import java.io.IOException

object ApiImpl {
    private val service = RetrofitFactory.makeRetrofitService()

    suspend fun fetchData() = safeApiCall(
        call = { call() },
        errorMessage = "Error occurred"
    )

    private suspend fun call(): NetworkResult<List<Rate>> {
            val request = service.getJSonVATAsync()
            Log.d("****", "$request")

            val response = request.await()
            Log.d("****", "$response")

            return if (response.isSuccessful) {
                Log.d(TAG_APIIMPL, "response is successful")
                NetworkResult.Success(response.body()!!.rates)
            } else {
                Log.e(TAG_APIIMPL, "Error: ${response.code()}")
                NetworkResult.Error(IOException("Error: ${response.code()}"))
            }
    }

    suspend fun fetchRates(): NetworkResult<List<Rate>> {
        return fetchData()
    }

    suspend fun <T : Any> safeApiCall(call: suspend () -> NetworkResult<T>, errorMessage: String): NetworkResult<T> = try {
        call.invoke()
    } catch (e: Exception) {
        NetworkResult.Error(IOException(errorMessage, e))
    }
}