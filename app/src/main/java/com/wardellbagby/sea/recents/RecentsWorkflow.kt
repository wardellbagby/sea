package com.wardellbagby.sea.recents

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.TextField
import androidx.ui.foundation.TextFieldValue
import androidx.ui.foundation.drawBorder
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.ColumnScope.weight
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.Card
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.text.TextRange
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.action
import com.squareup.workflow.asWorker
import com.squareup.workflow.ui.compose.composedViewFactory
import com.wardellbagby.sea.data.Listen
import com.wardellbagby.sea.getOrNull
import com.wardellbagby.sea.recents.RecentsWorkflow.RecentsRendering
import com.wardellbagby.sea.recents.RecentsWorkflow.State
import com.wardellbagby.sea.toData
import com.wardellbagby.sea.toSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle.SHORT
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

class RecentsWorkflow
@Inject constructor(
  private val recentsRepository: RecentsRepository,
  private val moshi: Moshi
) : StatefulWorkflow<Unit, State, Nothing, RecentsRendering>() {

  @JsonClass(generateAdapter = true)
  data class State(
    val username: TextFieldValue,
    val nowPlaying: Listen?,
    val listeningHistory: List<Listen>?
  )

  data class RecentsRendering(
    val username: TextFieldValue,
    val nowPlaying: Listen?,
    val listens: List<Listen>,
    val onUsernameChanged: (TextFieldValue) -> Unit
  )

  override fun initialState(
    props: Unit,
    snapshot: Snapshot?
  ): State {
    return snapshot?.toData(moshi) ?: State(
        username = TextFieldValue("wardellbagby", TextRange(12, 12)),
        listeningHistory = null,
        nowPlaying = null
    )
  }

  override fun snapshotState(state: State): Snapshot = state.toSnapshot(moshi)

  override fun render(
    props: Unit,
    state: State,
    context: RenderContext<State, Nothing>
  ): RecentsRendering {
    context.runningWorker(
        recentsRepository.recentListens(state.username.text)
            .repeatEvery(5)
            .asWorker(),
        key = state.username.text
    ) {
      action {
        nextState = nextState.copy(listeningHistory = it)
      }
    }
    context.runningWorker(
        recentsRepository.nowPlaying(state.username.text)
            .repeatEvery(5)
            .asWorker(),
        key = state.username.text
    ) {
      action {
        nextState = nextState.copy(nowPlaying = it.getOrNull())
      }
    }
    return RecentsRendering(
        username = state.username,
        nowPlaying = state.nowPlaying,
        listens = state.listeningHistory ?: listOf(),
        onUsernameChanged = {
          context.actionSink.send(action {
            nextState = nextState.copy(username = it)
          })
        }
    )
  }
}

@OptIn(ExperimentalTime::class, FlowPreview::class)
fun <T> Flow<T>.repeatEvery(seconds: Int): Flow<T> {
  return flow {
    while (true) {
      emit(Unit)
      delay(DurationUnit.SECONDS.toMillis(seconds.toLong()))
    }
  }
      .flowOn(Dispatchers.IO)
      .flatMapConcat { this }
}

val RecentsBinding = composedViewFactory<RecentsRendering> { rendering, _ ->
  Column(modifier = Modifier.padding(horizontal = 16.dp)) {
    Text(
        text = "Listening History",
        modifier = Modifier.padding(vertical = 16.dp),
        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
    )

    Text(
        text = "Username",
        modifier = Modifier.padding(vertical = 8.dp),
        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

    )
    TextField(
        value = rendering.username,
        onValueChange = rendering.onUsernameChanged,
        modifier = Modifier.fillMaxWidth()
            .drawBorder(
                size = 1.dp,
                shape = RoundedCornerShape(1.dp),
                color = MaterialTheme.colors.onBackground
            )
            .padding(16.dp)
    )

    if (rendering.listens.isEmpty() && rendering.nowPlaying == null) {
      Text(
          text = "No listening history...",
          modifier = Modifier.padding(vertical = 8.dp),
          style = TextStyle(fontSize = 16.sp)
      )
    }

    nowPlaying(nowPlaying = rendering.nowPlaying)
    recentPlays(listens = rendering.listens)
  }
}

@Composable
private fun listenCard(listen: Listen) {
  Card(
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.padding(vertical = 8.dp)
  ) {
    Box(gravity = Alignment.Center) {
      ListItem(
          text = listen.track_metadata.track_name,
          secondaryText = listen.track_metadata.artist_name,
          overlineText = listen.track_metadata.release_name,
          metaText = listen.listened_at?.let {
            Instant.ofEpochSecond(it.toLong())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .let { localDateTime ->
                  if (localDateTime
                          .toLocalDate()
                          .isEqual(LocalDate.now())
                  )
                    localDateTime
                        .toLocalTime()
                        .format(DateTimeFormatter.ofLocalizedTime(SHORT))
                  else localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(SHORT))
                }
          }
      )
    }
  }
}

@Composable
private fun nowPlaying(nowPlaying: Listen?) {
  if (nowPlaying != null) {
    Text(
        text = "Now Playing",
        modifier = Modifier.padding(vertical = 8.dp),
        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

    )
    listenCard(listen = nowPlaying)
  }
}

@Composable
private fun recentPlays(listens: List<Listen>) {
  if (listens.isNotEmpty()) {
    Text(
        text = "Recent Plays",
        modifier = Modifier.padding(vertical = 8.dp),
        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    )
    AdapterList(data = listens, modifier = Modifier.weight(1f)) { listen ->
      listenCard(
          listen
      )
    }
  }
}