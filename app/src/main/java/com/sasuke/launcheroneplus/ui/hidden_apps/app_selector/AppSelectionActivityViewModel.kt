package com.sasuke.launcheroneplus.ui.hidden_apps.app_selector

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sasuke.launcheroneplus.data.AppInfo
import com.sasuke.launcheroneplus.util.PackageResolverUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppSelectionActivityViewModel @Inject constructor(
    context: Context,
    private val appSelectionAdapter: AppSelectionAdapter
) : ViewModel() {

    private val packageManager = context.packageManager

    private lateinit var appList: MutableList<AppInfo>
    private val selectedAppList: MutableList<AppInfo> = ArrayList()

    private val _selectedAppCountLiveData = MutableLiveData<Int>()
    val selectedAppCountLiveData: LiveData<Int>
        get() = _selectedAppCountLiveData

    fun getAppList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                return@withContext PackageResolverUtils.getSortedAppList(packageManager)
            }.let {
                appList = it
                appSelectionAdapter.setApps(appList)
            }
        }
    }

    fun toggleSelection(position: Int) {
        if (::appList.isInitialized) {
            appSelectionAdapter.toggle(position)
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val item = appList[position]
                    if (selectedAppList.contains(item))
                        selectedAppList.remove(item)
                    else selectedAppList.add(item)
                    _selectedAppCountLiveData.postValue(selectedAppList.size)
                }
            }
        }
    }
}