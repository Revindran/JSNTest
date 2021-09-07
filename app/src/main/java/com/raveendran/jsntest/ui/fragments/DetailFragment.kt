package com.raveendran.jsntest.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.raveendran.jsntest.databinding.DetailsFragmentBinding
import com.refer.app.helper.BindingFragment

class DetailFragment : BindingFragment<DetailsFragmentBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = DetailsFragmentBinding::inflate

    private val args: DetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = args.peopleData

        binding.apply {
            nameTv.text = "First Name : ${data.first_name}"
            lastnameTv.text = "Last Name : ${data.last_name}"
            emailTv.text = "Email : ${data.email}"
            Glide.with(requireContext()).load(data.avatar).diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView)
        }
    }
}