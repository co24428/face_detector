package com.example.face_detector

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.face_detector.ui.theme.Face_detectorTheme
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.common.InputImage



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Face_detectorTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var showImage by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (showImage) {
            ImageScreen(
                onBack = { showImage = false }
            )
        } else {
            Button(onClick = { showImage = true }) {
                Text("Detect Face")
            }
        }
    }
}

@Composable
fun ImageScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val bitmap = remember {
//        BitmapFactory.decodeResource(context.resources, R.drawable.face_sample)
        BitmapFactory.decodeResource(context.resources, R.drawable.face_sample2)
//        BitmapFactory.decodeResource(context.resources, R.drawable.person)
    }

    var faceRects by remember { mutableStateOf<List<Rect>>(emptyList()) }

    // process result of image detection
    LaunchedEffect(Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val detector = FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build()
        )
        detector.process(image)
            .addOnSuccessListener { faces ->
                faceRects = faces.map {
                    Rect(
                        left = it.boundingBox.left.toFloat(),
                        top = it.boundingBox.top.toFloat(),
                        right = it.boundingBox.right.toFloat(),
                        bottom = it.boundingBox.bottom.toFloat()
                    )
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Face Sample",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // show face bounding box
        Canvas(modifier = Modifier
            .fillMaxSize()
            .matchParentSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // keep image ratio
            val imageAspectRatio = bitmap.width.toFloat() / bitmap.height
            val canvasAspectRatio = canvasWidth / canvasHeight

            val drawWidth: Float
            val drawHeight: Float
            val offsetX: Float
            val offsetY: Float

            // compare image and canvas ratio, set up offset
            if (canvasAspectRatio > imageAspectRatio) {
                drawHeight = canvasHeight
                drawWidth = canvasHeight * imageAspectRatio
                offsetX = (canvasWidth - drawWidth) / 2
                offsetY = 0f
            } else {
                drawWidth = canvasWidth
                drawHeight = canvasWidth / imageAspectRatio
                offsetX = 0f
                offsetY = (canvasHeight - drawHeight) / 2
            }

            val scaleX = drawWidth / bitmap.width
            val scaleY = drawHeight / bitmap.height

            faceRects.forEach { rect ->
                drawRect(
                    color = Color.Red,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        x = rect.left * scaleX + offsetX,
                        y = rect.top * scaleY + offsetY
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = (rect.right - rect.left) * scaleX,
                        height = (rect.bottom - rect.top) * scaleY
                    ),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                )
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Blue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Face_detectorTheme {
        MainScreen()
    }
}
