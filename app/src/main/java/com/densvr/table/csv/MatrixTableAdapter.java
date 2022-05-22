package com.densvr.table.csv;



import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.densvr.activities.MainActivity;
import com.densvr.nfcreader.OldConfirmDialog;
import com.densvr.androidsfr.R;
import com.densvr.table.BaseTableAdapter;
import com.densvr.table.TableFixHeaders;

public class MatrixTableAdapter extends BaseTableAdapter {

	private Context context;
	private TableFixHeaders pTableFixHeaders;

	private Table table;

	private OnCellSizeGetter onCellSizeGetter;
	
	
	private OnClickTableViewListener onClickTableViewListener;
	private OnLongClickTableViewListener onLongClickTableViewListener;
	private OnDataSetObserver onDataSetObserver;
	
	private OnViewPostCreationWizard onViewPostCreationWizard;
	
	private boolean bEditableData;
	private boolean bEditableHeader;
	
	EditTableDialogBuilder editTableDialogBuilder;
	
	
	/**
	 * specifies size of cells
	 * @author densvr
	 *
	 */
	public static class OnCellSizeGetter {
		
		private Context context;
		
		protected final static int WIDTH_DIP = 100;
		protected final static int HEIGHT_DIP = 32;
		
		public OnCellSizeGetter(Context context) {
			this.context = context; 
		}
		
		/**
		 * specifies column cells width
		 * @param column
		 * @return
		 */
		protected int getWidth(int column) {
			if (column == -1) {
				return 1; //1 dp
			}
			return WIDTH_DIP;
		}
		
		/**
		 * specifies row cells height
		 * @param row
		 * @return
		 */
		protected int getHeight(int row) {
			return HEIGHT_DIP;
		}
		
		
		/**
		 * returns raw display width
		 * @param column
		 * @return
		 */
		public final int getRawWidth(int column) {
			int width = getWidth(column);
			return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, context.getResources().getDisplayMetrics()));
		}
		
		/**
		 * returns raw display height
		 * @param row
		 * @return
		 */
		public final int getRawHeight(int row) {
			int height = getHeight(row);
			return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics()));
		}
		
		
	};
	
	public static interface OnDataSetObserver {
		
		/**
		 * called when user finishes editing the data
		 */
		public void onDataChanged(Table data);
	};
	
	public static interface OnViewPostCreationWizard {
		
		/**
		 * called when creation of view is finished
		 * @param view
		 */
		public void onViewCreationFinished(View view, int row, int col);
	};
	
	public enum EditTableOption {
		
		EDIT_TABLE_ADD_ROW,
		
		EDIT_TABLE_DELETE_ROW,
		
		EDIT_TABLE_ADD_COLUMN,
		
		EDIT_TABLE_DELETE_COLUMN,
		
		EDIT_TABLE_ADD_CELL_SHIFT_DOWN,
		
		EDIT_TABLE_DELETE_CELL_SHIFT_UP,
		
		EDIT_TABLE_BACK
	};
	
	private final CharSequence[] allOptNames = new CharSequence[] {
		"добавить строку",
		"удалить строку",
		"добавить столбец",
		"удалить столбец",
		"добавить ячейку", //shift down
		"удалить ячейку", //shift up
		"назад"
	};
	
	
	private class EditTableDialogBuilder implements DialogInterface.OnClickListener {
		
		int row, col;
		
		private List<EditTableOption> editTableOptions = new LinkedList<EditTableOption>();
		
		
		
		public void addOption(EditTableOption option) {
			editTableOptions.add(option);
		}
		
		public void clearOptions() {
			editTableOptions.clear();
		}
		
		/**
		 * returns alert dialog with options for table editing,
		 * if options not set, returns null
		 * @param row
		 * @param col
		 * @return
		 */
		public AlertDialog.Builder buildDialog(int row, int col) {
			if (editTableOptions.size() == 0) {
				return null;
			}
			this.row = row;
			this.col = col;
			String title = "Редактировать таблицу\nячейка [" + table.getValue(row, col) + "]";
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle(title);
			CharSequence optNames[] = new CharSequence[editTableOptions.size()];
			for(int i = 0; i < editTableOptions.size(); i++) {
				optNames[i] = allOptNames[editTableOptions.get(i).ordinal()];
			}
			alert.setItems(optNames, this);
			return alert;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (!bEditableData) {
				MainActivity.makeText("редактирование данных запрещено", context);
				dialog.cancel();
				return;
			}
			EditTableOption editTableOpt = editTableOptions.get(which);
			switch (editTableOpt) {
			case EDIT_TABLE_ADD_ROW: //add row
				table.add(row + 1, new LinkedList<String>());
				table.expand(); //expand new row to rectangle width
				MatrixTableAdapter.this.setInformation(table);
				MatrixTableAdapter.this.onDataSetObserver.onDataChanged(table);
				break;
			case EDIT_TABLE_DELETE_ROW:
				if (row == 0) {
					MainActivity.makeText("нельзя удалить строку заголовков", context);
					dialog.cancel();
					return;
				} 
				OldConfirmDialog.showDialog(context, "удалить строку?", table.rowToString(row), new OldConfirmDialog.OnClickListener() {
					@Override
					public void onClick(boolean bResult) {
						if (bResult) {
							table.remove(row);
							MatrixTableAdapter.this.setInformation(table);
							MatrixTableAdapter.this.onDataSetObserver.onDataChanged(table);
						} else {
							MainActivity.makeText("удаление отменено", context);
						}
					}
				});
				break;
			case EDIT_TABLE_ADD_COLUMN:
				table.expand(); //expand to rectangle form
				for(int i = 0; i < table.rows(); i++) {
					table.get(i).add(col + 1, "");
				}
				MatrixTableAdapter.this.setInformation(table);
				MatrixTableAdapter.this.onDataSetObserver.onDataChanged(table);
				break;
			case EDIT_TABLE_DELETE_COLUMN:
				OldConfirmDialog.showDialog(context, "удалить столбец?", table.columnToString(col), new OldConfirmDialog.OnClickListener() {
					@Override
					public void onClick(boolean bResult) {
						if (bResult) {
							table.expand(); //expand to rectangle form
							for(int i = 0; i < table.rows(); i++) { 
								table.get(i).remove(col);
							}
							MatrixTableAdapter.this.setInformation(table);
							MatrixTableAdapter.this.onDataSetObserver.onDataChanged(table);
						} else {
							MainActivity.makeText("удаление отменено", context);
						}
					}
				});
				break;
			case EDIT_TABLE_ADD_CELL_SHIFT_DOWN:
				table.add(new LinkedList<String>());
				table.expand();
				for(int i = table.rows() - 2; i >= row ; i--) {
					String val = table.getValue(i, col);
					table.setValue(i + 1, col, val);
				}
				table.setValue(row, col, "");
				MatrixTableAdapter.this.setInformation(table);
				MatrixTableAdapter.this.onDataSetObserver.onDataChanged(table);
				break;
			case EDIT_TABLE_DELETE_CELL_SHIFT_UP:
				table.expand();
				for(int i = row; i < table.rows() - 1; i++) {
					String val = table.getValue(i + 1, col);
					table.setValue(i, col, val);
				}
				table.setValue(table.rows() - 1, col, "");
				boolean bEmpty = true;
				for(int i = 0; i < table.cols(); i++) {
					if (!table.getValue(table.rows() - 1, i).isEmpty()) {
						bEmpty = false;
						break;
					}
				}
				if (bEmpty) {
					table.removeLast();
				}
				MatrixTableAdapter.this.setInformation(table);
				MatrixTableAdapter.this.onDataSetObserver.onDataChanged(table);
				break;
			case EDIT_TABLE_BACK:
				//do nothing and close dialog
				break;
			default:
				throw new RuntimeException();
			}
		}
		
	}
	
	
	
	/**
	 * opens edit cell dialog
	 * @author densvr
	 *
	 */
	private class OnClickTableViewListener implements OnClickListener {

		private Context context;
		
		TextView textView;
		EditText alertTextView;
		
		int row, col;
		
		OnClickTableViewListener(Context context) {
			this.context = context;
		}
		
		@Override
		public void onClick(View v) {
			textView = (TextView)v.findViewById(android.R.id.text1);
			int[] viewCoords = pTableFixHeaders.getViewCoordinates(v);
			row = viewCoords[0];
			col = viewCoords[1];
			
			if (row == 0 && !bEditableHeader) {
				MainActivity.makeText("редактирование заголовка запрещено", context);
				return;
			}
			if (row != 0 && !bEditableData) {
				MainActivity.makeText("редактирование данных запрещено", context);
				return;
			}
			
			//create alert dialog with textview
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("редактировать");
			alertTextView = new EditText(context);
			alertTextView.setText(textView.getText());
			alert.setView(alertTextView);
			
			alert.setPositiveButton("ок", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String newText = alertTextView.getText().toString(); 
					textView.setText(newText);
					table.setValue(row, col, newText);
					MatrixTableAdapter.this.setInformation(table);
					MatrixTableAdapter.this.onDataSetObserver.onDataChanged(table);
					dialog.cancel();
				}
				
			});
			
			alert.setNegativeButton("отмена", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
				
			});
			
			alert.show();
			return;
		}
		
	};
	
	/**
	 * opens edit table dialog (add or delete columns and rows)
	 * @author densvr
	 *
	 */
	private class OnLongClickTableViewListener implements OnLongClickListener {

		//private Context context;
		
		
		int row, col;
		
		OnLongClickTableViewListener(Context context) {
			//this.context = context;
		}
		
		@Override
		public boolean onLongClick(View v) {
			int[] viewCoords = pTableFixHeaders.getViewCoordinates(v);
			row = viewCoords[0];
			col = viewCoords[1];
			
			if (!bEditableData) {
				MainActivity.makeText("редактирование данных запрещено", context);
				return false;
			}
			
			//create alert dialog 
			AlertDialog.Builder alert = editTableDialogBuilder.buildDialog(row, col);
			if (alert == null) {
				MainActivity.makeText("редактирование таблицы запрещено", context);
			}
			alert.show();
			return true;
		}
		
	};
	
	
	
	
	

	

	
	

	public MatrixTableAdapter(TableFixHeaders pTableFixHeaders, Context context) {
		this(pTableFixHeaders, context, null);
		
	}

	public MatrixTableAdapter(TableFixHeaders pTableFixHeaders, Context context, Table t) {
		this.pTableFixHeaders = pTableFixHeaders;	
		this.context = context;
		
		onCellSizeGetter = new OnCellSizeGetter(context);

		setInformation(t);
		
		onClickTableViewListener = new OnClickTableViewListener(context);
		onLongClickTableViewListener = new OnLongClickTableViewListener(context);
		
		
		editTableDialogBuilder = new EditTableDialogBuilder();
	}

	public void setInformation(Table table) {
		this.table = table;
		pTableFixHeaders.setNeedRelayout(true);
		pTableFixHeaders.requestLayout();
	}

	@Override
	public int getRowCount() {
		return table.size() - 1;
	}

	@Override
	public int getColumnCount() {
		return table.get(0).size();
	}

	@Override
	public View getView(int row, int column, View convertView, ViewGroup parent) {
		convertView =  LayoutInflater.from(context).inflate(R.layout.item_table, parent, false);
		TextView pTextView = convertView.findViewById(android.R.id.text1);
		pTextView.setGravity(Gravity.CENTER_VERTICAL);
		
		if (row == -1) {
			//header
			convertView.setBackgroundResource(R.drawable.table_header_color);
		} else if (row % 2 == 0) {
			convertView.setBackgroundResource(R.drawable.table_color1);
		} else {
			convertView.setBackgroundResource(R.drawable.table_color2);
		}
		
		if (column == -1) {
			pTextView.setText("");
			//setOnClickListener(onViewClickListener);
		} else {
			pTextView.setText(table.get(row + 1).get(column).toString());
		}
		//set on click listener
		convertView.setOnClickListener(onClickTableViewListener);
		convertView.setOnLongClickListener(onLongClickTableViewListener);
		
		if (onViewPostCreationWizard != null) {
			onViewPostCreationWizard.onViewCreationFinished(convertView, row + 1, column);
		}
		
		return convertView;
	}

	@Override
	public int getHeight(int row) {
		return onCellSizeGetter.getRawHeight(row);
	}

	@Override
	public int getWidth(int column) {
		
		/*if (column == -1) {
			return 0;
		}*/
		
		return onCellSizeGetter.getRawWidth(column);
	}

	@Override
	public int getItemViewType(int row, int column) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	
	//public void setTableFixHeadersPointer(TableFixHeaders pTableFixHeaders) {
	//	this.pTableFixHeaders = pTableFixHeaders;
	//}
	
	
	public void setOnCellSizeGetter(OnCellSizeGetter onCellSizeGetter) {
		this.onCellSizeGetter = onCellSizeGetter;
	}
	
	
	public void setEditableHeader(boolean bEditable) {
		this.bEditableHeader = bEditable;
	}
	
	public void setEditableData(boolean bEditable) {
		this.bEditableData = bEditable;
	}
	
	public void setOnDataSetObserver(OnDataSetObserver onDataSetObserver) {
		this.onDataSetObserver = onDataSetObserver;
		MatrixTableAdapter.this.setInformation(table);
		MatrixTableAdapter.this.onDataSetObserver.onDataChanged(table);
	}
	
	public void setOnViewPostCreationWizard(OnViewPostCreationWizard onViewPostCreationWizard) {
		this.onViewPostCreationWizard = onViewPostCreationWizard;
	}
	

	
	/**
	 * add new option to edit table dialog
	 * @param option
	 */
	public void addEditTableOption(EditTableOption option) {
		this.editTableDialogBuilder.addOption(option);
	}
	
	/**
	 * erase all edit table dialog options
	 */
	public void clearEditTableOptions() {
		this.editTableDialogBuilder.clearOptions();
	}
	
	
	
	public Table getTable() {
		return table;
	}
	
	
}
