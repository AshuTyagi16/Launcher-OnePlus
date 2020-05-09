package com.sasuke.launcheroneplus.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sasuke.launcheroneplus.data.model.AppInfo
import com.sasuke.launcheroneplus.data.db.dao.AppsDao
import com.sasuke.launcheroneplus.data.model.App

@Database(entities = [App::class], version = 1, exportSchema = false)
abstract class LauncherDB : RoomDatabase() {
    abstract fun appsDao(): AppsDao
}