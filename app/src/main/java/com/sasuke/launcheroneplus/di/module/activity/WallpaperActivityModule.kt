package com.sasuke.launcheroneplus.di.module.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.di.mapkey.ViewModelKey
import com.sasuke.launcheroneplus.di.scope.PerActivityScope
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.ui.wallpaper.list.grid.WallpaperActivityViewModel
import com.sasuke.launcheroneplus.ui.wallpaper.list.grid.WallpaperAdapter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class WallpaperActivityModule {

    companion object {

        @Provides
        @PerActivityScope
        fun adapter(glide: RequestManager): WallpaperAdapter {
            return WallpaperAdapter(
                glide
            )
        }

        @Provides
        @PerActivityScope
        fun layoutManager(context: Context): GridLayoutManager {
            return GridLayoutManager(context, 3)
        }

        @Provides
        @PerActivityScope
        fun itemDecoration(): ItemDecorator {
            return ItemDecorator(10, 20)
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(WallpaperActivityViewModel::class)
    abstract fun bindWallpaperActivityViewModel(wallpaperActivityViewModel: WallpaperActivityViewModel): ViewModel
}