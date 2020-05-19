package com.sasuke.launcheroneplus.di.module.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sasuke.launcheroneplus.data.network.UnsplashService
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [NetworkModule::class])
class UnsplashServiceModule {

    companion object {
        private const val BASE_URL = "https://api.unsplash.com/"
    }

    @Provides
    @LauncherAppScope
    fun newsAppService(retrofit: Retrofit): UnsplashService {
        return retrofit.create(UnsplashService::class.java)
    }

    @Provides
    @LauncherAppScope
    fun retrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .build()
    }

    @Provides
    @LauncherAppScope
    fun gson(): Gson {
        return GsonBuilder().create()
    }
}