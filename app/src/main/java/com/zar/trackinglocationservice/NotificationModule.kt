package com.zar.trackinglocationservice

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    object Constants {

        const val NOTIFICATION_ID = 3

        const val ACTION_SERVICE_START = "ACTION_SERVICE_START"
        const val ACTION_SERVICE_STOP = "ACTION_SERVICE_STOP"
        const val ACTION_NAVIGATE_TO_MAPS_FRAGMENT = "ACTION_NAVIGATE_TO_MAPS_FRAGMENT"

        const val NOTIFICATION_CHANNEL_ID =  "tracker_notification_id"
        const val NOTIFICATION_CHANNEL_NAME = "tracker_notification"

        const val PENDING_INTENT_REQUEST_CODE = 99

    }
    @ServiceScoped
    @Provides
    fun providePendingIntent(
        @ApplicationContext context: Context
    ) : PendingIntent{
        return PendingIntent.getActivity(
            context,
            Constants.PENDING_INTENT_REQUEST_CODE,
            Intent(context, MainActivity::class.java)/*.apply {
                this.action = Constants.ACTION_NAVIGATE_TO_MAPS_FRAGMENT //the navigation has already implemented in the Activity
            },*/,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
    }

    @ServiceScoped
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ) : NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


}