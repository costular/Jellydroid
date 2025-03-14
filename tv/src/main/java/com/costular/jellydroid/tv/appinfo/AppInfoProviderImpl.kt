package com.costular.jellydroid.tv.appinfo

import com.costular.jellydroid.core.appinfo.AppInfoProvider
import com.costular.jellydroid.tv.BuildConfig

class AppInfoProviderImpl : AppInfoProvider {
    override val version: String = BuildConfig.VERSION_NAME
}