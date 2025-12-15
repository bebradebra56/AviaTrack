package com.aviatrac.softoclub.trgjo.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aviatrac.softoclub.trgjo.data.shar.AviaTrackSharedPreference
import com.aviatrac.softoclub.trgjo.data.utils.AviaTrackSystemService
import com.aviatrac.softoclub.trgjo.domain.usecases.AviaTrackGetAllUseCase
import com.aviatrac.softoclub.trgjo.presentation.app.AviaTrackAppsFlyerState
import com.aviatrac.softoclub.trgjo.presentation.app.AviaTrackApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AviaTrackLoadViewModel(
    private val aviaTrackGetAllUseCase: AviaTrackGetAllUseCase,
    private val aviaTrackSharedPreference: AviaTrackSharedPreference,
    private val aviaTrackSystemService: AviaTrackSystemService
) : ViewModel() {

    private val _aviaTrackHomeScreenState: MutableStateFlow<AviaTrackHomeScreenState> =
        MutableStateFlow(AviaTrackHomeScreenState.AviaTrackLoading)
    val aviaTrackHomeScreenState = _aviaTrackHomeScreenState.asStateFlow()

    private var aviaTrackGetApps = false


    init {
        viewModelScope.launch {
            when (aviaTrackSharedPreference.aviaTrackAppState) {
                0 -> {
                    if (aviaTrackSystemService.aviaTrackIsOnline()) {
                        AviaTrackApplication.aviaTrackConversionFlow.collect {
                            when(it) {
                                AviaTrackAppsFlyerState.AviaTrackDefault -> {}
                                AviaTrackAppsFlyerState.AviaTrackError -> {
                                    aviaTrackSharedPreference.aviaTrackAppState = 2
                                    _aviaTrackHomeScreenState.value =
                                        AviaTrackHomeScreenState.AviaTrackError
                                    aviaTrackGetApps = true
                                }
                                is AviaTrackAppsFlyerState.AviaTrackSuccess -> {
                                    if (!aviaTrackGetApps) {
                                        aviaTrackGetData(it.aviaTrackData)
                                        aviaTrackGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _aviaTrackHomeScreenState.value =
                            AviaTrackHomeScreenState.AviaTrackNotInternet
                    }
                }
                1 -> {
                    if (aviaTrackSystemService.aviaTrackIsOnline()) {
                        if (AviaTrackApplication.AVIA_TRACK_FB_LI != null) {
                            _aviaTrackHomeScreenState.value =
                                AviaTrackHomeScreenState.AviaTrackSuccess(
                                    AviaTrackApplication.AVIA_TRACK_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > aviaTrackSharedPreference.aviaTrackExpired) {
                            Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Current time more then expired, repeat request")
                            AviaTrackApplication.aviaTrackConversionFlow.collect {
                                when(it) {
                                    AviaTrackAppsFlyerState.AviaTrackDefault -> {}
                                    AviaTrackAppsFlyerState.AviaTrackError -> {
                                        _aviaTrackHomeScreenState.value =
                                            AviaTrackHomeScreenState.AviaTrackSuccess(
                                                aviaTrackSharedPreference.aviaTrackSavedUrl
                                            )
                                        aviaTrackGetApps = true
                                    }
                                    is AviaTrackAppsFlyerState.AviaTrackSuccess -> {
                                        if (!aviaTrackGetApps) {
                                            aviaTrackGetData(it.aviaTrackData)
                                            aviaTrackGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Current time less then expired, use saved url")
                            _aviaTrackHomeScreenState.value =
                                AviaTrackHomeScreenState.AviaTrackSuccess(
                                    aviaTrackSharedPreference.aviaTrackSavedUrl
                                )
                        }
                    } else {
                        _aviaTrackHomeScreenState.value =
                            AviaTrackHomeScreenState.AviaTrackNotInternet
                    }
                }
                2 -> {
                    _aviaTrackHomeScreenState.value =
                        AviaTrackHomeScreenState.AviaTrackError
                }
            }
        }
    }


    private suspend fun aviaTrackGetData(conversation: MutableMap<String, Any>?) {
        val aviaTrackData = aviaTrackGetAllUseCase.invoke(conversation)
        if (aviaTrackSharedPreference.aviaTrackAppState == 0) {
            if (aviaTrackData == null) {
                aviaTrackSharedPreference.aviaTrackAppState = 2
                _aviaTrackHomeScreenState.value =
                    AviaTrackHomeScreenState.AviaTrackError
            } else {
                aviaTrackSharedPreference.aviaTrackAppState = 1
                aviaTrackSharedPreference.apply {
                    aviaTrackExpired = aviaTrackData.aviaTrackExpires
                    aviaTrackSavedUrl = aviaTrackData.aviaTrackUrl
                }
                _aviaTrackHomeScreenState.value =
                    AviaTrackHomeScreenState.AviaTrackSuccess(aviaTrackData.aviaTrackUrl)
            }
        } else  {
            if (aviaTrackData == null) {
                _aviaTrackHomeScreenState.value =
                    AviaTrackHomeScreenState.AviaTrackSuccess(aviaTrackSharedPreference.aviaTrackSavedUrl)
            } else {
                aviaTrackSharedPreference.apply {
                    aviaTrackExpired = aviaTrackData.aviaTrackExpires
                    aviaTrackSavedUrl = aviaTrackData.aviaTrackUrl
                }
                _aviaTrackHomeScreenState.value =
                    AviaTrackHomeScreenState.AviaTrackSuccess(aviaTrackData.aviaTrackUrl)
            }
        }
    }


    sealed class AviaTrackHomeScreenState {
        data object AviaTrackLoading : AviaTrackHomeScreenState()
        data object AviaTrackError : AviaTrackHomeScreenState()
        data class AviaTrackSuccess(val data: String) : AviaTrackHomeScreenState()
        data object AviaTrackNotInternet: AviaTrackHomeScreenState()
    }
}