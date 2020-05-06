package com.sasuke.launcheroneplus.di.module.util

import androidx.lifecycle.ViewModelProvider
import com.sasuke.launcheroneplus.util.DaggerViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory
}