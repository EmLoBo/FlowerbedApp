package pl.flowerbedapp.core.data.repository

import pl.flowerbedapp.core.data.mapper.toDomain
import pl.flowerbedapp.core.data.remote.api.EdwinApi
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.Weather
import pl.flowerbedapp.core.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton
import pl.flowerbedapp.core.util.safeCall
import retrofit2.HttpException
import java.io.IOException

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val api: EdwinApi,
) : WeatherRepository {

    override suspend fun getWeather(lat: Double, lon: Double): Result<Weather> {
        val result = safeCall {
            val weather = api.getCurrentWeather(lat, lon).data
                ?: error("Empty weather response from Edwin API")
            val alerts = runCatching { api.getAlerts(lat, lon).data?.alerts }.getOrNull()
            weather.copy(alerts = alerts ?: weather.alerts).toDomain("Your location")
        }
        return if (result is Result.Error) {
            Result.Error(result.exception, showMessage(result.exception))
        } else result
    }

    private fun showMessage(t: Throwable): String = when (t) {
        is IOException   -> "Couldn't reach the weather service.\nCheck your connection and try again."
        is HttpException -> "Weather data isn't available right now.\nPlease try again later."
        else             -> "Couldn't load the weather. Please try again."
    }
}