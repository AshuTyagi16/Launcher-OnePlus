package com.sasuke.launcheroneplus.di.module.util

import android.content.Context
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import com.sasuke.launcheroneplus.util.StorageUtils
import dagger.Module
import dagger.Provides

@Module
class UtilsModule {

    @Provides
    @LauncherAppScope
    fun storageUtils(context: Context): StorageUtils {
        return StorageUtils(context)
    }
}