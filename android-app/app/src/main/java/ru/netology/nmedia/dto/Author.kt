package ru.netology.nmedia.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Author(
    val id: Long,
    val name: String,
    val avatar: String,
) : Parcelable

