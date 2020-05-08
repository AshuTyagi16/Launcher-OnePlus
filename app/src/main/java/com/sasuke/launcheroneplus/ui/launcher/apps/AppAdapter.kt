package com.sasuke.launcheroneplus.ui.launcher.apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.AppInfo

class AppAdapter : RecyclerView.Adapter<AppViewHolder>(),
    FastScroller.SectionIndexer,
    AppViewHolder.OnClickListeners {

    private lateinit var appList: MutableList<AppInfo>
    private lateinit var onClickListeners: OnClickListeners

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_app_info, parent, false)
        return AppViewHolder(view).apply {
            // The rotation pivot should be at the center of the top edge.
            itemView.doOnLayout { v -> v.pivotX = v.width / 2f }
            itemView.pivotY = 0f
        }
    }

    override fun getItemCount(): Int {
        return if (::appList.isInitialized) appList.size else 0
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        if (::appList.isInitialized) {
            holder.setAppInfo(appList[position])
            holder.setOnClickListeners(this)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setApps(list: MutableList<AppInfo>) {
        this.appList = list
        notifyDataSetChanged()
    }

    override fun getSectionText(position: Int): CharSequence {
        return appList[position].label[0].toUpperCase().toString()
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, parent: View, appInfo: AppInfo)
        fun onItemLongClick(position: Int, parent: View, appInfo: AppInfo)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }

    override fun onItemClick(position: Int, parent: View, appInfo: AppInfo) {
        if (::onClickListeners.isInitialized)
            onClickListeners.onItemClick(position, parent, appInfo)
    }

    override fun onItemLongClick(position: Int, parent: View, appInfo: AppInfo) {
        if (::onClickListeners.isInitialized)
            onClickListeners.onItemLongClick(position, parent, appInfo)
    }
}