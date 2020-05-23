package com.sasuke.launcheroneplus.util

import android.content.SharedPreferences
import android.text.TextUtils
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.sasuke.launcheroneplus.data.model.SettingPreference

class SharedPreferencesSettingsLiveData(
    sharedPrefs: SharedPreferences,
    private val gson: Gson
) :
    SharedPreferencesLiveData<SettingPreference>(sharedPrefs) {

    private fun getString(key: String, default: String): String {
        sharedPrefs.getString(key, default)?.let {
            return it
        } ?: run {
            return ""
        }
    }

    private fun getString(key: String): String {
        return getString(key, "")
    }

    override fun getValueFromPreferences(key: String): SettingPreference? {
        var user: SettingPreference? = null
        try {
            val userString = getString(Constants.Settings.PREFERENCES)
            if (!TextUtils.isEmpty(userString)) {
                user = gson.fromJson(userString, SettingPreference::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return user
        }
    }
}

abstract class SharedPreferencesLiveData<T>(val sharedPrefs: SharedPreferences) : LiveData<T>() {
    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == Constants.Settings.PREFERENCES) {
                value = getValueFromPreferences(key)
            }
        }

    abstract fun getValueFromPreferences(key: String): T?

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(Constants.Settings.PREFERENCES)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}