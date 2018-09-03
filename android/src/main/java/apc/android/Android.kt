package apc.android

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import apc.common.toJson

fun ViewGroup.inflate(resource: Int, attachToRoot: Boolean = false): View {
    val view = LayoutInflater.from(context).inflate(resource, this, false)
    if (attachToRoot) addView(view)
    return view
}

fun log(tag: String?, msg: Any?) { if (BuildConfig.DEBUG) Log.i(tag ?: "kt-${msg?.javaClass?.simpleName}", msg.toJson(true)) }
fun log(              msg: Any?) { log(null, msg) }