package com.example.wallifytask2.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.wallifytask2.model.Photo
import com.example.wallifytask2.utils.CircularIndeterminateProgressBar
import com.example.wallifytask2.utils.isNetworkAvailable
import com.example.wallifytask2.utils.pixelApiKey
import com.example.wallifytask2.viewmodel.MainViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun ApiScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .background(Color.Red)
            .fillMaxSize()
    ) {
        Text(text = "Api Screen", fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}

@Composable
fun FetchApiData(
    viewModel: MainViewModel,
    navController: NavController,
    modifier: Modifier
) {
    val apiKey = pixelApiKey
    val perPage = 16
    val query = "nature"
    val context = LocalContext.current

    if (context.isNetworkAvailable()) {
        val loading = viewModel.loading.value
        LaunchedEffect(Unit) {
            viewModel.wallpapers(apiKey, query, perPage)
        }

        // Observe LiveData using observeAsState
        val response by viewModel.apiResponse.observeAsState()

        Box(modifier = modifier.fillMaxSize()) {
            // If the response is not null, show wallpapers
            response?.let {
                SetDataOnRecyclerview(
                    photoList = it.photos,
                    navController = navController
                ) // Pass the necessary data to your WallpaperCard composable
            }
            CircularIndeterminateProgressBar(isDisplay = loading)
        }

    } else {
        // Handle no network case
        androidx.compose.material3.Text("No network available")
    }

}


@Composable
fun SetDataOnRecyclerview(photoList: List<Photo>, navController: NavController) {
    val imageList = mutableListOf(photoList)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp), // External padding for content
        verticalArrangement = Arrangement.spacedBy(16.dp), // Vertical space between rows
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
    // Convert the Photo object to a JSON string
    val photoJson = Gson().toJson(photo)
    // URL encode the JSON string to handle special characters
    val encodedPhotoJson = URLEncoder.encode(photoJson, StandardCharsets.UTF_8.toString())

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(10.dp)),
        onClick = {
            //navController.navigate("fullScreen/$encodedPhotoJson")
            Log.d("TAG", "SingleItemMain: $index")
            navController.navigate("swipPreview/$index")
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
