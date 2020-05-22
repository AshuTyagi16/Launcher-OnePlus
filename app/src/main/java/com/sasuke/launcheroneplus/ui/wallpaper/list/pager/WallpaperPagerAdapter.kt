package com.sasuke.launcheroneplus.ui.wallpaper.list.pager

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Result

class WallpaperPagerAdapter(private val glide: RequestManager) :
    RecyclerView.Adapter<WallpaperPagerViewHolder>(), WallpaperPagerViewHolder.OnItemListener {

    val wallpapers = ArrayList<Result>()
    private lateinit var onItemListener: OnItemListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperPagerViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_wallpaper_pager, parent, false)
        val holder = WallpaperPagerViewHolder(
            view,
            glide
        )
        holder.setOnItemListener(this)
        return holder
    }

    override fun getItemCount(): Int {
        return wallpapers.size
    }

    override fun onBindViewHolder(holder: WallpaperPagerViewHolder, position: Int) {
        if (wallpapers.isNotEmpty() && position < wallpapers.size) {
            holder.setWallpaper(wallpapers[position])
        }
    }

    fun addWallpapers(list: List<Result>) {
        wallpapers.addAll(list)
    }

    interface OnItemListener {
        fun onItemClick(position: Int, result: Result, imageView: ImageView)
    }

    fun setOnItemListener(onItemListener: OnItemListener) {
        this.onItemListener = onItemListener
    }

    override fun onItemClick(position: Int, result: Result, imageView: ImageView) {
        if (::onItemListener.isInitialized)
            onItemListener.onItemClick(position, result, imageView)
    }
}