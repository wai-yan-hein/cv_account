/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup.common;

import com.cv.accountswing.entity.Menu;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class RoleTreeTableModel implements TreeTableModel {

    private String[] columnNames = {"Name", "Type", "Allow"};

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return Object.class;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        MenuNode menuNode = new MenuNode(new Menu(1, "Best-System", "System"));
        DefaultMutableTreeNode dNode = new DefaultMutableTreeNode(menuNode);
        dNode.add(new DefaultMutableTreeNode(menuNode));
        return dNode;
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, Object node, int column) {
    }

    @Override
    public Object getRoot() {
        return new MenuNode(new Menu(1, "Best-System", "System"));
    }

    @Override
    public Object getChild(Object parent, int index) {
        return new MenuNode(new Menu(1, "Best-System", "System"));
    }

    @Override
    public int getChildCount(Object parent) {
        return 5;
    }

    @Override
    public boolean isLeaf(Object node) {
        return false;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        for (int i = 0; i < getChildCount(parent); i++) {
            if (getChild(parent, i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }

}
