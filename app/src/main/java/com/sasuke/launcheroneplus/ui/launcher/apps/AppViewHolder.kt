package com.sasuke.launcheroneplus.ui.launcher.apps

import android.animation.Animator
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.bumptech.glide.RequestManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.data.model.DragData
import com.sasuke.launcheroneplus.ui.base.MyDragShadowBuilder
import com.sasuke.launcheroneplus.util.Constants
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import kotlinx.android.synthetic.main.cell_app_info.view.*
import timber.log.Timber
import java.io.File
import kotlin.math.abs

class AppViewHolder(
    itemView: View,
    private val glide: RequestManager
) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onClickListeners: OnClickListeners

    private val dir = itemView.context.getExternalFilesDir("app_icon")

    var currentVelocity = 0f

    private lateinit var app: App

    private var dX = 0f
    private var dY = 0f

    companion object {
        private const val LONG_PRESS_DURATION = 600L
    }

    private var isDragAllowed = false
    private var isDragStarted = false

    private val longPressRunnable = Runnable {
        isDragAllowed = true
        itemView.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .translationY(-itemView.height / 6f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {

                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {

                }

            })
            .start()
        if (::onClickListeners.isInitialized)
            onClickListeners.onItemLongClick(adapterPosition, itemView, app)
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

    fun setAppInfo(appInfo: App) {
        this.app = appInfo
        glide.load(File("$dir${File.separator}${appInfo.label.replace("[\\W]|_".toRegex(), "")}"))
            .into(itemView.ivAppIcon)
        itemView.tvAppLabel.text = appInfo.label

        itemView.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dX = event.rawX
                    dY = event.rawY
                    handler.postDelayed(longPressRunnable, LONG_PRESS_DURATION)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDragAllowed) {
                        if (abs(event.rawX - dX) > Constants.MOVE_THRESHOLD_HORIZONTAL ||
                            abs(event.rawY - dY) > Constants.MOVE_THRESHOLD_VERTICAL) {
                            itemView.animate().scaleX(1f).scaleY(1f)
                                .translationY(0f)
                                .start()
                            if (::onClickListeners.isInitialized)
                                onClickListeners.onDragStart(adapterPosition, itemView, app)
                            val icon = itemView.ivAppIcon
                            val state =
                                DragData(
                                    app,
                                    icon.width,
                                    icon.height
                                )
                            val shadow = MyDragShadowBuilder(icon)
                            ViewCompat.startDragAndDrop(icon, null, shadow, state, 0)
                            isDragStarted = true
                        }
                    }
                    isDragAllowed = false
                }
                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(longPressRunnable)
                    if (!isDragStarted && isDragAllowed) {
//                        Toast.makeText(
//                            itemView.context,
//                            "LONG PRESS WITHOUT DRAG",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    } else {
                        if (event.eventTime - event.downTime < 200) {
                            if (::onClickListeners.isInitialized)
                                onClickListeners.onItemClick(adapterPosition, itemView, appInfo)
                        }
                    }
                    isDragAllowed = false
                    isDragStarted = false
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