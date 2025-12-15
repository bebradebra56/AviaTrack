package com.aviatrac.softoclub.trgjo.data.repo

import android.util.Log
import com.aviatrac.softoclub.trgjo.domain.model.AviaTrackEntity
import com.aviatrac.softoclub.trgjo.domain.model.AviaTrackParam
import com.aviatrac.softoclub.trgjo.presentation.app.AviaTrackApplication.Companion.AVIA_TRACK_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AviaTrackApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun aviaTrackGetClient(
        @Body jsonString: JsonObject,
    ): Call<AviaTrackEntity>
}


private const val AVIA_TRACK_MAIN = "https://aviiatrack.com/"
class AviaTrackRepository {

    suspend fun aviaTrackGetClient(
        aviaTrackParam: AviaTrackParam,
        aviaTrackConversion: MutableMap<String, Any>?
    ): AviaTrackEntity? {
        val gson = Gson()
        val api = aviaTrackGetApi(AVIA_TRACK_MAIN, null)

        val aviaTrackJsonObject = gson.toJsonTree(aviaTrackParam).asJsonObject
        aviaTrackConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            aviaTrackJsonObject.add(key, element)
        }
        return try {
            val aviaTrackRequest: Call<AviaTrackEntity> = api.aviaTrackGetClient(
                jsonString = aviaTrackJsonObject,
            )
            val aviaTrackResult = aviaTrackRequest.awaitResponse()
            Log.d(AVIA_TRACK_MAIN_TAG, "Retrofit: Result code: ${aviaTrackResult.code()}")
            if (aviaTrackResult.code() == 200) {
                Log.d(AVIA_TRACK_MAIN_TAG, "Retrofit: Get request success")
                Log.d(AVIA_TRACK_MAIN_TAG, "Retrofit: Code = ${aviaTrackResult.code()}")
                Log.d(AVIA_TRACK_MAIN_TAG, "Retrofit: ${aviaTrackResult.body()}")
                aviaTrackResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(AVIA_TRACK_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(AVIA_TRACK_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun aviaTrackGetApi(url: String, client: OkHttpClient?) : AviaTrackApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
