package com.densvr.ui.tableviews

import android.view.View
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import kotlinx.android.synthetic.main.view_simple_table_cell.view.*

/**
 * Created by i-sergeev on 22.04.2020.
 */
class SimpleTableCellVH(view: View): AbstractViewHolder(view) {

    val textView = view.view_simple_table_cell_text
}