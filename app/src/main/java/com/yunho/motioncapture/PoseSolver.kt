package com.yunho.motioncapture

import com.google.mediapipe.tasks.components.containers.Landmark
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import io.github.sceneview.collision.Quaternion
import io.github.sceneview.math.Rotation
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.min
import kotlin.reflect.KMutableProperty1

data class PoseRotation(val x: Float, val y: Float, val z: Float, val w: Float) {
    companion object {
        fun default() = PoseRotation(0f, 0f, 0f, 1f)

        fun PoseRotation.toRotation() = Rotation(x, y, z)
    }

    fun toQuaternion(): Quaternionf = Quaternionf(x, y, z, w)

    fun toQuaternion2(): Quaternion = Quaternion(x, y, z, w)
}

fun quaternionToFloatArray(q: Quaternion): FloatArray {
    val w = q.w
    val x = q.x
    val y = q.y
    val z = q.z
    return floatArrayOf(
        1f - 2f * (y * y + z * z),  // m00
        2f * x * y + 2f * w * z,      // m10
        2f * x * z - 2f * w * y,      // m20
        0f,                         // m30

        2f * x * y - 2f * w * z,      // m01
        1f - 2f * (x * x + z * z),    // m11
        2f * y * z + 2f * w * x,      // m21
        0f,                         // m31

        2f * x * z + 2f * w * y,      // m02
        2f * y * z - 2f * w * x,      // m12
        1f - 2f * (x * x + y * y),    // m22
        0f,                         // m32

        0f, 0f, 0f, 1f              // m03, m13, m23, m33
    )
}

class PoseSolverResultWrapper(private val poseSolverResult: PoseSolverResult) {
    val poseToEntityMap: Map<String, KMutableProperty1<PoseSolverResult, PoseRotation>> = mapOf(
        "上半身" to PoseSolverResult::upperBody,
//        "下半身" to PoseSolverResult::lowerBody,

//        "首" to PoseSolverResult::neck,

        "左腕" to PoseSolverResult::leftUpperArm,
        "左ひじ" to PoseSolverResult::leftLowerArm,

        "右腕" to PoseSolverResult::rightUpperArm,
        "右ひじ" to PoseSolverResult::rightLowerArm,

//        "左足" to PoseSolverResult::leftHip,
//        "右足" to PoseSolverResult::rightHip,
//
//        "左足首" to PoseSolverResult::leftFoot,
//        "右足首" to PoseSolverResult::rightFoot,
//
        "左手首" to PoseSolverResult::leftWrist,
        "右手首" to PoseSolverResult::rightWrist,
//
//        "左目" to PoseSolverResult::leftEyeRotation,
//        "右目" to PoseSolverResult::rightEyeRotation
    )

    fun getPoseRotationByIndex(name: String): PoseRotation? {
        return poseToEntityMap[name]?.get(poseSolverResult)
    }
}

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

enum class MainBodyIndex(val index: Int) {
    Nose(0),
    LeftEyeInner(1),
    LeftEye(2),
    LeftEyeOuter(3),
    RightEyeInner(4),
    RightEye(5),
    RightEyeOuter(6),
    LeftEar(7),
    RightEar(8),
    MouthLeft(9),
    MouthRight(10),
    LeftShoulder(11),
    RightShoulder(12),
    LeftElbow(13),
    RightElbow(14),
    LeftWrist(15),
    RightWrist(16),
    LeftPinky(17),
    RightPinky(18),
    LeftIndex(19),
    RightIndex(20),
    LeftThumb(21),
    RightThumb(22),
    LeftHip(23),
    RightHip(24),
    LeftKnee(25),
    RightKnee(26),
    LeftAnkle(27),
    RightAnkle(28),
    LeftHeel(29),
    RightHeel(30),
    LeftFootIndex(31),
    RightFootIndex(32)
}

enum class HandIndex(val index: Int) {
    Wrist(0),
    ThumbCMC(1),
    ThumbMCP(2),
    ThumbIP(3),
    ThumbTip(4),
    IndexMCP(5),
    IndexPIP(6),
    IndexDIP(7),
    IndexTip(8),
    MiddleMCP(9),
    MiddlePIP(10),
    MiddleDIP(11),
    MiddleTip(12),
    RingMCP(13),
    RingPIP(14),
    RingDIP(15),
    RingTip(16),
    PinkyMCP(17),
    PinkyPIP(18),
    PinkyDIP(19),
    PinkyTip(20)
}

enum class FaceIndex(val index: Int) {
    LeftEyeUpper(159),
    LeftEyeLower(145),
    LeftEyeLeft(33),
    LeftEyeRight(133),
    LeftEyeIris(468),
    RightEyeUpper(386),
    RightEyeLower(374),
    RightEyeLeft(362),
    RightEyeRight(263),
    RightEyeIris(473),
    UpperLipTop(13),
    LowerLipBottom(14),
    MouthLeft(61),
    MouthRight(291),
    UpperLipCenter(0),
    LowerLipCenter(17),
    LeftEar(234),
    RightEar(454)
}

operator fun List<Vector3f>.get(index: MainBodyIndex): Vector3f = this[index.index]
operator fun List<Vector3f>.get(index: HandIndex): Vector3f = this[index.index]
operator fun List<Vector3f>.get(index: FaceIndex): Vector3f = this[index.index]

const val LEFT = 0
const val RIGHT = 1

fun landmarksToVector3(landmarks: List<Landmark>): List<Vector3f> =
    landmarks.map { Vector3f(it.x(), it.y(), -it.z()) }

fun normalizedLandmarksToVector3(landmarks: List<NormalizedLandmark>): List<Vector3f> =
    landmarks.map { Vector3f(it.x(), it.y(), -it.z()) }

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

        val upperBodyQuat = upperBodyRotation.toQuaternion()
        val invUpper = Quaternionf(upperBodyQuat).conjugate()
        val localNeckDir = invUpper.transform(Vector3f(neckDir))

        val forwardDir = Vector3f(-localNeckDir.x, 0f, -localNeckDir.z).normalize()
        val tiltAngle = atan2(-localNeckDir.y, forwardDir.length())
        val tiltOffset = -PI.toFloat() / 9f
        val adjustedTiltAngle = tiltAngle + tiltOffset

        //todo check
        val horizontalQuat = Quaternionf().rotateTo(forwardDir.negate(), Vector3f(0f, 1f, 0f))
        val tiltQuat = Quaternionf().fromAxisAngleRad(Vector3f(1f, 0f, 0f), adjustedTiltAngle)

        val quat = Quaternionf(horizontalQuat).mul(tiltQuat)
        return PoseRotation(quat.x, quat.y, quat.z, quat.w)
    }

    private fun calculateUpperArmRotation(
        shoulder: Vector3f,
        elbow: Vector3f,
        upperBodyRotation: PoseRotation,
        side: Int
    ): PoseRotation {
        val armDir = Vector3f(elbow).sub(shoulder).normalize().also { it.y = -it.y }
        val upperBodyQuat = upperBodyRotation.toQuaternion()
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
        val upperArmQuat = upperArmRotation.toQuaternion()
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
        val lowerBodyQuat = lowerBodyRotation.toQuaternion()
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
        val lowerArmQuat = lowerArmRotation.toQuaternion()
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
        val parentQuat = parentRotation.toQuaternion()
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
        val parentQuat = parentRotation.toQuaternion()
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
