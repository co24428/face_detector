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
import androidx.compose.ui.unit.dp
import com.example.face_detector.ui.theme.Face_detectorTheme
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.common.InputImage

import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.camera.core.Preview
import androidx.core.content.ContextCompat
import android.util.Log

import android.app.Activity
import android.content.pm.PackageManager
import android.Manifest
import androidx.core.app.ActivityCompat

import androidx.camera.core.*
import androidx.compose.runtime.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.ArrowBack


// Get camera permission when open the application
private const val CAMERA_PERMISSION_REQUEST_CODE = 100
private fun checkAndRequestCameraPermission(activity: Activity) {
    if (ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
}

// Main Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestCameraPermission(this)

        enableEdgeToEdge()
        setContent {
            Face_detectorTheme {
                MainScreen()
            }
        }
    }
}

// Main Screen, when open the application
// Two button on this screen
@Composable
fun MainScreen() {
    var screenState by remember { mutableStateOf("home") }

    // Check screenState, and activate each screen, image and camera preview
    when (screenState) {
        "image" -> ImageScreen { screenState = "home" }
        "camera" -> CameraPreviewScreen { screenState = "home" }
        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = { screenState = "image" }) {
                        Text("Detect Face")
                    }

                    Button(onClick = { screenState = "camera" }) {
                        Text("Real Time Camera")
                    }
                }
            }
        }
    }
}

// CameraPreview Screen
// Get image from camera
// Detect face using included model in Android ML Kit
// Draw bounding box from result of the Detection model
@Composable
fun CameraPreviewScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val faceRects = remember { mutableStateListOf<Rect>() }

    Box(modifier = Modifier.fillMaxSize()) {

        // Camera preview using AndroidView
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                // ML Kit face detector
                val faceDetector = FaceDetection.getClient(
                    FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .build()
                )

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(
                                    ContextCompat.getMainExecutor(ctx)
                                ) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = InputImage.fromMediaImage(
                                            mediaImage,
                                            imageProxy.imageInfo.rotationDegrees
                                        )
                                        faceDetector.process(image)
                                            .addOnSuccessListener { faces ->
                                                faceRects.clear()
                                                faceRects.addAll(
                                                    faces.map { face ->
                                                        Rect(
                                                            face.boundingBox.left.toFloat(),
                                                            face.boundingBox.top.toFloat(),
                                                            face.boundingBox.right.toFloat(),
                                                            face.boundingBox.bottom.toFloat()
                                                        )
                                                    }
                                                )
                                            }
                                            .addOnFailureListener {
                                                it.printStackTrace()
                                            }
                                            .addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        imageProxy.close()
                                    }
                                }
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalyzer
                        )
                    } catch (e: Exception) {
                        Log.e("CameraX", "Camera initialization failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Draw bounding Box to every detected face ( object )
        Canvas(modifier = Modifier
            .fillMaxSize()
            .matchParentSize()
        ) {
            // Set Camera frame ( 480x640 or 720x1280, base setting in ML Kit )
            val frameWidth = 480f
            val frameHeight = 640f

            val scaleX = size.width / frameWidth
            val scaleY = size.height / frameHeight

            faceRects.forEach { rect ->
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(
                        x = rect.left * scaleX,
                        y = rect.top * scaleY
                    ),
                    size = Size(
                        width = (rect.right - rect.left) * scaleX,
                        height = (rect.bottom - rect.top) * scaleY
                    ),
                    style = Stroke(width = 4f)
                )
            }
        }

        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}

// Image Screen
// Detect face from pre imported image for test function
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

//@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Face_detectorTheme {
        MainScreen()
    }
}
