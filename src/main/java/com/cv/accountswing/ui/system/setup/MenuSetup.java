/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.Menu;
import com.cv.accountswing.entity.Privilege;
import com.cv.accountswing.entity.PrivilegeKey;
import com.cv.accountswing.entity.UserRole;
import com.cv.accountswing.entity.view.VRoleMenu;
import com.cv.accountswing.entity.view.VRoleMenuKey;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.MenuService;
import com.cv.accountswing.service.PrivilegeService;
import com.cv.accountswing.service.UserRoleService;
import com.cv.accountswing.service.VDescriptionService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.editor.MenuClassAutoCompleter;
import com.cv.accountswing.util.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
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
public class MenuSetup extends javax.swing.JPanel implements TreeSelectionListener, PanelControl {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MenuSetup.class);
    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private DefaultMutableTreeNode treeRoot;
    DefaultTreeModel treeModel;
    DefaultMutableTreeNode selectedNode;
    @Autowired
    COAService coaServcie;
    @Autowired
    private MenuService menuService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private VDescriptionService descriptionService;
    private LoadingObserver loadingObserver;
    private boolean isShown = false;
    private final String parentRootName = "Root Menu";

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
    }

    JPopupMenu popupmenu;
    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        LOGGER.info("Selected Menu : " + menuName);
        switch (menuName) {
            case "New Menu":
                newMenu("Menu");
                break;
            case "Delete":
                deleteMenu();//deleteCOA();
                break;
            case "New Function":
                newMenu("Function");
                break;
            default:
                break;
        }

    };

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    /**
     * Creates new form COASetup
     */
    public MenuSetup() {
        initComponents();
        initKeyListener();
        initPopup();
    }

    private void initMain() {
        initAutoCompler();
        initTree();
        isShown = true;
    }

    private void initAutoCompler() {
        MenuClassAutoCompleter menuClassAutoCompleter = new MenuClassAutoCompleter(txtClass, Global.listMenuClass,
                null, descriptionService);

    }

    private void initKeyListener() {
        treeCOA.addTreeSelectionListener(this);
        treeCOA.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupmenu.show(treeCOA, e.getX(), e.getY());
                }
            }

        });
    }

    private void initTree() {
        treeModel = (DefaultTreeModel) treeCOA.getModel();
        treeModel.setRoot(null);
        treeRoot = new DefaultMutableTreeNode(parentRootName);
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            List<VRoleMenu> listVRM = menuService.getParentChildMenu(Global.roleCode, "-");
            listVRM.forEach(menu -> {
                if (menu.getChild() != null) {
                    if (!menu.getChild().isEmpty()) {
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(menu);
                        treeRoot.add(node);
                        //Need to add action listener
                        //====================================
                        createTreeNode(node, menu.getChild());
                    } else {  //No Child
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(menu);

                        //Need to add action listener
                        //====================================
                        treeRoot.add(node);
                    }
                } else {  //No Child
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(menu);

                    //Need to add action listener
                    //====================================
                    treeRoot.add(node);
                }
            });

            treeModel.setRoot(treeRoot);
            loadingObserver.load(this.getName(), "Stop");
        });

    }

    private void createTreeNode(DefaultMutableTreeNode treeRoot, List<VRoleMenu> listVRM) {
        listVRM.forEach(vrMenu -> {
            if (vrMenu.getChild() != null) {
                if (!vrMenu.getChild().isEmpty()) {
                    DefaultMutableTreeNode menu = new DefaultMutableTreeNode(vrMenu);
                    //Need to add action listener
                    //====================================
                    treeRoot.add(menu);
                    createTreeNode(menu, vrMenu.getChild());
                } else {  //No Child
                    DefaultMutableTreeNode menu = new DefaultMutableTreeNode(vrMenu);
                    //Need to add action listener
                    //====================================
                    treeRoot.add(menu);
                }
            } else {  //No Child
                DefaultMutableTreeNode menu = new DefaultMutableTreeNode(vrMenu);
                //Need to add action listener
                //====================================
                treeRoot.add(menu);
            }
            /*if (!child.getCode().isEmpty()) {
                }*/
        });

    }

    private void initPopup() {
        popupmenu = new JPopupMenu("Edit");
        JMenuItem newMenu = new JMenuItem("New Menu");
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem newFun = new JMenuItem("New Function");
        newMenu.addActionListener(menuListener);
        delete.addActionListener(menuListener);
        newFun.addActionListener(menuListener);
        popupmenu.add(newMenu);
        popupmenu.add(delete);
        popupmenu.add(newFun);
    }

    private void newMenu(String type) {

        VRoleMenu menu = new VRoleMenu();
        if (type.equals("Menu")) {
            menu.setMenuName("New Menu");
            menu.setMenuType("Menu");
        } else {
            menu.setMenuName("New Function");
            menu.setMenuType("Function");
        }
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(menu);
        if (selectedNode != null) {
            selectedNode.add(child);
            treeModel.insertNodeInto(child, selectedNode, selectedNode.getChildCount() - 1);
        }
    }

    private void saveMenu() {
        String parentCode = "";
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
        if (parentNode != null) {
            Object userObject = parentNode.getUserObject();
            if (userObject.toString().equals(parentRootName)) {
                parentCode = "1";
            } else {
                VRoleMenu menu = (VRoleMenu) parentNode.getUserObject();
                parentCode = menu.getKey().getMenuCode();
            }
        }
        String menuName = txtMenuName.getText();
        if (!menuName.isEmpty()) {
            VRoleMenu vMenu = (VRoleMenu) selectedNode.getUserObject();
            Menu menu = new Menu();
            if (vMenu.getKey() != null) {
                menu.setCode(vMenu.getKey().getMenuCode());
            }
            menu.setMenuName(menuName);
            menu.setParent(parentCode);
            menu.setMenuUrl(txtMenuUrl.getText());
            menu.setSoureAccCode(txtAccount.getText());
            menu.setMenuType(txtMenuType.getText().trim());
            menu.setMenuClass(txtClass.getText());
            menu.setMenuNameMM(txtMenuMM.getText());
            menu.setCompCode(Global.compCode);
            menu.setMacId(Global.machineId);
            if (txtOrder.getValue() != null) {
                menu.setOrderBy(Util1.getInteger(txtOrder.getText()));
            }
            Menu saveMenu = menuService.saveMenu(menu);
            if (saveMenu != null) {
                savePrivileges(saveMenu.getCode());
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                VRoleMenu rMenu = new VRoleMenu();
                VRoleMenuKey key = new VRoleMenuKey();
                key.setMenuCode(saveMenu.getCode());
                key.setRoleCode(Global.roleCode);
                rMenu.setKey(key);
                rMenu.setMenuName(saveMenu.getMenuName());
                rMenu.setParent(saveMenu.getParent());
                rMenu.setOrderBy(saveMenu.getOrderBy());
                rMenu.setMenuType(saveMenu.getMenuType());
                selectedNode.setUserObject(rMenu);
                treeModel.reload(selectedNode);
                clear();
            }
        }
    }

    private void savePrivileges(String menuId) {
        if (menuId != null) {
            List<UserRole> listUser = userRoleService.search("-", Global.compCode);
            if (!listUser.isEmpty()) {
                listUser.stream().map(role -> {
                    Privilege p = new Privilege();
                    PrivilegeKey key = new PrivilegeKey(role.getRoleCode(), menuId);
                    p.setKey(key);
                    if (role.getRoleCode().equals(Global.roleCode)) {
                        p.setIsAllow(Boolean.TRUE);
                    } else {
                        p.setIsAllow(Boolean.FALSE);
                    }
                    return p;
                }).forEachOrdered(p -> {
                    privilegeService.save(p);
                });
            }
        }
    }

    private void deleteMenu() {
        try {
            if (selectedNode != null) {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete?");
                if (yes_no == JOptionPane.YES_OPTION) {
                    VRoleMenu rMenu = (VRoleMenu) selectedNode.getUserObject();
                    menuService.delete(rMenu.getKey().getMenuCode());
                    treeModel.removeNodeFromParent(selectedNode);
                    treeModel.reload(selectedNode);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            LOGGER.error("Delete Menu :" + e.getMessage());
        }
    }

    private void setMenu(VRoleMenu menu) {
        txtMenuName.setText(menu.getMenuName());
        txtMenuUrl.setText(menu.getMenuUrl());
        txtOrder.setText(menu.getOrderBy() == null ? null : menu.getOrderBy().toString());
        txtAccount.setText(menu.getSoureAccCode());
        txtMenuType.setText(menu.getMenuType());
        txtClass.setText(menu.getMenuClass());
        txtMenuMM.setText(menu.getMenuNameMM());
        enableControl(true);
    }

    private void clear() {
        txtMenuName.setText(null);
        txtMenuUrl.setText(null);
        txtOrder.setText(null);
        txtAccount.setText(null);
        txtMenuType.setText(null);
        txtClass.setText(null);
        txtMenuMM.setText(null);
        enableControl(false);

    }

    private void enableControl(boolean status) {
        txtMenuName.setEditable(status);
        txtMenuUrl.setEditable(status);
        txtOrder.setEditable(status);
        txtAccount.setEditable(status);
        txtMenuType.setEditable(status);
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
        txtMenuName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtMenuUrl = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtOrder = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtMenuType = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtClass = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtMenuMM = new javax.swing.JTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        treeCOA.setFont(Global.textFont);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Menu");
        treeCOA.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane1.setViewportView(treeCOA);

        jPanel1.setFont(Global.textFont);

        jLabel1.setFont(Global.textFont);
        jLabel1.setText("Menu Name");

        txtMenuName.setFont(Global.textFont);
        txtMenuName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuNameFocusGained(evt);
            }
        });

        jLabel2.setFont(Global.textFont);
        jLabel2.setText("Url");

        txtMenuUrl.setFont(Global.textFont);
        txtMenuUrl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuUrlFocusGained(evt);
            }
        });

        jButton1.setBackground(ColorUtil.mainColor);
        jButton1.setFont(Global.textFont);
        jButton1.setForeground(ColorUtil.foreground);
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.textFont);
        jLabel3.setText("Order");

        txtOrder.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtOrder.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOrderFocusGained(evt);
            }
        });

        jLabel4.setFont(Global.textFont);
        jLabel4.setText("Account ");

        txtAccount.setFont(Global.textFont);
        txtAccount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAccountFocusGained(evt);
            }
        });

        jLabel5.setFont(Global.textFont);
        jLabel5.setText("Menu Type");

        txtMenuType.setFont(Global.textFont);
        txtMenuType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuTypeFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.textFont);
        jLabel6.setText("Menu Class");

        txtClass.setFont(Global.textFont);
        txtClass.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClassFocusGained(evt);
            }
        });

        jLabel7.setFont(Global.textFont);
        jLabel7.setText("Menu MM");

        txtMenuMM.setFont(Global.textFont);
        txtMenuMM.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMenuMMFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMenuUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(txtMenuName)
                            .addComponent(txtOrder)
                            .addComponent(txtAccount, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(txtMenuType, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(txtClass, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(txtMenuMM)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtMenuName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtMenuMM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMenuUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtMenuType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
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

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
    }//GEN-LAST:event_formComponentShown

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try {
            saveMenu();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            LOGGER.error("Save Menu :" + e.getMessage());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtMenuNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuNameFocusGained
        // TODO add your handling code here:
        txtMenuName.selectAll();
    }//GEN-LAST:event_txtMenuNameFocusGained

    private void txtMenuUrlFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuUrlFocusGained
        // TODO add your handling code here:
        txtMenuUrl.selectAll();
    }//GEN-LAST:event_txtMenuUrlFocusGained

    private void txtAccountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusGained
        // TODO add your handling code here:
        txtAccount.selectAll();
    }//GEN-LAST:event_txtAccountFocusGained

    private void txtOrderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOrderFocusGained
        // TODO add your handling code here:
        txtOrder.selectAll();
    }//GEN-LAST:event_txtOrderFocusGained

    private void txtMenuTypeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuTypeFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMenuTypeFocusGained

    private void txtClassFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClassFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClassFocusGained

    private void txtMenuMMFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMenuMMFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMenuMMFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree treeCOA;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtClass;
    private javax.swing.JTextField txtMenuMM;
    private javax.swing.JTextField txtMenuName;
    private javax.swing.JTextField txtMenuType;
    private javax.swing.JTextField txtMenuUrl;
    private javax.swing.JFormattedTextField txtOrder;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selectedNode = (DefaultMutableTreeNode) treeCOA.getLastSelectedPathComponent();
        if (selectedNode != null) {
            if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                if (selectedNode.getUserObject() instanceof VRoleMenu) {
                    VRoleMenu menu = (VRoleMenu) selectedNode.getUserObject();
                    setMenu(menu);
                } else if (selectedNode.getUserObject() instanceof Menu) {
                    Menu menu = (Menu) selectedNode.getUserObject();
                    txtMenuName.setText(menu.getMenuName());
                    txtMenuUrl.setText(menu.getMenuUrl());
                }
            } else {
                clear();
                //setEnabledControl(false);
            }
        }
    }

    @Override
    public void save() {
        saveMenu();
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
    public void delete() {
    }

    @Override
    public void refresh() {
        initTree();
    }
}
