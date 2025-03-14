package com.costular.jellydroid.data.server

import arrow.core.Either
import com.costular.jellydroid.core.model.Server
import com.costular.jellydroid.data.db.ServerDao
import com.costular.jellydroid.data.db.ServerEntity
import com.costular.jellydroid.data.error.AddServerError
import com.costular.jellydroid.data.error.JellydroidError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.extensions.brandingApi
import org.jellyfin.sdk.discovery.RecommendedServerInfoScore.GOOD
import org.jellyfin.sdk.discovery.RecommendedServerInfoScore.GREAT
import org.jellyfin.sdk.discovery.RecommendedServerInfoScore.OK
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class ServerRepositoryImpl @Inject constructor(
    private val jellyfin: Jellyfin,
    private val serverDao: ServerDao,
) : ServerRepository {
    override fun getServers(): Either<JellydroidError, Flow<List<Server>>> {
        return runCatching {
            val servers = serverDao.observeAll().map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
            Either.Right(servers)
        }.getOrElse {
            Either.Left(JellydroidError.UnknownError)
        }
    }

    override suspend fun addServer(server: Server): Either<JellydroidError, Unit> {
        val candidates = jellyfin.discovery.getAddressCandidates(server.url)
        val recommendations = jellyfin.discovery.getRecommendedServers(candidates)

        val selectedServer = recommendations.firstOrNull { it.score == GREAT }
            ?: recommendations.firstOrNull { it.score == GOOD }
            ?: recommendations.firstOrNull { it.score == OK }
            ?: recommendations.firstOrNull()
        //FIXME: In the future we should handle bad recommendations and expose the reasons to the client

        if (selectedServer == null) {
            return Either.Left(AddServerError.NotFound)
        }

        val systemInfo = selectedServer.systemInfo.getOrNull()

        if (systemInfo == null) {
            return Either.Left(AddServerError.NotFound)
        }

        val api = jellyfin.createApi(selectedServer.address)
        val branding = api.brandingApi.getBrandingOptions().content
        val id = systemInfo.id!!

        val serverEntity = ServerEntity(
            id = id,
            name = systemInfo.serverName ?: JELLYFIN_SERVER_DEFAULT_NAME,
            url = selectedServer.address,
            lastUsedAt = Instant.now(),
            isSetupCompleted = systemInfo.startupWizardCompleted ?: true,
            loginDisclaimer = branding.loginDisclaimer,
        )

        return runCatching {
            serverDao.addServer(serverEntity)
            Either.Right(Unit)
        }.getOrElse { throwable ->
            Either.Left(JellydroidError.UnknownError)
        }
    }

    override fun isServerConfigured(): Flow<Boolean> {
        return serverDao.serverCount().map { it > 0 }
    }

    override suspend fun removeServer(uuid: UUID) {
        serverDao.deleteServer(uuid.toString())
    }

    companion object {
        private const val JELLYFIN_SERVER_DEFAULT_NAME = "Jellyfin Server"
    }
}