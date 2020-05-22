package com.sasuke.launcheroneplus.ui.color_picker

import android.graphics.Color
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.DefaultColor
import com.sasuke.launcheroneplus.util.hide
import com.sasuke.launcheroneplus.util.show
import kotlinx.android.synthetic.main.cell_default_color.view.*

class ColorViewHolder(itemView: View, private val glide: RequestManager) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onClickListeners: OnClickListeners
    private var requestOption = RequestOptions()

    init {
        requestOption = requestOption.circleCrop()
    }

    fun setColor(color: DefaultColor) {
        val drawable =
            AppCompatResources.getDrawable(itemView.context, R.drawable.scroll_accent_drawable)
        drawable?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            val parsedcolor = Color.parseColor(color.colorHex)
            DrawableCompat.setTint(
                wrappedDrawable,
                parsedcolor
            )
            glide.load(wrappedDrawable)
                .apply(requestOption)
                .into(itemView.ivColor)
        }

        itemView.setOnClickListener {
            if (::onClickListeners.isInitialized)
                onClickListeners.onItemClick(adapterPosition, color.colorHex)
        }
    }

    fun toggle(flag: Boolean) {
        if (flag)
            itemView.ivSelected.show()
        else
            itemView.ivSelected.hide()
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, color: String)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }
}