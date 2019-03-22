package com.sunnat629.vatcalculator

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sunnat629.vatcalculator.model.RatesEnum
import com.sunnat629.vatcalculator.model.entities.Calculate

class MainViewModel(application: Application): AndroidViewModel(application) {

    suspend fun fetchAllCountry(): LiveData<List<String>>{
        val allCountryLiveData = MutableLiveData<List<String>>()
        allCountryLiveData.value = Calculate.countryList()
       return allCountryLiveData
    }

    // Bind with the inputted amount
    @Bindable
    val exclVatAmount = MutableLiveData<String>()

    // Store  with the inputted amount
    @Bindable
    val radioVatRate = MutableLiveData<String>()


     suspend fun getPeriodsRateByCountry(country: String): List<Pair<RatesEnum, Double>> {
        return Calculate.getPeriodsRateByCountry(country)
    }

    val inclVatAmount: LiveData<String>
        @Bindable(value = ["radioVatRate", "exclVatAmount"])
        get() =  Calculate.getIncludingVatAmount(radioVatRate, exclVatAmount)


    val vatByAmount: LiveData<String>
        @Bindable(value = ["radioVatRate", "exclVatAmount"])
        get() = Calculate.getVatByAmount(radioVatRate, exclVatAmount)
}