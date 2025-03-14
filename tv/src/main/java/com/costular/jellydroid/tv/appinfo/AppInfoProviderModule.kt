package com.costular.jellydroid.tv.appinfo

import com.costular.jellydroid.core.appinfo.AppInfoProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppInfoProviderModule {
    @Binds
    abstract fun bindAppInfoProvider(
        appInfoProviderImpl: AppInfoProviderImpl
    ): AppInfoProvider
}