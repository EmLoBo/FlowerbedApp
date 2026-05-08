package pl.flowerbedapp.core.data.remote.dto.edwin

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EdwinAlertDto(
    @Json(name = "id")         val id: String,
    @Json(name = "type")       val type: String,
    @Json(name = "message")    val message: String,
    @Json(name = "severity")   val severity: String,
    @Json(name = "valid_from") val validFrom: Long,
    @Json(name = "valid_to")   val validTo: Long,
)