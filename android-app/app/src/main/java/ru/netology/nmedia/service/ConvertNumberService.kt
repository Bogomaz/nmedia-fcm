package ru.netology.nmedia.service
object ConvertNumberService {
    //Принимает число с количеством взаимодействий с постом
    //Возвращает строку, пригодную для вывода.
    fun convertNumberIntoText(number: Int): String {
        return when {
            number >= 1_000_000 -> formatNumberWithSuffix(number / 1_000_000.0, "M")
            number >= 10_000 -> "${number / 1_000}K"
            number >= 1_000 -> formatNumberWithSuffix(number / 1_000.0, "K")
            else -> number.toString()
        }
    }

    // Принимает число и суффикс, указывающий на размерность числа
    // Возвращает строку с нужным числом знаков после запятой и заданным суффиксом
    private fun formatNumberWithSuffix(value: Double, suffix: String): String {
        // Отрезать лишние цифры без округления
        val truncated = (value * 10).toInt() / 10.0
        val formatted = "%.1f".format(truncated).replace(",", ".")

        // Убрать .0, если число целое
        return if (formatted.endsWith(".0")) {
            formatted.dropLast(2) + "$suffix"
        } else {
            "$formatted$suffix"
        }
    }
}