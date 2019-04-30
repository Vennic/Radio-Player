package com.kuzheevadel.vmplayerv2.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class PlayerService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return PlayerBinder()
    }

    inner class PlayerBinder: Binder() {

    }
}