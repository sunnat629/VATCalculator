package com.sunnat629.vatcalculator.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sunnat629.vatcalculator.model.RatesEnum
import com.sunnat629.vatcalculator.model.api.ApiImpl
import com.sunnat629.vatcalculator.model.entities.Period
import com.sunnat629.vatcalculator.model.entities.Rate
import com.sunnat629.vatcalculator.model.network.NetworkResult

/**
 * This object variables will help to calculate all the necessary logic for the viewModel
 * */
object Calculate {

    /**
     * A temporary rawRateData list
     * */
    private var rawRateData: List<Rate> = listOf()

    /**
     * This function will return the fetched rawRateData or error message
     * */
    suspend fun fetchRateData(): NetworkResult<List<Rate>> {
        return ApiImpl.fetchRateData()
    }

    /**
     * This function will return the list of all countries
     * @param rawRateData is the fetched rawRateData
     * */
    fun getCountryList(rawRateData: List<Rate>): List<String> {
        return rawRateData.flatMap { listOf(it.name) }.sortedBy { it }
    }

    /**
     * This function will return the details of a specific country
     * @param country is specific country
     * */
    private fun getOneRateByCountry(country: String): Rate? {
        return rawRateData.find { it.name == country }
    }

    /**
     * This function will calculate and return the latest Period of a country
     * @param country is the specific country
     * */
    private fun getPeriodListByCountry(country: String): List<Period>? {
        val periods: List<Period> = getOneRateByCountry(country)!!.periods
        // from this sorted based on effective_from will help us to find the latest VAT rate
        periods.sortedBy { it.effective_from }
        return periods
    }

    /**
     * This function will calculate and return the latest Rates Types details of a country
     * @param rawRateData is the fetched raw RateData
     * */
    fun getRatesTypeByCountry(rawRateData: List<Rate>, country: String)
            : List<Pair<RatesEnum, Double>> {
        Calculate.rawRateData = rawRateData
        val rates = getPeriodListByCountry(country)!![0].rates
        return listOf(
            Pair(RatesEnum.STANDARD, rates.standard),
            Pair(RatesEnum.SUPER_REDUCED, rates.super_reduced),
            Pair(RatesEnum.REDUCED, rates.reduced),
            Pair(RatesEnum.REDUCED1, rates.reduced1),
            Pair(RatesEnum.REDUCED2, rates.reduced2),
            Pair(RatesEnum.PARKING, rates.parking)
        )
    }

    /**
     * This function will calculate and return the 'Including VAT Amount' amount based on 'Excluding VAT Amount' and 'VAT'.
     * @param inputVat is VAT
     * @param exclVatAmount is Excluding VAT Amount
     * */
    fun getIncludingVatAmount(inputVat: LiveData<String>, exclVatAmount: MutableLiveData<String>?): LiveData<String> {
        val output = MutableLiveData<String>()
        if (isAppStart(inputVat, exclVatAmount)) {
            output.value = (inputVat.value!!.toDouble() + exclVatAmount?.value!!.toDouble()).toString()
        } else {
            output.value = "0"
        }
        return output
    }

    /**
     * This function will calculate and return the 'VAT' amount based on 'Excluding VAT Amount' and 'VAT Rate'.
     * @param inputVat is VAT
     * @param exclVatAmount is Excluding VAT Amount
     * */
    fun getVatByAmount(inputVat: MutableLiveData<String>?, exclVatAmount: MutableLiveData<String>?): LiveData<String> {
        val output = MutableLiveData<String>()
        if (isAppStart(inputVat!!, exclVatAmount)) {
            output.value = ((inputVat.value!!.toDouble() * exclVatAmount?.value!!.toDouble() / 100)).toString()
        } else {
            output.value = "0"
        }
        return output
    }

    /**
     * when the app will start the values of the inputVAT and exclVatAmount will be null, so this function will handle it
     * @param inputVat is VAT
     * @param exclVatAmount is Excluding VAT Amount
     * */
    private fun isAppStart(inputVat: LiveData<String>, exclVatAmount: MutableLiveData<String>?): Boolean {
        return exclVatAmount?.value != null && exclVatAmount.value!!.isNotEmpty()
                && inputVat.value != null && inputVat.value!!.isNotEmpty()
    }
}