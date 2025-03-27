package kg.autojuuguch.automoikakg.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import io.reactivex.Maybe

fun getGalleryImages(context: Context): Maybe<MutableList<Uri>> {
    val fileList = mutableListOf<Uri>()
    val projection = arrayOf(MediaStore.Files.FileColumns._ID)
    val sortOrder = MediaStore.Images.Media._ID + " DESC"

    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )

    cursor?.use {
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id.toString()
            )

            fileList.add(contentUri)
        }
    }
    return Maybe.just(fileList)
}

fun getAllMediaFiles(context: Context) {
    val fileList = mutableListOf<Uri>()

    val queryUri = if (Build.VERSION.SDK_INT >= 29) {
        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else MediaStore.Files.getContentUri("external")

    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.MIME_TYPE
    )

    context.contentResolver.query(
        queryUri,
        projection,
        null,
        null,
        null
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val mimeType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
    }
}