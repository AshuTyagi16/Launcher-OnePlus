package com.sasuke.launcheroneplus.util

import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.WorkerThread
import com.sasuke.launcheroneplus.data.AppInfo
import java.util.*
import kotlin.collections.ArrayList

object PackageResolverUtils {

    @WorkerThread
    suspend fun getSortedAppList(
        packageManager: PackageManager
    ): MutableList<AppInfo> {

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val list = packageManager.queryIntentActivities(mainIntent, 0)
        val appInoList: MutableList<AppInfo> = ArrayList(list.size)

        list.forEach { resolveInfo ->
            val packageInfo = AppInfo(
                resolveInfo.loadIcon(packageManager),
                resolveInfo.activityInfo.packageName,
                resolveInfo.loadLabel(packageManager).toString()
            )
            appInoList.add(packageInfo)
        }

        appInoList.sortBy {
            it.label.toLowerCased()
        }

        return appInoList
    }
}