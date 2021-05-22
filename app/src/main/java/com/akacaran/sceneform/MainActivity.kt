package com.akacaran.sceneform

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk

/**
 * Created by akacaranlioglu on 1/5/2021.
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (isDeviceSupportsAR()) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, createArFragmentWithUrl(""))
                .commit()
        }
    }
}

fun Context.isDeviceSupportsAR(): Boolean {
    val openGlVersion =
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion

    return ArCoreApk.getInstance().checkAvailability(this).isSupported &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
            openGlVersion.toDouble() >= 3.0
}
