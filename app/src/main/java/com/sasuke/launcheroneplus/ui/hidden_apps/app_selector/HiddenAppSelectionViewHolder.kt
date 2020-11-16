package com.sasuke.launcheroneplus.ui.hidden_apps.app_selector

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.util.Constants
import com.sasuke.launcheroneplus.util.getIconFolderPath
import kotlinx.android.synthetic.main.cell_app_selector.view.*
import java.io.File

class HiddenAppSelectionViewHolder(itemView: View, private val glide: RequestManager) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onClickListeners: OnClickListeners

    fun setApp(appInfo: App) {
        glide.load(File(itemView.context.getIconFolderPath(appInfo.label)))
            .into(itemView.ivAppIcon)
        itemView.tvAppLabel.text = appInfo.label

        itemView.setOnClickListener {
            if (::onClickListeners.isInitialized)
                onClickListeners.onHiddenItemClick(adapterPosition, appInfo)
        }

        if (appInfo.isHidden)
            itemView.checkbox.isChecked = true
    }

    fun toggle(checked: Boolean) {
        itemView.checkbox.isChecked = checked
    }

    interface OnClickListeners {
        fun onHiddenItemClick(position: Int, appInfo: App)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }
}