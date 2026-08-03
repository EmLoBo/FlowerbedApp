package pl.flowerbedapp.core.data.remote.api

import pl.flowerbedapp.core.data.remote.dto.imgw.ImgwWarningDto
import retrofit2.http.GET

interface ImgwApi {

    /** Currently active meteorological warnings for the whole of Poland. No auth needed. */
    @GET("api/data/warningsmeteo")
    suspend fun getMeteoWarnings(): List<ImgwWarningDto>
}
