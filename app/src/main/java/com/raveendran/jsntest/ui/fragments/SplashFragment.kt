package com.raveendran.jsntest.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.raveendran.jsntest.R
import com.raveendran.jsntest.databinding.SplashFragmentBinding
import com.raveendran.jsntest.ui.viewmodel.PeopleViewModel
import com.raveendran.jsntest.util.Constants.first_time_toggle
import com.refer.app.helper.BindingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BindingFragment<SplashFragmentBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = SplashFragmentBinding::inflate

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    internal var internet: Boolean? = null

    private val peopleViewModel: PeopleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isFirstAppOpen = sharedPref.getBoolean(first_time_toggle, true)
        if (isFirstAppOpen) {
            if (internet!!) {
                lifecycleScope.launch {
                    peopleViewModel.getAllPeopleSafe()
                }
                sharedPref.edit().putBoolean(first_time_toggle, false).apply()
            } else {
                Toast.makeText(
                    requireContext(),
                    "No Internet Available to load the data for the first time, Please enable internet and try again",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        lifecycleScope.launch {
            delay(3000L)
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }
    }
}