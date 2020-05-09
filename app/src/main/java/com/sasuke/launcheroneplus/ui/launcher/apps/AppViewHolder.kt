package com.sasuke.launcheroneplus.ui.launcher.apps

import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.data.model.AppInfo
import com.sasuke.launcheroneplus.data.model.DragData
import com.sasuke.launcheroneplus.ui.base.MyDragShadowBuilder
import kotlinx.android.synthetic.main.cell_app_info.view.*
import java.io.File

class AppViewHolder(
    itemView: View,
    private val glide: RequestManager,
    private val consumeLongPress: Boolean = true
) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onClickListeners: OnClickListeners

    private val dir = itemView.context.getExternalFilesDir("app_icon")

    var currentVelocity = 0f

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

    fun setAppInfo(appInfo: App) {
        glide.load(File("$dir${File.separator}${appInfo.label}"))
            .into(itemView.ivAppIcon)
        itemView.tvAppLabel.text = appInfo.label

        itemView.setOnClickListener {
            if (::onClickListeners.isInitialized)
                onClickListeners.onItemClick(adapterPosition, itemView, appInfo)
        }

        itemView.setOnLongClickListener {
            if (::onClickListeners.isInitialized)
                onClickListeners.onItemLongClick(adapterPosition, itemView, appInfo)
            if (consumeLongPress) {
                val icon = itemView.ivAppIcon
                val state =
                    DragData(
                        appInfo,
                        icon.width,
                        icon.height
                    )
                val shadow = MyDragShadowBuilder(icon)
                ViewCompat.startDragAndDrop(icon, null, shadow, state, 0)
            }
            return@setOnLongClickListener consumeLongPress
        }
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, parent: View, appInfo: App)
        fun onItemLongClick(position: Int, parent: View, appInfo: App)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }
}