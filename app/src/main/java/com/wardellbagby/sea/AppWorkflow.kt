package com.wardellbagby.sea

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.renderChild
import com.squareup.workflow.ui.ViewRegistry
import com.squareup.workflow.ui.modal.AlertContainer
import com.wardellbagby.sea.AppWorkflow.AppState
import com.wardellbagby.sea.AppWorkflow.AppState.RecentPlays
import com.wardellbagby.sea.recents.RecentsBinding
import com.wardellbagby.sea.recents.RecentsWorkflow
import dev.zacsweers.moshisealed.annotations.TypeLabel
import okio.ByteString.Companion.toByteString
import javax.inject.Inject

val MainViewRegistry = ViewRegistry(AlertContainer, RecentsBinding)

class AppWorkflow
@Inject constructor(
  private val recentsWorkflow: RecentsWorkflow,
  private val moshi: Moshi
) : StatefulWorkflow<Unit, AppState, Nothing, Any>() {

  @JsonClass(generateAdapter = true, generator = "sealed:type")
  sealed class AppState {

    @TypeLabel("recent")
    @JsonClass(generateAdapter = true)
    data class RecentPlays(val unused: Int = 0) : AppState()
  }

  override fun initialState(
    props: Unit,
    snapshot: Snapshot?
  ): AppState = snapshot?.toData(moshi) ?: RecentPlays()

  override fun render(
    props: Unit,
    state: AppState,
    context: RenderContext<AppState, Nothing>
  ): Any {
    val sink = context.actionSink

    return when (state) {
      is RecentPlays -> context.renderChild(recentsWorkflow)
    }
  }

  override fun snapshotState(state: AppState) = state.toSnapshot(moshi)
}

fun <T : Parcelable> T.snapshot(): Snapshot {
  val parcel = Parcel.obtain()
  writeToParcel(parcel, 0)
  try {
    return Snapshot.of(
        parcel.marshall()
            .toByteString()
    )
  } finally {
    parcel.recycle()
  }
}

inline fun <reified T : Parcelable> Snapshot.data(): T? {
  val parcel = Parcel.obtain()
  try {
    parcel.unmarshall(bytes.toByteArray(), 0, bytes.size)
    return parcel.readParcelable(T::class.java.classLoader)
  } finally {
    parcel.recycle()
  }
}

inline fun <reified T> Moshi.adapter(): JsonAdapter<T> = adapter(T::class.java)

inline fun <reified T> T.toSnapshot(moshi: Moshi): Snapshot {
  return Snapshot.of {
    moshi.adapter<T>()
        .toJson(this)
        .encodeToByteArray()
        .toByteString()
  }
}

inline fun <reified T> Snapshot.toData(moshi: Moshi): T? {
  return moshi.adapter<T>()
      .fromJson(
          bytes.toByteArray()
              .decodeToString()
      )
}