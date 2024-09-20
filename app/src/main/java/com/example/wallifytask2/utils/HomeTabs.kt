package com.example.wallifytask2.utils

import com.example.wallifytask2.R

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