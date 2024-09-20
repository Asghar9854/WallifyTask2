package com.example.wallifytask2.ui.storage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.wallifytask2.ui.saved.SavedPhotoCard
import com.example.wallifytask2.utils.requestForStoragePermission
import com.example.wallifytask2.viewmodel.StorageViewModel


@Composable
fun StorageScreen(savedViewModel: StorageViewModel, navController: NavController) {

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
                savedViewModel.getAllStorageImages(context)
            }
        }

        if (isPermissionGranted) {
            val allImages by savedViewModel.allImagesList.observeAsState(initial = emptyList())
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allImages.size) { index ->
                    SavedPhotoCard(navController, allImages[index], index, "storage")
                }

            }
        }
        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("We need storage permissions to get All Images.")
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


fun isCheckPermission(context: Context) = ContextCompat.checkSelfPermission(
    context,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_IMAGES
    else Manifest.permission.READ_EXTERNAL_STORAGE
) == PackageManager.PERMISSION_GRANTED




