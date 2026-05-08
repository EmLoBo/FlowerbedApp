package pl.flowerbedapp.core.data.remote.dto.trefle.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pl.flowerbedapp.core.data.remote.dto.trefle.dto.TreflePlantDto


@JsonClass(generateAdapter = true)
data class TreflePlantsResponse(
    @Json(name = "data") val data: List<TreflePlantDto>,
    @Json(name = "meta") val meta: TrefleMeta?,
)