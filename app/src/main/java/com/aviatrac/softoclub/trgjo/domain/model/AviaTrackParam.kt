package com.aviatrac.softoclub.trgjo.domain.model

import com.google.gson.annotations.SerializedName


private const val OLYMPUS_VAULT_A = "com.aviatrac.softoclub"
private const val OLYMPUS_VAULT_B = "aviatrack-2d3bd"
data class AviaTrackParam (
    @SerializedName("af_id")
    val aviaTrackAfId: String,
    @SerializedName("bundle_id")
    val aviaTrackBundleId: String = OLYMPUS_VAULT_A,
    @SerializedName("os")
    val aviaTrackOs: String = "Android",
    @SerializedName("store_id")
    val aviaTrackStoreId: String = OLYMPUS_VAULT_A,
    @SerializedName("locale")
    val aviaTrackLocale: String,
    @SerializedName("push_token")
    val aviaTrackPushToken: String,
    @SerializedName("firebase_project_id")
    val aviaTrackFirebaseProjectId: String = OLYMPUS_VAULT_B,

    )