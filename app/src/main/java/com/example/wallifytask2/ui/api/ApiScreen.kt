package com.example.wallifytask2.ui.api

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.wallifytask2.model.Photo
import com.example.wallifytask2.utils.isNetworkAvailable
import com.example.wallifytask2.utils.pixelApiKey
import com.example.wallifytask2.viewmodel.ApiViewModel


@Composable
fun ApiScreen(
    viewModel: ApiViewModel,
    navController: NavController,
    modifier: Modifier
) {
    val apiKey = pixelApiKey
    val perPage = 16
    val query = "Landscapes"
    val context = LocalContext.current
    if (context.isNetworkAvailable()) {
        val response by viewModel.apiResponse.observeAsState()
        val loading by viewModel.loading
        val error by viewModel.error


        Box(modifier = modifier.fillMaxSize()) {
            when {
                loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                error != null -> {
                    Text(text = error ?: "Unknown error")
                }

                response != null -> {
                    response?.let {
                        SetDataOnRecyclerview(
                            photoList = it.photos,
                            navController = navController
                        )
                    }
                }
            }

        }

    } else {
        androidx.compose.material3.Text("No network available")
    }
}

@Composable
fun SetDataOnRecyclerview(photoList: List<Photo>, navController: NavController) {
    val imageList = mutableListOf(photoList)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        imageList.forEach {
            itemsIndexed(it) { index, item ->
                SingleItemMain(item, navController, index)
            }
        }
    }
}

@Composable
fun SingleItemMain(photo: Photo, navController: NavController, index: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(10.dp)),
        onClick = {
            Log.d("TAG", "SingleItemMain: $index")
            navController.navigate("ApiPreview/$index")
        }
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photo.src.large)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Wallpapers",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}
