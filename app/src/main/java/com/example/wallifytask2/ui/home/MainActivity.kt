package com.example.wallifytask2.ui.home

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wallifytask2.ui.preview.ApiPreviewScreen
import com.example.wallifytask2.ui.preview.StoragePreviewScreen
import com.example.wallifytask2.utils.DirectoryFileObserver
import com.example.wallifytask2.utils.SAVEDDIRECTORY
import com.example.wallifytask2.utils.pixelApiKey
import com.example.wallifytask2.utils.showToast
import com.example.wallifytask2.viewmodel.ApiViewModel
import com.example.wallifytask2.viewmodel.StorageViewModel
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject
import java.io.File


class MainActivity : ComponentActivity() {

    private val viewModel: ApiViewModel by inject()
    private val imagesViewModel: StorageViewModel by inject()
    private var directoryFileObserver: DirectoryFileObserver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val destinationFile = File(directory, SAVEDDIRECTORY)
        val aboslutePath: String = destinationFile.absolutePath
        directoryFileObserver = DirectoryFileObserver(aboslutePath)
        directoryFileObserver?.startWatching()

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            AppHost(
                navController,
                viewModel,
                imagesViewModel
            )
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }
}

@Composable
fun AppHost(
    navController: NavHostController,
    viewModel: ApiViewModel,
    imagesViewModel: StorageViewModel
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController, viewModel, imagesViewModel = imagesViewModel)
        }
        composable(
            route = "ApiPreview/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index")
            if (index != null) {
                ApiPreviewScreen(index = index, navController, viewModel)
            }
        }

        composable(
            route = "storagePreview/{index}/{invokeFrom}",
            arguments = listOf(
                navArgument("index") { type = NavType.IntType },
                navArgument("invokeFrom") { type = NavType.StringType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index")
            val invokedFrom = backStackEntry.arguments?.getString("invokeFrom")

            if (index != null) {
                StoragePreviewScreen(index = index, navController, imagesViewModel, invokedFrom)
            }
        }
    }
}




