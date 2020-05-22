package com.sasuke.launcheroneplus.data.network

import com.sasuke.launcheroneplus.data.model.Result
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
        @Query("per_page") per_page: Int = Constants.PAGE_SIZE
    ): Call<Wallpaper>

    @GET("photos")
    fun getPopular(
        @Query("order_by") order_by: String = "popular",
        @Query("client_id") client_id: String = Constants.EXTRA_API_KEY,
        @Query("page") pageNo: Int = 1,
        @Query("per_page") per_page: Int = 50
    ): Call<List<Result>>
}