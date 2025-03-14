package com.costular.jellydroid.data.server

import com.costular.jellydroid.core.model.Server
import com.costular.jellydroid.data.db.ServerEntity
import java.util.UUID

fun ServerEntity.toDomain(): Server = Server(
    serverId = UUID.fromString(id),
    name = name,
    url = url,
    lastUsedAt = lastUsedAt,
    isSetupCompleted = isSetupCompleted,
    loginDisclaimer = loginDisclaimer
)

fun Server.toEntity(): ServerEntity = ServerEntity(
    id = serverId.toString(),
    name = name,
    url = url,
    lastUsedAt = lastUsedAt,
    isSetupCompleted = isSetupCompleted,
    loginDisclaimer = loginDisclaimer
)