package pl.flowerbedapp.core.data.mapper

import pl.flowerbedapp.core.data.remote.dto.imgw.ImgwWarningDto
import pl.flowerbedapp.core.data.remote.dto.openmeteo.OpenMeteoCurrentDto
import pl.flowerbedapp.core.domain.model.AlertSeverity
import pl.flowerbedapp.core.domain.model.AlertType
import pl.flowerbedapp.core.domain.model.Weather
import pl.flowerbedapp.core.domain.model.WeatherAlert
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun OpenMeteoCurrentDto.toDomain(
    locationName: String,
    alerts: List<WeatherAlert> = emptyList(),
) = Weather(
    temperature  = temperature ?: 0.0,
    feelsLike    = apparentTemperature ?: temperature ?: 0.0,
    humidity     = humidity ?: 0,
    description  = weatherCodeToDescription(weatherCode),
    locationName = locationName,
    alerts       = alerts,
)

// ─── IMGW warnings ───────────────────────────────────────────────────────────

private val IMGW_DATE_FORMAT: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun ImgwWarningDto.toDomain() = WeatherAlert(
    id        = id,
    type      = eventNameToType(eventName),
    message   = content.orEmpty(),
    severity  = levelToSeverity(level),
    validFrom = parseImgwDate(validFrom),
    validTo   = parseImgwDate(validTo),
)

/** IMGW publishes Polish event names; match the common ones onto our garden-facing types. */
private fun eventNameToType(name: String?): AlertType {
    val n = name?.lowercase().orEmpty()
    return when {
        n.contains("przymrozk") || n.contains("mróz") || n.contains("mroz") -> AlertType.FROST
        n.contains("upał") || n.contains("upal")                           -> AlertType.HIGH_TEMP
        n.contains("deszcz") || n.contains("opad")                         -> AlertType.RAIN
        n.contains("susz")                                                 -> AlertType.DROUGHT
        n.contains("wiatr") || n.contains("wichur")                        -> AlertType.WIND
        else                                                               -> AlertType.GENERAL
    }
}

/** IMGW severity is 1 (be aware) / 2 (be prepared) / 3 (take action). */
private fun levelToSeverity(level: String?) = when (level) {
    "3"  -> AlertSeverity.DANGER
    "2"  -> AlertSeverity.WARNING
    else -> AlertSeverity.INFO
}

private fun parseImgwDate(raw: String?): Long = runCatching {
    LocalDateTime.parse(raw, IMGW_DATE_FORMAT)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}.getOrDefault(0L)

/** WMO weather interpretation codes used by Open-Meteo. */
private fun weatherCodeToDescription(code: Int?): String = when (code) {
    0            -> "Clear sky"
    1            -> "Mainly clear"
    2            -> "Partly cloudy"
    3            -> "Overcast"
    45, 48       -> "Fog"
    51, 53, 55   -> "Drizzle"
    56, 57       -> "Freezing drizzle"
    61           -> "Slight rain"
    63           -> "Rain"
    65           -> "Heavy rain"
    66, 67       -> "Freezing rain"
    71           -> "Slight snow"
    73           -> "Snow"
    75           -> "Heavy snow"
    77           -> "Snow grains"
    80, 81       -> "Rain showers"
    82           -> "Violent rain showers"
    85, 86       -> "Snow showers"
    95           -> "Thunderstorm"
    96, 99       -> "Thunderstorm with hail"
    else         -> "Unknown conditions"
}
