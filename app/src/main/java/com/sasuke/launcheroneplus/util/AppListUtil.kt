package com.sasuke.launcheroneplus.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.WorkerThread
import com.sasuke.launcheroneplus.data.db.RoomRepository
import com.sasuke.launcheroneplus.data.model.App
import javax.inject.Inject

class AppListUtil @Inject constructor(
    private val roomRepository: RoomRepository,
    private val bitmapUtils: BitmapUtils,
    private val storageUtils: StorageUtils,
    private val packageManager: PackageManager,
    private val context: Context
) {

    @WorkerThread
    suspend fun saveAppsInDB() {
        if (roomRepository.isAppsInDB() <= 0) {
            val list = PackageResolverUtils.getSortedAppList(packageManager)
            list.forEach { appInfo ->
                bitmapUtils.drawableToBitmap(appInfo.icon)?.let {
                    if (appInfo.packageName != context.packageName) {
                        storageUtils.saveBitmapToFile(it, appInfo.label)
                        roomRepository.insert(
                            App(
                                packageName = appInfo.packageName,
                                label = appInfo.label
                            )
                        )
                    }
                }
            }
        }
    }

    @WorkerThread
    suspend fun removeAppFromDB(packageName: String) {
        roomRepository.deleteApp(packageName)
    }

    @WorkerThread
    suspend fun addAppToDB(packageName: String) {
        PackageResolverUtils.getAppInfoFromPackageName(packageManager, packageName)
            ?.let { appInfo ->
                bitmapUtils.drawableToBitmap(appInfo.icon)?.let {
                    storageUtils.saveBitmapToFile(it, appInfo.label)
                    roomRepository.insert(
                        App(
                            packageName = appInfo.packageName,
                            label = appInfo.label
                        )
                    )
                }
            }
    }

}