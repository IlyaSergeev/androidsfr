package com.densvr.nfcreader;

import java.util.LinkedList;
import java.util.List;

import com.densvr.activities.MainActivity;
import com.densvr.nfcreader.ChipData.ResultsComparison;
import com.densvr.table.csv.CSV;
import com.densvr.table.csv.Table;

public class ResultsProtocol {

	private List<ChipData> results;
	
	
	public ResultsProtocol() {
		results = new LinkedList<ChipData>();
	}
	
	/**
	 * reads results from Globals.CSV_RESULTS
	 * @return can return null
	 */
	public static ResultsProtocol readFromDatabase() {
		ResultsProtocol r = new ResultsProtocol();
		List<ChipData> results = r.results;
		Table tableResults = CSV.read(Globals.CSV_RESULTS);
		if (tableResults == null) {
			return r;
		}
		String curDistName = "";
		int curAttempt = -1;
		for(int row = 0; row < tableResults.rows(); row++) {
			String firstCellVal = tableResults.getValue(row, 0);
			if (firstCellVal.isEmpty()) {
				//do nothing
			} else if (firstCellVal.equals("дистанция")) {
				curDistName = tableResults.getValue(row, 1);
			} else if (firstCellVal.equals("попытка")) {
				curAttempt = Integer.parseInt(tableResults.getValue(row, 1));
			} else {
				ChipData curResult = ChipData.parseCSVResultsLine(tableResults.get(row));
				if (curResult == null) {
					return null;
				}
				curResult.setDistName(curDistName);
				curResult.setAttempt(curAttempt);
				/*if (tableResults.getValue(row, 1).equals("снят")) {
					curResult.setDisqualified(true);
					curResult.setPlace(-1);
				} else {
					curResult.setDisqualified(false);
					curResult.setPlace(Integer.parseInt(tableResults.getValue(row, 1)));
				}*/
				results.add(curResult);
			}
		}
		return r;
	}
	
	/**
	 * overwrite existing CSV_RESULTS file to this results protocol
	 */
	public void writeToDatabase() {
		Table tResults = new Table();
		String distName = "";
		int attempt = -1;
		for(int resultNum = 0; resultNum < results.size(); resultNum++) {
			LinkedList<String> line = null;
			ChipData cd = get(resultNum);
			String curDistName = cd.getDistName();
			int curAttempt = cd.getAttempt();
			if (!curDistName.equals(distName)) {
				//new distance
				tResults.add(new LinkedList<String>());
				line = new LinkedList<String>();
				line.add("дистанция");
				line.add(curDistName);
				tResults.add(line);
				distName = curDistName;
				attempt = -1;
			} 
			if (curAttempt != attempt) {
				//new attempt
				line = new LinkedList<String>();
				line.add("попытка");
				line.add(String.valueOf(curAttempt));
				tResults.add(line);
				attempt = curAttempt;
			}
			line = cd.toResultsProtocolLine();
			tResults.add(line);
		}
		CSV.write(tResults, Globals.CSV_RESULTS);
	}
	
	/**
	 * answers which result is better: if cd1 - true, cd2 - false
	 * @param cd1
	 * @param cd2
	 * @return
	 */
	/*public static boolean compareResults(ChipData cd1, ChipData cd2) {
		if (!cd1.isDisqualified() && cd2.isDisqualified()) {
			return true;
		}
		if (cd1.isDisqualified() && !cd2.isDisqualified()) {
			return false;
		}
		return cd1.getFullTime().val <= cd2.getFullTime().val;
	}*/
	
	/**
	 * inserts chip data to results protocol in correct place (in order by full time)
	 * and updates place and attemptPlace of each user
	 * @param cd
	 */
	public void addChipData(ChipData cd) {
		int resultNum = 0;
		boolean bFoundDist = false;
		boolean bFoundAttempt = false;
		for(; resultNum < results.size(); resultNum++) {
			ChipData curCd = get(resultNum);
			if (curCd.getDistName().equals( cd.getDistName() )) {
				bFoundDist = true;
				if (curCd.getAttempt() == cd.getAttempt()) {
					bFoundAttempt = true;
					boolean bCurResultBetter = curCd.isBetterThen(cd) == ResultsComparison.RESULT_BETTER;
					if (!bCurResultBetter) {
						//insert here
						break;
					} 
				} else {
					if (bFoundAttempt) {
						break;
					}
				}
			} else {
				if (bFoundDist) {
					break;
				}
			}
		}
		cd.setPlace(getTheoreticalPlace(cd));
		cd.setAttemptPlace(getTheoreticalAttemptPlace(cd));
		results.add(resultNum, cd);
		//update other results
		for(int curResultNum = 0; curResultNum < results.size(); curResultNum++) {
			if (curResultNum == resultNum) {
				continue;
			}
			ChipData curCd = get(curResultNum);
			if (!curCd.getDistName().equals( cd.getDistName() )) {
				continue;
			}
			curCd.setPlace(getTheoreticalPlace(curCd));
			curCd.setAttemptPlace(getTheoreticalAttemptPlace(curCd));
			
			if (true) {
				continue;
			}
			
		
			/*
			if (curCd.getDistName().equals( cd.getDistName() )) {
				boolean bCurCdTheoreticalBetter = curCd.isTheoreticalBetterThen(cd) == ResultsComparison.RESULT_BETTER;
				if (bCurCdTheoreticalBetter) {
					//do nothing
				} else {
					curCd.setPlace(curCd.getPlace() + 1);
					if (curCd.getAttempt() == cd.getAttempt()) { 
						curCd.setAttemptPlace(curCd.getAttemptPlace() + 1);
					}
				}
				
			}*/
			switch(curCd.isTheoreticalBetterThen(cd)) {
			case RESULT_BETTER:
				break;
			case RESULT_EQUAL:
				/*if (curResultNum > resultNum) {
					curCd.setPlace(curCd.getPlace() + 1);
					if (curCd.getAttempt() == cd.getAttempt()) { 
						curCd.setAttemptPlace(curCd.getAttemptPlace() + 1);
					}
				} else {
					cd.setPlace(cd.getPlace() + 1);
					if (curCd.getAttempt() == cd.getAttempt()) { 
						cd.setAttemptPlace(cd.getAttemptPlace() + 1);
					}
				//}
				break;*/
			case RESULT_WORSE:
				curCd.setPlace(curCd.getPlace() + 1);
				if (curCd.getAttempt() == cd.getAttempt()) { 
					curCd.setAttemptPlace(curCd.getAttemptPlace() + 1);
				}
			}
		}
	}
	
	/**
	 * deletes last attempt and inserts this chip data instead it
	 * updates all places of current distance
	 * @param cd
	 */
	public void overwriteChipData(ChipData cd) {
		
		int attempt = cd.getAttempt() - 1;
		if (attempt == 0) {
			addChipData(cd);
			return;
		}
		
		//find last attempt
		String distName = cd.getDistName();
		String userName = cd.getUserName();
		cd.setAttempt(attempt);
		
		int resultNum = 0;
		for(;resultNum < results.size(); resultNum++) {
			ChipData curCd = results.get(resultNum);
			if (curCd.getDistName().equals(distName)) {
				if (curCd.getAttempt() == attempt) {
					if (curCd.getUserName().equals(userName)) {
						results.set(resultNum, cd);
						break;
					}
				}
			}
		}
		
		if (resultNum < results.size()) {
			//update other results
			updateDistResults(distName);
		} else {
			//attempt wasn't found - add new attempt 
			while(true) {
				MainActivity.makeText("fail");
			}
			///addChipData(cd);
		}
	}
	
	
	
	
	
	/**
	 * deletes all user attempts and updates others
	 * @param cd
	 */
	//TODO
	/*
	public void deleteAllUserAttempts(ChipData cd) {
		String userName = cd.getUserName();
		String distName = cd.getDistName();
		for(int i = 0; i < )
	}*/
	
	
//=============
//private
//=============
	
	/**
	 * return start and end indexes of given distance
	 * @param distName
	 * @return [0] - startIdx, [1] - endIdx + 1
	 */
	private int[] getDistBorderNums(String distName) {
		int startNum = -1, endNum = -1;
		int resultNum = 0;
		for(; resultNum < results.size(); resultNum++) {
			ChipData curCd = results.get(resultNum);
			if (curCd.getDistName().equals(distName)) {
				break;
			}
		}
		startNum = resultNum;
		for(; resultNum < results.size(); resultNum++) {
			ChipData curCd = results.get(resultNum);
			if (!curCd.getDistName().equals(distName)) {
				break;
			}
		}
		endNum = resultNum;
		return new int[]{startNum, endNum};
	}
	
	/**
	 * updates places, attempts places and positions of all results of given distance
	 * @param distName
	 */
	private void updateDistResults(String distName) {
		ResultsProtocol rp = new ResultsProtocol();
		int[] distBorderNums = getDistBorderNums(distName);
		int startNum = distBorderNums[0];
		int endNum = distBorderNums[1];
		for(int resultNum = startNum; resultNum < endNum; resultNum++) {
			rp.addChipData(results.get(startNum));
			results.remove(startNum);
		}
		for(int resultNum = startNum; resultNum < endNum; resultNum++) {
			results.add(resultNum, rp.results.get(resultNum - startNum));
		}
	}
	
	
	
	
//=============================
//getters (selects)
//=============================
	
	/**
	 * gets result, specified by number
	 * @param row
	 * @return
	 */
	public ChipData get(int num) {
		return results.get(num);
	}
	
	
	/**
	 * return theoretical place in protocol (first - 1, second - 2, ...)
	 * @param chipData
	 * @param bDisqualified
	 * @return
	 */
	public int getTheoreticalPlace(ChipData chipData) {
		int cnt = 0;
		int resultsWorseCnt = 0;
		for(int i = 0; i < results.size(); i++) {
			ChipData curResult = results.get(i);
			if (curResult.getDistName().equals(chipData.getDistName())) {
				cnt++;
				if (chipData.isTheoreticalBetterThen(curResult) != ResultsComparison.RESULT_WORSE) {
					resultsWorseCnt++;
				}
			}
		}
		return cnt - resultsWorseCnt + 1;
	}
	
	/**
	 * return theoretical place in current attempt (first - 1, second - 2, ...)
	 * @param chipData
	 * @param results
	 * @return
	 */
	public int getTheoreticalAttemptPlace(ChipData chipData) {
		int cnt = 0;
		int resultsWorseCnt = 0;
		for(int i = 0; i < results.size(); i++) {
			ChipData curResult = results.get(i);
			if (curResult.getDistName().equals(chipData.getDistName()) && curResult.getAttempt() == chipData.getAttempt()) {
				cnt++;
				if (chipData.isTheoreticalBetterThen(curResult) != ResultsComparison.RESULT_WORSE) {
					resultsWorseCnt++;
				}
			}
		}
		return cnt - resultsWorseCnt + 1;
	} 
	
	/**
	 * get number of specified user attempts (1 - first, 2 - second...)
	 * @param name
	 * @return
	 */
	public int getAttempt(String userName, String distName) {
		/*Table tableResults = CSV.read(Globals.CSV_RESULTS);
		if (tableResults == null) {
			return 1;
		}*/
		int attempt = 1;
		for(int i = 0; i < results.size(); i++) {
			ChipData curResult = results.get(i);
			if (curResult.getDistName().equals(distName)) {
				if (curResult.getUserName().equals(userName) && curResult.getDistName().equals(distName)) {
					attempt++;
				}
			}
		}
		return attempt;
	}
	
	
	
	
	
	
	

	public String toString() {
		String s = "results";
		for(ChipData r : results) {
			s += "\n" + r.toString();
		}
		return s;
	}
	
	
	
	
}
