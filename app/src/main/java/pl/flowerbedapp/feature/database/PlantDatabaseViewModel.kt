package pl.flowerbedapp.feature.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.usecase.plant.BrowsePlantsUseCase
import pl.flowerbedapp.core.domain.usecase.project.ObserveFavoritePlantIdsUseCase
import javax.inject.Inject

data class DatabaseUiState(
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val endReached: Boolean = false,
)

@HiltViewModel
class PlantDatabaseViewModel @Inject constructor(
    private val browse: BrowsePlantsUseCase,
    observeFavoriteIds: ObserveFavoritePlantIdsUseCase,
) : ViewModel() {

    val favoriteIds: StateFlow<Set<Int>> = observeFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _state = MutableStateFlow(DatabaseUiState())
    val state: StateFlow<DatabaseUiState> = _state.asStateFlow()

    private var page = 1
    private var loadJob: Job? = null

    init {
        @OptIn(FlowPreview::class)
        _query
            .debounce(400)
            .distinctUntilChanged()
            .onEach { loadFirstPage() }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(q: String) { _query.value = q }

    fun retry() = loadFirstPage()

    private fun loadFirstPage() {
        loadJob?.cancel()                     // przerwij ewentualne poprzednie ładowanie
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isLoadingMore = false, error = null, endReached = false) }
            loadPage(pageToLoad = 1, append = false)
        }
    }

    fun loadMore() {
        val s = _state.value
        if (s.isLoading || s.isLoadingMore || s.endReached || s.error != null) return
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            loadPage(pageToLoad = page + 1, append = true)
        }
    }

    private suspend fun loadPage(pageToLoad: Int, append: Boolean) {
        browse(_query.value, pageToLoad).collect { result ->
            when (result) {
                Result.Loading    -> Unit
                is Result.Success -> {
                    page = pageToLoad
                    _state.update {
                        it.copy(
                            plants        = if (append) it.plants + result.data else result.data,
                            isLoading     = false,
                            isLoadingMore = false,
                            endReached    = result.data.size < PAGE_SIZE,
                        )
                    }
                }
                is Result.Error   -> _state.update {
                    it.copy(
                        isLoading     = false,
                        isLoadingMore = false,
                        error         = if (append) it.error else result.message,  // przy doklejaniu nie kasujemy listy
                    )
                }
            }
        }
    }

    private companion object { const val PAGE_SIZE = 20 }
}
