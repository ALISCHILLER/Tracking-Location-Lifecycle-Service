package com.zar.trackinglocationservice

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext val context: Context
) : ViewModel() {
    val startIntent = Intent(context, LocationForegroundService::class.java)



    fun start() {
        viewModelScope.launch {
            ContextCompat.startForegroundService(context, startIntent)
        }

    }

}