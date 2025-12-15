package com.aviatrac.softoclub.trgjo.data.shar

import android.content.Context
import androidx.core.content.edit

class AviaTrackSharedPreference(context: Context) {
    private val aviaTrackPrefs = context.getSharedPreferences("aviaTrackSharedPrefsAb", Context.MODE_PRIVATE)

    var aviaTrackSavedUrl: String
        get() = aviaTrackPrefs.getString(AVIA_TRACK_SAVED_URL, "") ?: ""
        set(value) = aviaTrackPrefs.edit { putString(AVIA_TRACK_SAVED_URL, value) }

    var aviaTrackExpired : Long
        get() = aviaTrackPrefs.getLong(AVIA_TRACK_EXPIRED, 0L)
        set(value) = aviaTrackPrefs.edit { putLong(AVIA_TRACK_EXPIRED, value) }

    var aviaTrackAppState: Int
        get() = aviaTrackPrefs.getInt(AVIA_TRACK_APPLICATION_STATE, 0)
        set(value) = aviaTrackPrefs.edit { putInt(AVIA_TRACK_APPLICATION_STATE, value) }

    var aviaTrackNotificationRequest: Long
        get() = aviaTrackPrefs.getLong(AVIA_TRACK_NOTIFICAITON_REQUEST, 0L)
        set(value) = aviaTrackPrefs.edit { putLong(AVIA_TRACK_NOTIFICAITON_REQUEST, value) }

    var aviaTrackNotificationRequestedBefore: Boolean
        get() = aviaTrackPrefs.getBoolean(AVIA_TRACK_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = aviaTrackPrefs.edit { putBoolean(
            AVIA_TRACK_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val AVIA_TRACK_SAVED_URL = "aviaTrackSavedUrl"
        private const val AVIA_TRACK_EXPIRED = "aviaTrackExpired"
        private const val AVIA_TRACK_APPLICATION_STATE = "aviaTrackApplicationState"
        private const val AVIA_TRACK_NOTIFICAITON_REQUEST = "aviaTrackNotificationRequest"
        private const val AVIA_TRACK_NOTIFICATION_REQUEST_BEFORE = "aviaTrackNotificationRequestedBefore"
    }
}