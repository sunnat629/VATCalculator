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
     * A temporary rawData list
     * */
    private var rawData: List<Rate> = listOf()

    /**
     * This function will return the fetched rawData or error message
     * */
    suspend fun getData(): NetworkResult<List<Rate>> {
        return ApiImpl.fetchData()
    }

    /**
     * This function will return the list of all countries
     * @param rawData is the fetched rawData
     * */
    fun countryList(rawData:  List<Rate>): List<String> {
        return rawData.flatMap { listOf(it.name) }.sortedBy { it }
    }

    /**
     * This function will return the details of a specific country
     * @param country is specific country
     * */
    private fun getRateByCountry(country: String): Rate?{
       return rawData.find { it.name == country }
    }

    /**
     * This function will calculate and return the latest Period of a country
     * @param country is the specific country
     * */
    private fun getPeriodsByCountry(country: String): List<Period>? {
        val periods: List<Period> = getRateByCountry(country)!!.periods
        // from this sorted based on effective_from will help us to find the latest VAT rate
        periods.sortedBy { it.effective_from }
        return periods
    }

    /**
     * This function will calculate and return the latest PeriodsRate details of a country
     * @param rawData is the fetch rawData
     * */
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

    /**
     * This function will calculate and return the 'Including Vat Amount' amount based on 'Excluding Vat Amount' and 'VAT'.
     * @param inputVat is VAT
     * @param exclVatAmount is Excluding Vat Amount
     * */
    fun getIncludingVatAmount(inputVat: LiveData<String>, exclVatAmount: MutableLiveData<String>?): LiveData<String> {
        val output = MutableLiveData<String>()
        if (isAppStart(inputVat, exclVatAmount)){
            output.value = (inputVat.value!!.toDouble() + exclVatAmount?.value!!.toDouble()).toString()
        }
        return output
    }

    /**
     * This function will calculate and return the 'VAT' amount based on 'Excluding Vat Amount' and 'VAT Rate'.
     * @param inputVat is VAT
     * @param exclVatAmount is Excluding Vat Amount
     * */
    fun getVatByAmount(inputVat: MutableLiveData<String>?, exclVatAmount: MutableLiveData<String>?): LiveData<String> {
        val output = MutableLiveData<String>()
        if (isAppStart(inputVat!!, exclVatAmount)){
            output.value = ((inputVat.value!!.toDouble() * exclVatAmount?.value!!.toDouble()) / 100).toString()
        }
        return output
    }

    /**
     * when the app will start the values of the inputVAT and exclVatAmount will be null, so this function will handle it
     * @param inputVat is VAT
     * @param exclVatAmount is Excluding Vat Amount
     * */
    private fun isAppStart(inputVat: LiveData<String>, exclVatAmount: MutableLiveData<String>?): Boolean {
        return exclVatAmount?.value != null && exclVatAmount.value!!.isNotEmpty()
                && inputVat.value != null && inputVat.value!!.isNotEmpty()
    }
}