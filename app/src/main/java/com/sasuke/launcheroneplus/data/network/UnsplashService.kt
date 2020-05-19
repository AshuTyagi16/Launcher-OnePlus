package com.sasuke.launcheroneplus.data.network

import com.sasuke.launcheroneplus.data.model.Wallpaper
import com.sasuke.launcheroneplus.util.Constants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashService {

    @GET("search/photos")
    fun getWallpapersForQuery(
        @Query("query") query: String = "Switzerland",
        @Query("client_id") client_id: String = Constants.EXTRA_API_KEY,
        @Query("orientation") orientation: String = "portrait",
        @Query("page") pageNo: Int = 1,
        @Query("per_page") per_page: Int = 15
    ): Call<Wallpaper>
}