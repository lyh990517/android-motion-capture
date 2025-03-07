package com.yunho.motioncapture

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yunho.motioncapture.pose.PoseSolverResult
import com.yunho.motioncapture.pose.extractPoseByName
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode

@Composable
fun MotionCaptureScreen(
    captureResult: () -> PoseSolverResult
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val sampleModel = rememberNode {
        ModelNode(
            modelInstance = modelLoader.createModelInstance(
                assetFileLocation = "models/BoothScene.glb"
            ),
            scaleToUnits = 1f,
            autoAnimate = false
        )
    }
    val sampleEnvironment = rememberNode {
        ModelNode(
            modelInstance = modelLoader.createModelInstance(
                assetFileLocation = "models/skybox_anime_sky.glb"
            )
        )
    }
    val cameraNode = rememberCameraNode(engine) {
        position = Position(y = 2f, z = 1.0f)
        lookAt(sampleModel)
    }

    Scene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        cameraNode = cameraNode,
        cameraManipulator = rememberCameraManipulator(
            orbitHomePosition = cameraNode.worldPosition,
            targetPosition = sampleModel.worldPosition
        ),
        childNodes = listOf(
            sampleModel,
            sampleEnvironment
        ),
        onFrame = {
            cameraNode.lookAt(sampleModel)

            with(sampleModel.model) {
                entities.toList()
                    .mapNotNull { entity ->
                        getName(entity).let { name ->
                            captureResult().extractPoseByName(name)
                                ?.toQuaternion()
                                ?.let { quaternion -> quaternion to entity }
                        }
                    }
                    .forEach { (quaternion, entity) ->
                        engine.transformManager.setTransform(entity, quaternion.toFloatArray())
                    }
            }
        }
    )
}