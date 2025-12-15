package com.aviatrac.softoclub.trgjo.domain.model

import com.google.gson.annotations.SerializedName


data class AviaTrackEntity (
    @SerializedName("ok")
    val aviaTrackOk: String,
    @SerializedName("url")
    val aviaTrackUrl: String,
    @SerializedName("expires")
    val aviaTrackExpires: Long,
)