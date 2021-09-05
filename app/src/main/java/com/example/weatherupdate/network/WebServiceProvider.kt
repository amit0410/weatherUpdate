package com.example.weatherupdate.network

import com.example.weatherupdate.CustomWebSocketListener
import com.example.weatherupdate.SocketUpdate
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

class WebServiceProvider {
    private var _webSocket: WebSocket? = null

    private val socketOkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .hostnameVerifier { _, _ -> true }
        .build()

    private var _webSocketListener: CustomWebSocketListener? = null

    suspend fun startSocket(): Channel<SocketUpdate> =
        with(CustomWebSocketListener()) {
            startSocket(this)
            this@with.socketEventChannel
        }

    suspend fun startSocket(webSocketListener: CustomWebSocketListener) {
        _webSocketListener = webSocketListener
        _webSocket = socketOkHttpClient.newWebSocket(
            Request.Builder().url("ws://city-ws.herokuapp.com/").build(),
            webSocketListener
        )
//        socketOkHttpClient.dispatcher.executorService.shutdown()
    }

    suspend fun stopSocket() {
        try {
            _webSocket?.close(NORMAL_CLOSURE_STATUS, null)
            _webSocket = null
            _webSocketListener?.socketEventChannel?.close()
            _webSocketListener = null
        } catch (ex: Exception) {
        }
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}