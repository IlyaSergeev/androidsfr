package com.densvr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.densvr.androidsfr.databinding.ActivityEditAttemptBinding;
import com.densvr.nfcreader.OldChipData;
import com.densvr.nfcreader.OldGlobals;
import com.densvr.nfcreader.ResultsProtocol;

@Deprecated //Old activity. Not use it in future
public class EditAttemptActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityEditAttemptBinding binding = ActivityEditAttemptBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		
		OldChipData chipData = OldGlobals.chipData;
		binding.actEditAttemptViewText.setText(String.format(
			"%s уже бегал дистанцию %s\nтекущая попытка - %d\n\rоставить старые попытки?",
//			"%s уже бегал дистанцию %s %d раз. Добавить новую попытку?", 
			chipData.getUserName(), chipData.getDistName(), chipData.getAttempt()
			));

		binding.actEditAttemptButtonNo.setOnClickListener(v -> {
            //TODO delete all attempts from table results activity
            ResultsProtocol rp = ResultsProtocol.readFromDatabase();

            OldGlobals.chipData.setAttempt(1);
            Intent actIntent = new Intent(getApplicationContext(), TableIntermediateActivity.class);
            startActivity(actIntent);
        });
		binding.actEditAttemptButtonYes.setOnClickListener(v -> {
			Intent actIntent = new Intent(getApplicationContext(), TableIntermediateActivity.class);
			startActivity(actIntent);
		});
	}
}
