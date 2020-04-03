package com.densvr.activities;

import com.densvr.nfcreader.OldChipData;
import com.densvr.nfcreader.OldGlobals;
import com.densvr.androidsfr.R;
import com.densvr.table.csv.CSV;
import com.densvr.table.csv.Table;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChooseDistanceActivity extends ListActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_dist);
		Table dists = CSV.read(OldGlobals.CSV_DISTS);
		B b[] = new B[dists.cols()];
		for(int i = 0; i < dists.cols(); i++) {
			b[i] = new B(dists.get(0).get(i), EditUserNameActivity.class);
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
	
	
	private class B {
		private final String string;
		private final Class<? extends Activity> class1;

		B(String string, Class<? extends Activity> class1) {
			this.string = string;
			this.class1 = class1;
		}

		@Override
		public String toString() {
			return string;
		}
	}
}
