package com.aviatrac.softoclub.trgjo.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class AviaTrackDataStore : ViewModel(){
    val aviaTrackViList: MutableList<AviaTrackVi> = mutableListOf()
    var aviaTrackIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var aviaTrackContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var aviaTrackView: AviaTrackVi

}