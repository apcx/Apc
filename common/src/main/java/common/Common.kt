package common

import android.util.Log
import apc.common.BuildConfig
import core.Core

@Suppress("unused")
object Common {

    @Suppress("MemberVisibilityCanBePrivate")
    @JvmStatic fun log(tag: String?, msg: Any?) { if (BuildConfig.DEBUG) Log.i(tag ?: "kt-${msg?.javaClass?.simpleName}", msg as? String ?: Core.toJson(msg, true))}
    @JvmStatic fun log(              msg: Any?) { log(null, msg) }
}
