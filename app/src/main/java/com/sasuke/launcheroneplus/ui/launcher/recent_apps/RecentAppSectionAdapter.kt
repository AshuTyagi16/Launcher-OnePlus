package com.sasuke.launcheroneplus.ui.launcher.recent_apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.util.OnCustomEventListeners

class RecentAppSectionAdapter(private val glide: RequestManager) :
    RecyclerView.Adapter<RecentAppSectionViewHolder>(), OnCustomEventListeners {

    private lateinit var list: List<App>
    private lateinit var onCustomEventListeners: OnCustomEventListeners

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentAppSectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_recent_app_section, parent, false)
        return RecentAppSectionViewHolder(view, glide).apply {
            setOnCustomEventListener(this@RecentAppSectionAdapter)
        }
    }

    override fun onBindViewHolder(holder: RecentAppSectionViewHolder, position: Int) {
        if (::list.isInitialized)
            holder.setRecentApps(list)
    }

    override fun getItemCount(): Int {
        if (::list.isInitialized && list.isNotEmpty())
            return 1
        return 0
    }

    fun setRecentApps(list: List<App>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setOnCustomEventListener(onCustomEventListeners: OnCustomEventListeners) {
        this.onCustomEventListeners = onCustomEventListeners
    }

    override fun onItemClick(position: Int, parent: View, appInfo: App) {
        if (::onCustomEventListeners.isInitialized)
            onCustomEventListeners.onItemClick(position, parent, appInfo)
    }

    override fun onItemLongClick(position: Int, parent: View, appInfo: App) {
        if (::onCustomEventListeners.isInitialized)
            onCustomEventListeners.onItemLongClick(position, parent, appInfo)
    }

    override fun onDragStart(position: Int, parent: View, appInfo: App) {
        if (::onCustomEventListeners.isInitialized)
            onCustomEventListeners.onDragStart(position, parent, appInfo)
    }

    override fun onEventCancel(position: Int, appInfo: App) {
        if (::onCustomEventListeners.isInitialized)
            onCustomEventListeners.onEventCancel(position, appInfo)
    }
}