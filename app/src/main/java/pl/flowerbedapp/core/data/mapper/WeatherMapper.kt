package pl.flowerbedapp.core.data.mapper

import pl.flowerbedapp.core.data.remote.dto.edwin.EdwinAlertDto
import pl.flowerbedapp.core.data.remote.dto.edwin.EdwinWeatherDataDto
import pl.flowerbedapp.core.domain.model.AlertSeverity
import pl.flowerbedapp.core.domain.model.AlertType
import pl.flowerbedapp.core.domain.model.Weather
import pl.flowerbedapp.core.domain.model.WeatherAlert

fun EdwinWeatherDataDto.toDomain(fallbackLocation: String) = Weather(
    temperature  = temperature ?: 0.0,
    feelsLike    = feelsLike ?: 0.0,
    humidity     = humidity ?: 0,
    description  = description.orEmpty(),
    locationName = location ?: fallbackLocation,
    alerts       = alerts?.map { it.toDomain() }.orEmpty(),
)

fun EdwinAlertDto.toDomain() = WeatherAlert(
    id = id,
    type = parseAlertType(type),
    message = message,
    severity = parseAlertSeverity(severity),
    validFrom = validFrom,
    validTo = validTo,
)

private fun parseAlertType(raw: String) = when (raw.lowercase()) {
    "frost"             -> AlertType.FROST
    "high_temp", "heat" -> AlertType.HIGH_TEMP
    "rain"              -> AlertType.RAIN
    "drought"           -> AlertType.DROUGHT
    "wind"              -> AlertType.WIND
    "uv"                -> AlertType.UV
    else                -> AlertType.GENERAL
}

private fun parseAlertSeverity(raw: String) = when (raw.lowercase()) {
    "warning"           -> AlertSeverity.WARNING
    "danger", "extreme" -> AlertSeverity.DANGER
    else                -> AlertSeverity.INFO
}