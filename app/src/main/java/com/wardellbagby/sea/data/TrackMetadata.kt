package com.wardellbagby.sea.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackMetadata(
  val additional_info: AdditionalInfo,
  val artist_name: String,
  val release_name: String?,
  val track_name: String
)