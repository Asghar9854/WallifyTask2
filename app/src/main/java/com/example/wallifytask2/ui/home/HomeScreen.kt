@file:OptIn(ExperimentalPagerApi::class)

package com.example.wallifytask2.ui.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.wallifytask2.ui.api.ApiScreen
import com.example.wallifytask2.ui.camera.CameraScreen
import com.example.wallifytask2.ui.saved.SavedScreen
import com.example.wallifytask2.ui.storage.StorageScreen
import com.example.wallifytask2.utils.HomeTabs
import com.example.wallifytask2.viewmodel.StorageViewModel
import com.example.wallifytask2.viewmodel.ApiViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: ApiViewModel,
    imagesViewModel: StorageViewModel
) {
    val context = LocalContext.current
    val activity = (context as? Activity)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = HomeTabs.entries.size)
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                HomeTabs.entries.forEachIndexed { index, currentTabs ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.outline,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(currentTabs.ordinal)
                            }
                        },
                        text = { Text(text = currentTabs.text) },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = currentTabs.icon
                                ), contentDescription = "Tab Icon"
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState, modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { index ->
                when (index) {
                    0 -> {
                        ApiScreen(viewModel, navController, modifier = Modifier)
                    }

                    1 -> {
                        StorageScreen(
                            savedViewModel = imagesViewModel,
                            navController = navController
                        )
                    }

                    2 -> {
                        CameraScreen()
                    }

                    3 -> {
                        SavedScreen(
                            savedViewModel = imagesViewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    BackHandler(true) {
        if (pagerState.currentPage != 0) {
            scope.launch {
                pagerState.animateScrollToPage(0)
            }
        } else {
            activity?.finish()
        }
    }

}


