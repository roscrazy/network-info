package com.mike.network.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mike.network.NetworkStatusFactory
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {
    private val networkStatus = NetworkStatusFactory().create(this)
    private var disposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        disposable = networkStatus.nextWorkAvailability()
            .subscribe({
                Toast.makeText(this, "Network Available $it", Toast.LENGTH_LONG).show()
            }, { it.printStackTrace() })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
