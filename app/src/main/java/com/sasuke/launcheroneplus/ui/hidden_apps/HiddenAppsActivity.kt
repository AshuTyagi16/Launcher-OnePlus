package com.sasuke.launcheroneplus.ui.hidden_apps

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EdgeEffect
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.App
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.ui.hidden_apps.app_selector.AppSelectionActivity
import com.sasuke.launcheroneplus.ui.launcher.apps.AppAdapter
import com.sasuke.launcheroneplus.ui.launcher.apps.AppViewHolder
import com.sasuke.launcheroneplus.util.*
import kotlinx.android.synthetic.main.activity_hidden_apps.*
import javax.inject.Inject

class HiddenAppsActivity : BaseActivity(), AppAdapter.OnClickListeners {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var adapter: AppAdapter

    @Inject
    lateinit var layoutManager: GridLayoutManager

    @Inject
    lateinit var itemDecoration: ItemDecorator

    private lateinit var hiddenAppsActivityViewModel: HiddenAppsActivityViewModel

    companion object {
        fun newIntent(context: Context) = Intent(context, HiddenAppsActivity::class.java)

        /** The magnitude of rotation while the list is scrolled. */
        private const val SCROLL_ROTATION_MAGNITUDE = 0.25f

        /** The magnitude of rotation while the list is over-scrolled. */
        private const val OVERSCROLL_ROTATION_MAGNITUDE = -10

        /** The magnitude of translation distance while the list is over-scrolled. */
        private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.2f

        /** The magnitude of translation distance when the list reaches the edge on fling. */
        private const val FLING_TRANSLATION_MAGNITUDE = 0.5f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden_apps)
        inject()
        observeLiveData()
        setupRecyclerView()
        setupListeners()
    }

    private fun inject() {
        hiddenAppsActivityViewModel =
            ViewModelProvider(this, viewModelFactory).get(HiddenAppsActivityViewModel::class.java)
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

    private fun observeLiveData() {
        hiddenAppsActivityViewModel.appList.observe(this, Observer {
            it?.let {
                if (it.isNotEmpty()) {
                    adapter.setApps(it)
                    lottieView.hide()
                    tvNoHiddenApplications.hide()
                    rvApps.show()
                } else {
                    rvApps.hide()
                    lottieView.show()
                    tvNoHiddenApplications.show()
                }
            }
        })
    }

    private fun setupListeners() {
        btnAddAppToHide.setOnClickListener {
            startActivity(AppSelectionActivity.newIntent(this))
        }

        btnBack.setOnClickListener {
            finish()
        }

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
    }

    override fun onItemClick(position: Int, parent: View, appInfo: App) {
        openApp(appInfo)
    }

    override fun onItemLongClick(position: Int, parent: View, appInfo: App) {

    }

    override fun onDragStarted(position: Int, parent: View, appInfo: App) {

    }

    override fun onEventCancel(position: Int, appInfo: App) {

    }
}