package com.example.wallifytask2.ui.preview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.compose.runtime.MutableState
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
import com.example.wallifytask2.utils.MediaScanner
import com.example.wallifytask2.utils.createDestinationFile
import com.example.wallifytask2.utils.getBitmapFromUri
import com.example.wallifytask2.viewmodel.StorageViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun StoragePreviewScreen(
    index: Int,
    navController: NavHostController,
    viewModel: StorageViewModel,
    invokedFrom: String?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isSavingImages = remember { mutableStateOf(false) }
    val savingProgress = remember { mutableStateOf(0f) }
    val saveJob = remember { mutableStateOf<Job?>(null) } // Job for the saving process


    val imagesList by if (invokedFrom == "storage") {
        viewModel.allImagesList.observeAsState()
    } else {
        viewModel.saveImages.observeAsState()
    }
    imagesList?.let { list ->
        val startIndex = index.coerceIn(0, list.size - 1)
        val pagerState = rememberPagerState(initialPage = startIndex, pageCount = list.size)
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.shadow(elevation = 5.dp),
                    title = { Text(text = "Preview (${pagerState.currentPage + 1}/${list.size})") },
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
                                .weight(0.8f)
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
                                            .data(list[page].uri)
                                            .crossfade(false)
                                            .build()
                                    ),
                                    modifier = Modifier
                                        .fillMaxHeight(),
                                    contentDescription = "Full Screen Wallpaper"
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


                        //Save Image
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
                                    onClick = { scope.launch {
                                            isSavingImages.value = true
                                            savingProgress.value = 0f
                                            withContext(Dispatchers.IO) {
                                                saveImageToGallery(
                                                    context,
                                                    list[pagerState.currentPage].uri.toUri(),
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
                                    }, enabled = !isSavingImages.value
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
                                    onClick = { scope.launch {
                                            isSavingImages.value = true
                                            savingProgress.value = 0f
                                            withContext(Dispatchers.IO) {
                                                saveImageToGallery(
                                                    context,
                                                    list[pagerState.currentPage].uri.toUri(),
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
                                    }, enabled = !isSavingImages.value
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
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        saveJob.value = scope.launch {
                                            isSavingImages.value = true
                                            savingProgress.value = 0f // Reset progress

                                            withContext(Dispatchers.IO) {
                                                val totalImages = list.size

                                                for (photo in list) {

                                                    if (saveJob.value?.isCancelled == true) {
                                                        break
                                                    }

                                                    saveImageToGallery(
                                                        context,
                                                        photo.uri.toUri(),
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
                                                val totalImages = list.size

                                                for (photo in list) {
                                                    if (saveJob.value?.isCancelled == true) {
                                                        break
                                                    }
                                                    saveImageToGallery(
                                                        context,
                                                        photo.uri.toUri(),
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
                                    }, enabled = !isSavingImages.value
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
                }
            }
        )
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

fun saveImageToGallery(
    context: Context,
    uri: Uri,
    quality: Int = 100,
    onProgressUpdate: (Float) -> Unit = {}
) {
    val bitmap = getBitmapFromUri(context, uri)
    bitmap?.let {
        val destinationFile = createDestinationFile()
        FileOutputStream(destinationFile).use { outputStream ->
            val isSaved = it.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            MediaScanner(context, destinationFile)

            if (isSaved) {
                onProgressUpdate(1f)
            }
        }
    }
}
