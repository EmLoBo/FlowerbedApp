package pl.flowerbedapp.core.domain.model

data class Weather(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val description: String,
    val locationName: String,
    val alerts: List<WeatherAlert> = emptyList(),
) {
    val temperatureInt: Int get() = temperature.toInt()
    val hasAlerts: Boolean get() = alerts.isNotEmpty()
    val worstAlert: WeatherAlert? get() = alerts.maxByOrNull { it.severity.ordinal }
}

data class WeatherAlert(
    val id: String,
    val type: AlertType,
    val message: String,
    val severity: AlertSeverity,
    val validFrom: Long,
    val validTo: Long,
)

enum class AlertType(val emoji: String, val label: String) {
    FROST("❄️", "Frost warning"),
    HIGH_TEMP("🌡️", "Heat warning"),
    RAIN("🌧️", "Heavy rain"),
    DROUGHT("🏜️", "Drought"),
    WIND("💨", "Strong wind"),
    UV("☀️", "High UV"),
    GENERAL("⚠️", "Alert"),
}

enum class AlertSeverity { INFO, WARNING, DANGER }
