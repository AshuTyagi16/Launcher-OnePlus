package com.sasuke.launcheroneplus.ui.hidden_apps.app_selector

import androidx.lifecycle.*
import com.sasuke.launcheroneplus.data.db.RoomRepository
import com.sasuke.launcheroneplus.data.model.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppSelectionActivityViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val appSelectionAdapter: AppSelectionAdapter
) : ViewModel() {

    private lateinit var appList: MutableList<App>
    private val selectedAppList: MutableList<App> = ArrayList()

    private val _appListLiveData = MediatorLiveData<MutableList<App>>()
    val appListLiveData: LiveData<MutableList<App>>
        get() = _appListLiveData

    private val _selectedAppCountLiveData = MutableLiveData<Int>()
    val selectedAppCountLiveData: LiveData<Int>
        get() = _selectedAppCountLiveData

    init {
        _appListLiveData.addSource(roomRepository.getApps(true)) {
            appList = it
            _appListLiveData.postValue(it)
        }
    }

    fun setApps() {
        appSelectionAdapter.setApps(appList)
    }

    fun toggleSelection(position: Int) {
        if (::appList.isInitialized) {
            appSelectionAdapter.toggle(position)
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val item = appList[position]
                    if (selectedAppList.contains(item)) {
                        selectedAppList.remove(item)
                    } else {
                        selectedAppList.add(item)
                    }
                    _selectedAppCountLiveData.postValue(selectedAppList.size)
                }
            }
        }
    }

    fun hideSelectedApps() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val arr = IntArray(selectedAppList.size)
                selectedAppList.forEachIndexed { index, app ->
                    arr[index] = app._id
                }
                roomRepository.hideApps(arr)
            }
        }
    }
}