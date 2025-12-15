package com.aviatrac.softoclub.trgjo.data.utils

import android.util.Log
import com.aviatrac.softoclub.trgjo.presentation.app.AviaTrackApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class AviaTrackPushToken {

    suspend fun aviaTrackGetToken(
        aviaTrackMaxAttempts: Int = 7,
        aviaTrackDelayMs: Long = 1500
    ): String {

        repeat(aviaTrackMaxAttempts - 1) {
            try {
                val aviaTrackToken = FirebaseMessaging.getInstance().token.await()
                return aviaTrackToken
            } catch (e: Exception) {
                Log.e(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(aviaTrackDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}