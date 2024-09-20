package com.example.wallifytask2.ui.camera

import android.Manifest
import android.net.Uri
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.example.wallifytask2.ui.storage.isCheckPermission
import com.example.wallifytask2.utils.CameraUtils.takePicture
import com.example.wallifytask2.utils.requestForStoragePermission
import java.util.concurrent.Executors

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var isPermissionGranted by remember {
        mutableStateOf(false)
    }
    val permissionsToRequest = arrayOf(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (isCheckPermission(context)) {
            isPermissionGranted = true
        }
    }

    if (isPermissionGranted) {
        CameraView()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .windowInsetsPadding(WindowInsets.navigationBars),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("We need Camera permissions to Capture Images.")
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                context.requestForStoragePermission(permissionsToRequest) { isGranted ->
                    isPermissionGranted = isGranted
                }
            }) {
                Text("Request Permissions")
            }
        }
    }
}

@Composable
fun CameraView() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    val capturedImageUri = remember { mutableStateOf<Uri?>(null) }
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // PreviewView for the camera feed. Configured to fill the screen and display the camera output
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_START
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    controller = cameraController
                }
            },
            onRelease = {
                cameraController.unbind()
            }
        )
        Box(
            modifier = Modifier
                .padding(bottom = 30.dp)
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
                    .clickable(onClick = {
                        takePicture(cameraController, context, executor, { uri ->
                            capturedImageUri.value = uri
                        }, { exception ->
                        })
                    })
            )
        }

        capturedImageUri.value?.let { uri ->
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomStart)
            ) {
                Card(
                    modifier = Modifier
                        .border(1.dp, Color.White)
                        .width(80.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Saved Image",
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

    }
}