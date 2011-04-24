package rl;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import map.Map;

public class RLPolicy {

    //double[][] qValuesStateAction;
    ArrayList coords;
    ArrayList qValuesStateAction; // berisi qValuesStateAction dari state-action
    ArrayList<short[]> stateTable; // berisi states
    ArrayList stateDim; // berisi jumlah state dalam tiap2 level state
    ArrayList stateToQval; // pemetaan dari indeks state ke indeks qValuesStateAction
    ArrayList qValues; // sebagai penampungan qValues yg didapat dari method myQValues
    int states, actions;
    int[] dimsize = new int[3];
    MineWorld minefield;
    int rows;
    int columns;
    int mines;
    private short[] themap;
    public TreeSet<String> minetree;

    RLPolicy(RLWorld mino) {
        //stateTable = Array.newInstance(double.class, dimsize);// 3 aksi:
        // probe,mark,unmark

        stateTable = new ArrayList(0);

        this.minefield = (MineWorld) mino;
        this.themap = minefield.getTrainingMap();

        this.rows = minefield.rows;
        this.columns = minefield.columns;
        this.mines = minefield.mines;

        minetree = new TreeSet();

        coords = new ArrayList();
        int idx = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int[] coord = {j, i};
                coords.add(coord);
            }
        }
//        for (int i = rows-1; i >= 0; i--) {
//            for (int j = 0; j < columns; j++) {
//                int[] coord = {j, i};
//                coords.add(coord);
//            }
//        }
        printCoords();
        qValuesStateAction = new ArrayList();
        actions = coords.size();

        states = 1; // state awal: covered all
//        stateDim = new ArrayList();
//        stateDim.add(0, 1);
        int area = rows * columns;
        for (int j = 1; j <= area; j++) {
//            int hoo = factorial(area)
//                    / (factorial(j) * factorial(area - j));
            int hoo = getStateNum(area, j);
            System.out.println("Hoo_" + j + "=" + hoo);
//            stateDim.add(j, hoo);
            states += hoo;
        }
        stateToQval = new ArrayList();
//        System.out.print("\nStateDim: ");
//        for (int k = 0; k < stateDim.size(); k++) {
//            System.out.print("lev" + k + "=" + stateDim.get(k) + " ");
//        }
//        System.out.println();
        //System.out.println("\nStates num=" + states);
    }

    public void printCoords() {
        System.out.println("Print coords:");
        for (Iterator it = coords.iterator(); it.hasNext();) {
            int[] hoo = (int[]) it.next();
            System.out.print("[" + hoo[0] + "," + hoo[1] + "]");
        }
        System.out.println();
    }

    public void printStates() {
        System.out.println("Print States:");
        for (int i = 0; i < stateTable.size(); i++) {
            System.out.print("state-" + i + ": ");
            short[] hoo = (short[]) stateTable.get(i);
            for (int j = 0; j < hoo.length; j++) {
                System.out.print("[" + hoo[j] + "] ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printStateToQval() {
        System.out.println("Print State to Qval:");
        for (int i = 0; i < stateToQval.size(); i++) {
            System.out.print("state " + i + " : ");
            System.out.println("qVal idxbegin " + (Integer) stateToQval.get(i));
        }
        System.out.println();
    }

    public void printAllQvals() {
        System.out.println("Print All Qvals:");
//        for (int i = 0; i < qValuesStateAction.size(); i++) {
//            System.out.print("state idx " + i + " : ");
//            System.out.println("qVal = " + (Double) qValuesStateAction.get(i));
//        }
        for (int i = 0; i < stateTable.size(); i++) {
            short[] sta = stateTable.get(i);
            System.out.print("state: " + i + " ");
            for (int j = 0; j < sta.length; j++) {
                System.out.print(sta[j]);
            }
            System.out.print(": ");
            ArrayList hoo = myQValues(sta);
            for (int k = 0; k < hoo.size(); k++) {
                //if (hoo.size() != 0) {
                //int[] cord = indexUnprobedToCoord(k, sta);
                //System.out.print("[" + cord[0] + "," + cord[1] + "]=");
                System.out.print(hoo.get(k) + " ");
                //}
            }
            System.out.println();
        }
        System.out.println();
    }

    public void writeAllQvals() {
        System.out.println("Write All Qvals begin...");

        String trainingmap = "";
        short temp;
        for (int i = 0; i < minefield.trainingMap.length; i++) {
            temp = minefield.trainingMap[i];
            if (temp == -1) {
                trainingmap += "X";
            } else {
                trainingmap += minefield.trainingMap[i];
            }
        }

        try {
            String tablename = "minemap_"
                    + rows + "x"
                    + columns + "_"
                    + mines + "_" + trainingmap;
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/C:/sqlite/test.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("drop table if exists " + tablename + ";");
            stat.executeUpdate("create table " + tablename
                    + " (config text primary key,"
                    + " qvals text);");
            PreparedStatement prep = conn.prepareStatement("replace into "
                    + tablename + " values(?,?);");

            // Bangkitkan map sebanyak beberapa kali, entahlah berapa pastinya
            String strstat = "";
            String strqvals = "";
            for (int i = 0; i < stateTable.size(); i++) {
                short[] sta = stateTable.get(i);
                //System.out.print("state: " + i + " ");
                strstat = "";
                short tempo;
                for (int j = 0; j < sta.length; j++) {
                    //System.out.print(sta[j]);
                    temp = sta[j];
                    if (temp == Map.UNPROBED) {
                        strstat += "?";
                    } else if (temp == Map.BOOM) {
                        strstat += "X";
                    } else if (temp == Map.MARKED) {
                        strstat += ">";
                    } else if (temp == Map.OUT_OF_BOUNDS) {
                        strstat += ".";
                    } else {
                        strstat += sta[j];
                    }

                }

                strqvals = "";
                int[] cord;
                ArrayList hoo = myQValues(sta);
                for (int k = 0; k < hoo.size(); k++) {
                    if (hoo.size() != 0) {
                        cord = indexUnprobedToCoord(k, sta);
                        strqvals += "[" + cord[0] + "," + cord[1] + "]=";
                        //System.out.print("[" + cord[0] + "," + cord[1] + "]=");
                        strqvals += hoo.get(k) + ";";
                        //System.out.print(hoo.get(k) + " ");
                    }
                }
                prep.setString(1, strstat);
                prep.setString(2, strqvals);
                prep.addBatch();
                ////System.out.println();
            }

//            for (int i = 0; i < 9999; i++) {
//                //generateMap();
//                prep.setString(1, formatMinemap());
//                prep.addBatch();
//            }
            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);

            // Sekedar menampilkan ke layar console
            ResultSet rs = stat.executeQuery("select * from " + tablename + ";");
            int idx = 0;
            while (rs.next()) {
                // System.out.println("map = "+rs.getString("config"));
                System.out.println("state " + (idx++));
                for (int y = 0; y < rows; y++) {
                    System.out.println(rs.getString("config").substring(
                            columns * y, columns * (y + 1)));
                }
                System.out.print("qvals: ");
                System.out.println(rs.getString("qvals"));
                System.out.println("------");
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Write All Successul.");
    }

    public void initValues(double initValue) {
        int i;
        int actualdim = 0;
        short[] state = new short[rows * columns + 1];

        for (int z = 0; z < state.length - 1; z++) {
            state[z] = -2; // all unprobed
        }
        state[state.length - 1] = (short) mines;

        int needact = 0;
        stateTable.clear();
        stateToQval.add(0, 0);

        System.out.print("state-now: ");

        for (int k = 0; k < state.length; k++) {
            System.out.print("[" + state[k] + "] ");
        }
        System.out.println();

        //stateTable.add(state);
        for (int j = 0; j < states; j++) {
            short[] tobeinserted = new short[state.length];
            System.arraycopy(state, 0, tobeinserted, 0, state.length);
            stateTable.add(tobeinserted);
//            System.out.print("state-" + j + ": ");
//            short[] hoo = (short[]) stateTable.get(j);
//            for (int k = 0; k < hoo.length; k++) {
//                System.out.print("[" + hoo[k] + "] ");
//            }
//            System.out.println();
            stateToQval.add(j + 1, (Integer) stateToQval.get(j) + this.getUnprobeds(state).size());
            //System.out.print("state" + j + "=");
            //qValuesStateAction = (double[]) myQValues(state);
            needact = this.getUnprobeds(state).size();
            for (i = 0; i < needact; i++) {
                //qValuesStateAction.add(initValue++); // MESTI DIKEMBALIKAN SEPERTI SEMULA: tanpa inkremen!
                if (loseState(state)) {
                    qValuesStateAction.add(-(double) minefield.minePenalty);
                } else {
                    qValuesStateAction.add(initValue); //tanpa inkremen!
                }
                //System.out.print("[" + qValuesStateAction.get(i) + "]");
            }
            //System.out.println();
            state = getNextState(state);
        }
        //printStates();
//        for (int j = 0; j < stateToQval.size(); j++) {
//            System.out.println("state " + j + "=idxBegin[" + stateToQval.get(j) + "]");
//        }
        ///printAllQvals();
        System.out.println("States: " + states);
        System.out.println("qValsOfStateAction: " + qValuesStateAction.size());
    }

    /*
     * mengkonversi koordinat pada petak menjadi indeks pada array representasi state.
     */
    public int coordToIndex(int[] coord) {
        return (columns * coord[1] + coord[0]);
    }

    public int[] indexUnprobedToCoord(int upcoord, short[] state) {
        int[] hoo = (int[]) coords.get((Integer) this.getUnprobeds(state).get(upcoord));
//        System.out.print("indexUnprobedToCoord: [");
//        for (int i = 0; i < hoo.length; i++) {
//            System.out.print(hoo[i]);
//            if (i == 0) {
//                System.out.print(",");
//            }
//        }
//        System.out.println("]");

        return hoo;
    }

    public int[] indexToCoord(int coord) {
        if (coord == -1) {
            System.err.println("coord -1 detected.");
            return null;
        }
        int[] hoo = (int[]) coords.get(coord);
        System.out.print("indexToCoord: [");
        for (int i = 0; i < hoo.length; i++) {
            System.out.print(hoo[i]);
            if (i == 0) {
                System.out.print(",");
            }
        }
        System.out.println("]");

        return hoo;
    }

    public int factorial(int num) {
        if (num <= 1 && num >= 0) {
            return 1;
        } else {
            return (num * factorial(num - 1));
        }
    }

    public BigInteger bigFactorial(int num) {
        BigInteger n = BigInteger.ONE;
        for (int i = 1; i <= num; i++) {
            n = n.multiply(BigInteger.valueOf(i));
        }
        return n;
    }

    public int getStateNum(int area, int n) {
        BigInteger pemb = BigInteger.ONE;
        pemb = bigFactorial(area);
        BigInteger peny = BigInteger.ONE;
        peny = bigFactorial(n).multiply(bigFactorial(area - n));
        BigInteger hasil = pemb.divide(peny);
        return hasil.intValue();
    }

    public short[] getNextState(short[] state) {

        ArrayList probeds;
        int numprobed = 0;
        int lastidx = state.length - 2;
        probeds = new ArrayList(0);
//        System.out.println("StateLength:" + state.length);
        for (int i = 0; i < state.length - 1; i++) {
            if (state[i] != -2) {
                probeds.add(i);
                //System.out.print("i=" + i + " ");
                numprobed++;
            }
        }

//        System.out.print("probeds: ");
//        for (int h = 0; h < probeds.size(); h++) {
//            System.out.print(probeds.get(h) + " ");
//        }
//        System.out.println();

        if (probeds.isEmpty()) { // kondisi kosong (state awal)
            probeds.add(0);
        } else //kondisional terbaru
        if ((Integer) probeds.get(probeds.size() - 1) != rows * columns - 1) { //kondisi normal
            int newprob = (Integer) probeds.get(probeds.size() - 1);
            probeds.set(probeds.size() - 1, ++newprob);
        } else if (((Integer) probeds.get(probeds.size() - 1) == rows * columns - 1)) {
            if (!isConsecutive(probeds) && !isExistConsec(probeds)) { // kondisi1 && !kondisi2 && !kondisi3
                int beflast = (Integer) probeds.get(probeds.size() - 2);
                probeds.set(probeds.size() - 1, beflast + 2);
                int last = (Integer) probeds.get(probeds.size() - 1);
                probeds.set(probeds.size() - 2, --last);
            }
            if (isConsecutive(probeds)) { // kondisi3
                ArrayList temp = makeConsecutive(probeds.size() + 1, 0);
                probeds = temp;
            }
            if (isExistConsec(probeds) && !isConsecutive(probeds)) { //kondisi2
                int beginidx = indexConsecBegin(probeds) - 1;
                int beginwith = (Integer) probeds.get(beginidx) + 1;
                ArrayList temp = modifConsecutive(probeds, beginwith, beginidx);
                probeds = temp;
            }
        }


        //end kondisional terbaru

//        System.out.print("probeds after: ");
//        for (int h = 0; h < probeds.size(); h++) {
//            System.out.print(probeds.get(h) + " ");
//        }
//        System.out.println();

        if (!probeds.isEmpty() && probeds.size() <= rows * columns) {
            for (int ii = 0; ii < state.length - 1; ii++) {
                state[ii] = -2;
            }
            for (int it = 0; it < probeds.size(); it++) {

                int newun = (Integer) probeds.get(it);
                state[newun] = minefield.getTrainingMap()[newun]; // REVEAL!

            }
        }
        return state;
    }

    /**methods untuk membantu getNextState**/
    private ArrayList makeConsecutive(int num, int beginwith) {
        ArrayList arr = new ArrayList();
        for (int i = 0; i < num; i++) {
            arr.add(beginwith + i);
        }
        return arr;
    }

    private ArrayList modifConsecutive(ArrayList oldarr, int beginwith, int beginidx) {

        for (int i = beginidx; i < oldarr.size(); i++) {
            oldarr.set(i, beginwith++);
        }
        return oldarr;
    }

    private boolean isConsecutive(ArrayList arr) {
        boolean bool = false;
        if (arr.isEmpty()) {
            bool = false;
        }
        if (arr.size() == 1) {
            bool = true;
        } else {
            for (int i = 1; i < arr.size(); i++) {
                if ((Integer) arr.get(i) != (Integer) arr.get(i - 1) + 1) {
                    return false;
                }
            }
            bool = true;
        }
//        System.out.println("consecAll: " + bool);
        return bool;
    }

    private boolean isExistConsec(ArrayList arr) {
        boolean bool = false;
        int count = 0;

        if (arr.isEmpty()) {
            bool = false;
        }
        if (arr.size() == 1) {
            bool = true;
        } else {
            if ((Integer) arr.get(arr.size() - 1) == rows * columns - 1) {
                count = 1;
            }
            for (int i = 1; i < arr.size(); i++) {
                if ((Integer) arr.get(i) == (Integer) arr.get(i - 1) + 1) {
                    count++;
                }
            }
            if (count > 0) {
                bool = true;
            } else {
                bool = false;
            }
        }
//        System.out.println("consecExist: " + bool);
        return bool;
    }

    private int indexConsecBegin(ArrayList arr) {
        int idx = 0;
        int count = 0;

        if (arr.isEmpty()) {
            return 0;
        }
        if (arr.size() == 1) {
            return 0;
        } else {
            if ((Integer) arr.get(arr.size() - 1) == rows * columns - 1) {
                idx = arr.size() - 1;
            }
            // scan dari index terakhir
            for (int i = arr.size() - 1; i > 0; i--) {
                if ((Integer) arr.get(i) == (Integer) arr.get(i - 1) + 1) {
                    idx = i - 1;
                } else {
                    break; // sekali ketemu yg tidak consecutive, break.
                }
            }
        }
//        System.out.println("idxConsecBegin: " + idx);
        return idx;
    }

    /**end methods untuk membantu getNextState**/
    private ArrayList getProbeds(short[] state) {
        ArrayList arr = new ArrayList();

        for (int i = 0; i < state.length - 1; i++) {
            if (state[i] != -2) {
                arr.add(i);
            }
        }
        return arr;
    }

    private ArrayList getUnprobeds(short[] state) {
        ArrayList arr = new ArrayList();

        for (int i = 0; i < state.length - 1; i++) {
            if (state[i] == -2) {
                arr.add(i);
            }
        }
        return arr;
    }

    private ArrayList myQValues(short[] state) {
        int i;
        ArrayList uncovereds = this.getUnprobeds(state);
        //ArrayList curTable = new ArrayList(0);
        qValues = new ArrayList(0);
        List curTabletemp;

        /*
         * cek state menang atau kalah
         */

//        if (winState(state)) {
//            qValues.add((double) minefield.safeReward * 9);
//            return qValues;
//        }
//        if (loseState(state)) {
//            qValues.add(-(double) minefield.minePenalty);
//            return qValues;
//        }

//        int statenum = factorial(rows * columns)
//                / (factorial(uncovereds.size() + 1) * factorial(rows * columns - (uncovereds.size() + 1)));
//        System.out.print("MyQValues:State: ");
//        for (int ii = 0; ii < state.length; ii++) {
//            System.out.print(state[ii] + " ");
//        }
//        System.out.println();
        int idxstate = stateIndex(state);

        //System.out.println("IDXstate = " + idxstate);
        int idxQ = (Integer) stateToQval.get(idxstate);
        //System.out.println("IDXQ = " + idxQ);

        //for (i = idxQ; i < idxQ + statenum; i++) {
        curTabletemp = qValuesStateAction.subList(idxQ, idxQ + uncovereds.size());
        qValues.addAll(curTabletemp);
        //}
        //return Array.get(curTable, state[i]);
//        System.out.print("MyQValues: ");
//        for (int iii = 0; iii < qValues.size(); iii++) {
//            System.out.print("Q"+iii+"=["+qValues.get(iii) + "] ");
//        }
//        System.out.println();
        return qValues;
    }

    private int stateIndex(short[] state) {
        int idx;
        if (state == null) {
            return -1;
            //idx = -1;
        } else {
            //System.out.println("\tstateTablesize="+stateTable.size());
            for (int i = 0; i < stateTable.size(); i++) {
                short[] hoo = stateTable.get(i);
                if (Arrays.equals(state, hoo)) {
                    return i;
                    //idx = -1;
                    //break;
                }

            }
        }
        short[] tobeinserted = new short[state.length];
        System.arraycopy(state, 0, tobeinserted, 0, state.length);

        stateTable.add(tobeinserted);
        int j = stateTable.size() - 1;
        stateToQval.add(j + 1, (Integer) stateToQval.get(j) + this.getUnprobeds(state).size());
        int needact = this.getUnprobeds(state).size();
        for (int i = 0; i < needact; i++) {
            //qValuesStateAction.add(initValue++); // MESTI DIKEMBALIKAN SEPERTI SEMULA: tanpa inkremen!
            if (loseState(state)) {
                qValuesStateAction.add(-(double) minefield.minePenalty);
            } else {
                qValuesStateAction.add(0.0); //tanpa inkremen!
            }
            //System.out.print("[" + qValuesStateAction.get(i) + "]");
        }

        //stateTable.add(tobeinserted);
        idx = stateIndex(tobeinserted);
        return idx;
        //return -1;
    }

    public double getQValue(short[] state, int[] action) {
        System.out.println("Begin getQValue...");
        double qValue = 0;
        ArrayList qValueso = myQValues(state);
        //int idxstate = stateTable.indexOf(state);
        int coord = coordToIndex(action);
        System.out.println("coord action = " + coord + " [" + action[0] + "," + action[1] + "]");

//        /**debug**/
//        System.out.print("myQVal getQValue: ");
//        for (int ii = 0; ii < qValueso.size(); ii++) {
//            System.out.print(qValueso.get(ii) + " ");
//        }
//        System.out.println();
//        /**debug**/


        System.out.print("unprobeds: ");
        for (int ii = 0; ii < getUnprobeds(state).size(); ii++) {
            System.out.print(getUnprobeds(state).get(ii) + " ");
        }
        System.out.println();
        int idxstate = stateIndex(state);
        System.out.println("stateIndex: " + idxstate);
        int idxQval = getUnprobeds(state).indexOf(coord);

        System.out.println("idxQval = " + idxQval);
        System.out.println("idxQvalBegin = " + (Integer) stateToQval.get(idxstate));
        qValue = (Double) qValuesStateAction.get((Integer) stateToQval.get(idxstate) + idxQval);

        //qValue = (Double) qValuesStateAction.get(this.stateActionToString(state, action));
        System.out.println("qVal: state=" + stateIndex(state) + "; action=" + idxQval + "; qVal=" + qValue);
        System.out.println("End getQValue.");
        return qValue;
    }

    public double[] getQValuesAt(short[] state) {
        // persis myQValues tapi public
        int i;
        ArrayList uncovereds = this.getUnprobeds(state);
        ArrayList curTable = new ArrayList(0);
        double[] returnValues;
        List curTabletemp;

//        int statenum = factorial(rows * columns)
//                / (factorial(uncovereds.size() + 1) * factorial(rows * columns - (uncovereds.size() + 1)));
//        System.out.print("getQValuesAt:state: ");
//        for (int ii = 0; ii < state.length; ii++) {
//            System.out.print(state[ii] + " ");
//        }
//        System.out.println();
        int idxstate = stateIndex(state);
        //System.out.println("IDXstate hoo= " + idxstate);
        int idxQ = (Integer) stateToQval.get(idxstate);
        //for (i = idxQ; i < idxQ + statenum; i++) {
        curTabletemp = qValuesStateAction.subList(idxQ, idxQ + uncovereds.size());
        curTable.addAll(curTabletemp);
        returnValues = new double[curTabletemp.toArray().length];
        for (int ii = 0; ii < curTabletemp.size(); ii++) {
            returnValues[ii] = (Double) curTabletemp.get(ii);
        }
        //}
        //return Array.get(curTable, state[i]);
        System.out.print("MyQValues (Array): ");
        for (int iii = 0; iii < returnValues.length; iii++) {
            System.out.print(returnValues[iii] + " ");
        }
        System.out.println();

        return returnValues;
    }

    public void setQValue(short[] state, int[] action, double newQValue) {
//        qValuesStateAction = myQValues(state);
//        Array.setDouble(qValuesStateAction, action, newQValue);
//        qValuesStateAction.put(this.stateActionToString(state, action), new Double(newQValue));


        System.out.println("Begin setQValue");
        double qValue = 0;
        ArrayList qValueso = myQValues(state);
        //int idxstate = stateTable.indexOf(state);
        int coord = coordToIndex(action);
        /// int coord = coordToIndexUnprobed(action, state);
        //System.out.println("coord = " + coord);

//        /**debug**/
//        System.out.print("myQVal getQValue: ");
//        for (int ii = 0; ii < qValueso.size(); ii++) {
//            System.out.print(qValueso.get(ii) + " ");
//        }
//        System.out.println();
//        /**debug**/


//        System.out.print("getQValue Unprobeds: ");
//        for (int ii = 0; ii < getUnprobeds(state).size(); ii++) {
//            System.out.print(getUnprobeds(state).get(ii) + " ");
//        }
//        System.out.println();
        int idxstate = stateIndex(state);
//        System.out.println("stateIndex: " + idxstate);
        int idxQval = getUnprobeds(state).indexOf(coord);

//        System.out.println("idxQval = " + idxQval);
        qValuesStateAction.set((Integer) stateToQval.get(idxstate) + idxQval, newQValue);
        System.out.println("StateAction: " + (Integer) stateToQval.get(idxstate) + idxQval + " newQval = " + newQValue + "\nEnd setQValue.");
    }

    public String stateToString(short[] state) {
        String hoo = "";
        for (int i = 0; i
                < state.length; i++) {
            if (i < state.length - 1) {
                hoo += state[i];
                hoo += ",";
            } else {
                hoo += state[i];
                hoo += "%";
            }
        }
        return hoo;
    }

    public String actionToString(int[] action) {
        String hoo = "";
        for (int i = 0; i
                < action.length; i++) {
            if (i < action.length - 1) {
                hoo += action[i] + ",";
            } else {
                hoo += action[i];
            }
        }
        return hoo;
    }

    public String stateActionToString(short[] state, int[] action) {
        String hoo = "";
        hoo += this.stateToString(state) + this.actionToString(action);
        return hoo;
    }

    public double getMaxQValue(short[] state) {

        double maxQ = -Double.MAX_VALUE;
        ArrayList qValsTemp = myQValues(state);

        for (int action = 0; action < qValsTemp.size(); action++) {
            if ((Double) qValsTemp.get(action) > maxQ) {
                maxQ = (Double) qValsTemp.get(action);
            }
        }
        return maxQ;
    }

    public int getBestAction(short[] state) {
        System.out.println("begin GetBestAction...");
        double maxQ = -Double.MAX_VALUE;
        int selectedAction = -1;
        int maxDV = 0;
        /***/
        System.out.print("gba State: ");
        for (int ii = 0; ii < state.length; ii++) {
            System.out.print(state[ii] + " ");
        }
        // jika kebuka semua, berarti kalah, action yg dikembalikan ya -1
        if (this.getUnprobeds(state).size() == 0) {
            System.out.println("kebuka semua oiy!");
            return -1;
        }
        System.out.println();
        /***/
        ArrayList qValuesTemp = myQValues(state);
        /***/
        System.out.print("MyQValues: ");
        //System.out.print("QVALUES null?: "+((qValues==null)?"true":"false"));
        for (int iii = 0; iii < qValues.size(); iii++) {
            System.out.print(qValues.get(iii) + " ");
        }
        System.out.println();
        /***/
        int[] doubleValues = new int[qValuesTemp.size()];

        for (int action = 0; action < qValuesTemp.size(); action++) {
//             System.out.println( "STATE: [" + state[0] + "," + state[1] + "]"
//             );
//             System.out.println( "action:qValue, maxQ " + action + ":" +
//             qValuesStateAction[action] + "," + maxQ );

            if ((Double) qValuesTemp.get(action) > maxQ) {
                selectedAction = action;
                maxQ = (Double) qValuesTemp.get(action);
                maxDV = 0;
                doubleValues[maxDV] = selectedAction;
            } else if ((Double) qValuesTemp.get(action) == maxQ) {
                maxDV++;
                doubleValues[maxDV] = action;
            }
        }

        if (maxDV > 0) {
            // System.out.println( "DOUBLE values, random selection, maxdv =" +
            // maxDV );
            int randomIndex = (int) (Math.random() * (maxDV + 1));
            selectedAction = doubleValues[randomIndex];
        }

        if (selectedAction == -1) {
            // System.out.println("RANDOM Choice !" );
            selectedAction = (int) (Math.random() * qValuesTemp.size());
        }
        // Menegembalikan indeks (pada state), bukan indeks pada unprobeds.
        System.out.println("SelectedAction: " + selectedAction);
        int bestIdx = coordToIndex((int[]) indexUnprobedToCoord(selectedAction, state));
        System.out.println("bestAction: " + bestIdx);
        return bestIdx;
    }

    private boolean winState(short[] state) {
        if (getUnprobeds(state).size() == 0) {
            return false;
        }
        if (getUnprobeds(state).size() == minefield.mines) {
            for (int i = 0; i < state.length; i++) {
                if (state[i] == MineWorld.BOOM) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean loseState(short[] state) {
        if (getUnprobeds(state).size() == 0) {
            return true;
        } else {
            for (int i = 0; i < state.length - 1; i++) {
                if (state[i] == MineWorld.BOOM) {
                    return true;
                }
            }
        }
        return false;
    }

    private int coordToIndexUnprobed(int[] action, short[] state) {
        ArrayList unprobeds = getUnprobeds(state);
        int idx = coordToIndex(action);
        int returnidx = (Integer) unprobeds.indexOf(idx);
//        System.out.print("indexUnprobedToCoord: [");
//        for (int i = 0; i < hoo.length; i++) {
//            System.out.print(hoo[i]);
//            if (i == 0) {
//                System.out.print(",");
//            }
//        }
//        System.out.println("]");

        return returnidx;
    }
}
