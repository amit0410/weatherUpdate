package com.example.weatherupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherupdate.network.MainInteractor
import com.example.weatherupdate.viewmodels.GraphViewModel
import com.example.weatherupdate.viewmodels.MainViewModel

class MainViewModelFactory(private val interactor: MainInteractor): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(interactor) as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }

}

class GraphViewModelFactory(private val interactor: MainInteractor): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GraphViewModel::class.java)){
            return GraphViewModel(interactor) as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }

}
