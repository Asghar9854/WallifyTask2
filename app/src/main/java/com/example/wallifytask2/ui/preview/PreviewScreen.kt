package com.example.wallifytask2.ui.preview

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.wallifytask2.dataBase.AppDatabase
import com.example.wallifytask2.dataBase.savePhoto
import com.example.wallifytask2.model.Photo
import com.example.wallifytask2.utils.saveImageToCacheStorage
import com.example.wallifytask2.utils.showToast
import com.example.wallifytask2.utils.urlToBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(photo: Photo, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDataBase(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Preview") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() })
                    {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }, colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White)
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(photo.src.original)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = "Full Screen Wallpaper",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Button to save the image (you can implement actual saving logic later)
                Button(
                    onClick = {
                        scope.launch {
                            saveImage(context, photo, database)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(60.dp)
                        .width(200.dp)
                        .padding(bottom = 16.dp),
                ) {
                    Text(text = "Save Image")
                }
            }
        })
}

private fun saveImage(context: Context, photo: Photo, database: AppDatabase?) {
    urlToBitmap(
        photo.src.original,
        context,
        onSuccess = { bitmap ->
            val path = context.saveImageToCacheStorage(bitmap)
            context.showToast("Image Saved Successfully")
            insertDB(
                database,
                savePhoto(imgId = photo.id, name = photo.photographer, imagePath = path)
            )
        },
        onError = {})
}

private fun insertDB(database: AppDatabase?, savePhoto: savePhoto) {
    val photoDao = database?.photoDao()
    CoroutineScope(Dispatchers.IO).launch {
        photoDao?.insertPhoto(savePhoto)
    }
}