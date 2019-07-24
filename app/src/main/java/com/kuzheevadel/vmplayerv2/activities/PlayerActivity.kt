package com.kuzheevadel.vmplayerv2.activities

import android.arch.lifecycle.MutableLiveData
import android.content.res.TypedArray
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.PlayerPagerAdapter
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.common.State
import com.kuzheevadel.vmplayerv2.dialogs.SwitchThemeDialog
import com.kuzheevadel.vmplayerv2.fragments.*
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
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
    private val loadTracksData: MutableLiveData<State> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Constants.themeId)

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


        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {tab.setIcon(R.drawable.ic_list_default)}
                    1 -> {tab.setIcon(R.drawable.ic_albums_default)}
                    2 -> {tab.setIcon(R.drawable.ic_tab_playlist_default)}
                    3 -> {tab.setIcon(R.drawable.ic_radio_default)}
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {tab.setIcon(getStyleableDrawable(R.attr.allTracksIcon))}
                    1 -> {tab.setIcon(getStyleableDrawable(R.attr.albumsIcon))}
                    2 -> {tab.setIcon(getStyleableDrawable(R.attr.playlistIcon))}
                    3 -> {tab.setIcon(getStyleableDrawable(R.attr.radioIcon))}
                }
            }

        })

        setupTabIcons()

        supportFragmentManager.beginTransaction()
            .replace(R.id.playback_container, FullScreenPlaybackFragment(), "PlaybackFragment")
            .commit()
    }

    private fun setupPager(pager: ViewPager) {
        val adapter = PlayerPagerAdapter(supportFragmentManager)
        adapter.addFragment(AllTracksFragment.getInstance(loadTracksData), "All songs")
        adapter.addFragment(AlbumsFragment(), "Albums")
        adapter.addFragment(PlaylistFragment.getInstance(loadTracksData), "Playlist")
        adapter.addFragment(RadioFragment(), "Radio")
        pager.adapter = adapter
    }

    private fun setupTabIcons() {
        tab_layout.getTabAt(0)?.setIcon(R.drawable.ic_list_default)
        tab_layout.getTabAt(1)?.setIcon(R.drawable.ic_albums_default)
        tab_layout.getTabAt(2)?.setIcon(R.drawable.ic_tab_playlist_default)
        tab_layout.getTabAt(3)?.setIcon(R.drawable.ic_radio_default)
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

            R.id.nav_choose_theme -> {
                openSwitchThemeDialog()
            }

            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openSwitchThemeDialog() {
        val dialog = SwitchThemeDialog()
        dialog.show(supportFragmentManager, "switch dialog")
    }

    private fun getStyleableDrawable(attribute: Int): Int {
        val a: TypedArray? = this.theme?.obtainStyledAttributes(intArrayOf(attribute))
        return a!!.getResourceId(0, -1)
    }
}
