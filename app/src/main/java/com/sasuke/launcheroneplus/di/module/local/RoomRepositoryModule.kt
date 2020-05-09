package com.sasuke.launcheroneplus.di.module.local

import android.content.Context
import androidx.room.Room
import com.sasuke.launcheroneplus.data.db.dao.AppsDao
import com.sasuke.launcheroneplus.data.db.LauncherDB
import com.sasuke.launcheroneplus.data.db.RoomRepository
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import dagger.Module
import dagger.Provides

@Module
class RoomRepositoryModule {

    @Provides
    @LauncherAppScope
    fun launcherDB(context: Context): LauncherDB {
        return Room.databaseBuilder(
            context,
            LauncherDB::class.java, "launcher_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @LauncherAppScope
    fun appDao(launcherDB: LauncherDB): AppsDao {
        return launcherDB.appsDao()
    }

    @Provides
    @LauncherAppScope
    fun roomRepository(appsDao: AppsDao): RoomRepository {
        return RoomRepository(appsDao)
    }
}