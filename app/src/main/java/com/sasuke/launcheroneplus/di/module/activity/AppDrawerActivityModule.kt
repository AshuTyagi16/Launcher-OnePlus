package com.sasuke.launcheroneplus.di.module.activity

import androidx.lifecycle.ViewModel
import com.sasuke.launcheroneplus.di.mapkey.ViewModelKey
import com.sasuke.launcheroneplus.di.module.fragment.ColorPickerFragmentModule
import com.sasuke.launcheroneplus.di.scope.PerFragmentScope
import com.sasuke.launcheroneplus.ui.color_picker.ColorPickerFragment
import com.sasuke.launcheroneplus.ui.settings.app_drawer.AppDrawerActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class AppDrawerActivityModule {

    @PerFragmentScope
    @ContributesAndroidInjector(modules = [ColorPickerFragmentModule::class])
    internal abstract fun colorPickerFragment(): ColorPickerFragment

    @Binds
    @IntoMap
    @ViewModelKey(AppDrawerActivityViewModel::class)
    abstract fun bindAppDrawerActivityViewModel(appDrawerActivityViewModel: AppDrawerActivityViewModel): ViewModel
}