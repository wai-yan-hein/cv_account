/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.UserRole;
import com.cv.accountswing.entity.view.VRoleMenu;
import com.cv.accountswing.service.MenuService;
import com.cv.accountswing.service.PrivilegeService;
import com.cv.accountswing.service.UserRoleService;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.system.setup.common.UserRoleTableModel;
import com.cv.accountswing.ui.system.setup.treetable.MyAbstractTreeTableModel;
import com.cv.accountswing.ui.system.setup.treetable.MyDataModel;
import com.cv.accountswing.ui.system.setup.treetable.MyTreeTable;
import com.cv.accountswing.util.Util1;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
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
    private MenuService menuService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private TaskExecutor taskExecutor;

    private int selectRow = -1;
    private boolean isShown = false;

    public boolean isIsShown() {
        return isShown;
    }

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
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
        tblRole.setDefaultRenderer(Object.class, new TableCellRender());
        tblRole.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
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
        //createTree(Global.roleId.toString());
    }

    private void createTree(String roleId) {
        JDialog loading = Util1.getLoading(Global.parentForm);
        taskExecutor.execute(() -> {
            List<VRoleMenu> listVRM = menuService.getParentChildMenu(roleId);
            VRoleMenu vRoleMenu = new VRoleMenu("Best-System", "System", false, listVRM);
            MyAbstractTreeTableModel treeTableModel = new MyDataModel(vRoleMenu, privilegeService);
            MyTreeTable treeTable = new MyTreeTable(treeTableModel);
            scrollPane.getViewport().add(treeTable);
            loading.setVisible(false);
        });
        loading.setVisible(true);
    }

    private void searchAllUsers() {
        List<UserRole> listUserRole = userRoleService.search("-", Global.compId.toString());
        userRoleTableModel.setListRole(listUserRole);
        userRoleTableModel.addEmptyRow();
    }

    public void setCurrUserRole(UserRole currUserRole) {
        txtRoleName.setText(currUserRole.getRoleName());
        if (currUserRole.getRoleId() != null) {
            createTree(currUserRole.getRoleId().toString());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblRole = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtRoleName = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblRole.setAutoCreateRowSorter(true);
        tblRole.setFont(Global.textFont);
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
        tblRole.setInheritsPopupMenu(true);
        tblRole.setName("tblRole"); // NOI18N
        tblRole.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblRole);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Role Name");

        txtRoleName.setEditable(false);
        txtRoleName.setFont(Global.lableFont);

        scrollPane.setFont(Global.textFont);

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
                        .addComponent(txtRoleName, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtRoleName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollPane))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
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
        if (!isShown) {
            initMain();
        }
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tblRole;
    private javax.swing.JTextField txtRoleName;
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
