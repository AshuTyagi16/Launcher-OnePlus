package com.sasuke.launcheroneplus.di.module.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.di.mapkey.ViewModelKey
import com.sasuke.launcheroneplus.di.scope.PerActivityScope
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.ui.wallpaper.list.grid.WallpaperActivityViewModel
import com.sasuke.launcheroneplus.ui.wallpaper.list.grid.WallpaperAdapter
import com.sasuke.launcheroneplus.ui.wallpaper.list.pager.WallpaperPagerActivityViewModel
import com.sasuke.launcheroneplus.ui.wallpaper.list.pager.WallpaperPagerAdapter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class WallpaperPagerActivityModule {

    companion object {

        @Provides
        @PerActivityScope
        fun adapter(glide: RequestManager): WallpaperPagerAdapter {
            return WallpaperPagerAdapter(
                glide
            )
        }

        @Provides
        @PerActivityScope
        fun layoutManager(context: Context): LinearLayoutManager {
            return LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }

        @Provides
        @PerActivityScope
        fun itemDecoration(): ItemDecorator {
            return ItemDecorator(30, 250)
        }

        @Provides
        @PerActivityScope
        fun pagerSnapHelper(): PagerSnapHelper {
            return PagerSnapHelper()
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(WallpaperPagerActivityViewModel::class)
    abstract fun bindWallpaperPagerActivityViewModel(wallpaperPagerActivityViewModel: WallpaperPagerActivityViewModel): ViewModel
}