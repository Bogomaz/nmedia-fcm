package ru.netology.nmedia.model

data class User (
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String? = null,
    val login: String,
    val isDeleted: Boolean = false
)