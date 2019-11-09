package com.mike.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import java.util.*

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class FromLollipopSource(
    private val context: Context
) : NetworkStatusSource {

    @SuppressLint("MissingPermission")
    override fun networkStatus(): Flowable<Boolean> {
        return Flowable.create({ emitter ->
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            emitter.onNext(netInfo != null && netInfo.isConnected)
            val callBack = { status: Boolean -> emitter.onNext(status) }
            val netWorkCallback = createNetworkCallback(callBack)
            connectivityManager.registerNetworkCallback(createNetworkRequest(), netWorkCallback)
            emitter.setCancellable {
                try {
                    connectivityManager.unregisterNetworkCallback(netWorkCallback)
                } catch (e: IllegalArgumentException) {
                    emitter.onError(e)
                }
            }
        }, BackpressureStrategy.LATEST)
    }

    private fun createNetworkCallback(callBack: (Boolean) -> Unit): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {

            private var connectingNetworks = Collections.synchronizedSet(mutableSetOf<Network>())
            override fun onAvailable(network: Network?) {
                Log.v(TAG, "On available $network")
                super.onAvailable(network)
                callBack.invoke(true)
                network?.run { connectingNetworks.add(network) }
            }

            override fun onLost(network: Network?) {
                Log.v(TAG, "On onLost $network")
                super.onLost(network)
                connectingNetworks.remove(network)
                if (connectingNetworks.isEmpty()) callBack.invoke(false)
            }
        }
    }

    private fun createNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }
}

