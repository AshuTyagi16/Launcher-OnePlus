package com.sasuke.launcheroneplus.di.module.application

import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import dagger.Module
import dagger.Provides
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import timber.log.Timber

@Module
object LauncherApplicationModule {

    @Provides
    @LauncherAppScope
    fun timberTree(): Timber.Tree {
        return Timber.DebugTree()
    }

    @Provides
    @LauncherAppScope
    fun calligraphyInterceptor(): CalligraphyInterceptor {
        return CalligraphyInterceptor(
            CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lato.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}