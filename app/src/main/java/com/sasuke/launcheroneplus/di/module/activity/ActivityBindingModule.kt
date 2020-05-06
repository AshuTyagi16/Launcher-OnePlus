package com.sasuke.launcheroneplus.di.module.activity

import com.sasuke.launcheroneplus.ui.launcher.LauncherActivity
import com.sasuke.launcheroneplus.di.scope.PerActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [])
abstract class ActivityBindingModule {

    @PerActivityScope
    @ContributesAndroidInjector(modules = [LauncherActivityModule::class])
    internal abstract fun mainActivity(): LauncherActivity
}