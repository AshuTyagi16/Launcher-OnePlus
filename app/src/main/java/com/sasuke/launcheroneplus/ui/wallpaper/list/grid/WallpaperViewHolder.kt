package com.sasuke.launcheroneplus.ui.wallpaper.list.grid

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Result
import com.sasuke.launcheroneplus.util.dpToPx
import kotlinx.android.synthetic.main.cell_wallpaper.view.*

class WallpaperViewHolder(itemView: View, private val glide: RequestManager) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onItemClickListener: OnItemClickListener
    private var requestOptions = RequestOptions()

    init {
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(8.dpToPx()))
    }

    fun setWallpaper(result: Result) {
        glide.load(result.urls.small)
            .placeholder(R.drawable.placeholder_small_image)
            .apply(requestOptions)
            .into(itemView.ivWallpaper)

        itemView.setOnClickListener {
            if (::onItemClickListener.isInitialized)
                onItemClickListener.onItemClick(adapterPosition, itemView.ivWallpaper)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, imageView: ImageView)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}