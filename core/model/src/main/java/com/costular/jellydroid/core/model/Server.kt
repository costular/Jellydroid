package com.costular.jellydroid.core.model

import java.time.Instant
import java.util.UUID

data class Server(
    val serverId: UUID,
    val name: String,
    val url: String,
    val lastUsedAt: Instant,
    val isSetupCompleted: Boolean,
    val loginDisclaimer: String?,
)