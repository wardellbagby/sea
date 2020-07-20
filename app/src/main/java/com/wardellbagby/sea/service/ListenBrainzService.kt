package com.wardellbagby.sea.service

import com.wardellbagby.sea.data.ListenBrainzResponse
import com.wardellbagby.sea.data.ListensResponse
import com.wardellbagby.sea.data.NowPlayingResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ListenBrainzService {
  @ListenBrainzResponse
  @GET("/1/user/{user_name}/listens")
  suspend fun listeningActivityAsync(@Path("user_name") username: String): ListensResponse

  @ListenBrainzResponse
  @GET("/1/user/{user_name}/playing-now")
  suspend fun playingNowAsync(@Path("user_name") username: String): NowPlayingResponse
}