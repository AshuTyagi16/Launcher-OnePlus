package com.sasuke.launcheroneplus.data.network

import com.sasuke.launcheroneplus.data.model.Error
import com.sasuke.launcheroneplus.data.model.Wallpaper
import com.sasuke.launcheroneplus.util.ApiCallback

class UnsplashRepository(private val unsplashService: UnsplashService) {

    fun getWallpapers(
        onGetWallpaperListener: OnGetWallpaperListener
    ) {
        unsplashService.getWallpapersForQuery().enqueue(object : ApiCallback<Wallpaper>() {
            override fun success(response: Wallpaper) {
                onGetWallpaperListener.onGetWallpaperSuccess(response)
            }

            override fun failure(error: Error) {
                onGetWallpaperListener.onGetWallpaperFailure(error)
            }

        })
    }

    interface OnGetWallpaperListener {
        fun onGetWallpaperSuccess(wallpaper: Wallpaper)

        fun onGetWallpaperFailure(error: Error)
    }
}