package com.yunho.motioncapture

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

@Composable
fun ModelScreen(
    pose: () -> PoseSolverResult
) {
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
            position = Position(x = 0f, y = 0f, z = 0f)
        }
    }

    val cameraNode = rememberCameraNode(engine) {
        position = Position(y = 2f, z = 1.0f)
        lookAt(kizunaAi)
        kizunaAi.addChildNode(this)
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
            sky
        ),
        onFrame = {
            cameraNode.lookAt(kizunaAi)

            val wrap = PoseSolverResultWrapper(pose())

            kizunaAi.model.entities.forEach { entity ->
                val poseRotation = wrap.getPoseRotationByIndex(entity)
                val poseQuaternion = poseRotation?.toQuaternion2()
                val floatArray = poseQuaternion?.let { quat -> quaternionToFloatArray(quat) }

                floatArray?.let { array ->
                    kizunaAi.model.engine.transformManager.setTransform(
                        entity,
                        array
                    )
                }
            }
        }
    )
}