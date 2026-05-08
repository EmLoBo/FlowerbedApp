package pl.flowerbedapp.core.data.remote.dto.trefle.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TreflePlantDto(
    @Json(name = "id")                val id: Int,
    @Json(name = "slug")              val slug: String,
    @Json(name = "common_name")       val commonName: String?,
    @Json(name = "scientific_name")   val scientificName: String,
    @Json(name = "image_url")         val imageUrl: String?,
    @Json(name = "family_common_name") val familyCommonName: String?,
    @Json(name = "genus")             val genus: String?,
)