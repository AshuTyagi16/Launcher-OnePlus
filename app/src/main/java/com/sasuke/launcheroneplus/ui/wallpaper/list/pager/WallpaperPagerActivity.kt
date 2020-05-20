package com.sasuke.launcheroneplus.ui.wallpaper.list.pager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.mikhaellopez.gradientview.GradientView
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Status
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import kotlinx.android.synthetic.main.activity_wallpaper_pager.*
import javax.inject.Inject

class WallpaperPagerActivity : BaseActivity() {

    @Inject
    lateinit var adapter: WallpaperPagerAdapter

    @Inject
    lateinit var layoutManager: LinearLayoutManager

    @Inject
    lateinit var itemDecoration: ItemDecorator

    @Inject
    lateinit var pagerSnapHelper: PagerSnapHelper

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var glide: RequestManager

    private lateinit var query: String

    private var position: Int = 0

    private lateinit var wallpaperPagerActivityViewModel: WallpaperPagerActivityViewModel

    companion object {
        private const val EXTRA_QUERY = "EXTRA_QUERY"
        private const val EXTRA_POSITION = "EXTRA_POSITION"

        fun newIntent(context: Context, query: String, position: Int): Intent {
            return Intent(context, WallpaperPagerActivity::class.java).apply {
                putExtra(EXTRA_QUERY, query)
                putExtra(EXTRA_POSITION, position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_pager)
        inject()
        getArguments()
        getWallpapers()
        setupRecyclerView()
        observeLiveData()
    }

    private fun inject() {
        wallpaperPagerActivityViewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(WallpaperPagerActivityViewModel::class.java)
    }

    private fun getArguments() {
        query = intent.getStringExtra(EXTRA_QUERY)!!
        position = intent.getIntExtra(EXTRA_POSITION, 0)
    }

    private fun getWallpapers() {
        wallpaperPagerActivityViewModel.getWallpapersForQuery(query)
    }

    private fun setupRecyclerView() {
        rvWallpaperPager.layoutManager = layoutManager
        rvWallpaperPager.adapter = adapter
        rvWallpaperPager.addItemDecoration(itemDecoration)
        pagerSnapHelper.attachToRecyclerView(rvWallpaperPager)

        rvWallpaperPager.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }

            @SuppressLint("CheckResult")
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val pos = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val item = adapter.wallpapers[pos]
                    glide
                        .asBitmap()
                        .load(item.urls.regular)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onLoadCleared(placeholder: Drawable?) {

                            }

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                resource.let {
                                    Palette.Builder(it).generate {
                                        it?.let { palette ->
                                            gradientView.apply {
                                                // Set Color Start
                                                start = palette.getDarkVibrantColor(0)
                                                alphaStart = 1f

                                                // Set Color End
                                                end = palette.getDominantColor(0)
                                                alphaEnd = 1f

                                                // Set Gradient Direction
                                                direction =
                                                    GradientView.GradientDirection.LEFT_TO_RIGHT
                                            }
                                            gradientView.animate()
                                        }
                                    }
                                }
                            }

                        })
                }
            }
        })
    }

    private fun observeLiveData() {
        wallpaperPagerActivityViewModel.wallpaperLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    it.data?.let {
                        adapter.addWallpapers(it.results)
                        adapter.notifyDataSetChanged()
                        rvWallpaperPager.smoothScrollToPosition(position)
                    }
                }
                Status.ERROR -> {
                }
            }
        })
    }
}