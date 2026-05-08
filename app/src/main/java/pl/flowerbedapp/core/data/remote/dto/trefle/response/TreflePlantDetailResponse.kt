package pl.flowerbedapp.core.data.remote.dto.trefle.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pl.flowerbedapp.core.data.remote.dto.trefle.dto.TreflePlantDetailDto

@JsonClass(generateAdapter = true)
data class TreflePlantDetailResponse(
    @Json(name = "data") val data: TreflePlantDetailDto,
)
