package com.densvr.nfcreader;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.densvr.activities.MainActivity;

import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.util.Log;

@Deprecated
public class OldNfcVReaderTask {

	//TableFixHeaders tableFixHeaders = null;
	private OldChipData chipData;
	
	public boolean execute(Tag... params) {
		Tag tag = params[0];
		NfcV nfcvTag = NfcV.get(tag);
		//byte[] tagID = nfcvTag.getTag().getId();
		List<Byte> data = new LinkedList();
		
		try {
			nfcvTag.close();
			nfcvTag.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MainActivity.makeText("поднесите чип еще раз");
			return false;
		}
		
		/*	
		switch(params[0][0]){
	        case ReadSingleBlock:
	            mCommand = new byte[]{0x02, 0x20, params[1][0]};
	            break;
	        case ReadMultipleBlocks:
	            mCommand = new byte[]{0x02, 0x23,params[1][0],params[2][0]};
	            break;
	        case WriteSingleBlock:
	            mCommand = new byte[]{0x42, 0x21, (byte)params[1][0],params[2][0],params[2][1],params[2][2],params[2][3]};
	            break;
	        case GetSystemInfo:
	            mCommand = new byte[]{0x00,(byte)0x2B};
	            break;
	    }*/

		for (byte i = 0; i < 62; i++) {
			byte[] mCommand = new byte[]{0x02, 0x20, i};
			byte[] dataBlock = null;
			try {
				dataBlock = nfcvTag.transceive(mCommand);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				try {
					nfcvTag.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			}
			if (dataBlock == null) {
				break;
			}
			for(int j = i == 0 ? 0 : 1; j < dataBlock.length; j++) {
				data.add(dataBlock[j]);
			}
		}

		Log.d("densvr_d", Arrays.toString(data.toArray()));
		
		try {
			nfcvTag.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		byte[] arrayData = new byte[data.size()];
		for(int j = 0; j < data.size(); j++) {
			arrayData[j] = data.get(j);
		}

		chipData = OldChipData.parseChipArray(arrayData);
		if (chipData == null) {
			return false;
		}
		return true;
	}
	
	public OldChipData getChipData() {
		return chipData;
	}

}
