package pl.flowerbedapp.core.data.remote.dto.openmeteo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenMeteoResponse(
    @Json(name = "latitude")  val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "timezone")  val timezone: String? = null,
    @Json(name = "current")   val current: OpenMeteoCurrentDto? = null,
)

@JsonClass(generateAdapter = true)
data class OpenMeteoCurrentDto(
    @Json(name = "time")                 val time: String? = null,
    @Json(name = "temperature_2m")       val temperature: Double? = null,
    @Json(name = "relative_humidity_2m") val humidity: Int? = null,
    @Json(name = "apparent_temperature") val apparentTemperature: Double? = null,
    @Json(name = "precipitation")        val precipitation: Double? = null,
    @Json(name = "weather_code")         val weatherCode: Int? = null,
    @Json(name = "wind_speed_10m")       val windSpeed: Double? = null,
)
