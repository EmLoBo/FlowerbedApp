package pl.flowerbedapp.core.data.repository

import pl.flowerbedapp.core.data.mapper.toDomain
import pl.flowerbedapp.core.data.remote.api.EdwinApi
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.Weather
import pl.flowerbedapp.core.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton
import pl.flowerbedapp.core.util.safeCall

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val api: EdwinApi,
) : WeatherRepository {

    override suspend fun getWeather(lat: Double, lon: Double): Result<Weather> =
        safeCall {
            val weather = api.getCurrentWeather(lat, lon).data
                ?: error("Empty weather response from Edwin API")
            val alerts = runCatching { api.getAlerts(lat, lon).data?.alerts }.getOrNull()
            weather.copy(alerts = alerts ?: weather.alerts).toDomain("Your location")
        }
}