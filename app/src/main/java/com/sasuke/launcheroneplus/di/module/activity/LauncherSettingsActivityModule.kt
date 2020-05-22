package com.sasuke.launcheroneplus.di.module.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.di.mapkey.ViewModelKey
import com.sasuke.launcheroneplus.di.scope.PerActivityScope
import com.sasuke.launcheroneplus.ui.settings.LauncherSettingAdapter
import com.sasuke.launcheroneplus.ui.settings.LauncherSettingsActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class LauncherSettingsActivityModule {

    companion object {

        @Provides
        @PerActivityScope
        fun adapter(glide: RequestManager): LauncherSettingAdapter {
            return LauncherSettingAdapter(glide)
        }

        @Provides
        @PerActivityScope
        fun layoutManager(context: Context): LinearLayoutManager {
            return LinearLayoutManager(context)
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(LauncherSettingsActivityViewModel::class)
    abstract fun bindLauncherSettingsActivityViewModel(launcherSettingsActivityViewModel: LauncherSettingsActivityViewModel): ViewModel
}