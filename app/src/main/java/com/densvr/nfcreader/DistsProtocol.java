package com.densvr.nfcreader;

import java.util.LinkedList;
import java.util.List;

import com.densvr.table.csv.CSV;
import com.densvr.table.csv.Table;

@Deprecated
public class DistsProtocol {

	private List<ChipData> dists;
	
	
	
	private DistsProtocol() {
		dists = new LinkedList<ChipData>();
	}
	
	/**
	 * reads dists from Globals.CSV_DISTS
	 * @return can return null
	 */
	public static DistsProtocol readFromDatabase() {
		DistsProtocol distsProtocol = new DistsProtocol();
		List<ChipData> dists = distsProtocol.dists;
		Table tDists = CSV.read(Globals.CSV_DISTS);
		
		if (tDists == null) {
			return distsProtocol; 
		} else if (tDists.isEmpty()) {
			return distsProtocol; 
		}
		
		//tDists must have at least one col and one row
		for(int col = 0; col < tDists.cols(); col++) {
			String distName = tDists.getValue(0, col);
			if (distName.isEmpty()) {
				continue;
			}
			LinkedList<String> distCps = new LinkedList<String>();
			for(int row = 1; row < tDists.rows(); row++) {
				String cur = tDists.getValue(row, col);
				if (!cur.isEmpty()) {
					distCps.add(cur);
				} else {
					break;
				}
			}
			ChipData cd = ChipData.parseDistsResultsLine(distCps);
			if (cd == null) {
				return null;
			}
			cd.setDistName(distName);
			dists.add(cd);
		}
		return distsProtocol;
	}

	/**
	 * overwrite existing CSV_DISTS file to this dists protocol
	 */
	public void writeToDatabase() {
		Table tDists = new Table();
		for(int distsNum = 0; distsNum < dists.size(); distsNum++) {
			ChipData distance = dists.get(distsNum);
			tDists.setValue(0, distsNum, distance.getDistName());
			int selectingScope = 0;
			int cellCnt = 1;
			for(int cpNum = 0; cpNum < distance.getCPs().size(); cpNum++) {
				ChipData.CP cp = distance.getCPs().get(cpNum);
				if (cp.number == -1) {
					selectingScope++;
				} else if (selectingScope > 0) {
					//add banch of cps in selection mode
					tDists.setValue(cellCnt++, distsNum, String.format("[%d]", selectingScope));
				} else {
					//add single cp
					tDists.setValue(cellCnt++, distsNum, String.valueOf(cp.number));
				}
			}
		}
		CSV.write(tDists, Globals.CSV_DISTS);
	}

	/**
	 * checks if there are no dists
	 * @return true - if no dists, false - if exists
	 */
	public static boolean isThereAreNoDists() {
		Table tDists = CSV.read(Globals.CSV_DISTS);
		if (tDists == null) {
			return true; 
		} else if (tDists.isEmpty()) {
			return true; 
		}
		
		//tDists must have at least one col and one row
		for(int col = 0; col < tDists.cols(); col++) {
			String distName = tDists.getValue(0, col);
			if (distName.isEmpty()) {
				continue;
			}
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * gets distance with given name
	 * @param name
	 * @return null if not found
	 */
	public ChipData getDistByName(String name) {
		for(int i = 0; i < dists.size(); i++) {
			ChipData cd = dists.get(i);
			if (cd.getDistName().equals(name)) {
				return cd;
			}
		}
		return null;
	}

	/**
	 * add backward distance after selected one
	 * @param distance
	 * @return backward distance
	 */
	public ChipData addBackwardDist(ChipData distance, String backwardPostfixStr) {
		int idx = dists.indexOf(distance);
		if (idx == -1) {
			return null;
		}
        String distanceName = distance.getDistName();
        String backwardDistanceName = null;
        if (distanceName.endsWith(backwardPostfixStr)) {
            backwardDistanceName = distanceName.subSequence(0, distanceName.length() - backwardPostfixStr.length()).toString();
        } else {
            backwardDistanceName = distance.getDistName() + backwardPostfixStr;
        }
        ChipData backwardDistance = getDistByName(backwardDistanceName);
        if (backwardDistance != null) {
            return backwardDistance;
        }
		backwardDistance = new ChipData();
		backwardDistance.setDistName(backwardDistanceName);
		for(int i = 0; i < distance.getCPs().size(); i++) {
			ChipData.CP cp = new ChipData.CP();
			cp.number = distance.getCPs().get(i).number;
			backwardDistance.getCPs().add(0, cp);
		}
        ChipData.CP cp = backwardDistance.getCPs().get(0);
        backwardDistance.getCPs().set(0, backwardDistance.getCPs().get(backwardDistance.getCPs().size() - 1));
        backwardDistance.getCPs().set(backwardDistance.getCPs().size() - 1, cp);
		dists.add(idx + 1, backwardDistance);
        return backwardDistance;
	}
	
	/**
	 * predicts distance nearest to given chip data by cps sequence
	 * @param cd
	 * @return 
	 */
	
	public ChipData predictDistance(ChipData cd) {
		if (dists.size() == 0) {
			return null;
		} 
		int bestIdx = 0;
		/*
		int bestCorr = cd.calcCorrespondenceWith(dists.get(0));
		for(int i = 1; i < dists.size(); i++) {
			int curCorr = cd.calcCorrespondenceWith(dists.get(i));
			if (curCorr < bestCorr) {
				bestCorr = curCorr;
				bestIdx = i;
			}
		}*/
		return dists.get(bestIdx);
	}
	
	public ChipData getDistByNumber(int num) {
		return dists.get(num);
	}
	
	public int getCnt() {
		return dists.size();
	}
	
	
}
