@file:Suppress("unused")

package apc.android

import android.content.SharedPreferences
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.internals.AnkoInternals

fun log() {
    if (BuildConfig.DEBUG) {
        val stackTrace = Throwable().stackTrace[1]!!
        Log.i(stackTrace.tag, stackTrace.message)
    }
}

fun log(msg: String) {
    if (BuildConfig.DEBUG) {
        val stackTrace = Throwable().stackTrace[1]!!
        Log.i(stackTrace.tag, "${stackTrace.message} $msg")
    }
}

fun logCaller() {
    if (BuildConfig.DEBUG) {
        val stackTrace = Throwable().stackTrace
        val current = stackTrace[1]!!
        val caller = stackTrace[2]!!
        Log.i(current.tag, "${caller.tag}.${caller.message}")
    }
}

val StackTraceElement.tag get() = className.substringAfterLast('.')
val StackTraceElement.message get() = toString().substring(className.length + 1)

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