/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.Region;
import com.cv.accountswing.service.RegionService;
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
public class RegionSetup extends javax.swing.JPanel implements TreeSelectionListener, MouseListener, KeyListener,
        PanelControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegionSetup.class);
    DefaultMutableTreeNode root;
    DefaultMutableTreeNode child;
    DefaultTreeModel treeModel;
    DefaultMutableTreeNode selectedNode;
    int seletedIndex = 0;
    private final String parentRootName = "Region";
    @Autowired
    private TaskExecutor taskExecutor;
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

    JPopupMenu popupmenu;
    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        LOGGER.info("Selected Menu : " + menuName);
        switch (menuName) {
            case "New":
                newRegion();
                break;
            case "Delete":
                deleteRegion();
                break;
            default:
                break;
        }

    };
    @Autowired
    private RegionService regionService;

    /**
     * Creates new form RegionSetup
     */
    public RegionSetup() {
        initComponents();
        initPopup();
        initKeyListener();
    }

    private void initMain() {
        initTree();
        isShown = true;
    }

    private void initKeyListener() {
        txtRegionName.addKeyListener(this);
        btnClearR.addKeyListener(this);
        btnSaveR.addKeyListener(this);
        treeRegion.addKeyListener(this);
        treeRegion.addTreeSelectionListener(this);
        treeRegion.addMouseListener(this);
    }

    private void initTree() {
        treeModel = (DefaultTreeModel) treeRegion.getModel();
        treeModel.setRoot(null);
        root = new DefaultMutableTreeNode(parentRootName);
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            createTreeNode("0", root);
            treeModel.setRoot(root);
            loadingObserver.load(this.getName(), "Stop");

        });
    }

    private void createTreeNode(String parentMenuID, DefaultMutableTreeNode treeRoot) {
        List<Region> regions = regionService.search("-", "-", Global.compId.toString(), parentMenuID);
        for (Region region : regions) {
            DefaultMutableTreeNode regRoot = new DefaultMutableTreeNode(region);
            treeRoot.add(regRoot);
            createTreeNode(region.getRegId().toString(), regRoot);

            /*if (!child.getCode().isEmpty()) {
            }*/
        }

    }

    private void setRegion(Region region) {
        txtRegionCode.setText(Util1.getString(region.getRegId()));
        txtRegionName.setText(region.getRegionName());
        labelStatus.setText("EDIT");
    }

    public void clear() {
        txtRegionCode.setText(null);
        txtRegionName.setText(null);
        labelStatus.setText("NEW");
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

    private void deleteRegion() {
        try {
            if (isValidDelete()) {
                treeModel.removeNodeFromParent(selectedNode);
                treeModel.reload(selectedNode);

            }
        } catch (Exception e) {
            LOGGER.error("Delete Region :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Delete Region", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidDelete() {
        boolean status = false;
        Region reg = (Region) selectedNode.getUserObject();
        int showConfirmDialog = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete");
        if (showConfirmDialog == JOptionPane.OK_OPTION) {
            if (reg.getRegId() != null) {
                int delete = regionService.delete(reg.getRegId().toString(), Global.compId.toString());
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

    private void newRegion() {
        Region region = new Region();
        region.setRegionName("New Region");
        child = new DefaultMutableTreeNode(region);
        selectedNode.add(child);
        treeModel.insertNodeInto(child, selectedNode, selectedNode.getChildCount() - 1);

    }

    private void saveRegion() {
        Region reg = new Region();
        reg.setRegionCode(txtRegionCode.getText());
        reg.setRegionName(txtRegionName.getText());
        reg.setCompId(Global.compId);

        if (isValidRegion(reg, Global.loginUser.getUserId().toString(), Global.compId, labelStatus.getText())) {
            Region region = regionService.save(reg);
            if (region != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if (labelStatus.getText().equals("EDIT")) {
                    selectedNode.setUserObject(region);
                    treeModel.reload(selectedNode);
                    clear();

                }
            }
        }

    }

    private boolean isValidRegion(Region region,
            String userId, Integer compCode, String editStatus) {
        boolean status = true;

        if (Util1.isNull(region.getRegionName(), "-").equals("-")) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Region can't be blank.");
        } else {
            if (region.getRegionCode().isEmpty()) {
                //region.setDeptCode(getDeptCode(compCode));
                region.setCreatedBy(userId);
                region.setCreatedDt(Util1.getTodayDate());
            } else {
                List<Region> listDept = regionService.search(region.getRegionCode(), "-", compCode.toString(), "-");
                if (listDept != null) {
                    if (listDept.size() > 0) {
                        status = false;
                        JOptionPane.showMessageDialog(Global.parentForm, "Duplicate reg code.");
                    } else {
                    }
                }
            }
            region.setUpdatedBy(userId);
            region.setUpdatedDt(Util1.getTodayDate());
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedNode.getParent();
            Object userObject = node.getUserObject();
            if (userObject.toString().equals(parentRootName)) {
                region.setParentRegion(0);
            } else {
                Region reg = (Region) userObject;
                region.setParentRegion(reg.getRegId());
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
        txtRegionCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRegionName = new javax.swing.JTextField();
        labelStatus = new javax.swing.JLabel();
        btnClearR = new javax.swing.JButton();
        btnSaveR = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeRegion = new javax.swing.JTree();

        jLabel4.setText("jLabel4");

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setFont(Global.textFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Code");

        txtRegionCode.setEditable(false);
        txtRegionCode.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Region");

        txtRegionName.setFont(Global.textFont);
        txtRegionName.setEnabled(false);
        txtRegionName.setName("txtRegionName"); // NOI18N

        labelStatus.setFont(Global.lableFont);
        labelStatus.setText("NEW");

        btnClearR.setFont(Global.lableFont);
        btnClearR.setText("Clear");
        btnClearR.setEnabled(false);
        btnClearR.setName("btnClearR"); // NOI18N
        btnClearR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearRActionPerformed(evt);
            }
        });

        btnSaveR.setFont(Global.lableFont);
        btnSaveR.setText("Save");
        btnSaveR.setEnabled(false);
        btnSaveR.setName("btnSaveR"); // NOI18N
        btnSaveR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveRActionPerformed(evt);
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRegionName)
                            .addComponent(txtRegionCode)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(labelStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 192, Short.MAX_VALUE)
                        .addComponent(btnSaveR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClearR)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, labelStatus});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtRegionCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtRegionName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelStatus)
                    .addComponent(btnClearR)
                    .addComponent(btnSaveR))
                .addContainerGap(321, Short.MAX_VALUE))
        );

        treeRegion.setFont(Global.textFont);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("color");
        treeNode1.add(treeNode2);
        treeRegion.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeRegion.setName("treeRegion"); // NOI18N
        jScrollPane2.setViewportView(treeRegion);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        txtRegionName.requestFocus();

    }//GEN-LAST:event_formComponentShown

    private void btnSaveRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveRActionPerformed
        // TODO add your handling code here:
        try {
            saveRegion();
        } catch (Exception e) {
            LOGGER.error("Save Region :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Save Region", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveRActionPerformed

    private void btnClearRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearRActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearRActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearR;
    private javax.swing.JButton btnSaveR;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JTree treeRegion;
    private javax.swing.JTextField txtRegionCode;
    private javax.swing.JTextField txtRegionName;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selectedNode = (DefaultMutableTreeNode) treeRegion.getLastSelectedPathComponent();
        if (selectedNode != null) {
            if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                Region region = (Region) selectedNode.getUserObject();
                setRegion(region);
                setEnableControl(true);
            } else {
                clear();
                setEnableControl(false);
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
            case "txtRegionName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnSaveR.requestFocus();
                }
                tabToTree(e);
                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnClearR.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtRegionName.requestFocus();
                }
                tabToTree(e);
                break;

            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtRegionName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSaveR.requestFocus();
                }
                tabToTree(e);
                break;

            case "treeRegion":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtRegionName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    if (selectedNode != null) {
                        if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                            Region region = (Region) selectedNode.getUserObject();
                            setRegion(region);
                            setEnableControl(true);
                        } else {
                            clear();
                            setEnableControl(false);
                        }
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRegionName.requestFocus();
                }
                break;

        }
    }

    private void tabToTree(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            treeRegion.requestFocus();
        }
    }

    private void setEnableControl(boolean status) {
        txtRegionName.setEnabled(status);
        btnSaveR.setEnabled(status);
        btnClearR.setEnabled(status);

    }

    @Override
    public void save() {
        saveRegion();
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
}
