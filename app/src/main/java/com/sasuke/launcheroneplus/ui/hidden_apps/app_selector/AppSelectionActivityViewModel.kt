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
    private val visibleAppSelectionAdapter: VisibleAppSelectionAdapter,
    private val hiddenAppSelectionAdapter: HiddenAppSelectionAdapter
) : ViewModel() {

    private lateinit var visibleAppList: MutableList<App>
    private val selectedVisibleAppList: MutableList<App> = ArrayList()

    private lateinit var hiddenAppList: MutableList<App>
    private val selectedHiddenAppList: MutableList<App> = ArrayList()

    private val _hiddenAppListLiveData = MediatorLiveData<MutableList<App>>()
    val hiddenAppListLiveData: LiveData<MutableList<App>>
        get() = _hiddenAppListLiveData

    private val _visibleAppListLiveData = MediatorLiveData<MutableList<App>>()
    val visibleAppListLiveData: LiveData<MutableList<App>>
        get() = _visibleAppListLiveData

    private val _selectedAppCountLiveData = MutableLiveData<Int>()
    val selectedAppCountLiveData: LiveData<Int>
        get() = _selectedAppCountLiveData

    init {
        _visibleAppListLiveData.addSource(roomRepository.getApps()) {
            visibleAppList = it
            _visibleAppListLiveData.postValue(it)
        }
        _hiddenAppListLiveData.addSource(roomRepository.getOnlyHiddenApps()) {
            hiddenAppList = it
            _hiddenAppListLiveData.postValue(it)
        }
    }

    fun setVisibleApps() {
        visibleAppSelectionAdapter.setApps(visibleAppList)
    }

    fun setHiddenApps() {
        hiddenAppSelectionAdapter.setApps(hiddenAppList)
    }

    fun toggleVisibleSelection(position: Int) {
        if (::visibleAppList.isInitialized) {
            visibleAppSelectionAdapter.toggle(position)
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val item = visibleAppList[position]
                    if (selectedVisibleAppList.contains(item)) {
                        selectedVisibleAppList.remove(item)
                    } else {
                        selectedVisibleAppList.add(item)
                    }
                    _selectedAppCountLiveData.postValue(selectedVisibleAppList.size + selectedHiddenAppList.size)
                }
            }
        }
    }

    fun toggleHiddenSelection(position: Int) {
        if (::hiddenAppList.isInitialized) {
            hiddenAppSelectionAdapter.toggle(position)
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val item = hiddenAppList[position]
                    if (selectedHiddenAppList.contains(item)) {
                        selectedHiddenAppList.remove(item)
                    } else {
                        selectedHiddenAppList.add(item)
                    }
                    _selectedAppCountLiveData.postValue(selectedHiddenAppList.size + selectedVisibleAppList.size)
                }
            }
        }
    }

    fun hideSelectedApps() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val arr = IntArray(selectedVisibleAppList.size)
                selectedVisibleAppList.forEachIndexed { index, app ->
                    arr[index] = app._id
                }
                roomRepository.hideApps(arr)
            }
        }
    }

    fun unhideSelectedApps() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val arr = IntArray(selectedHiddenAppList.size)
                selectedHiddenAppList.forEachIndexed { index, app ->
                    arr[index] = app._id
                }
                roomRepository.unhideApps(arr)
            }
        }
    }
}