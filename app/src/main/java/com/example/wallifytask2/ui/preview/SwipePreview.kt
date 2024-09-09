package com.example.wallifytask2.ui.preview

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.wallifytask2.R
import com.example.wallifytask2.dataBase.AppDatabase
import com.example.wallifytask2.dataBase.savePhoto
import com.example.wallifytask2.model.Photo
import com.example.wallifytask2.utils.saveImageToCacheStorage
import com.example.wallifytask2.utils.showToast
import com.example.wallifytask2.utils.urlToBitmap
import com.example.wallifytask2.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipePreview(index: Int, navController: NavHostController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDataBase(context) }

    val loading = viewModel.loading.value
//    LaunchedEffect(Unit) {
//        viewModel.wallpapers(apiKey, query, perPage)
//    }

    // Observe LiveData using observeAsState
    val response by viewModel.apiResponse.observeAsState()
    response?.let { response ->
        val list = response.photos
        val photo = list.get(index)
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green)
                        .padding(padding)
                ) {
                    Row(modifier = Modifier.weight(1f)) {

                        //button back
                        Image(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "previous",
                            modifier = Modifier
                                .weight(0.1f)
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    context.showToast("previous")
                                })
                        // Preview Image
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(photo.src.original)
                                    .crossfade(true)
                                    .build()
                            ),
                            contentDescription = "Full Screen Wallpaper",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.weight(0.8f)
                        )

                        //button Next
                        Image(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "next",
                            modifier = Modifier
                                .weight(0.1f)
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    context.showToast("next")
                                }
                        )
                    }



                    Row(
                        modifier = Modifier
                            .weight(0.15f)
                            .horizontalScroll(rememberScrollState())
                            .background(Color.White)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    saveImage(context, photo, database)
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_save),
                                contentDescription = "save Image"
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "Save")
                        }
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    saveImage(context, photo, database)
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_save),
                                contentDescription = "save Image"
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "Compress")
                        }
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    saveImage(context, photo, database)
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_save),
                                contentDescription = "save Image"
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "Save All", modifier = Modifier)
                        }
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    saveImage(context, photo, database)
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_save),
                                contentDescription = "save Image"
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "Compress All")
                        }
                    }
                }
            })
    }

}

private fun saveImage(context: Context, photo: Photo, database: AppDatabase?) {
    urlToBitmap(
        photo.src.original,
        context,
        onSuccess = { bitmap ->
            val path = context.saveImageToCacheStorage(bitmap)
            context.showToast("Image Saved Successfully")
            insertDB(database, savePhoto(photo.id, photo.photographer, path))
        },
        onError = {})
}

private fun insertDB(database: AppDatabase?, savePhoto: savePhoto) {
    val photoDao = database?.photoDao()
    CoroutineScope(Dispatchers.IO).launch {
        photoDao?.insertPhoto(savePhoto)
    }
}