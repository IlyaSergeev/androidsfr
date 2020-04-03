package com.densvr.activities;

import com.densvr.nfcreader.OldChipData;
import com.densvr.nfcreader.OldGlobals;
import com.densvr.androidsfr.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditUserNameActivity extends Activity {
	
	
	private EditText editText;
	private Button buttonBack;
	private Button buttonOk;
	
	String startUserName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_user_name);
		
		
		editText = (EditText)findViewById(R.id.edit_text);
		buttonBack = (Button)findViewById(R.id.button_back);
		buttonOk = (Button)findViewById(R.id.button_ok);
		
		editText.setText(OldGlobals.chipData.getUserName());
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
					editName();//match this behavior to your 'Send' (or Confirm) button
					return true;
				}
				return false;
			}
		});

		
		startUserName = new String(OldGlobals.chipData.getUserName());
		
		buttonBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditUserNameActivity.this.finish();
			}
		});
		buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editName();
			}
		});
		
	}

	private void editName() {
		OldChipData chipData = OldGlobals.chipData;
		String newUserName = editText.getText().toString();
		chipData.setUserName(newUserName);
		if (!newUserName.equals(startUserName)) {
			//update name in database
			TableNamesActivity.writeUserToCSV(chipData.getUserId(), chipData.getUserName());
		}

		//set attempt (necessary for next step)
		//int attempt =
		//		ResultsProtocol.readFromDatabase().getAttempt(chipData.getUserName(), chipData.getDistName());
		//chipData.setAttempt(attempt);
		//Intent intent = null;
		//if (attempt == 1) {
		//	intent = new Intent(getApplicationContext(), TableIntermediateActivity.class);
		//} else {
		//	intent = new Intent(getApplicationContext(), EditAttemptActivity.class);
		//}
		//startActivity();

		//TODO finalize this activity and starts its parent
		//EditUserNameActivity.this.finish();
		startActivity(new Intent(getBaseContext(), TableIntermediateActivity.class));
	}
	
}
