package com.kuzheevadel.vmplayerv2.common

import android.net.Uri

data class UpdateUIMessage(val title: String,
                           val artist: String,
                           val albumId: Uri,
                           val duration: Int,
                           val albumName: String)