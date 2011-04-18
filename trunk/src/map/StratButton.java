/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package map;

import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Wafdan
 */
public class StratButton extends JButton implements ActionListener{
    JButton but;
    PGMS pg;
    public StratButton(PGMS p) {
        but = new JButton("Set Strategy");
        add(but);
    }



    public void actionPerformed(ActionEvent e) {
        
    }

}
