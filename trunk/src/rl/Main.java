package rl;

//import gen.MineGenerator;
public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World");
        //Generator g = new Generator(3,3,1);
        //g.retrieveMap();

        //Interfaceu displayo;

        MineWorld mino = new MineWorld(3, 3, 1);
        mino.printTrainingMap();
//        mino.printStateArray();
//        mino.getNextState(1, 0, 0);
//        mino.getNextState(1, 1, 1);
//        mino.getNextState(1, 2, 2);
//        mino.printState(mino.getState(2, 2));
//		mino.getState();
//		mino.getNextState(2, 0);
//		mino.getState();
//		mino.look(2, 0);


//        short[] statea = mino.getState(2, 2);
        //System.out.println(rlp.stateActionTable.size());
        /*
         * Test method policy.MyQValues
         */
//        System.out.println("testMyQValues:");
//        for (int i = 0; i < rlp.myQValues(statea).size(); i++) {
//            System.out.println(rlp.myQValues(statea).get(i)+" ");
//        }
//        System.out.println("end testMyQValues.");

//        rlp.getQValue(statea, 1);
//        rlp.setQValue(statea, 1,2.22);
//        rlp.getBestAction(statea);
        RLPolicy rlp = new RLPolicy(mino);
        System.out.println("TESTO!");
//        mino.printStateArray();
//        mino.getNextState(0, 0, 0);
//        mino.printStateArray();
//        mino.getNextState(0, 1, 0);
//        mino.printStateArray();
//        mino.getNextState(0, 0, 1);
//        mino.printStateArray();
//        mino.getNextState(0, 1, 1);
//        mino.printStateArray();
//        mino.getNextState(0, 0, 0);
//        mino.printStateArray();

        //for (int i = 0; i < 5; i++) {
        mino.getNextState(MineWorld.PROBE_P);
        mino.printStateArray();
        mino.getNextState(MineWorld.PROBE_R);
        mino.printStateArray();
        mino.getNextState(MineWorld.PROBE_L);
        mino.printStateArray();
        mino.getNextState(MineWorld.PROBE_D);
        mino.printStateArray();
        mino.getNextState(MineWorld.PROBE_DR);
        mino.printStateArray();
        mino.getNextState(MineWorld.LEAVE);
        mino.printStateArray();
        //}

        //rlp.printAllQvals();
        ///RLearner rln = new RLearner(mino);
        ///rlp.initValues(0);
        ///rlp.printStates();
        //rlp.printStateToQval();
        //rlp.printAllQvals();
        short[] stateu = new short[]{0, -2, -2, -2, -2, -2, -2, -2, -2, 1};
        short[] statei = new short[]{-2, 1, -2, -2, -2, -2, -2, -2, -2, 1};

        ///rlp.getQValue(stateu, new int[]{2,0});
        //System.out.println("MAXQ: "+rlp.getMaxQValue(stateu));
        //System.out.println("BestAction: "+rlp.getBestAction(stateu));
        ///rlp.getQValuesAt(stateu);
        ///rln.selectAction(stateu);
        ///rlp.getBestAction(stateu);

//        rlp.printCoords();
//        rlp.indexUnprobedToCoord(7, stateu);
        //rln.runEpoch();

        //rlp.setQValue(stateu, new int[]{2,0}, 9999);
        //rlp.printAllQvals();

//        System.out.println("minefield:");
//        rlp.minefield.printTrainingMap();
//        short[] statetest = {-2, -2, -2, -2, -2, -2, -2, -2, -2, 1};
//        int[] actiontest = {2,2};
//
//        short[] newstate;
//
//        for (int i = 0; i < 512; i++) {
//            System.out.println("newstate[" + (i+1) + "]:");
//            statetest = rlp.getNextState(statetest);
//            for (int j = 0; j < statetest.length - 1; j++) {
//                System.out.print(statetest[j]);
//            }
//            System.out.println();
//
//        }

//////////////////////////////////////
        //g.saveGenerateMap(4, 4, 4);

//		MineGenerator mg = new MineGenerator();
//		mg.generateMap(3, 3, 3);
//		mg.showMinemap();

//		CobiDbase cd = new CobiDbase();
//		cd.doDbase();

//		CobiHash ch = new CobiHash();
//		ch.doSave();
//		ch.doLoad();


    }
}
