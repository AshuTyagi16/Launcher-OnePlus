package com.sasuke.launcheroneplus.ui.launcher.all_apps

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App

class AppAdapter(private val glide: RequestManager) :
    RecyclerView.Adapter<AppViewHolder>(),
    RecyclerViewFastScroller.OnPopupViewUpdate,
    AppViewHolder.OnClickListeners {

    init {
        setHasStableIds(true)
    }

    lateinit var appList: MutableList<App>
    private lateinit var onClickListeners: OnClickListeners

    private var primaryColor: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_app_info, parent, false)
        return AppViewHolder(view, glide).apply {
            // The rotation pivot should be at the center of the top edge.
            itemView.doOnLayout { v -> v.pivotX = v.width / 2f }
            itemView.pivotY = 0f
            setOnClickListeners(this@AppAdapter)
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setApps(list: MutableList<App>) {
        this.appList = list
        notifyDataSetChanged()
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, parent: View, appInfo: App)
        fun onItemLongClick(position: Int, parent: View, appInfo: App)
        fun onDragStarted(position: Int, parent: View, appInfo: App)
        fun onEventCancel(position: Int, appInfo: App)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
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
            onClickListeners.onDragStarted(position, parent, appInfo)
    }

    override fun onEventCancel(position: Int, appInfo: App) {
        if (::onClickListeners.isInitialized)
            onClickListeners.onEventCancel(position, appInfo)
    }

    override fun onUpdate(position: Int, popupTextView: TextView) {
        popupTextView.background.colorFilter = PorterDuffColorFilter(
            primaryColor,
            PorterDuff.Mode.SRC_IN
        )
        popupTextView.text = appList[position].label[0].toUpperCase().toString()
    }

    fun updatePrimaryColor(color: Int) {
        primaryColor = color
    }
}