package com.example.wallifytask2.utils

import android.app.Activity
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.postDelayed
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


var pixelApiKey = "9ybqmcDFACoAEsaStiwcLT2XRKkT0Sxww4XpWgBX8cEzRgrxMsmDPlle"

var WALLPAPER_OBJ = "wallpaperObj"


fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.getBitmapFromUrl(imageUrl: String): Bitmap? {
    return Glide.with(this)
        .asBitmap()
        .load(imageUrl)
        .submit()
        .get()
}


fun urlToBitmap(
    imageURL: String,
    context: Context,
    transformation: coil.transform.Transformation? = null,
    onSuccess: (bitmap: Bitmap) -> Unit,
    onError: (error: Throwable) -> Unit
) {
    var bitmap: Bitmap? = null
    val loadBitmap = CoroutineScope(Dispatchers.IO).launch {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageURL)
            .transformations(transformation ?: DefaultTransformation())
            .allowHardware(false)
            .build()
        val result = loader.execute(request)
        if (result is SuccessResult) {
            bitmap = (result.drawable as BitmapDrawable).bitmap
        } else if (result is ErrorResult) {
            cancel(result.throwable.localizedMessage ?: "ErrorResult", result.throwable)
        }
    }
    loadBitmap.invokeOnCompletion { throwable ->
        CoroutineScope(Dispatchers.Main).launch {
            bitmap?.let {
                onSuccess(it)
            } ?: throwable?.let {
                onError(it)
            } ?: onError(Throwable("Undefined Error"))
        }
    }
}


fun Context.saveImageToCacheStorage(bitmap: Bitmap): String {
    val file = File(cacheDir, "walify_${System.currentTimeMillis()}.png")
    val fileOutputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
    fileOutputStream.flush()
    fileOutputStream.close()
    return file.path
}



fun Context.saveImageToPublicFolder(bitmap: Bitmap): String? {
    val currentTime = System.currentTimeMillis()
    val dataStamp = SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(Date())
    val saveImageName = "wallify_${currentTime}_$dataStamp.png"
    val folderName = "Wallify"
    var fos: OutputStream? = null
    var filePath: String? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, saveImageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/$folderName")
        }
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        fos = imageUri?.let { contentResolver.openOutputStream(it) }

        // Get file path from URI
        filePath = imageUri?.let { getRealPathFromURI(it) }

    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() +
                File.separator + folderName
        val file = File(imagesDir)
        if (!file.exists()) {
            file.mkdir()
        }
        val image = File(imagesDir, saveImageName)
        fos = FileOutputStream(image)
        MediaScanner(this, image)
        filePath = image.absolutePath // Get the file path
    }

    fos?.let {
        val saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        it.flush()
        it.close()

        if (saved) {
            Handler(Looper.getMainLooper()).post {
                this.showToast("Saved to Gallery")
            }
        }
    }

    return filePath
}

// Helper function to convert URI to file path (for Android Q and above)
private fun Context.getRealPathFromURI(uri: Uri): String? {
    var path: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = contentResolver.query(uri, projection, null, null, null)
    cursor?.let {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            path = it.getString(columnIndex)
        }
        it.close()
    }
    return path
}



//fun Context.saveImageToPublicFolder(bitmap: Bitmap) {
//    val currentTime = System.currentTimeMillis()
//    val dataStamp = SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(Date())
//    val saveImageName = "wallify_${currentTime}_$dataStamp.png"
//    val folderName = "Wallify"
//    val fos: OutputStream?
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        val contentValues = ContentValues()
//        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, saveImageName)
//        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
//        contentValues.put(
//            MediaStore.MediaColumns.RELATIVE_PATH,
//            "${Environment.DIRECTORY_DCIM}/$folderName"
//        )
//        val imageUri =
//            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//        fos = imageUri?.let { contentResolver.openOutputStream(it) }
//    } else {
//        val imagesDir = Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_DCIM
//        ).toString() + File.separator + folderName
//        val file = File(imagesDir)
//        if (!file.exists()) {
//            file.mkdir()
//        }
//        val image = File(imagesDir, saveImageName)
//        fos = FileOutputStream(image)
//        MediaScanner(this, image)
//    }
//   var  saved = fos?.let { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) } == true
//    fos?.flush()
//    fos?.close()
//    Handler(Looper.getMainLooper()).postDelayed(0) {
//        this.showToast("Save To Gallery")
//    }
//}

fun setWallpaper(context: Context, bitmap: Bitmap, flagSystem: Int) {
    val wallpaperManager = WallpaperManager.getInstance(context)
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            wallpaperManager.setBitmap(bitmap, null, true, flagSystem)
        } else {
            wallpaperManager.setBitmap(bitmap)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // Handle the exception (e.g., show an error message)
    }
}


fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

@Composable
fun CircularIndeterminateProgressBar(isDisplay: Boolean) {
    if (isDisplay) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
