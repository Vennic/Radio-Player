package com.kuzheevadel.vmplayerv2.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.common.Constants

class SplashActivity: AppCompatActivity() {
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        setThemeId()

        setTheme(R.style.LauncherDark)

        super.onCreate(savedInstanceState)

        val intent = Intent(this, PlayerActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setThemeId() {
        pref = getSharedPreferences("laststate", Context.MODE_PRIVATE)
        Constants.themeId = pref.getInt(Constants.THEME_ID, R.style.FeedActivityThemeLight)
    }
}