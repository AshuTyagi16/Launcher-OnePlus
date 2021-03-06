package com.sasuke.launcheroneplus.ui.launcher.recent_apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.ui.launcher.all_apps.AppViewHolder
import com.sasuke.launcheroneplus.util.OnCustomEventListeners

class RecentAppAdapter(private val glide: RequestManager) :
    ListAdapter<App, AppViewHolder>(appItemDiffCallback), OnCustomEventListeners {

    private lateinit var onClickListeners: OnCustomEventListeners

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_app_info, parent, false)
        return AppViewHolder(view, glide).apply {
            // The rotation pivot should be at the center of the top edge.
            itemView.doOnLayout { v -> v.pivotX = v.width / 2f }
            itemView.pivotY = 0f
            setOnCustomEventListeners(this@RecentAppAdapter)
        }
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setAppInfo(it)
        }
    }

    fun setOnClickListeners(onClickListeners: OnCustomEventListeners) {
        this.onClickListeners = onClickListeners
    }

    override fun onItemClick(position: Int, parent: View, appInfo: App) {
        if (::onClickListeners.isInitialized)
            onClickListeners.onItemClick(position, parent, appInfo)
    }

    override fun onItemLongClick(position: Int, parent: View, appInfo: App) {
        if (::onClickListeners.isInitialized)
            onClickListeners.onItemLongClick(position, parent, appInfo)
    }

    override fun onDragStart(position: Int, parent: View, appInfo: App) {
        if (::onClickListeners.isInitialized)
            onClickListeners.onDragStart(position, parent, appInfo)
    }

    override fun onEventCancel(position: Int, appInfo: App) {
        if (::onClickListeners.isInitialized)
            onClickListeners.onEventCancel(position, appInfo)
    }
}

private val appItemDiffCallback = object : DiffUtil.ItemCallback<App>() {
    override fun areItemsTheSame(oldItem: App, newItem: App): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: App, newItem: App): Boolean {
        return oldItem == newItem
    }
}