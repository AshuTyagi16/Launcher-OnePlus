package com.sasuke.launcheroneplus.ui.hidden_apps.app_selector

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.AppInfo

class AppSelectionAdapter : RecyclerView.Adapter<AppSelectionViewHolder>(),
    FastScroller.SectionIndexer,
    AppSelectionViewHolder.OnClickListeners {

    private lateinit var onClickListeners: OnClickListeners
    private lateinit var appList: MutableList<AppInfo>

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppSelectionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_app_selector, parent, false)
        return AppSelectionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (::appList.isInitialized) appList.size else 0
    }

    override fun onBindViewHolder(holder: AppSelectionViewHolder, position: Int) {
        if (::appList.isInitialized) {
            holder.setApp(appList[position])
            holder.setOnClickListeners(this)
        }
    }

    override fun onBindViewHolder(
        holder: AppSelectionViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.isNotEmpty()) {
            val flag = payloads[0]
            if (flag is Boolean) {
                holder.toggle(flag)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, appInfo: AppInfo)
    }

    fun setApps(list: MutableList<AppInfo>) {
        this.appList = list
        notifyDataSetChanged()
    }

    fun toggle(position: Int) {
        var flag = appList[position].isSelected
        flag = !flag
        appList[position].isSelected = flag
        notifyItemChanged(position, flag)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }

    override fun onItemClick(position: Int, appInfo: AppInfo) {
        if (::onClickListeners.isInitialized)
            onClickListeners.onItemClick(position, appInfo)
    }

    override fun getSectionText(position: Int): CharSequence {
        return appList[position].label[0].toUpperCase().toString()
    }
}