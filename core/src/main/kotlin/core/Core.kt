package core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.Closeable
import java.io.IOException

fun main(args: Array<String>) { println("kotlin main args: ${args.toList()}") }

@Suppress("unused")
object Core {
            val gson       by lazy(LazyThreadSafetyMode.PUBLICATION) { Gson() }
    private val prettyGson by lazy(LazyThreadSafetyMode.PUBLICATION) { GsonBuilder().setPrettyPrinting().create() }

    @JvmStatic fun close(closeable: Closeable?) { if (closeable != null) try { closeable.close() } catch (e: IOException) {} }

    @JvmStatic fun isEmpty(collection: Collection<Any?>?) = collection?.isEmpty() ?: true

    @JvmStatic fun toJson(src: Any?):                  String = toJson(src, false)
    @JvmStatic fun toJson(src: Any?, pretty: Boolean): String = (if (pretty) prettyGson else gson).toJson(src)

    @JvmStatic fun         <T: Any> fromJson(json: String?, clazz: Class<T>) = gson.fromJson<T?>(json, clazz)
        inline fun <reified T: Any> fromJson(json: String?)                  = gson.fromJson<T?>(json, T::class.java)

    @JvmStatic fun underscoresToCamelCase(underscores: String): String {
        var first = true
        return underscores.split("_").joinToString("") {
            if (first) {
                first = false
                it
            } else it.capitalize()
        }
    }
}
