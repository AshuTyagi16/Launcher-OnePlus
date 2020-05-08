package com.sasuke.launcheroneplus.di.module.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.sasuke.launcheroneplus.di.mapkey.ViewModelKey
import com.sasuke.launcheroneplus.di.scope.PerActivityScope
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.ui.hidden_apps.app_selector.AppSelectionActivityViewModel
import com.sasuke.launcheroneplus.ui.hidden_apps.app_selector.AppSelectionAdapter
import com.sasuke.launcheroneplus.util.Constants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class AppSelectionActivityModule {

    companion object {

        @Provides
        @PerActivityScope
        fun adapter(): AppSelectionAdapter {
            return AppSelectionAdapter()
        }

        @Provides
        @PerActivityScope
        fun gridLayoutManager(context: Context): GridLayoutManager {
            return GridLayoutManager(context, Constants.APP_LIST_SPAN_COUNT)
        }

        @Provides
        @PerActivityScope
        fun itemDecorator(): ItemDecorator {
            return ItemDecorator(
                Constants.APP_LIST_HORIZONTAL_SPACING,
                Constants.APP_LIST_VERTICAL_SPACING
            )
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(AppSelectionActivityViewModel::class)
    abstract fun bindAppSelectionActivityViewModel(appSelectionActivityViewModel: AppSelectionActivityViewModel): ViewModel
}