package com.wardellbagby.sea

import android.app.Application
import com.squareup.hephaestus.annotations.ContributesTo
import com.squareup.hephaestus.annotations.MergeComponent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wardellbagby.sea.data.ListenBrainzResponseFactory
import com.wardellbagby.sea.service.ListenBrainzService
import dagger.Module
import dagger.Provides
import dev.zacsweers.moshisealed.reflect.MoshiSealedJsonAdapterFactory
import okhttp3.HttpUrl
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Scope

class DefaultApplication : Application() {
  companion object {
    val APP_COMPONENT by lazy { DaggerAppComponent.create() }
  }
}

@Scope
annotation class AppScope

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
interface AppComponent {
  fun appWorkflow(): AppWorkflow
}

@ContributesTo(AppScope::class)
@Module
object AppModule {
  @Provides
  fun provideMoshi(): Moshi {
    return Moshi.Builder()
        .add(ListenBrainzResponseFactory)
        .add(MoshiSealedJsonAdapterFactory())
        .add(KotlinJsonAdapterFactory())
        .build()
  }
}

@ContributesTo(AppScope::class)
@Module
object NetworkModule {
  @Provides
  fun provideRetrofit(moshi: Moshi): Retrofit {
    //get("https://api.listenbrainz.org/")
    return Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(
            HttpUrl.Builder()
                .scheme("https")
                .host("api.listenbrainz.org")
                .build()
        )
        .build()
  }

  @Provides
  fun provideListenBrainzService(retrofit: Retrofit): ListenBrainzService = retrofit.create()
}