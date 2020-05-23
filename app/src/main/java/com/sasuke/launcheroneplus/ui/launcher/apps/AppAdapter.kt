package com.sasuke.launcheroneplus.ui.launcher.apps

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App

class AppAdapter(private val glide: RequestManager, private var consumeLongPress: Boolean) :
    RecyclerView.Adapter<AppViewHolder>(),
    RecyclerViewFastScroller.OnPopupViewUpdate,
    AppViewHolder.OnClickListeners {

    lateinit var appList: MutableList<App>
    private lateinit var onClickListeners: OnClickListeners

    private var primaryColor: Int = 0

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_app_info, parent, false)
        return AppViewHolder(view, glide, consumeLongPress).apply {
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
            holder.updateConsumeLongPress(consumeLongPress)
            holder.setAppInfo(appList[position])
            holder.setOnClickListeners(this)
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

    fun consumeLongPress(consumeLongPress: Boolean, shouldNotify: Boolean) {
        this.consumeLongPress = consumeLongPress
        if (shouldNotify)
            notifyDataSetChanged()
    }
}