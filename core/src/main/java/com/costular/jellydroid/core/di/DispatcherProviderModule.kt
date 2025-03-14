package com.costular.jellydroid.core.di

import com.costular.jellydroid.core.net.AppDispatcherProvider
import com.costular.jellydroid.core.net.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DispatcherProviderModule {
    @Provides
    fun providesDispatcherProvider(): DispatcherProvider = AppDispatcherProvider()
}
