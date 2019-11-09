package com.mike.network

import io.reactivex.Flowable


internal class NetworkStatusImpl(private val networkInfoSource: NetworkStatusSource) : NetworkStatus {

    private val sharedFlowable: Flowable<Boolean> by lazy { createSharedFlowable() }

    private fun createSharedFlowable(): Flowable<Boolean> {
        return networkInfoSource.networkStatus()
            .distinctUntilChanged()
            .replay(1)
            .refCount()
    }

    override fun nextWorkAvailability(): Flowable<Boolean> = sharedFlowable
}


