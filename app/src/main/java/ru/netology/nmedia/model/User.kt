package ru.netology.nmedia.model

data class User (
    val id: Int = 0,
    var firstName: String = "",
    var lastName: String = "",
    var middleName: String? = null,
    val login: String,
    var isDeleted: Boolean = false
)