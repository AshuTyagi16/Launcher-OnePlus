package com.sasuke.launcheroneplus.ui.wallpaper.list.grid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Result

class WallpaperAdapter(private val glide: RequestManager) :
    RecyclerView.Adapter<WallpaperViewHolder>(), WallpaperViewHolder.OnItemClickListener {

    private val wallpapers = ArrayList<Result>()
    private lateinit var onItemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_wallpaper, parent, false)
        val holder = WallpaperViewHolder(
            view,
            glide
        )
        holder.setOnItemClickListener(this)
        return holder
    }

    override fun getItemCount(): Int {
        return wallpapers.size
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        if (wallpapers.isNotEmpty() && position < wallpapers.size - 1) {
            holder.setWallpaper(wallpapers[position])
        }
    }

    fun addWallpapers(list: List<Result>) {
        wallpapers.clear()
        wallpapers.addAll(list)
    }

    override fun onItemClick(position: Int) {
        if (::onItemClickListener.isInitialized)
            onItemClickListener.onItemClick(position)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}