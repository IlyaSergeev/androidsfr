package com.densvr.ui.fragments.names

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.densvr.androidsfr.databinding.FragmentNamesBinding
import com.densvr.model.Person

class NamesFragment : Fragment() {

    private var _binding: FragmentNamesBinding? = null
    private val binding get() = _binding!!

    private val tableAdapter by lazy {
        NamesTableAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.namesRecyclerView.adapter = tableAdapter
        ItemTouchHelper(SwipeToDeleteCallback(tableAdapter, requireContext())).attachToRecyclerView(
            binding.namesRecyclerView
        )
    }

    override fun onResume() {
        super.onResume()

        tableAdapter.showPersons(
            listOf(
                Person("Vasa", "1111"),
                Person("Petya", "2222"),
                Person("Nina", "3333"),
                Person("Lena", "4444")
            )
        )
    }
}