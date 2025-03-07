package com.yunho.motioncapture

import com.google.mediapipe.tasks.components.containers.Landmark
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import io.github.sceneview.collision.Quaternion
import org.joml.Vector3f

fun Quaternion.toFloatArray(): FloatArray {
    return floatArrayOf(
        1f - 2f * (y * y + z * z),
        2f * x * y + 2f * w * z,
        2f * x * z - 2f * w * y,
        0f,

        2f * x * y - 2f * w * z,
        1f - 2f * (x * x + z * z),
        2f * y * z + 2f * w * x,
        0f,

        2f * x * z + 2f * w * y,
        2f * y * z - 2f * w * x,
        1f - 2f * (x * x + y * y),
        0f,

        0f, 0f, 0f, 1f
    )
}

// z axis reversed
fun landmarksToVector3(landmarks: List<Landmark>): List<Vector3f> =
    landmarks.map { Vector3f(it.x(), it.y(), -it.z()) }

fun normalizedLandmarksToVector3(landmarks: List<NormalizedLandmark>): List<Vector3f> =
    landmarks.map { Vector3f(it.x(), it.y(), -it.z()) }