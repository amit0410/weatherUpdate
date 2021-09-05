package com.example.weatherupdate.network

import com.example.weatherupdate.repo.MainRepository
import com.example.weatherupdate.SocketUpdate
import kotlinx.coroutines.channels.Channel

class MainInteractor constructor(private val repository: MainRepository) {


    suspend fun stopSocket() {
        repository.closeSocket()
    }

    suspend fun startSocket(): Channel<SocketUpdate> = repository.startSocket()

}