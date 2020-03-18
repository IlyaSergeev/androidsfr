package com.densvr.table.csv;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class Table extends LinkedList<LinkedList<String>> {
	
	
	public void setValue(int row, int col, String val) {
		if (rows() == 0) {
			add(new LinkedList<String>());
		}
		//add rows top 
		for(int i = 0; i < -row; i++) {
			add(0, new LinkedList<String>());
		}
		//add columns left
		for(int i = 0; i < -col; i++) {
			for(int j = 0; j < rows(); j++) {
				get(j).add(0,"");
			}
		}
		//add rows bottom
		int addRows = row - rows() + 1;
		for(int i = 0; i < addRows; i++) {
			LinkedList<String> newRow = new LinkedList<String>();
			for(int j = 0; j < cols(); j++) {
				newRow.add("");
			}
			add(newRow);
		}
		//add columns right
		for(int i = 0; i < rows(); i++) {
			int addCols = col - get(i).size() + 1;
			for(int j = 0; j < addCols; j++) {
				get(i).add("");
			}
		}
		if (row < 0) {
			row = 0;
		}
		if (col < 0) {
			col = 0;
		}
		get(row).set(col, val);
	}
	
	public String getValue(int row, int col) {
		if (row > rows() - 1) {
			return "";
		}
		if (col > get(row).size() - 1) {
			return "";
		}
		return get(row).get(col);
	}
	
	public void expand() {
		int Rows = rows();
		if (Rows == 0) {
			return;
		}
		int Cols = cols();
		for(int i = 0; i < rows(); i++) {
			Cols = Math.max(this.get(i).size(), Cols);
		}
		String s = this.getValue(Rows - 1, Cols - 1);
		if (s == null) {
			s = "";
		}
		this.setValue(Rows - 1, Cols - 1, s);
	}
	
	
	
	
	public final int cols() {
		if (rows() == 0) {
			return 0;
		}
		return get(0).size();
	}
	public final int rows() {
		return size();
	}
	
	
	/**
	 * return full row data with spaces
	 * @param row
	 * @return
	 */
	public String rowToString(int row) {
		if (row < 0 || row >= rows()) {
			return "";
		}
		List<String> line = get(row);
		String res = "";
		for(int i = 0; i < line.size(); i++) {
			res += line.get(i) + " ";
		}
		return res;
	}
	
	/**
	 * return column header
	 * @param col
	 * @return
	 */
	public String columnToString(int col) {
		if (col < 0 || col >= cols()) {
			return "";
		}
		return get(0).get(col);
	}
	
	/**
	 * if table has no data, returns false
	 * table must be expanded
	 */
	public boolean isEmpty() {
		if (rows() == 0) {
			return true;
		}
		if (cols() == 0) {
			return true;
		}
		return false;
		
	}
	
	
}
