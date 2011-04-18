package rl;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import map.*;

public class RLController extends Thread {

    public RLearner learner;
    public MineWorld mineworld;
    public int epochswaiting = 0, epochsdone = 0, totaldone = 0;
    long delay;
    int UPDATE_EPOCHS = 100;
    public boolean newInfo;
    PGMS pgms;

    public RLController(PGMS iface, long waitperiod) {
        
        delay = waitperiod;
        this.pgms = iface;

    }

    public void initDisplayMap(){
        mineworld = mapToWorld(pgms.getDisplayMap().getMineMap());
        learner = new RLearner(mineworld);
        mineworld.printTrainingMap();
    }

    private MineWorld mapToWorld(MineMap m) {

        int r = m.rows();
        int c = m.columns();
        int mi = m.mines();
        short[] traintemp;
        MineWorld mw = new MineWorld(r, c, mi);

        traintemp = new short[r * c];
        for (int y = 0; y < r; y++) {
            for (int x = 0; x < c; x++) {
                traintemp[r * y + x] = (short) m.mine_map[y][x];
//                mark_map[y][x] = false;
//                unprobed_map[y][x] = true;
            }
        }

        mw.trainingMap = traintemp;

        return mw;

    }

    public RLearner getLearner(){
        return learner;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (epochswaiting > 0) {
                    System.out.println("Menjalankan " + epochswaiting + " episodes");
                    learner.running = true;
                    while (epochswaiting > 0) {
                        epochswaiting--;
                        epochsdone++;
                        learner.runEpoch();
                        if (epochswaiting % UPDATE_EPOCHS == 0) {
                            SwingUtilities.invokeLater((Runnable) pgms);
                        }
                    }
                    totaldone += epochsdone;
                    epochsdone = 0;
                    learner.running = false;

                    newInfo = true;
                    SwingUtilities.invokeLater((Runnable) pgms);
                }
                sleep(delay);
            }
        } catch (InterruptedException e) {
            System.out.println("Controller interrupted.");
        }
    }

    public void setEpisodes(int episodes) {
        System.out.println("Setting " + episodes + " episodes");
        this.epochswaiting += episodes;
    }

    public void stopLearner() {
        System.out.println("Stopping learner.");
        newInfo = false;
        epochswaiting = 0;
        totaldone += epochsdone;
        epochsdone = 0;

        SwingUtilities.invokeLater(pgms);

        learner.running = false;
    }

    public synchronized RLPolicy resetLearner() {
        totaldone = 0;
        epochsdone = 0;
        epochswaiting = 0;

        return learner.newPolicy();
    }
}
