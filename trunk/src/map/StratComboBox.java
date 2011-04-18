/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Wafdan
 */
public class StratComboBox extends JPanel implements ActionListener, Runnable {

    private String strategy_name = "sp.SinglePointStrategy";
    private final JComboBox com_strat;
    private final Panel spanel;
    private final int gap = 5;
    private Strategy strategy;
    Thread t;
    PGMS pg;

    public StratComboBox(PGMS p) {
        super();
        pg = p;
        spanel = new Panel(new FlowLayout(FlowLayout.LEFT, gap, 0));
        com_strat = new JComboBox(new String[]{"sp.SinglePointStrategy", "eqn.EqnStrategy", "eqn.MioStrategy", "rl.RLStrategy"});
        com_strat.addActionListener(this);
        spanel.add(com_strat);
        add(spanel);
        this.t = new Thread(this);
        this.t.start();
    }

    public void actionPerformed(ActionEvent e) {
        strategy_name = com_strat.getSelectedItem().toString();
        try {
            pg.s = (Strategy) Class.forName(strategy_name).newInstance();
            System.out.println("Created strategy " + pg.s.getClass().getCanonicalName());
            
//            initDisplay();
//            Panel map_panel = new Panel(new FlowLayout(FlowLayout.CENTER, gap, 0));
//            DisplayMap display = new DisplayMap(pg.s, 3, 3, 3, null, null, null, null, null, null);
//            display.setBackground(Color.white);
            //display.init();
            //m = display;		// Save DisplayMap for the start and stop method
//            map_panel.add(display);
//            add(map_panel);
        } catch (Exception ex) {
            System.out.println("Cannot create strategy " + strategy_name);
        }
    }

    public String getStratName() {
        return strategy_name;
    }

    public Strategy getStrategy() {
        System.out.println("Get strategy " + strategy.getClass().getCanonicalName());
        return strategy;
    }

    public void initDisplay() {
        setLayout(new BorderLayout());
        setBackground(Color.lightGray);

        /* The status panel displays status information  */
        final Label status = new Label("Strategy: " + strategy_name,
                Label.CENTER);
        add(status, "South");
    }

    public void run() {
    }
}
