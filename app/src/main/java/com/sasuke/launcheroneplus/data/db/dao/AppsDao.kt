package com.sasuke.launcheroneplus.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sasuke.launcheroneplus.data.model.App

@Dao
interface AppsDao {

    @Query("SELECT * from apps WHERE isHidden =:isHidden  ORDER BY label COLLATE NOCASE ASC")
    fun getVisibleApps(isHidden: Boolean): LiveData<MutableList<App>>

    @Query("SELECT * from apps ORDER BY label COLLATE NOCASE ASC")
    fun getAllApps(): LiveData<MutableList<App>>

    @Query("SELECT * from apps WHERE isHidden =:isHidden ORDER BY label COLLATE NOCASE ASC")
    fun getOnlyHiddenApps(isHidden: Boolean): LiveData<MutableList<App>>

    @Query("SELECT COUNT(*) from apps")
    fun isAppsInDB(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: App)

    @Query("UPDATE apps SET isHidden = :isHidden where _id IN(:ids)")
    suspend fun hideApps(ids: IntArray, isHidden: Boolean)

    @Query("UPDATE apps SET isHidden = :isHidden where _id IN(:ids)")
    suspend fun unhideApps(ids: IntArray, isHidden: Boolean)

    @Query("DELETE FROM apps")
    suspend fun deleteAllApps()

    @Query("DELETE FROM apps where packageName = :packageName")
    suspend fun deleteApp(packageName: String)
}