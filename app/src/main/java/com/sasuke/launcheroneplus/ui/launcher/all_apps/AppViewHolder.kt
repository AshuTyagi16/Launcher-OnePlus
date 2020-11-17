package com.sasuke.launcheroneplus.ui.launcher.all_apps

import android.annotation.SuppressLint
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.ui.base.BaseViewHolder
import com.sasuke.launcheroneplus.util.OnCustomEventListeners
import com.sasuke.launcheroneplus.util.getIconFolderPath
import kotlinx.android.synthetic.main.cell_app_info.view.*
import java.io.File

class AppViewHolder(
    itemView: View,
    private val glide: RequestManager
) :
    BaseViewHolder(itemView) {

    private lateinit var onCustomEventListeners: OnCustomEventListeners

    private lateinit var app: App

    private val generator = ColorGenerator.MATERIAL
    private val textDrawableBuilder = TextDrawable.builder().beginConfig().bold().endConfig()

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
            .start()
        if (::onCustomEventListeners.isInitialized)
            onCustomEventListeners.onItemLongClick(bindingAdapterPosition, itemView, app)
    }

    private val handler = Handler()

    @SuppressLint("ClickableViewAccessibility")
    fun setAppInfo(appInfo: App) {
        this.app = appInfo
        glide.load(File(itemView.context.getIconFolderPath(appInfo.label)))
            .placeholder(
                textDrawableBuilder.buildRound(
                    appInfo.label[0].toString(),
                    generator.getColor(appInfo.label)
                )
            )
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
                        if (::onCustomEventListeners.isInitialized)
                            onCustomEventListeners.onDragStart(
                                bindingAdapterPosition,
                                itemView,
                                app
                            )
//                        val icon = itemView.ivAppIcon
//                        val state =
//                            DragData(
//                                app,
//                                icon.width,
//                                icon.height
//                            )
//                        val shadow = MyDragShadowBuilder(icon)
//                        ViewCompat.startDragAndDrop(icon, null, shadow, state, 0)
                    }
                    isDragAllowed = false
                }
                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(longPressRunnable)
                    if (!isDragAllowed) {
                        if (event.eventTime - event.downTime < LONG_PRESS_DURATION) {
                            if (::onCustomEventListeners.isInitialized)
                                onCustomEventListeners.onItemClick(
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
                    if (::onCustomEventListeners.isInitialized)
                        onCustomEventListeners.onEventCancel(bindingAdapterPosition, app)
                    handler.removeCallbacks(longPressRunnable)
                    isDragAllowed = false
                }
            }
            return@setOnTouchListener true
        }
    }

    fun setOnCustomEventListeners(onCustomEventListeners: OnCustomEventListeners) {
        this.onCustomEventListeners = onCustomEventListeners
    }
}