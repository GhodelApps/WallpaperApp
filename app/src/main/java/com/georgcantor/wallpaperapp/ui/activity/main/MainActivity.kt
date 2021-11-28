package com.georgcantor.wallpaperapp.ui.activity.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat.START
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.databinding.ActivityMainBinding
import com.georgcantor.wallpaperapp.ui.activity.GalleryActivity
import com.georgcantor.wallpaperapp.ui.activity.SearchActivity
import com.georgcantor.wallpaperapp.ui.activity.categories.CategoriesActivity
import com.georgcantor.wallpaperapp.ui.activity.favorites.FavoritesActivity
import com.georgcantor.wallpaperapp.ui.activity.videos.VideosActivity
import com.georgcantor.wallpaperapp.util.Constants.PIC_EXTRA
import com.georgcantor.wallpaperapp.util.NetworkUtils.getNetworkLiveData
import com.georgcantor.wallpaperapp.util.shortToast
import com.georgcantor.wallpaperapp.util.startActivity
import com.georgcantor.wallpaperapp.util.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private var backButtonPressedTwice = false
    private lateinit var reviewManager: ReviewManager
    private lateinit var reviewInfo: ReviewInfo

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.bmwFragment, R.id.audiFragment,  R.id.mercedesFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null

        reviewManager = ReviewManagerFactory.create(this)
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
                if (::reviewInfo.isInitialized) reviewManager.launchReviewFlow(this, reviewInfo)
            }
        }

        getNetworkLiveData(applicationContext).observe(this) {
            binding.noInternetWarning.isVisible = !it
        }
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
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
                when (navController.currentDestination?.label) {
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
}