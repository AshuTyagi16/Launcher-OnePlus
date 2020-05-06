package com.sasuke.launcheroneplus.ui.launcher.apps

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.AppInfo

class AppAdapter : RecyclerView.Adapter<AppViewHolder>(), FastScroller.SectionIndexer {

    private lateinit var appList: MutableList<AppInfo>

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
        }
    }

    fun setApps(list: MutableList<AppInfo>) {
        this.appList = list
        notifyDataSetChanged()
    }

    override fun getSectionText(position: Int): CharSequence {
        return appList[position].label[0].toString()
    }
}