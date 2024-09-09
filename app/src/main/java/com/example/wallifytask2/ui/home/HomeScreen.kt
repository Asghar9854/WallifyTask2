@file:OptIn(ExperimentalPagerApi::class)

package com.example.wallifytask2.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.wallifytask2.R
import com.example.wallifytask2.viewmodel.MainViewModel
import com.example.wallifytask2.viewmodel.SavedViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    savedViewModel: SavedViewModel
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = HomeTabs.entries.size)
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
    Column(
        modifier = Modifier
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
                    FetchApiData(viewModel, navController, modifier = Modifier)
                }

                1 -> {
                    StorageScreen()
                }

                2 -> {
                    CameraScreen()
                }

                3 -> {
                    SavedScreen(
                        savedViewModel = savedViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = com.example.wallifytask2.R.string.name),
                color = Color.Black
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = colorResource(id = com.example.wallifytask2.R.color.toolbarcolor))
    )
}


enum class HomeTabs(
    val icon: Int,
    val text: String
) {
    Online(
        icon = R.drawable.ic_api,
        text = "Api"
    ),
    Offline(
        icon = R.drawable.ic_storage,
        text = "Storage"
    ),
    Camera(
        icon = R.drawable.ic_camera,
        text = "Camera"
    ),
    Saved(
        icon = R.drawable.ic_save,
        text = "Save"
    )
}
