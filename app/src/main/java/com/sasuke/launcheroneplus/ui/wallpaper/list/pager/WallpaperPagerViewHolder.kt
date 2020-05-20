package com.sasuke.launcheroneplus.ui.wallpaper.list.pager

import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sasuke.launcheroneplus.data.model.Result
import com.sasuke.launcheroneplus.util.dpToPx
import kotlinx.android.synthetic.main.cell_wallpaper_pager.view.*

class WallpaperPagerViewHolder(itemView: View, private val glide: RequestManager) :
    RecyclerView.ViewHolder(itemView) {

    private var requestOptions = RequestOptions()

    init {
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(8.dpToPx()))
        ViewCompat.setTransitionName(itemView.ivWallpaper, adapterPosition.toString())
    }

    private lateinit var onItemListener: OnItemListener

    fun setWallpaper(result: Result) {
        glide
            .asBitmap()
            .load(result.urls.regular)
            .apply(requestOptions)
            .into(itemView.ivWallpaper)

        itemView.setOnClickListener {
            if (::onItemListener.isInitialized)
                onItemListener.onItemClick(adapterPosition, result, itemView.ivWallpaper)
        }
    }

    interface OnItemListener {
        fun onItemClick(position: Int, result: Result, imageView: ImageView)
    }

    fun setOnItemListener(onItemListener: OnItemListener) {
        this.onItemListener = onItemListener
    }
}