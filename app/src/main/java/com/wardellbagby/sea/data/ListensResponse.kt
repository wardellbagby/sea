package com.wardellbagby.sea.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListensResponse(
  val count: Int,
  val latest_listen_ts: Int?,
  val listens: List<Listen>,
  val user_id: String?
)

@JsonClass(generateAdapter = true)
data class NowPlayingResponse(
  val count: Int,
  val latest_listen_ts: Int?,
  val listens: List<Listen>
)