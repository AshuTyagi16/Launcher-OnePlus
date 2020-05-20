package com.sasuke.launcheroneplus.ui.launcher

import androidx.lifecycle.*
import com.sasuke.launcheroneplus.data.db.RoomRepository
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.data.model.Error
import com.sasuke.launcheroneplus.data.model.Resource
import com.sasuke.launcheroneplus.data.model.Wallpaper
import com.sasuke.launcheroneplus.data.network.UnsplashRepository
import com.sasuke.launcheroneplus.util.SearchUtils
import com.sasuke.launcheroneplus.util.toLowerCased
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.ArrayList

class LauncherActivityViewModel @Inject constructor(
    roomRepository: RoomRepository,
    private val unsplashRepository: UnsplashRepository
) :
    ViewModel(), UnsplashRepository.OnGetWallpaperListener {

    private val _wallpaperLiveData = MutableLiveData<Resource<Wallpaper>>()
    val wallpaperLiveData: LiveData<Resource<Wallpaper>>
        get() = _wallpaperLiveData

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

    fun getWallpaper() {
        _wallpaperLiveData.postValue(Resource.loading())
        unsplashRepository.getWallpapers(onGetWallpaperListener = this)
    }

    override fun onGetWallpaperSuccess(wallpaper: Wallpaper) {
        _wallpaperLiveData.postValue(Resource.success(wallpaper))
    }

    override fun onGetWallpaperFailure(error: Error) {
        _wallpaperLiveData.postValue(Resource.error(error))
    }
}