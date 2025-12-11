package ru.netology.nmedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class PostActivityContract : ActivityResultContract<String?, String?>() {

    override fun createIntent(context: Context, input: String?): Intent =
        Intent(context, PostActivity::class.java).apply{
            input?.let{putExtra(Intent.EXTRA_TEXT, it)}
        }


    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        if (resultCode == Activity.RESULT_OK) {
            intent?.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }
}