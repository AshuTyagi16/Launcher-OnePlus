package com.sasuke.launcheroneplus.ui.launcher.apps

import android.animation.Animator
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
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
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import kotlinx.android.synthetic.main.cell_app_info.view.*
import java.io.File

class AppViewHolder(
    itemView: View,
    private val glide: RequestManager,
    private var consumeLongPress: Boolean = true
) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var onClickListeners: OnClickListeners

    private val dir = itemView.context.getExternalFilesDir("app_icon")

    var currentVelocity = 0f

    private lateinit var app: App

    companion object {
        private const val LONG_PRESS_DURATION = 600L
    }

    private var isDragAllowed = false
    private var isDragStarted = false

    private val popup = createBalloon(itemView.context) {
        setArrowVisible(true)
        setArrowSize(10)
        setCircularDuration(200)
        setArrowColor(ContextCompat.getColor(itemView.context, R.color.light_grey))
        setArrowOrientation(ArrowOrientation.BOTTOM)
        setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.transparent))
        setBalloonAnimation(BalloonAnimation.OVERSHOOT)
        setLayout(R.layout.popup_app_options)
        setDismissWhenTouchOutside(true)
    }

    init {
        popup.getContentView().findViewById<LinearLayout>(R.id.ivUninstall).setOnClickListener {
            popup.dismiss()
            if (::onClickListeners.isInitialized)
                onClickListeners.onAppUninstallClick(adapterPosition, app)
        }
        popup.getContentView().findViewById<LinearLayout>(R.id.ivEdit).setOnClickListener {
            popup.dismiss()
            if (::onClickListeners.isInitialized)
                onClickListeners.onAppEditClick(adapterPosition, app)
        }
        popup.getContentView().findViewById<LinearLayout>(R.id.ivAppInfo).setOnClickListener {
            popup.dismiss()
            if (::onClickListeners.isInitialized)
                onClickListeners.onAppInfoClick(adapterPosition, app)
        }
    }

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
        popup.showAlignTop(itemView, 0, 40)
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
                    handler.postDelayed(longPressRunnable, LONG_PRESS_DURATION)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDragAllowed) {
                        popup.dismiss()
                        itemView.animate().scaleX(1f).scaleY(1f)
                            .translationY(0f)
                            .start()
                        if (::onClickListeners.isInitialized)
                            onClickListeners.onItemLongClick(adapterPosition, itemView, app)
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
                        popup.dismiss()
                        if (::onClickListeners.isInitialized)
                            onClickListeners.onItemClick(adapterPosition, itemView, appInfo)
                    }
                    isDragAllowed = false
                    isDragStarted = false
                    itemView.animate().scaleX(1f).scaleY(1f)
                        .translationY(0f)
                        .start()
                }
                MotionEvent.ACTION_CANCEL -> {
                    popup.dismiss()
                    itemView.animate().scaleX(1f).scaleY(1f)
                        .translationY(0f)
                        .start()
                    handler.removeCallbacks(longPressRunnable)
                    isDragAllowed = false
                }
            }
            return@setOnTouchListener true
        }

//        itemView.setOnLongClickListener {
//            if (::onClickListeners.isInitialized)
//                onClickListeners.onItemLongClick(adapterPosition, itemView, appInfo)
//            if (consumeLongPress) {
//                val icon = itemView.ivAppIcon
//                val state =
//                    DragData(
//                        appInfo,
//                        icon.width,
//                        icon.height
//                    )
//                val shadow = MyDragShadowBuilder(icon)
//                ViewCompat.startDragAndDrop(icon, null, shadow, state, 0)
//            }
//            return@setOnLongClickListener consumeLongPress
//        }
    }

    interface OnClickListeners {
        fun onItemClick(position: Int, parent: View, appInfo: App)
        fun onItemLongClick(position: Int, parent: View, appInfo: App)
        fun onAppInfoClick(position: Int, appInfo: App)
        fun onAppUninstallClick(position: Int, appInfo: App)
        fun onAppEditClick(position: Int, appInfo: App)
    }

    fun setOnClickListeners(onClickListeners: OnClickListeners) {
        this.onClickListeners = onClickListeners
    }
}