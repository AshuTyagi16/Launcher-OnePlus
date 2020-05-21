package com.sasuke.launcheroneplus.data.network

import com.sasuke.launcheroneplus.data.model.Error
import com.sasuke.launcheroneplus.data.model.Result
import com.sasuke.launcheroneplus.data.model.Wallpaper
import com.sasuke.launcheroneplus.util.ApiCallback

class UnsplashRepository(private val unsplashService: UnsplashService) {

    fun getWallpapers(
        query: String = "Switzerland",
        page: Int = 1,
        onGetWallpaperListener: OnGetWallpaperListener
    ) {
        unsplashService.getWallpapersForQuery(query = query, pageNo = page)
            .enqueue(object : ApiCallback<Wallpaper>() {
                override fun success(response: Wallpaper) {
                    onGetWallpaperListener.onGetWallpaperSuccess(response)
                }

                override fun failure(error: Error) {
                    onGetWallpaperListener.onGetWallpaperFailure(error)
                }

            })
    }

    fun getPopular(onGetPopularListener: OnGetPopularListener) {
        unsplashService.getPopular().enqueue(object : ApiCallback<List<Result>>() {
            override fun success(response: List<Result>) {
                onGetPopularListener.onGetPopularSuccess(response)
            }

            override fun failure(error: Error) {
                onGetPopularListener.onGetPopularFailure(error)
            }

        })
    }

    interface OnGetWallpaperListener {
        fun onGetWallpaperSuccess(wallpaper: Wallpaper)

        fun onGetWallpaperFailure(error: Error)
    }

    interface OnGetPopularListener {
        fun onGetPopularSuccess(list: List<Result>)

        fun onGetPopularFailure(error: Error)
    }
}