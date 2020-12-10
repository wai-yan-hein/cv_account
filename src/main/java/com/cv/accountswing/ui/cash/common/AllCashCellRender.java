/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash.common;

import com.cv.accountswing.util.Util1;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lenovo
 */
public class AllCashCellRender extends DefaultTableCellRenderer {

    private final JCheckBox check = new JCheckBox();
    private JLabel labelIcon;
    private JLabel labelText;
    private JPanel panel;
    private final Color bgColor = new Color(232, 232, 232);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(row % 2 == 0 ? bgColor : Color.WHITE);
        /* if (table.isColumnSelected(column)) {
        c.setBackground(new Color(38, 117, 191));
        }*/
        if (isSelected) {
            c.setBackground(new Color(62, 178, 194));

        }

        String s;
        if (value instanceof Double) {
            DecimalFormat dFormat = new DecimalFormat("#,##0.###");
            Double d = (Double) value;
            s = dFormat.format(d);
            c = getTableCellRendererComponent(table, s,
                    isSelected, hasFocus, row, column);
            ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        if (value instanceof Boolean) {
            check.setSelected((Boolean) value);
            check.setHorizontalAlignment(SwingConstants.CENTER);
            check.setBackground(c.getBackground());
            c = check;
        }
        if (column == 1) {
            labelIcon = new JLabel(UIManager.getIcon("Table.ascendingSortIcon"));
            labelIcon.setHorizontalAlignment(SwingConstants.RIGHT);
            labelText = new JLabel(Util1.isNullObj(value, ""));
            labelText.setHorizontalAlignment(SwingConstants.LEFT);
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.add(labelText);
            panel.add(labelIcon);
            c = panel;
        }

        return c;
    }

}
