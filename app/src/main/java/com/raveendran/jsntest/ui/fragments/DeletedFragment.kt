package com.raveendran.jsntest.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.raveendran.jsntest.databinding.DeletedFragmentBinding
import com.raveendran.jsntest.model.People
import com.raveendran.jsntest.ui.viewmodel.PeopleViewModel
import com.refer.app.helper.BindingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeletedFragment : BindingFragment<DeletedFragmentBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = DeletedFragmentBinding::inflate
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
                R.id.action_deletedFragment_to_detailFragment,
                bundle
            )
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
                        deleted = false
                    )
                    peopleViewModel.updatePeople(people = data)
                }
                Snackbar.make(
                    view,
                    "Item Restored",
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
            attachToRecyclerView(binding.deletedRecycler)
        }

    }

    private fun observeFromLocalDB() {
        showProgressBar()
        peopleViewModel.getAllDeletedPeoplesFromLocal().observe(viewLifecycleOwner, {
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
        binding.deletedRecycler.apply {
            adapter = peopleAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

}