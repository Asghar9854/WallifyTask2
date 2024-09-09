package com.example.wallifytask2.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun StorageScreen(modifier: Modifier = Modifier) {
    Column(modifier = Modifier
        .background(Color.Blue)
        .fillMaxSize()) {
        Text(text = "Storage Screen", fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}