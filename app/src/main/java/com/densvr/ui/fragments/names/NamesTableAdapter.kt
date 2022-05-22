package com.densvr.ui.fragments.names

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.densvr.androidsfr.R
import com.densvr.androidsfr.databinding.ItemNamesAddPersonBinding
import com.densvr.androidsfr.databinding.ItemNamesPersonBinding
import com.densvr.model.Person
import com.densvr.ui.helpers.SimpleTextWatcher
import com.densvr.ui.helpers.setTextWithChangeListener

/**
 * Created by i-sergeev on 22.04.2020.
 */
internal class NamesTableAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_PERSON = 1
        private const val TYPE_ADD_PERSON = 2

        private const val COUNT_TYPE_HEADER = 1
        private const val COUNT_TYPE_ADD_PERSON = 1

        fun RecyclerView.ViewHolder.canSwipe(): Boolean {
            return this is PersonVH
        }
    }

    private val persons = mutableListOf<Person>()

    fun showPersons(newPersons: Iterable<Person>) {
        persons.clear()
        persons.addAll(newPersons)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderVH(
                layoutInflater.inflate(
                    R.layout.item_names_header,
                    parent,
                    false
                )
            )
            TYPE_PERSON -> PersonVH(ItemNamesPersonBinding.inflate(layoutInflater, parent, false))
            TYPE_ADD_PERSON ->
                AddPersonVH(
                    ItemNamesAddPersonBinding.inflate(layoutInflater, parent, false)
                ) { itemPosition ->
                    persons += Person("", "")
                    notifyItemInserted(itemPosition)
                }
            else -> throw IllegalStateException("Unknown viewType=$viewType")
        }
    }

    override fun getItemCount(): Int {
        return COUNT_TYPE_HEADER + persons.size + COUNT_TYPE_ADD_PERSON
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PersonVH -> (position - COUNT_TYPE_HEADER).let { personPosition ->
                holder.bind(persons[personPosition], personPosition + 1)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position < COUNT_TYPE_HEADER -> TYPE_HEADER
            position >= COUNT_TYPE_HEADER + persons.size -> TYPE_ADD_PERSON
            else -> TYPE_PERSON
        }
    }

    fun delete(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is PersonVH) {
            persons.remove(viewHolder.person)
            notifyItemRemoved(viewHolder.adapterPosition)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    private class HeaderVH(view: View) : RecyclerView.ViewHolder(view)

    private class AddPersonVH(viewBinding: ItemNamesAddPersonBinding, onClickAdd: (Int) -> Unit) :
        RecyclerView.ViewHolder(viewBinding.root) {

        init {
            viewBinding.itemNameAddPersonButton.setOnClickListener {
                onClickAdd(adapterPosition)
            }
        }
    }

    private class PersonVH(private val viewBinding: ItemNamesPersonBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        lateinit var person: Person

        private val nameTextChangeListener = object : SimpleTextWatcher() {
            override fun afterTextChanged(text: Editable?) {
                person.name = text.toString()
            }
        }

        private val chipIdTextChangeListener = object : SimpleTextWatcher() {
            override fun afterTextChanged(text: Editable?) {
                person.chipId = text.toString()
            }
        }

        fun bind(person: Person, position: Int) {
            this.person = person

            viewBinding.itemNamesPersonId.text = "$position"
            viewBinding.itemNamesPersonName.setTextWithChangeListener(
                person.name,
                nameTextChangeListener
            )
            viewBinding.itemNamesPersonChipId.setTextWithChangeListener(
                person.chipId,
                chipIdTextChangeListener
            )
        }
    }
}