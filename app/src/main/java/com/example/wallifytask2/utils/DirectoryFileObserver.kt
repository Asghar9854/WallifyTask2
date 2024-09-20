package com.example.wallifytask2.utils

import android.os.Environment
import android.os.FileObserver
import android.util.Log
import java.io.File

class DirectoryFileObserver(path: String) : FileObserver(path, CREATE) {
    val directory =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val destinationFile = File(directory, SAVEDDIRECTORY)
    var aboslutePath: String = destinationFile.absolutePath

    init {
        aboslutePath = path
    }

    override fun onEvent(event: Int, path: String?) {
        if (path != null) {
            Log.e("FileObserver: ", "File Created"+path)
        }
    }
}