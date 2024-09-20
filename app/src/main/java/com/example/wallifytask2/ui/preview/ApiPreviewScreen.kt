package com.example.wallifytask2.ui.preview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.wallifytask2.R
import com.example.wallifytask2.model.Photo
import com.example.wallifytask2.utils.MediaScanner
import com.example.wallifytask2.utils.createDestinationFile
import com.example.wallifytask2.utils.getBitmapFromUri
import com.example.wallifytask2.utils.saveImageToPublicFolder
import com.example.wallifytask2.utils.urlToBitmap
import com.example.wallifytask2.viewmodel.ApiViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.nabinbhandari.android.permissions.PermissionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun ApiPreviewScreen(index: Int, navController: NavHostController, viewModel: ApiViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val response by viewModel.apiResponse.observeAsState()

    val isSavingImages = remember { mutableStateOf(false) }
    val savingProgress = remember { mutableStateOf(0f) }
    val saveJob = remember { mutableStateOf<Job?>(null) }


    response?.let { response ->

        val imagesList = response.photos
        val startIndex = index.coerceIn(0, imagesList.size)
        val pagerState = rememberPagerState(initialPage = startIndex, pageCount = imagesList.size)
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.shadow(elevation = 5.dp),
                    title = { Text(text = "Preview (${pagerState.currentPage + 1}/${imagesList.size})") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() })
                        {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }, colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White)
                )
            },
            content = { padding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(0.9f)
                                .padding(8.dp)
                        ) {
                            //button back
                            Image(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "previous",
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier
                                    .weight(0.1f)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                                    .padding(5.dp)
                                    .align(Alignment.CenterVertically)
                                    .clickable {
                                        if (pagerState.currentPage > 0) {
                                            val prevPageIndex = pagerState.currentPage - 1
                                            scope.launch {
                                                pagerState.animateScrollToPage(
                                                    prevPageIndex
                                                )
                                            }
                                        }
                                    })

                            HorizontalPager(
                                state = pagerState, modifier = Modifier
                                    .weight(0.8f)
                                    .fillMaxHeight()
                                    .padding(horizontal = 5.dp)
                            ) { page ->
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(context)
                                            .data(imagesList[page].src.original)
                                            .crossfade(false)
                                            .build()
                                    ),
                                    modifier = Modifier
                                        .fillMaxHeight(),
                                    contentDescription = "Full Screen Wallpaper",
                                    contentScale = ContentScale.FillBounds
                                )
                            }


                            //button Next
                            Image(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "next",
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier
                                    .weight(0.1f)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                                    .padding(5.dp)
                                    .align(Alignment.CenterVertically)
                                    .clickable {
                                        if (pagerState.currentPage < pagerState.pageCount - 1) {
                                            val nextPageIndex = pagerState.currentPage + 1
                                            scope.launch {
                                                pagerState.animateScrollToPage(
                                                    nextPageIndex
                                                )
                                            }
                                        }
                                    }
                            )

                        }


                        Column(
                            modifier = Modifier
                                .weight(0.2f)
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .background(Color.White),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(0.1f)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    modifier = Modifier
                                        .weight(1f),
                                    onClick = {
                                        scope.launch {
                                            isSavingImages.value = true
                                            savingProgress.value = 0f
                                            withContext(Dispatchers.IO) {
                                                saveImage(
                                                    context,
                                                    imagesList[pagerState.currentPage],
                                                    100
                                                ) { imageProgress ->
                                                    savingProgress.value =
                                                        (index + imageProgress)
                                                }
                                                withContext(Dispatchers.Main) {
                                                    isSavingImages.value = false
                                                }
                                            }
                                        }

//                                        scope.launch {
//                                            withContext(Dispatchers.IO) {
//                                                saveImage(
//                                                    context,
//                                                    imagesList[pagerState.currentPage]
//                                                )
//                                            }
//                                        }
                                    }
                                )
                                {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_download),
                                        contentDescription = "save Image"
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(text = "Save")
                                }

                                //Compress Image
                                OutlinedButton(
                                    modifier = Modifier
                                        .weight(1f),
                                    onClick = {
                                        scope.launch {
                                            isSavingImages.value = true
                                            savingProgress.value = 0f // Reset progress
                                            withContext(Dispatchers.IO) {
                                                saveImage(
                                                    context,
                                                    imagesList[pagerState.currentPage],
                                                    50
                                                ) { imageProgress ->
                                                    savingProgress.value =
                                                        (index + imageProgress)
                                                }
                                                withContext(Dispatchers.Main) {
                                                    isSavingImages.value = false
                                                }
                                            }
                                        }

//                                            scope.launch {
//                                                saveImage(
//                                                    context = context,
//                                                    photo = imagesList[pagerState.currentPage],
//                                                    compress = true
//                                                )
//                                            }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_compress),
                                        contentDescription = "Compress"
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(text = "Compress")
                                }
                            }


                            Row(
                                modifier = Modifier
                                    .weight(0.1f)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                //Save All Images
                                OutlinedButton(
                                    modifier = Modifier
                                        .weight(1f),
                                    onClick = {
                                        saveJob.value = scope.launch {
                                            isSavingImages.value = true
                                            savingProgress.value = 0f // Reset progress

                                            withContext(Dispatchers.IO) {
                                                val totalImages = imagesList.size

                                                for (photo in imagesList) {

                                                    if (saveJob.value?.isCancelled == true) {
                                                        break
                                                    }
                                                    saveImage(
                                                        context,
                                                        photo,
                                                        100
                                                    ) { imageProgress ->
                                                        savingProgress.value =
                                                            (index + imageProgress) / totalImages
                                                    }
                                                }
                                                withContext(Dispatchers.Main) {
                                                    isSavingImages.value = false
                                                }
                                            }
                                        }

//                                        scope.launch {
//                                            isLoading.value = true
//                                            withContext(Dispatchers.IO) {
//                                                imagesList.forEach { photo ->
//                                                    saveImage(context, photo, compress = false)
//                                                }
//                                                withContext(Dispatchers.Main) {
//                                                    //isLoading.value = false
//                                                }
//                                            }
//                                        }
                                    },
                                    enabled = !isSavingImages.value
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_saveall),
                                        contentDescription = "Save All"
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(text = "Save All", modifier = Modifier)
                                }

                                //Compress All Images
                                OutlinedButton(
                                    modifier = Modifier
                                        .weight(1f),
                                    onClick = {

                                        saveJob.value = scope.launch {
                                            isSavingImages.value = true
                                            savingProgress.value = 0f // Reset progress

                                            withContext(Dispatchers.IO) {
                                                val totalImages = imagesList.size

                                                for (photo in imagesList) {

                                                    if (saveJob.value?.isCancelled == true) {
                                                        break
                                                    }
                                                    saveImage(
                                                        context,
                                                        photo,
                                                        50
                                                    ) { imageProgress ->
                                                        savingProgress.value =
                                                            (index + imageProgress) / totalImages
                                                    }
                                                }
                                                withContext(Dispatchers.Main) {
                                                    isSavingImages.value = false
                                                }
                                            }
                                        }

//                                        scope.launch {
//                                            withContext(Dispatchers.IO) {
//                                                imagesList.forEach { photo ->
//                                                    saveImage(context, photo, compress = true)
//                                                }
//                                            }
//                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_compress),
                                        contentDescription = "Compress All"
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(text = "Compress All")
                                }
                            }
                        }
                    }
                    ProgressBar(isDisplay = isSavingImages.value)
                }
            })
    }

    if (isSavingImages.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 2.dp,
                        Color.Red,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Saving Images",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary,
                    progress = savingProgress.value
                )
                Text(text = "${(savingProgress.value * 100).toInt()}%")

                OutlinedButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        saveJob.value?.cancel() // Cancel the saving process
                        isSavingImages.value = false
                        savingProgress.value = 0f
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        }

    }

}

@Composable
fun ProgressBar(isDisplay: Boolean) {
    if (isDisplay) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA000000)), // Semi-transparent background
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


private fun saveImage(
    context: Context,
    photo: Photo,
    compress: Boolean = false
) {
    val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        com.nabinbhandari.android.permissions.Permissions.check(
            context,
            permissions,
            null,
            null,
            object : PermissionHandler() {
                override fun onGranted() {
                    saveToGallery(context, photo, compress)
                }
            })
    } else {
        saveToGallery(context, photo, compress)
    }
}


fun saveImage(
    context: Context,
    photo: Photo,
    quality: Int = 100,
    onProgressUpdate: (Float) -> Unit = {}
) {
    val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        com.nabinbhandari.android.permissions.Permissions.check(
            context,
            permissions,
            null,
            null,
            object : PermissionHandler() {
                override fun onGranted() {
                    urlToBitmap(
                        photo.src.original,
                        context,
                        onSuccess = { bitmap ->
                            bitmap.let {
                                val destinationFile = createDestinationFile()
                                FileOutputStream(destinationFile).use { outputStream ->
                                    val isSaved = it.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                                    MediaScanner(context, destinationFile)
                                    if (isSaved) {
                                        onProgressUpdate(1f)
                                    }
                                }
                            }
                        },
                        onError = {})
                }
            })
    } else {
        urlToBitmap(
            photo.src.original,
            context,
            onSuccess = { bitmap ->
                bitmap.let {
                    val destinationFile = createDestinationFile()
                    FileOutputStream(destinationFile).use { outputStream ->
                        val isSaved = it.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                        MediaScanner(context, destinationFile)

                        if (isSaved) {
                            onProgressUpdate(1f)
                        }
                    }
                }
            },
            onError = {})
    }
}



fun saveToGallery(
    context: Context,
    photo: Photo,
    compress: Boolean = false
) {
    urlToBitmap(
        photo.src.original,
        context,
        onSuccess = { bitmap ->
            val newBitmap = if (compress) {
                compressBitmap(bitmap)
            } else {
                bitmap
            }
            val path = context.saveImageToPublicFolder(newBitmap)
        },
        onError = {})
}

fun compressBitmap(bitmap: Bitmap): Bitmap {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
    val byteArray = baos.toByteArray()
    val inputStream = ByteArrayInputStream(byteArray)
    return BitmapFactory.decodeStream(inputStream, null, null)!!
}