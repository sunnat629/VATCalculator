package com.sunnat629.vatcalculator

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sunnat629.vatcalculator.model.RatesEnum
import com.sunnat629.vatcalculator.model.entities.Calculate
import com.sunnat629.vatcalculator.model.entities.Rate
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)



    var rawData: MutableList<Rate> = mutableListOf()

    fun fetchData(): MutableList<Rate>{
        var a:  MutableList<Rate> = mutableListOf()
        runBlocking {
            a = withContext(scope.coroutineContext + Dispatchers.IO) {
                Calculate.getData()
            }
        }
        return a
    }

    fun fetchAllCountries(): LiveData<List<String>> {
        val allCountryLiveData = MutableLiveData<List<String>>()
        allCountryLiveData.value = Calculate.countryList(rawData)
        return allCountryLiveData
    }

    // Bind with the inputted amount
    @Bindable
    val exclVatAmount = MutableLiveData<String>()

    // Store  with the inputted amount
    @Bindable
    val radioVatRate = MutableLiveData<String>()


    fun getPeriodsRateByCountry(country: String): List<Pair<RatesEnum, Double>> {
        return Calculate.getPeriodsRateByCountry(rawData, country)
    }

    val inclVatAmount: LiveData<String>
        @Bindable(value = ["radioVatRate", "exclVatAmount"])
        get() = Calculate.getIncludingVatAmount(vatByAmount, exclVatAmount)


    val vatByAmount: LiveData<String>
        @Bindable(value = ["radioVatRate", "exclVatAmount"])
        get() = Calculate.getVatByAmount(radioVatRate, exclVatAmount)

    fun clearAll() {
        exclVatAmount.value = "0"
    }

    fun getNetworkConnectivity(){

    }
}