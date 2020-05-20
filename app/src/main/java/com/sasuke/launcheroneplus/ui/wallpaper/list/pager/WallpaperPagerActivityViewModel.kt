package com.sasuke.launcheroneplus.ui.wallpaper.list.pager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sasuke.launcheroneplus.data.model.Error
import com.sasuke.launcheroneplus.data.model.Resource
import com.sasuke.launcheroneplus.data.model.Wallpaper
import com.sasuke.launcheroneplus.data.network.UnsplashRepository
import javax.inject.Inject

class WallpaperPagerActivityViewModel @Inject constructor(private val unsplashRepository: UnsplashRepository) :
    ViewModel(), UnsplashRepository.OnGetWallpaperListener {

    private val _wallpaperLiveData = MutableLiveData<Resource<Wallpaper>>()
    val wallpaperLiveData: LiveData<Resource<Wallpaper>>
        get() = _wallpaperLiveData

    fun getWallpapersForQuery(query: String) {
        unsplashRepository.getWallpapers(query, this)
    }

    override fun onGetWallpaperSuccess(wallpaper: Wallpaper) {
        _wallpaperLiveData.postValue(Resource.success(wallpaper))
    }

    override fun onGetWallpaperFailure(error: Error) {
        _wallpaperLiveData.postValue(Resource.error(error))
    }
}