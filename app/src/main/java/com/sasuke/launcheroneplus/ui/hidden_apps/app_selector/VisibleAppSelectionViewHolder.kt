package com.sasuke.launcheroneplus.ui.hidden_apps.app_selector

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.data.model.App
import kotlinx.android.synthetic.main.cell_app_selector.view.*
import java.io.File

class VisibleAppSelectionViewHolder(itemView: View, private val glide: RequestManager) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onClickListeners: OnClickListeners
    private val dir = itemView.context.getExternalFilesDir("app_icon")

    fun setApp(appInfo: App) {
        glide.load(File("$dir${File.separator}${appInfo.label}"))
            .into(itemView.ivAppIcon)
        itemView.tvAppLabel.text = appInfo.label

        itemView.setOnClickListener {
            if (::onClickListeners.isInitialized)
                onClickListeners.onVisibleItemClick(adapterPosition, appInfo)
        }
    }

    fun toggle(checked: Boolean) {
        itemView.checkbox.isChecked = checked
    }

    interface OnClickListeners {
        fun onVisibleItemClick(position: Int, appInfo: App)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }
}