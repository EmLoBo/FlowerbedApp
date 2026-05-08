package pl.flowerbedapp.core.domain.usecase.weather

import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.Weather
import pl.flowerbedapp.core.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repo: WeatherRepository,
) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<Weather> =
        repo.getWeather(lat, lon)
}