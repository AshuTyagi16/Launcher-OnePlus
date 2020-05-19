package com.sasuke.launcheroneplus.di.module.network

import com.sasuke.launcheroneplus.data.network.UnsplashRepository
import com.sasuke.launcheroneplus.data.network.UnsplashService
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import dagger.Module
import dagger.Provides

@Module(includes = [UnsplashServiceModule::class])
class UnsplashRepositoryModule {

    @Provides
    @LauncherAppScope
    fun newsAppRepository(unsplashService: UnsplashService): UnsplashRepository {
        return UnsplashRepository(unsplashService)
    }
}