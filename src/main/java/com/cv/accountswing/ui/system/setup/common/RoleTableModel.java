/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup.common;

import com.cv.accountswing.entity.view.VRoleMenu;

/**
 *
 * @author Lenovo
 */
public class RoleTableModel extends RoleAbstractTreeTableModel {

    // Spalten Name.
    static protected String[] columnNames = {"Menu Name", "System", "Acive"};

    // Spalten Typen.
    static protected Class<?>[] columnTypes = {RoleTreeTableModel.class, String.class, Boolean.class};

    public RoleTableModel(RoleTableModel rootNode) {
        super(rootNode);
        //root = rootNode;
    }

    public Object getChild(Object parent, int index) {
        return ((VRoleMenu) parent).getChild().get(index);
    }

    public int getChildCount(Object parent) {
        return ((VRoleMenu) parent).getChild().size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }

    public Class<?> getColumnClass(int column) {
        return columnTypes[column];
    }

    public Object getValueAt(Object node, int column) {
        switch (column) {
            case 0:
                return ((VRoleMenu) node).getMenuName();
            case 1:
                return ((VRoleMenu) node).getMenuClass();
            case 2:
                return true;
            default:
                break;
        }
        return null;
    }

    public boolean isCellEditable(Object node, int column) {
        return true; // Important to activate TreeExpandListener
    }

    public void setValueAt(Object aValue, Object node, int column) {
    }

}
//The class is a simple value object with getter and setter that stores the data of a node. MyDataNode

