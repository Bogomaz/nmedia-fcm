package ru.netology.nmedia.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import ru.netology.nmedia.R

fun Context.openVideo(url: String?) {
    if (url.isNullOrBlank()) {
        Toast.makeText(this, R.string.invalid_link, Toast.LENGTH_SHORT).show()
        return
    }
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, R.string.invalid_link, Toast.LENGTH_SHORT).show()
    }
}