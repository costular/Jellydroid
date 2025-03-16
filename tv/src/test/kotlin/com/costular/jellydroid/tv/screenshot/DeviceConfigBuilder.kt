package com.costular.jellydroid.tv.screenshot

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import app.cash.paparazzi.DeviceConfig
import com.android.resources.Density
import com.android.resources.NightMode
import com.android.resources.ScreenOrientation
import com.android.resources.ScreenRatio
import com.android.resources.ScreenRound
import com.android.resources.ScreenSize
import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo
import sergio.sastre.composable.preview.scanner.android.device.DevicePreviewInfoParser
import sergio.sastre.composable.preview.scanner.android.device.domain.Orientation
import kotlin.math.ceil

object DeviceConfigBuilder {
    fun build(preview: AndroidPreviewInfo): DeviceConfig {
        val parsedDevice =
            DevicePreviewInfoParser.parse(preview.device)?.inPx() ?: return DeviceConfig()
        val conversionFactor = parsedDevice.densityDpi / 160f
        val previewWidthInPx = ceil(preview.widthDp * conversionFactor).toInt()
        val previewHeightInPx = ceil(preview.heightDp * conversionFactor).toInt()

        return DeviceConfig(
            screenHeight = when (preview.heightDp > 0) {
                true -> previewHeightInPx
                false -> parsedDevice.dimensions.height.toInt()
            },
            screenWidth = when (preview.widthDp > 0) {
                true -> previewWidthInPx
                false -> parsedDevice.dimensions.width.toInt()
            },
            density = Density.create(parsedDevice.densityDpi),
            xdpi = parsedDevice.densityDpi,
            ydpi = parsedDevice.densityDpi,
            size = ScreenSize.valueOf(parsedDevice.screenSize.name),
            ratio = ScreenRatio.valueOf(parsedDevice.screenRatio.name),
            screenRound = ScreenRound.valueOf(parsedDevice.shape.name),
            orientation = when (parsedDevice.orientation) {
                Orientation.PORTRAIT -> ScreenOrientation.PORTRAIT
                Orientation.LANDSCAPE -> ScreenOrientation.LANDSCAPE
            },
            locale = preview.locale.ifBlank { "en" },
            nightMode = when (preview.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES) {
                true -> NightMode.NIGHT
                false -> NightMode.NOTNIGHT
            },
        )
    }
}