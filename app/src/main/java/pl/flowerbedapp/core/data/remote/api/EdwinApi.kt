package pl.flowerbedapp.core.data.remote.api


import pl.flowerbedapp.core.data.remote.dto.edwin.EdwinWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EdwinApi {

    @GET("weather/current")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ): EdwinWeatherResponse

    @GET("weather/alerts")
    suspend fun getAlerts(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ): EdwinWeatherResponse
}
