package com.example.wallifytask2.ui.api

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.wallifytask2.domain.model.Photo
import com.example.wallifytask2.utils.isNetworkAvailable
import com.example.wallifytask2.utils.pixelApiKey
import com.example.wallifytask2.viewmodel.ApiViewModel


@Composable
fun ApiScreen(
    viewModel: ApiViewModel,
    navController: NavController,
    modifier: Modifier
) {
    val context = LocalContext.current
    if (context.isNetworkAvailable()) {
        val response by viewModel.apiResponse.observeAsState()
        val loading by viewModel.loading
        val error by viewModel.error

        LaunchedEffect(Unit) {
            viewModel.wallpapers(pixelApiKey, "random", 16)
        }
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
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalItemSpacing = 16.dp,
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
            AsyncImage(
                model = photo.src.large,
                contentDescription = "Wallpapers",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}
