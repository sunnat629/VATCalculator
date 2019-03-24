package com.sunnat629.vatcalculator.viewmodel

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sunnat629.vatcalculator.model.NonNullMediatorLiveData
import com.sunnat629.vatcalculator.model.RatesEnum
import com.sunnat629.vatcalculator.model.entities.Rate
import com.sunnat629.vatcalculator.model.network.NetworkResult
import com.sunnat629.vatcalculator.utils.Calculate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * This is the viewModel of the MainActivity class
 * */
class MainViewModel : ViewModel() {

    // Creating the Job variable
    private var parentJob = Job()

    // Creating the CoroutineContext variable and it uses the parentJob and the main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    // Creating the CoroutineScope variable based on coroutineContext
    private val scope = CoroutineScope(coroutineContext)

    /**
     * These two NonNullMediatorLiveData variables are using to get a NonNull safe value
     * */
    private val _rawRateData = NonNullMediatorLiveData<List<Rate>>()
    private val _error = NonNullMediatorLiveData<String>()

    /**
     * These two LiveData variables are using to get the '_rawRateData' and '_error' and publicly.
     * It helps to protect our immutable objects.
     * */
    val rawRateData: LiveData<List<Rate>>
        get() = _rawRateData
    val error: LiveData<String>
        get() = _error

    /**
     * This is a MutableLiveData variable of 'Including VAT Amount' amount based on 'Excluding VAT Amount' and 'VAT'.
     * It is a an two-way dataBinding property because it can get the value from the EditText
     * and also shows the value in the EditText.
     * */
    @Bindable
    val exclVatAmount = MutableLiveData<String>()

    /**
     * This is a MutableLiveData variable of 'VAT Rate' based on the Country
     * */
    @Bindable
    val radioVatRate = MutableLiveData<String>()

    /**
     * This is a LiveData variable of 'Including VAT Amount' amount based on 'Excluding VAT Amount' and 'VAT'.
     * It is a an one-way dataBinding property because it only shows the value
     * */
    val inclVatAmount: LiveData<String>
        @Bindable(value = ["vatByAmount", "exclVatAmount"])
        get() = Calculate.getIncludingVatAmount(vatByAmount, exclVatAmount)

    /**
     * This is a LiveData variable of 'VAT' amount based on 'Excluding VAT Amount' and 'VAT Rate'.
     * It is a an one-way dataBinding property because it only shows the value
     * */
    val vatByAmount: LiveData<String>
        @Bindable(value = ["radioVatRate", "exclVatAmount"])
        get() = Calculate.getVatByAmount(radioVatRate, exclVatAmount)

    /**
     * To execute a suspending function, we need a suspend block and 'launch' is a suspending block.
     * It launches the the coroutine without blocking the main or current thread and returns a 'Job' reference.
     * Here if the return result is Success, then it post the rawRateData to '_rawRateData'
     * Or, if there is any error, it will post the error message to '_error'
     * */
    private fun getRawRateData() {
        parentJob = scope.launch {
            val result = Calculate.fetchRateData()
            when (result) {
                is NetworkResult.Success -> _rawRateData.postValue(result.data)
                is NetworkResult.Error -> _error.postValue(result.exception.message)
            }
        }
    }

    /**
     * This function will fetch all the countries name from the rawRateData
     * */
    fun fetchAllCountries(): LiveData<List<String>> {
        val allCountryLiveData = MutableLiveData<List<String>>()
        allCountryLiveData.value = Calculate.getCountryList(rawRateData.value!!)
        return allCountryLiveData
    }

    /**
     * This function will the details of a country based of the
     * @param country
     * */
    fun getPeriodsRateByCountry(country: String): List<Pair<RatesEnum, Double>> {
        return Calculate.getRatesTypeByCountry(rawRateData.value!!, country)
    }

    /**
     * This is a 'onClick' function to reset the value into '0' of 'Excluding VAT Amount'
     * */
    fun reset() {
        scope.launch { exclVatAmount.value = "0" }
    }

    /**
     * This is a 'onClick' function to retry fetching data from the server
     * */
    fun retry() {
        scope.launch { getRawRateData() }
    }

    /**
     * during creating the viewModel, it will fetch the data or error messages
     * */
    init {
        getRawRateData()
    }

    /**
     * This function is to cancel the Job lifecycle and destroy this viewModel
     * */
    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}