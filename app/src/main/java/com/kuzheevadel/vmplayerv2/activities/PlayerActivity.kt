package com.kuzheevadel.vmplayerv2.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.kuzheevadel.vmplayerv2.fragments.AllTracksFragment
import com.kuzheevadel.vmplayerv2.adapters.PlayerPagerAdapter
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.fragments.FullScreenPlaybackFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.full_screen_playback.*
import kotlinx.android.synthetic.main.player_layout.*

class PlayerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(player_toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, player_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        setupPager(player_pager)
        tab_layout.setupWithViewPager(player_pager)

        supportFragmentManager.beginTransaction()
            .add(R.id.playback_container, FullScreenPlaybackFragment())
            .commit()
    }

    private fun setupPager(pager: ViewPager) {
        val adapter = PlayerPagerAdapter(supportFragmentManager)
        adapter.addFragment(AllTracksFragment(), "All Songs")
        adapter.addFragment(AllTracksFragment(), "Albums")
        pager.adapter = adapter
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (activity_main != null &&
            (activity_main.panelState == PanelState.EXPANDED || activity_main.panelState == PanelState.ANCHORED)){
            activity_main.panelState = PanelState.COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

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
