package com.sasuke.launcheroneplus.ui.launcher

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.EdgeEffect
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.TouchTypeDetector
import com.huxq17.handygridview.HandyGridView
import com.huxq17.handygridview.listener.OnItemCapturedListener
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.AppInfo
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.ui.drag_drop.GridViewAdapter
import com.sasuke.launcheroneplus.ui.launcher.apps.AppAdapter
import com.sasuke.launcheroneplus.ui.launcher.apps.AppViewHolder
import com.sasuke.launcheroneplus.util.Constants
import com.sasuke.launcheroneplus.util.KeyboardTriggerBehavior
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_launcher.*
import kotlinx.android.synthetic.main.layout_non_sliding.*
import kotlinx.android.synthetic.main.layout_sliding.*
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

    private lateinit var launcherActivityViewModel: LauncherActivityViewModel

    private lateinit var handler: Handler

    private val gridAdapter = GridViewAdapter()

    private lateinit var keyboardTriggerBehavior: KeyboardTriggerBehavior

    init {
        gridAdapter.setOnClickListeners(this)
    }

    companion object {
        /** The magnitude of rotation while the list is scrolled. */
        private const val SCROLL_ROTATION_MAGNITUDE = 0.25f
        /** The magnitude of rotation while the list is over-scrolled. */
        private const val OVERSCROLL_ROTATION_MAGNITUDE = -10
        /** The magnitude of translation distance while the list is over-scrolled. */
        private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.2f
        /** The magnitude of translation distance when the list reaches the edge on fling. */
        private const val FLING_TRANSLATION_MAGNITUDE = 0.5f

        const val DRAWABLE_LEFT = 0
        const val DRAWABLE_TOP = 1
        const val DRAWABLE_RIGHT = 2
        const val DRAWABLE_BOTTOM = 3

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        inject()
        setWindowInsets()
        setupRecyclerView()
        setupGridView()
        setupListeners()
        getAppList()
        observeLiveData()
    }

    private fun inject() {
        launcherActivityViewModel =
            ViewModelProvider(this, viewModelFactory).get(LauncherActivityViewModel::class.java)
        Sensey.getInstance().init(this)
        handler = Handler()
    }

    private fun setWindowInsets() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            clParent.setOnApplyWindowInsetsListener { v, insets ->
                v.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
                insets
            }
        }
    }

    private fun setupRecyclerView() {
        rvApps.layoutManager = layoutManager
        rvApps.addItemDecoration(itemDecoration)
        rvApps.adapter = adapter
        adapter.setOnClickListeners(this)

        rvApps.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {
                return object : EdgeEffect(recyclerView.context) {

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
                            holder.rotation.cancel()
                            holder.translationY.cancel()
                            holder.itemView.rotation += rotationDelta
                            holder.itemView.translationY += translationYDelta
                        }
                    }

                    override fun onRelease() {
                        super.onRelease()
                        // The finger is lifted. This is when we should start the animations to bring
                        // the view property values back to their resting states.
                        recyclerView.forEachVisibleHolder { holder: AppViewHolder ->
                            holder.rotation.start()
                            holder.translationY.start()
                        }
                    }

                    override fun onAbsorb(velocity: Int) {
                        super.onAbsorb(velocity)
                        val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                        // The list has reached the edge on fling.
                        val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                        recyclerView.forEachVisibleHolder { holder: AppViewHolder ->
                            holder.translationY
                                .setStartVelocity(translationVelocity)
                                .start()
                        }
                    }
                }
            }
        }

    }

    private fun setupGridView() {
        gridApps.adapter = gridAdapter
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

        val touchTypeDetector = object : TouchTypeDetector.TouchTypListener {
            override fun onDoubleTap() {

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
                            handler.postDelayed({
                                openStatusBar()
                            }, 1)
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

        Sensey.getInstance().startTouchTypeDetection(this, touchTypeDetector)

        clParent.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                ivDragIcon.alpha = 1 - slideOffset
                clSearchBar.alpha = slideOffset
                gridApps.alpha = 1 - slideOffset
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                when (newState) {
                    SlidingUpPanelLayout.PanelState.EXPANDED -> {
                        Sensey.getInstance().stopTouchTypeDetection()
                    }
                    SlidingUpPanelLayout.PanelState.DRAGGING -> {

                    }
                    SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                        Sensey.getInstance().startTouchTypeDetection(
                            this@LauncherActivity,
                            touchTypeDetector
                        )
                    }
                }
            }
        })

        rvApps.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (layoutManager.findFirstCompletelyVisibleItemPosition() >= Constants.APP_LIST_SPAN_COUNT)
                    ivSeparator.visibility = View.GONE
                else
                    ivSeparator.visibility = View.VISIBLE

                recyclerView.forEachVisibleHolder { holder: AppViewHolder ->
                    holder.rotation
                        // Update the velocity.
                        // The velocity is calculated by the horizontal scroll offset.
                        .setStartVelocity(holder.currentVelocity - dx * SCROLL_ROTATION_MAGNITUDE)
                        // Start the animation. This does nothing if the animation is already running.
                        .start()
                }
            }
        })

        etSearch.addTextChangedListener {
            if (!it.isNullOrEmpty()) {
                launcherActivityViewModel.filterApps(it.toString())
            } else {
                launcherActivityViewModel.getDefaultList()
            }
        }
    }

    private fun getAppList() {
        launcherActivityViewModel.getAppsList()
    }

    private fun observeLiveData() {
        launcherActivityViewModel.appList.observe(this, Observer {
            it?.let {
                adapter.setApps(it)
            }
        })
    }

    private fun setMode(mode: HandyGridView.MODE) {
        gridApps.mode = mode
        gridAdapter.setInEditMode(mode == HandyGridView.MODE.TOUCH)
    }

    private fun openApp(appInfo: AppInfo) {
        val intent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun keyboardOpen() {
        etSearch.apply {
            gravity = Gravity.START
            compoundDrawablePadding = 80
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back_white, 0, 0, 0)
        }
    }

    private fun keyboardClosed() {
        etSearch.apply {
            compoundDrawablePadding = 0
            gravity = Gravity.CENTER
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_white, 0, 0, 0)
        }
        etSearch.clearFocus()
        etSearch.setText("")
    }

    private inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachVisibleHolder(
        action: (T) -> Unit
    ) {
        for (i in 0 until childCount) {
            action(getChildViewHolder(getChildAt(i)) as T)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Sensey.getInstance().setupDispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        Sensey.getInstance().stop()
        super.onDestroy()
    }

    override fun onItemClick(position: Int, parent: View, appInfo: AppInfo) {
        openApp(appInfo)
    }

    override fun onItemLongClick(position: Int, parent: View, appInfo: AppInfo) {
        if (gridAdapter.addItem(appInfo)) {
            clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            showToast(getString(R.string.shortcut_added_to_home_screen))
        }
    }

    override fun onBackPressed() {
        if (clParent.panelState === SlidingUpPanelLayout.PanelState.EXPANDED) {
            clParent.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        } else
            super.onBackPressed()
    }
}
