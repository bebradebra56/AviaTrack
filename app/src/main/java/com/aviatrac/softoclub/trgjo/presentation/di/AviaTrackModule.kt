package com.aviatrac.softoclub.trgjo.presentation.di

import com.aviatrac.softoclub.trgjo.data.repo.AviaTrackRepository
import com.aviatrac.softoclub.trgjo.data.shar.AviaTrackSharedPreference
import com.aviatrac.softoclub.trgjo.data.utils.AviaTrackPushToken
import com.aviatrac.softoclub.trgjo.data.utils.AviaTrackSystemService
import com.aviatrac.softoclub.trgjo.domain.usecases.AviaTrackGetAllUseCase
import com.aviatrac.softoclub.trgjo.presentation.pushhandler.AviaTrackPushHandler
import com.aviatrac.softoclub.trgjo.presentation.ui.load.AviaTrackLoadViewModel
import com.aviatrac.softoclub.trgjo.presentation.ui.view.AviaTrackViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val aviaTrackModule = module {
    factory {
        AviaTrackPushHandler()
    }
    single {
        AviaTrackRepository()
    }
    single {
        AviaTrackSharedPreference(get())
    }
    factory {
        AviaTrackPushToken()
    }
    factory {
        AviaTrackSystemService(get())
    }
    factory {
        AviaTrackGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        AviaTrackViFun(get())
    }
    viewModel {
        AviaTrackLoadViewModel(get(), get(), get())
    }
}