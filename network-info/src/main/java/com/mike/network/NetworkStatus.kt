package com.mike.network

import android.content.Context
import android.os.Build
import io.reactivex.Flowable

interface NetworkStatusSource {
    fun networkStatus(): Flowable<Boolean>
}

interface NetworkStatus{
    fun nextWorkAvailability() : Flowable<Boolean>
}

class NetworkStatusFactory {
    private fun provideNetworkStatusSource(context: Context): NetworkStatusSource {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return PreLollipopSource(context)
        } else
            return FromLollipopSource(context)
    }

    fun create(context: Context): NetworkStatus {
        return NetworkStatusImpl(provideNetworkStatusSource(context))
    }
}