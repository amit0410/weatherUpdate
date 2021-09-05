package com.example.weatherupdate.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherupdate.network.MainInteractor
import com.example.weatherupdate.models.CityModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class GraphViewModel constructor(
    private val interactor: MainInteractor
):
    ViewModel() {

    private val response = MutableLiveData<List<CityModel>>()
    fun getResponse(): LiveData<List<CityModel>> {return response}

    suspend fun subscribeToSocketEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                interactor.startSocket().consumeEach {
                    if (it.exception == null) {
                        val gson = Gson()
                        val list = object : TypeToken<List<CityModel>>() {}.type
                        val res: List<CityModel> = gson.fromJson(it.text, list)
                        handleResponse(res)
                        println("Collecting : ${response}")
                    } else {
                        onSocketError(it.exception)
                    }
                }
            } catch (ex: java.lang.Exception) {
                onSocketError(ex)
            }
        }
    }

    suspend fun stopSocket() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                interactor.stopSocket()
            } catch (ex: java.lang.Exception) {
                onSocketError(ex)
            }
        }
    }

    private fun handleResponse(res: List<CityModel>){
        this.response.postValue(res)
    }

    private fun onSocketError(ex: Throwable) {
        println("Error occurred : ${ex.message}")
    }

    override fun onCleared() {
        viewModelScope.launch {
            interactor.stopSocket()
        }
        super.onCleared()
    }

}