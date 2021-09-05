package com.example.weatherupdate
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherupdate.models.CityModel
import com.example.weatherupdate.network.MainInteractor
import com.example.weatherupdate.network.WebServiceProvider
import com.example.weatherupdate.ui.MainFragment
import com.example.weatherupdate.repo.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

//    private val viewModel by viewModels<MainViewModel> {
//        Injection.provideViewModelFactory()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    private fun renderResponse(response: List<CityModel>){

    }


    private fun connectWebSocket() {
        val provider = WebServiceProvider()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                provider.startSocket().consumeEach {
                    if (it.exception == null) {
                        println("Collecting : ${it.text}")
                    } else {

                    }
                }
            } catch (ex: java.lang.Exception) {

            }
        }

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

}

object Injection {

    private val provider : WebServiceProvider = WebServiceProvider()
    private val repository: MainRepository = MainRepository(provider)
    private val interactor = MainInteractor(repository)
    private val viewModelFactory = MainViewModelFactory(interactor)

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return viewModelFactory
    }
}

object GraphInjection {

    private val provider : WebServiceProvider = WebServiceProvider()
    private val repository: MainRepository = MainRepository(provider)
    private val interactor = MainInteractor(repository)
    private val viewModelFactory = GraphViewModelFactory(interactor)

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return viewModelFactory
    }
}

enum class Color(vararg rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF)
}