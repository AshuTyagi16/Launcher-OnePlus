package com.sasuke.launcheroneplus.ui.hidden_apps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.sasuke.launcheroneplus.data.db.RoomRepository
import com.sasuke.launcheroneplus.data.model.App
import javax.inject.Inject

class HiddenAppsActivityViewModel @Inject constructor(private val roomRepository: RoomRepository) :
    ViewModel() {

    private var _appList = MediatorLiveData<MutableList<App>>()
    val appList: LiveData<MutableList<App>>
        get() = _appList

    private lateinit var list: MutableList<App>

    init {
        _appList.addSource(roomRepository.getOnlyHiddenApps()) {
            list = it
            _appList.postValue(it)
        }
    }
}