package com.sasuke.launcheroneplus.di.module.util

import android.content.Context
import com.sasuke.launcheroneplus.data.db.RoomRepository
import com.sasuke.launcheroneplus.di.module.local.RoomRepositoryModule
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import com.sasuke.launcheroneplus.util.AppListUtil
import com.sasuke.launcheroneplus.util.BitmapUtils
import com.sasuke.launcheroneplus.util.NetworkUtil
import com.sasuke.launcheroneplus.util.StorageUtils
import dagger.Module
import dagger.Provides

@Module(includes = [RoomRepositoryModule::class])
class UtilsModule {

    @Provides
    @LauncherAppScope
    fun storageUtils(context: Context): StorageUtils {
        return StorageUtils(context)
    }

    @Provides
    @LauncherAppScope
    fun bitmapUtils(): BitmapUtils {
        return BitmapUtils()
    }

    @Provides
    @LauncherAppScope()
    fun appListUtils(
        roomRepository: RoomRepository,
        bitmapUtils: BitmapUtils,
        storageUtils: StorageUtils,
        context: Context
    ): AppListUtil {
        return AppListUtil(roomRepository, bitmapUtils, storageUtils, context.packageManager)
    }

    @Provides
    @LauncherAppScope
    fun networkUtil(context: Context): NetworkUtil {
        return NetworkUtil(context)
    }
}