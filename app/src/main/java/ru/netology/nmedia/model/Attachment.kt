package ru.netology.nmedia.model

sealed class Attachment(val type: String)

data class PhotoAttachment(val photo: Photo): Attachment("photo")
data class VideoAttachment(val video: Video): Attachment("video")
data class FileAttachment(val file: File): Attachment("file")
data class StickerAttachment(val sticker: Sticker): Attachment("sticker")
data class GeotagAttachment(val geotag: Geotag): Attachment("geotag")


data class Photo(
    val id: Int, // идентификатор
    val ownerId: Int, // идентификатор владельца фото
    val photo130: String?, // фото для предпросмотра
    val photo604: String // полноразмерное фото
)

data class Video(
    val id: Int,
    val ownerId: Int, // идентификатор владельца видео
    val title: String?, // название
    val description: String?, // текст описания
    val duration: Int, // длительность ролика в секундах
    val views: Int, // количество просмотров
    val player: String //URL страницы с плеером, который можно использовать для воспроизведения ролика в браузере.
)
data class File(
    val id: Int,
    val ownerId: Int, // идентификатор владельца видео
    val title: String?, // название
    val size: Int, //Размер файла в байтах.
    val ext: String, // Расширение файла.
    val url: String, // Адрес файла, по которому его можно загрузить.
    val date: Int, // Дата добавления в формате Unixtime
)
data class Sticker(
    val productId: Int, //Идентификатор набора
    val stickerId: Int, // Идентификатор стикера
    val animationUrl: String?, // URL анимации стикера
    val isAllowed: Boolean // Информация о том, доступен ли стикер.
)

data class Geotag(
    val type: PlaceType = PlaceType.HOME, //Тип места.
    val coordinates: Coordinates, //Координаты места. Объект, который содержит поля:
    val place: Place? = null // Информация о месте, если она добавлена.
)

data class Coordinates(
    val latitude: Double, // географическая широта
    val longitude: Double, //географическая долгота.
)

data class Place(
    val type: Int, //тип чекина
    val groupId: Int, //идентификатор сообщества
    val groupPhoto: String, //URL миниатюры главной фотографии сообщества
    val checkins: Int, //количество чекинов
    val updated: Int, // время последнего чекина в Unixtime;
    val address: Int // адрес.
)

enum class PlaceType {
    HOME,
    JOB,
    RESTAURANT,
    SIGHT,
    SCHOOL,
    HOSPITAL
}