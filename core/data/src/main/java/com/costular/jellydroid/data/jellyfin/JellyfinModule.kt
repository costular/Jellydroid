package com.costular.jellydroid.data.jellyfin

import android.content.Context
import com.costular.jellydroid.core.appinfo.AppInfoProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.createJellyfin
import org.jellyfin.sdk.model.ClientInfo
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface JellyfinModule {
    @Singleton
    @Provides
    fun provideJellyfin(
        @ApplicationContext androidContext: Context,
        appInfoProvider: AppInfoProvider,
    ): Jellyfin =
        createJellyfin {
            context = androidContext
            clientInfo = ClientInfo("Jellydroid", appInfoProvider.version)
            // deviceInfo = DeviceInfo TODO: pass the device info
        }

    @Singleton
    @Provides
    fun provideJellyfinApi(
        jellyfin: Jellyfin,
    ): ApiClient = jellyfin.createApi()
}