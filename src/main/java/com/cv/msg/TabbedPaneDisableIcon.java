/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.msg;

/**
 *
 * @author Lenovo
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Lenovo
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TabbedPaneDisableIcon extends JPanel {

    private final ImageIcon[] disableIcons = {
        new ImageIcon(
        this.getClass().getResource("/images/date.png")),
        new ImageIcon(
        this.getClass().getResource("/images/date.png")),
        new ImageIcon(
        this.getClass().getResource("/images/date.png"))
    };

    public TabbedPaneDisableIcon() {
        initializeUI();
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(500, 200));

        JTabbedPane pane = new JTabbedPane();

        ImageIcon tab1Icon = new ImageIcon(
                this.getClass().getResource("/images/loading_tab.gif"));
        ImageIcon tab2Icon = new ImageIcon(
                this.getClass().getResource("/images/loading_tab.gif"));
        ImageIcon tab3Icon = new ImageIcon(
                this.getClass().getResource("/images/loading_tab.gif"));

        JPanel content1 = new JPanel();
        JButton b = new JButton("Stop");
        b.addActionListener((ActionEvent e) -> {
            for (int i = 0; i < pane.getTabCount(); i++) {
                System.out.println("add");
                pane.setDisabledIconAt(i, null);
                this.add(pane, BorderLayout.CENTER);
            }

            // Disable the last tab to see the disabled icon displayed.
            pane.setEnabledAt(pane.getTabCount() - 1, false);

        });
        content1.add(b);
        JPanel content2 = new JPanel();
        JPanel content3 = new JPanel();

        pane.addTab("Success", tab1Icon, content1);
        pane.addTab("Fail", tab2Icon, content2);
        pane.addTab("Error", tab3Icon, content3);

        for (int i = 0; i < pane.getTabCount(); i++) {
            pane.setDisabledIconAt(i, disableIcons[i]);
        }
        // Disable the last tab to see the disabled icon displayed.
        pane.setEnabledAt(pane.getTabCount() - 1, false);

        this.add(pane, BorderLayout.CENTER);
    }

    public static void showFrame() {
        JPanel panel = new TabbedPaneDisableIcon();
        panel.setOpaque(true);

        JFrame frame = new JFrame("JTabbedPane Demo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TabbedPaneDisableIcon.showFrame();
            }
        });
    }
}
