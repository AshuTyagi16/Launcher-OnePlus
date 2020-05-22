package com.sasuke.launcheroneplus.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Setting

class LauncherSettingAdapter(private val glide: RequestManager) :
    RecyclerView.Adapter<LauncherSettingViewHolder>(),
    LauncherSettingViewHolder.OnItemClickListener {

    private lateinit var settings: List<Setting>
    private lateinit var onItemClickListener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LauncherSettingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_setting, parent, false)
        return LauncherSettingViewHolder(view, glide)
    }

    override fun getItemCount(): Int {
        return if (::settings.isInitialized) settings.size else 0
    }

    override fun onBindViewHolder(holder: LauncherSettingViewHolder, position: Int) {
        if (::settings.isInitialized) {
            holder.setSettingInfo(settings[position])
            holder.setOnItemClickListener(this)
        }
    }

    fun setSettings(list: List<Setting>) {
        this.settings = list
        notifyDataSetChanged()
    }

    override fun onItemClick(setting: Setting) {
        if (::onItemClickListener.isInitialized)
            onItemClickListener.onItemClick(setting)
    }

    interface OnItemClickListener {
        fun onItemClick(setting: Setting)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}