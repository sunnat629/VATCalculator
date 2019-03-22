package com.sunnat629.vatcalculator.model.entities


data class JSonVAT(
    var details: String,
    var version: Int,
    var rates: List<Rate>) {
    override fun toString(): String {
        return "JSonVAT(details='$details', version=$version, rates=$rates)"
    }
}

data class Rate(
    var name: String,
    var code: String,
    var country_code: String,
    var periods: List<Period>) {
    override fun toString(): String {
        return "Rate(name='$name', code='$code', countryCode='$country_code', periods=$periods)"
    }
}

class Period(
    var effective_from: String,
    var rates: Rates) {
    override fun toString(): String {
        return "Period(effectiveFrom='$effective_from', rates=$rates)"
    }
}

class Rates(
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