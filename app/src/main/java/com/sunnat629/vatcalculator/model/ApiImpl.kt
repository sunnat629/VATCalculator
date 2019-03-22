package com.sunnat629.vatcalculator.model

import android.util.Log
import com.sunnat629.vatcalculator.model.entities.Rate
import com.sunnat629.vatcalculator.model.network.RetrofitFactory
import com.sunnat629.vatcalculator.utils.Constance.TAG_APIIMPL

object ApiImpl {
    private val service = RetrofitFactory.makeRetrofitService()
    private suspend fun call(): List<Rate> {
        val request = service.getJSonVATAsync()
        val response = request.await()

        return if (response.isSuccessful) {
            Log.d(TAG_APIIMPL, "response is successful")
            response.body()!!.rates
        } else {
            Log.e(TAG_APIIMPL, "Error: ${response.code()}")
            emptyList()
        }
    }

    suspend fun fetchRates(): MutableList<Rate> {
        return call().toMutableList()
    }
}