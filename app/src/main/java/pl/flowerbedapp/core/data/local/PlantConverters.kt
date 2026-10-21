package pl.flowerbedapp.core.data.local

import androidx.room.TypeConverter

/**
 * Lists are stored as one TEXT column wrapped in commas: ",SANDY,LOAMY,".
 * The outer commas let a query match a whole value with LIKE '%,CLAY,%' —
 * without them "CLAY" would also match any longer value that merely contains it.
 */
class PlantConverters {

    @TypeConverter
    fun fromStringList(values: List<String>): String =
        if (values.isEmpty()) "" else values.joinToString(",", prefix = ",", postfix = ",")

    @TypeConverter
    fun toStringList(raw: String): List<String> =
        raw.split(',').filter { it.isNotEmpty() }

    @TypeConverter
    fun fromIntList(values: List<Int>): String = fromStringList(values.map(Int::toString))

    @TypeConverter
    fun toIntList(raw: String): List<Int> = toStringList(raw).map(String::toInt)
}
