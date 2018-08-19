@file:Suppress("unused")

package apc.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

var verbose = false

val gson by lazy { Gson() }
private val prettyGson by lazy { GsonBuilder().setPrettyPrinting().create() }

fun Any?.toJson(pretty: Boolean = false) = (if (pretty) prettyGson else gson).toJson(this)!!

inline fun <reified T> String?.toObject() = try {
    gson.fromJson<T?>(this, T::class.java)
} catch (e: JsonSyntaxException) {
    if (verbose) e.printStackTrace()
    null
}

inline fun <reified T> String?.toObjectList() = try {
    gson.fromJson<List<T>?>(this, object : TypeToken<MutableList<T>>() {}.type)
} catch (e: JsonSyntaxException) {
    if (verbose) e.printStackTrace()
    null
}

fun underscoresToCamelCase(underscores: String): String {
    var first = true
    return underscores.split("_").joinToString("") {
        if (first) {
            first = false
            it
        } else it.capitalize()
    }
}