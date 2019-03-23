package com.sunnat629.vatcalculator.model.network

/**
 * This is a sealed class and it allows to represent constrained hierarchies in which
 * an object can only be of one of the given types.
 * */
sealed class NetworkResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Exception) : NetworkResult<Nothing>()
}