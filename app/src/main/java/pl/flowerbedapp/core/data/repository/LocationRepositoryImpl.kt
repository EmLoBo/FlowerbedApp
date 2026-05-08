package pl.flowerbedapp.core.data.repository

import kotlinx.coroutines.tasks.await
import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.repository.LocationRepository
import pl.flowerbedapp.core.util.safeCall
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationRepository {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<Pair<Double, Double>> = safeCall {
        val cts = CancellationTokenSource()
        try{
            val req = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .build()
            val loc = fusedClient.getCurrentLocation(req, cts.token).await()
                ?: error("Location is null — permission may be missing")
            loc.latitude to loc.longitude
        }finally {
            cts.cancel()
            //without it: If the calling coroutine is cancelled (screen leaves, ViewModel cleared), the location request keeps running and holds resources.
        }

    }
}