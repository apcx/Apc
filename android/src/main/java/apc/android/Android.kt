package apc.android

import android.util.Log
import apc.common.toJson

fun log(tag: String?, msg: Any?) { if (BuildConfig.DEBUG) Log.i(tag ?: "kt-${msg?.javaClass?.simpleName}", msg.toJson(true)) }
fun log(              msg: Any?) { log(null, msg) }