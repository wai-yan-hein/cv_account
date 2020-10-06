/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.Privilege;
import com.cv.accountswing.entity.UserRole;
import com.cv.accountswing.service.PrivilegeService;
import com.cv.accountswing.service.UserRoleService;
import com.cv.accountswing.ui.system.setup.common.JTreeTable;
import com.cv.accountswing.ui.system.setup.common.MenuNode;
import com.cv.accountswing.ui.system.setup.common.MenuSystemModel;
import com.cv.accountswing.ui.system.setup.common.RoleTreeTableModel;
import com.cv.accountswing.ui.system.setup.common.UserRoleTableModel;
import com.cv.accountswing.util.Util1;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class RoleSetup extends javax.swing.JPanel implements KeyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleSetup.class);

    @Autowired
    private UserRoleTableModel userRoleTableModel;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private RoleTreeTableModel roleTreeTableModel;
    private MenuSystemModel treeModel;
    private JTreeTable treeTable;
    private List<Privilege> privilegeList;
    private int selectRow = -1;
    private boolean isShonw = false;

    public void setIsShonw(boolean isShonw) {
        this.isShonw = isShonw;
    }

    /**
     * Creates new form RoleSetup
     */
    public RoleSetup() {
        initComponents();
    }

    private void initMain() {
        initTable();
        initTree();
        searchAllUsers();

    }

    private void initTable() {
        tblRole.setModel(userRoleTableModel);
        tblRole.getTableHeader().setFont(Global.lableFont);
        tblRole.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRole.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblRole.getSelectedRow() >= 0) {
                    selectRow = tblRole.convertRowIndexToModel(tblRole.getSelectedRow());
                    UserRole role = userRoleTableModel.getRole(selectRow);
                    setCurrUserRole(role);
                }
            }
        });
    }

    private void initTree() {
        treeModel = new MenuSystemModel();
        treeTable = new JTreeTable(treeModel);
        privilegeList = new ArrayList<>();

    }

    private void searchAllUsers() {
        List<UserRole> listUserRole = userRoleService.search("-", Global.compId.toString());
        userRoleTableModel.setListRole(listUserRole);
    }

    public void setCurrUserRole(UserRole currUserRole) {
        clearPrivilegeStatus(treeModel.getTreeRoot());

        try {
            //this.currUserRole = (UserRole) dao.find(UserRole.class, currUserRole.getRoleId());
            privilegeList = privilegeService.search(currUserRole.getRoleId().toString(), "-");
            updateStauts(treeModel.getTreeRoot());

        } catch (Exception ex) {
            LOGGER.error("setCurrUserRole : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        AbstractTableModel model = (AbstractTableModel) treeTable.getModel();
        model.fireTableDataChanged();
    }

    private void clearPrivilegeStatus(MenuNode root) {
        Object[] menuNodes = (Object[]) root.getChildren();
        if (menuNodes != null) {
            for (int i = 0; i < menuNodes.length; i++) {
                MenuNode menuNode = (MenuNode) menuNodes[i];
                menuNode.setAllow(false);

                clearPrivilegeStatus(menuNode);
            }
        }
    }

    private void updateStauts(MenuNode root) {
        //RoleDaoImpl roleDao = new RoleDaoImpl();
        Object[] menuNodes = (Object[]) root.getChildren();
        if (menuNodes != null) {
            for (int i = 0; i < menuNodes.length; i++) {
                MenuNode menuNode = (MenuNode) menuNodes[i];
                menuNode.setAllow(Util1.getBoolean(menuNode.getAllow()));
                updateStauts(menuNode);
            }
        }
    }

    /*private boolean getStatus(Integer id) {
    List<Privilege> privilegeListL = privilegeService.search(id.toString(), "-");
    boolean allow = false;
    
    for (int i = 0; i < privilegeListL.size(); i++) {
    if (privilegeListL.get(i).getKey().getMenuId()== id) {
    allow = privilegeListL.get(i).getAllow();
    i = privilegeListL.size();
    }
    }
    
    return allow;
    }*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblRole = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblRole.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblRole.setName("tblRole"); // NOI18N
        jScrollPane1.setViewportView(tblRole);

        jLabel1.setText("Role Name");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollPane))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        if (!isShonw) {
            initMain();
        }
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tblRole;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";

        if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();

        }
        switch (ctrlName) {
            case "tblRole":
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    selectRow = tblRole.convertRowIndexToModel(tblRole.getSelectedRow());
                    UserRole role = userRoleTableModel.getRole(selectRow);
                    setCurrUserRole(role);
                }
                break;
        }
    }
}
