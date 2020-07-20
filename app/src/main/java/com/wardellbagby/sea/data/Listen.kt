package com.wardellbagby.sea.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Listen(
  val inserted_at: Int?,
  val listened_at: Int?,
  val recording_msid: String?,
  val track_metadata: TrackMetadata,
  val user_name: String?
)