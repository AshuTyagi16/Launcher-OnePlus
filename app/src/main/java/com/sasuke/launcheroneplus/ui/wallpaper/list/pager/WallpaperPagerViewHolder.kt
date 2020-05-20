package com.sasuke.launcheroneplus.ui.wallpaper.list.pager

import android.graphics.Bitmap
import android.view.View
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sasuke.launcheroneplus.data.model.Result
import com.sasuke.launcheroneplus.util.dpToPx
import kotlinx.android.synthetic.main.cell_wallpaper_pager.view.*

class WallpaperPagerViewHolder(itemView: View, private val glide: RequestManager) :
    RecyclerView.ViewHolder(itemView) {

    private var requestOptions = RequestOptions()

    init {
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(8.dpToPx()))
    }

    fun setWallpaper(result: Result) {
        glide
            .asBitmap()
            .load(result.urls.regular)
            .apply(requestOptions)
            .into(itemView.ivWallpaper)
    }
}