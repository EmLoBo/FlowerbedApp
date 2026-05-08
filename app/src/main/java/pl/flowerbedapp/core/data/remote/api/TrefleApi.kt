package pl.flowerbedapp.core.data.remote.api

import pl.flowerbedapp.core.data.remote.dto.trefle.response.TreflePlantDetailResponse
import pl.flowerbedapp.core.data.remote.dto.trefle.response.TreflePlantsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TrefleApi {

    @GET("plants")
    suspend fun getPlants(
        @Query("page") page: Int = 1,
    ): TreflePlantsResponse

    @GET("plants/search")
    suspend fun searchPlants(
        @Query("q")    query: String,
        @Query("page") page: Int = 1,
    ): TreflePlantsResponse

    /**
     * Trefle range filter — ph_minimum, ph_maximum, light, soil_texture.
     * Trefle uses filter[] query params for each field.
     */
    @GET("plants")
    suspend fun filterPlants(
        @Query("filter[ph_minimum]")  phMin: Double? = null,
        @Query("filter[ph_maximum]")  phMax: Double? = null,
        @Query("filter[light]")       light: Int? = null,
        @Query("filter[soil_texture]") soilTexture: Int? = null,
        @Query("q")                   query: String? = null,
        @Query("page")                page: Int = 1,
    ): TreflePlantsResponse

    @GET("plants/{id}")
    suspend fun getPlantById(@Path("id") id: Int): TreflePlantDetailResponse
}