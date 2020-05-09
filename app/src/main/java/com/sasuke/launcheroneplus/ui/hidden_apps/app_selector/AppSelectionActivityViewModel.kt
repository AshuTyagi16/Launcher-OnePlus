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

    private val _showSeparatorLiveData = MediatorLiveData<Boolean>()
    val showSeparatorLiveData: LiveData<Boolean>
        get() = _showSeparatorLiveData

    private var defaultSelectedCount = 0


    init {
        _visibleAppListLiveData.addSource(roomRepository.getApps()) {
            visibleAppList = it
            _visibleAppListLiveData.postValue(it)
        }
        _hiddenAppListLiveData.addSource(roomRepository.getOnlyHiddenApps()) {
            hiddenAppList = it
            defaultSelectedCount = it.size
            _hiddenAppListLiveData.postValue(it)
        }
        _showSeparatorLiveData.addSource(
            zipLiveData(
                _visibleAppListLiveData,
                _hiddenAppListLiveData
            )
        ) {
            _showSeparatorLiveData.postValue(it.first.isNotEmpty() && it.second.isNotEmpty())
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
                    _selectedAppCountLiveData.postValue(selectedVisibleAppList.size + defaultSelectedCount)
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
                        defaultSelectedCount++
                    } else {
                        selectedHiddenAppList.add(item)
                        defaultSelectedCount--
                    }
                    _selectedAppCountLiveData.postValue(selectedVisibleAppList.size + defaultSelectedCount)
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

    private fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
        return MediatorLiveData<Pair<A, B>>().apply {
            var lastA: A? = null
            var lastB: B? = null

            fun update() {
                val localLastA = lastA
                val localLastB = lastB
                if (localLastA != null && localLastB != null)
                    this.value = Pair(localLastA, localLastB)
            }

            addSource(a) {
                lastA = it
                update()
            }
            addSource(b) {
                lastB = it
                update()
            }
        }
    }
}