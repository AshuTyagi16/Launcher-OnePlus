package com.sasuke.launcheroneplus.di.component

import android.content.Context
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.LauncherApp
import com.sasuke.launcheroneplus.data.db.RoomRepository
import com.sasuke.launcheroneplus.data.network.UnsplashRepository
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import com.sasuke.launcheroneplus.di.module.util.ViewModelFactoryModule
import com.sasuke.launcheroneplus.di.module.activity.ActivityBindingModule
import com.sasuke.launcheroneplus.di.module.application.LauncherApplicationModule
import com.sasuke.launcheroneplus.di.module.library.GlideModule
import com.sasuke.launcheroneplus.di.module.network.UnsplashRepositoryModule
import com.sasuke.launcheroneplus.di.module.util.UtilsModule
import com.sasuke.launcheroneplus.util.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import timber.log.Timber

@LauncherAppScope
@Component(
    modules = [
        LauncherApplicationModule::class,
        UnsplashRepositoryModule::class,
        GlideModule::class,
        UtilsModule::class,
        AndroidSupportInjectionModule::class,
        ViewModelFactoryModule::class,
        ActivityBindingModule::class]
)
interface LauncherAppComponent : AndroidInjector<LauncherApp> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): LauncherAppComponent
    }

    fun context(): Context

    fun glide(): RequestManager

    fun getStorageUtil(): StorageUtils

    fun getBitmapUtils(): BitmapUtils

    fun getRoomRepository(): RoomRepository

    fun getAllListUtils(): AppListUtil

    fun getUnsplashRepository(): UnsplashRepository

    fun timberTree(): Timber.Tree

    fun calligraphyInterceptor(): CalligraphyInterceptor

    fun getSettingUtils(): SettingUtils

    fun getColorUtils(): ColorUtils
}