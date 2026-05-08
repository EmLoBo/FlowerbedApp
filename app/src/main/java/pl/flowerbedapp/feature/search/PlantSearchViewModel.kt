package pl.flowerbedapp.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.flowerbedapp.core.domain.model.GardenSearchParams
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.SoilType
import pl.flowerbedapp.core.domain.model.SunExposure
import pl.flowerbedapp.core.domain.usecase.location.GetLocationUseCase
import pl.flowerbedapp.core.domain.usecase.plant.SearchPlantsUseCase
import javax.inject.Inject

data class SearchUiState(
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val query: String = "",
    val phMin: Float = 5.5f,
    val phMax: Float = 7.0f,
    val sunExposure: SunExposure = SunExposure.PARTIAL_SUN,
    val soilType: SoilType = SoilType.LOAMY,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val usingDeviceLocation: Boolean = false,
)

@HiltViewModel
class PlantSearchViewModel @Inject constructor(
    private val searchPlants: SearchPlantsUseCase,
    private val getLocation: GetLocationUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState()
    )

    private val queryFlow = MutableStateFlow("")

    init {
        @OptIn(FlowPreview::class)
        queryFlow
            .debounce(450L)
            .distinctUntilChanged()
            .onEach { q ->
                _state.update { it.copy(query = q) }
                if (q.length >= 2 || q.isEmpty()) search()
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(q: String) { queryFlow.value = q }
    fun onPhChanged(min: Float, max: Float) = _state.update { it.copy(phMin = min, phMax = max) }
    fun onSunExposureChanged(s: SunExposure) = _state.update { it.copy(sunExposure = s) }
    fun onSoilTypeChanged(t: SoilType) = _state.update { it.copy(soilType = t) }
    fun onManualLocation(lat: Double, lon: Double) =
        _state.update { it.copy(latitude = lat, longitude = lon, usingDeviceLocation = false) }

    fun useDeviceLocation() {
        viewModelScope.launch {
            when (val r = getLocation()) {
                is Result.Success -> _state.update {
                    it.copy(latitude = r.data.first, longitude = r.data.second, usingDeviceLocation = true)
                }
                is Result.Error -> _state.update { it.copy(error = "Location unavailable") }
                Result.Loading  -> Unit
            }
        }
    }

    fun search() {
        viewModelScope.launch {
            val s = _state.value
            _state.update { it.copy(isLoading = true, error = null) }
            val params = GardenSearchParams(
                latitude = s.latitude,
                longitude = s.longitude,
                soilPhMin = s.phMin.toDouble(),
                soilPhMax = s.phMax.toDouble(),
                sunExposure = s.sunExposure,
                soilType = s.soilType,
                query = s.query,
            )
            when (val result = searchPlants(params)) {
                is Result.Success -> _state.update { it.copy(plants = result.data, isLoading = false) }
                is Result.Error   -> _state.update { it.copy(error = result.message, isLoading = false) }
                Result.Loading    -> Unit
            }
        }
    }
}
