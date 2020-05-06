package com.sasuke.launcheroneplus.di.component

import android.content.Context
import com.sasuke.launcheroneplus.LauncherApp
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import com.sasuke.launcheroneplus.di.module.util.ViewModelFactoryModule
import com.sasuke.launcheroneplus.di.module.activity.ActivityBindingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@LauncherAppScope
@Component(
    modules = [
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
}