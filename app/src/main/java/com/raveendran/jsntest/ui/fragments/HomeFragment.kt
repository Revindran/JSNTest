package com.raveendran.jsntest.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.raveendran.jsntest.R
import com.raveendran.jsntest.adapter.PeopleAdapter
import com.raveendran.jsntest.databinding.HomeFragmentBinding
import com.raveendran.jsntest.model.People
import com.raveendran.jsntest.ui.viewmodel.PeopleViewModel
import com.raveendran.jsntest.util.NetworkResponse
import com.refer.app.helper.BindingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BindingFragment<HomeFragmentBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = HomeFragmentBinding::inflate

    private val peopleViewModel: PeopleViewModel by viewModels()
    private lateinit var peopleAdapter: PeopleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeFromLocalDB()

        peopleAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("peopleData", it)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_detailFragment,
                bundle
            )
        }

        binding.deleteIcon.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_deletedFragment)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val people = peopleAdapter.differ.currentList[position]
                lifecycleScope.launch {
                    val data = People(
                        avatar = people.avatar,
                        email = people.email,
                        first_name = people.first_name,
                        id = people.id,
                        last_name = people.last_name,
                        deleted = true
                    )
                    peopleViewModel.updatePeople(people = data)
                }
                Snackbar.make(
                    view,
                    "Deleted the swiped Item in position $position",
                    Snackbar.LENGTH_SHORT
                ).setAction(
                    "Undo"
                ) {
                    lifecycleScope.launch {
                        peopleViewModel.insertPeople(people = people)
                    }
                }.show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.homeRecycler)
        }
    }

    private fun observeData() {
        peopleViewModel.allPeoples.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResponse.Success -> {
                    hideProgressBar()
                    response.data?.let { pplResponse ->
                        peopleAdapter.differ.submitList(pplResponse.data.toList())
                    }
                }
                is NetworkResponse.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e("TAG", "An Error Occured: $message")
                        Toast.makeText(activity, "An Error Occured: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                is NetworkResponse.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun observeFromLocalDB() {
        showProgressBar()
        peopleViewModel.getAllPeopleDetailsFromLocal().observe(viewLifecycleOwner, {
            it?.let {
                hideProgressBar()
                peopleAdapter.differ.submitList(it)
            }
            if (it.isEmpty())
                binding.emptyListTv.visibility = View.VISIBLE
            else
                binding.emptyListTv.visibility = View.GONE
        })
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        peopleAdapter = PeopleAdapter()
        binding.homeRecycler.apply {
            adapter = peopleAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }
}