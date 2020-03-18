package com.densvr.activities;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.densvr.nfcreader.ChipData;
import com.densvr.nfcreader.ConfirmDialog;
import com.densvr.nfcreader.Globals;
import com.densvr.androidsfr.R;
import com.densvr.table.TableFixHeaders;
import com.densvr.table.csv.CSV;
import com.densvr.table.csv.MatrixTableAdapter;
import com.densvr.table.csv.MatrixTableAdapter.OnViewPostCreationWizard;
import com.densvr.table.csv.Table;
import com.densvr.table.csv.TableActivity;

public class TableResultsActivity extends Activity {

	private String adress;
	
	Table table;
	
	private MatrixTableAdapter matrixTableAdapter;
	private TableFixHeaders tableFixHeaders;
	
	private Button buttonBack;
	private Button buttonClear;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_table_results);
		adress = Globals.CSV_RESULTS;
		
		tableFixHeaders = (TableFixHeaders) findViewById(R.id.table_results_table);
		matrixTableAdapter = new MatrixTableAdapter(tableFixHeaders, this, table);
		matrixTableAdapter.setEditableHeader(false);
		matrixTableAdapter.setEditableData(false);
		matrixTableAdapter.setOnCellSizeGetter(new MatrixTableAdapter.OnCellSizeGetter(this) {
			
			@Override
			protected int getWidth(int column) {
				if (column == -1) {
					return 0;
				} 
				if (column == 0) {
					return 200;
				}
				return 75;
			}
			
			@Override
			protected int getHeight(int row) {
				if (row == -1) {
					return 0;
				}
				return super.getHeight(row);
			}
		});
		matrixTableAdapter.setOnDataSetObserver(new MatrixTableAdapter.OnDataSetObserver() {
			
			@Override
			public void onDataChanged(Table table) {
				if (table == null) {
					return;
				}
				CSV.write(table, adress);
			}
		});
		matrixTableAdapter.setOnViewPostCreationWizard(new OnViewPostCreationWizard() {
			
			@Override
			public void onViewCreationFinished(View view, int row, int col) {
				String startRowText = matrixTableAdapter.getTable().getValue(row, 0);
				if (startRowText.equals("дистанция")) {
					view.setBackgroundResource(R.drawable.table_results_color_distance_row);
				} else if (startRowText.equals("попытка")) {
					view.setBackgroundResource(R.drawable.table_results_color_attempt_row);
				} else if (startRowText.isEmpty()) {
					view.setBackgroundResource(R.drawable.table_results_color_skipped_row);
				} else {
					//use standard background
				}
			}
		});
		
		
		//load table from CSV
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
		
		//buttons
		buttonBack = (Button) findViewById(R.id.table_results_button_back);
		buttonClear = (Button) findViewById(R.id.table_results_button_clear);
		
		//back
		buttonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TableResultsActivity.this.finish();
			}
			
		});
		
		buttonClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ConfirmDialog.showDialog(TableResultsActivity.this, "Удалить все результаты?", "",
					new ConfirmDialog.OnClickListener() {
						
						@Override
						public void onClick(boolean bResult) {
							if (bResult) {
								CSV.write(new Table(), adress);
								table = onTableEmpty();
								matrixTableAdapter.setInformation(onTableEmpty());
								MainActivity.makeText("Все результаты были удалены");
								//TableResultsActivity.this.finish();
								startActivity(new Intent(getApplicationContext(), MainActivity.class));
							} else {
								//do nothing
							}
						}
					});
			}
		});
		
	}

	protected Table onTableEmpty() {
		Table table = new Table();
		table.setValue(0, 0, "нет результатов");
		return table;
	}
	
}