package com.yunho.motioncapture

import com.google.mediapipe.tasks.components.containers.Landmark
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.yunho.motioncapture.pose.FaceIndex
import com.yunho.motioncapture.pose.HandIndex
import com.yunho.motioncapture.pose.MainBodyIndex
import com.yunho.motioncapture.pose.PoseRotation
import com.yunho.motioncapture.pose.PoseSolverResult
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.min

object PoseSolver {
    private val defaultDirections: HashMap<String, Vector3f> = hashMapOf(
        "upper_body" to Vector3f(1f, 0f, 0f),
        "lower_body" to Vector3f(1f, 0f, 0f),
        "hip" to Vector3f(0f, -1f, 0f),
        "left_arm" to Vector3f(1f, -1f, 0f),
        "right_arm" to Vector3f(-1f, -1f, 0f)
    )

    fun solve(
        mainBodyLandmarks: List<Landmark>,
        leftHandLandmarks: List<Landmark>,
        rightHandLandmarks: List<Landmark>,
        faceLandmarks: List<NormalizedLandmark>
    ): PoseSolverResult {
        if (mainBodyLandmarks.isEmpty()) return PoseSolverResult()

        val mainBody = landmarksToVector3(mainBodyLandmarks)
        val result = PoseSolverResult()

        val leftShoulder = mainBody[MainBodyIndex.LeftShoulder]
        val rightShoulder = mainBody[MainBodyIndex.RightShoulder]
        val leftHip = mainBody[MainBodyIndex.LeftHip]
        val rightHip = mainBody[MainBodyIndex.RightHip]

        result.upperBody = calculateUpperBodyRotation(leftShoulder, rightShoulder)
        result.lowerBody = calculateLowerBodyRotation(leftHip, rightHip)
        result.neck = calculateNeckRotation(
            mainBody[MainBodyIndex.Nose],
            leftShoulder,
            rightShoulder,
            result.upperBody
        )
        result.leftUpperArm = calculateUpperArmRotation(
            leftShoulder,
            mainBody[MainBodyIndex.LeftElbow],
            result.upperBody,
            LEFT
        )
        result.leftLowerArm = calculateLowerArmRotation(
            mainBody[MainBodyIndex.LeftElbow],
            mainBody[MainBodyIndex.LeftWrist],
            result.leftUpperArm,
            LEFT
        )
        result.rightUpperArm = calculateUpperArmRotation(
            rightShoulder,
            mainBody[MainBodyIndex.RightElbow],
            result.upperBody,
            RIGHT
        )
        result.rightLowerArm = calculateLowerArmRotation(
            mainBody[MainBodyIndex.RightElbow],
            mainBody[MainBodyIndex.RightWrist],
            result.rightUpperArm,
            RIGHT
        )
        result.leftHip = calculateHipRotation(
            leftHip,
            mainBody[MainBodyIndex.LeftKnee],
            result.lowerBody
        )
        result.rightHip = calculateHipRotation(
            rightHip,
            mainBody[MainBodyIndex.RightKnee],
            result.lowerBody
        )
        result.leftFoot = result.leftHip
        result.rightFoot = result.rightHip

        if (leftHandLandmarks.isNotEmpty()) {
            val leftHand = landmarksToVector3(leftHandLandmarks)
            result.leftWrist = calculateWristRotation(
                leftHand[HandIndex.Wrist],
                leftHand[HandIndex.PinkyMCP],
                result.leftLowerArm,
                LEFT
            )
            result.leftThumbCMC = calculateThumbRotation(
                leftHand[HandIndex.ThumbCMC],
                leftHand[HandIndex.ThumbMCP],
                result.leftWrist,
                LEFT
            )
            result.leftThumbMCP = calculateThumbRotation(
                leftHand[HandIndex.ThumbMCP],
                leftHand[HandIndex.ThumbIP],
                result.leftThumbCMC,
                LEFT
            )
            result.leftIndexFingerMCP = calculateFingerRotation(
                leftHand[HandIndex.IndexMCP],
                leftHand[HandIndex.IndexPIP],
                result.leftWrist,
                LEFT
            )
            result.leftIndexFingerPIP = calculateFingerRotation(
                leftHand[HandIndex.IndexPIP],
                leftHand[HandIndex.IndexDIP],
                result.leftIndexFingerMCP,
                LEFT
            )
            result.leftIndexFingerDIP = calculateFingerRotation(
                leftHand[HandIndex.IndexDIP],
                leftHand[HandIndex.IndexTip],
                result.leftIndexFingerPIP,
                LEFT
            )
            result.leftMiddleFingerMCP = calculateFingerRotation(
                leftHand[HandIndex.MiddleMCP],
                leftHand[HandIndex.MiddlePIP],
                result.leftWrist,
                LEFT
            )
            result.leftMiddleFingerPIP = calculateFingerRotation(
                leftHand[HandIndex.MiddlePIP],
                leftHand[HandIndex.MiddleDIP],
                result.leftMiddleFingerMCP,
                LEFT
            )
            result.leftMiddleFingerDIP = calculateFingerRotation(
                leftHand[HandIndex.MiddleDIP],
                leftHand[HandIndex.MiddleTip],
                result.leftMiddleFingerPIP,
                LEFT
            )
            result.leftRingFingerMCP = calculateFingerRotation(
                leftHand[HandIndex.RingMCP],
                leftHand[HandIndex.RingPIP],
                result.leftWrist,
                LEFT
            )
            result.leftRingFingerPIP = calculateFingerRotation(
                leftHand[HandIndex.RingPIP],
                leftHand[HandIndex.RingDIP],
                result.leftRingFingerMCP,
                LEFT
            )
            result.leftRingFingerDIP = calculateFingerRotation(
                leftHand[HandIndex.RingDIP],
                leftHand[HandIndex.RingTip],
                result.leftRingFingerPIP,
                LEFT
            )
            result.leftPinkyFingerMCP = calculateFingerRotation(
                leftHand[HandIndex.PinkyMCP],
                leftHand[HandIndex.PinkyPIP],
                result.leftWrist,
                LEFT
            )
            result.leftPinkyFingerPIP = calculateFingerRotation(
                leftHand[HandIndex.PinkyPIP],
                leftHand[HandIndex.PinkyDIP],
                result.leftPinkyFingerMCP,
                LEFT
            )
            result.leftPinkyFingerDIP = calculateFingerRotation(
                leftHand[HandIndex.PinkyDIP],
                leftHand[HandIndex.PinkyTip],
                result.leftPinkyFingerPIP,
                LEFT
            )
        }

        if (rightHandLandmarks.isNotEmpty()) {
            val rightHand = landmarksToVector3(rightHandLandmarks)
            result.rightWrist = calculateWristRotation(
                rightHand[HandIndex.Wrist],
                rightHand[HandIndex.PinkyMCP],
                result.rightLowerArm,
                RIGHT
            )
            result.rightThumbCMC = calculateThumbRotation(
                rightHand[HandIndex.ThumbCMC],
                rightHand[HandIndex.ThumbMCP],
                result.rightWrist,
                RIGHT
            )
            result.rightThumbMCP = calculateThumbRotation(
                rightHand[HandIndex.ThumbMCP],
                rightHand[HandIndex.ThumbIP],
                result.rightThumbCMC,
                RIGHT
            )
            result.rightIndexFingerMCP = calculateFingerRotation(
                rightHand[HandIndex.IndexMCP],
                rightHand[HandIndex.IndexPIP],
                result.rightWrist,
                RIGHT
            )
            result.rightIndexFingerPIP = calculateFingerRotation(
                rightHand[HandIndex.IndexPIP],
                rightHand[HandIndex.IndexDIP],
                result.rightIndexFingerMCP,
                RIGHT
            )
            result.rightIndexFingerDIP = calculateFingerRotation(
                rightHand[HandIndex.IndexDIP],
                rightHand[HandIndex.IndexTip],
                result.rightIndexFingerPIP,
                RIGHT
            )
            result.rightMiddleFingerMCP = calculateFingerRotation(
                rightHand[HandIndex.MiddleMCP],
                rightHand[HandIndex.MiddlePIP],
                result.rightWrist,
                RIGHT
            )
            result.rightMiddleFingerPIP = calculateFingerRotation(
                rightHand[HandIndex.MiddlePIP],
                rightHand[HandIndex.MiddleDIP],
                result.rightMiddleFingerMCP,
                RIGHT
            )
            result.rightMiddleFingerDIP = calculateFingerRotation(
                rightHand[HandIndex.MiddleDIP],
                rightHand[HandIndex.MiddleTip],
                result.rightMiddleFingerPIP,
                RIGHT
            )
            result.rightRingFingerMCP = calculateFingerRotation(
                rightHand[HandIndex.RingMCP],
                rightHand[HandIndex.RingPIP],
                result.rightWrist,
                RIGHT
            )
            result.rightRingFingerPIP = calculateFingerRotation(
                rightHand[HandIndex.RingPIP],
                rightHand[HandIndex.RingDIP],
                result.rightRingFingerMCP,
                RIGHT
            )
            result.rightRingFingerDIP = calculateFingerRotation(
                rightHand[HandIndex.RingDIP],
                rightHand[HandIndex.RingTip],
                result.rightRingFingerPIP,
                RIGHT
            )
            result.rightPinkyFingerMCP = calculateFingerRotation(
                rightHand[HandIndex.PinkyMCP],
                rightHand[HandIndex.PinkyPIP],
                result.rightWrist,
                RIGHT
            )
            result.rightPinkyFingerPIP = calculateFingerRotation(
                rightHand[HandIndex.PinkyPIP],
                rightHand[HandIndex.PinkyDIP],
                result.rightPinkyFingerMCP,
                RIGHT
            )
            result.rightPinkyFingerDIP = calculateFingerRotation(
                rightHand[HandIndex.PinkyDIP],
                rightHand[HandIndex.PinkyTip],
                result.rightPinkyFingerPIP,
                RIGHT
            )
        }

        if (faceLandmarks.isNotEmpty()) {
            val face = normalizedLandmarksToVector3(faceLandmarks)
            val leftEyeGaze = calculateEyeGaze(
                face[FaceIndex.LeftEyeLeft],
                face[FaceIndex.LeftEyeRight],
                face[FaceIndex.LeftEyeIris]
            )
            val rightEyeGaze = calculateEyeGaze(
                face[FaceIndex.RightEyeLeft],
                face[FaceIndex.RightEyeRight],
                face[FaceIndex.RightEyeIris]
            )
            val averageGaze = Pair(
                (leftEyeGaze.first + rightEyeGaze.first) / 2f,
                (leftEyeGaze.second + rightEyeGaze.second) / 2f
            )
            result.leftEyeOpenness = calculateEyeOpenness(
                face[FaceIndex.RightEyeLeft],
                face[FaceIndex.RightEyeRight],
                face[FaceIndex.RightEyeUpper],
                face[FaceIndex.RightEyeLower]
            )
            result.rightEyeOpenness = calculateEyeOpenness(
                face[FaceIndex.LeftEyeLeft],
                face[FaceIndex.LeftEyeRight],
                face[FaceIndex.LeftEyeUpper],
                face[FaceIndex.LeftEyeLower]
            )
            result.leftEyeRotation = calculateEyeRotation(averageGaze.first, averageGaze.second)
            result.rightEyeRotation = calculateEyeRotation(averageGaze.first, averageGaze.second)
            result.mouthOpenness = calculateMouthOpenness(
                face[FaceIndex.UpperLipTop],
                face[FaceIndex.LowerLipBottom],
                face[FaceIndex.MouthLeft],
                face[FaceIndex.MouthRight]
            )
        }

        return result
    }

    private fun calculateUpperBodyRotation(
        leftShoulder: Vector3f,
        rightShoulder: Vector3f
    ): PoseRotation {
        val spineDir = Vector3f(leftShoulder).sub(rightShoulder).normalize().also { it.y = -it.y }
        val defaultUpperBody = defaultDirections["upper_body"] ?: Vector3f(1f, 0f, 0f)
        val spineRotation = Quaternionf().rotationTo(defaultUpperBody, spineDir)

        val bendDir =
            Vector3f(leftShoulder).add(rightShoulder).mul(0.5f).normalize().also { it.y = -it.y }
        val bendAngle = acos(bendDir.dot(Vector3f(0f, 1f, 0f)).toDouble()).toFloat()
        val bendAxis = Vector3f(0f, 1f, 0f).cross(bendDir, Vector3f()).normalize()
        val bendRotation = Quaternionf().fromAxisAngleRad(bendAxis, bendAngle)

        val quat = Quaternionf(spineRotation).mul(bendRotation)
        return PoseRotation(quat.x, quat.y, quat.z, quat.w)
    }

    private fun calculateLowerBodyRotation(leftHip: Vector3f, rightHip: Vector3f): PoseRotation {
        val hipDir = Vector3f(leftHip).sub(rightHip).normalize().also { it.y = -it.y }
        val defaultLowerBody = defaultDirections["lower_body"] ?: Vector3f(1f, 0f, 0f)
        val quat = Quaternionf().rotationTo(defaultLowerBody, hipDir)
        return PoseRotation(quat.x, quat.y, quat.z, quat.w)
    }

    private fun calculateNeckRotation(
        nose: Vector3f,
        leftShoulder: Vector3f,
        rightShoulder: Vector3f,
        upperBodyRotation: PoseRotation
    ): PoseRotation {
        val neckPos = Vector3f(leftShoulder).add(rightShoulder).mul(0.5f)
        val neckDir = Vector3f(nose).sub(neckPos).normalize()
        val upperBodyQuat = Quaternionf(upperBodyRotation.toQuaternionf())
        val invUpperBodyQuat = Quaternionf(upperBodyQuat).conjugate()
        val localNeckDir = Vector3f(neckDir)

        invUpperBodyQuat.transform(localNeckDir)

        val forwardDir = Vector3f(-localNeckDir.x, 0f, -localNeckDir.z).normalize()
        val tiltAngle = atan2(-localNeckDir.y, forwardDir.length())
        val tiltOffset = (-PI / 9).toFloat()
        val adjustedTiltAngle = tiltAngle + tiltOffset
        val horizontalQuat = Quaternionf().lookAlong(forwardDir, Vector3f(0f, 1f, 0f))
        val tiltQuat = Quaternionf().rotateX(-adjustedTiltAngle)
        val resultQuat = Quaternionf(horizontalQuat).mul(tiltQuat)

        return PoseRotation(resultQuat.x, resultQuat.y, resultQuat.z, resultQuat.w)
    }

    private fun calculateUpperArmRotation(
        shoulder: Vector3f,
        elbow: Vector3f,
        upperBodyRotation: PoseRotation,
        side: Int
    ): PoseRotation {
        val armDir = Vector3f(elbow).sub(shoulder).normalize().also { it.y = -it.y }
        val upperBodyQuat = upperBodyRotation.toQuaternionf()
        val invUpper = Quaternionf(upperBodyQuat).conjugate()
        val localArmDir = invUpper.transform(Vector3f(armDir))

        val defaultDir = if (side == LEFT) {
            defaultDirections["left_arm"] ?: Vector3f(1f, -1f, 0f)
        } else {
            defaultDirections["right_arm"] ?: Vector3f(-1f, -1f, 0f)
        }
        val quat = Quaternionf().rotationTo(defaultDir, localArmDir)
        return PoseRotation(quat.x, quat.y, quat.z, quat.w)
    }

    private fun calculateLowerArmRotation(
        elbow: Vector3f,
        wrist: Vector3f,
        upperArmRotation: PoseRotation,
        side: Int
    ): PoseRotation {
        val lowerArmDir = Vector3f(wrist).sub(elbow).normalize().also { it.y = -it.y }
        val upperArmQuat = upperArmRotation.toQuaternionf()
        val invUpperArm = Quaternionf(upperArmQuat).conjugate()
        val localLowerArmDir = invUpperArm.transform(Vector3f(lowerArmDir))

        val defaultDir = if (side == LEFT) {
            defaultDirections["left_arm"] ?: Vector3f(1f, -1f, 0f)
        } else {
            defaultDirections["right_arm"] ?: Vector3f(-1f, -1f, 0f)
        }
        val quat = Quaternionf().rotationTo(defaultDir, localLowerArmDir)
        return PoseRotation(quat.x, quat.y, quat.z, quat.w)
    }

    private fun calculateHipRotation(
        hip: Vector3f,
        knee: Vector3f,
        lowerBodyRotation: PoseRotation
    ): PoseRotation {
        val legDir = Vector3f(knee).sub(hip).normalize().also { it.y = -it.y }
        val lowerBodyQuat = lowerBodyRotation.toQuaternionf()
        val invLowerBody = Quaternionf(lowerBodyQuat).conjugate()
        val localLegDir = invLowerBody.transform(Vector3f(legDir))

        val angle = defaultDirections["hip"]?.angle(localLegDir) ?: 0f
        val maxAngle = PI.toFloat() / 2f
        val clampedAngle = min(angle, maxAngle)

        val rotationAxis =
            Vector3f(defaultDirections["hip"]).cross(localLegDir, Vector3f()).normalize()
        val quat = Quaternionf().fromAxisAngleRad(rotationAxis, clampedAngle)
        return PoseRotation(quat.x, quat.y, quat.z, quat.w)
    }

    private fun calculateWristRotation(
        wrist: Vector3f,
        middleFinger: Vector3f,
        lowerArmRotation: PoseRotation,
        side: Int
    ): PoseRotation {
        val wristDir = Vector3f(middleFinger).sub(wrist).normalize().also { it.y = -it.y }
        val lowerArmQuat = lowerArmRotation.toQuaternionf()
        val invLowerArm = Quaternionf(lowerArmQuat).conjugate()
        val localWristDir = invLowerArm.transform(Vector3f(wristDir))

        val defaultDir = if (side == LEFT) {
            defaultDirections["left_arm"] ?: Vector3f(1f, -1f, 0f)
        } else {
            defaultDirections["right_arm"] ?: Vector3f(-1f, -1f, 0f)
        }
        val quat = Quaternionf().rotationTo(defaultDir, localWristDir)
        return PoseRotation(quat.x, quat.y, quat.z, quat.w)
    }

    private fun calculateThumbRotation(
        currentJoint: Vector3f,
        nextJoint: Vector3f,
        parentRotation: PoseRotation,
        side: Int
    ): PoseRotation {
        val jointDir = Vector3f(nextJoint).sub(currentJoint).normalize().also { it.y = -it.y }
        val parentQuat = parentRotation.toQuaternionf()
        val invParent = Quaternionf(parentQuat).conjugate()
        val localJointDir = invParent.transform(Vector3f(jointDir))

        val defaultDir = Vector3f(if (side == LEFT) -1f else 1f, -1f, -1f).normalize()
        val quat = Quaternionf().rotationTo(defaultDir, localJointDir)
        return PoseRotation(quat.x, quat.y, quat.z, quat.w)
    }

    private fun calculateFingerRotation(
        currentJoint: Vector3f,
        nextJoint: Vector3f,
        parentRotation: PoseRotation,
        side: Int
    ): PoseRotation {
        val jointDir = Vector3f(nextJoint).sub(currentJoint).normalize().also { it.y = -it.y }
        val parentQuat = parentRotation.toQuaternionf()
        val invParent = Quaternionf(parentQuat).conjugate()
        val localJointDir = invParent.transform(Vector3f(jointDir))

        val defaultDir = Vector3f(if (side == LEFT) 1f else -1f, -1f, 0f).normalize()
        val quat = Quaternionf().rotationTo(defaultDir, localJointDir)
        return PoseRotation(quat.x, quat.y, quat.z, quat.w)
    }

    private fun calculateEyeRotation(x: Float, y: Float): PoseRotation {
        val maxHorizontalRotation = PI.toFloat() / 6f
        val maxVerticalRotation = PI.toFloat() / 12f

        val xRotation = y * maxVerticalRotation
        val yRotation = -x * maxHorizontalRotation

        //todo check
        val quat = Quaternionf().rotateXYZ(xRotation, yRotation, 0f)
        return PoseRotation(quat.x, quat.y, quat.z, quat.w)
    }

    private fun calculateEyeGaze(
        eyeLeft: Vector3f,
        eyeRight: Vector3f,
        iris: Vector3f
    ): Pair<Float, Float> {
        val eyeCenter = Vector3f(eyeLeft).mul(10f).add(Vector3f(eyeRight).mul(10f)).mul(0.5f)
        val eyeWidth = Vector3f(eyeLeft).mul(10f).sub(Vector3f(eyeRight).mul(10f)).length()
        val eyeHeight = eyeWidth * 0.5f

        val scaledIris = Vector3f(iris).mul(10f)
        val x = ((scaledIris.x - eyeCenter.x) / (eyeWidth * 0.5f)).coerceIn(-1f, 1f)
        val y = ((scaledIris.y - eyeCenter.y) / (eyeHeight * 0.5f)).coerceIn(-0.5f, 0.5f)
        return Pair(x, y)
    }

    private fun calculateEyeOpenness(
        eyeLeft: Vector3f,
        eyeRight: Vector3f,
        eyeUpper: Vector3f,
        eyeLower: Vector3f
    ): Float {
        val eyeHeight = Vector3f(eyeUpper).sub(eyeLower).length()
        val eyeWidth = Vector3f(eyeLeft).sub(eyeRight).length()
        val aspectRatio = eyeHeight / eyeWidth

        val openRatio = 0.28f
        val closedRatio = 0.15f

        return when {
            aspectRatio <= closedRatio -> 0f
            aspectRatio >= openRatio -> 1f
            else -> (aspectRatio - closedRatio) / (openRatio - closedRatio)
        }
    }

    private fun calculateMouthOpenness(
        upperLipTop: Vector3f,
        lowerLipBottom: Vector3f,
        mouthLeft: Vector3f,
        mouthRight: Vector3f
    ): Float {
        val mouthHeight = Vector3f(upperLipTop).sub(lowerLipBottom).length()
        val mouthWidth = Vector3f(mouthLeft).sub(mouthRight).length()
        val openness = ((mouthHeight / mouthWidth) - 0.1f) / 0.5f
        return openness.coerceIn(0f, 0.7f)
    }
}
