package com.sunnat629.vatcalculator

import android.app.Application
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sunnat629.vatcalculator.model.NonNullMediatorLiveData
import com.sunnat629.vatcalculator.model.RatesEnum
import com.sunnat629.vatcalculator.model.entities.Rate
import com.sunnat629.vatcalculator.model.network.NetworkResult
import com.sunnat629.vatcalculator.utils.Calculate
import com.sunnat629.vatcalculator.utils.Constance
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val _rawData = NonNullMediatorLiveData<List<Rate>>()
    val rawData: LiveData<List<Rate>>
        get() = _rawData

    private val _error = NonNullMediatorLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private fun getRawData() {
        parentJob = scope.launch {
            val result = Calculate.getData()
            when (result) {
                is NetworkResult.Success -> _rawData.postValue(result.data)
                is NetworkResult.Error -> _error.postValue(result.exception.message)
            }
        }
    }

    init {
        getRawData()
    }

    fun fetchAllCountries(): LiveData<List<String>> {
        val allCountryLiveData = MutableLiveData<List<String>>()
        allCountryLiveData.value = Calculate.countryList(rawData.value!!)
        return allCountryLiveData
    }

    // Bind with the inputted amount
    @Bindable
    val exclVatAmount = MutableLiveData<String>()

    // Store  with the inputted amount
    @Bindable
    val radioVatRate = MutableLiveData<String>()


    fun getPeriodsRateByCountry(country: String): List<Pair<RatesEnum, Double>> {
        return Calculate.getPeriodsRateByCountry(rawData.value!!, country)
    }

    val inclVatAmount: LiveData<String>
        @Bindable(value = ["radioVatRate", "exclVatAmount"])
        get() = Calculate.getIncludingVatAmount(vatByAmount, exclVatAmount)


    val vatByAmount: LiveData<String>
        @Bindable(value = ["radioVatRate", "exclVatAmount"])
        get() = Calculate.getVatByAmount(radioVatRate, exclVatAmount)


    fun reset() {
        exclVatAmount.value = "0"
    }
    fun retry() {
        getRawData()
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}