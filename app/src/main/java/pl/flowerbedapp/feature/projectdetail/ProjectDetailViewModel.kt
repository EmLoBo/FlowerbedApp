package pl.flowerbedapp.feature.projectdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pl.flowerbedapp.core.domain.model.Project
import pl.flowerbedapp.core.domain.usecase.project.ObserveFavoritePlantIdsUseCase
import pl.flowerbedapp.core.domain.usecase.project.ObserveProjectsUseCase
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    handle: SavedStateHandle,
    observeProjects: ObserveProjectsUseCase,
    observeFavoriteIds: ObserveFavoritePlantIdsUseCase,
) : ViewModel() {

    private val projectId: Long = checkNotNull(handle["projectId"])

    val favoriteIds: StateFlow<Set<Int>> = observeFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    // Derived from the observed project list, so the screen updates itself when the
    // project or its plants change. null = not loaded yet (or project was deleted).
    val project: StateFlow<Project?> = observeProjects()
        .map { projects -> projects.firstOrNull { it.id == projectId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
