package com.costular.jellydroid.data.server

import arrow.core.Either
import com.costular.jellydroid.core.model.Server
import com.costular.jellydroid.data.error.AddServerError
import com.costular.jellydroid.data.error.JellydroidError
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ServerRepository {
    fun getServers(): Either<JellydroidError, Flow<List<Server>>>
    suspend fun addServer(server: Server): Either<JellydroidError, Unit>
    fun isServerConfigured(): Flow<Boolean>
    suspend fun removeServer(uuid: UUID)
}