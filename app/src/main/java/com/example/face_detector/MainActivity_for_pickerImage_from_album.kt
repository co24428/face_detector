//package com.example.face_detector
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.core.net.toUri
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.*
//import coil.compose.rememberAsyncImagePainter
//import com.example.face_detector.ui.theme.Face_detectorTheme
//import androidx.activity.compose.setContent
//
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            Face_detectorTheme {
//                AppNavigator()
//            }
//        }
//    }
//}
//
//@Composable
//fun AppNavigator() {
//    val navController = rememberNavController()
//    NavHost(navController = navController, startDestination = "main") {
//        composable("main") {
//            ImagePickerScreen(navController)
//        }
//        composable("displayImage/{imageUri}") { backStackEntry ->
//            val uriString = backStackEntry.arguments?.getString("imageUri") ?: ""
//            DisplayImageScreen(imageUri = uriString)
//        }
//    }
//}
//
//@Composable
//fun ImagePickerScreen(navController: NavHostController) {
//    val context = LocalContext.current
//    val imageUri = remember { mutableStateOf<Uri?>(null) }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            imageUri.value = it
//            navController.navigate("displayImage/${Uri.encode(it.toString())}")
//        }
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Button(onClick = {
//            launcher.launch("image/*")
//        }) {
//            Text("Button")
//        }
//    }
//}
//
//@Composable
//fun DisplayImageScreen(imageUri: String) {
//    val decodedUri = Uri.decode(imageUri)
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Image(
//            painter = rememberAsyncImagePainter(decodedUri),
//            contentDescription = "Selected Image",
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    Face_detectorTheme {
//        // Preview 생략: Navigation은 미리보기 안 됨
//    }
//}
