package com.yunho.motioncapture.pose

import io.github.sceneview.collision.Quaternion
import org.joml.Quaternionf

data class PoseRotation(val x: Float, val y: Float, val z: Float, val w: Float) {
    companion object {
        fun default() = PoseRotation(0f, 0f, 0f, 1f)
    }

    fun toQuaternionf(): Quaternionf = Quaternionf(x, y, z, w)

    fun toQuaternion(): Quaternion = Quaternion(x, y, z, w)
}