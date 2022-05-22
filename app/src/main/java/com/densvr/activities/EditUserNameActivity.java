package com.densvr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.densvr.androidsfr.databinding.ActivityEditUserNameBinding;
import com.densvr.nfcreader.OldChipData;
import com.densvr.nfcreader.OldGlobals;

@Deprecated //Old activity. Not use it in future
public class EditUserNameActivity extends Activity {

	private ActivityEditUserNameBinding binding;
	String startUserName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityEditUserNameBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		binding.editText.setText(OldGlobals.chipData.getUserName());
		binding.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

		binding.buttonBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditUserNameActivity.this.finish();
			}
		});
		binding.buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editName();
			}
		});
		
	}

	private void editName() {
		OldChipData chipData = OldGlobals.chipData;
		String newUserName = binding.editText.getText().toString();
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
