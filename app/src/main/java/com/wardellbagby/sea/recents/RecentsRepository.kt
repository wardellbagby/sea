package com.wardellbagby.sea.recents

import com.wardellbagby.sea.AppScope
import com.wardellbagby.sea.SingleIn
import com.wardellbagby.sea.data.Listen
import com.wardellbagby.sea.service.ListenBrainzService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Optional
import javax.inject.Inject

@SingleIn(AppScope::class)
class RecentsRepository
@Inject constructor(
  private val service: ListenBrainzService
) {
  fun recentListens(username: String): Flow<List<Listen>> {
    return flow {
      if (username.isBlank()) {
        emit(listOf())
        return@flow
      }
      val response = service.listeningActivityAsync(username)
      emit(response.listens)
    }
        .catch { emit(listOf()) }
        .flowOn(Dispatchers.IO)
  }

  fun nowPlaying(username: String): Flow<Optional<Listen>> {
    return flow {
      if (username.isBlank()) {
        emit(Optional.empty())
        return@flow
      }
      val response = service.playingNowAsync(username)
      emit(Optional.ofNullable(response.listens.firstOrNull()))
    }
        .catch { emit(Optional.empty()) }
        .flowOn(Dispatchers.IO)
  }
}