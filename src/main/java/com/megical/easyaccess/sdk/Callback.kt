package com.megical.easyaccess.sdk

interface Callback<T> {
    fun onSuccess(response: T)
    fun onFailure(error: MegicalException)
}