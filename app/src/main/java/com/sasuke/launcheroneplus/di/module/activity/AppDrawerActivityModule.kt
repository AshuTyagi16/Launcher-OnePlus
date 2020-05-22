package com.sasuke.launcheroneplus.di.module.activity

import com.sasuke.launcheroneplus.di.module.fragment.ColorPickerFragmentModule
import com.sasuke.launcheroneplus.di.scope.PerFragmentScope
import com.sasuke.launcheroneplus.ui.color_picker.ColorPickerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppDrawerActivityModule {

    @PerFragmentScope
    @ContributesAndroidInjector(modules = [ColorPickerFragmentModule::class])
    internal abstract fun colorPickerFragment(): ColorPickerFragment
}