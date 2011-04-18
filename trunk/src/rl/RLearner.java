package rl;

public class RLearner {

    public RLWorld thisWorld;
    //MineWorld mineWorld;
    public RLPolicy policy;
    // tipe learning
    public static final int Q_LEARNING = 1;
    // tipe pemilihan aksi
    public static final int E_GREEDY = 1;
    int learningMethod;
    int actionSelection;
    double alpha, gamma, lambda, epsilon, temp;
    short[] state;
    short[] newstate;
    int[] action;
    double reward;
    // epoch-epoch-an//
    int epochs;
    public int epochsdone;
    Thread thisThread;
    public boolean running;
    boolean random = false;

    public RLearner(RLWorld world) {
        thisWorld = (MineWorld) world;
        policy = new RLPolicy(thisWorld);
        policy.initValues(0);

        learningMethod = Q_LEARNING;
        actionSelection = E_GREEDY;

        epsilon = 0.1;
        temp = 1;

        alpha = 1;
        gamma = 0.1;
        lambda = 0.1;

        state = new short[policy.columns * policy.rows + 1];
        newstate = new short[policy.columns * policy.rows + 1];

        System.out.println("RLearner initialized");
    }

    public void runEpoch() {
        state = thisWorld.resetState();
        
        double this_Q;
        double max_Q;
        double new_Q;

        while (!thisWorld.endState()) {
            if (!running) {
                break;
            }
            System.out.println("[[[[[[[[[Begin RunEpochs...]]]]]]]]]");
            // action berupa probing koordinat x,y
            action = selectAction(state);
            System.out.println("selected action: [" + action[0] + "," + action[1] + "]");
            newstate = thisWorld.getNextState(action[0], action[1]);
            //System.arraycopy(thisWorld.getNextState(action[0], action[1]), 0, newstate, 0, newstate.length);
            System.out.print("state: ");
            /**/
            for (int i = 0; i < state.length - 1; i++) {
                if (state[i] == -2) {
                    System.out.print("?");
                } else {
                    System.out.print(state[i]);
                }
            }
            System.out.println(" mine=" + state[state.length - 1]);

            System.out.print("newstate: ");
            /**/
            for (int i = 0; i < newstate.length - 1; i++) {
                if (newstate[i] == -2) {
                    System.out.print("?");
                } else {
                    System.out.print(newstate[i]);
                }
            }
            System.out.println(" mine=" + newstate[newstate.length - 1]);
            /**/
            reward = thisWorld.getReward();
            System.out.println(" reward=" + reward);

            this_Q = policy.getQValue(state, action);
            max_Q = policy.getMaxQValue(newstate);

            // Calculate new Value for Q
            new_Q = this_Q + alpha * (reward + gamma * max_Q - this_Q);
            policy.setQValue(state, action, new_Q);

            // Set state to the new state.
            state = newstate;
        }
        System.err.println("EndState.");
    }

    public void setAlpha(double a) {

        if (a >= 0 && a < 1) {
            alpha = a;
        }
    }

    public double getAlpha() {

        return alpha;
    }

    public void setGamma(double g) {

        if (g > 0 && g < 1) {
            gamma = g;
        }
    }

    public double getGamma() {

        return gamma;
    }

    public void setEpsilon(double e) {

        if (e > 0 && e < 1) {
            epsilon = e;
        }
    }

    public double getEpsilon() {

        return epsilon;
    }

    public RLPolicy getPolicy() {
        return policy;
    }

    public RLPolicy newPolicy() {
        policy = new RLPolicy(thisWorld);
        policy.initValues(thisWorld.getInitValues());
        return policy;
    }

    public int[] selectAction(short[] state) { // jgn lupa diganti jadi private
        System.out.println("begin selectAction...");
//        System.out.print("\tstate: ");
//        for (int ii = 0; ii < state.length; ii++) {
//            System.out.print(state[ii] + " ");
//        }
//        System.out.println();
        double[] qValues = policy.getQValuesAt(state);
        int selectedAction = -1;
        //switch (actionSelection) {

        //case E_GREEDY: {

        random = false;
        double maxQ = -Double.MAX_VALUE;
        int[] doubleValues = new int[qValues.length];
        int maxDV = 0;

        //Explore
        if (Math.random() < epsilon) {
            selectedAction = -1;
            random = true;
        } else {
            int actiono;
            for (actiono = 0; actiono < qValues.length; actiono++) {

                if (qValues[actiono] > maxQ) {
                    selectedAction = actiono;
                    maxQ = qValues[actiono];
                    maxDV = 0;
                    doubleValues[maxDV] = selectedAction;
                } else if (qValues[actiono] == maxQ) {
                    maxDV++;
                    doubleValues[maxDV] = actiono;
                }
            }
            //System.out.println("actiono=" + actiono);
            if (maxDV > 0) {
                int randomIndex = (int) (Math.random() * (maxDV + 1));
                selectedAction = doubleValues[randomIndex];
            }
        }

        // Select random action if all qValues == 0 or exploring.
        if (selectedAction == -1) {

            // System.out.println( "Exploring ..." );
            selectedAction = (int) (Math.random() * qValues.length);
        }

        // Choose new action if not valid. (tapi da pasti valid euy)
        //while (!thisWorld.validAction(selectedAction)) {
        ////System.out.println("HOOO DEBUG HOOO.");

        //selectedAction = (int) (Math.random() * qValues.length);
        // System.out.println( "Invalid action, new one:" + selectedAction);
        //}

        //break;
        //} //case egreedy
        //} //switch

        // selected action itu indeks pada unprobeds, sedangkan bestIdx itu indeks pada state.
        // gunakan yg di state.
        //int bestIdx = policy.coordToIndex((int[]) policy.indexUnprobedToCoord(selectedAction, state));
//        int bestIdx = policy.coordToIndex((int[]) policy.indexToCoord(selectedAction));
//        System.out.println("bestAction idx in MyQValues: " + bestIdx);
//        System.out.println("selectedAction Unprobed: " + selectedAction);
        System.out.println("end selectAction.");
        return policy.indexUnprobedToCoord(selectedAction, state);
    }
}
