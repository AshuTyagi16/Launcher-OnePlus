package com.sasuke.launcheroneplus.ui.hidden_apps.app_selector

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sasuke.launcheroneplus.data.AppInfo
import kotlinx.android.synthetic.main.cell_app_selector.view.*

class AppSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var onClickListeners: OnClickListeners

    fun setApp(appInfo: AppInfo) {
        itemView.ivAppIcon.setImageDrawable(appInfo.icon)
        itemView.tvAppLabel.text = appInfo.label

        itemView.setOnClickListener {
            if (::onClickListeners.isInitialized)
                onClickListeners.onItemClick(adapterPosition, appInfo)
        }
    }

    fun toggle(checked: Boolean) {
        itemView.checkbox.isChecked = checked
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, appInfo: AppInfo)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }
}