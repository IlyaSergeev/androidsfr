package com.densvr.nfcreader;

import android.util.Log;

import com.densvr.activities.MainActivity;
import com.densvr.activities.TableNamesActivity;
import com.densvr.table.csv.CSV;
import com.densvr.table.csv.Table;

import java.util.LinkedList;
import java.util.List;

import static com.densvr.nfcreader.StringToTimeExtentionsKt.createDelayMillis;
import static com.densvr.nfcreader.StringToTimeExtentionsKt.tryParseDelayMillisOrZero;

@Deprecated
public class OldChipData {


    private static final String TAG = "ChipData";

    public static class CP {
        //if number == -1 then CP works in selection mode (each CP is equal to the CP)
        public int number;
        public Long lapTime;
        public Long splitTime;

        public CP(int number, Long lapTime, Long splitTime) {
            this.number = number;
            this.lapTime = lapTime;
            this.splitTime = splitTime;
        }

        public CP() {
            number = -1;
            splitTime = 0L;
            lapTime = 0L;
        }

        public String toString() {
            return String.valueOf(number) + " " + lapTime.toString() + " " + splitTime.toString();
        }
    }


    private List<CP> cps;
    private String distName;
    private int userId; //chip number
    private String userName;
    private int place; //1 - first, 2 - second...
    private boolean bDisqualified;
    private boolean bReestablished; //if true, no matter disqualified or not - consider that not
    private int attempt;
    private int attemptPlace;
    private Long fullTime;


    private static byte[] getCell(byte[] data, int num) {
        return new byte[]{data[4 * num], data[4 * num + 1], data[4 * num + 2], data[4 * num + 3]};
    }

    /**
     * parse user chip number (must be unique)
     *
     * @param data
     *
     * @return
     */
    private static int parseUserId(byte[] data) {
        byte[] cell = getCell(data, 4);
        int l = cell[0] & 0xFF;
        int m = cell[1] & 0xFF;
        //int h = cell[2] & 0xFF;
        return l + m * 200; //+ h * 40000;
    }

    private static int parseCPCnt(byte[] data) {
        return getCell(data, 5)[0] & 0xFF;
    }

    private static int parseCPNumber(byte[] data, int num) {
        return getCell(data, num + 7)[0] & 0xFF;
    }

    private static Long parseCPTime(byte[] data, int num) {
        byte[] cell = getCell(data, num + 6);
        int h = Integer.parseInt(Integer.toHexString(cell[3]), 10);
        int m = Integer.parseInt(Integer.toHexString(cell[2]), 10);
        int s = Integer.parseInt(Integer.toHexString(cell[1]), 10);
        return createDelayMillis(h, m, s);
    }


//====================================
//public methods
//====================================


    public OldChipData() {
        cps = new LinkedList<CP>();
        distName = new String();
        userName = new String();
        bReestablished = false;
        place = -1;
        attemptPlace = -1;
        attempt = -1;
        fullTime = 0L;
    }

    /**
     * parse array data
     *
     * @param bytes
     *
     * @return
     */
    public static OldChipData parseChipArray(byte[] bytes) {
        OldChipData chipData = new OldChipData();
        chipData.userId = parseUserId(bytes);
        Table tNames = CSV.read(OldGlobals.CSV_NAMES);
        chipData.userName = TableNamesActivity.getUserById(tNames, chipData.getUserId());
        if (chipData.userName == null) {
            chipData.userName = String.valueOf(chipData.getUserId());
        }
        int cpCnt = parseCPCnt(bytes);
        Long startTime = parseCPTime(bytes, 0);
        Log.i("android SFR", startTime.toString());
        for (int i = 0; i <= cpCnt - 6; i++) {
            CP cp = new CP();
            cp.number = parseCPNumber(bytes, i);
            cp.lapTime = parseCPTime(bytes, i);
            cp.lapTime = cp.lapTime - startTime;

            if (i == 0) {
                cp.splitTime = 0L;
            } else {
                Long prevLapTime = chipData.cps.get(i - 1).lapTime;
                cp.splitTime = cp.lapTime - prevLapTime;
            }
            Log.i("android SFR", String.format("%d) ", i) + String.valueOf(cp.number) + " " +
                cp.lapTime.toString() + " " + cp.splitTime.toString());
            chipData.cps.add(cp);
        }
        if (chipData.cps.size() > 0) {
            chipData.fullTime = chipData.cps.get(cpCnt - 6).lapTime;
        }
        Log.i("android SFR", chipData.fullTime.toString());
        return chipData;
    }

    public static OldChipData fillFrom(SfrRecord sfrRecord) {
        OldChipData chipData = new OldChipData();
        chipData.userId = sfrRecord.getPersonNumber();
        Table tNames = CSV.read(OldGlobals.CSV_NAMES);
        chipData.userName = TableNamesActivity.getUserById(tNames, chipData.getUserId());
        if (chipData.userName == null) {
            chipData.userName = String.valueOf(chipData.getUserId());
        }
        List<SFRPointInfo> points = sfrRecord.getPoints();
        int cpCnt = points.size();
        Long startTime = points.get(0).getTime();
        Log.i("android SFR", startTime.toString());
        for(SFRPointInfo point :points) {
            CP cp = new CP();
            cp.number = point.getPointId();
            cp.lapTime = point.getTime()- startTime;

            if (points.get(0) == point) {
                cp.splitTime = 0L;
            } else {
                Long prevLapTime = chipData.cps.get(chipData.cps.size() - 1).lapTime;
                cp.splitTime = cp.lapTime - prevLapTime;
            }
            chipData.cps.add(cp);
        }
        if (chipData.cps.size() > 0) {
            chipData.fullTime = chipData.cps.get(cpCnt - 6).lapTime;
        }
        Log.i("android SFR", chipData.fullTime.toString());
        return chipData;
    }

    /**
     * initialize chip data from CSV_RESULTS (only cps and place)
     *
     * @param line
     *
     * @return can return null
     */
    public static OldChipData parseCSVResultsLine(List<String> line) {
        OldChipData chipData = new OldChipData();
        int iter = 0;
        chipData.userName = line.get(iter++);
        try {
            //place and attempt place
            String sPlace = line.get(iter++);
            String sAttemptPlace = line.get(iter++);
            chipData.setPlaceStr(sPlace);
            chipData.setAttemptPlaceStr(sAttemptPlace);
            /*
			if (sPlace.startsWith("снят")) {
				chipData.place = -1;
				chipData.attemptPlace = -1;
				chipData.bDisqualified = true;
			} else {
				chipData.place = Integer.parseInt(sPlace);
				chipData.attemptPlace = Integer.parseInt(sAttemptPlace);
				chipData.bDisqualified = false;
			}*/

            //time
            chipData.fullTime = tryParseDelayMillisOrZero(line.get(iter++));
            //cps
            int cnt = line.size() - iter;
            if (cnt % 2 != 0) {
                return null;
            }
            cnt /= 2;
            chipData.cps = new LinkedList<CP>();
            for (int i = 0; i < cnt; i++) {
                String sNumber = line.get(iter++);
                String sSplitTime = line.get(iter++);
                if (sNumber.isEmpty() || sSplitTime.isEmpty()) {
                    break;
                }
                CP cp = new CP();
                cp.number = Integer.parseInt(sNumber);
                cp.splitTime = tryParseDelayMillisOrZero(sSplitTime);
                if (i == 0) {
                    cp.lapTime = 0L;
                } else {
                    Long prevLapTime = chipData.cps.get(i - 1).lapTime;
                    cp.lapTime = cp.splitTime + prevLapTime;
                }
                chipData.cps.add(cp);
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return null;
        }
        return chipData;
    }

    /**
     * initialize chip data from CSV_DISTS (only cps numbers)
     *
     * @param line
     *
     * @return
     */
    public static OldChipData parseDistsResultsLine(List<String> line) {
        OldChipData chipData = new OldChipData();
        for (int i = 0; i < line.size(); i++) {
            String s = line.get(i);
            try {
                if (s.startsWith("[") && s.endsWith("]")) {
                    s = s.substring(1, s.length() - 1);
                    int cnt = Integer.parseInt(s);
                    for (int ii = 0; ii < cnt; ii++) {
                        CP cp = new CP();
                        cp.number = -1;
                        chipData.getCPs().add(cp);
                    }
                } else {
                    CP cp = new CP();
                    cp.number = Integer.parseInt(line.get(i));
                    chipData.getCPs().add(cp);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "exception", e);
                return null;
            }
        }
        return chipData;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < cps.size(); i++) {
            s += cps.get(i).toString() + "\n";
        }
        return s;
    }

//==========================
//getters and setters
//==========================

    public void setDistName(String distName) {
        this.distName = distName;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public void setDisqualified(boolean bDisqualified) {
        this.bDisqualified = bDisqualified;
    }

    public List<CP> getCPs() {
        return cps;
    }

    public String getDistName() {
        return distName;
    }

    public String getUserName() {
        return userName;
    }

    /**
     * returns sername, name in this order
     *
     * @return
     */
    public String[] getSernameAndName() {
        String[] userNameArr = userName.split(" ");
        String sername = userNameArr[0];
        String name = "";
        for (int i = 1; i < userNameArr.length; i++) {
            name += userNameArr[i];
        }
        return new String[]{
            sername, name
        };
    }

    public int getPlace() {
        return place;
    }

    public boolean isDisqualified() {
        return bDisqualified;
    }

    public void setFullTime(Long time) {
        this.fullTime = time;
    }

    public Long getFullTime() {
        return fullTime;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public void setAttemptPlace(int attemptPlace) {
        this.attemptPlace = attemptPlace;
    }

    public int getAttemptPlace() {
        return attemptPlace;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isReestablished() {
        return bReestablished;
    }

    public void setReestablished(boolean bReestablished) {
        this.bReestablished = bReestablished;
    }

    /**
     * checks if disqualified and not reestablished
     *
     * @return
     */
    public boolean isFinallyDisqualified() {
        return bDisqualified && !bReestablished;
    }


    /**
     * if disqualified get place with format: снят (результат)
     *
     * @return
     */
    public String getPlaceStr() {
        String sPlace = "";
        if (isReestablished()) {
            sPlace = String.format("вос. (%d)", getPlace());
        } else if (!isDisqualified()) {
            sPlace = String.valueOf(getPlace());
        } else {
            sPlace = String.format("снят (%d)", getPlace());
        }
        return sPlace;
    }

    /**
     * if disqualified get place in current attempt with format: снят (результат)
     *
     * @return
     */
    public String getAttemptPlaceStr() {
        String sAttemptPlace = "";
        if (isReestablished()) {
            sAttemptPlace = String.format("вос. (%d)", getAttemptPlace());
        } else if (!isDisqualified()) {
            sAttemptPlace = String.format("%d", getAttemptPlace());
        } else {
            sAttemptPlace = String.format("снят (%d)", getAttemptPlace());
        }
        return sAttemptPlace;
    }

    /**
     * inits place and bDisqualified, parsing this str
     *
     * @param place
     */
    public void setPlaceStr(String place) {
        String sPlace = null;
        if (place.startsWith("снят")) {
            bDisqualified = true;
            //TODO change
            sPlace = place.substring(6, place.length() - 1);
        } else if (place.startsWith("вос.")) {
            bReestablished = true;
            sPlace = place.substring(6, place.length() - 1);
        } else {
            bDisqualified = false;
            sPlace = place;
        }
        this.place = Integer.parseInt(sPlace);
    }

    /**
     * inits attempt place and bDisqualified, parsing this str
     *
     * @param attemptPlace
     */
    public void setAttemptPlaceStr(String attemptPlace) {
        String sAttemptPlace = null;
        if (attemptPlace.startsWith("снят")) {
            bDisqualified = true;
            //TODO change
            sAttemptPlace = attemptPlace.substring(6, attemptPlace.length() - 1);
        } else if (attemptPlace.startsWith("вос.")) {
            bReestablished = true;
            sAttemptPlace = attemptPlace.substring(6, attemptPlace.length() - 1);
        } else {
            bDisqualified = false;
            sAttemptPlace = attemptPlace;
        }
        this.attemptPlace = Integer.parseInt(sAttemptPlace);
    }


    /**
     * converts chip data to results protocol line
     *
     * @return
     */
    public LinkedList<String> toResultsProtocolLine() {
        LinkedList<String> line = new LinkedList<String>();
        line.add(userName); //name
        line.add(getPlaceStr()); //place
        line.add(getAttemptPlaceStr());//place in attempt
        line.add(fullTime.toString()); //time
        for (int i = 0; i < cps.size(); i++) {
            CP cp = cps.get(i);
            line.add(String.valueOf(cp.number)); //number
            line.add(cp.splitTime.toString()); //split time
        }
        return line;
    }


    public enum ResultsComparison {
        RESULT_BETTER,
        RESULT_WORSE,
        RESULT_EQUAL
    }

    ;

    /**
     * real results comparison
     *
     * @param cd
     *
     * @return
     */
    public ResultsComparison isBetterThen(OldChipData cd) {
        if (!this.isFinallyDisqualified() && cd.isFinallyDisqualified()) {
            return ResultsComparison.RESULT_BETTER;
        }
        if (this.isFinallyDisqualified() && !cd.isFinallyDisqualified()) {
            return ResultsComparison.RESULT_WORSE;
        }
        long time = this.getFullTime();
        long cdTime = cd.getFullTime();
        if (time < cdTime) {
            return ResultsComparison.RESULT_BETTER;
        } else if (time > cdTime) {
            return ResultsComparison.RESULT_WORSE;
        }
        return ResultsComparison.RESULT_EQUAL;
    }

    /**
     * theoretical results comparison (if not disqualified)
     *
     * @param cd
     *
     * @return
     */
    public ResultsComparison isTheoreticalBetterThen(OldChipData cd) {
        if (cd.isFinallyDisqualified()) {
            return ResultsComparison.RESULT_BETTER;
        }
        long time = this.getFullTime();
        long cdTime = cd.getFullTime();
        if (time < cdTime) {
            return ResultsComparison.RESULT_BETTER;
        } else if (time > cdTime) {
            return ResultsComparison.RESULT_WORSE;
        }
        return ResultsComparison.RESULT_EQUAL;
    }


//=================	
//dist prediction
//=================

    /**
     * calculates dynamic time warping distance between s and t vectors
     * @param s [1..n]
     * @param t [1..m]
     * @return
     */
	/*
	private static int dtwDist(int[] s, int[] t) {
		int n = s.length - 1; 
		int m = t.length - 1;
		int[][] dtw = new int[n + 1][m + 1];
		for(int i = 1; i <= n; i++) {
			dtw[i][0] = Integer.MAX_VALUE;
		}
		for(int i = 1; i <= m; i++) {
			dtw[0][i] = Integer.MAX_VALUE;
		}
		dtw[0][0] = 0;
		for(int i = 1; i < n; i++) {
			for(int j = 1; j < m; j++) {
				int cost = s[i] == t[j] ? 1 : 0;
				dtw[i][j] = cost + Math.min( Math.min(dtw[i - 1][j], dtw[i][j - 1]), dtw[i - 1][j - 1]);
			}
		}
		return dtw[n][m];
	}
	*/

    /**
     * calculates correspondence with given distance using DTW
     * @param dist
     * @return
     */
	/*
	public int calcCorrespondenceWith(ChipData dist) {
		List<CP> userCpsList = new LinkedList<CP>();
		//delete all excessed cps
		for(int distCPIter = 0, chipCPIter = 0; chipCPIter < this.cps.size(); chipCPIter++) {
			CP chipCP = this.cps.get(chipCPIter);
			if (distCPIter < dist.getCPs().size()) {
				CP distCP = dist.getCPs().get(distCPIter);
				if (distCP.number == chipCP.number) {
					userCpsList.add(distCP);
					distCPIter++;
				} else {
				}
			} else {
				//all cps were checked 
				break;
			}
		}
		userCpsList = this.cps;
		int[] userCps = new int[userCpsList.size() + 1];
		int[] distCps = new int[dist.cps.size() + 1];
		for(int i = 0; i < userCpsList.size(); i++) {
			userCps[i + 1] = userCpsList.get(i).number;
		}
		for(int i = 0; i < dist.cps.size(); i++) {
			distCps[i + 1] = dist.cps.get(i).number;
		}
		return dtwDist(userCps, distCps);
	}*/


//==================	
//debug only
//==================	


    /**
     * gets true with specified probability
     *
     * @param probability
     *     from 0 to 1
     *
     * @return
     */
    public static boolean randBool(float probability) {
        return Math.random() < probability;
    }

    /**
     * gets int form 0 to end
     *
     * @return
     */
    public static int randInt(int cnt) {
        if (cnt <= 0) {
            return 0;
        }
        double d = Math.random();
        d *= cnt;
        return (int) d % cnt;
    }

    public static OldChipData genChipDataForImitation() {
        return genChipDataForImitationRandom();
        //return presetChipData();
    }


    private static OldChipData genChipDataForImitationRandom() {

        //random dist
        OldDistsProtocol dists = OldDistsProtocol.readFromDatabase();
        OldChipData chipData = dists.getDistByNumber(randInt(dists.getCnt()));
        //ChipData chipData = dists.getDistByNumber(0);

        MainActivity.makeText("дистанция " + chipData.getDistName());

        chipData.setDistName("");

        //generate random user
        chipData.userId = randInt(10);
        Table tNames = CSV.read(OldGlobals.CSV_NAMES);
        chipData.userName = TableNamesActivity.getUserById(tNames, chipData.userId);
        //chipData.userName = "Иванов Петя";

        //generate random full time
        if (randBool(0.8f)) {
            chipData.fullTime = tryParseDelayMillisOrZero("30:00");
            chipData.fullTime = chipData.fullTime + randInt(10 * 60);
        } else {
            chipData.fullTime = tryParseDelayMillisOrZero("33:14");
        }

        for (int i = 0; i < chipData.cps.size(); i++) {
            chipData.cps.get(i).splitTime = i == 0 ? 0L : randInt(2 * 60);
            chipData.cps.get(i).lapTime = i == 0 ? 0L : chipData.cps.get(i - 1).lapTime
                + chipData.cps.get(i).splitTime;
        }

        boolean bContinue = true;
        while (bContinue) {
            bContinue = false;
            int errorType = randInt(6);
            int cpNum = randInt(chipData.cps.size());
            switch (errorType) {
                case 0: //edit one cp
                    CP cp = chipData.cps.get(cpNum);
                    cp.number = 31 + randInt(100);
                    bContinue = true;
                    break;
                case 1: //add one cp
                    CP cpNew = new CP();
                    cpNew.number = 31 + randInt(100);
                    chipData.cps.add(cpNum, cpNew);
                    bContinue = true;
                    break;
                case 2: //delete one cp
                    if (chipData.cps.size() > 1) {
                        chipData.cps.remove(cpNum);
                        bContinue = true;
                    }
                    break;
                default:
                    bContinue = false;
                    break;
            }
        }
		
		/*
		chipData.cps.clear();
        chipData.cps.add(new CP(1));
        chipData.cps.add(new CP(31));
        chipData.cps.add(new CP(2));
        chipData.cps.add(new CP(3));
        chipData.cps.add(new CP(32));
        chipData.cps.add(new CP(55));
        chipData.cps.add(new CP(100));

        for(int i = 0; i < chipData.cps.size(); i++) {
            chipData.cps.get(i).splitTime = tryParseDelayMillisOrZero("1:00");
            chipData.cps.get(i).lapTime = tryParseDelayMillisOrZero("1:00");
        }
        */


        return chipData;
    }

    private static OldChipData presetChipData() {


        OldChipData chipData = new OldChipData();
        chipData.cps.add(new CP(35, tryParseDelayMillisOrZero("0:00"), tryParseDelayMillisOrZero("0:00")));
        chipData.cps.add(new CP(38, tryParseDelayMillisOrZero("0:12"), tryParseDelayMillisOrZero("0:12")));
        chipData.cps.add(new CP(43, tryParseDelayMillisOrZero("0:17"), tryParseDelayMillisOrZero("0:05")));
        chipData.cps.add(new CP(46, tryParseDelayMillisOrZero("0:24"), tryParseDelayMillisOrZero("0:07")));
        chipData.cps.add(new CP(34, tryParseDelayMillisOrZero("0:40"), tryParseDelayMillisOrZero("0:16")));
        chipData.cps.add(new CP(48, tryParseDelayMillisOrZero("0:45"), tryParseDelayMillisOrZero("0:05")));
        chipData.fullTime = null;

        chipData.setDistName("");

        //generate random user
        chipData.userId = randInt(10);
        Table tNames = CSV.read(OldGlobals.CSV_NAMES);
        chipData.userName = TableNamesActivity.getUserById(tNames, chipData.userId);

        return chipData;
		/*for(int i = 0; i < chipData.cps.size(); i++) {
			chipData.cps.get(i).splitTime = tryParseDelayMillisOrZero("1:00");
			chipData.cps.get(i).lapTime = tryParseDelayMillisOrZero("1:00");
		}*/
    }


}
