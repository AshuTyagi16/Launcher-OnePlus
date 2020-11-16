package com.sasuke.launcheroneplus.ui.launcher

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.DragEvent
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.biometric.BiometricPrompt
import androidx.collection.LruCache
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.github.nisrulz.sensey.PinchScaleDetector
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.TouchTypeDetector
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huxq17.handygridview.HandyGridView
import com.huxq17.handygridview.listener.OnItemCapturedListener
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.sasuke.launcheroneplus.LauncherApp
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.data.model.DragData
import com.sasuke.launcheroneplus.data.model.SettingPreference
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.base.BaseEdgeEffectFactory
import com.sasuke.launcheroneplus.ui.base.BaseViewHolder
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.ui.drag_drop.GridViewAdapter
import com.sasuke.launcheroneplus.ui.hidden_apps.HiddenAppsActivity
import com.sasuke.launcheroneplus.ui.launcher.all_apps.AppAdapter
import com.sasuke.launcheroneplus.ui.launcher.all_apps.AppViewHolder
import com.sasuke.launcheroneplus.ui.launcher.recent_apps.RecentAppAdapter
import com.sasuke.launcheroneplus.ui.launcher.recent_apps.RecentAppSectionAdapter
import com.sasuke.launcheroneplus.ui.settings.LauncherSettingsActivity
import com.sasuke.launcheroneplus.ui.wallpaper.list.grid.WallpaperGridActivity
import com.sasuke.launcheroneplus.util.*
import com.skydoves.balloon.*
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
    lateinit var allAppadapter: AppAdapter

    @Inject
    lateinit var layoutManager: GridLayoutManager

    @Inject
    lateinit var itemDecoration: ItemDecorator

    @Inject
    lateinit var gridAdapter: GridViewAdapter

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var baseEdgeEffectFactory: BaseEdgeEffectFactory

    @Inject
    lateinit var sharedPreferencesSettingsLiveData: SharedPreferencesSettingsLiveData

    @Inject
    lateinit var recentAppSectionAdapter: RecentAppSectionAdapter

    @Inject
    lateinit var concatAdapter: ConcatAdapter

    @Inject
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    @Inject
    lateinit var gson: Gson

    private lateinit var launcherActivityViewModel: LauncherActivityViewModel

    private val handler: Handler by lazy {
        Handler()
    }

    private lateinit var keyboardTriggerBehavior: KeyboardTriggerBehavior

    private lateinit var pinchScaleListener: PinchScaleDetector.PinchScaleListener
    private lateinit var touchTypeListener: TouchTypeDetector.TouchTypListener

    private lateinit var executor: Executor

    private lateinit var biometricPrompt: BiometricPrompt

    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var primaryColor = 0

    private lateinit var popup: Balloon

    private var isKeyboardOpen = false

    companion object {
        /** The magnitude of rotation while the list is scrolled. */
        private const val SCROLL_ROTATION_MAGNITUDE = 0.25f
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
        primaryColor = LauncherApp.color
    }

    private fun setWindowInsets() {
        clParent.setOnApplyWindowInsetsListener { v, insets ->
            v.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        rvAllApps.layoutManager = layoutManager
        rvAllApps.addItemDecoration(itemDecoration)
        rvAllApps.adapter = concatAdapter
        allAppadapter.updatePrimaryColor(primaryColor)
        allAppadapter.setOnClickListeners(this)
        rvAllApps.edgeEffectFactory = baseEdgeEffectFactory
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
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (launcherActivityViewModel.lruCache.size() >= Constants.APP_LIST_SPAN_COUNT)
                    if (position == 0) Constants.APP_LIST_SPAN_COUNT else 1
                else 1
            }
        }

        keyboardTriggerBehavior = KeyboardTriggerBehavior(this).apply {
            observe(this@LauncherActivity, {
                it?.let {
                    when (it) {
                        KeyboardTriggerBehavior.Status.OPEN -> isKeyboardOpen = true
                        KeyboardTriggerBehavior.Status.CLOSED -> isKeyboardOpen = false
                    }
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
                        rvAllApps.forEachVisibleHolder { holder: BaseViewHolder ->
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
                        etSearch.clearFocus()
                        etSearch.text?.clear()
                        dismissPopup()
                    }
                    else -> {

                    }
                }
            }
        })

        rvAllApps.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                recyclerView.forEachVisibleHolder { holder: BaseViewHolder ->

                    holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(
                            this@LauncherActivity,
                            R.color.app_un_highlight
                        )
                    )
                    holder.rotation
                        // Update the velocity.
                        // The velocity is calculated by the horizontal scroll offset.
                        .setStartVelocity(holder.currentVelocity - dx * SCROLL_ROTATION_MAGNITUDE)
                        // Start the animation. This does nothing if the animation is already running.
                        .start()
                }
            }
        })

        etSearch.addTextChangedListener(DebouncingEditTextQueryTextListener(lifecycle) {
            launcherActivityViewModel.filterApps(it)
        })

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

        fastscroller.setHandleStateListener(object : RecyclerViewFastScroller.HandleStateListener {
            override fun onDragged(offset: Float, position: Int) {
                super.onDragged(offset, position)
                rvAllApps.forEachVisibleHolder { holder: BaseViewHolder ->
                    if (holder is AppViewHolder) {
                        if (allAppadapter.appList[position].label[0].toUpperCase() == allAppadapter.appList[holder.bindingAdapterPosition].label[0].toUpperCase()) {
                            AppCompatResources.getDrawable(
                                this@LauncherActivity,
                                R.drawable.bg_app_highlight
                            )?.let {
                                it.updateTint(ColorUtils.setAlphaComponent(primaryColor, 90))
                                holder.itemView.background = it
                            }
                        } else
                            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
                    }
                }
            }
        })
    }

    private fun observeLiveData() {
        launcherActivityViewModel.recentAppsLiveData.observe(this, {
            recentAppSectionAdapter.setRecentApps(it)
        })

        launcherActivityViewModel.appList.observe(this, {
            it?.let {
                allAppadapter.setApps(it)
            }
        })

        launcherActivityViewModel.filterAppsLiveData.observe(this, {
            it?.let {
                allAppadapter.setApps(it)
            }
        })

        sharedPreferencesSettingsLiveData.observe(this, {
            it?.let {
                updateUI(it)
            }
        })
    }

    private fun setMode(mode: HandyGridView.MODE) {
        gridApps.mode = mode
        gridAdapter.setInEditMode(mode == HandyGridView.MODE.TOUCH)
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
        dismissPopup()
        launcherActivityViewModel.insertInRecentAppCache(appInfo)
    }

    override fun onItemLongClick(position: Int, parent: View, appInfo: App) {
        showPopup(parent, appInfo)
        clParent.isTouchEnabled = false
        rvAllApps.suppressLayout(true)
    }

    override fun onDragStarted(position: Int, parent: View, appInfo: App) {
        dragView.visibility = View.VISIBLE
        clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        dismissPopup()
    }

    override fun onEventCancel(position: Int, appInfo: App) {
        dismissPopup()
    }

    override fun onBackPressed() {
        when {
            isKeyboardOpen -> super.onBackPressed()
            clParent.panelState === SlidingUpPanelLayout.PanelState.EXPANDED -> {
                clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }
    }

    private fun updateUI(settingPreference: SettingPreference) {
        primaryColor = settingPreference.primaryColor
        allAppadapter.updatePrimaryColor(primaryColor)

        when (settingPreference.drawerStyle) {
            Constants.DrawerStyle.VERTICAL -> {
                rvAllApps.layoutManager = layoutManager
                if (settingPreference.isFastScrollEnabled) {
                    fastscroller.handleDrawable?.let {
                        AppCompatResources.getDrawable(this, R.drawable.fast_scroll_handle)?.let {
                            it.updateTint(primaryColor)
                            fastscroller.handleDrawable = it
                        }
                    }
                } else {
                    fastscroller.handleDrawable?.let {
                        AppCompatResources.getDrawable(this, R.drawable.fast_scroll_handle)?.let {
                            it.updateTint(Color.TRANSPARENT)
                            fastscroller.handleDrawable = it
                        }
                    }
                }
            }
            Constants.DrawerStyle.LIST -> {
            }
        }
        clParent.coveredFadeColor =
            ColorUtils.setAlphaComponent(
                settingPreference.backgroundColor,
                settingPreference.backgroundColorAlpha.alphaPercentage()
            )
    }

    private fun showPopup(view: View, app: App) {
        popup = createBalloon(this) {
            setArrowVisible(true)
            setArrowSize(10)
            setArrowPosition(0.5f)
            setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
            setCircularDuration(200)
            setArrowColor(ContextCompat.getColor(this@LauncherActivity, R.color.light_grey))
            setArrowOrientation(ArrowOrientation.BOTTOM)
            setBackgroundColor(ContextCompat.getColor(this@LauncherActivity, R.color.transparent))
            setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            setLayout(R.layout.popup_app_options)
            setDismissWhenTouchOutside(true)
            setOnBalloonDismissListener {
                clParent.isTouchEnabled = true
                rvAllApps.suppressLayout(false)
            }
        }
        popup.getContentView().findViewById<LinearLayout>(R.id.llUninstall).apply {
            if (isSystemApp(this@LauncherActivity, app.packageName)) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                setOnClickListener {
                    dismissPopup()
                    startUninstall(app.packageName)
                }
                AppCompatResources.getDrawable(this@LauncherActivity, R.drawable.ic_delete)?.let {
                    it.updateTint(primaryColor)
                    findViewById<ImageView>(R.id.ivUninstall).setImageDrawable(it)
                }
            }
        }
        popup.getContentView().findViewById<LinearLayout>(R.id.llEdit).apply {
            setOnClickListener {
                dismissPopup()
            }
            AppCompatResources.getDrawable(this@LauncherActivity, R.drawable.ic_edit)?.let {
                it.updateTint(primaryColor)
                findViewById<ImageView>(R.id.ivEdit).setImageDrawable(it)
            }
        }
        popup.getContentView().findViewById<LinearLayout>(R.id.llAppInfo).apply {
            setOnClickListener {
                dismissPopup()
                openAppInfo(app.packageName)
            }
            AppCompatResources.getDrawable(this@LauncherActivity, R.drawable.ic_info)?.let {
                it.updateTint(primaryColor)
                findViewById<ImageView>(R.id.ivAppInfo).setImageDrawable(it)
            }
        }
        popup.showAlignTop(view, 0, 40)
    }

    private fun dismissPopup() {
        if (::popup.isInitialized && popup.isShowing)
            popup.dismiss()
    }
}
