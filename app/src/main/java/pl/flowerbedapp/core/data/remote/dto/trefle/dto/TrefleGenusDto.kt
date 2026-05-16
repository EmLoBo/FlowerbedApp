package pl.flowerbedapp.core.data.remote.dto.trefle.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrefleGenusDto(
    @Json(name = "name") val name: String?,
)
