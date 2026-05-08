package pl.flowerbedapp.core.domain.repository

import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.Weather

interface WeatherRepository {

    suspend fun getWeather(lat: Double, lon: Double): Result<Weather>
}