package rl;

import javax.swing.*;
import sp.*;
import map.*;
import set.*;

public final class RLStrategy implements Strategy {
    /* from MineGame */

    long delay;
    PGMS a;
    RLearner rl;
    RLController rlc;
    MineWorld world;
    static final int GREEDY = 0, SMART = 1; // tipe agent
    int agenttype = SMART;
    public boolean gameOn = false, single = false, gameActive, newInfo = false;
    private RLPolicy policy;
    private MineWorld mworld;

    /**
     * Invoke the RLStrategy strategy.
     * @see Strategy
     */
    public void play(Map m) {
        System.out.println("RL playing...");
        for (;;) {
            int y = m.pick(m.rows());
            int x = m.pick(m.columns());
            int q = m.probe(x, y);	// Guess a point to be probed
            if (Map.BOOM == q) // Opps! Bad guess
            {
                return;
            } else if (q >= 0) {
                //apply(m, x, y);		// Try strategy at this point

                if (m.done()) {
                    return;		// We win!
                }
            }
        }
    }

    public void play(Map m, RLController rlc) {
        System.out.println("True RL playing...");
        this.rlc = rlc;
        //System.err.println("RLC null = "+(rlc==null?"true":"false"));
        if (this.rlc == null) {
            System.err.println("RL palying gagal. RLC masih null!");
            return;
        }
        this.rl = rlc.getLearner();
        mworld = (MineWorld) rl.thisWorld;
        mworld.resetState();
        policy = rlc.getLearner().getPolicy();
        int action;
        int actioncoord[] = new int[2];
        int x;
        int y;
        // percobaan pertama random
//        y = m.pick(m.rows());
//        x = m.pick(m.columns());
//        int q = m.probe(x, y);	// Guess a point to be probed
//        if (Map.BOOM == q) // Opps! Bad guess
//        {
//            return;
//        }

        for (;;) {
            //apply(m, x, y);		// Try strategy at this point
//            System.out.print("mworld.getstate: ");
            /**/
//            for (int i = 0; i < mworld.getState().length - 1; i++) {
//                if (mworld.getState()[i] == -2) {
//                    System.out.print("?");
//                } else {
//                    System.out.print(mworld.getState()[i]);
//                }
//            }
//            System.out.println();
            /**/
            action = policy.getBestAction(mworld.getState());
            actioncoord = policy.indexToCoord(action);
            if (actioncoord == null) {
                return;
            }
            x = actioncoord[0];
            y = actioncoord[1];
            m.probe(x,y);
            mworld.getNextState(x, y);
            if (m.done()) {
                System.out.print("<<<<<<<<<<<GAME OVER!>>>>>>>>>>");
                return;		// We win!
            }
            //System.out.print("<<<<<<<<<<<DEBUGu>>>>>>>>>>");
        }
    }

    private int worldToMap(int hoo){
        int[] ar = new int[mworld.rows];
        int row = mworld.rows;
        for(int i=0;i<row;i++){
            ar[i]= --row;
        }
        return ar[hoo];
    }

    /*
     *This routine applies the RLStrategy Strategy.
     */
    public void init(PGMS iface, long delay, MineWorld mworld, RLPolicy pol) {
        this.a = iface;
        this.delay = delay;
        this.world = mworld;
        this.policy = pol;
    }
//
//    public void run() {
//        System.out.println("Game Thread Started.");
//        try {
//            while (true) {
//                while (gameOn) {
//                    gameActive = true;
//                    //resetGame();
//                    SwingUtilities.invokeLater((Runnable) a);
//                    //runGame();
//                    gameActive = false;
//                    newInfo = true;
//                    SwingUtilities.invokeLater((Runnable) a);
//                    sleep(delay);
//                }
//                sleep(delay);
//            }
//        } catch (InterruptedException e) {
//            System.out.println("interrupted.");
//        }
//        System.out.println("== Game finished.");
//    }
//
//    public void runGame() {
//        int[] actioncoord = new int[2];
//        while (!world.endState()) {
//            int action = -1;
//            if (agenttype == SMART) {
//                action = policy.getBestAction(world.getState());
//            } else {
//                System.err.println("Tipe agen tidak valid.");
//            }
//            actioncoord = policy.indexToCoord(action);
//            world.getNextState(actioncoord[0], actioncoord[1]);
//
//            SwingUtilities.invokeLater((Runnable) a);
//
//            try {
//                sleep(delay);
//            } catch (InterruptedException e) {
//                System.out.println("interrupted.");
//            }
//        }
//
//        // penanganan kalo cuma single game
//        // if (single) gameOn = false;
//    }
//
//    public void interrupt() {
//        super.interrupt();
//        System.out.println("(interrupt)");
//    }

    public void setPolicy(RLPolicy p) {
        this.policy = p;
    }

    public void resetGame() {
        world.resetState();
    }
}

