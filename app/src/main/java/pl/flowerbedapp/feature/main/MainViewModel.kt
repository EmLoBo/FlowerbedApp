package pl.flowerbedapp.feature.main

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.Weather
import pl.flowerbedapp.core.domain.repository.PreferencesRepository
import pl.flowerbedapp.core.domain.usecase.location.GetLocationUseCase
import pl.flowerbedapp.core.domain.usecase.weather.GetWeatherUseCase
import javax.inject.Inject

data class MainUiState(
    val weather: Weather? = null,
    val isWeatherLoading: Boolean = true,
    val weatherError: String? = null,
    val backgroundUri: String? = null,
    val isLoggedIn: Boolean = false,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getWeather: GetWeatherUseCase,
    private val getLocation: GetLocationUseCase,
    private val prefs: PreferencesRepository,
) : ViewModel() {

    private var _weatherState = MutableStateFlow<Result<Weather>>(Result.Loading)

    val uiState: StateFlow<MainUiState> = combine(
        _weatherState,
        prefs.observeBackgroundUri(),
        prefs.observeIsLoggedIn(),
    ) { weather, bgUri, isLoggedIn ->
        MainUiState(
            weather = (weather as? Result.Success)?.data,
            isWeatherLoading = weather is Result.Loading,
            weatherError = (weather as? Result.Error)?.message,
            backgroundUri = bgUri,
            isLoggedIn = isLoggedIn,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainUiState(),
    )

    init {
        loadWeather()
    }

    fun loadWeather() {
        viewModelScope.launch {
            _weatherState.update { Result.Loading }
            when (val loc = getLocation()) {
                is Result.Success -> {
                    val (lat, lon) = loc.data
                    prefs.setLastLocation(lat, lon)
                    _weatherState.update { getWeather(lat, lon) }
                }

                is Result.Error -> _weatherState.update { loc }
                Result.Loading -> Unit
            }
        }
    }

    fun setBackground(uri: String?) {
        viewModelScope.launch { prefs.setBackgroundUri(uri) }
    }

    fun onPermissionDenied() {
        _weatherState.update {
            Result.Error(
                exception = SecurityException("Location permission required"),
                message = "Location permission required",
            )
        }
    }
}
