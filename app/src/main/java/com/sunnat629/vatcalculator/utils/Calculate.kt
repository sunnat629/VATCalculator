package com.sunnat629.vatcalculator.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sunnat629.vatcalculator.model.api.ApiImpl
import com.sunnat629.vatcalculator.model.RatesEnum
import com.sunnat629.vatcalculator.model.entities.Period
import com.sunnat629.vatcalculator.model.entities.Rate
import com.sunnat629.vatcalculator.model.network.NetworkResult


object Calculate {

    var rawData: List<Rate> = listOf()

    suspend fun getData(): NetworkResult<List<Rate>> {
        return ApiImpl.fetchRates()
    }

    fun countryList(rawData:  List<Rate>): List<String> {
        return rawData.flatMap { listOf(it.name) }.sortedBy { it }
    }

    private fun getRateByCountry(country: String): Rate?{
       return rawData.find { it.name == country }
    }

    private fun getPeriodsByCountry(country: String): List<Period>? {
        val periods = getRateByCountry(country)!!.periods
        periods.sortedBy { it.effective_from }
        return periods
    }

    fun getPeriodsRateByCountry(rawData: List<Rate>, country: String)
            : List<Pair<RatesEnum, Double>> {
        Calculate.rawData = rawData
        val rates = getPeriodsByCountry(country)!![0].rates
        return listOf(
            Pair(RatesEnum.STANDARD, rates.standard),
            Pair(RatesEnum.SUPER_REDUCED, rates.super_reduced),
            Pair(RatesEnum.REDUCED, rates.reduced),
            Pair(RatesEnum.REDUCED1, rates.reduced1),
            Pair(RatesEnum.REDUCED2, rates.reduced2),
            Pair(RatesEnum.PARKING, rates.parking)
        )
    }

    fun getIncludingVatAmount(inputVat: LiveData<String>, exclVatAmount: MutableLiveData<String>?): LiveData<String> {
        val output = MutableLiveData<String>()
        if (isAppStart(inputVat, exclVatAmount)){
            output.value = (inputVat.value!!.toDouble() + exclVatAmount?.value!!.toDouble()).toString()
        }
        return output
    }

    fun getVatByAmount(inputVat: MutableLiveData<String>?, exclVatAmount: MutableLiveData<String>?): LiveData<String> {
        val output = MutableLiveData<String>()
        if (isAppStart(inputVat!!, exclVatAmount)){
            output.value = ((inputVat.value!!.toDouble() * exclVatAmount?.value!!.toDouble()) / 100).toString()
        }
        return output
    }

    // when the app will start the values of the inputVAT and exclVatAmount will be null, so this function will handle it
    private fun isAppStart(inputVat: LiveData<String>, exclVatAmount: MutableLiveData<String>?): Boolean {
        return exclVatAmount?.value != null && exclVatAmount.value!!.isNotEmpty()
                && inputVat.value != null && inputVat.value!!.isNotEmpty()
    }
}