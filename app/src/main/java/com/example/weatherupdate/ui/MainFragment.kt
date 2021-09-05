package com.example.weatherupdate.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherupdate.*
import com.example.weatherupdate.models.CityModel
import com.example.weatherupdate.ui.adapters.WeatherAdapter
import com.example.weatherupdate.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragment : Fragment(R.layout.fragment_main), WeatherAdapter.Interaction {

    companion object {
        fun newInstance() = MainFragment()
    }

    lateinit var weatherAdapter: WeatherAdapter

    private val viewModel by viewModels<MainViewModel> {
        Injection.provideViewModelFactory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            weatherAdapter = WeatherAdapter(this@MainFragment)
            adapter = weatherAdapter
        }
        viewModel.getResponse().observe(viewLifecycleOwner, Observer(:: renderResponse))
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.subscribeToSocketEvents()
        }
    }

    private fun renderResponse(response: List<CityModel>){
        weatherAdapter.submitList(response)

    }

    override fun onItemClick(position: Int, item: CityModel) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, GraphFragment.newInstance(item.city)).addToBackStack("Graph_fragment")
            .commit()
    }

}