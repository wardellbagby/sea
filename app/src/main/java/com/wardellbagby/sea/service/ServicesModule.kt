package com.wardellbagby.sea.service

import com.squareup.hephaestus.annotations.ContributesTo
import com.wardellbagby.sea.AppScope
import dagger.Module
import retrofit2.Retrofit

@Module
@ContributesTo(AppScope::class)
interface ServicesModule {
  fun listenBrainzService(): ListenBrainzService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.listenbrainz.org")
        .build()

    return retrofit.create(ListenBrainzService::class.java)
  }
}