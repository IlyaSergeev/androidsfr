package com.densvr.table.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import com.densvr.activities.MainActivity;
import com.densvr.nfcreader.Globals;

import android.app.Activity;
import android.content.Context;

public class CSV {
	
	//csv support
	public static Table read(String name) {
		LinkedList<LinkedList<String>> data1 = new LinkedList<LinkedList<String>>();
		int cols = 0;
		try {
			File myFile = new File(Globals.CSV_ADDRESS + name);
			//if (!myFile.exists()) {
			//	if (!myFile.createNewFile()) {
			//		return null;
			//	}
			//}
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fIn));
			String line = null;
			while(true) {
				line = br.readLine();
			    if (line == null) {
			    	break;
			    }
			    line = line.replace(',', ';');
			    String[] vals = line.split(";");
			    LinkedList<String> l = new LinkedList<String>();
			    for(int i = 0; i < vals.length; i++) {
			    	l.add(vals[i]);
			    }
			    data1.add(l);
			    if (vals.length > cols) {
			    	cols = vals.length;
			    }
			} 
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Table data = new Table();
		for(int i = 0; i < data1.size(); i++) {
			data.add(new LinkedList<String>());
			for(int j = 0; j < cols; j++) {
				data.get(i).add("");
			}
			for(int j = 0; j < data1.get(i).size(); j++) {
				data.get(i).set(j, data1.get(i).get(j));
			}
		}
		return data;
	}
	
	public static void write(Table data, String name) {
		data.expand();
		try {
			File myFile = new File(Globals.CSV_ADDRESS + name);
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
	        for(int i = 0; i < data.size(); i++) {
	        	for(int j = 0; j < data.get(i).size() - 1; j++) {
					myOutWriter.write(data.get(i).get(j) + ";");
	        	}
	        	myOutWriter.write(data.get(i).get(data.get(i).size() - 1) + "\n");
		    }
	        myOutWriter.close();
	        fOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
//=====================================	
//for debug only (creates some files)
//=====================================	
	
	
	private static String[] dists = new String[]{
			"ml1;ml2;sr1;sr2;st1;st2;st3",
			"31;37;101;35;35;41;32",
			"32;38;102;36;36;42;33",
			"33;39;103;106;106;43;34",
			"34;40;104;37;107;44;35",
			"35;41;;38;37;45;46",
			";;;32;32;35;37",
			";;;100;100;36;48",
			";;;;;106;52",
			";;;;;37;49",
			";;;;;38;100",
			";;;;;32;",
			";;;;;100;" 
		};
	
	private static String[] names = new String[]{
		"Чип;Фамилия;Имя",
		"1;ПРОСЯН;МАКСИМ",
		"2;КАШИН;НИКОЛАЙ",
		"3;ПЕРЕСЫПКИН;АНДРЕЙ",
		"123;РОЖДЕСТВЕНСКИЙ;АЛЕКСАНДР",
		"5;НИКОЛАЕВ;МИХАИЛ",
		"9600122;СЕРГЕЕВ;ДАНИЭЛ",
		"6;КАРАБАНОВА;АНАСТАСИЯ",
		"7;ФОМИНА;ЕЛИЗАВЕТА",
		"8;МИКУЛАН;ЕКАТЕРИНА",
		"9;ЦВЕТКОВ;ИЛЬЯ",
		"10;ШАПОШНИКОВ;ИЛЬЯ",
		"11;ХРУСТАЛЕВ;АНДРЕЙ",
		"12;МОЛОЛКИН;КИРИЛЛ",
		"13;АЛЯБЬЕВ;КОНСТАНТИН",
		"186;ТУПИЦЫН;ЛЕОНИД",
		"15;ВОРОБЬЕВ;ВЛАД",
		"16;ФЕДОРОВ;ВЛАД",
		"17;САЛАЙ;ЮЛИЯ",
		"18;ВАСИЛЬЕВ;КИРИЛЛ",
		"19;КИНИЧЕНКО;ВЛАД",
		"20;АРЕФЬЕВ;МИША",
		"21;Малявкина;АНЯ",
		"22;ШЕВЕЛЕВА;НАСТЯ",
		"23;ГАДЖИЕВА;НАСТЯ",
		"25;Зиновьева;Вика",
		"26;Гояев;Артем",
		"27;Золоторев;Даня"
	};
	
	
	public static void saveArr(String[] arr, String name) {
		Table data = new Table();
		for(int i = 0; i < arr.length; i++) {
			String[] v = arr[i].split(";");
			data.add(new LinkedList<String>());
			for(int j = 0; j < v.length; j++) {
				data.get(i).add(v[j]);
			}
		}
		CSV.write(data, name);
	}
	
	public static void createNamesAndDists() {
		saveArr(dists, Globals.CSV_DISTS);
		saveArr(names, Globals.CSV_NAMES);
	}
}
