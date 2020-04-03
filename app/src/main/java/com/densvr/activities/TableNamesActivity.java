package com.densvr.activities;

import java.util.LinkedList;

import android.os.Bundle;

import com.densvr.nfcreader.OldGlobals;
import com.densvr.table.csv.CSV;
import com.densvr.table.csv.MatrixTableAdapter;
import com.densvr.table.csv.MatrixTableAdapter.EditTableOption;
import com.densvr.table.csv.Table;
import com.densvr.table.csv.TableActivity;

public class TableNamesActivity extends TableActivity {
	
	public TableNamesActivity() {
		super(OldGlobals.CSV_NAMES);
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
				if (column == 0) {
					return 70;
				}
				return 250;
			}
			
			@Override
			protected int getHeight(int row) {
				return super.getHeight(row);
			}
		});
		matrixTableAdapter.setEditableData(true);
		matrixTableAdapter.addEditTableOption(EditTableOption.EDIT_TABLE_ADD_ROW);
		matrixTableAdapter.addEditTableOption(EditTableOption.EDIT_TABLE_DELETE_ROW);
		matrixTableAdapter.addEditTableOption(EditTableOption.EDIT_TABLE_BACK);
	}
	
	@Override 
	protected void loadFromCSV(String adress) {
		table = CSV.read(adress);
		if (table == null) {
			table = new Table();
			table.setValue(0, 0, "Чип");
			table.setValue(0, 1, "Фамилия Имя");
		}
		int cols = table.cols();
		for(int row = 0; row < table.rows(); row++) {
			String fio = "";
			for(int col = 1; col < cols; col++) {
				if (col > 1) {
					fio += " ";
				} 
				fio += table.getValue(row, 1);
				table.get(row).remove(1);
			}
			table.get(row).add(fio);
			
		}
		matrixTableAdapter.setInformation(table);
		tableFixHeaders.setAdapter(matrixTableAdapter);
	}
	
	
	
	/**
	 * gets user F_I_O by its id (chip number)
	 * @param data
	 * @param userId
	 * @return
	 */
	public static String getUserById(Table data, int userId) {
		if (data == null) {
			return "Новый участник";
		}
		String sUserId = String.valueOf(userId);
		for(int i = 1; i < data.size(); i++) {
			if (data.get(i).get(0).equals(sUserId)) {
				LinkedList<String> line = data.get(i);
				String fio = new String();
				for(int j = 1; j < line.size(); j++) {
					if (j > 1) {
						fio += " ";
					}
					fio += line.get(j);
				}
				return fio;
			}
		}
		return "Новый участник";
	}
	
	
	
	/**
	 * adds user to table or overwrite it if already exists   
	 * @param userId - chip number
	 * @param userName - F_I_O
	 * @return
	 */
	public static void writeUserToCSV(int userId, String userName) {
		Table tUsers = CSV.read(OldGlobals.CSV_NAMES);
		if (tUsers == null) {
			tUsers = new Table();
			tUsers.setValue(0, 0, "Чип");
			tUsers.setValue(0, 1, "Фамилия Имя");
		}
		String sUserId = String.valueOf(userId);
		int row = 1;
		for(; row < tUsers.rows(); row++) {
			if (tUsers.get(row).get(0).equals(sUserId)) {
				tUsers.setValue(row, 0, sUserId);
				tUsers.setValue(row, 1, userName);
				break;
			}
		}
		tUsers.setValue(row, 0, sUserId);
		tUsers.setValue(row, 1, userName);
		CSV.write(tUsers, OldGlobals.CSV_NAMES);
	}
	
}

	

