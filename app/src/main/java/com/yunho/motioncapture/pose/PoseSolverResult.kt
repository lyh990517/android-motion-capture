package com.yunho.motioncapture.pose

data class PoseSolverResult(
    var upperBody: PoseRotation = PoseRotation.default(),
    var lowerBody: PoseRotation = PoseRotation.default(),
    var neck: PoseRotation = PoseRotation.default(),
    var leftHip: PoseRotation = PoseRotation.default(),
    var rightHip: PoseRotation = PoseRotation.default(),
    var leftFoot: PoseRotation = PoseRotation.default(),
    var rightFoot: PoseRotation = PoseRotation.default(),
    var leftUpperArm: PoseRotation = PoseRotation.default(),
    var rightUpperArm: PoseRotation = PoseRotation.default(),
    var leftLowerArm: PoseRotation = PoseRotation.default(),
    var rightLowerArm: PoseRotation = PoseRotation.default(),
    var leftWrist: PoseRotation = PoseRotation.default(),
    var rightWrist: PoseRotation = PoseRotation.default(),
    var leftThumbCMC: PoseRotation = PoseRotation.default(),
    var leftThumbMCP: PoseRotation = PoseRotation.default(),
    var leftIndexFingerMCP: PoseRotation = PoseRotation.default(),
    var leftIndexFingerPIP: PoseRotation = PoseRotation.default(),
    var leftIndexFingerDIP: PoseRotation = PoseRotation.default(),
    var leftMiddleFingerMCP: PoseRotation = PoseRotation.default(),
    var leftMiddleFingerPIP: PoseRotation = PoseRotation.default(),
    var leftMiddleFingerDIP: PoseRotation = PoseRotation.default(),
    var leftRingFingerMCP: PoseRotation = PoseRotation.default(),
    var leftRingFingerPIP: PoseRotation = PoseRotation.default(),
    var leftRingFingerDIP: PoseRotation = PoseRotation.default(),
    var leftPinkyFingerMCP: PoseRotation = PoseRotation.default(),
    var leftPinkyFingerPIP: PoseRotation = PoseRotation.default(),
    var leftPinkyFingerDIP: PoseRotation = PoseRotation.default(),
    var rightThumbCMC: PoseRotation = PoseRotation.default(),
    var rightThumbMCP: PoseRotation = PoseRotation.default(),
    var rightIndexFingerMCP: PoseRotation = PoseRotation.default(),
    var rightIndexFingerPIP: PoseRotation = PoseRotation.default(),
    var rightIndexFingerDIP: PoseRotation = PoseRotation.default(),
    var rightMiddleFingerMCP: PoseRotation = PoseRotation.default(),
    var rightMiddleFingerPIP: PoseRotation = PoseRotation.default(),
    var rightMiddleFingerDIP: PoseRotation = PoseRotation.default(),
    var rightRingFingerMCP: PoseRotation = PoseRotation.default(),
    var rightRingFingerPIP: PoseRotation = PoseRotation.default(),
    var rightRingFingerDIP: PoseRotation = PoseRotation.default(),
    var rightPinkyFingerMCP: PoseRotation = PoseRotation.default(),
    var rightPinkyFingerPIP: PoseRotation = PoseRotation.default(),
    var rightPinkyFingerDIP: PoseRotation = PoseRotation.default(),
    var leftEyeRotation: PoseRotation = PoseRotation.default(),
    var rightEyeRotation: PoseRotation = PoseRotation.default(),
    var leftEyeOpenness: Float = 0f,
    var rightEyeOpenness: Float = 0f,
    var mouthOpenness: Float = 0f
) {
    override fun toString(): String {
        return """
            PoseSolverResult(
                ─── Body ───
                upperBody: $upperBody
                lowerBody: $lowerBody
                neck: $neck
                
                ─── Hips & Feet ───
                leftHip: $leftHip, rightHip: $rightHip
                leftFoot: $leftFoot, rightFoot: $rightFoot
                
                ─── Arms ───
                leftUpperArm: $leftUpperArm, rightUpperArm: $rightUpperArm
                leftLowerArm: $leftLowerArm, rightLowerArm: $rightLowerArm
                leftWrist: $leftWrist, rightWrist: $rightWrist
                
                ─── Hands (Left) ───
                leftThumbCMC: $leftThumbCMC, leftThumbMCP: $leftThumbMCP
                leftIndexFingerMCP: $leftIndexFingerMCP, leftIndexFingerPIP: $leftIndexFingerPIP, leftIndexFingerDIP: $leftIndexFingerDIP
                leftMiddleFingerMCP: $leftMiddleFingerMCP, leftMiddleFingerPIP: $leftMiddleFingerPIP, leftMiddleFingerDIP: $leftMiddleFingerDIP
                leftRingFingerMCP: $leftRingFingerMCP, leftRingFingerPIP: $leftRingFingerPIP, leftRingFingerDIP: $leftRingFingerDIP
                leftPinkyFingerMCP: $leftPinkyFingerMCP, leftPinkyFingerPIP: $leftPinkyFingerPIP, leftPinkyFingerDIP: $leftPinkyFingerDIP
                
                ─── Hands (Right) ───
                rightThumbCMC: $rightThumbCMC, rightThumbMCP: $rightThumbMCP
                rightIndexFingerMCP: $rightIndexFingerMCP, rightIndexFingerPIP: $rightIndexFingerPIP, rightIndexFingerDIP: $rightIndexFingerDIP
                rightMiddleFingerMCP: $rightMiddleFingerMCP, rightMiddleFingerPIP: $rightMiddleFingerPIP, rightMiddleFingerDIP: $rightMiddleFingerDIP
                rightRingFingerMCP: $rightRingFingerMCP, rightRingFingerPIP: $rightRingFingerPIP, rightRingFingerDIP: $rightRingFingerDIP
                rightPinkyFingerMCP: $rightPinkyFingerMCP, rightPinkyFingerPIP: $rightPinkyFingerPIP, rightPinkyFingerDIP: $rightPinkyFingerDIP
                
                ─── Facial Features ───
                leftEyeRotation: $leftEyeRotation, rightEyeRotation: $rightEyeRotation
                leftEyeOpenness: $leftEyeOpenness, rightEyeOpenness: $rightEyeOpenness
                mouthOpenness: $mouthOpenness
            )
        """.trimIndent()
    }
}