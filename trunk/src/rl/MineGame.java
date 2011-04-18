package rl;


import javax.swing.SwingUtilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Wafdan
 */
public class MineGame extends Thread {

    long delay;
    Interfaceu a;
    RLPolicy policy;
    MineWorld world;
    static final int GREEDY = 0, SMART = 1; // tipe agent
    int agenttype = SMART;
    public boolean gameOn = false, single = false, gameActive, newInfo = false;

    public MineGame(Interfaceu iface, long delay, MineWorld mworld, RLPolicy pol) {
        this.a = iface;
        this.delay = delay;
        this.world = mworld;
        this.policy = pol;
    }

    public void run() {
        System.out.println("Game Thread Started.");
        try {
            while (true) {
                while (gameOn) {
                    gameActive = true;
                    //resetGame();
                    SwingUtilities.invokeLater((Runnable) a);
                    //runGame();
                    gameActive = false;
                    newInfo = true;
                    SwingUtilities.invokeLater((Runnable) a);
                    sleep(delay);
                }
                sleep(delay);
            }
        } catch (InterruptedException e) {
            System.out.println("interrupted.");
        }
        System.out.println("== Game finished.");
    }

    public void runGame(){
        int[] actioncoord = new int[2];
        while(!world.endState()){
            int action=-1;
            if(agenttype==SMART){
                action = policy.getBestAction(world.getState());
            }else{
                System.err.println("Tipe agen tidak valid.");
            }
            actioncoord=policy.indexToCoord(action);
            world.getNextState(actioncoord[0], actioncoord[1]);

            SwingUtilities.invokeLater((Runnable) a);

            try{
                sleep(delay);
            } catch(InterruptedException e){
                System.out.println("interrupted.");
            }
        }
        a.winscore +=world.numwin;
        a.losescore +=world.numlose;

        // penanganan kalo cuma single game
        // if (single) gameOn = false;
    }

    public void interrupt(){
        super.interrupt();
        System.out.println("(interrupt)");
    }

    public void setPolicy(RLPolicy p){
        this.policy = p;
    }

    public void resetGame(){
        world.resetState();
    }

}
