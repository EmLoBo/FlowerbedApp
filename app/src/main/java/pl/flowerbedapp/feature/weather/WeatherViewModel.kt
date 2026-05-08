package pl.flowerbedapp.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.Weather
import pl.flowerbedapp.core.domain.usecase.location.GetLocationUseCase
import pl.flowerbedapp.core.domain.usecase.weather.GetWeatherUseCase
import javax.inject.Inject

data class WeatherUiState(
    val weather: Weather? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeather: GetWeatherUseCase,
    private val getLocation: GetLocationUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val loc = getLocation()) {
                is Result.Success -> when (val w = getWeather(loc.data.first, loc.data.second)) {
                    is Result.Success -> _state.update { it.copy(weather = w.data, isLoading = false) }
                    is Result.Error   -> _state.update { it.copy(error = w.message, isLoading = false) }
                    Result.Loading    -> Unit
                }
                is Result.Error -> _state.update { it.copy(error = "Location unavailable", isLoading = false) }
                Result.Loading  -> Unit
            }
        }
    }
}