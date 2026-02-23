package ru.netology.nmedia.utils

import android.content.Context
import androidx.core.content.edit

private const val PREFS_NAME = "draft_prefs"
private const val KEY_DRAFT_TEXT = "draft_text"

fun Context.saveDraft(text: String?) {
    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit {
        putString(KEY_DRAFT_TEXT, text)
    }
}

fun Context.getDraft(): String? {
    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_DRAFT_TEXT, null)
}

fun Context.clearDraft() {
    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit {
        remove(KEY_DRAFT_TEXT)
    }
}
