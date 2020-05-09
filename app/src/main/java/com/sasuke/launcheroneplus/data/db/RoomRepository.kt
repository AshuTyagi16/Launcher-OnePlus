package com.sasuke.launcheroneplus.data.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.sasuke.launcheroneplus.data.db.dao.AppsDao
import com.sasuke.launcheroneplus.data.model.App

class RoomRepository(private val appsDao: AppsDao) {

    fun getApps(includeHidden: Boolean = false): LiveData<MutableList<App>> {
        if (includeHidden)
            return appsDao.getAllApps()
        else
            return appsDao.getVisibleApps(false)
    }

    fun getOnlyHiddenApps(): LiveData<MutableList<App>> {
        return appsDao.getOnlyHiddenApps(true)
    }

    fun isAppsInDB(): Int {
        return appsDao.isAppsInDB()
    }

    @WorkerThread
    suspend fun insert(app: App) {
        appsDao.insert(app)
    }

    @WorkerThread
    suspend fun hideApps(ids: IntArray) {
        appsDao.hideApps(ids, true)
    }

    @WorkerThread
    suspend fun unhideApps(ids: IntArray) {
        appsDao.unhideApps(ids, false)
    }
}