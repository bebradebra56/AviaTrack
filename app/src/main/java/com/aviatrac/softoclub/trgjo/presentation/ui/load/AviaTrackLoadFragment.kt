package com.aviatrac.softoclub.trgjo.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.aviatrac.softoclub.MainActivity
import com.aviatrac.softoclub.R
import com.aviatrac.softoclub.databinding.FragmentLoadAviaTrackBinding
import com.aviatrac.softoclub.trgjo.data.shar.AviaTrackSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class AviaTrackLoadFragment : Fragment(R.layout.fragment_load_avia_track) {
    private lateinit var aviaTrackLoadBinding: FragmentLoadAviaTrackBinding

    private val aviaTrackLoadViewModel by viewModel<AviaTrackLoadViewModel>()

    private val aviaTrackSharedPreference by inject<AviaTrackSharedPreference>()

    private var aviaTrackUrl = ""

    private val aviaTrackRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            aviaTrackNavigateToSuccess(aviaTrackUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                aviaTrackSharedPreference.aviaTrackNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                aviaTrackNavigateToSuccess(aviaTrackUrl)
            } else {
                aviaTrackNavigateToSuccess(aviaTrackUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aviaTrackLoadBinding = FragmentLoadAviaTrackBinding.bind(view)

        aviaTrackLoadBinding.aviaTrackGrandButton.setOnClickListener {
            val aviaTrackPermission = Manifest.permission.POST_NOTIFICATIONS
            aviaTrackRequestNotificationPermission.launch(aviaTrackPermission)
            aviaTrackSharedPreference.aviaTrackNotificationRequestedBefore = true
        }

        aviaTrackLoadBinding.aviaTrackSkipButton.setOnClickListener {
            aviaTrackSharedPreference.aviaTrackNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            aviaTrackNavigateToSuccess(aviaTrackUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                aviaTrackLoadViewModel.aviaTrackHomeScreenState.collect {
                    when (it) {
                        is AviaTrackLoadViewModel.AviaTrackHomeScreenState.AviaTrackLoading -> {

                        }

                        is AviaTrackLoadViewModel.AviaTrackHomeScreenState.AviaTrackError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is AviaTrackLoadViewModel.AviaTrackHomeScreenState.AviaTrackSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val aviaTrackPermission = Manifest.permission.POST_NOTIFICATIONS
                                val aviaTrackPermissionRequestedBefore = aviaTrackSharedPreference.aviaTrackNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), aviaTrackPermission) == PackageManager.PERMISSION_GRANTED) {
                                    aviaTrackNavigateToSuccess(it.data)
                                } else if (!aviaTrackPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > aviaTrackSharedPreference.aviaTrackNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    aviaTrackLoadBinding.aviaTrackNotiGroup.visibility = View.VISIBLE
                                    aviaTrackLoadBinding.aviaTrackLoadingGroup.visibility = View.GONE
                                    aviaTrackUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(aviaTrackPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > aviaTrackSharedPreference.aviaTrackNotificationRequest) {
                                        aviaTrackLoadBinding.aviaTrackNotiGroup.visibility = View.VISIBLE
                                        aviaTrackLoadBinding.aviaTrackLoadingGroup.visibility = View.GONE
                                        aviaTrackUrl = it.data
                                    } else {
                                        aviaTrackNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    aviaTrackNavigateToSuccess(it.data)
                                }
                            } else {
                                aviaTrackNavigateToSuccess(it.data)
                            }
                        }

                        AviaTrackLoadViewModel.AviaTrackHomeScreenState.AviaTrackNotInternet -> {
                            aviaTrackLoadBinding.aviaTrackStateGroup.visibility = View.VISIBLE
                            aviaTrackLoadBinding.aviaTrackLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun aviaTrackNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_aviaTrackLoadFragment_to_aviaTrackV,
            bundleOf(AVIA_TRACK_D to data)
        )
    }

    companion object {
        const val AVIA_TRACK_D = "aviaTrackData"
    }
}