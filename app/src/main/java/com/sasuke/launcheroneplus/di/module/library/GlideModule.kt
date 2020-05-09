package com.sasuke.launcheroneplus.di.module.library

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import dagger.Module
import dagger.Provides

@Module
class GlideModule {

    @Provides
    @LauncherAppScope
    fun glide(context: Context): RequestManager {
        return Glide.with(context)
    }
}