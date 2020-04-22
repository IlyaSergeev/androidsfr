package com.densvr.ui.fragments.names

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.densvr.androidsfr.R
import kotlinx.android.synthetic.main.fragment_names.view.*

class NamesFragment : Fragment(R.layout.fragment_names) {

    private val tableAdapter by lazy {
        NamesTableAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.names_table_view.setAdapter(tableAdapter)
        tableAdapter.setAllItems(
            mutableListOf("1", "2", "3"),
            mutableListOf("1", "2", "3"),
            mutableListOf(
                mutableListOf("11", "12", "13"),
                mutableListOf("21", "22", "23"),
                mutableListOf("31", "32", "33")
            )
        )
    }
}