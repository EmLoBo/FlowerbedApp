package pl.flowerbedapp.core.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.flowerbedapp.BuildConfig
import pl.flowerbedapp.core.data.remote.api.ImgwApi
import pl.flowerbedapp.core.data.remote.api.OpenMeteoApi
import pl.flowerbedapp.core.data.remote.api.TrefleApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides @Named("trefle")
    fun provideTrefleAuthInterceptor(): Interceptor = Interceptor { chain ->
        chain.proceed(
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.TREFLE_TOKEN}")
                .build()
        )
    }

    private fun baseOkHttp(vararg interceptors: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .apply { interceptors.forEach(::addInterceptor) }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides @Singleton
    fun provideTrefleApi(@Named("trefle") auth: Interceptor, moshi: Moshi): TrefleApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.TREFLE_BASE_URL)
            .client(baseOkHttp(auth))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TrefleApi::class.java)

    @Provides @Singleton
    fun provideOpenMeteoApi(moshi: Moshi): OpenMeteoApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.OPEN_METEO_BASE_URL)
            .client(baseOkHttp())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenMeteoApi::class.java)

    @Provides @Singleton
    fun provideImgwApi(moshi: Moshi): ImgwApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.IMGW_BASE_URL)
            .client(baseOkHttp())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ImgwApi::class.java)
}