package com.example.wallifytask2.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import java.io.File

class MediaScanner(context: Context?, private val mFile: File) :
    MediaScannerConnection.MediaScannerConnectionClient {

    private var mMs: MediaScannerConnection? = MediaScannerConnection(context, this)

    override fun onMediaScannerConnected() = mMs?.scanFile(mFile.absolutePath, null)!!

    override fun onScanCompleted(path: String, uri: Uri) {
        mMs?.disconnect()
        mMs = null
    }

    init {
        mMs?.connect()
    }
}