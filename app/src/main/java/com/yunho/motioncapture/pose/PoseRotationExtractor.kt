package com.yunho.motioncapture.pose

import kotlin.reflect.KMutableProperty1

class PoseRotationExtractor(private val poseSolverResult: PoseSolverResult) {
    private val poseToEntityMap: Map<String, KMutableProperty1<PoseSolverResult, PoseRotation>> =
        mapOf(
            "上半身" to PoseSolverResult::upperBody,
            "下半身" to PoseSolverResult::lowerBody,

//        "首" to PoseSolverResult::neck,

            "左腕" to PoseSolverResult::leftUpperArm,
            "左ひじ" to PoseSolverResult::leftLowerArm,

            "右腕" to PoseSolverResult::rightUpperArm,
            "右ひじ" to PoseSolverResult::rightLowerArm,

            "左足" to PoseSolverResult::leftHip,
            "右足" to PoseSolverResult::rightHip,

//        "左足首" to PoseSolverResult::leftFoot,
//        "右足首" to PoseSolverResult::rightFoot,

            "左手首" to PoseSolverResult::leftWrist,
            "右手首" to PoseSolverResult::rightWrist,

//        "左目" to PoseSolverResult::leftEyeRotation,
//        "右目" to PoseSolverResult::rightEyeRotation,

            "左親指１" to PoseSolverResult::leftThumbCMC,
            "左親指２" to PoseSolverResult::leftThumbMCP,
            "左人指１" to PoseSolverResult::leftIndexFingerMCP,
            "左人指２" to PoseSolverResult::leftIndexFingerPIP,
            "左人指３" to PoseSolverResult::leftIndexFingerDIP,
            "左中指１" to PoseSolverResult::leftMiddleFingerMCP,
            "左中指２" to PoseSolverResult::leftMiddleFingerPIP,
            "左中指３" to PoseSolverResult::leftMiddleFingerDIP,
            "左薬指１" to PoseSolverResult::leftRingFingerMCP,
            "左薬指２" to PoseSolverResult::leftRingFingerPIP,
            "左薬指３" to PoseSolverResult::leftRingFingerDIP,
            "左小指１" to PoseSolverResult::leftPinkyFingerMCP,
            "左小指２" to PoseSolverResult::leftPinkyFingerPIP,
            "左小指３" to PoseSolverResult::leftPinkyFingerDIP,

            "右親指１" to PoseSolverResult::rightThumbCMC,
            "右親指２" to PoseSolverResult::rightThumbMCP,
            "右人指１" to PoseSolverResult::rightIndexFingerMCP,
            "右人指２" to PoseSolverResult::rightIndexFingerPIP,
            "右人指３" to PoseSolverResult::rightIndexFingerDIP,
            "右中指１" to PoseSolverResult::rightMiddleFingerMCP,
            "右中指２" to PoseSolverResult::rightMiddleFingerPIP,
            "右中指３" to PoseSolverResult::rightMiddleFingerDIP,
            "右薬指１" to PoseSolverResult::rightRingFingerMCP,
            "右薬指２" to PoseSolverResult::rightRingFingerPIP,
            "右薬指３" to PoseSolverResult::rightRingFingerDIP,
            "右小指１" to PoseSolverResult::rightPinkyFingerMCP,
            "右小指２" to PoseSolverResult::rightPinkyFingerPIP,
            "右小指３" to PoseSolverResult::rightPinkyFingerDIP
        )

    fun extractByName(name: String): PoseRotation? {
        return poseToEntityMap[name]?.get(poseSolverResult)
    }
}