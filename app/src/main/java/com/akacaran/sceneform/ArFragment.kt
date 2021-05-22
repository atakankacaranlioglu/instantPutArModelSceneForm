package com.akacaran.sceneform

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.InstantPutArOnUpdateListener
import com.google.ar.sceneform.ux.ArFragment as SceneFormArFragment

/**
 * Created by akacaranlioglu on 1/5/2021.
 */

class ArFragment : Fragment(R.layout.fragment_ar) {

    private var arFragment: SceneFormArFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arFragment = childFragmentManager.findFragmentById(R.id.arFragment) as? SceneFormArFragment

        val modelUrl = arguments?.run { getString(MODEL_URL, null) }

        arFragment?.setOnViewReadyListener {
            createModel(Uri.parse(modelUrl))
        }

    }

    private fun createModel(uri: Uri) {
        ModelRenderable.Builder()
            .setSource(arFragment?.context, uri)
            .setIsFilamentGltf(true)
            .build()
            .thenAccept { model ->
                arFragment?.apply {
                    arSceneView?.scene?.addOnUpdateListener(
                        InstantPutArOnUpdateListener(this, model)
                    )
                }
            }
    }

}

fun createArFragmentWithUrl(modelUrl: String) =
    ArFragment().apply {
        arguments = Bundle().also { arguments -> arguments.putString(MODEL_URL, modelUrl) }
    }

private const val MODEL_URL = "modelUrl"
