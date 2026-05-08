package pl.flowerbedapp.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.flowerbedapp.core.data.repository.LocationRepositoryImpl
import pl.flowerbedapp.core.data.repository.PlantRepositoryImpl
import pl.flowerbedapp.core.data.repository.PreferencesRepositoryImpl
import pl.flowerbedapp.core.data.repository.ProjectRepositoryImpl
import pl.flowerbedapp.core.data.repository.WeatherRepositoryImpl
import pl.flowerbedapp.core.domain.repository.LocationRepository
import pl.flowerbedapp.core.domain.repository.PlantRepository
import pl.flowerbedapp.core.domain.repository.PreferencesRepository
import pl.flowerbedapp.core.domain.repository.ProjectRepository
import pl.flowerbedapp.core.domain.repository.WeatherRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun bindPlants(impl: PlantRepositoryImpl): PlantRepository
    @Binds abstract fun bindWeather(impl: WeatherRepositoryImpl): WeatherRepository
    @Binds abstract fun bindProjects(impl: ProjectRepositoryImpl): ProjectRepository
    @Binds abstract fun bindLocation(impl: LocationRepositoryImpl): LocationRepository
    @Binds abstract fun bindPrefs(impl: PreferencesRepositoryImpl): PreferencesRepository
}