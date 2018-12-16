@file:Suppress("unused")

package apc.android

import android.content.SharedPreferences
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import apc.common.toJson
import org.jetbrains.anko.internals.AnkoInternals

@Suppress("IMPLICIT_CAST_TO_ANY")
inline operator fun <reified T> SharedPreferences.get(key: String) = when (T::class) {
    Int::class -> getInt(key, 0)
    Long::class -> getLong(key, 0)
    Float::class -> getFloat(key, 0f)
    String::class -> getString(key, "")
    CharSequence::class -> getString(key, "")
    Set::class -> getStringSet(key, mutableSetOf())
    Collection::class -> getStringSet(key, mutableSetOf())
    Iterable::class -> getStringSet(key, mutableSetOf())
    else -> getBoolean(key, false)
} as T

var TextView.textDp: Float
    @Deprecated(AnkoInternals.NO_GETTER, level = DeprecationLevel.ERROR) get() = AnkoInternals.noGetter()
    set(size) {
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
    }

fun ViewGroup.inflate(resource: Int, attachToRoot: Boolean = false) = LayoutInflater.from(context).inflate(resource, this, attachToRoot)!!

fun log(tag: String?, msg: Any?) {
    if (BuildConfig.DEBUG) Log.i(tag ?: "kt-${msg?.javaClass?.simpleName}", msg.toJson(true))
}

fun log(msg: Any?) {
    log(null, msg)
}