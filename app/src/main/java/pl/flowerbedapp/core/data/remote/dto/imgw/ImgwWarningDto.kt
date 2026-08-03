package pl.flowerbedapp.core.data.remote.dto.imgw

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Official IMGW meteorological warning.
 * Note: `stopien` and `prawdopodobienstwo` arrive as strings ("1", "80"), not numbers.
 * `teryt` lists the affected powiat codes — the first two digits identify the voivodeship.
 */
@JsonClass(generateAdapter = true)
data class ImgwWarningDto(
    @Json(name = "id")                 val id: String,
    @Json(name = "nazwa_zdarzenia")    val eventName: String? = null,
    @Json(name = "stopien")            val level: String? = null,
    @Json(name = "prawdopodobienstwo") val probability: String? = null,
    @Json(name = "obowiazuje_od")      val validFrom: String? = null,
    @Json(name = "obowiazuje_do")      val validTo: String? = null,
    @Json(name = "tresc")              val content: String? = null,
    @Json(name = "teryt")              val teryt: List<String>? = null,
)
