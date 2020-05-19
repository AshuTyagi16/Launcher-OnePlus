package com.sasuke.launcheroneplus.di.module.network

import android.content.Context
import com.sasuke.launcheroneplus.data.event.NoInternetEvent
import com.sasuke.launcheroneplus.data.exception.NoConnectivityException
import com.sasuke.launcheroneplus.di.qualifiers.*
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import com.sasuke.launcheroneplus.util.Constants
import com.sasuke.launcheroneplus.util.NetworkUtil
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.util.concurrent.TimeUnit

@Module
class NetworkModule {

    companion object {
        private const val CONNECTION_TIMEOUT: Long = 60
        private const val WRITE_TIMEOUT: Long = 60
        private const val READ_TIMEOUT: Long = 60
        private const val MAX_STALE: Int = 7
        private const val MAX_AGE: Int = 1
        private const val CACHE_SIZE: Long = 10 * 1000 * 1000 //10 MB CACHE
        private const val CACHE_CONTROL = "Cache-Control"
        private const val PRAGMA = "Pragma"
    }

    @Provides
    @LauncherAppScope
    fun okHttpClient(
        @HeaderInterceptor customInterceptor: Interceptor,
        @CacheInterceptor cacheInterceptor: Interceptor,
        @NetworkInterceptor networkInterceptor: Interceptor,
        @LoggingInterceptor loggingInterceptor: HttpLoggingInterceptor,
        @StaleIfErrorInterceptor staleIfErrorInterceptor: Interceptor,
        cache: Cache
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(customInterceptor)
            .addInterceptor(networkInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(staleIfErrorInterceptor)
            .addNetworkInterceptor(cacheInterceptor)
            .cache(cache)
            .build()
    }

    @Provides
    @LauncherAppScope
    @LoggingInterceptor
    fun loggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.i(message)
            }
        })
        loggingInterceptor.redactHeader("x-auth-token")
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    @Provides
    @LauncherAppScope
    @HeaderInterceptor
    fun customInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
            requestBuilder.header(Constants.EXTRA_API_VERSION_HEADER, Constants.EXTRA_API_VERSION)
            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @LauncherAppScope
    fun cache(cacheFile: File): Cache {
        return Cache(cacheFile, CACHE_SIZE)
    }

    @Provides
    @LauncherAppScope
    fun file(context: Context): File {
        return File(context.cacheDir, "okhttp-cache")
    }

    @Provides
    @LauncherAppScope
    @NetworkInterceptor
    fun networkInterceptor(networkUtil: NetworkUtil, cacheControl: CacheControl): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!networkUtil.isOnline()) {
                EventBus.getDefault().post(NoInternetEvent())
                request = request.newBuilder().cacheControl(cacheControl).build()
                val response = chain.proceed(request)
                if (response.cacheResponse == null)
                    throw NoConnectivityException()
            }
            return@Interceptor chain.proceed(request)
        }
    }

    @Provides
    @LauncherAppScope
    @StaleIfErrorInterceptor
    fun staleIfErrorInterceptor(cacheControl: CacheControl): Interceptor {
        return Interceptor { chain ->
            var response: Response? = null
            val request = chain.request()
            try {
                response?.close()
                response = chain.proceed(request)
                if (response.isSuccessful) response
            } catch (e: Exception) {

            }

            if (response == null || !response.isSuccessful) {
                val newRequest = request.newBuilder().cacheControl(cacheControl).build();
                try {
                    response?.close()
                    response = chain.proceed(newRequest)
                } catch (e: Exception) {
                    throw e
                }
            }
            response
        }
    }

    @Provides
    @LauncherAppScope
    fun cacheControl(): CacheControl {
        return CacheControl.Builder()
            .maxStale(MAX_STALE, TimeUnit.MINUTES)
            .maxAge(MAX_AGE, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @LauncherAppScope
    @CacheInterceptor
    fun cacheInterceptor(cacheControl: CacheControl): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            request = request.newBuilder()
                .header(CACHE_CONTROL, cacheControl.toString())
                .build()
            var response = chain.proceed(request)
            response = response.newBuilder()
                .removeHeader(PRAGMA)
                .removeHeader(CACHE_CONTROL)
                .header(CACHE_CONTROL, cacheControl.toString())
                .build();
            return@Interceptor response
        }
    }

}