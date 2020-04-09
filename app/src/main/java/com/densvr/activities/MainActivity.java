package com.densvr.activities;

import java.io.File;

import com.densvr.nfcreader.OldChipData;
import com.densvr.nfcreader.OldDistsProtocol;
import com.densvr.nfcreader.OldGlobals;
import com.densvr.nfcreader.NfcVReaderTask;
import com.densvr.androidsfr.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;


public class MainActivity extends ListActivity {

	//Properties...
	private final Context ctx = this;

	private NfcAdapter nfcAdapter;
	
	//pointer to mainActivity
	public static Activity pActivity;

	public CheckBox checkBoxSimpleMode;

	/*
	public static String CSV_ADDRESS = "/sdcard/AndroidSFR/";
	public static String CSV_NAMES = "names.csv";
	public static String CSV_DISTS = "dists.csv";
	public static String CSV_RESULTS = "results.csv";
	*/
	
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pActivity = this;
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		//if (nfcAdapter == null) {
		//	makeText("для работы программы включите NFC");
		//}
		
		//debug files creation
		//CSV.createNamesAndDists();
	
		//Globals.CSV_ADDRESS = getApplicationContext().getFilesDir().getAbsolutePath() + "/AndroidSFR/";
		OldGlobals.CSV_ADDRESS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
				.getAbsolutePath() + "/AndroidSFR/";
		File dataDir = new File(OldGlobals.CSV_ADDRESS);
		if (!dataDir.canWrite()) {
			dataDir.mkdirs();
		}
		if (dataDir.canWrite()) {
			Log.i("AndroidSFR", "can");
		}


		checkBoxSimpleMode = (CheckBox)findViewById(R.id.checkBox_simple_mode);
		SharedPreferences settings = this.getSharedPreferences("UserInfo", 0);
		checkBoxSimpleMode.setChecked(settings.getBoolean("SimpleMode", false));
		checkBoxSimpleMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences settings = ctx.getSharedPreferences("UserInfo", 0);
				settings.edit().putBoolean("SimpleMode", isChecked).apply();
			}
		});

		
		//if (nfcAdapter.isEnabled() == false) {
			//textView.setText("NFC is disabled.");
		//}
		
		final B b[] = new B[] {
				new B("дистанции", TableDistsActivity.class),
				new B("имена", TableNamesActivity.class),
				new B("результаты", TableResultsActivity.class),
				new B("о программе", AboutActivity.class),
				new B("chip imitation", ChooseDistanceActivity.class)

		};
		ArrayAdapter<B> arrAdapter = new ArrayAdapter<B>(this, R.layout.listview_item, R.id.item_textView, b) {
			
			@Override 
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				if (v == null) {
					return convertView;
				}
				LinearLayout layout = (LinearLayout)v;
				if (layout != null) {
					//convertView.setBackgroundResource(R.drawable.table_color2);
				}
				TextView textView = (TextView)layout.findViewById(R.id.item_textView);
				textView.setVisibility(View.VISIBLE);
				textView.setText(b[position].string);
						//convertView = LayoutInflater.from(ctx).inflate(R.layout.shelfrow, parent, false)
				return layout;
			}
		};
		setListAdapter(arrAdapter);
		handleIntent(getIntent());
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.

	}


	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		LinearLayout layout = (LinearLayout)v;
		TextView textView = (TextView)layout.findViewById(R.id.item_textView);
		if (textView.getText().equals("chip imitation")) {// chip imitation
			if (OldDistsProtocol.isThereAreNoDists()) {
				MainActivity.makeText("нет дистанций");
				return;
			} 
			OldGlobals.chipData = OldChipData.genChipDataForImitation();
			onNewChipData();
			return;
		} else if (textView.getText().length() == 0) {
			//do nothing
			return;
		}
		startActivity(new Intent(this, ((B) l.getItemAtPosition(position)).class1));
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
	
	
	
	
	
	@Override
	public void onResume() {
		super.onResume();	
		if (nfcAdapter != null) {
			setupForegroundDispatch(this, nfcAdapter);
		} else {
			nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		}
		if (nfcAdapter == null) {
			makeText("для работы программы включите NFC");
		}
	}
	
	@Override
	public void onPause() {
		if (nfcAdapter != null) {
			stopForegroundDispatch(this, nfcAdapter);
		}
		super.onPause();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}
	
	/**
	 * 
	 * @param intent	The calling {@link Intent} (When an NfcV device is recognized)
	 */
	private void handleIntent(Intent intent){
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Log.d("Action", "ACTION_TAG_DISCOVERED");			
			Log.d("Tag", tag.toString());
			String[] techList = tag.getTechList();
			String searchedTech = NfcV.class.getName();
			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					//Log.d("Tech", tech);
					NfcVReaderTask task = new NfcVReaderTask();
					if (!task.execute(tag)) {
						MainActivity.makeText("поднесите чип еще раз");
					} else {
						OldGlobals.chipData = task.getChipData();
						onNewChipData();
					}	
					break;
				} //end if
			}
		}
	}
	
	/**
	 * called when new chip data appears
	 * @return
	 */
	private boolean onNewChipData() {
		if (OldGlobals.chipData.getCPs().size() == 0) {
			MainActivity.makeText("пустой чип");
			return false;
		}
		if (checkBoxSimpleMode.isChecked()) {
			//simple mode
			Intent actIntent = new Intent(ctx, TableIntermediateActivity.class);
			startActivity(actIntent);
		} else {
			//complex mode
			if (OldDistsProtocol.isThereAreNoDists()) {
				MainActivity.makeText("нет дистанций");
				return false;
			}
			Intent actIntent = new Intent(ctx, ChooseDistanceActivity.class);
			startActivity(actIntent);
		}
		return true;
	}
	
	
	/**
	 * 
	 * @param activity	The {@link Activity} requesting foreground dispatch.
	 * @param adapter	The {@link NfcAdapter} used for the foreground dispatch.
	 */
	public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
		
		IntentFilter[] filters = new IntentFilter[1];
		String[][] techList = new String[][]{};
		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		
		adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
	}
	
	/**
	 * 
	 * @param activity	The {@link Activity} requesting to stop the foreground dispatch
	 * @param adapter	The {@link NfcAdapter} used for the foreground dispatch
	 */
	public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}
	
	
	
	
	public static void makeText(String text) {
		Toast.makeText(pActivity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

	
}
