package com.sunnat629.vatcalculator

import android.app.Application
import timber.log.Timber

class VatCalcAppication: Application(){
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) run { Timber.plant(Timber.DebugTree()) }
    }
}