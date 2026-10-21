package pl.flowerbedapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pl.flowerbedapp.core.data.local.seed.PlantSeeder
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class FlowerbedApp : Application() {

    @Inject lateinit var plantSeeder: PlantSeeder

    // Lives as long as the process, so seeding is never cancelled by a screen closing
    private val appScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        appScope.launch { plantSeeder.seedIfNeeded() }
    }
}
