package com.densvr.activities;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.densvr.nfcreader.OldChipData;
import com.densvr.nfcreader.OldDistsProtocol;
import com.densvr.nfcreader.OldGlobals;
import com.densvr.nfcreader.ResultsProtocol;
import com.densvr.nfcreader.OldChipData.CP;
import com.densvr.androidsfr.R;
import com.densvr.table.TableFixHeaders;
import com.densvr.table.csv.MatrixTableAdapter;
import com.densvr.table.csv.MatrixTableAdapter.OnViewPostCreationWizard;
import com.densvr.table.csv.Table;

import static com.densvr.util.TimeFormatKt.secondsFormatString;

public class TableIntermediateActivity extends Activity {


	private Context ctx;

	private Table table = new Table();
	
	private MatrixTableAdapter matrixTableAdapter;
	private TableFixHeaders tableFixHeaders;

	private Button buttonCancel;
	private Button buttonSave;
	private CheckBox checkBoxReestablish;
	private CheckBox checkBoxOverwriteAttempt;
	
	private Button buttonEditName;
	private Button buttonEditDistance;


	
	
	
	/**
	 * link to Globals.chipData
	 */
	private OldChipData chipData;

	/**
	 * index wrong cp
	 */
	private int disqCPNum;

	private int headersCnt;




	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.ctx = this;
		setContentView(R.layout.activity_intermediate_results);
		//setContentView(R.layout.table);

		chipData = OldGlobals.chipData;
		
		//inites disqCpNum
		disqCPNum = -1;
		createIntermediateResultsTable();
		
		TableFixHeaders tableFixHeaders = (TableFixHeaders) findViewById(R.id.inter_results_table);
		matrixTableAdapter = new MatrixTableAdapter(tableFixHeaders, this, table);
		tableFixHeaders.setAdapter(matrixTableAdapter);
		
		//ISO15693 - ti не поддерживается
		//опциональные команды убрали

		matrixTableAdapter.setOnViewPostCreationWizard(new OnViewPostCreationWizard() {

			@Override
			public void onViewCreationFinished(View view, int row, int column) {
				view.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						TableIntermediateActivity.this.showEditTableDialog();
						return true;
					}
				});
				if (chipData.isDisqualified()) {
					if (row == disqCPNum + headersCnt) {
						//make cp with fail red
						view.setBackgroundResource(R.drawable.table_inter_results_color_disq_cell);
					}
				}

			}
		});

		buttonCancel = (Button)findViewById(R.id.button_cancel);
		buttonSave = (Button)findViewById(R.id.button_save);
		checkBoxReestablish = (CheckBox)findViewById(R.id.CheckBox_reestablish);
		checkBoxOverwriteAttempt = (CheckBox)findViewById(R.id.checkBox_overwrite_attempt);	
		
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TableIntermediateActivity.this.finish();
				startActivity(new Intent(MainActivity.pActivity.getBaseContext(), MainActivity.class));
			}
		});

		buttonSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//save to results protocol
				TableIntermediateActivity.this.saveToResultsProtocol();
				MainActivity.makeText("результат сохранен");
				TableIntermediateActivity.this.finish();
				startActivity(new Intent(MainActivity.pActivity.getBaseContext(), MainActivity.class));
			}
		});
		
		
		checkBoxReestablish.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (!TableIntermediateActivity.this.chipData.isDisqualified()) {
						buttonView.setChecked(false);
						MainActivity.makeText("нельзя восстановить не снятого участника");
					} else {
						MainActivity.makeText("участник будет восстановлен");
					}
				} else {
					MainActivity.makeText("участник будет снят");
				}
				
			}
		});
		
		checkBoxOverwriteAttempt.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					MainActivity.makeText("попытка будет добавлена без перезаписи");
				} else {
					MainActivity.makeText("попытка будет перезаписана");
				}
				
			}
		});
		
	}
	
	/**
	 * checks results, find place
	 * CSV_DISTS must be filled
	 * @return
	 */
	@SuppressLint("DefaultLocale") 
	private boolean createIntermediateResultsTable() {
		
		//init distance 
		OldDistsProtocol distsProtocol = OldDistsProtocol.readFromDatabase();
		OldChipData distData = null;
		if (chipData.getDistName().isEmpty()) {
			//TODO predict distance
			distData = distsProtocol.predictDistance(chipData);
			if (distData == null) {
				distData = chipData;
			}
			chipData.setDistName(distData.getDistName());
		} else {
			distData = distsProtocol.getDistByName(chipData.getDistName());
		}
		if (distData == null) {
			return false;
		}
		
		
		//init attempt
		int attempt = ResultsProtocol.readFromDatabase().getAttempt(chipData.getUserName(), chipData.getDistName());
		chipData.setAttempt(attempt);
		
		
		
		LinkedList<String> line = null;
		
		line = new LinkedList<String>();
		line.add("Сплит");
		String[] userSernameAndName = chipData.getSernameAndName();
		line.add(userSernameAndName[0]);
		line.add(userSernameAndName[1]);
		table.add(line);
		//line 2
		line = new LinkedList<String>();
		line.add("Дистанция");
		line.add(chipData.getDistName());
		line.add(String.format("попытка %d", chipData.getAttempt()));
		table.add(line);
		//line 3  
		line = new LinkedList<String>();
		line.add("Результат");
		line.add(""); //after cp lines 
		line.add("");
		table.add(line);
		//line 4
		line = new LinkedList<String>();
		line.add("Должно быть");
		line.add("Отметил");
		line.add("");
		table.add(line);


		headersCnt = table.rows();

		//TODO outer cycle by distance and inner by chip data
		//cp lines
		//ChipData distData = TableDistsActivity.getDistByName(tDists, chipData.getDistName());
		Long startTime = 0L;
		Long finishTime = 0L;
		disqCPNum = -1; //not disqualified
		int disqDistCpNum = -1;
		int distCPIter = 0;
		Set<Integer> choiceCPs = new HashSet<>();
		for(int chipCPIter = 0; chipCPIter < chipData.getCPs().size(); chipCPIter++) {
			CP chipCP = chipData.getCPs().get(chipCPIter);
			line = new LinkedList<String>();
			if (distCPIter < distData.getCPs().size()) {
				CP distCP = distData.getCPs().get(distCPIter);
				if (distCP.number == chipCP.number)
				{
					if (distCPIter == 0) {
						startTime = chipCP.lapTime;
					}
					choiceCPs.clear();
					choiceCPs.add(chipCP.number);
					line.add(String.valueOf(distCP.number));
					distCPIter++;
					disqCPNum = -1; //not disqualified
				} else if (distCP.number == -1) {
					if (!choiceCPs.contains(chipCP.number)) {
						choiceCPs.add(chipCP.number);
						distCPIter++;
						line.add(String.valueOf(distCP.number));
						disqCPNum = -1; //not disqualified
					} else {
                        //CP was already taken in choice mode
                        line.add("");
                        if (disqCPNum == -1) {
                            disqCPNum = chipCPIter;
                            disqDistCpNum = distCPIter;
                        }
                    }
				} else {
					line.add("");
					if (disqCPNum == -1) {
						disqCPNum = chipCPIter;
						disqDistCpNum = distCPIter;
					}
				}
			}
			line.add(String.valueOf(chipCP.number));
			line.add(secondsFormatString(chipCP.splitTime));
			table.add(line);
			if (distCPIter >= distData.getCPs().size()) {
				//all dist cps checked
				finishTime = chipCP.lapTime;
				disqCPNum = -1;
				break;
			}
		}
		if (disqCPNum == -1 && distCPIter < distData.getCPs().size()) {
			disqCPNum = table.rows() - headersCnt;
			disqDistCpNum = distCPIter;
		}
		if (chipData.getCPs().size() > 1) {
			if (startTime == null) {
				startTime = chipData.getCPs().get(0).lapTime;
			}
			if (finishTime == null) {
				finishTime = chipData.getCPs().get(chipData.getCPs().size() - 1).lapTime;
			}
		}
		if (startTime != null && finishTime != null) {
			chipData.setFullTime(finishTime - startTime);
		}

		//if disqualified, add rest cps
		if (disqCPNum != -1) {
			for(int i = disqDistCpNum; i < distData.getCPs().size(); i++) {
				table.setValue(headersCnt + disqCPNum + i - disqDistCpNum, 0, String.valueOf(distData.getCPs().get(i).number));
			}
		}

		
		//set disqualified
		if (disqCPNum == -1) {
			chipData.setDisqualified(false);
		} else {
			chipData.setDisqualified(true);
		}
		
		//add time
		line = new LinkedList<String>();
		line.add("");
		line.add("Время");
		line.add(secondsFormatString(chipData.getFullTime()));
		table.add(line);
		
		//add place and attempt place
		ResultsProtocol results = ResultsProtocol.readFromDatabase();
		chipData.setPlace(results.getTheoreticalPlace(chipData));
		chipData.setAttemptPlace(results.getTheoreticalAttemptPlace(chipData));
		table.setValue(2, 1, chipData.getPlaceStr());
		table.setValue(2, 2, chipData.getAttemptPlaceStr());
	
		return true;
	}

	/**
	 * adds current result to results protocol
	 */
	private void saveToResultsProtocol() {
		if (checkBoxReestablish.isChecked()) {
			if (chipData.isDisqualified()) {
				chipData.setReestablished(true);
			}
		} else {
			chipData.setReestablished(false);
		}
		//save chip data to results protocol
		ResultsProtocol rp = ResultsProtocol.readFromDatabase();
		//Log.i("android SFR", rp.toString());
		if (checkBoxOverwriteAttempt.isChecked()) {
			//overwrite
			rp.overwriteChipData(OldGlobals.chipData);
		} else {
			//insert 
			rp.addChipData(OldGlobals.chipData);
		}
		rp.writeToDatabase();
		//Log.i("android SFR", rp.toString());
	}
	
	/**
	 * opens dialog with edit name and edit distance options (buttons)
	 */
	private void showEditTableDialog() {
		String title = "Редактировать";
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title);
		CharSequence optNames[] = new CharSequence[] {
			"имя", "дистанцию", "в обратную сторону", "отменить"
		};
		alert.setItems(optNames, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case 0: //edit name
					editUserName();
					break;
				case 1: //edit distance
					editDistance();
					break;
				case 2:
					if (switchToBackwardDistance()) {
						//Toast.makeText(ctx, "Обратный порядок кп", Toast.LENGTH_SHORT).show();
						TableIntermediateActivity.this.recreate();
					} else {
						Toast.makeText(ctx, "Не удалось изменить порядок кп", Toast.LENGTH_SHORT).show();
					}
					break;
				default: //cancel
					//do nothing
					break;
				}
				dialog.cancel();
				
			}
		});
		alert.show();
	}
	
	/**
	 * for editing user name button
	 */
	private void editUserName() {
		startActivity(new Intent(getBaseContext(), EditUserNameActivity.class));
	}
	
	/**
	 * for editing distance name button
	 */
	private void editDistance() {
		startActivity(new Intent(getBaseContext(), ChooseDistanceActivity.class));
		
	}

	/**
	 * switches chip data to backward order
	 * @return
	 */
	private boolean switchToBackwardDistance() {
		OldDistsProtocol distsProtocol = OldDistsProtocol.readFromDatabase();
		OldChipData distance = distsProtocol.getDistByName(chipData.getDistName());
		if (distance == null) {
			return false;
		}
		OldChipData backwardDistance = distsProtocol.addBackwardDist(distance, getString(R.string.backward_distance_postfix));
		if (backwardDistance == null) {
			return false;
		}
		chipData.setDistName(backwardDistance.getDistName());
		distsProtocol.writeToDatabase();
		return true;
	}
	
	
	
}
