package com.sasuke.launcheroneplus.ui.wallpaper.list.grid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.sasuke.launcheroneplus.R
import com.sasuke.launcheroneplus.data.model.Status
import com.sasuke.launcheroneplus.ui.base.BaseActivity
import com.sasuke.launcheroneplus.ui.base.SpaceItemDecoration
import com.sasuke.launcheroneplus.ui.wallpaper.list.pager.WallpaperPagerActivity
import com.sasuke.launcheroneplus.util.Constants
import com.sasuke.launcheroneplus.util.DebouncingSearchViewQueryTextListener
import com.sasuke.launcheroneplus.util.hide
import com.sasuke.launcheroneplus.util.show
import kotlinx.android.synthetic.main.activity_wallpaper_settings.*
import ru.alexbykov.nopaginate.callback.OnLoadMoreListener
import ru.alexbykov.nopaginate.paginate.NoPaginate
import javax.inject.Inject
import kotlin.math.ceil

class WallpaperGridActivity : BaseActivity(),
    WallpaperAdapter.OnItemClickListener, OnLoadMoreListener {

    @Inject
    lateinit var adapter: WallpaperAdapter

    @Inject
    lateinit var layoutManager: GridLayoutManager

    @Inject
    lateinit var spaceItemDecoration: SpaceItemDecoration

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var wallpaperActivityViewModel: WallpaperActivityViewModel

    private var query: String? = null

    private var isFirstLoad = false

    private lateinit var paginate: NoPaginate

    companion object {
        fun newIntent(context: Context) = Intent(context, WallpaperGridActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_settings)
        inject()
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        getPopularWalls()
        observeLiveData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.wallpaper, menu)
        menu?.findItem(R.id.action_search)?.let {
            searchView.setMenuItem(it)
        }
        return true
    }

    private fun inject() {
        wallpaperActivityViewModel =
            ViewModelProvider(this, viewModelFactory).get(WallpaperActivityViewModel::class.java)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.wallpaper)
    }

    private fun setupRecyclerView() {
        rvWallpaper.layoutManager = layoutManager
        rvWallpaper.adapter = adapter
        rvWallpaper.setHasFixedSize(true)
        adapter.setOnItemClickListener(this)
        rvWallpaper.addItemDecoration(spaceItemDecoration)
    }

    private fun setupListeners() {
        searchView.setOnQueryTextListener(DebouncingSearchViewQueryTextListener(this.lifecycle) {
            it?.let {
                if (it.isNotBlank()) {
                    if (query == null) {
                        query = it
                        wallpaperActivityViewModel.getWallpapersForQuery(it)
                        paginate = NoPaginate.with(rvWallpaper)
                            .setLoadingTriggerThreshold(0)
                            .setOnLoadMoreListener(this)
                            .build()
                    } else {
                        if (query != it) {
                            query = it
                            adapter.wallpapers.clear()
                            adapter.notifyDataSetChanged()
                            wallpaperActivityViewModel.refresh()
                            isFirstLoad = true
                            setNoMoreItems(false)
                            wallpaperActivityViewModel.getWallpapersForQuery(it)
                        }
                    }
                }
            }
        })
    }

    private fun getPopularWalls() {
        wallpaperActivityViewModel.getPopularWalls()
    }

    private fun observeLiveData() {

        wallpaperActivityViewModel.popularWallpaperLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    rvWallpaper.hide()
                    progressBar.show()
                }
                Status.SUCCESS -> {
                    progressBar.hide()
                    rvWallpaper.show()
                    it.data?.let {
                        adapter.addWallpapers(it)
                        adapter.notifyDataSetChanged()
                        isFirstLoad = true
                    }
                }
                Status.ERROR -> {
                    progressBar.hide()
                    rvWallpaper.hide()
                }
            }
        })

        wallpaperActivityViewModel.wallpaperLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    if (isFirstLoad) {
                        rvWallpaper.hide()
                        progressBar.show()
                    }
                    showLoading(true)
                }
                Status.SUCCESS -> {
                    progressBar.hide()
                    rvWallpaper.show()
                    showLoading(false)
                    if (isFirstLoad) {
                        isFirstLoad = false
                        adapter.wallpapers.clear()
                        adapter.notifyDataSetChanged()
                        runLayoutAnimation()
                    }
                    it.data?.let {
                        if (it.isNotEmpty()) {
                            val previousSize = adapter.wallpapers.size
                            adapter.addWallpapers(it)
                            if (previousSize == 0) {
                                adapter.notifyDataSetChanged()
                                runLayoutAnimation()
                            } else
                                adapter.notifyItemRangeInserted(
                                    previousSize,
                                    adapter.wallpapers.size - previousSize
                                )
                        } else
                            setNoMoreItems(true)
                    }
                }
                Status.ERROR -> {
                    if (isFirstLoad) {
                        progressBar.hide()
                        rvWallpaper.hide()
                    } else {
                        showLoading(false)
                        showError(true)
                    }
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

    private fun runLayoutAnimation() {
        val context = rvWallpaper.context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.grid_layout_animation_from_bottom)

        rvWallpaper.layoutAnimation = controller
        rvWallpaper.adapter?.notifyDataSetChanged()
        rvWallpaper.scheduleLayoutAnimation()
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen) {
            searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onItemClick(position: Int, imageView: ImageView) {
        startActivity(
            WallpaperPagerActivity.newIntent(
                this,
                query,
                position,
                ceil((position + 1).toDouble() / Constants.PAGE_SIZE.toDouble()).toInt()
            )
        )
        overridePendingTransition(R.anim.task_open_enter,R.anim.task_open_enter)
    }

    override fun onLoadMore() {
        query?.let {
            if (!isFirstLoad)
                wallpaperActivityViewModel.getWallpapersForQuery(it)
        }
    }
}