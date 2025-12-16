package com.aviatrac.softoclub.trgjo.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.aviatrac.softoclub.trgjo.presentation.di.aviaTrackModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface AviaTrackAppsFlyerState {
    data object AviaTrackDefault : AviaTrackAppsFlyerState
    data class AviaTrackSuccess(val aviaTrackData: MutableMap<String, Any>?) :
        AviaTrackAppsFlyerState

    data object AviaTrackError : AviaTrackAppsFlyerState
}

interface AviaTrackAppsApi {
    @Headers("Content-Type: application/json")
    @GET(AVIA_TRACK_LIN)
    fun aviaTrackGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val AVIA_TRACK_APP_DEV = "yeSgBNyoTXcFtCmopGQUEm"
private const val AVIA_TRACK_LIN = "com.aviatrac.softoclub"

class AviaTrackApplication : Application() {

    private var aviaTrackIsResumed = false
    private var aviaTrackDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        aviaTrackSetDebufLogger(appsflyer)
        aviaTrackMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        aviaTrackExtractDeepMap(p0.deepLink)
                        Log.d(AVIA_TRACK_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(AVIA_TRACK_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(AVIA_TRACK_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            AVIA_TRACK_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    Log.d(AVIA_TRACK_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = aviaTrackGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.aviaTrackGetClient(
                                    devkey = AVIA_TRACK_APP_DEV,
                                    deviceId = aviaTrackGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(AVIA_TRACK_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    aviaTrackResume(AviaTrackAppsFlyerState.AviaTrackError)
                                } else {
                                    aviaTrackResume(
                                        AviaTrackAppsFlyerState.AviaTrackSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(AVIA_TRACK_MAIN_TAG, "Error: ${d.message}")
                                aviaTrackResume(AviaTrackAppsFlyerState.AviaTrackError)
                            }
                        }
                    } else {
                        aviaTrackResume(AviaTrackAppsFlyerState.AviaTrackSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    Log.d(AVIA_TRACK_MAIN_TAG, "onConversionDataFail: $p0")
                    aviaTrackResume(AviaTrackAppsFlyerState.AviaTrackError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(AVIA_TRACK_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(AVIA_TRACK_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, AVIA_TRACK_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(AVIA_TRACK_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(AVIA_TRACK_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@AviaTrackApplication)
            modules(
                listOf(
                    aviaTrackModule
                )
            )
        }
    }

    private fun aviaTrackExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(AVIA_TRACK_MAIN_TAG, "Extracted DeepLink data: $map")
        aviaTrackDeepLinkData = map
    }

    private fun aviaTrackResume(state: AviaTrackAppsFlyerState) {
        if (state is AviaTrackAppsFlyerState.AviaTrackSuccess) {
            val convData = state.aviaTrackData ?: mutableMapOf()
            val deepData = aviaTrackDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!aviaTrackIsResumed) {
                aviaTrackIsResumed = true
                aviaTrackConversionFlow.value =
                    AviaTrackAppsFlyerState.AviaTrackSuccess(merged)
            }
        } else {
            if (!aviaTrackIsResumed) {
                aviaTrackIsResumed = true
                aviaTrackConversionFlow.value = state
            }
        }
    }

    private fun aviaTrackGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(AVIA_TRACK_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun aviaTrackSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun aviaTrackMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun aviaTrackGetApi(url: String, client: OkHttpClient?): AviaTrackAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {

        var aviaTrackInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val aviaTrackConversionFlow: MutableStateFlow<AviaTrackAppsFlyerState> = MutableStateFlow(
            AviaTrackAppsFlyerState.AviaTrackDefault
        )
        var AVIA_TRACK_FB_LI: String? = null
        const val AVIA_TRACK_MAIN_TAG = "AviaTrackMainTag"
    }
}