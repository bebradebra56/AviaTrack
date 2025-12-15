package com.aviatrac.softoclub

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.aviatrac.softoclub.trgjo.AviaTrackGlobalLayoutUtil
import com.aviatrac.softoclub.trgjo.aviaTrackSetupSystemBars
import com.aviatrac.softoclub.trgjo.presentation.app.AviaTrackApplication
import com.aviatrac.softoclub.trgjo.presentation.pushhandler.AviaTrackPushHandler
import org.koin.android.ext.android.inject

class AviaTrackActivity : AppCompatActivity() {

    private val aviaTrackPushHandler by inject<AviaTrackPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        aviaTrackSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_avia_track)

        val aviaTrackRootView = findViewById<View>(android.R.id.content)
        AviaTrackGlobalLayoutUtil().aviaTrackAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(aviaTrackRootView) { aviaTrackView, aviaTrackInsets ->
            val aviaTrackSystemBars = aviaTrackInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val aviaTrackDisplayCutout = aviaTrackInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val aviaTrackIme = aviaTrackInsets.getInsets(WindowInsetsCompat.Type.ime())


            val aviaTrackTopPadding = maxOf(aviaTrackSystemBars.top, aviaTrackDisplayCutout.top)
            val aviaTrackLeftPadding = maxOf(aviaTrackSystemBars.left, aviaTrackDisplayCutout.left)
            val aviaTrackRightPadding = maxOf(aviaTrackSystemBars.right, aviaTrackDisplayCutout.right)
            window.setSoftInputMode(AviaTrackApplication.aviaTrackInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "ADJUST PUN")
                val aviaTrackBottomInset = maxOf(aviaTrackSystemBars.bottom, aviaTrackDisplayCutout.bottom)

                aviaTrackView.setPadding(aviaTrackLeftPadding, aviaTrackTopPadding, aviaTrackRightPadding, 0)

                aviaTrackView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = aviaTrackBottomInset
                }
            } else {
                Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "ADJUST RESIZE")

                val aviaTrackBottomInset = maxOf(aviaTrackSystemBars.bottom, aviaTrackDisplayCutout.bottom, aviaTrackIme.bottom)

                aviaTrackView.setPadding(aviaTrackLeftPadding, aviaTrackTopPadding, aviaTrackRightPadding, 0)

                aviaTrackView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = aviaTrackBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Activity onCreate()")
        aviaTrackPushHandler.aviaTrackHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            aviaTrackSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        aviaTrackSetupSystemBars()
    }
}