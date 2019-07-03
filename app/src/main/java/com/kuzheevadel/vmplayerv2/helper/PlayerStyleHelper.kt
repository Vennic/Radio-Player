package com.kuzheevadel.vmplayerv2.helper

import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log


class PlayerStyleHelper {
    companion object {
        fun from(context: Context?, mediaSessionCompat: MediaSessionCompat): NotificationCompat.Builder {
            Log.i("NotificationTest", "$mediaSessionCompat")
            val controller: MediaControllerCompat = mediaSessionCompat.controller
            val mediaMetadata: MediaMetadataCompat = controller.metadata
            val description: MediaDescriptionCompat = mediaMetadata.description

            val builder = NotificationCompat.Builder(context)
            builder
                .setContentTitle(description.title)
                .setContentText(description.subtitle)
                .setSubText(description.description)
                .setLargeIcon(description.iconBitmap)
                .setContentIntent(controller.sessionActivity)
                .setDeleteIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)
                )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            return builder
        }
    }
}