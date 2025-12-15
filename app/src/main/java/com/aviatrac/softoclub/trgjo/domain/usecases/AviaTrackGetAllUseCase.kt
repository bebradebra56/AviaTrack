package com.aviatrac.softoclub.trgjo.domain.usecases

import android.util.Log
import com.aviatrac.softoclub.trgjo.data.repo.AviaTrackRepository
import com.aviatrac.softoclub.trgjo.data.utils.AviaTrackPushToken
import com.aviatrac.softoclub.trgjo.data.utils.AviaTrackSystemService
import com.aviatrac.softoclub.trgjo.domain.model.AviaTrackEntity
import com.aviatrac.softoclub.trgjo.domain.model.AviaTrackParam
import com.aviatrac.softoclub.trgjo.presentation.app.AviaTrackApplication

class AviaTrackGetAllUseCase(
    private val aviaTrackRepository: AviaTrackRepository,
    private val aviaTrackSystemService: AviaTrackSystemService,
    private val aviaTrackPushToken: AviaTrackPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : AviaTrackEntity?{
        val params = AviaTrackParam(
            aviaTrackLocale = aviaTrackSystemService.aviaTrackGetLocale(),
            aviaTrackPushToken = aviaTrackPushToken.aviaTrackGetToken(),
            aviaTrackAfId = aviaTrackSystemService.aviaTrackGetAppsflyerId()
        )
        Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Params for request: $params")
        return aviaTrackRepository.aviaTrackGetClient(params, conversion)
    }



}