/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.Menu;
import com.cv.accountswing.entity.view.VRoleMenu;
import com.cv.accountswing.entity.view.VRoleMenuKey;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.MenuService;
import com.cv.accountswing.ui.ApplicationMainFrame;
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
        initTree();
        isShown = true;
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
            List<VRoleMenu> listVRM = menuService.getParentChildMenu(Global.roleId.toString());
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
        Menu menu = new Menu();
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
                parentCode = menu.getKey().getMenuId().toString();
            }
        }
        String menuName = txtMenuName.getText();
        if (!menuName.isEmpty()) {
            VRoleMenu vMenu = (VRoleMenu) selectedNode.getUserObject();
            Menu menu = new Menu();
            menu.setId(vMenu.getKey().getMenuId());
            menu.setMenuName(menuName);
            menu.setParent(parentCode);
            menu.setMenuUrl(txtMenuUrl.getText());
            if (txtOrder.getValue() != null) {
                menu.setOrderBy(Util1.getInteger(txtOrder.getText()));
            }
            Menu saveMenu = menuService.saveMenu(menu);
            if (saveMenu != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                VRoleMenu rMenu = new VRoleMenu();
                VRoleMenuKey key = new VRoleMenuKey();
                key.setMenuId(saveMenu.getId());
                key.setRoleId(Global.roleId);
                rMenu.setKey(key);
                rMenu.setMenuName(saveMenu.getMenuName());
                rMenu.setParent(saveMenu.getParent());
                rMenu.setOrderBy(saveMenu.getOrderBy());
                selectedNode.setUserObject(rMenu);
                treeModel.reload();
                clear();
            }
        }
    }

    private void deleteMenu() {
        try {
            if (selectedNode != null) {
                VRoleMenu rMenu = (VRoleMenu) selectedNode.getUserObject();
                menuService.delete(rMenu.getKey().getMenuId().toString());
                treeModel.removeNodeFromParent(selectedNode);
                treeModel.reload(selectedNode);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            LOGGER.error("Delete Menu :" + e.getMessage());
        }
    }

    private void setMenu(VRoleMenu menu) {
        txtMenuName.setText(menu.getMenuName());
        txtMenuUrl.setText(menu.getMenuUrl());
        txtOrder.setText(menu.getOrderBy().toString());
    }

    private void clear() {
        txtMenuName.setText(null);
        txtMenuUrl.setText(null);
        txtOrder.setText(null);
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

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        treeCOA.setFont(Global.textFont);
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

        jButton1.setFont(Global.textFont);
        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.textFont);
        jLabel3.setText("Order");

        txtOrder.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

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
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMenuUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(txtMenuName)
                            .addComponent(txtOrder)))
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
                    .addComponent(jLabel2)
                    .addComponent(txtMenuUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(240, Short.MAX_VALUE))
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree treeCOA;
    private javax.swing.JTextField txtMenuName;
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
