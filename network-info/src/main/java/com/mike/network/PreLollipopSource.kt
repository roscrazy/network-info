package com.mike.network

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

internal class PreLollipopSource(private val context: Context) : NetworkStatusSource {

    override fun networkStatus(): Flowable<Boolean> {
        return Flowable.create({ emitter ->
            emitter.onNext(currentStatus())
            val callBack = { status: Boolean -> emitter.onNext(status) }
            val broadcastReceiver = NetworkReceiver(callBack)
            context.registerReceiver(
                broadcastReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            emitter.setCancellable { context.unregisterReceiver(broadcastReceiver) }
        }, BackpressureStrategy.LATEST)
    }

    private inner class NetworkReceiver(val callBack: (Boolean) -> Unit) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            callBack(currentStatus())
        }
    }

    @SuppressLint("MissingPermission")
    private fun currentStatus(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}