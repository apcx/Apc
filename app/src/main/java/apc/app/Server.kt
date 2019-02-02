package apc.app

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer

object Server {

    internal lateinit var url: String

    fun start() {
        GlobalScope.launch {
            val server = MockWebServer()
            server.start()
            url = server.url("test").toString()

            val response = MockResponse().setChunkedBody("", 1024)
            server.enqueue(response)

            val field = MockResponse::class.java.getDeclaredField("body")
            field.isAccessible = true
            val body = field[response] as Buffer
            while (true) {
                body.writeUtf8("hello")
                delay(1000)
            }
        }
    }
}