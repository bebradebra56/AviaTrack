package com.aviatrac.softoclub.trgjo.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aviatrac.softoclub.trgjo.presentation.app.AviaTrackApplication
import com.aviatrac.softoclub.trgjo.presentation.ui.load.AviaTrackLoadFragment
import org.koin.android.ext.android.inject

class AviaTrackV : Fragment(){

    private lateinit var aviaTrackPhoto: Uri
    private var aviaTrackFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val aviaTrackTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        aviaTrackFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        aviaTrackFilePathFromChrome = null
    }

    private val aviaTrackTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            aviaTrackFilePathFromChrome?.onReceiveValue(arrayOf(aviaTrackPhoto))
            aviaTrackFilePathFromChrome = null
        } else {
            aviaTrackFilePathFromChrome?.onReceiveValue(null)
            aviaTrackFilePathFromChrome = null
        }
    }

    private val aviaTrackDataStore by activityViewModels<AviaTrackDataStore>()


    private val aviaTrackViFun by inject<AviaTrackViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (aviaTrackDataStore.aviaTrackView.canGoBack()) {
                        aviaTrackDataStore.aviaTrackView.goBack()
                        Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "WebView can go back")
                    } else if (aviaTrackDataStore.aviaTrackViList.size > 1) {
                        Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "WebView can`t go back")
                        aviaTrackDataStore.aviaTrackViList.removeAt(aviaTrackDataStore.aviaTrackViList.lastIndex)
                        Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "WebView list size ${aviaTrackDataStore.aviaTrackViList.size}")
                        aviaTrackDataStore.aviaTrackView.destroy()
                        val previousWebView = aviaTrackDataStore.aviaTrackViList.last()
                        aviaTrackAttachWebViewToContainer(previousWebView)
                        aviaTrackDataStore.aviaTrackView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (aviaTrackDataStore.aviaTrackIsFirstCreate) {
            aviaTrackDataStore.aviaTrackIsFirstCreate = false
            aviaTrackDataStore.aviaTrackContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return aviaTrackDataStore.aviaTrackContainerView
        } else {
            return aviaTrackDataStore.aviaTrackContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "onViewCreated")
        if (aviaTrackDataStore.aviaTrackViList.isEmpty()) {
            aviaTrackDataStore.aviaTrackView = AviaTrackVi(requireContext(), object :
                AviaTrackCallBack {
                override fun aviaTrackHandleCreateWebWindowRequest(aviaTrackVi: AviaTrackVi) {
                    aviaTrackDataStore.aviaTrackViList.add(aviaTrackVi)
                    Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "WebView list size = ${aviaTrackDataStore.aviaTrackViList.size}")
                    Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "CreateWebWindowRequest")
                    aviaTrackDataStore.aviaTrackView = aviaTrackVi
                    aviaTrackVi.aviaTrackSetFileChooserHandler { callback ->
                        aviaTrackHandleFileChooser(callback)
                    }
                    aviaTrackAttachWebViewToContainer(aviaTrackVi)
                }

            }, aviaTrackWindow = requireActivity().window).apply {
                aviaTrackSetFileChooserHandler { callback ->
                    aviaTrackHandleFileChooser(callback)
                }
            }
            aviaTrackDataStore.aviaTrackView.aviaTrackFLoad(arguments?.getString(
                AviaTrackLoadFragment.AVIA_TRACK_D) ?: "")
//            ejvview.fLoad("www.google.com")
            aviaTrackDataStore.aviaTrackViList.add(aviaTrackDataStore.aviaTrackView)
            aviaTrackAttachWebViewToContainer(aviaTrackDataStore.aviaTrackView)
        } else {
            aviaTrackDataStore.aviaTrackViList.forEach { webView ->
                webView.aviaTrackSetFileChooserHandler { callback ->
                    aviaTrackHandleFileChooser(callback)
                }
            }
            aviaTrackDataStore.aviaTrackView = aviaTrackDataStore.aviaTrackViList.last()

            aviaTrackAttachWebViewToContainer(aviaTrackDataStore.aviaTrackView)
        }
        Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "WebView list size = ${aviaTrackDataStore.aviaTrackViList.size}")
    }

    private fun aviaTrackHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        aviaTrackFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Launching file picker")
                    aviaTrackTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "Launching camera")
                    aviaTrackPhoto = aviaTrackViFun.aviaTrackSavePhoto()
                    aviaTrackTakePhoto.launch(aviaTrackPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(AviaTrackApplication.AVIA_TRACK_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                aviaTrackFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun aviaTrackAttachWebViewToContainer(w: AviaTrackVi) {
        aviaTrackDataStore.aviaTrackContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            aviaTrackDataStore.aviaTrackContainerView.removeAllViews()
            aviaTrackDataStore.aviaTrackContainerView.addView(w)
        }
    }


}