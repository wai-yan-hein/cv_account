/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Menu;
import com.cv.accountswing.entity.Privilege;
import com.cv.accountswing.entity.PrivilegeKey;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.MenuService;
<<<<<<< HEAD
import com.cv.accountswing.ui.ApplicationMainFrame;
=======
import com.cv.accountswing.service.PrivilegeService;
>>>>>>> f99da13e2a9811307e724bc8dad573eec9c24135
import com.cv.accountswing.util.BindingUtil;
import com.cv.accountswing.util.Util1;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class ChartOfAccountSetup extends javax.swing.JPanel implements MouseListener, TreeSelectionListener, KeyListener,
        PanelControl {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ChartOfAccountSetup.class);
    private int count = 0;
    DefaultMutableTreeNode selectedNode;
    DefaultTreeModel treeModel;
    private final String parentRootName = "Core Account";
    private DefaultMutableTreeNode treeRoot;
    @Autowired
    COAService coaServcie;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private MenuService menuService;
    @Autowired
<<<<<<< HEAD
    private ApplicationMainFrame mainFrame;
=======
    private PrivilegeService privilegeService;
>>>>>>> f99da13e2a9811307e724bc8dad573eec9c24135
    JPopupMenu popupmenu;
    private LoadingObserver loadingObserver;
    private HashMap<String, Menu> hmMenu = new HashMap<>();
    private boolean isShown = false;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        LOGGER.info("Selected Menu : " + menuName);
        switch (menuName) {
            case "New":
                newCOA();
                break;
            case "Delete":
                deleteCOA();
                break;
            default:
                break;
        }

    };

    /**
     * Creates new form COASetup
     */
    public ChartOfAccountSetup() {
        initComponents();
        initKeyListener();
        initPopup();
    }

    private void initMain() {
        initTree();
        initCombo();
        isShown = true;
    }

    private void initCombo() {
        List<Menu> listMenu = menuService.getParentChildMenu();
        BindingUtil.BindCombo(cboMenu, listMenu, null, false);
        listMenu.forEach((menu) -> {
            if (menu.getChild() != null) {
                menu.getChild().stream().filter(m -> (m.getSoureAccCode() != null)).forEachOrdered(m -> {
                    hmMenu.put(m.getSoureAccCode(), menu);
                });
            }
        });
        cboMenu.setSelectedItem(null);

    }

    private void newCOA() {
        ChartOfAccount coa = new ChartOfAccount();
        coa.setCoaNameEng("New Chart of Account");
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(coa);
        if (selectedNode != null) {
            selectedNode.add(child);
            treeModel.insertNodeInto(child, selectedNode, selectedNode.getChildCount() - 1);

        }
    }

    private void saveChartAcc() {
        String parentCode;
        String option;
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
        if (parentNode != null) {
            Object userObject = parentNode.getUserObject();
            if (userObject.toString().equals(parentRootName)) {
                parentCode = "#";
                option = "SYS";
            } else {
                ChartOfAccount pCoa = (ChartOfAccount) parentNode.getUserObject();
                parentCode = pCoa.getCode();
                option = "USR";
            }
            int level = selectedNode.getLevel();
            ChartOfAccount coa = new ChartOfAccount();
            coa.setCode(txtSysCode.getText());
            coa.setCoaNameEng(txtName.getText());
            coa.setCoaCodeUsr(txtUsrCode.getText());
            coa.setCompCode(Global.compId);
            coa.setParent(parentCode);
            coa.setLevel(level);
            coa.setCreatedBy(Global.loginUser.getUserId().toString());
            coa.setCreatedDate(Util1.getTodayDate());
            coa.setOption(option);
            coa.setActive(chkActive.isSelected());
            ChartOfAccount coaSave = coaServcie.save(coa);
            if (coaSave != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if (lblStatus.getText().equals("EDIT")) {
                    selectedNode.setUserObject(coaSave);
                    treeModel.reload(selectedNode);
                    setEnabledControl(false);
                    clear();
                }
            }
        }

    }

    private void deleteCOA() {
        try {
            if (selectedNode != null) {
                ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
                if (coa != null) {
                    String code = coa.getCode();
                    coaServcie.delete(code, Global.compId.toString());
                }
                treeModel.removeNodeFromParent(selectedNode);
                treeModel.reload(selectedNode);
            }
        } catch (Exception e) {
            LOGGER.error("Delete ChartOfAccount :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Delete ChartOfAccount", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initPopup() {
        popupmenu = new JPopupMenu("Edit");
        JMenuItem cut = new JMenuItem("New");
        JMenuItem copy = new JMenuItem("Delete");
        cut.addActionListener(menuListener);
        copy.addActionListener(menuListener);
        popupmenu.add(cut);
        popupmenu.add(copy);

    }

    private void initTree() {

        treeModel = (DefaultTreeModel) treeCOA.getModel();
        treeModel.setRoot(null);
        treeRoot = new DefaultMutableTreeNode(parentRootName);
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            createTreeNode("#", treeRoot);
            treeModel.setRoot(treeRoot);
            loadingObserver.load(this.getName(), "Stop");
        });

        //treMenu.addPropertyChangeListener(propertyChangeListener);
    }

    private void createTreeNode(String parentMenuID, DefaultMutableTreeNode treeRoot) {
        List<ChartOfAccount> listChild = coaServcie.getChild(Global.compId.toString(), parentMenuID);
        listChild.forEach(child -> {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(child);
            treeRoot.add(root);
            createTreeNode(child.getCode(), root);
        });

    }

    private void setCOA(ChartOfAccount coa) {
        setEnabledControl(true);
        txtSysCode.setText(coa.getCode());
        txtName.setText(coa.getCoaNameEng());
        txtUsrCode.setText(coa.getCoaCodeUsr());
        chkActive.setSelected(Util1.getBoolean(coa.isActive()));
        lblStatus.setText("EDIT");
        if (coa.getLevel() == 3) {
            btnCreate.setEnabled(true);
            Menu menu = hmMenu.get(coa.getCode());
            cboMenu.setSelectedItem(menu == null ? null : menu);
        } else {
            btnCreate.setEnabled(false);
        }
    }

    public void clear() {
        txtSysCode.setText(null);
        txtName.setText(null);
        txtUsrCode.setText(null);
        chkActive.setSelected(false);
        treeCOA.requestFocus();
    }

    private void initKeyListener() {
        txtName.addKeyListener(this);
        txtSysCode.addKeyListener(this);
        txtUsrCode.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnSave.addKeyListener(this);
        treeCOA.addKeyListener(this);
        btnClear.addKeyListener(this);
        treeCOA.addMouseListener(this);
        treeCOA.addTreeSelectionListener(this);

    }

    private void setEnabledControl(boolean status) {
        txtUsrCode.setEnabled(status);
        txtName.setEnabled(status);
        btnSave.setEnabled(status);
        btnClear.setEnabled(status);
        chkActive.setEnabled(status);

    }

    private void saveMenu() {
        try {
            if (cboMenu.getSelectedItem() != null) {
                if (cboMenu.getSelectedItem() instanceof Menu) {
                    ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
                    Menu selectMenu = (Menu) cboMenu.getSelectedItem();
                    Menu menu = new Menu();
                    menu.setMenuName(coa.getCoaNameEng());
                    menu.setMenuClass(selectMenu.getMenuClass());
                    menu.setParent(selectMenu.getId().toString());
                    menu.setSoureAccCode(coa.getCode());
                    Menu saveMenu = menuService.saveMenu(menu);
                    if (saveMenu != null) {
                        Integer menuId = saveMenu.getId();
                        Privilege p = new Privilege();
                        PrivilegeKey key = new PrivilegeKey(Global.roleId, menuId);
                        p.setKey(key);
                        p.setIsAllow(Boolean.FALSE);
                        privilegeService.save(p);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            LOGGER.info("Save Menu :" + e.getMessage());
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
        treeCOA = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSysCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtUsrCode = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        btnSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();
        panelMenu = new javax.swing.JPanel();
        cboMenu = new javax.swing.JComboBox<>();
        btnCreate = new javax.swing.JButton();

        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                formComponentRemoved(evt);
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        treeCOA.setFont(Global.textFont);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Root");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("color");
        treeNode1.add(treeNode2);
        treeCOA.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeCOA.setName("treeCOA"); // NOI18N
        jScrollPane1.setViewportView(treeCOA);

        jPanel1.setFont(Global.textFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("System Code");

        txtSysCode.setFont(Global.textFont);
        txtSysCode.setEnabled(false);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("User Code");

        txtUsrCode.setFont(Global.textFont);
        txtUsrCode.setEnabled(false);
        txtUsrCode.setName("txtUsrCode"); // NOI18N

        txtName.setFont(Global.textFont);
        txtName.setEnabled(false);
        txtName.setName("txtName"); // NOI18N
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Name");

        chkActive.setFont(Global.lableFont);
        chkActive.setText("Active");
        chkActive.setEnabled(false);
        chkActive.setName("chkActive"); // NOI18N

        btnSave.setFont(Global.textFont);
        btnSave.setText("Save");
        btnSave.setEnabled(false);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        btnClear.setFont(Global.textFont);
        btnClear.setText("Clear");
        btnClear.setEnabled(false);
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        panelMenu.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Menu Group Mapping", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        cboMenu.setEditable(true);
        cboMenu.setFont(Global.textFont);
        cboMenu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboMenuItemStateChanged(evt);
            }
        });
        cboMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMenuActionPerformed(evt);
            }
        });

        btnCreate.setFont(Global.lableFont);
        btnCreate.setText("Create");
        btnCreate.setEnabled(false);
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMenuLayout = new javax.swing.GroupLayout(panelMenu);
        panelMenu.setLayout(panelMenuLayout);
        panelMenuLayout.setHorizontalGroup(
            panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboMenu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCreate)
                .addContainerGap())
        );
        panelMenuLayout.setVerticalGroup(
            panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCreate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName)
                            .addComponent(txtUsrCode)
                            .addComponent(txtSysCode)
                            .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtSysCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUsrCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkActive)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            save();
        } catch (Exception e) {
            LOGGER.error("Save Account Group :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Save Account Group", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        txtUsrCode.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
        // TODO add your handling code here:
        LOGGER.info("FOCUS LOST");
    }//GEN-LAST:event_formFocusLost

    private void formComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_formComponentRemoved
        // TODO add your handling code here:
        LOGGER.info("COMPONENT REMOVED");
    }//GEN-LAST:event_formComponentRemoved

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
        LOGGER.info("COMPONENT RESIZED");

    }//GEN-LAST:event_formComponentResized

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        // TODO add your handling code here:
        LOGGER.info("COMPONENT MOVED");

    }//GEN-LAST:event_formComponentMoved

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        // TODO add your handling code here:
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained

    private void cboMenuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboMenuItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboMenuItemStateChanged

    private void cboMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboMenuActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        saveMenu();
    }//GEN-LAST:event_btnCreateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cboMenu;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelMenu;
    private javax.swing.JTree treeCOA;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSysCode;
    private javax.swing.JTextField txtUsrCode;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            popupmenu.show(this, e.getX(), e.getY());
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selectedNode = (DefaultMutableTreeNode) treeCOA.getLastSelectedPathComponent();
        if (selectedNode != null) {
            if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
                setCOA(coa);
            } else {
                clear();
                setEnabledControl(false);
            }
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
        //LOGGER.info("Control Name Key Released:" + ctrlName);
        switch (ctrlName) {
            case "txtUsrCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
                tabToTree(e);
                break;
            case "txtName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtUsrCode.requestFocus();
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
                    txtUsrCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
                tabToTree(e);
                break;
            case "treeCOA":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtUsrCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    if (selectedNode != null) {
                        if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                            ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
                            setCOA(coa);
                        } else {
                            clear();
                            setEnabledControl(false);

                        }
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtUsrCode.requestFocus();
                }
        }
    }

    private void tabToTree(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            treeCOA.requestFocus();
        }
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void save() {
        saveChartAcc();
    }

}
