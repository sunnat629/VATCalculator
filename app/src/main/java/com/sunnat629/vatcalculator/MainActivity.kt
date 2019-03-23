package com.sunnat629.vatcalculator

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sunnat629.vatcalculator.databinding.ActivityMainBinding
import com.sunnat629.vatcalculator.databinding.ActivityMainNoNetworkBinding
import com.sunnat629.vatcalculator.model.RatesEnum
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {


    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProviders.of(this)
            .get(MainViewModel::class.java)

        mainViewModel.rawData.observe(this@MainActivity, Observer {
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
                .apply {
                    this.lifecycleOwner = this@MainActivity
                    this.viewModel = mainViewModel
                }
            setSpinner()
        })

        mainViewModel.error.observe(this@MainActivity, Observer {
            DataBindingUtil.setContentView<ActivityMainNoNetworkBinding>(this, R.layout.activity_main_no_network)
                .apply {
                    this.lifecycleOwner = this@MainActivity
                    this.viewModel = mainViewModel
                }
        })
    }

    // set Country in spinner
    private fun setSpinner() {

        GlobalScope.launch(Dispatchers.Main) {
            // country is in observation if it changes or not
            mainViewModel.fetchAllCountries().observe(this@MainActivity, Observer {
                val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, it)
                countrySpinner.adapter = adapter
            })

            countrySpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    // VAT rate changes based on selected country
                    GlobalScope.launch(Dispatchers.Main) {
                        val country = mainViewModel.fetchAllCountries().value?.get(position)
                        country?.let { getOneRate(it) }
                        toast(country.toString())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

    }

    // fetch rates based on Country
    private fun getOneRate(country: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val rates = mainViewModel.getPeriodsRateByCountry(country)
            createRadioButton(rates)
        }
    }

    // create Radio Button based on Country
    @SuppressLint("ResourceType")
    private fun createRadioButton(rates: List<Pair<RatesEnum, Double>>) {
        radioGroup.clearCheck()
        radioGroup.removeAllViews()
        for (radioButton in 0 until rates.size) {
            val myRadioButton = RadioButton(this)
            if (rates[radioButton].second != 0.0) {
                myRadioButton.layoutParams =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                myRadioButton.text = this.getString(
                    R.string.vat_rate,
                    rates[radioButton].second.toString(),
                    rates[radioButton].first.toString().toLowerCase()
                )
                myRadioButton.id = radioButton
                radioGroup.addView(myRadioButton)
            }
            if (rates[radioButton].first == RatesEnum.STANDARD) {
                myRadioButton.isChecked = true
                mainViewModel.radioVatRate.value = rates[radioButton].second.toString()
            }
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId >= 0) {
                if (!TextUtils.isEmpty(input_ex_vat.text)) {
                    mainViewModel.radioVatRate.value = rates[checkedId].second.toString()
                }
            }
        }
    }
}
