package rl;

import java.util.ArrayList;
import javax.print.attribute.standard.Finishings;

public class MineWorld implements RLWorld {

    public Generator mapGenerator;
    public int columns, rows; // dimensi panjang&lebar board. bx == kolom, by == rows
    public int mines; // banyaknya ranjau
    public boolean gotMine = false; // true jika kena ranjau
    public int numwin = 0;
    public int numlose = 0;
    public int safeReward = 50, minePenalty = 100;
    public int NUM_OBJECTS = columns * rows + 1; // banyaknya objek: 9kotak
    // + jumlah ranjau tersisa
    // /
    /**
     * Marked return code.
     *
     * @see Map#look
     */
    static final short MARKED = -3;
    /**
     * Unprobed return code.
     *
     * @see Map#look
     */
    static final short UNPROBED = -2;
    /**
     * Boom return code.
     *
     * @see Map#look
     */
    static final short BOOM = -1;
    // /
    boolean victory;
    short[] stateArray;
    double waitingReward;
    short[] trainingMap;
    short[] coveredMap;
    static final double INIT_VALS = 0;

    public MineWorld(int rows, int columns, int m) {
        this.columns = columns;
        this.rows = rows;
        mines = m;
        // inisialisasi number of object dlm state, 1 terakhir untuk jumlah ranjau.
        NUM_OBJECTS = rows * columns + 1;
        // inisialisasi Generator
        // mapGenerator = new Generator(rows, columns, m); // dicomment sebentar untuk pengujian

        // mendapatkan satu training Map dari generator
        //trainingMap = mapGenerator.getSingleMap(1);
        //this.getTrainingMap();

        trainingMap = new short[rows * columns];

        // konstruksi Map yg menjadi tabir (cover)
        coveredMap = new short[rows * columns];
        stateArray = new short[NUM_OBJECTS];
        // reset state
        resetState();
    }

    public short[] getState() {
        //stateArray = new short[NUM_OBJECTS];

        short[] returnArray = new short[stateArray.length];

        // siapkan indeks seukuran trainingMap
        // int temp = bx * by - 1;
        int temp = coveredMap.length;
        // isi stateArray dengan nilai2.
        // jika tercover, isi dengan nilai coveredMap.
        // jika tidak, isi dengan nilai trainingMap.
        for (int i = 0; i < temp; i++) {
            if (coveredMap[i] == 1) {
                //stateArray[i] = coveredMap[i];
                stateArray[i] = UNPROBED;
            } else {
                stateArray[i] = trainingMap[i];
            }
        }

        // menyimpan jumlah ranjau
        stateArray[temp] = (short) mines;

        //printStateArray();
        System.arraycopy(stateArray, 0, returnArray, 0, stateArray.length);

        return returnArray;
    }

    public void printTrainingMap() {
        System.out.print("training map = ");
        for (int i = 0; i < trainingMap.length; i++) {
            System.out.print(trainingMap[i]);
        }
        System.out.println();
    }

    public void printStateArray() {
        System.out.print("curr state: ");
        for (int i = 0; i < stateArray.length - 1; i++) {
            if (stateArray[i] == UNPROBED) {
                System.out.print("?");
            } else {
                System.out.print(stateArray[i]);
            }
        }
        System.out.println(" mine=" + stateArray[stateArray.length - 1]);
    }

    public int[] probe(int x, int y) {
        int[] coord = new int[2];
        coord[0] = x;
        coord[1] = y;

        coveredMap[columns * y + x] = 0;
        System.out.println("now probing (" + x + "," + y + ")");

        return coord;
    }

    public short look(int x, int y) {
        int coord = columns * y + x;
        System.out.println("look (" + x + "," + y + ")=" + this.stateArray[coord]);
        return this.stateArray[coord];
    }

    public short[] getTrainingMap() {
        //printTrainingMap();
        // return this.mapGenerator.array_genmap[idx];
        return this.trainingMap;
    }

    boolean legal(int x, int y) {
        return ((x >= 0) && (x < columns) && (y >= 0) && (y < rows));
    }

    @Override
    public boolean endState() {
//        System.out.println("endstate? nih: ");
//        printStateArray();
        boolean end = false;
        if (defeat() || won()) {
            return true;
        }
        victory = true;
        return end;
    }

    public boolean defeat() {
        boolean def = false;
        System.out.println("DEFEAT?");
        for (int i = 0; i < stateArray.length; i++) {
            if (stateArray[i] == BOOM) {
                System.out.println("DEFEAT!");
                def = true;
                break;
            }
        }
        return def;
    }

    public boolean won() {
        //return victory;
        System.out.println("WON?");
        //ArrayList unprobed = new ArrayList();
        for (int i = 0; i < stateArray.length - 1; i++) {
            if (stateArray[i] == BOOM) {
                return false;
            }
        }
        for (int i = 0; i < stateArray.length - 1; i++) {
            if (stateArray[i] == UNPROBED && trainingMap[i] != BOOM) {
                return false;
            }
        }
        System.out.println("WON!!!");
        return true;
    }

    @Override
    public int[] getDimension() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getInitValues() {
        // TODO Auto-generated method stub
        return INIT_VALS;
    }

    @Override
    public short[] getNextState(int x, int y) {
        // TODO Auto-generated method stub
        // action agen: probe koordinat (x,y)
        probe(x, y);
        getState();
        waitingReward = calcReward();
        return getState();
    }

    @Override
    public double getReward() {
        // TODO Auto-generated method stub
        return waitingReward;
    }

    public double calcReward() {
        double newReward = 0;
        if (defeat()) {
            newReward -= minePenalty;
        } else if (won()) {
            newReward += safeReward;
        } else {
            newReward += 0;
        }
        return newReward;
    }

    @Override
    public short[] resetState() {
        // inisialisasi coveredMap menjadi tercover semua
        for (int i = 0; i < coveredMap.length; i++) {
            coveredMap[i] = 1;
        }
        for (int i = 0; i < stateArray.length - 1; i++) {
            stateArray[i] = UNPROBED; // 1 covered, 0 uncovered
        }
        stateArray[stateArray.length - 1] = (short) mines;

        return getState();
    }

    @Override
    public boolean validAction(int action) {
        // TODO Auto-generated method stub
        return false;
    }
}
