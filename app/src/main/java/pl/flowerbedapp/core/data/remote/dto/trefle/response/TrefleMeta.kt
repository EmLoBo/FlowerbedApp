package pl.flowerbedapp.core.data.remote.dto.trefle.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrefleMeta(
    @Json(name = "total") val total: Int?,
)