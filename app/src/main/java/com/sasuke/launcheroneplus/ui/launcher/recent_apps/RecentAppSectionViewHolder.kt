package com.sasuke.launcheroneplus.ui.launcher.recent_apps

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.data.model.DrawerStyle
import com.sasuke.launcheroneplus.ui.base.BaseViewHolder
import com.sasuke.launcheroneplus.util.Constants
import com.sasuke.launcheroneplus.util.OnCustomEventListeners
import com.sasuke.launcheroneplus.util.hide
import com.sasuke.launcheroneplus.util.show
import kotlinx.android.synthetic.main.cell_recent_app_section.view.*

class RecentAppSectionViewHolder(
    itemView: View,
    private val glide: RequestManager,
    private val drawerStyle: DrawerStyle
) :
    BaseViewHolder(itemView), OnCustomEventListeners {

    private lateinit var recentAppAdapter: RecentAppAdapter
    private lateinit var onCustomEventListeners: OnCustomEventListeners

    fun setRecentApps(list: List<App>) {
        when (drawerStyle) {
            DrawerStyle.VERTICAL -> {
                itemView.separator.show()
            }
            DrawerStyle.LIST -> {
                itemView.separator.hide()
            }
        }
        itemView.rvRecentApps.layoutManager =
            GridLayoutManager(
                itemView.context,
                Constants.APP_LIST_SPAN_COUNT,
                RecyclerView.VERTICAL,
                false
            )
        recentAppAdapter = RecentAppAdapter(glide)
        recentAppAdapter.setOnClickListeners(this)
        itemView.rvRecentApps.adapter = recentAppAdapter
        recentAppAdapter.submitList(list)
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