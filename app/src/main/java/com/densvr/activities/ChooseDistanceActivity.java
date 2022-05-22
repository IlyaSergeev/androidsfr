package com.densvr.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.densvr.androidsfr.databinding.ActivityChooseDistBinding;
import com.densvr.nfcreader.OldChipData;
import com.densvr.nfcreader.OldGlobals;
import com.densvr.table.csv.CSV;
import com.densvr.table.csv.Table;

@Deprecated //Old activity. Not use it in future
public class ChooseDistanceActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityChooseDistBinding binding = ActivityChooseDistBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		Table dists = CSV.read(OldGlobals.CSV_DISTS);
		B b[] = new B[dists.cols()];
		for(int i = 0; i < dists.cols(); i++) {
			b[i] = new B(dists.get(0).get(i));
		}
		setListAdapter(new ArrayAdapter<B>(this, android.R.layout.simple_list_item_1, android.R.id.text1, b));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String distName = ((B) l.getItemAtPosition(position)).string;
		OldChipData chipData = OldGlobals.chipData;
		chipData.setDistName(distName);
		//Intent intent = new Intent(this, ((B) l.getItemAtPosition(position)).class1);
		//intent.putExtra("dist_name", ((B) l.getItemAtPosition(position)).string);
		//intent.putExtra("data", chipData);	
		//startActivity(intent);
		//startActivity(new Intent(null, TableIntermediateActivity.class));
		
		//TODO finalize this activity and starts its parent
		//EditUserNameActivity.this.finish();
		startActivity(new Intent(getBaseContext(), TableIntermediateActivity.class));
	}
	
	
	private static class B {
		private final String string;

		B(String string) {
			this.string = string;
		}

		@NonNull
		@Override
		public String toString() {
			return string;
		}
	}
}
