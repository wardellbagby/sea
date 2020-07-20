package com.wardellbagby.sea.data

import android.annotation.SuppressLint
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonReader.Options
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(RUNTIME)
@JsonQualifier
@Target(FUNCTION, CLASS)
annotation class ListenBrainzResponse

object ListenBrainzResponseFactory : JsonAdapter.Factory {
  override fun create(
    type: Type,
    annotations: MutableSet<out Annotation>,
    moshi: Moshi
  ): JsonAdapter<*>? {
    val delegateAnnotations = Types.nextAnnotations(
        annotations,
        ListenBrainzResponse::class.java
    ) ?: return null
    val delegate = moshi.nextAdapter<Any>(
        this,
        type,
        delegateAnnotations
    )
    return ListenBrainzResponseJsonAdapter(delegate)
  }

  private class ListenBrainzResponseJsonAdapter(
    val delegate: JsonAdapter<*>
  ) : JsonAdapter<Any>() {
    @SuppressLint("CheckResult")
    override fun fromJson(reader: JsonReader): Any? {
      reader.beginObject()
      reader.selectName(Options.of("payload"))
      val actualResponse = delegate.fromJson(reader)
      reader.endObject()
      return actualResponse
    }

    override fun toJson(
      writer: JsonWriter?,
      value: Any?
    ) =
      error("Shouldn't be writing a ListenBrainzResponse.")
  }
}