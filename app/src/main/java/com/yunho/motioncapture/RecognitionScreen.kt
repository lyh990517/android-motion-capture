package com.yunho.motioncapture

import androidx.camera.core.ImageAnalysis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RecognitionScreen(
    modifier: Modifier = Modifier,
    viewModel: RecognitionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val imageAnalysisUseCase = remember {
        ImageAnalysis.Builder().build().apply {
            setAnalyzer(context.mainExecutor, viewModel.imageAnalyzer)
        }
    }

    CameraEffect(
        imageAnalysisUseCase = imageAnalysisUseCase
    )

    ModelScreen()
}