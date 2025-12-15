package com.aviatrac.softoclub.trgjo

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.aviatrac.softoclub.trgjo.presentation.app.AviaTrackApplication

class AviaTrackGlobalLayoutUtil {

    private var aviaTrackMChildOfContent: View? = null
    private var aviaTrackUsableHeightPrevious = 0

    fun aviaTrackAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        aviaTrackMChildOfContent = content.getChildAt(0)

        aviaTrackMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val aviaTrackUsableHeightNow = aviaTrackComputeUsableHeight()
        if (aviaTrackUsableHeightNow != aviaTrackUsableHeightPrevious) {
            val aviaTrackUsableHeightSansKeyboard = aviaTrackMChildOfContent?.rootView?.height ?: 0
            val aviaTrackHeightDifference = aviaTrackUsableHeightSansKeyboard - aviaTrackUsableHeightNow

            if (aviaTrackHeightDifference > (aviaTrackUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(AviaTrackApplication.aviaTrackInputMode)
            } else {
                activity.window.setSoftInputMode(AviaTrackApplication.aviaTrackInputMode)
            }
//            mChildOfContent?.requestLayout()
            aviaTrackUsableHeightPrevious = aviaTrackUsableHeightNow
        }
    }

    private fun aviaTrackComputeUsableHeight(): Int {
        val r = Rect()
        aviaTrackMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}