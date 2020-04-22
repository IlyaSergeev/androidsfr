package com.densvr.ui.fragments.names

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.densvr.androidsfr.R
import com.densvr.ui.tableviews.SimpleTableCellVH
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder

/**
 * Created by i-sergeev on 22.04.2020.
 */
class NamesTableAdapter : AbstractTableAdapter<String, String, String>() {

    private fun ViewGroup.createCellViewHolder(): SimpleTableCellVH {

        val layoutInflater = LayoutInflater.from(context)
        return SimpleTableCellVH(
            layoutInflater.inflate(
                R.layout.view_simple_table_cell,
                this,
                false
            )
        )
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {

        return parent.createCellViewHolder()
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: String?,
        columnPosition: Int
    ) {
        (holder as SimpleTableCellVH).textView.text = columnHeaderItemModel
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: String?,
        rowPosition: Int
    ) {
        (holder as SimpleTableCellVH).textView.text = rowHeaderItemModel
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {

        return parent.createCellViewHolder()
    }

    override fun getCellItemViewType(position: Int): Int {
        return 0
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        return parent.createCellViewHolder()
    }

    override fun onCreateCornerView(parent: ViewGroup): View {

        return LayoutInflater.from(parent.context)
            .inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: String?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        (holder as SimpleTableCellVH).textView.text = cellItemModel
    }

    override fun getColumnHeaderItemViewType(position: Int): Int {
        return 0
    }

    override fun getRowHeaderItemViewType(position: Int): Int {
        return 0
    }
}