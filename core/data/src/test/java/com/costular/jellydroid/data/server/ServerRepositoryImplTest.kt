package com.costular.jellydroid.data.server

import arrow.core.Either
import com.costular.jellydroid.core.model.Server
import com.costular.jellydroid.data.db.ServerDao
import com.costular.jellydroid.data.db.ServerEntity
import com.costular.jellydroid.data.error.AddServerError
import com.costular.jellydroid.data.error.JellydroidError
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.Response
import org.jellyfin.sdk.api.client.extensions.brandingApi
import org.jellyfin.sdk.api.operations.BrandingApi
import org.jellyfin.sdk.discovery.DiscoveryService
import org.jellyfin.sdk.discovery.RecommendedServerInfo
import org.jellyfin.sdk.discovery.RecommendedServerInfoScore
import org.jellyfin.sdk.model.api.BrandingOptions
import org.jellyfin.sdk.model.api.PublicSystemInfo
import org.jellyfin.sdk.JellyfinOptions
import org.jellyfin.sdk.model.ClientInfo
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.UUID

class ServerRepositoryImplTest {
    private lateinit var sut: ServerRepository

    private val jellyfin: Jellyfin = mockk()
    private val serverDao: ServerDao = mockk()
    private val discoveryService: DiscoveryService = mockk()
    private val apiClient: ApiClient = mockk()
    private val brandingApiClient = mockk<BrandingApi>()
    private val jellyfinOptions: JellyfinOptions = mockk()
    private val clientInfo: ClientInfo = mockk()

    @Before
    fun setUp() {
        every { jellyfin.discovery } returns discoveryService
        every { jellyfin.options } returns jellyfinOptions
        every { jellyfinOptions.clientInfo } returns clientInfo
        every { jellyfin.createApi(any(), any(), any(), any(), any()) } returns apiClient
        every { jellyfin.createApi(any()) } returns apiClient
        every { apiClient.brandingApi } returns brandingApiClient

        sut = ServerRepositoryImpl(
            jellyfin = jellyfin,
            serverDao = serverDao,
        )
    }

    @Test
    fun `Should return server list when getServers is called given successful dao operation`() = runTest {
        val serverEntities = listOf(
            ServerEntity(
                id = "550e8400-e29b-41d4-a716-446655440000",
                name = "Test Server 1",
                url = "http://test1.com",
                lastUsedAt = Instant.now(),
                isSetupCompleted = true,
                loginDisclaimer = null
            ),
            ServerEntity(
                id = "550e8400-e29b-41d4-a716-446655422928",
                name = "Test Server 2",
                url = "http://test2.com",
                lastUsedAt = Instant.now(),
                isSetupCompleted = false,
                loginDisclaimer = "disclaimer"
            )
        )

        every { serverDao.observeAll() } returns flowOf(serverEntities)

        val result = sut.getServers()

        result.shouldBeTypeOf<Either.Right<Flow<List<Server>>>>()
        val servers = result.value

        val flowResult = mutableListOf<List<Server>>()
        servers.collect { flowResult.add(it) }

        flowResult.shouldHaveSize(1)
        flowResult.first().shouldHaveSize(2)
        flowResult.first()[0].serverId shouldBe UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        flowResult.first()[0].name shouldBe "Test Server 1"
        flowResult.first()[0].url shouldBe "http://test1.com"
        flowResult.first()[0].isSetupCompleted shouldBe true
        flowResult.first()[0].loginDisclaimer shouldBe null

        flowResult.first()[1].serverId shouldBe UUID.fromString("550e8400-e29b-41d4-a716-446655422928")
        flowResult.first()[1].name shouldBe "Test Server 2"
        flowResult.first()[1].url shouldBe "http://test2.com"
        flowResult.first()[1].isSetupCompleted shouldBe false
        flowResult.first()[1].loginDisclaimer shouldBe "disclaimer"

        verify { serverDao.observeAll() }
    }

    @Test
    fun `Should return error when getServers fails given dao throws exception`() = runTest {
        every { serverDao.observeAll() } throws RuntimeException("Database error")

        val result = sut.getServers()

        result.shouldBeTypeOf<Either.Left<JellydroidError>>()
        result.value shouldBe JellydroidError.UnknownError

        verify { serverDao.observeAll() }
    }

    @Test
    fun `Should add server successfully when addServer is called given great recommendation score`() = runTest {
        val testUrl = ServerFixture.serverUrl
        val serverUUID = "550e8400-e29b-41d4-a716-446655440000"
        
        givenAddressCandidates(testUrl)
        givenRecommendedServerWithScore(testUrl, serverUUID, "Test Server", true, RecommendedServerInfoScore.GREAT)
        givenSuccessfulBrandingResponse(null)
        givenDaoAddsServerSuccessfully()

        val result = sut.addServer(ServerFixture.server)

        result.shouldBeTypeOf<Either.Right<Unit>>()
        verifyServerAddedWithProperties(serverUUID, "Test Server", testUrl, true)
    }

    @Test
    fun `Should add server successfully with default name when addServer is called given server name is null`() = runTest {
        val testUrl = ServerFixture.serverUrl
        val serverUUID = "550e8400-e29b-41d4-a716-446655440000"
        
        givenAddressCandidates(testUrl)
        givenRecommendedServerWithScore(testUrl, serverUUID, null, true, RecommendedServerInfoScore.GREAT)
        givenSuccessfulBrandingResponse("Login disclaimer")
        givenDaoAddsServerSuccessfully()

        val result = sut.addServer(ServerFixture.server)

        result.shouldBeTypeOf<Either.Right<Unit>>()
        verifyServerAddedWithProperties(serverUUID, "Jellyfin Server", testUrl, true, "Login disclaimer")
    }

    @Test
    fun `Should add server successfully when addServer is called given good recommendation score`() = runTest {
        val testUrl = ServerFixture.serverUrl
        val serverUUID = ServerFixture.server.serverId.toString()
        
        givenAddressCandidates(testUrl)
        val greatServer = mockk<RecommendedServerInfo> {
            every { score } returns RecommendedServerInfoScore.OK
        }
        
        val goodServer = givenSuccessfulRecommendedServer(
            testUrl, 
            serverUUID, 
            "Test Server", 
            true, 
            RecommendedServerInfoScore.GOOD
        )

        givenRecommendedServers(listOf(greatServer, goodServer))
        givenSuccessfulBrandingResponse(null)
        givenDaoAddsServerSuccessfully()

        val result = sut.addServer(ServerFixture.server)

        result.shouldBeTypeOf<Either.Right<Unit>>()
        coVerify { serverDao.addServer(any()) }
    }

    @Test
    fun `Should add server successfully when addServer is called given OK recommendation score`() = runTest {
        val testUrl = ServerFixture.serverUrl
        val serverUUID = ServerFixture.server.serverId.toString()
        
        givenAddressCandidates(testUrl)
        givenRecommendedServerWithScore(testUrl, serverUUID, "Test Server", false, RecommendedServerInfoScore.OK)
        givenSuccessfulBrandingResponse(null)
        givenDaoAddsServerSuccessfully()

        val result = sut.addServer(ServerFixture.server)

        result.shouldBeTypeOf<Either.Right<Unit>>()
        verifyServerAddedWithProperties(serverUUID, "Test Server", testUrl, false)
    }

    @Test
    fun `Should add server successfully when addServer is called given any recommendation score`() = runTest {
        val testUrl = ServerFixture.serverUrl
        val serverUUID = "550e8400-e29b-41d4-a716-446655440000"
        
        givenAddressCandidates(testUrl)
        givenRecommendedServerWithScore(testUrl, serverUUID, "Test Server", true, RecommendedServerInfoScore.BAD)
        givenSuccessfulBrandingResponse(null)
        givenDaoAddsServerSuccessfully()

        val result = sut.addServer(ServerFixture.server)

        result.shouldBeTypeOf<Either.Right<Unit>>()
        coVerify { serverDao.addServer(any()) }
    }

    @Test
    fun `Should return NotFound error when addServer is called given empty recommendations`() = runTest {
        val testUrl = ServerFixture.serverUrl
        val server = ServerFixture.server
        
        givenAddressCandidates(testUrl)
        givenRecommendedServers(emptyList())

        val result = sut.addServer(server)

        result.shouldBeTypeOf<Either.Left<AddServerError>>()
        result.value shouldBe AddServerError.NotFound
        coVerify(exactly = 0) { serverDao.addServer(any()) }
    }

    @Test
    fun `Should return NotFound error when addServer is called given system info is null`() = runTest {
        val testUrl = ServerFixture.serverUrl
        val server = ServerFixture.server
        
        givenAddressCandidates(testUrl)
        givenRecommendedServerWithFailedSystemInfo(testUrl)
        
        val result = sut.addServer(server)

        result.shouldBeTypeOf<Either.Left<AddServerError>>()
        result.value shouldBe AddServerError.NotFound
        coVerify(exactly = 0) { serverDao.addServer(any()) }
    }

    @Test
    fun `Should return UnknownError when addServer fails given dao throws exception`() = runTest {
        val testUrl = ServerFixture.serverUrl
        val serverUUID = "550e8400-e29b-41d4-a716-446655440000"
        
        givenAddressCandidates(testUrl)
        givenRecommendedServerWithScore(testUrl, serverUUID, "Test Server", true, RecommendedServerInfoScore.GREAT)
        givenSuccessfulBrandingResponse(null)
        givenDaoThrowsException()

        val result = sut.addServer(ServerFixture.server)

        result.shouldBeTypeOf<Either.Left<JellydroidError>>()
        result.value shouldBe JellydroidError.UnknownError
    }
    
    @Test
    fun `Should return true when isServerConfigured is called given servers count greater than 0`() = runTest {
        every { serverDao.serverCount() } returns flowOf(1)

        val result = sut.isServerConfigured()

        val flowResult = mutableListOf<Boolean>()
        result.collect { flowResult.add(it) }

        flowResult.shouldHaveSize(1)
        flowResult.first().shouldBeTrue()

        verify { serverDao.serverCount() }
    }

    @Test
    fun `Should return false when isServerConfigured is called given servers count is 0`() = runTest {
        every { serverDao.serverCount() } returns flowOf(0)

        val result = sut.isServerConfigured()

        val flowResult = mutableListOf<Boolean>()
        result.collect { flowResult.add(it) }

        flowResult.shouldHaveSize(1)
        flowResult.first().shouldBeFalse()

        verify { serverDao.serverCount() }
    }

    @Test
    fun `Should call dao deleteServer when removeServer is called`() = runTest {
        val uuid = UUID.randomUUID()
        coJustRun { serverDao.deleteServer(uuid.toString()) }

        sut.removeServer(uuid)

        coVerify { serverDao.deleteServer(uuid.toString()) }
    }

    private fun givenRecommendedServers(
        servers: List<RecommendedServerInfo> = emptyList<RecommendedServerInfo>()
    ) {
        coEvery { discoveryService.getRecommendedServers(any<List<String>>()) } returns servers
    }

    private fun givenAddressCandidates(url: String) {
        val candidates = listOf(url)
        every { discoveryService.getAddressCandidates(url) } returns candidates
    }

    private fun givenRecommendedServerWithScore(
        url: String,
        id: String,
        name: String?,
        setupCompleted: Boolean,
        score: RecommendedServerInfoScore
    ) {
        val server = givenSuccessfulRecommendedServer(url, id, name, setupCompleted, score)
        givenRecommendedServers(listOf(server))
    }

    private fun givenSuccessfulRecommendedServer(
        url: String,
        id: String,
        name: String?,
        setupCompleted: Boolean,
        score: RecommendedServerInfoScore
    ): RecommendedServerInfo {
        val systemInfo = mockk<PublicSystemInfo> {
            every { this@mockk.id } returns id
            every { serverName } returns name
            every { startupWizardCompleted } returns setupCompleted
        }

        return mockk {
            every { address } returns url
            every { this@mockk.score } returns score
            every { this@mockk.systemInfo } returns Result.success(systemInfo)
        }
    }

    private fun givenRecommendedServerWithFailedSystemInfo(url: String) {
        val server = mockk<RecommendedServerInfo> {
            every { address } returns url
            every { score } returns RecommendedServerInfoScore.GREAT
            every { this@mockk.systemInfo } returns Result.failure(Exception("No system info"))
        }
        givenRecommendedServers(listOf(server))
    }

    private fun givenSuccessfulBrandingResponse(disclaimer: String?) {
        val brandingResponse = mockk<Response<BrandingOptions>> {
            every { content } returns mockk {
                every { loginDisclaimer } returns disclaimer
            }
        }
        coEvery { brandingApiClient.getBrandingOptions() } returns brandingResponse
    }

    private fun givenDaoAddsServerSuccessfully() {
        coJustRun { serverDao.addServer(any()) }
    }

    private fun givenDaoThrowsException() {
        coEvery { serverDao.addServer(any()) } throws Exception("Database error")
    }

    private fun verifyServerAddedWithProperties(
        id: String,
        name: String,
        url: String,
        setupCompleted: Boolean,
        disclaimer: String? = null
    ) {
        coVerify {
            serverDao.addServer(match {
                it.id == id &&
                        it.name == name &&
                        it.url == url &&
                        it.isSetupCompleted == setupCompleted &&
                        it.loginDisclaimer == disclaimer
            })
        }
    }
}