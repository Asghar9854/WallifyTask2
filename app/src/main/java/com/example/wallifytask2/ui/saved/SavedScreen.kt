package com.example.wallifytask2.ui.saved

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.wallifytask2.domain.model.ModelImages
import com.example.wallifytask2.ui.storage.isCheckPermission
import com.example.wallifytask2.utils.requestForStoragePermission
import com.example.wallifytask2.viewmodel.StorageViewModel


@Composable
fun SavedScreen(savedViewModel: StorageViewModel, navController: NavController) {
    val context = LocalContext.current

    var isPermissionGranted by remember {
        mutableStateOf(false)
    }
    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LaunchedEffect(Unit) {
            if (isCheckPermission(context)) {
                isPermissionGranted = true
            }
        }
        LaunchedEffect(isPermissionGranted) {
            if (isPermissionGranted) {
                savedViewModel.getSaveImages(context)
            }
        }

        if (isPermissionGranted) {
            val savedPhotos by savedViewModel.saveImages.observeAsState(initial = emptyList())
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp), // External padding for content
                verticalArrangement = Arrangement.spacedBy(16.dp), // Vertical space between rows
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(savedPhotos.size) { index ->
                    SavedPhotoCard(navController, savedPhotos[index], index, "saved")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("We need storage permissions to get Saved Images.")
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
}

@Composable
fun SavedPhotoCard(
    navController: NavController,
    savedPhoto: ModelImages,
    index: Int,
    invokedFrom: String
) {
    val painter =
        rememberAsyncImagePainter(model = savedPhoto.uri) // Make sure to provide a valid image path
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            navController.navigate("storagePreview/$index/$invokedFrom")
        }
    ) {
        Column {
            Image(
                painter = painter,
                contentDescription = "Saved Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}
