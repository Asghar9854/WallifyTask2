package com.example.wallifytask2.ui.preview

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.wallifytask2.R
import com.example.wallifytask2.dataBase.AppDatabase
import com.example.wallifytask2.dataBase.savePhoto
import com.example.wallifytask2.model.Photo
import com.example.wallifytask2.utils.saveImageToPublicFolder
import com.example.wallifytask2.utils.urlToBitmap
import com.example.wallifytask2.viewmodel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.nabinbhandari.android.permissions.PermissionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
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

        val imagesList = response.photos
        // Ensure the passed index is within bounds
        val startIndex = index.coerceIn(0, imagesList.size - 1)

        val pagerState = rememberPagerState(initialPage = startIndex, pageCount = imagesList.size)

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.shadow(elevation = 5.dp),
                    title = { Text(text = "Preview (${pagerState.currentPage}/${imagesList.size - 1})") },
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
                                        scope.launch { pagerState.animateScrollToPage(prevPageIndex) }
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
                                        scope.launch { pagerState.animateScrollToPage(nextPageIndex) }
                                    }
                                }
                        )

                    }

                    //Bottom buttons
                    Row(
                        modifier = Modifier
                            .weight(0.1f)
                            .horizontalScroll(rememberScrollState())
                            .background(Color.White)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    saveImage(context, imagesList[pagerState.currentPage], database)
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_download),
                                contentDescription = "save Image"
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "Save")
                        }

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    scope.launch {
                                        saveImage(
                                            context,
                                            imagesList[pagerState.currentPage],
                                            database, true
                                        )
                                    }
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_compress),
                                contentDescription = "Compress"
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "Compress")
                        }

                        OutlinedButton(
                            onClick = {
                                scope.launch {

                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_saveall),
                                contentDescription = "save All"
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "Save All", modifier = Modifier)
                        }

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                }
                            },
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
            })
    }

}


private fun saveImage(
    context: Context,
    photo: Photo,
    database: AppDatabase?,
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
                    saveToGallery(context, photo, database, compress)
                }
            })
    } else {
        saveToGallery(context, photo, database, compress)
    }
}

fun saveToGallery(
    context: Context,
    photo: Photo,
    database: AppDatabase?,
    compress: Boolean = false
) {
    urlToBitmap(
        photo.src.original,
        context,
        onSuccess = { bitmap ->
            val newBitmap = if (compress) {
                compressBitmap(bitmap)
            } else bitmap
            val path = context.saveImageToPublicFolder(newBitmap)
            path?.let {
                insertDB(
                    database,
                    savePhoto(imgId = photo.id, name = photo.photographer, imagePath = it)
                )
            }

        },
        onError = {})
}

fun compressBitmap(bitmap: Bitmap): Bitmap {
    val out = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 50, out)
    return BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
}

private fun insertDB(database: AppDatabase?, savePhoto: savePhoto) {
    val photoDao = database?.photoDao()
    CoroutineScope(Dispatchers.IO).launch {
        photoDao?.insertPhoto(savePhoto)
    }
}