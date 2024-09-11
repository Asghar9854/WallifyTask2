package com.example.wallifytask2.model

import java.io.Serializable

class ModelImages(
    val id: Long,
    val name: String,
    val size: String,
    val date: String,
    val uri: String,
    val isEdited: Boolean
) : Serializable
