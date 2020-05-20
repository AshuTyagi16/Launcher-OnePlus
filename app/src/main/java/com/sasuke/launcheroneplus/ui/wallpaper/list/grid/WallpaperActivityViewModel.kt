package com.sasuke.launcheroneplus.ui.wallpaper.list.grid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sasuke.launcheroneplus.data.model.Error
import com.sasuke.launcheroneplus.data.model.Resource
import com.sasuke.launcheroneplus.data.model.Result
import com.sasuke.launcheroneplus.data.model.Wallpaper
import com.sasuke.launcheroneplus.data.network.UnsplashRepository
import javax.inject.Inject

class WallpaperActivityViewModel @Inject constructor(private val unsplashRepository: UnsplashRepository) :
    ViewModel(), UnsplashRepository.OnGetWallpaperListener,
    UnsplashRepository.OnGetPopularListener {

    private val _wallpaperLiveData = MutableLiveData<Resource<List<Result>>>()
    val wallpaperLiveData: LiveData<Resource<List<Result>>>
        get() = _wallpaperLiveData

    fun getWallpapersForQuery(query: String) {
        _wallpaperLiveData.postValue(Resource.loading())
        unsplashRepository.getWallpapers(query, this)
    }

    fun getPopularWalls() {
        _wallpaperLiveData.postValue(Resource.loading())
        unsplashRepository.getPopular(this)
    }

    override fun onGetWallpaperSuccess(wallpaper: Wallpaper) {
        _wallpaperLiveData.postValue(Resource.success(wallpaper.results))
    }

    override fun onGetWallpaperFailure(error: Error) {
        _wallpaperLiveData.postValue(Resource.error(error))
    }

    override fun onGetPopularSuccess(list: List<Result>) {
        _wallpaperLiveData.postValue(Resource.success(list))
    }

    override fun onGetPopularFailure(error: Error) {
        _wallpaperLiveData.postValue(Resource.error(error))
    }
}