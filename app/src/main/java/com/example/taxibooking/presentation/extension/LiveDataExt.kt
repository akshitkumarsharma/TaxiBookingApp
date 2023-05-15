package com.example.taxibooking.presentation.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class MutableLiveDataSingle<T> : MutableLiveData<SingleEventWrapper<T>>()

class SingleEventWrapper<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

fun <K : Any, L : SingleEventWrapper<K>, M : LiveData<L>> Fragment.observeLiveDataSingle(liveData: M?, body: (K) -> Unit) {
    liveData?.observe(viewLifecycleOwner, SingleEventObserver(body))
}

private class SingleEventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<SingleEventWrapper<T>> {
    override fun onChanged(event: SingleEventWrapper<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}

fun <T> MutableLiveDataSingle<T>.postValueOnce(value: T) {
    postValue(SingleEventWrapper(value))
}