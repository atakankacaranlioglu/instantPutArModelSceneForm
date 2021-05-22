package com.google.ar.sceneform.ux

import android.content.res.Resources
import android.util.Size
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable

/**
 * Created by akacaranlioglu on 1/5/2021.
 */

class InstantPutArOnUpdateListener(
    private val arFragment: ArFragment,
    private var modelRenderable: ModelRenderable?
) : Scene.OnUpdateListener {
    override fun onUpdate(frameTime: FrameTime?) {
        //If model not used before
        if (modelRenderable != null) {
            val sceneView = arFragment.arSceneView
            val frame = sceneView.arFrame

            //Get the trackables to ensure planes are detected
            val updatedTrackables =
                frame?.getUpdatedTrackables(Plane::class.java)?.iterator()
            if (updatedTrackables?.hasNext() == true) {
                val plane = updatedTrackables.next() as Plane
                //If a plane has been detected & is being tracked by ARCore
                if (plane.trackingState == TrackingState.TRACKING) {
                    //Hide the plane discovery helper animation
                    arFragment.planeDiscoveryController?.hide()

                    //Perform a hit test at the center of the screen to place an object without tapping
                    val hitTest = frame.hitTest(screenCenter().x, screenCenter().y)
                    val hitTestIterator = hitTest?.iterator()

                    //Get all added anchors to the frame
                    if (hitTestIterator?.hasNext() == true) {
                        val hitResult = hitTestIterator.next()

                        //Create an anchor at the plane hit
                        val modelAnchor = plane.createAnchor(hitResult.hitPose)
                        //Attach a node to this anchor with the scene as the parent
                        val anchorNode = AnchorNode(modelAnchor)
                        anchorNode.setParent(sceneView.scene)

                        //Create a new TransformableNode that will carry our object
                        val transformableNode =
                            TransformableNode(arFragment.transformationSystem)
                        transformableNode.setParent(anchorNode)
                        transformableNode.renderable = modelRenderable
                        //Clear modelRenderable for ensure one time render only.
                        modelRenderable = null

                        //Alter the real world position to ensure object renders on the table top.Not somewhere inside.
                        transformableNode.worldPosition = Vector3(
                            modelAnchor.pose.tx(),
                            modelAnchor.pose.compose(
                                Pose.makeTranslation(
                                    0f,
                                    POSE_TRANSLATION_Y,
                                    0f
                                )
                            ).ty(),
                            modelAnchor.pose.tz()
                        )
                    }
                }
            }
        }
    }

    private fun screenCenter(): Vector3 = Vector3(
        getBounds().height.div(2f),
        getBounds().width.div(2f),
        0f
    )

    private fun getBounds(): Size {
        val x = Resources.getSystem().displayMetrics.widthPixels
        val y = Resources.getSystem().displayMetrics.heightPixels
        return Size(x, y)
    }

}

private const val POSE_TRANSLATION_Y = 0.05F
