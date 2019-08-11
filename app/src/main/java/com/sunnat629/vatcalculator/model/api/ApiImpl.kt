package com.sunnat629.vatcalculator.model.api

import android.util.Log
import com.sunnat629.vatcalculator.model.entities.Rate
import com.sunnat629.vatcalculator.model.network.NetworkResult
import com.sunnat629.vatcalculator.model.network.RetrofitFactory
import com.sunnat629.vatcalculator.utils.DeclareConstant.TAG_API_IMPL
import java.io.IOException

/**
 * In this object, we will fetch the data using the API endpoints
 */
object ApiImpl {
    private val service = RetrofitFactory.makeRetrofitService()


    /**
     * This extension function will trigger the network request using the top-level suspending function.
     * 'call' will carry the successful outcome
     * 'errorMessage' will carry an error message if the fetch didn't success.
     * */
    suspend fun fetchRateData() = safeApiCall(
        call = { call() },
        errorMessage = "Error occurred"
    )

    /**
     * If response is successful. This suspending function will handle the fetched data or the error during failed.
     * */
    private suspend fun call(): NetworkResult<List<Rate>> {
        val request = service.getJSonVATAsync()
        val response = request.await()

        return if (response.isSuccessful) {
            Log.d(TAG_API_IMPL, "response is successful")
            NetworkResult.Success(response.body()!!.rates)
        } else {
            Log.e(TAG_API_IMPL, "Error: ${response.code()}")
            NetworkResult.Error(IOException("Error: ${response.code()}"))
        }
    }

    /**
     * Instead of try-catch, we are using a top-level suspending function named sadeApiCall().
     * It will trigger the network request.
     * @param call is for fetched raw data
     * @param errorMessage contains the error or failed messages
     * */
    private suspend fun <T : Any> safeApiCall(
        call: suspend () -> NetworkResult<T>,
        errorMessage: String
    ): NetworkResult<T> = try {
        call.invoke()
    } catch (e: Exception) {
        NetworkResult.Error(IOException(errorMessage, e))
    }
}