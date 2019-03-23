package com.sunnat629.vatcalculator.model.entities

/**
 * Fetch the raw data
 * */
data class JSonVAT(
    var details: String,
    var version: Int,
    var rates: List<Rate>) {
    override fun toString(): String {
        return "JSonVAT(details='$details', version=$version, rates=$rates)"
    }
}

/**
 * Fetch the rates details list of all the countries
 * */
data class Rate(
    var name: String,
    var code: String,
    var country_code: String,
    var periods: List<Period>) {
    override fun toString(): String {
        return "Rate(name='$name', code='$code', countryCode='$country_code', periods=$periods)"
    }
}

/**
 * Fetch the Period list of a specific country
 * */
data class Period(
    var effective_from: String,
    var rates: Rates) {
    override fun toString(): String {
        return "Period(effectiveFrom='$effective_from', rates=$rates)"
    }
}

/**
 * Fetch the VAT rates based on Period
 * */
data class Rates(
    var super_reduced: Double,
    var reduced: Double,
    var standard: Double,
    var reduced1: Double,
    var reduced2: Double,
    var parking: Double) {
    override fun toString(): String {
        return "Rates(superReduced=$super_reduced, reduced=$reduced, standard=$standard, reduced1=$reduced1, reduced2=$reduced2, parking=$parking)"
    }
}