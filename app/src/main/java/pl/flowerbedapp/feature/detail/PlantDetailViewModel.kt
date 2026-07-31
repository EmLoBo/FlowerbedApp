package pl.flowerbedapp.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Project
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.usecase.plant.GetPlantDetailUseCase
import pl.flowerbedapp.core.domain.usecase.project.AddPlantToProjectUseCase
import pl.flowerbedapp.core.domain.usecase.project.ObserveFavoritePlantIdsUseCase
import pl.flowerbedapp.core.domain.usecase.project.ObserveProjectsUseCase
import pl.flowerbedapp.core.domain.usecase.project.ToggleFavoriteUseCase
import javax.inject.Inject

data class DetailUiState(
    val plant: Plant? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val getDetail: GetPlantDetailUseCase,
    private val addToProject: AddPlantToProjectUseCase,
    private val favoriteToggle: ToggleFavoriteUseCase,
    observeProjects: ObserveProjectsUseCase,
    observeFavoriteIds: ObserveFavoritePlantIdsUseCase,
) : ViewModel() {

    private val plantId: Int = checkNotNull(handle["plantId"])
    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    // Backs the "add to project" picker
    val projects: StateFlow<List<Project>> = observeProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Read straight from the DB, so the heart always reflects reality
    val isFavorite: StateFlow<Boolean> = observeFavoriteIds()
        .map { ids -> plantId in ids }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val r = getDetail(plantId)) {
                is Result.Success -> _state.update { it.copy(plant = r.data, isLoading = false) }
                is Result.Error   -> _state.update { it.copy(error = r.message, isLoading = false) }
                Result.Loading    -> Unit
            }
        }
    }

    fun saveToProject(projectId: Long) {
        val plant = _state.value.plant ?: return
        viewModelScope.launch { addToProject(projectId, plant) }
    }

    fun toggleFavorite() {
        val plant = _state.value.plant ?: return
        viewModelScope.launch { favoriteToggle(plant) }
    }
}