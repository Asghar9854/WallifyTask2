package com.example.wallifytask2.ui.home

import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.wallifytask2.dataBase.savePhoto
import com.example.wallifytask2.model.ModelImages
import com.example.wallifytask2.viewmodel.ImagesVM
import com.example.wallifytask2.viewmodel.SavedModel
import com.example.wallifytask2.viewmodel.SavedViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(savedViewModel: ImagesVM, navController: NavController) {
    val context = LocalContext.current
    // val savedPhotos by savedViewModel.getSavedPhotos(context).observeAsState(emptyList())
    LaunchedEffect(Unit) {
        savedViewModel.getSaveImages(context)
    }
    val savedPhotos by savedViewModel.cropDirectory.observeAsState(initial = emptyList())
    Log.d("TAG", "SavedScreen: ${savedPhotos.size}")


    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp), // External padding for content
            verticalArrangement = Arrangement.spacedBy(16.dp), // Vertical space between rows
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(savedPhotos) { savedPhoto ->
                SavedPhotoCard(navController, savedPhoto)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPhotoCard(navController: NavController, savedPhoto: ModelImages) {
    // Convert the Photo object to a JSON string
    val photoJson = Gson().toJson(savedPhoto)
    // URL encode the JSON string to handle special characters
    val encodedPhotoJson = URLEncoder.encode(photoJson, StandardCharsets.UTF_8.toString())
    val context = LocalContext.current
    val painter =
        rememberAsyncImagePainter(model = savedPhoto.uri) // Make sure to provide a valid image path

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            //  navController.navigate("editScreen/$encodedPhotoJson")
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
