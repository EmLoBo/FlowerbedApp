package pl.flowerbedapp.core.data.remote.dto.edwin

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EdwinWeatherDataDto(
    @Json(name = "temperature") val temperature: Double?,
    @Json(name = "feels_like")  val feelsLike: Double?,
    @Json(name = "humidity")    val humidity: Int?,
    @Json(name = "description") val description: String?,
    @Json(name = "location")    val location: String?,
    @Json(name = "alerts")      val alerts: List<EdwinAlertDto>?,
)