package com.sunnat629.vatcalculator.model

import androidx.lifecycle.MediatorLiveData

/**
 * This class is extended of MediatorLiveData. MediatorLiveData is a subclass of LiveData and it reacts
 * on 'onChange' events. Here we are using this to make a NonNull safe value.
 * */
class NonNullMediatorLiveData<T> : MediatorLiveData<T>()
