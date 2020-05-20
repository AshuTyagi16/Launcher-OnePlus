package com.sasuke.launcheroneplus.di.module.activity

import com.sasuke.launcheroneplus.ui.launcher.LauncherActivity
import com.sasuke.launcheroneplus.di.scope.PerActivityScope
import com.sasuke.launcheroneplus.ui.hidden_apps.HiddenAppsActivity
import com.sasuke.launcheroneplus.ui.hidden_apps.app_selector.AppSelectionActivity
import com.sasuke.launcheroneplus.ui.screen_off.ScreenOffActivity
import com.sasuke.launcheroneplus.ui.wallpaper.WallpaperPreviewActivity
import com.sasuke.launcheroneplus.ui.wallpaper.list.grid.WallpaperSettingsActivity
import com.sasuke.launcheroneplus.ui.wallpaper.list.pager.WallpaperPagerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [])
abstract class ActivityBindingModule {

    @PerActivityScope
    @ContributesAndroidInjector(modules = [LauncherActivityModule::class])
    internal abstract fun mainActivity(): LauncherActivity

    @PerActivityScope
    @ContributesAndroidInjector(modules = [HiddenAppsActivityModule::class])
    internal abstract fun hiddenAppsActivity(): HiddenAppsActivity

    @PerActivityScope
    @ContributesAndroidInjector(modules = [AppSelectionActivityModule::class])
    internal abstract fun appSelectionActivity(): AppSelectionActivity

    @PerActivityScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun screenOffActivity(): ScreenOffActivity

    @PerActivityScope
    @ContributesAndroidInjector(modules = [WallpaperActivityModule::class])
    internal abstract fun wallpaperSettingsActivity(): WallpaperSettingsActivity

    @PerActivityScope
    @ContributesAndroidInjector(modules = [WallpaperPagerActivityModule::class])
    internal abstract fun wallpaperPagerActivity(): WallpaperPagerActivity

    @PerActivityScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun wallpaperPreviewActivity(): WallpaperPreviewActivity
}