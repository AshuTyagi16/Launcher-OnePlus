package com.sasuke.launcheroneplus.di.module.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.sasuke.launcheroneplus.di.scope.LauncherAppScope
import com.sasuke.launcheroneplus.util.SharedPreferenceUtil
import com.sasuke.launcheroneplus.util.SharedPreferencesSettingsLiveData
import dagger.Module
import dagger.Provides

@Module
object SharedPreferenceModule {

    @Provides
    @LauncherAppScope
    fun sharedPreferenceUtil(preferences: SharedPreferences, gson: Gson): SharedPreferenceUtil {
        return SharedPreferenceUtil(preferences, gson)
    }

    @Provides
    @LauncherAppScope
    fun preferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @LauncherAppScope
    fun getSharedPreferencesUserLiveData(
        preferences: SharedPreferences,
        gson: Gson
    ): SharedPreferencesSettingsLiveData {
        return SharedPreferencesSettingsLiveData(preferences, gson)
    }
}