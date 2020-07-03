package com.densvr.ui.fragments.names

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.densvr.androidsfr.R
import com.densvr.model.Person
import kotlinx.android.synthetic.main.fragment_names.view.*

class NamesFragment : Fragment(R.layout.fragment_names) {

    private val tableAdapter by lazy {
        NamesTableAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.names_recycler_view.adapter = tableAdapter
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