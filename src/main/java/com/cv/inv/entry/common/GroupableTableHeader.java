/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.common;

import java.util.*;
import javax.swing.table.*;

/**
 * GroupableTableHeader
 *
 * @version 1.1 2010/10/23
 * @author Nobuo Tamemasa (modified by Q)
 */
public class GroupableTableHeader extends JTableHeader {

    private static final String uiClassID = "GroupableTableHeaderUI";
    protected Vector columnGroups = null;

    public GroupableTableHeader(TableColumnModel model) {
        super(model);
        setUI(new GroupableTableHeaderUI());
        setReorderingAllowed(false);
    }

    @Override
    public void updateUI() {
        setUI(new GroupableTableHeaderUI());
    }

    @Override
    public void setReorderingAllowed(boolean b) {
        reorderingAllowed = false;
    }

    public void addColumnGroup(ColumnGroup g) {
        if (columnGroups == null) {
            columnGroups = new Vector();
        }
        columnGroups.addElement(g);
    }

    public Enumeration getColumnGroups(TableColumn col) {
        if (columnGroups == null) {
            return null;
        }
        Enumeration e = columnGroups.elements();
        while (e.hasMoreElements()) {
            ColumnGroup cGroup = (ColumnGroup) e.nextElement();
            Vector v_ret = (Vector) cGroup.getColumnGroups(col, new Vector());
            if (v_ret != null) {
                return v_ret.elements();
            }
        }
        return null;
    }

    public void setColumnMargin() {
        if (columnGroups == null) {
            return;
        }
        int columnMargin = getColumnModel().getColumnMargin();
        Enumeration e = columnGroups.elements();
        while (e.hasMoreElements()) {
            ColumnGroup cGroup = (ColumnGroup) e.nextElement();
            cGroup.setColumnMargin(columnMargin);
        }
    }
}
