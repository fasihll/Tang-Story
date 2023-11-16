package com.example.tangstory.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private val MAX_SIZE = 1000000
private const val NAME_FORMAT= "yyyyMMdd_HHmmss"
private val filename: String = "TangStory_"+SimpleDateFormat(NAME_FORMAT,Locale.US).format(Date())


fun formatDate(currentDateString: String, targetTimeZone: String): String {
    val instant = Instant.parse(currentDateString)
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm")
        .withZone(ZoneId.of(targetTimeZone))
    return formatter.format(instant)
}

fun createCustomTempFile(context: Context): File{
    val fileDir = context.externalCacheDir
    return  File.createTempFile(filename,".jpg",fileDir)
}

fun uriToFile(imageUri: Uri,context: Context): File{
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStram = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStram.write(buffer,0, length)
    outputStram.close()
    inputStream.close()
    return myFile
}

fun File.reduceFileImage(): File{
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,compressQuality,bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    }while (streamLength > MAX_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG,compressQuality, FileOutputStream(file))
    return file
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun rotateImage(source: Bitmap,angel:Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angel)
    return Bitmap.createBitmap(
        source,0,0,source.width,source.height,matrix,true
    )
}
