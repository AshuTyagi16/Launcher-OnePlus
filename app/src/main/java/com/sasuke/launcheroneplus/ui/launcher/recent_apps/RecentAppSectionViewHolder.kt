package com.sasuke.launcheroneplus.ui.launcher.recent_apps

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.ui.base.BaseViewHolder
import com.sasuke.launcheroneplus.util.Constants
import kotlinx.android.synthetic.main.cell_recent_app_section.view.*

class RecentAppSectionViewHolder(itemView: View, private val glide: RequestManager) :
    BaseViewHolder(itemView) {

    private lateinit var recentAppAdapter: RecentAppAdapter

    fun setRecentApps(list: List<App>) {
        itemView.rvRecentApps.layoutManager =
            GridLayoutManager(
                itemView.context,
                Constants.APP_LIST_SPAN_COUNT,
                RecyclerView.VERTICAL,
                false
            )
        recentAppAdapter = RecentAppAdapter(glide)
        itemView.rvRecentApps.adapter = recentAppAdapter
        recentAppAdapter.submitList(list)
    }
}