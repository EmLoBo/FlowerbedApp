package pl.flowerbedapp.core.data.remote.api

import pl.flowerbedapp.core.data.remote.dto.openmeteo.OpenMeteoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {

    /**
     * Current conditions for a coordinate. No API key needed.
     * `current` is a comma-separated list of variables — see https://open-meteo.com/en/docs
     */
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude")  lat: Double,
        @Query("longitude") lon: Double,
        @Query("current")   current: String = CURRENT_VARIABLES,
        @Query("timezone")  timezone: String = "auto",
    ): OpenMeteoResponse

    companion object {
        const val CURRENT_VARIABLES =
            "temperature_2m,relative_humidity_2m,apparent_temperature," +
                "precipitation,weather_code,wind_speed_10m"
    }
}
