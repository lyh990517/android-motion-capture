package com.yunho.motioncapture

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.AndroidViewModel
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.holisticlandmarker.HolisticLandmarker
import com.google.mediapipe.tasks.vision.holisticlandmarker.HolisticLandmarker.HolisticLandmarkerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max

data class RecognitionUiState(
    val recognized: String? = null,
)

class RecognitionViewModel(application: Application) : AndroidViewModel(application) {
    val uiState = MutableStateFlow(RecognitionUiState())

    private val holisticRecognizer by lazy {
        val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath("holistic_landmarker.task")
        val baseOptions = baseOptionsBuilder.build()

        val optionsBuilder = HolisticLandmarkerOptions.builder()
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setBaseOptions(baseOptions)
            .setResultListener { result, _ ->
                uiState.update {
                    it.copy(
                        recognized = result.faceLandmarks().getOrNull(0).toString()
                    )
                }
            }

        val options = optionsBuilder.build()
        HolisticLandmarker.createFromOptions(getApplication(), options)
    }

    val imageAnalyzer = ImageAnalysis.Analyzer { image ->
        val imageBitmap = image.toBitmap()
        val scale = 500f / max(image.width, image.height)
        val scaleAndRotate = Matrix().apply {
            postScale(scale, scale)
            postRotate(image.imageInfo.rotationDegrees.toFloat())
        }
        val scaledAndRotatedBmp =
            Bitmap.createBitmap(imageBitmap, 0, 0, image.width, image.height, scaleAndRotate, true)

        image.close()

        holisticRecognizer.detectAsync(
            BitmapImageBuilder(scaledAndRotatedBmp).build(),
            System.currentTimeMillis()
        )
    }
}