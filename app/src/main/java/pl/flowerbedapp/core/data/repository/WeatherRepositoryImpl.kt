package pl.flowerbedapp.core.data.repository

import android.content.Context
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.flowerbedapp.core.data.mapper.toDomain
import pl.flowerbedapp.core.data.remote.api.ImgwApi
import pl.flowerbedapp.core.data.remote.api.OpenMeteoApi
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.Weather
import pl.flowerbedapp.core.domain.model.WeatherAlert
import pl.flowerbedapp.core.domain.repository.WeatherRepository
import pl.flowerbedapp.core.util.Voivodeship
import pl.flowerbedapp.core.util.safeCall
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val api: OpenMeteoApi,
    private val imgwApi: ImgwApi,
    @ApplicationContext private val ctx: Context,
) : WeatherRepository {

    override suspend fun getWeather(lat: Double, lon: Double): Result<Weather> {
        val result = safeCall {
            val current = api.getCurrentWeather(lat, lon).current
                ?: error("Empty weather response from Open-Meteo")

            current.toDomain(
                locationName = resolveLocationName(lat, lon),
                alerts       = loadWarnings(lat, lon),
            )
        }
        // Translate raw infrastructure errors into a friendly, user-facing message
        return if (result is Result.Error) {
            Result.Error(result.exception, showMessage(result.exception))
        } else result
    }

    /**
     * Official IMGW warnings for the user's voivodeship. Poland-only and strictly optional:
     * any failure here must not take the weather down with it.
     */
    private suspend fun loadWarnings(lat: Double, lon: Double): List<WeatherAlert> {
        val voivodeship = Voivodeship.nearestTo(lat, lon) ?: return emptyList()
        val now = System.currentTimeMillis()

        return runCatching {
            imgwApi.getMeteoWarnings()
                .filter { dto -> dto.teryt.orEmpty().any { it.startsWith(voivodeship.terytPrefix) } }
                .map { it.toDomain() }
                .filter { it.validTo == 0L || it.validTo >= now }
        }.getOrDefault(emptyList())
    }

    /** Reverse-geocodes to a city name; falls back to a generic label when unavailable. */
    private suspend fun resolveLocationName(lat: Double, lon: Double): String =
        withContext(Dispatchers.IO) {
            runCatching {
                @Suppress("DEPRECATION")
                Geocoder(ctx, Locale.getDefault())
                    .getFromLocation(lat, lon, 1)
                    ?.firstOrNull()
                    ?.let { it.locality ?: it.subAdminArea ?: it.adminArea }
            }.getOrNull() ?: DEFAULT_LOCATION_NAME
        }

    private fun showMessage(t: Throwable): String = when (t) {
        is IOException   -> "Couldn't reach the weather service.\nCheck your connection and try again."
        is HttpException -> "Weather data isn't available right now.\nPlease try again later."
        else             -> "Couldn't load the weather. Please try again."
    }

    private companion object {
        const val DEFAULT_LOCATION_NAME = "Your location"
    }
}
