/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.AppUser;
import com.cv.accountswing.entity.CompanyInfo;
import com.cv.accountswing.entity.UserRole;
import com.cv.accountswing.entity.UsrCompRole;
import com.cv.accountswing.entity.UsrCompRoleKey;
import com.cv.accountswing.entity.view.VUsrCompRole;
import com.cv.accountswing.service.CompanyInfoService;
import com.cv.accountswing.service.UserRoleService;
import com.cv.accountswing.service.UserService;
import com.cv.accountswing.service.UsrCompRoleService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.setup.common.UserTableModel;
import com.cv.accountswing.ui.setup.common.UsrCompRoleTableModel;
import com.cv.accountswing.util.BindingUtil;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
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
public class RoleAssignSetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    private int selectRow = -1;
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleAssignSetup.class);

    @Autowired
    private UserTableModel userTableModel;
    @Autowired
    private UsrCompRoleTableModel compRoleTableModel;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyInfoService compInfoService;
    @Autowired
    private UsrCompRoleService usrCompRoleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private LoadingObserver loadingObserver;
    private boolean isShown = false;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    @Autowired
    private TaskExecutor taskExecutor;

    /**
     * Creates new form RoleAssignSetup
     */
    public RoleAssignSetup() {
        initComponents();
    }

    private void initMain() {
        loadingObserver.load(this.getName(), "Start");
        initKeyListener();
        initCombo();
        initTable();
        isShown = true;
    }

    private void initCombo() {
        List<CompanyInfo> listComInfo = compInfoService.search("-", "-", "-", "-", "-", "-");
        List<UserRole> listUserRole = userRoleService.search("-", Global.compId.toString());
        BindingUtil.BindComboFilter(cboComInfo, listComInfo, null, true, false);
        BindingUtil.BindComboFilter(cboRole, listUserRole, null, true, false);

    }

    private void initTable() {
        tblUser.setModel(userTableModel);
        tblRoleAssign.setModel(compRoleTableModel);
        tblUser.getTableHeader().setFont(Global.textFont);
        tblRoleAssign.getTableHeader().setFont(Global.textFont);
        tblUser.setDefaultRenderer(Object.class, new TableCellRender());
        tblRoleAssign.setDefaultRenderer(Double.class, new TableCellRender());
        tblRoleAssign.setDefaultRenderer(Object.class, new TableCellRender());
        tblUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUser.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblUser.getSelectedRow() >= 0) {
                    selectRow = tblUser.convertRowIndexToModel(tblUser.getSelectedRow());
                    AppUser user = userTableModel.getUser(selectRow);
                    getAssignRole(user.getUserId());

                }

            }
        });
        taskExecutor.execute(() -> {
            List<AppUser> listUser = userService.search("-", "-", "-", "-");
            userTableModel.setListUser(listUser);
            loadingObserver.load(this.getName(), "Stop");
        });
    }

    private void initKeyListener() {
        tblRoleAssign.addKeyListener(this);
        tblUser.addKeyListener(this);
        btnAdd.addKeyListener(this);

        cboComInfo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cboRole.requestFocus();
                }
                tabTotblRoleAssign(e);

            }

        });
        cboRole.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnAdd.requestFocus();
                }
                tabTotblRoleAssign(e);

            }
        });
    }

    private void getAssignRole(int userId) {
        List assignCompany = usrCompRoleService.getAssignRole(String.valueOf(userId));
        List<VUsrCompRole> listUCR = (List<VUsrCompRole>) (List<?>) assignCompany;
        compRoleTableModel.setListUCR(listUCR);
        btnAdd.setEnabled(true);
    }

    private void saveRoleAssign() {
        AppUser user = userTableModel.getUser(selectRow);
        CompanyInfo cInfo = (CompanyInfo) cboComInfo.getSelectedItem();
        UserRole uRole = (UserRole) cboRole.getSelectedItem();
        UsrCompRoleKey key = new UsrCompRoleKey();
        key.setCompCode(cInfo.getCompId());
        key.setRoleId(uRole.getRoleId());
        key.setUserId(user.getUserId());
        UsrCompRole ucr = new UsrCompRole();
        ucr.setKey(key);

        UsrCompRole save = usrCompRoleService.save(ucr);
        if (save != null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Saved");
            VUsrCompRole vucr = new VUsrCompRole();
            vucr.setCompName(cInfo.getName());
            vucr.setRoleName(uRole.getRoleName());
            compRoleTableModel.addVUsrCompRole(vucr);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblUser = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboComInfo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cboRole = new javax.swing.JComboBox<>();
        btnAdd = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRoleAssign = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblUser.setFont(Global.textFont);
        tblUser.setModel(new javax.swing.table.DefaultTableModel(
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
        tblUser.setName("tblUser"); // NOI18N
        tblUser.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblUser);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Company");

        cboComInfo.setFont(Global.textFont);
        cboComInfo.setName("cboComInfo"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Role");

        cboRole.setFont(Global.textFont);
        cboRole.setName("cboRole"); // NOI18N

        btnAdd.setFont(Global.lableFont);
        btnAdd.setText("Add");
        btnAdd.setEnabled(false);
        btnAdd.setName("btnAdd"); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        tblRoleAssign.setFont(Global.textFont);
        tblRoleAssign.setModel(new javax.swing.table.DefaultTableModel(
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
        tblRoleAssign.setName("tblRoleAssign"); // NOI18N
        tblRoleAssign.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblRoleAssign);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboRole, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboComInfo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnAdd))))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboComInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cboRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        try {
            saveRoleAssign();
        } catch (Exception e) {
            LOGGER.error("Save RoleAssign :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Save RoleAssign", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JComboBox<String> cboComInfo;
    private javax.swing.JComboBox<String> cboRole;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblRoleAssign;
    private javax.swing.JTable tblUser;
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
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JComboBox) {
            ctrlName = ((JComboBox) sourceObj).getName();
        }
        switch (ctrlName) {
            case "tblRoleAssign":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnAdd.requestFocus();
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    tblUser.requestFocus();
                    if (tblUser.getRowCount() >= 0) {
                        tblUser.setRowSelectionInterval(0, 0);
                    }
                }
                break;
            case "tblUser":
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    selectRow = tblUser.convertRowIndexToModel(tblUser.getSelectedRow());
                    AppUser user = userTableModel.getUser(selectRow);
                    getAssignRole(user.getUserId());
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    cboComInfo.requestFocus();
                }
                break;
            case "btnAdd":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    cboComInfo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    cboRole.requestFocus();
                }
                break;

        }
    }

    private void tabTotblRoleAssign(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblRoleAssign.requestFocus();
            if (tblRoleAssign.getRowCount() >= 0) {
                tblRoleAssign.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void save() {
        saveRoleAssign();
    }
}
