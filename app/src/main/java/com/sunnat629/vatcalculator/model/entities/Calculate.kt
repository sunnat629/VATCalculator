package com.sunnat629.vatcalculator.model.entities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sunnat629.vatcalculator.model.ApiImpl
import com.sunnat629.vatcalculator.model.RatesEnum


object Calculate {

    private val _fetchAllCountry = MutableLiveData<List<String>>()
    val fetchAllCountry: LiveData<List<String>>
        get() = _fetchAllCountry

    private val _exclVatAmount = MutableLiveData<Double>()
    val exclVatAmount: LiveData<Double>
        get() = _exclVatAmount

    private val _inclVatAmount = MutableLiveData<Double>()
    val inclVatAmount: LiveData<Double>
        get() = _inclVatAmount

    private var _radioVatRate = MutableLiveData<Double>()
    val radioVatRate: LiveData<Double>
        get() = _radioVatRate

    private val _inputVat = MutableLiveData<Double>()
    val inputVat: LiveData<Double>
        get() = _inputVat


    private suspend fun getRates() = ApiImpl.fetchRates()

    suspend fun countryList(): List<String> {
        return getRates().flatMap { listOf(it.name) }.sortedBy { it }
    }

    private suspend fun getRateByCountry(country: String) = getRates().find { it.name == country }

    private suspend fun getPeriodsByCountry(country: String): List<Period>? {
        val periods = getRateByCountry(country)!!.periods
        periods.sortedBy { it.effective_from }
        return periods
    }

    suspend fun getPeriodsRateByCountry(country: String): List<Pair<RatesEnum, Double>> {
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

    fun getIncludingVatAmount(inputVat: MutableLiveData<String>?, exclVatAmount: MutableLiveData<String>?): LiveData<String> {
        val output = MutableLiveData<String>()
        if (isAppStart(inputVat,exclVatAmount)){
            output.value = (inputVat?.value!!.toDouble() + exclVatAmount?.value!!.toDouble()).toString()
        }
        return output
    }

    fun getVatByAmount(inputVat: MutableLiveData<String>?, exclVatAmount: MutableLiveData<String>?): LiveData<String> {
        val output = MutableLiveData<String>()
        if (isAppStart(inputVat,exclVatAmount)){
            output.value = ((inputVat?.value!!.toDouble() * exclVatAmount?.value!!.toDouble()) / 100).toString()
        }
        return output
    }


    // when the app will start the values of the inputVAT and exclVatAmount will be null, so this function will handle it
    private fun isAppStart(inputVat: MutableLiveData<String>?, exclVatAmount: MutableLiveData<String>?): Boolean {
        return exclVatAmount?.value != null && exclVatAmount.value!!.isNotEmpty()
                && inputVat?.value != null && inputVat.value!!.isNotEmpty()
    }

    fun setVat(exclVatAmount: MutableLiveData<String>?): LiveData<String> {
        val output = MutableLiveData<String>()
        if (exclVatAmount?.value != null && exclVatAmount.value!!.isNotEmpty()){
            val vat = (15.0 * exclVatAmount.value!!.toDouble()) / 100
            output.value = vat.toString()
        }
        return output
    }
}