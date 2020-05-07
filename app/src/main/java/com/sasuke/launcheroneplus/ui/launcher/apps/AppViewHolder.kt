package com.sasuke.launcheroneplus.ui.launcher.apps

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.AppInfo
import kotlinx.android.synthetic.main.cell_app_info.view.*

class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var onClickListeners: OnClickListeners

    var currentVelocity = 0f

    var highlight = false

    private val highlightColor = ContextCompat.getColor(itemView.context, R.color.app_highlight)
    private val unhighlightColor =
        ContextCompat.getColor(itemView.context, R.color.app_un_highlight)

    /**
     * A [SpringAnimation] for this RecyclerView item. This animation rotates the view with a bouncy
     * spring configuration, resulting in the oscillation effect.
     *
     * The animation is started in [CheeseAdapter.onScrollListener].
     */
    val rotation: SpringAnimation = SpringAnimation(itemView, SpringAnimation.ROTATION)
        .setSpring(
            SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW)
        )
        .addUpdateListener { _, _, velocity ->
            currentVelocity = velocity
        }

    /**
     * A [SpringAnimation] for this RecyclerView item. This animation is used to bring the item back
     * after the over-scroll effect.
     */
    val translationY: SpringAnimation = SpringAnimation(itemView, SpringAnimation.TRANSLATION_Y)
        .setSpring(
            SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW)
        )

    fun setAppInfo(appInfo: AppInfo) {
        itemView.ivAppIcon.setImageDrawable(appInfo.icon)
        itemView.tvAppLabel.text = appInfo.label

        itemView.setOnLongClickListener {
            if (::onClickListeners.isInitialized)
                onClickListeners.onItemLongClick(adapterPosition, itemView, appInfo)
            return@setOnLongClickListener true
        }

        itemView.setOnClickListener {
            if (::onClickListeners.isInitialized)
                onClickListeners.onItemClick(adapterPosition, itemView, appInfo)
        }

        if (highlight)
            itemView.clApp.setBackgroundColor(highlightColor)
        else
            itemView.clApp.setBackgroundColor(unhighlightColor)
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, parent: View, appInfo: AppInfo)
        fun onItemLongClick(position: Int, parent: View, appInfo: AppInfo)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }
}