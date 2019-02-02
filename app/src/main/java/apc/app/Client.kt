package apc.app

import apc.android.log
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.header
import io.ktor.client.response.readBytes
import io.ktor.http.HttpMethod
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.io.core.String

object Client {

    fun start() {
        GlobalScope.launch {
            delay(100)
            val response = HttpClient().call(Server.url) {
                method = HttpMethod.Post
                header("Cache-Control", "no-cache")
                header("Connection", "Keep-Alive")
            }.response
            delay(2000)
            while (true) {
                val bytes = response.readBytes(5)
                log(String(bytes))
            }
        }
    }
}