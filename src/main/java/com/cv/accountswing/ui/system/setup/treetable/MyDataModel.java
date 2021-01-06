/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup.treetable;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Privilege;
import com.cv.accountswing.entity.PrivilegeKey;
import com.cv.accountswing.entity.view.VRoleMenu;
import com.cv.accountswing.service.PrivilegeService;
import java.util.List;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class MyDataModel extends MyAbstractTreeTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyDataModel.class);

    // Spalten Name.
    static protected String[] columnNames = {"Name", "Type", "Allow"};
    private final PrivilegeService privilegeService;
    private SelectionObserver observer;
    // Spalten Typen.
    static protected Class<?>[] columnTypes = {MyTreeTableModel.class, String.class, Boolean.class};

    public MyDataModel(VRoleMenu rootNode, PrivilegeService privilegeService, SelectionObserver observer) {
        super(rootNode);
        root = rootNode;
        this.privilegeService = privilegeService;
        this.observer = observer;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((VRoleMenu) parent).getChild().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        List<VRoleMenu> child = ((VRoleMenu) parent).getChild();
        return (child == null) ? 0 : child.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return columnTypes[column];
    }

    @Override
    public Object getValueAt(Object node, int column) {
        switch (column) {
            case 0:
                return ((VRoleMenu) node).getMenuName();
            case 1:
                return ((VRoleMenu) node).getMenuType();
            case 2:
                return ((VRoleMenu) node).getIsAllow();
            default:
                break;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return true; // Important to activate TreeExpandListener
    }

    @Override
    public void setValueAt(Object aValue, Object node, int column) {
        switch (column) {
            case 2:
                if (aValue != null) {
                    if (node instanceof VRoleMenu) {
                        VRoleMenu roleMenu = (VRoleMenu) node;
                        if (roleMenu.getChild() != null) {
                            setAllow(roleMenu.getChild(), (Boolean) aValue);
                        }
                        roleMenu.setIsAllow((Boolean) aValue);
                        savePrivilege(roleMenu, (Boolean) aValue);

                    }
                }
                break;
            default:
                break;
        }

    }

    private void setAllow(List<VRoleMenu> parent, boolean allow) {
        parent.forEach(child -> {
            if (child.getChild() != null) {
                child.setIsAllow(allow);
                savePrivilege(child, allow);
                setAllow(child.getChild(), allow);
            } else {
                child.setIsAllow(allow);
            }
        });
    }

    private void savePrivilege(VRoleMenu roleMenu, boolean allow) {
        try {
            if (roleMenu.getKey() != null) {
                PrivilegeKey key = new PrivilegeKey(roleMenu.getKey().getRoleCode(), roleMenu.getKey().getMenuCode());
                Privilege privilege = new Privilege();
                privilege.setKey(key);
                privilege.setIsAllow(allow);
                privilegeService.save(privilege);
                if (observer != null) {
                    observer.selected("CHANGE", "-");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Save Priviliges  :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Permission Allow.", JOptionPane.ERROR_MESSAGE);
        }
    }

}
