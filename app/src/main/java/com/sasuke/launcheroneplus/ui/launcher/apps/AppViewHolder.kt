package com.sasuke.launcheroneplus.ui.launcher.apps

import android.annotation.SuppressLint
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.data.model.DragData
import com.sasuke.launcheroneplus.ui.base.MyDragShadowBuilder
import com.sasuke.launcheroneplus.util.getIconFolderPath
import kotlinx.android.synthetic.main.cell_app_info.view.*
import java.io.File

class AppViewHolder(
    itemView: View,
    private val glide: RequestManager
) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onClickListeners: OnClickListeners

    var currentVelocity = 0f

    private lateinit var app: App

    companion object {
        private const val LONG_PRESS_DURATION = 200L
    }

    private var isDragAllowed = false

    private val longPressRunnable = Runnable {
        isDragAllowed = true
        itemView.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .translationY(-itemView.height / 6f)
            .withEndAction {
                if (::onClickListeners.isInitialized)
                    onClickListeners.onItemLongClick(bindingAdapterPosition, itemView, app)
            }
            .start()
    }

    private val handler = Handler()

    /**
     * A [SpringAnimation] for this RecyclerView item. This animation rotates the view with a bouncy
     * spring configuration, resulting in the oscillation effect.
     *
     * The animation is started in [Recyclerview.onScrollListener].
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

    @SuppressLint("ClickableViewAccessibility")
    fun setAppInfo(appInfo: App) {
        this.app = appInfo
        glide.load(File(itemView.context.getIconFolderPath(appInfo.label)))
            .into(itemView.ivAppIcon)
        itemView.tvAppLabel.text = appInfo.label

        itemView.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    handler.postDelayed(longPressRunnable, LONG_PRESS_DURATION)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDragAllowed) {
                        itemView.animate().scaleX(1f).scaleY(1f)
                            .translationY(0f)
                            .start()
                        if (::onClickListeners.isInitialized)
                            onClickListeners.onDragStart(bindingAdapterPosition, itemView, app)
                        val icon = itemView.ivAppIcon
                        val state =
                            DragData(
                                app,
                                icon.width,
                                icon.height
                            )
                        val shadow = MyDragShadowBuilder(icon)
                        ViewCompat.startDragAndDrop(icon, null, shadow, state, 0)
                    }
                    isDragAllowed = false
                }
                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(longPressRunnable)
                    if (!isDragAllowed) {
                        if (event.eventTime - event.downTime < LONG_PRESS_DURATION) {
                            if (::onClickListeners.isInitialized)
                                onClickListeners.onItemClick(
                                    bindingAdapterPosition,
                                    itemView,
                                    appInfo
                                )
                        }
                    }
                    isDragAllowed = false
                    itemView.animate().scaleX(1f).scaleY(1f)
                        .translationY(0f)
                        .start()
                }
                MotionEvent.ACTION_CANCEL -> {
                    itemView.animate().scaleX(1f).scaleY(1f)
                        .translationY(0f)
                        .start()
                    handler.removeCallbacks(longPressRunnable)
                    isDragAllowed = false
                }
            }
            return@setOnTouchListener true
        }
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, parent: View, appInfo: App)
        fun onItemLongClick(position: Int, parent: View, appInfo: App)
        fun onDragStart(position: Int, parent: View, appInfo: App)
        fun onEventCancel(position: Int, appInfo: App)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }
}