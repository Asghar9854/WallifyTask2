package com.example.wallifytask2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wallifytask2.model.Photo
import com.example.wallifytask2.ui.home.HomeScreen
import com.example.wallifytask2.ui.preview.PreviewScreen
import com.example.wallifytask2.ui.preview.SwipePreview
import com.example.wallifytask2.utils.showToast
import com.example.wallifytask2.viewmodel.ImagesVM
import com.example.wallifytask2.viewmodel.MainViewModel
import com.example.wallifytask2.viewmodel.SavedViewModel
import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels<MainViewModel>()
    private val savedViewModel: SavedViewModel by viewModels<SavedViewModel>()
    private val imagesViewModel: ImagesVM by viewModels<ImagesVM>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            AppHost(
                navController,
                viewModel,
                savedViewModel,
                imagesViewModel
            )
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showToast("backPressedCallback")
        }

    }
}

@Composable
fun AppHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    savedViewModel: SavedViewModel,
    imagesViewModel: ImagesVM
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController, viewModel, savedViewModel, imagesViewModel = imagesViewModel)
        }
        composable(
            route = "fullScreen/{photo}",
            arguments = listOf(navArgument("photo") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedJsonPhoto = backStackEntry.arguments?.getString("photo")
            encodedJsonPhoto?.let {
                val decodedJsonPhoto = URLDecoder.decode(
                    it,
                    StandardCharsets.UTF_8.toString()
                ) // Decode it
                val photo = Gson().fromJson(
                    decodedJsonPhoto,
                    Photo::class.java
                ) // Deserialize back to Photo
                PreviewScreen(photo = photo, navController = navController)
            }
        }
        composable(
            route = "swipPreview/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index")
            if (index != null) {
                SwipePreview(index = index, navController, viewModel)
            }
        }

    }
}