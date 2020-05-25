package com.sasuke.launcheroneplus.ui.launcher

import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.DragEvent
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.EdgeEffect
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.github.nisrulz.sensey.PinchScaleDetector
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.TouchTypeDetector
import com.huxq17.handygridview.HandyGridView
import com.huxq17.handygridview.listener.OnItemCapturedListener
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.sasuke.launcheroneplus.LauncherApp
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.data.model.DragData
import com.sasuke.launcheroneplus.data.model.SettingPreference
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.ui.base.SnapToBlock
import com.sasuke.launcheroneplus.ui.drag_drop.GridViewAdapter
import com.sasuke.launcheroneplus.ui.hidden_apps.HiddenAppsActivity
import com.sasuke.launcheroneplus.ui.launcher.apps.AppAdapter
import com.sasuke.launcheroneplus.ui.launcher.apps.AppViewHolder
import com.sasuke.launcheroneplus.ui.settings.LauncherSettingsActivity
import com.sasuke.launcheroneplus.ui.wallpaper.list.grid.WallpaperGridActivity
import com.sasuke.launcheroneplus.util.*
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_launcher.*
import kotlinx.android.synthetic.main.layout_non_sliding.*
import kotlinx.android.synthetic.main.layout_sliding.*
import java.util.concurrent.Executor
import javax.inject.Inject

class LauncherActivity : BaseActivity(), AppAdapter.OnClickListeners,
    GridViewAdapter.OnClickListeners {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var adapter: AppAdapter

    @Inject
    lateinit var layoutManager: GridLayoutManager

    @Inject
    lateinit var itemDecoration: ItemDecorator

    @Inject
    lateinit var gridAdapter: GridViewAdapter

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var sharedPreferencesSettingsLiveData: SharedPreferencesSettingsLiveData

    @Inject
    lateinit var pagerSnapHelper: SnapToBlock

    private lateinit var launcherActivityViewModel: LauncherActivityViewModel

    private lateinit var handler: Handler

    private lateinit var keyboardTriggerBehavior: KeyboardTriggerBehavior

    private lateinit var pinchScaleListener: PinchScaleDetector.PinchScaleListener
    private lateinit var touchTypeListener: TouchTypeDetector.TouchTypListener

    private lateinit var executor: Executor

    private lateinit var biometricPrompt: BiometricPrompt

    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var primaryColor = 0

    private lateinit var popup: Balloon

    companion object {
        /** The magnitude of rotation while the list is scrolled. */
        private const val SCROLL_ROTATION_MAGNITUDE = 0.25f

        /** The magnitude of rotation while the list is over-scrolled. */
        private const val OVERSCROLL_ROTATION_MAGNITUDE = -10

        /** The magnitude of translation distance while the list is over-scrolled. */
        private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.2f

        /** The magnitude of translation distance when the list reaches the edge on fling. */
        private const val FLING_TRANSLATION_MAGNITUDE = 0.5f

        private const val REQUEST_CODE_ADMIN_RIGHTS = 567

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        inject()
        setWindowInsets()
        setupRecyclerView()
        setupGridView()
        setupListeners()
        observeLiveData()
        initBiometric()
    }

    private fun inject() {
        launcherActivityViewModel =
            ViewModelProvider(this, viewModelFactory).get(LauncherActivityViewModel::class.java)
        Sensey.getInstance().init(this)
        handler = Handler()
        primaryColor = LauncherApp.color
    }

    private fun setWindowInsets() {
        clParent.setOnApplyWindowInsetsListener { v, insets ->
            v.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        rvHideApps.layoutManager = layoutManager
        rvHideApps.addItemDecoration(itemDecoration)
        rvHideApps.adapter = adapter
        adapter.updatePrimaryColor(primaryColor)
        adapter.setOnClickListeners(this)

        fastscroller.setHandleStateListener(object : RecyclerViewFastScroller.HandleStateListener {
            override fun onDragged(offset: Float, postion: Int) {
                super.onDragged(offset, postion)
                rvHideApps.forEachVisibleHolder { holder: AppViewHolder ->
                    if (adapter.appList[postion].label[0].toUpperCase() == adapter.appList[holder.adapterPosition].label[0].toUpperCase()) {
                        AppCompatResources.getDrawable(
                            this@LauncherActivity,
                            R.drawable.bg_app_highlight
                        )?.let {
                            val wrappedDrawable = DrawableCompat.wrap(it)
                            DrawableCompat.setTint(
                                wrappedDrawable,
                                ColorUtils.setAlphaComponent(primaryColor, 90)
                            )
                            holder.itemView.background = wrappedDrawable
                        }
                    } else
                        holder.itemView.setBackgroundColor(
                            Color.TRANSPARENT
                        )
                }

            }

        })

        rvHideApps.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {
                val edgeEffect = object : EdgeEffect(recyclerView.context) {

                    override fun onPull(deltaDistance: Float) {
                        super.onPull(deltaDistance)
                        handlePull(deltaDistance)
                    }

                    override fun onPull(deltaDistance: Float, displacement: Float) {
                        super.onPull(deltaDistance, displacement)
                        handlePull(deltaDistance)
                    }

                    private fun handlePull(deltaDistance: Float) {
                        // This is called on every touch event while the list is scrolled with a finger.
                        // We simply update the view properties without animation.
                        val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                        val rotationDelta = sign * deltaDistance * OVERSCROLL_ROTATION_MAGNITUDE
                        val translationYDelta =
                            sign * recyclerView.width * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                        recyclerView.forEachVisibleHolder { holder: AppViewHolder ->
                            if (layoutManager.orientation == RecyclerView.VERTICAL) {
                                holder.rotation.cancel()
                                holder.translationY.cancel()
                                holder.itemView.rotation += rotationDelta
                                holder.itemView.translationY += translationYDelta
                            }
                        }
                    }

                    override fun onRelease() {
                        super.onRelease()
                        // The finger is lifted. This is when we should start the animations to bring
                        // the view property values back to their resting states.
                        recyclerView.forEachVisibleHolder { holder: AppViewHolder ->
                            if (layoutManager.orientation == RecyclerView.VERTICAL) {
                                holder.rotation.start()
                                holder.translationY.start()
                            }
                        }
                    }

                    override fun onAbsorb(velocity: Int) {
                        super.onAbsorb(velocity)
                        val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                        // The list has reached the edge on fling.
                        val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                        if (layoutManager.orientation == RecyclerView.VERTICAL) {
                            recyclerView.forEachVisibleHolder { holder: AppViewHolder ->
                                holder.translationY
                                    .setStartVelocity(translationVelocity)
                                    .start()
                            }
                        }
                    }
                }
                edgeEffect.color = Color.TRANSPARENT
                return edgeEffect
            }
        }

    }

    private fun setupGridView() {
        gridApps.adapter = gridAdapter
        gridAdapter.setOnClickListeners(this)
        setMode(HandyGridView.MODE.LONG_PRESS)
        gridApps.setAutoOptimize(true)
        gridApps.setScrollSpeed(750)

        gridApps.setOnItemLongClickListener { _, _, i, _ ->
            if (!gridApps.isTouchMode && !gridApps.isNoneMode && !gridAdapter.isFixed(i)) {//long press enter edit mode.
                setMode(HandyGridView.MODE.TOUCH)
                return@setOnItemLongClickListener true
            }
            return@setOnItemLongClickListener false
        }

        gridApps.setOnItemCapturedListener(object : OnItemCapturedListener {
            override fun onItemReleased(v: View?, position: Int) {
                v?.scaleX = 1f
                v?.scaleY = 1f
                setMode(HandyGridView.MODE.LONG_PRESS)
            }

            override fun onItemCaptured(v: View?, position: Int) {
                v?.scaleX = 1.2f
                v?.scaleY = 1.2f
            }
        })
    }

    private fun setupListeners() {
        keyboardTriggerBehavior = KeyboardTriggerBehavior(this).apply {
            observe(this@LauncherActivity, Observer {
                when (it) {
                    KeyboardTriggerBehavior.Status.OPEN -> keyboardOpen()
                    KeyboardTriggerBehavior.Status.CLOSED -> keyboardClosed()
                }
            })
        }

        touchTypeListener = object : TouchTypeDetector.TouchTypListener {
            override fun onDoubleTap() {
                startActivity(LauncherSettingsActivity.newIntent(this@LauncherActivity))
            }

            override fun onSwipe(p0: Int) {

            }

            override fun onSingleTap() {

            }

            override fun onScroll(scrollDirection: Int) {
                when (scrollDirection) {
                    TouchTypeDetector.SCROLL_DIR_UP -> {
                        handler.postDelayed({
                            clParent.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
                        }, 50)
                    }
                    TouchTypeDetector.SCROLL_DIR_DOWN -> {
                        if (clParent.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                            openStatusBar()
                    }
                }
            }

            override fun onLongPress() {

            }

            override fun onThreeFingerSingleTap() {

            }

            override fun onTwoFingerSingleTap() {

            }

        }

        pinchScaleListener = object : PinchScaleDetector.PinchScaleListener {
            override fun onScaleEnd(p0: ScaleGestureDetector?) {
                if (::touchTypeListener.isInitialized) {
                    Sensey.getInstance()
                        .startTouchTypeDetection(this@LauncherActivity, touchTypeListener)
                }
            }

            override fun onScale(p0: ScaleGestureDetector?, isScalingOut: Boolean) {
                if (!isScalingOut) {
                    biometricPrompt.authenticate(promptInfo)
                }
            }

            override fun onScaleStart(p0: ScaleGestureDetector?) {
                Sensey.getInstance().stopTouchTypeDetection()
            }

        }

        Sensey.getInstance().startTouchTypeDetection(this, touchTypeListener)
        Sensey.getInstance().startPinchScaleDetection(this@LauncherActivity, pinchScaleListener)

        clParent.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                ivDragIcon.alpha = 1 - slideOffset
                clSearchBar.alpha = slideOffset
                gridApps.alpha = 1 - slideOffset
                clWallpaperIcon.alpha = 1 - slideOffset
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                when (newState) {
                    SlidingUpPanelLayout.PanelState.EXPANDED -> {
                        Sensey.getInstance().stopTouchTypeDetection()
                        Sensey.getInstance().stopPinchScaleDetection()
                    }
                    SlidingUpPanelLayout.PanelState.DRAGGING -> {
                        rvHideApps.forEachVisibleHolder { holder: AppViewHolder ->

                            holder.itemView.setBackgroundColor(
                                ContextCompat.getColor(
                                    this@LauncherActivity,
                                    R.color.app_un_highlight
                                )
                            )
                        }

                    }
                    SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                        Sensey.getInstance().startTouchTypeDetection(
                            this@LauncherActivity,
                            touchTypeListener
                        )
                        Sensey.getInstance()
                            .startPinchScaleDetection(this@LauncherActivity, pinchScaleListener)
                        hideKeyboard()
                    }
                }
            }
        })

        rvHideApps.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (layoutManager.orientation == RecyclerView.VERTICAL) {
                    if (layoutManager.findFirstCompletelyVisibleItemPosition() >= Constants.APP_LIST_SPAN_COUNT)
                        ivSeparator.visibility = View.GONE
                    else
                        ivSeparator.visibility = View.VISIBLE
                }

                recyclerView.forEachVisibleHolder { holder: AppViewHolder ->

                    holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(
                            this@LauncherActivity,
                            R.color.app_un_highlight
                        )
                    )

                    if (layoutManager.orientation == RecyclerView.VERTICAL) {
                        holder.rotation
                            // Update the velocity.
                            // The velocity is calculated by the horizontal scroll offset.
                            .setStartVelocity(holder.currentVelocity - dx * SCROLL_ROTATION_MAGNITUDE)
                            // Start the animation. This does nothing if the animation is already running.
                            .start()
                    }
                }
            }
        })

        etSearch.addTextChangedListener {
            it?.let {
                launcherActivityViewModel.filterApps(it.toString())
            }
        }

        dragView.setOnDragListener { _, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    clInnerParent.animate().scaleX(0.7f).scaleY(0.7f).start()
                    clInnerParent.background = ContextCompat.getDrawable(this, R.drawable.shadow)
                    clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                }
                DragEvent.ACTION_DRAG_EXITED -> {

                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    clInnerParent.background = null
                    clInnerParent.animate().scaleX(1f).scaleY(1f).start()
                }
                DragEvent.ACTION_DROP -> {
                    val item = dragEvent.localState as DragData
                    gridAdapter.addItem(item.item)
                    showToast(getString(R.string.shortcut_added_to_home_screen))
                    dragView.visibility = View.GONE
                }
                else -> {

                }
            }
            return@setOnDragListener true
        }

        clWallpaperIcon.setOnClickListener {
            startActivity(WallpaperGridActivity.newIntent(this@LauncherActivity))
        }
    }

    private fun observeLiveData() {

        launcherActivityViewModel.appList.observe(this, Observer {
            it?.let {
                adapter.setApps(it)
            }
        })

        launcherActivityViewModel.filterAppsLiveData.observe(this, Observer {
            it?.let {
                adapter.setApps(it)
            }
        })

        sharedPreferencesSettingsLiveData.observe(this, Observer {
            it?.let {
                updateUI(it)
            }
        })
    }

    private fun setMode(mode: HandyGridView.MODE) {
        gridApps.mode = mode
        gridAdapter.setInEditMode(mode == HandyGridView.MODE.TOUCH)
    }

    private fun keyboardOpen() {
        etSearch.animate()
            .x(0f)
            .setDuration(100)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    etSearch.apply {
                        compoundDrawablePadding = 40
                        AppCompatResources.getDrawable(
                            this@LauncherActivity,
                            R.drawable.ic_back_white
                        )?.let {
                            val wrappedDrawable = DrawableCompat.wrap(it)
                            DrawableCompat.setTint(
                                wrappedDrawable,
                                primaryColor
                            )
                            setCompoundDrawablesWithIntrinsicBounds(
                                wrappedDrawable,
                                null,
                                null,
                                null
                            )
                        }
                    }
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {

                }

            })
    }

    private fun keyboardClosed() {
        etSearch.animate()
            .x(300f)
            .setDuration(100)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    etSearch.apply {
                        compoundDrawablePadding = 40
                        AppCompatResources.getDrawable(
                            this@LauncherActivity,
                            R.drawable.ic_search_white
                        )?.let {
                            val wrappedDrawable = DrawableCompat.wrap(it)
                            DrawableCompat.setTint(
                                wrappedDrawable,
                                primaryColor
                            )
                            setCompoundDrawablesWithIntrinsicBounds(
                                wrappedDrawable,
                                null,
                                null,
                                null
                            )
                        }
                    }
                    etSearch.clearFocus()
                    etSearch.setText("")
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {

                }

            })

    }

    private fun initBiometric() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    startActivity(HiddenAppsActivity.newIntent(this@LauncherActivity))
                }

            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.confirm_your_password))
            .setConfirmationRequired(false)
            .setDeviceCredentialAllowed(true)
            .build()

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            Sensey.getInstance().setupDispatchTouchEvent(it)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        Sensey.getInstance().stop()
        super.onDestroy()
    }

    override fun onItemClick(position: Int, parent: View, appInfo: App) {
        openApp(parent, appInfo)
        if (::popup.isInitialized)
            popup.dismiss()
    }

    override fun onItemLongClick(position: Int, parent: View, appInfo: App) {
        showPopup(position, parent, appInfo)
        clParent.isTouchEnabled = false
        rvHideApps.isLayoutFrozen = true
    }

    override fun onDragStarted(position: Int, parent: View, appInfo: App) {
        dragView.visibility = View.VISIBLE
        clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        if (::popup.isInitialized)
            popup.dismiss()
    }

    override fun onEventCancel(position: Int, appInfo: App) {
        if (::popup.isInitialized)
            popup.dismiss()
    }

    override fun onBackPressed() {
        if (clParent.panelState === SlidingUpPanelLayout.PanelState.EXPANDED) {
            clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        } else
            super.onBackPressed()
    }

    private fun updateUI(settingPreference: SettingPreference) {
        primaryColor = settingPreference.primaryColor
        adapter.updatePrimaryColor(primaryColor)

        etSearch.setTextColor(primaryColor)
        etSearch.setHintTextColor(primaryColor)
        AppCompatResources.getDrawable(this@LauncherActivity, R.drawable.ic_search_white)?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            DrawableCompat.setTint(
                wrappedDrawable,
                primaryColor
            )
            etSearch.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null)
        }
        ivSeparator.setBackgroundColor(primaryColor)
        when (settingPreference.drawerStyle) {
            Constants.Drawer.STYLE_VERTICAL_INDICATOR -> {
                layoutManager.orientation = RecyclerView.VERTICAL
                rvHideApps.layoutManager = layoutManager
                pagerSnapHelper.attachToRecyclerView(null)
                if (rvHideApps.onFlingListener == null)
                    fastscroller.attachFastScrollerToRecyclerView(rvHideApps)
                if (settingPreference.isFastScrollEnabled) {
                    fastscroller.handleDrawable?.let {
                        AppCompatResources.getDrawable(this, R.drawable.fast_scroll_handle)?.let {
                            val wrappedDrawable = DrawableCompat.wrap(it)
                            DrawableCompat.setTint(
                                wrappedDrawable,
                                primaryColor
                            )
                            fastscroller.handleDrawable = wrappedDrawable
                        }
                    }
                } else {
                    fastscroller.handleDrawable?.let {
                        AppCompatResources.getDrawable(this, R.drawable.fast_scroll_handle)?.let {
                            val wrappedDrawable = DrawableCompat.wrap(it)
                            DrawableCompat.setTint(
                                wrappedDrawable,
                                Color.TRANSPARENT
                            )
                            fastscroller.handleDrawable = wrappedDrawable
                        }
                    }
                }
            }
            Constants.Drawer.STYLE_HORIZONTAL_INDICATOR -> {
                layoutManager.orientation = RecyclerView.HORIZONTAL
                rvHideApps.layoutManager = layoutManager
                fastscroller.detachFastScrollerFromRecyclerView()
                pagerSnapHelper.attachToRecyclerView(rvHideApps)
                fastscroller.handleDrawable?.let {
                    AppCompatResources.getDrawable(this, R.drawable.fast_scroll_handle)?.let {
                        val wrappedDrawable = DrawableCompat.wrap(it)
                        DrawableCompat.setTint(
                            wrappedDrawable,
                            Color.TRANSPARENT
                        )
                        fastscroller.handleDrawable = wrappedDrawable
                    }
                }

            }
            Constants.Drawer.STYLE_LIST_INDICATOR -> {
            }
        }
        clParent.coveredFadeColor =
            ColorUtils.setAlphaComponent(
                settingPreference.backgroundColor,
                settingPreference.backgroundColorAlpha.alphaPercentage()
            )
    }

    private fun showPopup(position: Int, view: View, app: App) {
        val arrpos = when {
            position % 5 == 0 -> 0.13f
            position % 5 == 1 -> 0.37f
            position % 5 == 2 -> 0.5f
            position % 5 == 3 -> 0.64f
            position % 5 == 4 -> 0.87f
            else -> 0.5f
        }
        popup = createBalloon(this) {
            setArrowVisible(true)
            setArrowSize(10)
            setArrowPosition(arrpos)
            setCircularDuration(200)
            setArrowColor(ContextCompat.getColor(this@LauncherActivity, R.color.light_grey))
            setArrowOrientation(ArrowOrientation.BOTTOM)
            setBackgroundColor(ContextCompat.getColor(this@LauncherActivity, R.color.transparent))
            setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            setLayout(R.layout.popup_app_options)
            setDismissWhenTouchOutside(true)
            setOnBalloonDismissListener {
                clParent.isTouchEnabled = true
                rvHideApps.isLayoutFrozen = false
            }
            setDismissWhenShowAgain(true)
            setLifecycleOwner(this@LauncherActivity)
        }
        popup.getContentView().findViewById<LinearLayout>(R.id.ivUninstall).setOnClickListener {
            popup.dismiss()
            startUninstall(app.packageName)
        }
        popup.getContentView().findViewById<LinearLayout>(R.id.ivEdit).setOnClickListener {
            popup.dismiss()

        }
        popup.getContentView().findViewById<LinearLayout>(R.id.ivAppInfo).setOnClickListener {
            popup.dismiss()
            openAppInfo(app.packageName)
        }
        popup.showAlignTop(view, 0, 40)
    }
}
