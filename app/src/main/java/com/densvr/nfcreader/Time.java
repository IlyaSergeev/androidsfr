package com.densvr.nfcreader;

import android.annotation.SuppressLint;

@Deprecated
@SuppressLint("DefaultLocale") public class Time {

	
	private long val;
	
	
	public Time() {
		setVal(0);
	}
	
	public Time(int val) {
		this.setVal(val);
	}
	
	//hh:mm:ss
	public static Time parseString(String s) {
		Time t = new Time();
		//s = s.replace("-", ""); //fast fix TODO find, why '-' appears
		String[] vs = s.split(":");
		if (vs.length == 3) {
			int hours = Integer.parseInt(vs[0]);
			int minutes = Integer.parseInt(vs[1]);
			int seconds = Integer.parseInt(vs[2]);
			t.setVal(seconds + 60 * minutes + 3600 * hours);
		}
		if (vs.length == 2) {
			int minutes = Integer.parseInt(vs[0]);
			int seconds = Integer.parseInt(vs[1]);
			t.setVal(seconds + 60 * minutes);
		}
		if (vs.length == 1) {
			int seconds = Integer.parseInt(vs[0]);
			t.setVal(seconds);
		}
		return t;
	}
	
	public static Time genTime(int hh, int mm, int ss) {
		if (hh < 0) {
			hh = 0;
		}
		if (mm < 0) {
			mm = 0;
		}
		if (ss < 0) {
			ss = 0;
		}
		Time t = new Time();
		t.setVal(ss + 60 * mm + 3600 * hh);
		return t;
	}
	
	//hh:mm:ss
	@SuppressLint("DefaultLocale") 
	public String toString() {
		String s = "";
		int hours = (int) (getVal() / 3600);
		int minutes = (int)((getVal() - hours * 3600) / 60);
		int seconds = (int)(getVal() - hours * 3600 - minutes * 60);
		if (minutes < 0) {
			minutes = 0;
		}
		if (seconds < 0) {
			seconds = 0;
		}
		if (hours != 0) {
			if (hours < 10) {
				s += "0";
			}
			s += String.valueOf(hours) + ":";
		}
		if (minutes < 10) {
			s += "0";
		}
		s += String.valueOf(minutes) + ":";
		if (seconds < 10) {
			s += "0";
		} 
		s += String.valueOf(seconds);
		return s;
	}
	
	
	public Time add(Time t) {
		Time res = new Time();
		res.setVal(this.getVal() + t.getVal());
		return res;
	}
	
	public Time diff(Time t) {
		Time res = new Time();
		res.setVal(this.getVal() - t.getVal());
		return res;
	}

	public long getVal() {
		return val;
	}

	public void setVal(long val) {
		if (val < 0) {
			val = 0;
		}
		this.val = val;
	}

	
}
