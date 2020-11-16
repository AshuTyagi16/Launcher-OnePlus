package com.sasuke.launcheroneplus.ui.launcher

import androidx.collection.LruCache
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sasuke.launcheroneplus.data.db.RoomRepository
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.util.Constants
import com.sasuke.launcheroneplus.util.SearchUtils
import com.sasuke.launcheroneplus.util.SharedPreferenceUtil
import com.sasuke.launcheroneplus.util.toLowerCased
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.ArrayList

class LauncherActivityViewModel @Inject constructor(
    roomRepository: RoomRepository,
    private val sharedPreferenceUtil: SharedPreferenceUtil,
    private val gson: Gson
) :
    ViewModel() {

    private var _appList = MediatorLiveData<MutableList<App>>()
    val appList: LiveData<MutableList<App>>
        get() = _appList

    private lateinit var list: MutableList<App>

    val lruCache: LruCache<String, App> = LruCache(Constants.APP_LIST_SPAN_COUNT)

    init {
        _appList.addSource(roomRepository.getApps()) {
            list = it
            _appList.postValue(it)
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.getString(Constants.RECENT_APPS).let {
                    if (!it.isBlank()) {
                        gson.fromJson<List<App>>(it, object : TypeToken<List<App>>() {}.type)?.let {
                            _recentAppsLiveData.postValue(it)
                            it.forEach {
                                insertInRecentAppCache(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private val _filterAppsLiveData = MutableLiveData<MutableList<App>>()
    val filterAppsLiveData: LiveData<MutableList<App>>
        get() = _filterAppsLiveData

    private val _recentAppsLiveData = MutableLiveData<List<App>>()
    val recentAppsLiveData: LiveData<List<App>>
        get() = _recentAppsLiveData

    fun filterApps(query: CharSequence?) {
        if (::list.isInitialized) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    if (query.isNullOrBlank())
                        _filterAppsLiveData.postValue(list)
                    else {
                        val filtered = ArrayList<App>()
                        list.forEach {
                            if (SearchUtils.matches(
                                    it.label.toLowerCased(),
                                    query.toString().toLowerCased()
                                )
                            )
                                filtered.add(it)
                        }
                        _filterAppsLiveData.postValue(filtered)
                    }
                }
            }
        }
    }

    fun insertInRecentAppCache(app: App) {
        lruCache.put(app.packageName, app)
        if (lruCache.size() >= Constants.APP_LIST_SPAN_COUNT)
            _recentAppsLiveData.postValue(lruCache.snapshot().toList().map { it.second }
                .asReversed())
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferenceUtil.putString(
                    Constants.RECENT_APPS,
                    gson.toJson(lruCache.snapshot().toList().map { it.second })
                )
            }
        }
    }
}