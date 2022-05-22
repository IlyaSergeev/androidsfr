package com.densvr.table.csv;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.densvr.androidsfr.R;
import com.densvr.table.TableFixHeaders;

public abstract class TableActivity extends Activity {

	
	protected String adress;
	
	
	
	protected MatrixTableAdapter matrixTableAdapter;
	protected TableFixHeaders tableFixHeaders;
	
	protected Table table;

	public TableActivity(String adress) {
		this.adress = adress;
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_names);
		tableFixHeaders = (TableFixHeaders) findViewById(R.id.table);
		matrixTableAdapter = new MatrixTableAdapter(tableFixHeaders, this, table);
		matrixTableAdapter.setEditableHeader(false);
		matrixTableAdapter.setEditableData(true);
		matrixTableAdapter.setOnDataSetObserver(new MatrixTableAdapter.OnDataSetObserver() {
			
			@Override
			public void onDataChanged(Table data) {
				saveToCSV(adress);
			}
		});
		
		//tableFixHeaders.setAdapter(matrixTableAdapter);
		
		loadFromCSV(adress);
		
		//buttons
		Button buttonBack = (Button) findViewById(R.id.table_buttonBack);
		
		//back
		buttonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TableActivity.this.finish();
			}
			
		});
		
		
		
	
	}
	
	
	
	

	protected void makeText(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}
	
	
	protected Table onTableEmpty() {
		Table table = new Table();
		table.setValue(0, 0, "empty table");
		return table;
	}
	
	protected void loadFromCSV(String adress) {
		table = CSV.read(adress);
		boolean bEmptyTable = false;
		if (table == null) {
			bEmptyTable = true;
		} else if (table.rows() == 0) {
			bEmptyTable = true;
		} else if (table.cols() == 0) {
			bEmptyTable = true;
		}
		if (bEmptyTable) {
			table = onTableEmpty();
		}
		if (matrixTableAdapter != null) {
			matrixTableAdapter.setInformation(table);
			tableFixHeaders.setAdapter(matrixTableAdapter);
		}
	}
	
	protected void saveToCSV(String adress) {
		if (table == null) {
			return;
		}
		CSV.write(table, adress);
	}
	
	
	
	
	
	
	
}
