@file:Suppress("SpellCheckingInspection")

package apc.app

import android.os.Build
import apc.ndk.Cpu
import kotlin.reflect.KProperty0

// https://stackoverflow.com/questions/2799097/how-can-i-detect-when-an-android-application-is-running-in-the-emulator
val emulator = (Build.FINGERPRINT.startsWith("generic")
        || Build.FINGERPRINT.startsWith("unknown")
        || "google_sdk" in Build.MODEL
        || "Emulator" in Build.MODEL
        || "Android SDK built for x86" in Build.MODEL
        || "Genymotion" in Build.MANUFACTURER
        || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
        || "google_sdk" == Build.PRODUCT)

val builds
    get(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        @Suppress("DEPRECATION") val properties = mutableListOf<KProperty0<*>>(
                Build.VERSION::SDK_INT,
                Build::MANUFACTURER,
                Build::BRAND,
                Build::MODEL,
                Build::CPU_ABI,
                Build::CPU_ABI2
        )
        if (Build.VERSION.SDK_INT >= 21) {
            properties += Build::SUPPORTED_ABIS
            properties += Build::SUPPORTED_32_BIT_ABIS
            properties += Build::SUPPORTED_64_BIT_ABIS
        }
        properties.forEach {
            val value = it.get()
            if (value != null) map[it.name] = if (value is Array<*>) value.joinToString() else value
        }
        map["cpuFeatures"] = Cpu.features
        return map
    }