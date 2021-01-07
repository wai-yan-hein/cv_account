/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.util.Util1;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
public class DepartmentSetup extends javax.swing.JPanel implements TreeSelectionListener, MouseListener, KeyListener,
        PanelControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentSetup.class);
    DefaultMutableTreeNode treeRoot;
    DefaultMutableTreeNode child;
    DefaultTreeModel treeModel;
    DefaultMutableTreeNode selectedNode;
    private final String parentRootName = "Department";
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
    @Autowired
    private ApplicationMainFrame mainFrame;

    JPopupMenu popupmenu;
    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        LOGGER.info("Selected Menu : " + menuName);
        switch (menuName) {
            case "New":
                newDepartment();
                break;
            case "Delete":
                deleteDepatment();
                break;
            default:
                break;
        }

    };
    @Autowired
    private DepartmentService deptService;

    /**
     * Creates new form DepartmentSetup
     */
    public DepartmentSetup() {
        initComponents();
        initKeyListener();
        initPopup();
    }

    private void initMain() {
        initTree();
        isShown = true;
    }

    private void initTree() {
        treeModel = (DefaultTreeModel) treeDept.getModel();
        treeModel.setRoot(null);
        treeRoot = new DefaultMutableTreeNode(parentRootName);
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            createTreeNode("#", treeRoot);
            treeModel.setRoot(treeRoot);
            loadingObserver.load(this.getName(), "Stop");
        });

    }

    private void createTreeNode(String parentMenuID, DefaultMutableTreeNode treeRoot) {
        List<Department> deparments = deptService.search("-", "-", "-", "-", parentMenuID);
        deparments.forEach(deparment -> {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(deparment);
            treeRoot.add(root);
            createTreeNode(deparment.getDeptCode(), root);
            /*if (!child.getCode().isEmpty()) {
                }*/
        });

    }

    private void setEnabledControl(boolean status) {
        txtUserCode.setEnabled(status);
        txtName.setEnabled(status);
        chkActive.setEnabled(status);
        btnSave.setEnabled(status);
        btnClear.setEnabled(status);
    }

    private void setDepartment(Department dep) {
        txtSystemCode.setText(dep.getDeptCode());
        txtUserCode.setText(dep.getUsrCode());
        txtName.setText(dep.getDeptName());
        chkActive.setSelected(Util1.getBoolean(dep.getActive()));
        labelStatus.setText("EDIT");
    }

    public void clear() {
        txtSystemCode.setText(null);
        txtUserCode.setText(null);
        txtName.setText(null);
        chkActive.setSelected(Boolean.FALSE);
        labelStatus.setText("NEW");
        Global.listDepartment = deptService.search("-", "-", Global.compCode, "-", "-");
        //txtUserCode.requestFocus();
    }

    private void initPopup() {
        popupmenu = new JPopupMenu();
        JMenuItem cut = new JMenuItem("New");
        JMenuItem copy = new JMenuItem("Delete");
        cut.addActionListener(menuListener);
        copy.addActionListener(menuListener);
        popupmenu.add(cut);
        popupmenu.add(copy);

    }

    private void initKeyListener() {
        txtName.addKeyListener(this);
        txtSystemCode.addKeyListener(this);
        txtUserCode.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnClear.addKeyListener(this);
        btnSave.addKeyListener(this);
        txtUserCode.requestFocus();
        treeDept.addMouseListener(this);
        treeDept.addTreeSelectionListener(this);
        treeDept.addKeyListener(this);

    }

    private void deleteDepatment() {
        try {
            if (isValidDelete()) {
                treeModel.removeNodeFromParent(selectedNode);
                //treeModel.nodesWereRemoved(child, childIndices, removedChildren);
                treeModel.reload(selectedNode);
            }
        } catch (Exception e) {
            LOGGER.error("Delete Department :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Delete Department", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidDelete() {
        boolean status = false;
        Department dep = (Department) selectedNode.getUserObject();
        int showConfirmDialog = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete");
        if (showConfirmDialog == JOptionPane.OK_OPTION) {
            if (dep.getDeptCode() != null) {
                int delete = deptService.delete(dep.getDeptCode());
                if (delete == 1) {
                    clear();
                    status = true;
                } else {
                    JOptionPane.showMessageDialog(Global.parentForm, "Can't delete.");
                    status = false;
                }
            } else {
                status = true;
            }
        }
        return status;
    }

    private void newDepartment() {
        Department dep = new Department();
        dep.setDeptName("New Department");
        child = new DefaultMutableTreeNode(dep);
        selectedNode.add(child);
        treeModel.insertNodeInto(child, selectedNode, selectedNode.getChildCount() - 1);
        //treeModel.reload();

    }

    private void saveDepartment() {
        Department department = new Department();
        department.setDeptCode(txtSystemCode.getText());
        department.setDeptName(txtName.getText());
        department.setUsrCode(txtUserCode.getText());
        department.setActive(chkActive.isSelected());
        department.setCompCode(Global.compCode);
        department.setMacId(Global.machineId);
        if (isValidDepartment(department, Global.loginUser.getUserCode(), Global.compCode)) {
            Department dep = deptService.save(department);
            if (dep != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if (labelStatus.getText().equals("EDIT")) {
                    selectedNode.setUserObject(dep);
                    clear();
                    setEnabledControl(false);
                    treeModel.reload(selectedNode);
                }
            }
        }
    }

    private boolean isValidDepartment(Department dept,
            String userId, String compCode) {
        boolean status = true;

        if (Util1.isNull(dept.getDeptName(), "-").equals("-")) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid department code.");
        } else {

            if (dept.getDeptCode().isEmpty()) {
                //dept.setDeptCode(getDeptCode(compCode));
                dept.setCreatedBy(userId);
                dept.setCreatedDt(Util1.getTodayDate());
            } else {
                if (labelStatus.getText().equals("NEW")) {
                    List<Department> listDept = deptService.search(dept.getDeptCode(),
                            "-", compCode, "-", "-");
                    if (listDept != null) {
                        if (listDept.size() > 0) {
                            status = false;
                            JOptionPane.showMessageDialog(Global.parentForm, "Duplicate department code.");
                        }
                    }
                }
            }
            dept.setUpdatedBy(userId);
            dept.setUpdatedDt(Util1.getTodayDate());
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedNode.getParent();
            Object userObject = node.getUserObject();
            if (userObject.toString().equals(parentRootName)) {
                dept.setParentDept("#");
            } else {
                Department dep = (Department) userObject;
                dept.setParentDept(dep.getDeptCode());
            }

        }

        return status;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSystemCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtUserCode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        btnSave = new javax.swing.JButton();
        labelStatus = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeDept = new javax.swing.JTree();

        jLabel4.setText("jLabel4");

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont        );
        jLabel1.setText("System Code");

        txtSystemCode.setEditable(false);
        txtSystemCode.setFont(Global.textFont);
        txtSystemCode.setName("txtSystemCode"); // NOI18N

        jLabel2.setFont(Global.lableFont        );
        jLabel2.setText("User Code");

        txtUserCode.setFont(Global.textFont);
        txtUserCode.setEnabled(false);
        txtUserCode.setName("txtUserCode"); // NOI18N

        jLabel3.setFont(Global.lableFont        );
        jLabel3.setText("Name");

        txtName.setFont(Global.textFont);
        txtName.setEnabled(false);
        txtName.setName("txtName"); // NOI18N
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });

        chkActive.setFont(Global.lableFont        );
        chkActive.setText("Active");
        chkActive.setEnabled(false);
        chkActive.setName("chkActive"); // NOI18N

        btnSave.setBackground(ColorUtil.mainColor);
        btnSave.setFont(Global.lableFont        );
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setEnabled(false);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        labelStatus.setFont(Global.lableFont        );
        labelStatus.setText("NEW");

        btnClear.setBackground(ColorUtil.btnEdit);
        btnClear.setFont(Global.lableFont        );
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clear-button-white.png"))); // NOI18N
        btnClear.setText("Clear");
        btnClear.setEnabled(false);
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtName)
                    .addComponent(txtUserCode)
                    .addComponent(txtSystemCode)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 58, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear))
                    .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtSystemCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUserCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(labelStatus)
                    .addComponent(btnClear))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        treeDept.setFont(Global.textFont);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("color");
        treeNode1.add(treeNode2);
        treeDept.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeDept.setName("treeDept"); // NOI18N
        jScrollPane2.setViewportView(treeDept);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            saveDepartment();
        } catch (Exception e) {
            LOGGER.error("Save Department :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Save Department", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        txtUserCode.requestFocus();

    }//GEN-LAST:event_formComponentShown

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        // TODO add your handling code here:
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JTree treeDept;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSystemCode;
    private javax.swing.JTextField txtUserCode;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selectedNode = (DefaultMutableTreeNode) treeDept.getLastSelectedPathComponent();
        if (selectedNode != null) {
            if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                Department dep = (Department) selectedNode.getUserObject();
                setDepartment(dep);
                setEnabledControl(true);
            } else {
                clear();
                setEnabledControl(false);
            }
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            popupmenu.show(this, e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

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

        if (sourceObj instanceof JTree) {
            ctrlName = ((JTree) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtSystemCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtUserCode.requestFocus();
                }
                break;
            case "txtUserCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtSystemCode.requestFocus();
                }
                tabToTree(e);
                break;
            case "txtName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtUserCode.requestFocus();
                }
                tabToTree(e);
                break;
            case "chkActive":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnSave.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtName.requestFocus();
                }
                tabToTree(e);
                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnClear.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    chkActive.requestFocus();
                }
                tabToTree(e);
                break;
            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtUserCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
                tabToTree(e);
                break;
            case "treeDept":
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    if (selectedNode != null) {
                        if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                            Department dep = (Department) selectedNode.getUserObject();
                            setDepartment(dep);
                            setEnabledControl(true);
                        } else {
                            clear();
                            setEnabledControl(false);
                        }
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtUserCode.requestFocus();
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtUserCode.requestFocus();
                }

        }
    }

    private void tabToTree(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            treeDept.requestFocus();
        }
    }

    @Override
    public void save() {
        saveDepartment();
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
        clear();
        isShown = false;
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        initTree();
    }

}
