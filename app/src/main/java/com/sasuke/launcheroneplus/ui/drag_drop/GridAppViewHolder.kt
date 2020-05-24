package com.sasuke.launcheroneplus.ui.drag_drop

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.data.model.App
import kotlinx.android.synthetic.main.cell_app_info.view.*
import java.io.File

class GridAppViewHolder(
    itemView: View,
    private val glide: RequestManager
) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onClickListeners: OnClickListeners

    private val dir = itemView.context.getExternalFilesDir("app_icon")

    fun setAppInfo(appInfo: App) {
        glide.load(File("$dir${File.separator}${appInfo.label.replace("[\\W]|_".toRegex(), "")}"))
            .into(itemView.ivAppIcon)
        itemView.tvAppLabel.text = appInfo.label

        itemView.setOnClickListener {
            if (::onClickListeners.isInitialized)
                onClickListeners.onItemClick(adapterPosition, itemView, appInfo)
        }

        itemView.setOnLongClickListener {
            return@setOnLongClickListener false
        }
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, parent: View, appInfo: App)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }
}