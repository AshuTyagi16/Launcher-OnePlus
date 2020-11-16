package com.sasuke.launcheroneplus.ui.launcher.recent_apps

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App

class RecentAppSectionAdapter(private val glide: RequestManager) :
    RecyclerView.Adapter<RecentAppSectionViewHolder>() {

    private lateinit var list: List<App>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentAppSectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_recent_app_section, parent, false)
        return RecentAppSectionViewHolder(view, glide)
    }

    override fun onBindViewHolder(holder: RecentAppSectionViewHolder, position: Int) {
        if (::list.isInitialized)
            holder.setRecentApps(list)
    }

    override fun getItemCount(): Int {
        if (::list.isInitialized)
            return 1
        return 0
    }

    fun setRecentApps(list: List<App>) {
        this.list = list
        notifyDataSetChanged()
    }
}