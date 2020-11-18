package com.sasuke.launcheroneplus.di.module.activity

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.di.mapkey.ViewModelKey
import com.sasuke.launcheroneplus.di.qualifiers.GridItemDecoration
import com.sasuke.launcheroneplus.di.qualifiers.ListDividerItemDecoration
import com.sasuke.launcheroneplus.di.qualifiers.ListItemDecoration
import com.sasuke.launcheroneplus.di.scope.PerActivityScope
import com.sasuke.launcheroneplus.ui.base.BaseEdgeEffectFactory
import com.sasuke.launcheroneplus.ui.base.SpaceItemDecoration
import com.sasuke.launcheroneplus.ui.base.SnapToBlock
import com.sasuke.launcheroneplus.ui.drag_drop.GridViewAdapter
import com.sasuke.launcheroneplus.ui.launcher.LauncherActivityViewModel
import com.sasuke.launcheroneplus.ui.launcher.all_apps.AppAdapter
import com.sasuke.launcheroneplus.ui.launcher.recent_apps.RecentAppSectionAdapter
import com.sasuke.launcheroneplus.util.Constants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class LauncherActivityModule {

    companion object {

        @Provides
        @PerActivityScope
        fun adapter(glide: RequestManager): AppAdapter {
            return AppAdapter(glide)
        }

        @Provides
        @PerActivityScope
        fun gridLayoutManager(context: Context): GridLayoutManager {
            return GridLayoutManager(
                context,
                Constants.APP_LIST_SPAN_COUNT,
                RecyclerView.VERTICAL,
                false
            ).apply {
                isItemPrefetchEnabled = true
                initialPrefetchItemCount = 50
            }
        }

        @Provides
        @PerActivityScope
        fun linearLayoutManager(context: Context): LinearLayoutManager {
            return LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        @Provides
        @PerActivityScope
        @GridItemDecoration
        fun gridSpaceItemDecoration(): SpaceItemDecoration {
            return SpaceItemDecoration(
                Constants.GRID_HORIZONTAL_SPACING,
                Constants.GRID_VERTICAL_SPACING
            )
        }

        @Provides
        @PerActivityScope
        @ListItemDecoration
        fun listSpaceItemDecorator(): SpaceItemDecoration {
            return SpaceItemDecoration(
                Constants.LIST_HORIZONTAL_SPACING,
                Constants.LIST_VERTICAL_SPACING
            )
        }

        @Provides
        @PerActivityScope
        @ListDividerItemDecoration
        fun listDividerItemDecorator(context: Context): DividerItemDecoration {
            return DividerItemDecoration(context, RecyclerView.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context, R.drawable.divider_app_list)!!)
            }
        }

        @Provides
        @PerActivityScope
        fun gridViewAdapter(glide: RequestManager): GridViewAdapter {
            return GridViewAdapter(glide)
        }

        @Provides
        @PerActivityScope
        fun pageSnapHelper(): SnapToBlock {
            return SnapToBlock(1)
        }

        @Provides
        @PerActivityScope
        fun baseEdgeEffect(): BaseEdgeEffectFactory {
            return BaseEdgeEffectFactory()
        }

        @Provides
        @PerActivityScope
        fun recentAppAdapter(glide: RequestManager): RecentAppSectionAdapter {
            return RecentAppSectionAdapter(glide)
        }

        @Provides
        @PerActivityScope
        fun concatAdapter(
            recentAppSectionAdapter: RecentAppSectionAdapter,
            appAdapter: AppAdapter
        ): ConcatAdapter {
            return ConcatAdapter(recentAppSectionAdapter, appAdapter)
        }
    }

    @Binds
    @IntoMap
    @ViewModelKey(LauncherActivityViewModel::class)
    abstract fun bindMainActivityViewModel(mainActivityViewModel: LauncherActivityViewModel): ViewModel
}