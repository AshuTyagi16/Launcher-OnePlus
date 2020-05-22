package com.sasuke.launcheroneplus.ui.settings

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.data.model.Setting
import kotlinx.android.synthetic.main.cell_setting.view.*

class LauncherSettingViewHolder(itemView: View, private val glide: RequestManager) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onItemClickListener: OnItemClickListener

    fun setSettingInfo(setting: Setting) {
        glide
            .load(setting.icon)
            .into(itemView.ivIcon)
        itemView.tvSettingTitle.text = setting.title
        itemView.tvSettingDescription.text = setting.description

        itemView.setOnClickListener {
            if (::onItemClickListener.isInitialized)
                onItemClickListener.onItemClick(setting)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(setting: Setting)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}