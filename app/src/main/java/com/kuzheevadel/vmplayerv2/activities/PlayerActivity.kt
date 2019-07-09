package com.kuzheevadel.vmplayerv2.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.kuzheevadel.vmplayerv2.adapters.PlayerPagerAdapter
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.fragments.*
import com.sothree.slidinguppanel.SlidingUpPanelLayout.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.player_layout.*
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var count = 0
    private var clickCount = 0
    private var isStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.FeedActivityThemeDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(player_toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, player_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        player_pager.offscreenPageLimit = 4
        setupPager(player_pager)
        tab_layout.setupWithViewPager(player_pager)

        supportFragmentManager.beginTransaction()
            .replace(R.id.playback_container, FullScreenPlaybackFragment(), "PlaybackFragment")
            .commit()
    }

    private fun setupPager(pager: ViewPager) {
        val adapter = PlayerPagerAdapter(supportFragmentManager)
        adapter.addFragment(AllTracksFragment(), "All Songs")
        adapter.addFragment(AlbumsFragment(), "Albums")
        adapter.addFragment(PlaylistFragment(), "Playlist")
        adapter.addFragment(RadioFragment(), "Radio")
        pager.adapter = adapter
    }
    

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (activity_main != null &&
            (activity_main.panelState == PanelState.EXPANDED || activity_main.panelState == PanelState.ANCHORED)){
            activity_main.panelState = PanelState.COLLAPSED
        } else {
            clickCount++

            if (!isStarted) {
                Snackbar.make(activity_main, "Click one more time for exit", Snackbar.LENGTH_LONG).show()
                startTimer()
            }

            if (clickCount == 2 && count < 5) {
                finish()
                super.onBackPressed()
            }
        }
    }

    private fun startTimer() {
        val disposable = CompositeDisposable()
        isStarted = true

        disposable.add(Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                count++

                if (it >= 2) {
                    count = 0
                    clickCount = 0
                    isStarted = false
                    disposable.dispose()
                }
            })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_choose_theme -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
