package pl.flowerbedapp.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun observeBackgroundUri(): Flow<String?>
    fun observeIsLoggedIn(): Flow<Boolean>
    fun observeLastLocation(): Flow<Pair<Double, Double>?>

    /** null = the user hasn't chosen yet, so follow the system setting. */
    fun observeDarkTheme(): Flow<Boolean?>

    suspend fun setBackgroundUri(uri: String?)
    suspend fun setLoggedIn(value: Boolean)
    suspend fun setLastLocation(lat: Double, lon: Double)
    suspend fun setDarkTheme(value: Boolean)
}
