package com.densvr.activities;

import com.densvr.nfcreader.OldChipData;
import com.densvr.nfcreader.OldGlobals;
import com.densvr.nfcreader.ResultsProtocol;
import com.densvr.androidsfr.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EditAttemptActivity extends Activity {
	
	
	private TextView textView;
	private Button buttonNo;
	private Button buttonYes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_attempt);
		
		
		textView = (TextView)findViewById(R.id.act_edit_attempt_view_text);
		buttonNo = (Button)findViewById(R.id.act_edit_attempt_button_no);
		buttonYes = (Button)findViewById(R.id.act_edit_attempt_button_yes);
		
		OldChipData chipData = OldGlobals.chipData;
		textView.setText(String.format(	
			"%s уже бегал дистанцию %s\nтекущая попытка - %d\n\rоставить старые попытки?",
//			"%s уже бегал дистанцию %s %d раз. Добавить новую попытку?", 
			chipData.getUserName(), chipData.getDistName(), chipData.getAttempt()
			));
		
		buttonNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
				//delete all attempts from table results activity
				ResultsProtocol rp = ResultsProtocol.readFromDatabase();
				
				OldGlobals.chipData.setAttempt(1);
				Intent actIntent = new Intent(getApplicationContext(), TableIntermediateActivity.class);
				startActivity(actIntent);
			}
		});
		buttonYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent actIntent = new Intent(getApplicationContext(), TableIntermediateActivity.class);
				startActivity(actIntent);
			}
		});
		
	}
	
}
