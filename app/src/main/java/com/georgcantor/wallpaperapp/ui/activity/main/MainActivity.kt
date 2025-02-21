package com.georgcantor.wallpaperapp.ui.activity.main

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat.START
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.ActivityMainBinding
import com.georgcantor.wallpaperapp.ui.activity.GalleryActivity
import com.georgcantor.wallpaperapp.ui.activity.SearchActivity
import com.georgcantor.wallpaperapp.ui.activity.categories.CategoriesActivity
import com.georgcantor.wallpaperapp.ui.activity.favorites.FavoritesActivity
import com.georgcantor.wallpaperapp.ui.activity.videos.VideosActivity
import com.georgcantor.wallpaperapp.util.*
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.NetworkUtils.getNetworkLiveData
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject lateinit var preferences: SharedPreferences
    private val binding by viewBinding(ActivityMainBinding::inflate)
    private var currentNavController: LiveData<NavController>? = null
    private var backButtonPressedTwice = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState == null) setupBottomNavigationBar()

        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null

        getNetworkLiveData(applicationContext).observe(this) {
            binding.noInternetWarning.isVisible = !it
        }

        if (this::preferences.isInitialized) {
            var openCounter: Int = preferences.getAny(0, OPEN_COUNT) as Int
            if (openCounter >= 4) {
                val reviewManager = ReviewManagerFactory.create(this)
                val request = reviewManager.requestReviewFlow()
                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val reviewInfo = task.result
                        reviewManager.launchReviewFlow(this, reviewInfo)
                    }
                }
            } else {
                preferences.putAny(OPEN_COUNT, ++openCounter)
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> startActivity<SearchActivity>()
            R.id.action_gallery -> startActivity<CategoriesActivity>()
            R.id.action_videos -> startActivity<VideosActivity>()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(R.navigation.bmw, R.navigation.audi, R.navigation.mercedes)

        val controller = binding.bottomNav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        controller.observe(this) { navController ->
            setupActionBarWithNavController(navController)
        }
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_aston -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.aston)) }
            R.id.nav_bentley -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.bentley)) }
            R.id.nav_bugatti -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.bugatti)) }
            R.id.nav_ferrari -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.ferrari)) }
            R.id.nav_lambo -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.lambo_walp)) }
            R.id.nav_mclaren -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.mclaren)) }
            R.id.nav_porsche -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.porsche)) }
            R.id.nav_rolls -> startActivity<GalleryActivity> { putExtra(PIC_EXTRA, getString(R.string.rolls)) }
            R.id.nav_favorites-> startActivity<FavoritesActivity>()
        }
        binding.drawerLayout.closeDrawer(START)
        return true
    }

    override fun onBackPressed() {
        when {
            binding.drawerLayout.isDrawerOpen(START) -> binding.drawerLayout.closeDrawer(START)
            else -> {
                when (currentNavController?.value?.currentDestination?.label) {
                    getString(R.string.bmw) -> {
                        if (backButtonPressedTwice) {
                            super.onBackPressed()
                        } else {
                            backButtonPressedTwice = true
                            shortToast(getString(R.string.press_back))
                            lifecycleScope.launchWhenStarted {
                                delay(2000)
                                backButtonPressedTwice = false
                            }
                        }
                    }
                    else -> super.onBackPressed()
                }
            }
        }
    }

    companion object {
        private const val OPEN_COUNT = "open_count"
    }
}