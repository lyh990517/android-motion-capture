package com.yunho.motioncapture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RecognitionScreen(
    viewModel: RecognitionViewModel = viewModel()
) {
    val captureResult by viewModel.captureResult.collectAsStateWithLifecycle()

    CameraPreview(viewModel.imageAnalyzer)

    MotionCaptureScreen { captureResult }
}