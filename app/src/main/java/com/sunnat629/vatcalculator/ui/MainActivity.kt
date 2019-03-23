package com.sunnat629.vatcalculator.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
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
import com.bumptech.glide.Glide
import com.sunnat629.vatcalculator.R
import com.sunnat629.vatcalculator.databinding.ActivityMainFetchingBinding
import com.sunnat629.vatcalculator.model.RatesEnum
import com.sunnat629.vatcalculator.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_fetching.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        DataBindingUtil.setContentView<ActivityMainFetchingBinding>(
            this,
            com.sunnat629.vatcalculator.R.layout.activity_main_fetching
        )

        // access the viewModel to control the UI
        mainViewModel = ViewModelProviders.of(this)
            .get(MainViewModel::class.java)

        Glide.with(this).load(resources.getDrawable(R.drawable.fetching, null)).into(fetchingImage)

//        fetchRawRateDataOrHandleNetwork()

    }

    /**
     * If there is no problem during fetch the data, then it will go with @rawRateData
     * or, if there is no internet connectivity or any other error, it will go with @error
     * */
//    private fun fetchRawRateDataOrHandleNetwork() {
//        // When the fetched data is completed then it will start to observe this 'rawRateData' and bind with the 'activity_main layout' and 'mainViewModel'
//        mainViewModel.rawRateData.observe(this@MainActivity, Observer {
//            DataBindingUtil.setContentView<ActivityMainBinding>(
//                this,
//                com.sunnat629.vatcalculator.R.layout.activity_main
//            )
//                .apply {
//                    this.lifecycleOwner = this@MainActivity
//                    this.viewModel = mainViewModel
//                }
//
//            Log.i(TAG_MAIN_ACTIVITY, resources.getString(R.string.successful))
//            mainViewModel.exclVatAmount.value = resources.getString(R.string.reset_value)
//            setSpinner()
//        })
//
//        // When the fetched data is completed then it will start to observe this 'error' and bind with the 'activity_main_no_network' layout and 'mainViewModel'
//        mainViewModel.error.observe(this@MainActivity, Observer {
//            DataBindingUtil.setContentView<ActivityMainNoNetworkBinding>(
//                this,
//                com.sunnat629.vatcalculator.R.layout.activity_main_no_network
//            )
//                .apply {
//                    this.lifecycleOwner = this@MainActivity
//                    this.viewModel = mainViewModel
//                }
//            Log.e(TAG_MAIN_ACTIVITY, resources.getString(R.string.error))
//            toast(resources.getString(R.string.error))
//
//        })
//    }

    /**
     * Set Country in spinner and 'launch' is suspending block
     * 'Dispatchers.Main' is a coroutine dispatcher that is confined to the Main thread operating with UI objects
     * */
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
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

    }

    /**
     * fetch rates based on Country
     **/
    private fun getOneRate(country: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val rates = mainViewModel.getPeriodsRateByCountry(country)
            createRadioButton(rates)
        }
    }

    /**
     * create dynamic Radio Buttons based on Country Rate types
     **/
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
                    com.sunnat629.vatcalculator.R.string.vat_rate,
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
