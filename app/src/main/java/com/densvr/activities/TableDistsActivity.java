package com.densvr.activities;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.densvr.nfcreader.ChipData;
import com.densvr.nfcreader.Globals;
import com.densvr.androidsfr.R;
import com.densvr.table.TableFixHeaders;
import com.densvr.table.csv.CSV;
import com.densvr.table.csv.MatrixTableAdapter;
import com.densvr.table.csv.Table;
import com.densvr.table.csv.TableActivity;
import com.densvr.table.csv.MatrixTableAdapter.EditTableOption;

public class TableDistsActivity extends TableActivity {

	public TableDistsActivity() {
		super(Globals.CSV_DISTS);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		matrixTableAdapter.setOnCellSizeGetter(new MatrixTableAdapter.OnCellSizeGetter(this) {
			
			@Override
			protected int getWidth(int column) {
				if (column == -1) {
					return 0;
				} 
				return 70;
			}
			
			@Override
			protected int getHeight(int row) {
				return super.getHeight(row);
			}
		});
		matrixTableAdapter.setEditableData(true);
		matrixTableAdapter.setEditableHeader(true);
		matrixTableAdapter.addEditTableOption(EditTableOption.EDIT_TABLE_ADD_COLUMN);
		matrixTableAdapter.addEditTableOption(EditTableOption.EDIT_TABLE_DELETE_COLUMN);
		matrixTableAdapter.addEditTableOption(EditTableOption.EDIT_TABLE_ADD_CELL_SHIFT_DOWN);
		matrixTableAdapter.addEditTableOption(EditTableOption.EDIT_TABLE_DELETE_CELL_SHIFT_UP);
		matrixTableAdapter.addEditTableOption(EditTableOption.EDIT_TABLE_BACK);
	}
	
	
	public static ChipData getDistByName(Table data, String name) {
		for(int i = 0; i < data.get(0).size(); i++) {
			if (data.get(0).get(i).equals(name)) {
				LinkedList<String> distCps = new LinkedList<String>();
				for(int j = 1; j < data.size(); j++) {
					String cur = data.get(j).get(i);
					if (!cur.isEmpty()) {
						distCps.add(cur);
					} else {
						break;
					}
				}
				return ChipData.parseDistsResultsLine(distCps);
			}
		}
		return null;
	}

	
}