package com.sasuke.launcheroneplus.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sasuke.launcheroneplus.data.model.Resource
import com.sasuke.launcheroneplus.data.model.Setting
import com.sasuke.launcheroneplus.util.SettingUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LauncherSettingsActivityViewModel @Inject constructor(private val settingUtils: SettingUtils) :
    ViewModel() {

    private val _settingsLiveData = MutableLiveData<Resource<List<Setting>>>()
    val settingsLiveData: LiveData<Resource<List<Setting>>>
        get() = _settingsLiveData

    fun getSettings() {
        _settingsLiveData.postValue(Resource.loading())
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _settingsLiveData.postValue(Resource.success(settingUtils.getSettings()))
            }
        }
    }
}