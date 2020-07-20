package com.wardellbagby.sea.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AdditionalInfo(
  val artist_mbids: List<String>?,
  val artist_msid: String?,
  val isrc: String?,
  val recording_mbid: String?,
  val recording_msid: String?,
  val release_group_mbid: String?,
  val release_mbid: String?,
  val release_msid: String?,
  val spotify_id: String?,
  val tags: List<String>?,
  val track_mbid: String?,
  val tracknumber: String?,
  val work_mbids: List<String>?
)