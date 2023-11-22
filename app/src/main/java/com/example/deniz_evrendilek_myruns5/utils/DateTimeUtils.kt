package com.example.deniz_evrendilek_myruns5.utils

import android.content.Context
import android.text.format.DateFormat

/**
 * Get the hour format preference
 */
object DateTimeUtils {
    var is24HourFormat: Boolean = false
        private set

    fun init(context: Context) {
        is24HourFormat = DateFormat.is24HourFormat(context)
    }
}