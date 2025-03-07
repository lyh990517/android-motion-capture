package com.yunho.motioncapture

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberOnGestureListener

@Composable
fun ModelScreen() {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    val kizunaAi = rememberNode {
        ModelNode(
            modelInstance = modelLoader.createModelInstance(
                assetFileLocation = "models/BoothScene.glb"
            ),
            scaleToUnits = 7f,
            autoAnimate = false
        ).apply {
            playAnimation(0, 1f, true)

            renderableNodes.forEach { node ->
                Log.e("123", "${node.name}")
            }
            position = Position(x = 0f, y = 0f, z = 0f)
        }
    }

    val cameraNode = rememberCameraNode(engine) {
        position = Position(y = 2f, z = 1.0f)
        lookAt(kizunaAi)
        kizunaAi.addChildNode(this)
    }

    val modelNode = rememberNode {
        ModelNode(
            modelInstance = modelLoader.createModelInstance(
                assetFileLocation = "models/tiny_isometric_room.glb"
            )
        ).apply {
            scale *= 0.02f
        }
    }

    val sky = rememberNode {
        ModelNode(
            modelInstance = modelLoader.createModelInstance(
                assetFileLocation = "models/skybox_anime_sky.glb"
            )
        )
    }

    Scene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        cameraNode = cameraNode,
        cameraManipulator = rememberCameraManipulator(
            orbitHomePosition = cameraNode.worldPosition,
            targetPosition = kizunaAi.worldPosition
        ),
        childNodes = listOf(
            kizunaAi,
            sky,
            modelNode
        ),
        onFrame = {
            cameraNode.lookAt(kizunaAi)
        },
        onGestureListener = rememberOnGestureListener(
            onDoubleTap = { _, node ->
                node?.apply {
                    scale *= 2.0f
                }
            }
        )
    )
}