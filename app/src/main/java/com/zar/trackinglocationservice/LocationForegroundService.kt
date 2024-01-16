package com.zar.trackinglocationservice


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.zar.trackinglocationservice.NotificationModule.Constants.ACTION_SERVICE_START
import com.zar.trackinglocationservice.NotificationModule.Constants.ACTION_SERVICE_STOP
import com.zar.trackinglocationservice.NotificationModule.Constants.NOTIFICATION_CHANNEL_ID
import com.zar.trackinglocationservice.NotificationModule.Constants.NOTIFICATION_CHANNEL_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LocationForegroundService : LifecycleService() {

    @Inject
    lateinit var notification: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var piLocationManager: PiLocationManager

    private var gpsCheckJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    companion object {
        val started = MutableLiveData<Boolean>()
    }

    private fun setInitialValues() {
        started.postValue(false)
    }

    override fun onCreate() {
        setInitialValues()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SERVICE_START -> {
                    started.postValue(true)
                    startForegroundService()
                }
                ACTION_SERVICE_STOP -> {
                    started.postValue(false)
                }
                else -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NotificationModule.Constants.NOTIFICATION_ID, notification.build())
        Timber.d("startForegroundService: ")
        startGpsCheck()

        coroutineScope.launch {
            try {
                piLocationManager.locationUpdates(5)
                    .collect { location ->
                        Timber.d("LocationForegroundService: " + location.latitude + " " + location.longitude)

                        // Do something with the location data...
                        val message = " ${location.latitude} ,  ${location.longitude}"
                    }
            } catch (e: PiLocationException) {
                // Handle PiLocationException...
                Timber.e("PiLocationException: " + e.message)
            }
        }

    }
    private fun startGpsCheck() {
        gpsCheckJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                Timber.d("startGpsCheck")
                piLocationManager.turnOnGPS()
                delay(5000) // You can adjust the interval as needed
            }
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}
