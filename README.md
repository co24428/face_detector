# Face Detector Android App

This is a simple Android application that demonstrates **real-time face detection** using the **CameraX API** and **ML Kit's Face Detection model**, with Jetpack Compose UI. The app also supports face detection from a static image as a test mode.

---

## Features

- **Live camera face detection** with bounding boxes
- **Static image face detection** (for preview and testing)
- Uses **ML Kit's on-device face detection** model (fast & lightweight)
- Built entirely using **Jetpack Compose**
- Smooth switching between static image and real-time camera mode
- Handles runtime camera permission requests

---

## Development Environment

| Item                | Version        |
|---------------------|----------------|
| Android Studio      | Meerkat ( 2024.03.01 ) |
| Kotlin              | 2.0.21         |
| Compile SDK         | 35 ( VanillaIceCream ) |
| Gradle              | 8.3 or higher  |

---

## Dependencies

| Library                            | Version     | Purpose                       |
|------------------------------------|-------------|-------------------------------|
| `androidx.camera:camera-core`      | 1.3.0       | CameraX core API              |
| `androidx.camera:camera-camera2`   | 1.3.0       | Camera2 backend               |
| `androidx.camera:camera-lifecycle` | 1.3.0       | Camera lifecycle binding      |
| `androidx.camera:camera-view`      | 1.3.0       | PreviewView for camera output|
| `androidx.lifecycle:lifecycle-runtime-compose` | 2.6.2 | Lifecycle-aware Compose      |
| `com.google.mlkit:face-detection`  | 16.1.7      | ML Kit face detection model   |

---

## How to Run

1. Clone this repository
   ```bash
   git clone https://github.com/your-repo/face-detector-app.git
   ```

2. Open the project in Android Studio

3. Sync Gradle  
   Make sure dependencies download without errors.

4. Connect your physical Android device  
   *(CameraX may not work properly on emulator.)*

5. Build and run the app

6. Grant camera permission  
   The app will request runtime camera permission on first launch.

---

## App Structure

| Screen                 | Description |
|------------------------|-------------|
| `MainScreen`           | Home screen with 2 buttons: image mode & camera mode |
| `ImageScreen`          | Detect faces from a preloaded image and draw bounding boxes |
| `CameraPreviewScreen`  | Real-time face detection with bounding boxes using CameraX and ML Kit |

---

## Preview

> Real-time bounding boxes are drawn on detected faces using Compose `Canvas`.

| Mode         | Description                        |
|--------------|------------------------------------|
| Static Image | Detect face from imported image |
| Real-time    | Live face tracking from back camera |

---

## Improvements

- Pick Image from Albums
- Add face landmarks (eyes, nose, etc.)
- Add support for front camera toggle
- Improve bounding box scaling accuracy for different resolutions
- Save face coordinates/logs for later use

---
