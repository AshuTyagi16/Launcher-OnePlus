package com.sasuke.launcheroneplus.util

import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.annotation.WorkerThread
import com.sasuke.launcheroneplus.data.AppInfo
import java.util.*
import kotlin.collections.ArrayList

object PackageResolverUtils {

    @WorkerThread
    suspend fun getSortedAppList(
        packageManager: PackageManager,
        list: List<ResolveInfo>
    ): MutableList<AppInfo> {
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
            it.label.toLowerCase(Locale.getDefault())
        }
        return appInoList
    }
}