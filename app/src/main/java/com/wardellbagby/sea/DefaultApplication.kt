package com.wardellbagby.sea

import android.app.Application
import androidx.ui.foundation.TextFieldValue
import androidx.ui.text.TextRange
import com.squareup.hephaestus.annotations.ContributesTo
import com.squareup.hephaestus.annotations.MergeComponent
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
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
        .add(TextFieldValueAdapter)
        .build()
  }
}

@ContributesTo(AppScope::class)
@Module
object NetworkModule {
  @Provides
  fun provideRetrofit(moshi: Moshi): Retrofit {
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

private object TextFieldValueAdapter {
  @ToJson fun toJson(value: TextFieldValue): Map<String, String> {
    return mapOf(
        "text" to value.text,
        "start" to value.selection.start.toString(),
        "end" to value.selection.end.toString()
    )
  }

  @Suppress("MapGetWithNotNullAssertionOperator")
  @FromJson fun fromJson(value: Map<String, String>): TextFieldValue {
    return TextFieldValue(
        text = value["text"]!!,
        selection = TextRange(
            start = value["start"]!!.toInt(),
            end = value["end"]!!.toInt()
        )
    )
  }
}