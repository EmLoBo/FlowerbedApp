package pl.flowerbedapp.core.util

import pl.flowerbedapp.core.domain.model.Result
import timber.log.Timber

internal inline fun <T> safeCall(block: () -> T): Result<T> = try {
  Result.Success(block())
} catch (e: Exception) {
    Timber.e(e)
    Result.Error(e, e.message)
}