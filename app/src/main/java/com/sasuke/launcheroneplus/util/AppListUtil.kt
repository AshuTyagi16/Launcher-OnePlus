package com.sasuke.launcheroneplus.util

import android.content.pm.PackageManager
import android.database.DatabaseUtils
import androidx.annotation.WorkerThread
import com.sasuke.launcheroneplus.data.db.RoomRepository
import com.sasuke.launcheroneplus.data.model.App

class AppListUtil(
    private val roomRepository: RoomRepository,
    private val bitmapUtils: BitmapUtils,
    private val storageUtils: StorageUtils,
    private val packageManager: PackageManager
) {

    @WorkerThread
    suspend fun saveAppsInDB() {
        if (roomRepository.isAppsInDB() <= 0) {
            val list = PackageResolverUtils.getSortedAppList(packageManager)
            list.forEach { appInfo ->
                bitmapUtils.drawableToBitmap(appInfo.icon)?.let {
                    storageUtils.saveBitmapToFile(it, appInfo.label.replace("[\\W]|_".toRegex(),""))?.let {
                        roomRepository.insert(
                            App(
                                icon = it,
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
        val appInfo = PackageResolverUtils.getAppInfoFromPackageName(packageManager, packageName)
        bitmapUtils.drawableToBitmap(appInfo.icon)?.let {
            storageUtils.saveBitmapToFile(it, appInfo.label.replace("[\\W]|_".toRegex(),""))?.let {
                roomRepository.insert(
                    App(
                        icon = it,
                        packageName = appInfo.packageName,
                        label = appInfo.label
                    )
                )
            }
        }
    }

}