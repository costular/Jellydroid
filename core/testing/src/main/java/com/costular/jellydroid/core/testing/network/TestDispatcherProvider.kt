package com.costular.jellydroid.core.testing.network

import com.costular.jellydroid.core.net.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher

class TestDispatcherProvider(
    testDispatcher: TestDispatcher,
) : DispatcherProvider {
    override val io: CoroutineDispatcher = testDispatcher
    override val main: CoroutineDispatcher = testDispatcher
    override val computation: CoroutineDispatcher = testDispatcher
}
