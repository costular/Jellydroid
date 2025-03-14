package com.costular.jellydroid.core.testing.di

import com.costular.jellydroid.core.testing.network.TestDispatcherProvider
import com.costular.jellydroid.core.net.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.test.TestDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestDispatcherProviderModule {
    @Provides
    @Singleton
    fun provideTestDispatcherProvider(testDispatcher: TestDispatcher): DispatcherProvider =
        TestDispatcherProvider(testDispatcher)
}
