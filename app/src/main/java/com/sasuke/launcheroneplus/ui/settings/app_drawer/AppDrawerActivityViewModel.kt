package com.sasuke.launcheroneplus.ui.settings.app_drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sasuke.launcheroneplus.util.Constants
import com.sasuke.launcheroneplus.util.SharedPreferenceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppDrawerActivityViewModel @Inject constructor(
    private val sharedPreferenceUtil: SharedPreferenceUtil,
    private val gson: Gson
) :
    ViewModel() {

    fun setFastScrollState(isEnabled: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.getSettingPreference()?.let {
                    it.isFastScrollEnabled = isEnabled
                    sharedPreferenceUtil.putString(Constants.Settings.PREFERENCES, gson.toJson(it))
                }
            }
        }
    }

    fun setDrawerStyle(style: String) {
        var indicator = Constants.DrawerStyle.VERTICAL
        when (style) {
            Constants.DrawerStyle.VERTICAL.name -> indicator = Constants.DrawerStyle.VERTICAL
            Constants.DrawerStyle.LIST.name -> indicator = Constants.DrawerStyle.LIST
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.getSettingPreference()?.let {
                    it.drawerStyle = indicator
                    sharedPreferenceUtil.putString(Constants.Settings.PREFERENCES, gson.toJson(it))
                }
            }
        }
    }

    fun setPrimaryColor(color: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.getSettingPreference()?.let {
                    it.primaryColor = color
                    sharedPreferenceUtil.putString(Constants.Settings.PREFERENCES, gson.toJson(it))
                }
            }
        }
    }

    fun setBackgroundColor(color: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.getSettingPreference()?.let {
                    it.backgroundColor = color
                    sharedPreferenceUtil.putString(Constants.Settings.PREFERENCES, gson.toJson(it))
                }
            }
        }
    }

    fun setBackgroundTransparency(transparency: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.getSettingPreference()?.let {
                    it.backgroundColorAlpha = 100 - transparency
                    sharedPreferenceUtil.putString(Constants.Settings.PREFERENCES, gson.toJson(it))
                }
            }
        }
    }
}