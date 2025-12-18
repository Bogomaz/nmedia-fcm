package ru.netology.nmedia.utils

import android.os.Bundle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// Объект для хранения ключей. На случай, если придётся передавать что-то ещё кроме текста поста.
object BundleKeys {
    const val ARG_POST_TEXT = "arg_post_text"
}
/**
 * Делегат в конструкторе имеет ключ,
 * по которому в bundle ищется  нужная строка,
 * и либо возвращается её нынешнее значение, либо записывается новое*/
class BundleStringArg(private val key: String) : ReadWriteProperty<Bundle, String?> {
    override fun getValue(
        thisRef: Bundle,
        property: KProperty<*>
    ): String? = thisRef.getString(key)

    override fun setValue(
        thisRef: Bundle,
        property: KProperty<*>,
        value: String?
    ) {
        if (value != null) {
            thisRef.putString(key, value)
        } else {
            thisRef.remove(key) // если по ключу вернулось null - удаляем ключ, чтобы не хранить устаревшие значения
        }
    }
}


object StringArg : ReadWriteProperty<Bundle, String?> {
    override fun getValue(
        thisRef: Bundle,
        property: KProperty<*>
    ): String? = thisRef.getString(property.name)


    override fun setValue(
        thisRef: Bundle,
        property: KProperty<*>,
        value: String?
    ) {
        thisRef.putString(property.name, value)
    }
}