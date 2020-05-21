package com.sasuke.launcheroneplus.ui.launcher

import androidx.lifecycle.*
import com.sasuke.launcheroneplus.data.db.RoomRepository
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.util.SearchUtils
import com.sasuke.launcheroneplus.util.toLowerCased
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.ArrayList

class LauncherActivityViewModel @Inject constructor(roomRepository: RoomRepository) :
    ViewModel() {

    private var _appList = MediatorLiveData<MutableList<App>>()
    val appList: LiveData<MutableList<App>>
        get() = _appList

    private lateinit var list: MutableList<App>

    init {
        _appList.addSource(roomRepository.getApps()) {
            list = it
            _appList.postValue(it)
        }
    }

    private val _filterAppsLiveData = MutableLiveData<MutableList<App>>()
    val filterAppsLiveData: LiveData<MutableList<App>>
        get() = _filterAppsLiveData

    fun filterApps(query: String) {
        if (::list.isInitialized) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    if (query.isBlank())
                        _filterAppsLiveData.postValue(list)
                    else {
                        val filtered = ArrayList<App>()
                        list.forEach {
                            if (SearchUtils.matches(it.label.toLowerCased(), query.toLowerCased()))
                                filtered.add(it)
                        }
                        _filterAppsLiveData.postValue(filtered)
                    }
                }
            }
        }
    }
}