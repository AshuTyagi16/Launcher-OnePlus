package com.sasuke.launcheroneplus.ui.launcher

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.DragEvent
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
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
import com.huxq17.handygridview.HandyGridView
import com.huxq17.handygridview.listener.OnItemCapturedListener
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.sasuke.launcheroneplus.LauncherApp
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.data.model.DragData
import com.sasuke.launcheroneplus.data.model.DrawerStyle
import com.sasuke.launcheroneplus.data.model.SettingPreference
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.base.BaseEdgeEffectFactory
import com.sasuke.launcheroneplus.ui.base.BaseViewHolder
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.ui.drag_drop.GridViewAdapter
import com.sasuke.launcheroneplus.ui.hidden_apps.HiddenAppsActivity
import com.sasuke.launcheroneplus.ui.launcher.all_apps.AppAdapter
import com.sasuke.launcheroneplus.ui.launcher.all_apps.AppViewHolder
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

class LauncherActivity : BaseActivity(), OnCustomEventListeners,
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

    private lateinit var launcherActivityViewModel: LauncherActivityViewModel

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private lateinit var recentApps: List<App>

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
        createPopup()
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
        rvAllApps.setHasFixedSize(true)
        rvAllApps.adapter = concatAdapter
        allAppadapter.updatePrimaryColor(primaryColor)
        allAppadapter.setOnCustomEventListeners(this)
        recentAppSectionAdapter.setOnCustomEventListener(this)
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
                return if (::recentApps.isInitialized && recentApps.isNotEmpty())
                    if (position == 0) Constants.APP_LIST_SPAN_COUNT else 1
                else 1
            }
        }

        touchTypeListener = object : TouchTypeDetector.TouchTypListener {
            override fun onDoubleTap() {
                startActivity(LauncherSettingsActivity.newIntent(this@LauncherActivity))
            }

            override fun onSwipe(swipeDirection: Int) {
                when (swipeDirection) {
                    TouchTypeDetector.SWIPE_DIR_UP -> {
                        handler.postDelayed({
                            clParent.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
                        }, 50)
                    }
                    TouchTypeDetector.SWIPE_DIR_DOWN -> {
                        if (clParent.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                            openStatusBar()
                    }
                }
            }

            override fun onSingleTap() {

            }

            override fun onScroll(scrollDirection: Int) {

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
                    if (BiometricManager.from(this@LauncherActivity)
                            .canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
                    )
                        biometricPrompt.authenticate(promptInfo)
                    else
                        startActivity(HiddenAppsActivity.newIntent(this@LauncherActivity))
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

        etSearch.setOnFocusChangeListener { _, b ->
            if (b)
                ivSearchState.setImageResource(R.drawable.ic_back_white)
            else
                ivSearchState.setImageResource(R.drawable.ic_search_white)
        }

        ivSearchState.setOnClickListener {
            if (etSearch.hasFocus()) {
                hideKeyboard()
                etSearch.clearFocus()
                etSearch.text?.clear()
            }
        }

        dragView.setOnDragListener { _, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    clInnerParent.animate().scaleX(0.75f).scaleY(0.75f)
                        .withEndAction {
                            ivDelete.show()
                        }
                        .start()
                    clInnerParent.background = ContextCompat.getDrawable(this, R.drawable.shadow)
                    clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    clInnerParent.alpha = 1f
                    clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    clInnerParent.alpha = 0.7f
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    clInnerParent.background = null
                    clInnerParent.animate().scaleX(1f).scaleY(1f).start()
                    ivDelete.hide()
                    clInnerParent.alpha = 1f
                    dismissPopup()
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
            recentApps = it
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
        launcherActivityViewModel.saveRecentApps()
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

    override fun onDragStart(position: Int, parent: View, appInfo: App) {
        dragView.visibility = View.VISIBLE
        clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        dismissPopup()
    }

    override fun onEventCancel(position: Int, appInfo: App) {
        dismissPopup()
    }

    override fun onBackPressed() {
        if (etSearch.keyboardIsVisible)
            super.onBackPressed()
        else if (clParent.panelState == SlidingUpPanelLayout.PanelState.EXPANDED)
            clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    private fun updateUI(settingPreference: SettingPreference) {
        primaryColor = settingPreference.primaryColor
        allAppadapter.updatePrimaryColor(primaryColor)

        ContextCompat.getDrawable(this, R.drawable.ic_delete)?.let {
            it.updateTint(primaryColor)
            ivDelete.setImageDrawable(it)
        }

        when (settingPreference.drawerStyle) {
            DrawerStyle.VERTICAL -> {
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
            DrawerStyle.LIST -> {
            }
        }
        clParent.coveredFadeColor =
            ColorUtils.setAlphaComponent(
                settingPreference.backgroundColor,
                settingPreference.backgroundColorAlpha.alphaPercentage()
            )
    }

    private fun createPopup() {
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
    }

    private fun showPopup(view: View, app: App) {
        if (::popup.isInitialized) {
            popup.getContentView().findViewById<LinearLayout>(R.id.llUninstall).apply {
                if (isSystemApp(this@LauncherActivity, app.packageName)) {
                    visibility = View.GONE
                } else {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        dismissPopup()
                        startUninstall(app.packageName)
                    }
                    AppCompatResources.getDrawable(this@LauncherActivity, R.drawable.ic_delete)
                        ?.let {
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
    }

    private fun dismissPopup() {
        if (::popup.isInitialized)
            popup.dismiss()
    }
}
