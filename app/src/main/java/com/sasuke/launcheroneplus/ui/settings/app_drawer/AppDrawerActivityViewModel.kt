package com.sasuke.launcheroneplus.ui.settings.app_drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sasuke.launcheroneplus.data.model.DrawerStyle
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
                    sharedPreferenceUtil.putString(Constants.PREFERENCES, gson.toJson(it))
                }
            }
        }
    }

    fun setDrawerStyle(style: String) {
        var indicator = DrawerStyle.VERTICAL
        when (style) {
            DrawerStyle.VERTICAL.name -> indicator = DrawerStyle.VERTICAL
            DrawerStyle.LIST.name -> indicator = DrawerStyle.LIST
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.getSettingPreference()?.let {
                    it.drawerStyle = indicator
                    sharedPreferenceUtil.putString(Constants.PREFERENCES, gson.toJson(it))
                }
            }
        }
    }

    fun setPrimaryColor(color: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.getSettingPreference()?.let {
                    it.primaryColor = color
                    sharedPreferenceUtil.putString(Constants.PREFERENCES, gson.toJson(it))
                }
            }
        }
    }

    fun setBackgroundColor(color: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.getSettingPreference()?.let {
                    it.backgroundColor = color
                    sharedPreferenceUtil.putString(Constants.PREFERENCES, gson.toJson(it))
                }
            }
        }
    }

    fun setBackgroundTransparency(transparency: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.getSettingPreference()?.let {
                    it.backgroundColorAlpha = 100 - transparency
                    sharedPreferenceUtil.putString(Constants.PREFERENCES, gson.toJson(it))
                }
            }
        }
    }
}