package com.example.weatherupdate.repo

import com.example.weatherupdate.SocketUpdate
import com.example.weatherupdate.network.WebServiceProvider
import kotlinx.coroutines.channels.Channel

class MainRepository constructor(private val webServicesProvider: WebServiceProvider) {

    suspend fun startSocket(): Channel<SocketUpdate> =
        webServicesProvider.startSocket()

    suspend fun closeSocket() {
        webServicesProvider.stopSocket()
    }
}