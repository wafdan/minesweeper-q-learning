package map;

import com.sun.org.apache.bcel.internal.generic.Select;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import rl.*;

/* Copyright (C) 1995 and 1997 John D. Ramsdell

This file is part of Programmer's Minesweeper (PGMS).

PGMS is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

PGMS is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received pgms copy of the GNU General Public License
along with PGMS; see the file COPYING.  If not, write to
the Free Software Foundation, 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.
 */
/**
 * The class PGMS creates and displays minesweeper games played by Java
 * programs. A user contributes pgms Java class that conforms to the interface
 * called Strategy. The strategy plays pgms game of minesweeper using the methods
 * provided by the Map class.
 * <p>
 * This class provides both an applet for graphical presentations using
 * browsers, and pgms main routine for applications.
 * 
 * @see Strategy
 * @see map.Map
 * @version February 1997
 * @author John D. Ramsdell
 */
public class PGMS extends JApplet implements ActionListener, Runnable {
    // The default strategy

//    private static String default_strategy_name = "sp.SinglePointStrategy";
//    private static String default_strategy_name = "sp.SinglePointStrategy";
    private static String default_strategy_name = "rl.RLStrategy";
    public static final String SET_STRATEGY = "SetStrategy";
    public static final int gap = 5;
    public Container stratChooser;
    public Container mapMaker;
    public Strategy s; // The selected strategy
    int mines = 1; // Beginner game
    int rows = 3;
    int columns = 3;
    DisplayMap m; // Panel for map display
    /** khusus RL **/
    RLController rlc;
    RLearner rl;
    Thread t;
    public boolean ischoosing = true;
    /* ********** */
    /**
     * Application entry point.
     * @param args        program arguments
     * <dl>
     * <dt> <code>-b</code>
     * <dd> play pgms beginner game
     * <dt> <code>-i</code>
     * <dd> play an intermediate game
     * <dt> <code>-e</code>
     * <dd> play an expert game
     * <dt> <code>-s</code> <var>strategy class name</var>
     * <dd> play with given strategy
     * <dt> <code>-n</code> <var>number of games</var>
     * <dd> play multiple games - graphics will be disabled
     * with more than one game
     * </dl>
     */
    private Panel strat_panel;
    private JComboBox com_strat;
    private Label status;
    private Panel cus_panel;
    private JTextField txt_row;
    private JTextField txt_col;
    private JTextField txt_min;
    private Label lb_row;
    private Label lb_col;
    private Label lb_min;
    private DisplayMap display;
    private Label tally;
    private Panel map_panel;
    private Panel pan;
    private Checkbox cb_con;
    private Panel score_panel;
    public Label lb_win;
    public Label lb_lose;
    private Label lb_wins;
    private Label lb_loses;
    private JButton jbt_startTrain;
    private JButton jbt_stopTrain;
    private JButton jbt_clearPolicy;
    private JTextField txt_penalty;
    private JTextField txt_reward;
    private JTextField txt_alpha;
    private JTextField txt_gamma;
    private JTextField txt_epsilon;
    private JTextField txt_epochs;
    private JProgressBar pb_progress;
    private JLabel lb_learnEpochsDone;
    private int DEF_EPOCHS = 50000;
    private JTabbedPane tabbedPane;
    private Panel playPanel;
    private Container trainPanel;
    private MineWorld trainWorld;

    public static void main(String args[]) {
        String strategy_name = default_strategy_name;
        String game_name = "beginner";
        int mines = 1; // Beginner game
        int rows = 3;
        int columns = 3;
        int tries = 1;
        int wins = 0;
        int probed = 0;

//        Strategy s;
//        try {
//            s = (Strategy) Class.forName(strategy_name).newInstance();
//        } catch (Exception e) {
//            System.out.println("Cannot create strategy " + strategy_name);
//            usage();
//            return;
//        }

        //if (tries == 1) {
        ///
        ///
        Frame f = new Frame("PGMS");
        f.setSize(560, 560);
        f.setLayout(new ColumnLayout());
        PGMS p = new PGMS();
        f.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        JPanel pan = new JPanel(new ColumnLayout());
        p.stratChooser = p.makeStratChooser();
        p.mapMaker = p.makeMapMaker();
        p.tabbedPane = (JTabbedPane) p.makeTabPanel();

        pan.add(p.stratChooser, 0);
        pan.add(p.mapMaker, 1);
        pan.add(p.tabbedPane, 2);
        f.add(pan, 0);
        /* // percobian tgl 07/04/2011
        PGMS thegame = new PGMS(mines, rows, columns);
        StratComboBox spanel = new StratComboBox(thegame);
        f.add(spanel);
         */
        //Strategy strato = spanel.getStrategy();
        // System.out.println("strategy:"+s.getClass().getCanonicalName());

//            PGMS p = new PGMS(s, mines, rows, columns);
        MenuBar mb = new MenuBar();
        f.setMenuBar(mb);
        Menu m = new Menu("File");
        mb.add(m);
        MenuItem mi = new MenuItem("Exit", new MenuShortcut(KeyEvent.VK_X));
        mi.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        m.add(mi);
//
//            p.init_display(strategy_name);
//            f.add(p);
        f.pack();
        f.show();
        //p.start();
//


        return;
        //}

//        System.out.print("Playing " + tries + " " + game_name + " games");
//        System.out.println(" using strategy " + strategy_name);
//
//        for (int n = 1; n <= tries; n++) {
//            Map m = new MineMap(mines, rows, columns); // Create mine map
//            try {
//                s.play(m); // Play game
//            } catch (Exception e) {
//                System.out.println(e.toString());
//            }
//            if (m.won()) {
//                wins++; // Record results
//            }
//            if (m.probed()) {
//                probed++;
//            }
//            System.out.print(wins + " wins in " + n + " tries -- "
//                    + percent(wins, n));
//            if (probed > 0) {
//                System.out.print("%, with " + probed + " standard tries -- "
//                        + percent(wins, probed));
//            }
//            System.out.println("%.");
//        }
    }
    public static final long DELAY = 2000;
    private Checkbox cb_change;
    private JRadioButton rb_softmax;
    private JRadioButton rb_greedy;
    private JRadioButton sarsa;
    private JRadioButton qlearn;

    /**
     * Create pgms PGMS instance for an application
     */
    PGMS(Strategy s, int mines, int rows, int columns) {
        this.s = s;
        this.mines = mines;
        this.rows = rows;
        this.columns = columns;
    }

    PGMS(int mines, int rows, int columns) {
        this.mines = mines;
        this.rows = rows;
        this.columns = columns;
        initRLController();
    }

    public void initRLController() {
        // inisialisasi RL controllerw
        rlc = new RLController(this, DELAY);
        rlc.start();
    }

    public Container makeMapMaker() {
        /* This panel displays settings for custom map */
        cus_panel = new Panel(new FlowLayout(FlowLayout.LEFT, gap, 0));
        lb_row = new Label("Rows :", Label.LEFT);
        lb_col = new Label("Cols :", Label.LEFT);
        lb_min = new Label("Mines:", Label.LEFT);
        txt_row = new JTextField(String.valueOf(rows), 2);
        txt_col = new JTextField(String.valueOf(columns), 2);
        txt_min = new JTextField(String.valueOf(mines), 2);
        Button bt_create = new Button("Create");
        bt_create.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                rows = Integer.parseInt(txt_row.getText());
                columns = Integer.parseInt(txt_col.getText());
                mines = Integer.parseInt(txt_min.getText());
                if (display != null) {
                    display.stop();
                    map_panel.remove(display);
                }
//                initRLvar(rows, columns, mines);
//                DisplayMap distemp;
//                distemp = display;
                display = new DisplayMap(s, mines, rows, columns, status, tally, lb_win, lb_lose, rlc, rl);
                if (cb_con != null) {
                    display.setContinuous(cb_con.getState());
                } else {
                    System.out.println("cb_con belum ada!");
                }
                if (cb_change != null) {
                    display.setChanging(cb_change.getState());
                } else {
                    System.out.println("cb_change belum ada!");
                }
//                //display = m;
                //rlc.initDisplayMap();
                display.setBackground(Color.white);
                System.out.println("Hoo");
                display.init();
                m = display;
                map_panel.add(display);
                cb_con.setEnabled(true);
                cb_change.setEnabled(true);
                if (s.getClass().getCanonicalName() == "rl.RLStrategy") {
                    if (rlc != null) {
                        rlc.initDisplayMap();
                        initRLStrategy();
                        System.err.println("RLController berhasil direinisialisasi!");
                    } else {
                        System.err.println("RLController belum diinstantiasi!");
                        initRLController();
                        rlc.initDisplayMap();
                        initRLStrategy();
                        System.err.println("RLController berhasil diinstantiasi!");
                    }
                    tabbedPane.setEnabledAt(1, true);
                    initRLvar(rows, columns, mines);
                    display.setRLController(rlc);
                }
                if(lb_win!=null && lb_lose!=null){
                    lb_win.setText("0");
                    lb_lose.setText("0");
                }
//                cb_con.setVisible(true);
//                lb_win.setText("0");
//                lb_lose.setText("0");
                validate();
                findWindow(mapMaker).pack();
                start();
            }
        });

        cb_con = new Checkbox("Continuous");
        cb_con.addItemListener(new ItemListener() {
            /* display harus sudah diinstantiaasi */

            public void itemStateChanged(ItemEvent e) {
                //System.out.println(e.getStateChange());
                if (e.getStateChange() == 1) {
                    display.setContinuous(true);
                } else {
                    display.setContinuous(false);
                }
            }
        });
        cb_con.setEnabled(false);
//        cb_con.setVisible(false);
        cb_change = new Checkbox("Changing");
        cb_change.addItemListener(new ItemListener() {
            /* display harus sudah diinstantiaasi */

            public void itemStateChanged(ItemEvent e) {
                //System.out.println(e.getStateChange());
                if (e.getStateChange() == 1) {
                    display.setChanging(true);
                } else {
                    display.setChanging(false);
                }
            }
        });
        cb_change.setEnabled(false);
//        cb_con.setVisible(false);

        cus_panel.add(lb_row);
        cus_panel.add(txt_row);
        cus_panel.add(lb_col);
        cus_panel.add(txt_col);
        cus_panel.add(lb_min);
        cus_panel.add(txt_min);
        cus_panel.add(bt_create);
        cus_panel.add(cb_con);
        cus_panel.add(cb_change);

        //cus_panel.setEnabled(false);
        cus_panel.setEnabled(false);
        cus_panel.setVisible(false);
        return cus_panel;
    }

    public Container makeStratChooser() {
        /* This panel displays Strategy chooser */
        final int gap = 5;
        //final Object[] aob = new Object[2];
        Container spanel = new Panel(new FlowLayout(FlowLayout.LEFT, gap, 0));
        final JComboBox como_strat = new JComboBox(new String[]{"sp.SinglePointStrategy", "eqn.EqnStrategy", "eqn.MioStrategy", "rl.RLStrategy"});
        JButton bt_strat = new JButton("Set Strategy");
        //como_strat.setActionCommand(SET_STRATEGY+como_strat.getSelectedItem().toString());
        como_strat.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //String strat = e.getActionCommand();//.getSelectedItem().toString();//e.getActionCommand().substring(11);
                //System.out.println("null? "+((como_strat==null)?"true":"No"+como_strat.getSelectedItem().toString()));
                //como_strat.getSelectedItem().toString();
                //System.out.println(strat);
                //Strategy s;
                try {
                    s = (Strategy) Class.forName(como_strat.getSelectedItem().toString()).newInstance();
                } catch (Exception ex) {
                    System.out.println("Cannot create strategy " + como_strat.getSelectedItem().toString() + ". " + ex);
                    return;
                }
                System.out.println(s.getClass().getCanonicalName());
                if (mapMaker != null) {
                    mapMaker.setEnabled(true);
                    mapMaker.setVisible(true);
                    mapMaker.getParent().validate();
                    status.setText("Strategy: " + s.getClass().getCanonicalName());
                } else {
                    System.err.println("mapMaker null!");
                }
                if(lb_win!=null && lb_lose!=null){
                    lb_win.setText("0");
                    lb_lose.setText("0");
                }
                findWindow(mapMaker).pack();
                //como_strat.setActionCommand(SET_STRATEGY+como_strat.getSelectedItem().toString());
                //como_strat.addActionListener(this);
            }
        });

        //bt_strat.setActionCommand(SET_STRATEGY + como_strat.getSelectedItem().toString());
        //bt_strat.addActionListener(this);
        spanel.add(como_strat);
        //spanel.add(bt_strat);

        //System.out.println("Test strategy " + s.getClass().getCanonicalName());

        return spanel;
    }

    public void initRLStrategy() {
        if (this.s.getClass().getCanonicalName().toString() != "rl.RLStrategy") {
            return;
        } else {
            if (rlc != null) {
                trainWorld = rlc.mineworld;
                rl = rlc.learner; // baru bisa diassign setelah method initDisplayMap jalan
                //RLStrategy rlg = (RLStrategy) s;
                ((RLStrategy) s).init(this, DELAY, trainWorld, rl.getPolicy());
                System.err.println("RLStrategy berhasil diinisialisasi.");
            } else {
                System.err.println("RLStrategy gagal diinisialisasi. RLC masih null!");
            }
        }
    }

    private static int percent(int n, int d) {
        return (200 * n + d) / (2 * d);
    }

    private static void usage() {
        System.out.println("Usage: java PGMS [-b] [-i] [-e]"
                + " [-s strategy_name] [-n number_of_games]");
        System.out.println("Beginner:     -b");
        System.out.println("Intermediate: -i");
        System.out.println("Expert:       -e");
    }

    /**
     * Create applet PGMS
     */
    public PGMS() {
    }

    /**
     * Initialize applet by processing the attributes.
     * <dl>
     * <dt> <code>strategy</code>
     * <dd>class name of user supplied strategy
     * <dt> <code>game</code>
     * <dd>level of game, one of
     * <ul>
     * <li> <code>beginner</code>
     * <li> <code>intemediate</code>
     * <li> <code>expert</code>
     * </ul>
     * </dl>
     * <p>
     * Sample:
     *
     * <pre>
     * &lt;applet codebase="classes" code="map/PGMS.class"
     *       width=302 height=262&gt;
     * &lt;param name="strategy" value="eqn.EqnStrategy"&gt;
     * &lt;param name="game" value="intermediate"&gt;
     * &lt;/applet&gt;
     * </pre>
     */
    public DisplayMap getDisplayMap() {
        return display;
    }

    public void init() {
        String strategy_name = getParameter("strategy");
        String game = getParameter("game");

        if (strategy_name == null) {
            strategy_name = default_strategy_name;
            strategy_name = com_strat.getSelectedItem().toString();
        }

        // Create strategy
        try {
            s = (Strategy) Class.forName(strategy_name).newInstance();
        } catch (Exception e) {
            return;
        }

        if (game != null) { // Set game level
            if (game.equals("intermediate")) {
                mines = 40; // Intermediate game
                rows = 13;
                columns = 15;
            } else if (game.equals("expert")) {
                mines = 99; // Expert game
                rows = 16;
                columns = 30;
            }
        }
        init_display(strategy_name);
    }

    /**
     * This routine creates the panels that make up the display.
     */
    public Container makeTabPanel() {
        map_panel = new Panel(new FlowLayout(FlowLayout.CENTER, gap, 0));
        map_panel.setName("map_panel");
        ///initRLController();
        //display = new DisplayMap(s, mines, rows, columns,
        //        status, tally, lb_win, lb_lose, rlc, rl);
        //display.setBackground(Color.white);
        //display.init();
        //rlc.initDisplayMap();
        ///initRLStrategy();
        //m = display; // Save DisplayMap for the start and stop method
        //map_panel.add(display);

        /* This panel contains the tally and the control buttons. */
        Panel button_panel = new Panel(new FlowLayout(FlowLayout.LEFT, gap, 0));
        tally = new Label("999", Label.CENTER);
        tally.setBackground(Color.white);
        button_panel.add(tally);

        Button b = new Button("Start");
        b.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (display != null) {
                    display.start();
                } else {
                    System.err.println("display belum diinisialisasi!");
                }
//                System.out.println("lb_win: "+lb_win.getX()+","+lb_win.getY());
//                System.out.println("lb_lose: "+lb_lose.getX()+","+lb_lose.getY());
                //validate();
            }
        });
        button_panel.add(b);

        /* This panel displays score */
        score_panel = new Panel(new FlowLayout(FlowLayout.LEFT, gap, 0));
        score_panel.setName("score_panel");
        lb_wins = new Label("Win :");
        lb_wins.setName("lb_wins");
        lb_win = new Label("0");
        lb_win.setName("lb_win");
        lb_loses = new Label("Lose:");
        lb_loses.setName("lb_loses");
        lb_lose = new Label("0");
        lb_lose.setName("lb_lose");
        score_panel.add(lb_wins);
        score_panel.add(lb_win);
        score_panel.add(lb_loses);
        score_panel.add(lb_lose);

        b = new Button("Stop");
        b.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (display != null) {
                    display.stop();
                } else {
                    System.err.println("display belum diinisialisasi!");
                }
            }
        });
        button_panel.add(b);

        /* The status panel displays status information */
        status = new Label();

        /* This is play panel */
        pan = new Panel(new ColumnLayout(0, gap));
        pan.setName("mainPanel");
        pan.add(button_panel);
        pan.add(map_panel);
        pan.add(score_panel);
        pan.add(status, "South");
        //add(p, "Center");

        ///

        tabbedPane = new JTabbedPane();
        playPanel = pan;
        /* This is train panel */
        trainPanel = makeTrainingPanel();

        tabbedPane.addTab("Play", playPanel);
        tabbedPane.addTab("Train", trainPanel);
        tabbedPane.setSelectedIndex(0);

        // disable sampai Map dibuat
        //tabbedPane.setEnabledAt(0, false);
        tabbedPane.setEnabledAt(1, false);
        //add(tabbedPane);
        return tabbedPane;
    }

    private void init_display(String strategy_name) {
        final int gap = 5;
        setLayout(new BorderLayout());
        setBackground(Color.lightGray);

        /* The status panel displays status information */
        status = new Label("Strategy: " + strategy_name,
                Label.CENTER);
        add(status, "South");

        /*
         * The tally panel displays the number of mines minus the number of
         * marks.
         */
        tally = new Label("999", Label.CENTER);
        tally.setBackground(Color.white);

//        /* This panel displays settings for custom map */
//        cus_panel = new Panel(new FlowLayout(FlowLayout.LEFT, gap, 0));
//        lb_row = new Label("Rows :", Label.LEFT);
//        lb_col = new Label("Cols :", Label.LEFT);
//        lb_min = new Label("Mines:", Label.LEFT);
//        txt_row = new JTextField(String.valueOf(rows), 2);
//        txt_col = new JTextField(String.valueOf(columns), 2);
//        txt_min = new JTextField(String.valueOf(mines), 2);
//        Button bt_create = new Button("Create");
//        bt_create.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                rows = Integer.parseInt(txt_row.getText());
//                columns = Integer.parseInt(txt_col.getText());
//                mines = Integer.parseInt(txt_min.getText());
//                System.out.println("Hoo");
//                map_panel.remove(display);
//                initRLvar(rows, columns, mines);
//                DisplayMap distemp;
//                distemp = display;
//                display = new DisplayMap(s, mines, rows, columns, status, tally, lb_win, lb_lose, rlc, rl);
//                //display = m;
//                rlc.initDisplayMap();
//                display.setBackground(Color.white);
//                display.init();
//                m = display;
//                map_panel.add(display);
//                lb_win.setText("0");
//                lb_lose.setText("0");
//                validate();
//            }
//        });
//
//        cb_con = new Checkbox("Continuous");
//
//        cus_panel.add(lb_row);
//        cus_panel.add(txt_row);
//        cus_panel.add(lb_col);
//        cus_panel.add(txt_col);
//        cus_panel.add(lb_min);
//        cus_panel.add(txt_min);
//        cus_panel.add(bt_create);
//        cus_panel.add(cb_con);


//        /* This panel displays Strategy chooser */
//        strat_panel = new Panel(new FlowLayout(FlowLayout.LEFT, gap, 0));
//        com_strat = new JComboBox(new String[]{"sp.SinglePointStrategy", "eqn.EqnStrategy", "eqn.MioStrategy", "rl.RLStrategy"});
//        com_strat.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                s = selectStrategy();
//                status.setText("Strategy: " + com_strat.getSelectedItem().toString());
//            }
//        });
//        strat_panel.add(com_strat);

        /* This panel displays the map. */
        map_panel = new Panel(new FlowLayout(FlowLayout.CENTER, gap, 0));
        map_panel.setName("map_panel");
        initRLController();
        display = new DisplayMap(s, mines, rows, columns,
                status, tally, lb_win, lb_lose, rlc, rl);
        display.setBackground(Color.white);
        display.init();
        rlc.initDisplayMap();
        initRLStrategy();
        m = display; // Save DisplayMap for the start and stop method
        map_panel.add(display);

        /* This panel contains the tally and the control buttons. */
        Panel button_panel = new Panel(new FlowLayout(FlowLayout.LEFT, gap, 0));

        button_panel.add(tally);

        Button b = new Button("Start");
        b.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (cb_con.getState()) {
                    display.setContinuous(true);
                } else {
                    display.setContinuous(false);
                }
                display.start();

//                System.out.println("lb_win: "+lb_win.getX()+","+lb_win.getY());
//                System.out.println("lb_lose: "+lb_lose.getX()+","+lb_lose.getY());
                //validate();
            }
        });
        button_panel.add(b);

        /* This panel displays score */
        score_panel = new Panel(new FlowLayout(FlowLayout.LEFT, gap, 0));
        score_panel.setName("score_panel");
        lb_wins = new Label("Win :");
        lb_wins.setName("lb_wins");
        lb_win = new Label(display.getWinScore());
        lb_win.setName("lb_win");
        lb_loses = new Label("Lose:");
        lb_loses.setName("lb_loses");
        lb_lose = new Label(display.getLoseScore());
        lb_lose.setName("lb_lose");
        score_panel.add(lb_wins);
        score_panel.add(lb_win);
        score_panel.add(lb_loses);
        score_panel.add(lb_lose);

        b = new Button("Stop");
        b.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                display.stop();
            }
        });
        button_panel.add(b);


        /* This is play panel */
        pan = new Panel(new ColumnLayout(0, gap));
        pan.setName("mainPanel");
        pan.add(strat_panel);
        pan.add(cus_panel);
        pan.add(button_panel);
        pan.add(map_panel);
        pan.add(score_panel);
        //add(p, "Center");

        ///

        tabbedPane = new JTabbedPane();
        playPanel = pan;
        /* This is train panel */
        trainPanel = makeTrainingPanel();

        tabbedPane.addTab("Play", playPanel);
        tabbedPane.addTab("Train", trainPanel);
        tabbedPane.setSelectedIndex(0);

        // disable panes until world created
        //tabbedPane.setEnabledAt(1, false);
        add(tabbedPane);
    }

    public void initRLvar(int rows, int columns, int mines) {
        // untuk set variabel aja.
        if (trainWorld != null && rl != null) {
            txt_penalty.setText(Integer.toString(trainWorld.minePenalty));
            txt_reward.setText(Integer.toString(trainWorld.safeReward));
            txt_alpha.setText(Double.toString(rl.getAlpha()));
            txt_gamma.setText(Double.toString(rl.getGamma()));
            txt_epsilon.setText(Double.toString(rl.getEpsilon()));
            System.err.println("variabel RL berhasil diinisialisasi.");
        } else {
            System.err.println("variabel RL gagal diinisialisasi. trainWorld atau rl masih null!");
        }
    }

    public Strategy selectStrategy() {
        String strat = com_strat.getSelectedItem().toString();
        try {
            Strategy ns = (Strategy) Class.forName(strat).newInstance();
            return ns;
        } catch (Exception e) {
            System.out.println("Cannot create selected strategy " + strat);
            usage();
            return null;
        }
    }

    Container makeTrainingPanel() {
        JPanel trainPanel = new JPanel();
        trainPanel.setLayout(new BorderLayout());
        trainPanel.add(makeParamPanel());
        trainPanel.add(makeTrainButtonPanel(), BorderLayout.SOUTH);

        return trainPanel;
    }

    Container makeTrainButtonPanel() {
        JPanel setPanel = new JPanel();
        setPanel.setLayout(new GridLayout(0, 1));
        JPanel controls = new JPanel();

        jbt_startTrain = new JButton("Begin Training");
        jbt_startTrain.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ((txt_epochs.getText() == null ? "" != null : !txt_epochs.getText().equals(""))
                        && (txt_alpha.getText() == null ? "" != null : !txt_alpha.getText().equals(""))
                        && (txt_gamma.getText() == null ? "" != null : !txt_gamma.getText().equals(""))
                        && (txt_epsilon.getText() == null ? "" != null : !txt_epsilon.getText().equals(""))
                        && (txt_reward.getText() == null ? "" != null : !txt_reward.getText().equals(""))
                        && (txt_penalty.getText() == null ? "" != null : !txt_penalty.getText().equals(""))) {
                    doTraining();
                } else {
                    System.err.println("Semua parameter Training harus diisi!");
                }
            }
        });

        jbt_stopTrain = new JButton("Stop");
        jbt_stopTrain.setEnabled(false);
        jbt_stopTrain.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                endTraining();
            }
        });

        jbt_clearPolicy = new JButton("Undo Training");
        jbt_clearPolicy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                rlc.resetLearner();
            }
        });

        controls.add(jbt_startTrain);
        controls.add(jbt_stopTrain);
        controls.add(jbt_clearPolicy);

        setPanel.add(controls);
        return setPanel;
    }

    Container makeParamPanel() {
        JPanel parampane = new JPanel();
        parampane.setLayout(new BorderLayout(1, 2));

        JPanel labelpane = new JPanel();
        labelpane.setLayout(new GridLayout(0, 1));
        labelpane.add(new JLabel("Penalty:", JLabel.RIGHT));
        labelpane.add(new JLabel("Reward:", JLabel.RIGHT));
        labelpane.add(new JLabel("Alpha:", JLabel.RIGHT));
        labelpane.add(new JLabel("Gamma:", JLabel.RIGHT));
        labelpane.add(new JLabel("Epsilon:", JLabel.RIGHT));
        labelpane.add(new JLabel("Action Selection Method:", JLabel.RIGHT));
        labelpane.add(new JLabel("Learning Method:", JLabel.RIGHT));
        labelpane.add(new JLabel("Epochs to train for:", JLabel.RIGHT));
        labelpane.add(new JLabel("Progress:", JLabel.RIGHT));
        labelpane.add(new JLabel("Epochs done:", JLabel.RIGHT));

        JPanel actionButtons = new JPanel();
        actionButtons.setLayout(new GridLayout(1, 0));
        rb_softmax = new JRadioButton("Softmax");
        rb_greedy = new JRadioButton("Greedy",true);
        ButtonGroup actionButts = new ButtonGroup();
        actionButts.add(rb_greedy);
        actionButts.add(rb_softmax);
        actionButtons.add(rb_greedy);
        actionButtons.add(rb_softmax);

        JPanel controlspane = new JPanel();
        controlspane.setLayout(new GridLayout(0, 1));
        txt_penalty = new JTextField(20);
        txt_reward = new JTextField(20);
        txt_alpha = new JTextField(20);
        txt_gamma = new JTextField(20);
        txt_epsilon = new JTextField(20);
        controlspane.add(txt_penalty);
        controlspane.add(txt_reward);
        controlspane.add(txt_alpha);
        controlspane.add(txt_gamma);
        controlspane.add(txt_epsilon);

        //JPanel actionButtons = new JPanel();
        //actionButtons.setLayout(new GridLayout(1, 0));
        //softmax = new JRadioButton("Softmax");
        //greedy = new JRadioButton("Greedy", true);
        //ButtonGroup actionButts = new ButtonGroup();
        //actionButts.add(softmax);
        //actionButts.add(greedy);
        //actionButtons.add(softmax);
        //actionButtons.add(greedy);

        JPanel learnButtons = new JPanel();
        learnButtons.setLayout(new GridLayout(1, 0));
        sarsa = new JRadioButton("SARSA");
        qlearn = new JRadioButton("Q-Learning", true);
        ButtonGroup learnButts = new ButtonGroup();
        learnButts.add(sarsa);
        learnButts.add(qlearn);
        learnButtons.add(sarsa);
        learnButtons.add(qlearn);
        
        txt_epochs = new JTextField(Integer.toString(DEF_EPOCHS));
        pb_progress = new JProgressBar();
        lb_learnEpochsDone = new JLabel("0", JLabel.LEFT);

        controlspane.add(actionButtons);
        controlspane.add(learnButtons);
        controlspane.add(txt_epochs);
        controlspane.add(pb_progress);
        controlspane.add(lb_learnEpochsDone);

        parampane.add(labelpane, BorderLayout.CENTER);
        parampane.add(controlspane, BorderLayout.EAST);

        parampane.setBorder(BorderFactory.createTitledBorder("Parameters"));


        return parampane;
    }

    void doTraining() {

        int episodes = 0;
        double aval;
        double gval;
        double eval;
        int cval = 0;
        int dval = 0;

        episodes = Integer.parseInt(txt_epochs.getText());
        aval = Double.parseDouble(txt_alpha.getText());
        gval = Double.parseDouble(txt_gamma.getText());
        eval = Double.parseDouble(txt_epsilon.getText());
        cval = Integer.parseInt(txt_reward.getText());
        dval = Integer.parseInt(txt_penalty.getText());
        // atur variabel learner
        rl.setAlpha(aval);
        rl.setGamma(gval);
        rl.setEpsilon(eval);

        // disable controls
        jbt_startTrain.setEnabled(false);
        txt_epochs.setEnabled(false);
        txt_reward.setEnabled(false);
        txt_penalty.setEnabled(false);
        txt_alpha.setEnabled(false);
        txt_gamma.setEnabled(false);
        txt_epsilon.setEnabled(false);
        rb_softmax.setEnabled(false);
        rb_greedy.setEnabled(false);
        sarsa.setEnabled(false);
        qlearn.setEnabled(false);

        pb_progress.setMinimum(0);
        pb_progress.setMaximum(episodes);
        pb_progress.setValue(0);

        jbt_stopTrain.setEnabled(true);

//        // mulai training
        trainWorld.safeReward = cval;
        trainWorld.minePenalty = dval;
        rlc.setEpisodes(episodes);

        System.out.println("doTraining...");
    }

    void endTraining() {
        // stop trainingr
        rlc.stopLearner();
        System.out.println("<<<<<<<<<<<<<<<<<PRINTS>>>>>>>>>>>>>>>>");
        rl.getPolicy().printAllQvals();
//        rl.getPolicy().printCoords();
//        rl.getPolicy().printStates();

        // enable buttons
        jbt_startTrain.setEnabled(true);
        txt_epochs.setEnabled(true);
        txt_reward.setEnabled(true);
        txt_penalty.setEnabled(true);
        txt_alpha.setEnabled(true);
        txt_gamma.setEnabled(true);
        txt_epsilon.setEnabled(true);
        rb_softmax.setEnabled(true);
        rb_greedy.setEnabled(true);
        sarsa.setEnabled(true);
        qlearn.setEnabled(true);

        // disable stop button
        jbt_stopTrain.setEnabled(false);
        System.out.println("endTraining.");
    }

    public void start() {
        m.resume();
    }

    public void stop() {
        m.stop();
    }

    public void run() {
        updateBoard();
        validate();
    }

    private void updateBoard() {
        // update progress info
        if (rlc != null) {
            pb_progress.setValue(rlc.epochsdone);
            lb_learnEpochsDone.setText(Integer.toString(rlc.totaldone));
            if (rlc.newInfo) {
                endTraining();
            }
        }

        lb_win.setText(display.getWinScore());
        lb_lose.setText(display.getLoseScore());
        //System.err.println("lb_lose=" + display.getLoseScore());
        this.repaint();
        //trainPanel.repaint();
        //lb_learnEpochsDone.repaint();
    }

    public static Window findWindow(Component c) {
    if (c == null) {
        return JOptionPane.getRootFrame();
    } else if (c instanceof Window) {
        return (Window) c;
    } else {
        return findWindow(c.getParent());
    }
}

    public void actionPerformed(ActionEvent e) {
        //System.out.println("actionPerformed.");
        if (e.getActionCommand().startsWith(SET_STRATEGY)) {
            String strat = e.getActionCommand().substring(11);
            System.out.println(strat);
            //Strategy s;
            try {
                this.s = (Strategy) Class.forName(strat).newInstance();
                //
            } catch (Exception ex) {
                System.out.println("Cannot create strategy " + strat + ". " + ex);
                return;
            }
            System.out.println(this.s.getClass().getCanonicalName());
        }
    }
}
