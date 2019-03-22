package com.sunnat629.vatcalculator.model

import com.sunnat629.vatcalculator.model.entities.Period
import com.sunnat629.vatcalculator.model.entities.Rate

class RatesRepository{

    var exclVatAmount: Double = 0.0
    var inclVatAmount: Double = 0.0
    var radioVatRate: Double = 0.0
    var inputVat: Double = 0.0


    private suspend fun getRates(): MutableList<Rate> {
        return ApiImpl.fetchRates()
    }

    suspend fun getAllCountries(): List<String>{
        return getRates().flatMap {
            listOf(it.name)
        }.sortedBy { it }
    }


    private suspend fun getRateByCountry(country: String): Rate? {
        return getRates().find { it.name == country }
    }

    private suspend fun getPeriodsByCountry(country: String): List<Period>? {
        val periods = getRateByCountry(country)!!.periods
        periods.sortedBy { it.effective_from }
        return periods
    }

    suspend fun getPeriodsRateByCountry(country: String): List<Pair<RatesEnum, Double>> {
        val rates = getPeriodsByCountry(country)!![0].rates
        return listOf(
            Pair(RatesEnum.STANDARD,rates.standard),
            Pair(RatesEnum.SUPER_REDUCED,rates.super_reduced),
            Pair(RatesEnum.REDUCED,rates.reduced),
            Pair(RatesEnum.REDUCED1,rates.reduced1),
            Pair(RatesEnum.REDUCED2,rates.reduced2),
            Pair(RatesEnum.PARKING,rates.parking))
    }

    fun setExcludingVatAmount(digit: Double){
        exclVatAmount  = digit
        setVat()
        setIncludingVatAmount()
    }

    fun setIncludingVatAmount(){
        inclVatAmount = inputVat + exclVatAmount
    }

    fun setVat(){
        inputVat = (radioVatRate * exclVatAmount) / 100
    }

    fun setVatRate(digit: Double){
        radioVatRate = digit
        setVat()
    }

}