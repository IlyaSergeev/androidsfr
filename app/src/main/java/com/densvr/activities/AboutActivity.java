package com.densvr.activities;

import com.densvr.nfcreader.OldGlobals;
import com.densvr.androidsfr.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity {

	
	
	Button buttonBack;
	TextView textView; 
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		
		//buttons
		buttonBack = (Button) findViewById(R.id.buttonBack);
	
		//back
		buttonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AboutActivity.this.finish();
			}
			
		});


		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		String version = pInfo.versionName;
		int code = pInfo.versionCode;
		
		textView = (TextView) findViewById(R.id.textView1);
		
		textView.setText("Приложение AndroidSFR, версия " + version + ", код " + code
				+ "\nРазработано для проведения тренировок по спортивному ориентированию в полевых условиях."
				+ "\nИспользуется система SFR-system http://sfr-system.com"
				+ "\nПриложение рассчитано на android 2.3.3+ и требует наличия NFC (Near Field Communication)."
				+ "\nДля работы приложения необходимо создать на sd карте папку " + OldGlobals.CSV_ADDRESS + " и включить NFC."
				+ "\nПри поднесении чипа данные будут считаны и откроется окно промежуточных результатов."
				+ "\nПри нажатии кнопки сохранить в окне промежуточных результатов результат будет добавлен в финальный протокол."
				+ "\nПротокол результатов, имена и дистанции хранятся в формате csv в папке" + OldGlobals.CSV_ADDRESS + "."
				+ " \n"
				+ "\nАвтор: Даниэл Сергеев densvr@list.ru "
				+ "\nРазработано при поддержке клуба СК Петродворец Ориентирование. http://denpsv.narod.ru"
				+ "\nОсобая благодарность Д.С. Пьянкову за предоставление идеи и поддержку."
				+ " \n"
				+ "\nСанкт-Петербург"
				+ "\nоктябрь 2016");
		
		 Linkify.addLinks(textView, Linkify.ALL);
		
		 
		
		
	}
	
}
