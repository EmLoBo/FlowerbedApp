package pl.flowerbedapp.core.data.remote.dto.trefle.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class TrefleSpecDto(
    @Json(name = "average_height") val averageHeight: TrefleHeightDto?,
    @Json(name = "maximum_height") val maximumHeight: TrefleHeightDto?,
    @Json(name = "toxicity")       val toxicity: String?,
    @Json(name = "lifespan")       val lifespan: String?,
)