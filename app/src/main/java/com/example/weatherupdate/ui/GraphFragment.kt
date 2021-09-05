package com.example.weatherupdate.ui

import android.content.Context
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.weatherupdate.GraphInjection
import com.example.weatherupdate.R
import com.example.weatherupdate.models.CityModel
import com.example.weatherupdate.viewmodels.GraphViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.fragment_graph.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val CITY_EXTRA = "CITY"
class GraphFragment : Fragment(R.layout.fragment_graph) {

    companion object {
        fun newInstance(city:String) = GraphFragment().apply {
            arguments = Bundle().apply {
                putString(CITY_EXTRA, city)
            }
        }
    }
    lateinit var cityName:String

    private val viewModel by viewModels<GraphViewModel> {
        GraphInjection.provideViewModelFactory()
    }

    val values: ArrayList<Entry> = ArrayList()
    var count:Int =0

    private fun renderResponse(response: List<CityModel>){
        if(response.filter { x->x.city == cityName }.any()){
            count++
            if(count<=30){
                lifecycleScope.launch(Dispatchers.Main){
                    if(response.filter { x->x.city == "Delhi" }.firstOrNull()?.aqi?.toFloat()!=null) {
                        setData(Entry(count.toFloat(),
                            response.filter { x -> x.city == "Delhi" }
                                .firstOrNull()?.aqi?.toFloat()!!
                        )
                        )
                        chart1.animateX(500)
                    }
                }

            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getString(CITY_EXTRA)?.let {
            cityName = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getResponse().observe(viewLifecycleOwner, Observer(:: renderResponse))
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.subscribeToSocketEvents()
        }
        cityTitle.setText(cityName)

            chart1.getDescription().setEnabled(false);

            // enable touch gestures
            chart1.setTouchEnabled(true);

            // set listeners
            chart1.setDrawGridBackground(false);

            // create marker to display box when values are selected
            val mv = CustomMarker(requireContext(), R.layout.marker_view);

            // Set the marker to the chart
            mv.setChartView(chart1);
            chart1.setMarker(mv);

            // enable scaling and dragging
            chart1.setDragEnabled(true);
            chart1.setScaleEnabled(true);

            // force pinch zoom along both axis
            chart1.setPinchZoom(true);

            val xAxis = chart1.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);

            val yAxis = chart1.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart1.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
            yAxis.setAxisMaximum(1000f);
            yAxis.setAxisMinimum(0f);
    }

    private fun setData(value: Entry) {
        val set1: LineDataSet
        if (chart1!!.data != null &&
            chart1!!.data.dataSetCount > 0
        ) {
            set1 = chart1!!.data.getDataSetByIndex(0) as LineDataSet
            set1.addEntry(value)
            set1.notifyDataSetChanged()
            chart1!!.data.notifyDataChanged()
            chart1!!.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "DataSet 1")
            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f)

            // black lines and points
            set1.color = R.color.black
            set1.setCircleColor(R.color.black)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider -> chart1!!.axisLeft.axisMinimum }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
            } else {
                set1.fillColor = R.color.purple_200
            }
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            chart1!!.data = data
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            viewModel.stopSocket()
        }

    }


}

