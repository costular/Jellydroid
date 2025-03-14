package com.costular.jellydroid.data.server

import com.costular.jellydroid.core.model.Server
import java.time.Instant
import java.util.UUID

object ServerFixture {
    const val serverUrl = "http://jellydroid.io/test"

    val server = Server(
        serverId = UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
        name = "Test Server",
        url = serverUrl,
        lastUsedAt = Instant.now(),
        isSetupCompleted = true,
        loginDisclaimer = null,
    )

    val serverWithDisclaimer = Server(
        serverId = UUID.fromString("123e4567-e89b-12d3-a456-426655440000"),
        name = "Test Server",
        url = serverUrl,
        lastUsedAt = Instant.now(),
        isSetupCompleted = true,
        loginDisclaimer = "This is a test disclaimer for the server",
    )
}