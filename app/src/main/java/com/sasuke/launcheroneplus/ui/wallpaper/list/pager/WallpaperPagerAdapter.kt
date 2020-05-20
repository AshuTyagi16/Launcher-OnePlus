package com.sasuke.launcheroneplus.ui.wallpaper.list.pager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Result

class WallpaperPagerAdapter(private val glide: RequestManager) :
    RecyclerView.Adapter<WallpaperPagerViewHolder>() {

    val wallpapers = ArrayList<Result>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperPagerViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_wallpaper_pager, parent, false)
        val holder = WallpaperPagerViewHolder(
            view,
            glide
        )
        return holder
    }

    override fun getItemCount(): Int {
        return wallpapers.size
    }

    override fun onBindViewHolder(holder: WallpaperPagerViewHolder, position: Int) {
        if (wallpapers.isNotEmpty() && position < wallpapers.size - 1) {
            holder.setWallpaper(wallpapers[position])
        }
    }

    fun addWallpapers(list: List<Result>) {
        wallpapers.clear()
        wallpapers.addAll(list)
    }
}