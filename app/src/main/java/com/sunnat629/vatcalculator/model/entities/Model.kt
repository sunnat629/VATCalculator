package com.sunnat629.vatcalculator.model.entities

/**
 * Fetch the raw data
 *
 * @param details is the url of the documentation of this api
 * @param version is the version number of this REST API
 * @param rates is the Array object of the main content
 * */
data class JSonVAT(
    var details: String,
    var version: Int,
    var rates: List<Rate>
) {
    override fun toString(): String {
        return "JSonVAT(details='$details', version=$version, rates=$rates)"
    }
}

/**
 * Fetch the rates details list of all the countries
 *
 * @param name is the name of a country
 * @param code is the code name of a country
 * @param country_code is the country code of a country
 * @param periods is the list of the periods
 * */
data class Rate(
    var name: String,
    var code: String,
    var country_code: String,
    var periods: List<Period>
) {
    override fun toString(): String {
        return "Rate(name='$name', code='$code', countryCode='$country_code', periods=$periods)"
    }
}

/**
 * Fetch the Period list of a specific country
 *
 * @param effective_from mention the date when the rates are effective
 * @param rates contains the information of the rate types
 * */
data class Period(
    var effective_from: String,
    var rates: Rates
) {
    override fun toString(): String {
        return "Period(effectiveFrom='$effective_from', rates=$rates)"
    }
}

/**
 * Fetch the VAT rates based on Period
 *
 * @param super_reduced is the super_reduced VAT rates
 * @param reduced is the reduced VAT rates
 * @param standard is the standard VAT rates
 * @param reduced1 is the reduced1 VAT rates
 * @param reduced2 is the reduced2 VAT rates
 * @param parking is the parking VAT rates
 * */
data class Rates(
    var super_reduced: Double,
    var reduced: Double,
    var standard: Double,
    var reduced1: Double,
    var reduced2: Double,
    var parking: Double
) {
    override fun toString(): String {
        return "Rates(superReduced=$super_reduced, reduced=$reduced, standard=$standard, reduced1=$reduced1, reduced2=$reduced2, parking=$parking)"
    }
}