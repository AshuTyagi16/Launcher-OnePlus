package com.sasuke.launcheroneplus.ui.settings

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.LauncherApp
import com.sasuke.launcheroneplus.data.model.Setting
import com.sasuke.launcheroneplus.util.updateTint
import kotlinx.android.synthetic.main.cell_setting.view.*

class LauncherSettingViewHolder(itemView: View, private val glide: RequestManager) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onItemClickListener: OnItemClickListener

    fun setSettingInfo(setting: Setting) {
        if (LauncherApp.color != 0) {
            AppCompatResources.getDrawable(itemView.context, setting.icon)?.let {
                it.updateTint(LauncherApp.color)
                glide
                    .load(it)
                    .into(itemView.ivIcon)
            }
        } else {
            glide
                .load(setting.icon)
                .into(itemView.ivIcon)
        }
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