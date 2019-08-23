package com.kuzheevadel.vmplayerv2.model

import android.net.Uri

data class TrackInfo(val artist: String,
                     val title: String,
                     val album: String,
                     val duration: String,
                     val size: String,
                     val albumImageUri: Uri,
                     val bitRate: String,
                     val inPlaylist: Boolean)