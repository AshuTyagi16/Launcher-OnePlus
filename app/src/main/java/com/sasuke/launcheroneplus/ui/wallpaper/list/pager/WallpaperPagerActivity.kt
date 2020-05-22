package com.sasuke.launcheroneplus.ui.wallpaper.list.pager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.mikhaellopez.gradientview.GradientView
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Result
import com.sasuke.launcheroneplus.data.model.Status
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.base.ItemDecorator
import com.sasuke.launcheroneplus.ui.wallpaper.WallpaperPreviewActivity
import com.sasuke.launcheroneplus.util.Constants
import kotlinx.android.synthetic.main.activity_wallpaper_pager.*
import ru.alexbykov.nopaginate.callback.OnLoadMoreListener
import ru.alexbykov.nopaginate.paginate.NoPaginate
import javax.inject.Inject

class WallpaperPagerActivity : BaseActivity(), WallpaperPagerAdapter.OnItemListener,
    OnLoadMoreListener {

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

    @Inject
    lateinit var gson: Gson

    private var query: String? = null

    private var page: Int = 1

    private var position: Int = 0

    private lateinit var wallpaperPagerActivityViewModel: WallpaperPagerActivityViewModel

    private lateinit var paginate: NoPaginate

    private var isFirstLoad = true

    companion object {
        private const val EXTRA_QUERY = "EXTRA_QUERY"
        private const val EXTRA_PAGE = "EXTRA_PAGE"
        private const val EXTRA_POSITION = "EXTRA_POSITION"

        fun newIntent(context: Context, query: String?, position: Int, page: Int): Intent {
            return Intent(context, WallpaperPagerActivity::class.java).apply {
                putExtra(EXTRA_QUERY, query)
                putExtra(EXTRA_POSITION, position)
                putExtra(EXTRA_PAGE, page)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_pager)
        inject()
        getArguments()
        setupRecyclerView()
        initGradientView()
        getWallpapers()
        observeLiveData()
    }

    private fun inject() {
        wallpaperPagerActivityViewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(WallpaperPagerActivityViewModel::class.java)
    }

    private fun getArguments() {
        query = intent.getStringExtra(EXTRA_QUERY)
        page = intent.getIntExtra(EXTRA_PAGE, 0)
        position = intent.getIntExtra(EXTRA_POSITION, 0)
    }

    private fun getWallpapers() {
        if (!query.isNullOrEmpty()) {
            paginate = NoPaginate.with(rvWallpaperPager)
                .setLoadingTriggerThreshold(0)
                .setOnLoadMoreListener(this)
                .build()
            wallpaperPagerActivityViewModel.getWallpapersForQuery(query!!, page)
        } else
            wallpaperPagerActivityViewModel.getPopularWalls()
    }

    private fun setupRecyclerView() {
        rvWallpaperPager.layoutManager = layoutManager
        rvWallpaperPager.adapter = adapter
        adapter.setOnItemListener(this)
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
                    if (pos >= 0 && pos < adapter.wallpapers.size) {
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
            }
        })
    }

    private fun initGradientView() {
        gradientView.apply {
            // Set Color Start
            start = Color.BLACK
            alphaStart = 1f

            // Set Color End
            end = Color.BLACK
            alphaEnd = 1f

            // Set Gradient Direction
            direction =
                GradientView.GradientDirection.LEFT_TO_RIGHT
        }
        gradientView.animate()
    }

    private fun observeLiveData() {
        wallpaperPagerActivityViewModel.popularWallpaperLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    it.data?.let {
                        adapter.addWallpapers(it)
                        adapter.notifyDataSetChanged()
                    }
                }
                Status.ERROR -> {

                }
            }
        })

        wallpaperPagerActivityViewModel.wallpaperLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    showLoading(true)
                    showError(false)
                }
                Status.SUCCESS -> {
                    showLoading(false)
                    showError(false)
                    page++
                    it.data?.let {
                        if (it.isNotEmpty()) {
                            val previousSize = adapter.wallpapers.size
                            adapter.addWallpapers(it)
                            if (previousSize == 0)
                                adapter.notifyDataSetChanged()
                            else
                                adapter.notifyItemRangeInserted(
                                    previousSize,
                                    adapter.wallpapers.size - previousSize
                                )
                            if (isFirstLoad) {
                                rvWallpaperPager.scrollToPosition(position % Constants.PAGE_SIZE)
                                isFirstLoad = false
                            }
                        } else {
                            setNoMoreItems(true)
                        }
                    }
                }
                Status.ERROR -> {
                    showLoading(false)
                    showError(true)
                }
            }
        })
    }

    private fun showLoading(loading: Boolean) {
        if (::paginate.isInitialized)
            paginate.showLoading(loading)
    }

    private fun showError(error: Boolean) {
        if (::paginate.isInitialized)
            paginate.showError(error)
    }

    private fun setNoMoreItems(noMoreItems: Boolean) {
        if (::paginate.isInitialized)
            paginate.setNoMoreItems(noMoreItems)
    }

    override fun onItemClick(position: Int, result: Result, imageView: ImageView) {
        val imagePair =
            Pair.create<View, String>(imageView, position.toString())

        val activityOptions =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                imagePair
            )
        startActivity(
            WallpaperPreviewActivity.newIntent(this, gson.toJson(result), position),
            activityOptions.toBundle()
        )
    }

    override fun onLoadMore() {
        query?.let {
            wallpaperPagerActivityViewModel.getWallpapersForQuery(it, page)
        }
    }
}