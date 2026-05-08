package pl.flowerbedapp.core.data.remote.dto.edwin

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EdwinWeatherResponse(
    @Json(name = "data") val data: EdwinWeatherDataDto?,
)