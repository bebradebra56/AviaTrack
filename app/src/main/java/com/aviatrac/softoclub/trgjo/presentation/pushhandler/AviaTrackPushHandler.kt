package com.aviatrac.softoclub.trgjo.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.aviatrac.softoclub.trgjo.presentation.app.AviaTrackApplication

class AviaTrackPushHandler {
    fun aviaTrackHandlePush(extras: Bundle?) {
        Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = aviaTrackBundleToMap(extras)
            Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    AviaTrackApplication.AVIA_TRACK_FB_LI = map["url"]
                    Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Push data no!")
        }
    }

    private fun aviaTrackBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}