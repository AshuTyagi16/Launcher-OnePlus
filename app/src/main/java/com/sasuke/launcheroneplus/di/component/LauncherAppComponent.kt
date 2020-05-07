package com.sasuke.launcheroneplus.di.component

import android.content.Context
import com.sasuke.launcheroneplus.LauncherApp
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import com.sasuke.launcheroneplus.di.module.util.ViewModelFactoryModule
import com.sasuke.launcheroneplus.di.module.activity.ActivityBindingModule
import com.sasuke.launcheroneplus.di.module.util.UtilsModule
import com.sasuke.launcheroneplus.util.StorageUtils
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@LauncherAppScope
@Component(
    modules = [
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

    fun getStorageUtil(): StorageUtils
}