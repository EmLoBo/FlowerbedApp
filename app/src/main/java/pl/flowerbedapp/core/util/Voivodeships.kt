package pl.flowerbedapp.core.util

import kotlin.math.pow

/**
 * Polish voivodeships with their TERYT code and an approximate centre.
 *
 * IMGW publishes warnings per powiat (TERYT), and the first two digits of a powiat code are
 * its voivodeship. Picking the nearest centre is deliberately chosen over reverse geocoding:
 * it works offline, never returns null and doesn't depend on the device language. It can be
 * imprecise near a border, but warnings normally cover whole regions anyway.
 */
enum class Voivodeship(
    val terytPrefix: String,
    val label: String,
    val lat: Double,
    val lon: Double,
) {
    DOLNOSLASKIE("02", "dolnośląskie", 51.05, 16.30),
    KUJAWSKO_POMORSKIE("04", "kujawsko-pomorskie", 53.10, 18.35),
    LUBELSKIE("06", "lubelskie", 51.25, 22.80),
    LUBUSKIE("08", "lubuskie", 52.20, 15.25),
    LODZKIE("10", "łódzkie", 51.75, 19.40),
    MALOPOLSKIE("12", "małopolskie", 49.95, 20.15),
    MAZOWIECKIE("14", "mazowieckie", 52.30, 21.00),
    OPOLSKIE("16", "opolskie", 50.65, 17.90),
    PODKARPACKIE("18", "podkarpackie", 49.95, 22.20),
    PODLASKIE("20", "podlaskie", 53.30, 22.90),
    POMORSKIE("22", "pomorskie", 54.20, 18.00),
    SLASKIE("24", "śląskie", 50.30, 19.00),
    SWIETOKRZYSKIE("26", "świętokrzyskie", 50.75, 20.70),
    WARMINSKO_MAZURSKIE("28", "warmińsko-mazurskie", 53.85, 20.60),
    WIELKOPOLSKIE("30", "wielkopolskie", 52.30, 17.20),
    ZACHODNIOPOMORSKIE("32", "zachodniopomorskie", 53.60, 15.60);

    companion object {
        /** Nearest voivodeship centre to the given point, or null when clearly outside Poland. */
        fun nearestTo(lat: Double, lon: Double): Voivodeship? {
            if (lat !in POLAND_LAT_RANGE || lon !in POLAND_LON_RANGE) return null
            return entries.minByOrNull { v ->
                // Squared distance is enough for comparing; longitude is scaled because
                // a degree of longitude is shorter than a degree of latitude at this latitude.
                (v.lat - lat).pow(2) + ((v.lon - lon) * 0.6).pow(2)
            }
        }

        private val POLAND_LAT_RANGE = 48.5..55.0
        private val POLAND_LON_RANGE = 13.5..24.5
    }
}
