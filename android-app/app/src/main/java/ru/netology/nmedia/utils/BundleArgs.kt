package ru.netology.nmedia.utils

import android.os.Bundle
import android.os.Parcelable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

var Bundle.post by BundlePostArgs(BundleKeys.ARG_POST)
var Bundle.postText by BundleStringArg(BundleKeys.ARG_POST_TEXT)
var Bundle.postId by BundleLongArg(BundleKeys.ARG_POST_ID)

var Bundle.editMode by BundleStringArg(BundleKeys.ARG_EDIT_TYPE)
// Объект для хранения ключей. На случай, если придётся передавать что-то ещё кроме текста поста.
object BundleKeys {
    const val ARG_POST_TEXT = "postText" // для хранения текста поста
    const val ARG_POST = "post" // для самого поста
    const val ARG_POST_ID = "postId" // для идентификатора поста
    const val ARG_EDIT_TYPE = "editType"
}

/**
 * Получение / запись строкового значения
 * */
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
/**
 * Получение / запись объекта */
class BundlePostArgs(private val key: String) : ReadWriteProperty<Bundle, Parcelable?> {
    override fun getValue(
        thisRef: Bundle,
        property: KProperty<*>
    ): Parcelable? = thisRef.getParcelable(key)

    override fun setValue(
        thisRef: Bundle,
        property: KProperty<*>,
        value: Parcelable?
    ) {
        if (value != null) {
            thisRef.putParcelable(key, value)
        } else {
            thisRef.remove(key)
        }
    }
}

/**
 * Получение / запись числа */
//class BundleLongArg(private val key: String) : ReadWriteProperty<Bundle, Long?> {
//    override fun getValue(
//        thisRef: Bundle,
//        property: KProperty<*>
//    ): Long = thisRef.getLong(key)
//
//    override fun setValue(
//        thisRef: Bundle,
//        property: KProperty<*>,
//        value: Long?
//    ) {
//        if (value != null) {
//            thisRef.putLong(key, value)
//        } else {
//            thisRef.remove(key)
//        }
//    }
//}

class BundleLongArg(
    private val key: String,
    private val defaultValue: Long = 0L
) : ReadWriteProperty<Bundle, Long> {

    override fun getValue(thisRef: Bundle, property: KProperty<*>): Long =
        thisRef.getLong(key, defaultValue)

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: Long) {
        thisRef.putLong(key, value)
    }
}