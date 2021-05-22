/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.ux

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.sceneform.rendering.ModelRenderable

/**
 * Implements AR Required ArFragment. Does not require additional permissions and uses the default
 * configuration for ARCore.
 */
class ArFragment : BaseArFragment() {

    /**
     * To achive putting 3D model at AR Scene Plane ready we need to know when fragment views are
     * ready. Only after this callback searching for Plane and putting 3D model can be possible.
     */
    private var onViewReady: ((ArFragment) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady?.invoke(this)
    }

    fun setOnViewReadyListener(onCreated: (fragment: ArFragment) -> Unit) {
        this.onViewReady = onCreated
    }

    override fun isArRequired(): Boolean {
        return true
    }

    override fun getAdditionalPermissions(): Array<String> {
        return arrayOf()
    }

    override fun handleSessionException(sessionException: UnavailableException) {
        val message: String = when (sessionException) {
            is UnavailableArcoreNotInstalledException -> {
                "Please install ARCore"
            }
            is UnavailableApkTooOldException -> {
                "Please update ARCore"
            }
            is UnavailableSdkTooOldException -> {
                "Please update this app"
            }
            is UnavailableDeviceNotCompatibleException -> {
                "This device does not support AR"
            }
            else -> {
                "Failed to create AR session"
            }
        }
        Log.e(TAG, "Error: $message", sessionException)
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
    }

    override fun getSessionConfiguration(session: Session): Config {
        return Config(session)
    }

    override fun getSessionFeatures(): Set<Session.Feature> {
        return emptySet()
    }

    companion object {
        private const val TAG = "StandardArFragment"
    }
}
