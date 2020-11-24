/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.dialog;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.inv.entity.Location;
import com.cv.inv.service.LocationService;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class LocationSetupDialog extends javax.swing.JDialog implements KeyListener, TreeSelectionListener {

    /**
     * Creates new form LocationSetupDialog
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationSetupDialog.class);
    private String parentRootName = "Location";
    private DefaultMutableTreeNode selectedNode;
    private DefaultMutableTreeNode child;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode treeRoot;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private LocationService locationService;
    private Location location;

    JPopupMenu popupmenu;
    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        switch (menuName) {
            case "New":
                newLocation();
                break;
            case "Delete":
                //deleteDepatment();
                break;
            default:
                break;
        }

    };

    public LocationSetupDialog() {
        super(Global.parentForm, true);
        initComponents();
        initPopup();
        initKeyListener();
        txtName.requestFocus();
    }

    private void initTree() {
        treeModel = (DefaultTreeModel) treeLoc.getModel();
        treeModel.setRoot(null);
        treeRoot = new DefaultMutableTreeNode(parentRootName);
        taskExecutor.execute(() -> {
            createTreeNode("#", treeRoot);
            treeModel.setRoot(treeRoot);
        });
    }

    private void createTreeNode(String parentMenuID, DefaultMutableTreeNode treeRoot) {
        List<Location> locs = locationService.search(parentMenuID);
        locs.forEach(loc -> {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(loc);
            treeRoot.add(root);
            createTreeNode(loc.getLocationId().toString(), root);
            /*if (!child.getCode().isEmpty()) {
                }*/
        });
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
        chkActive.addKeyListener(this);
        btnClear.addKeyListener(this);
        btnDelete.addKeyListener(this);
        btnSave.addKeyListener(this);
        treeLoc.addTreeSelectionListener(this);
        treeLoc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupmenu.show(treeLoc, e.getX(), e.getY());
                }
            }

        });
    }

    private void newLocation() {
        Location loc = new Location();
        loc.setLocationName("New Location");
        child = new DefaultMutableTreeNode(loc);
        selectedNode.add(child);
        treeModel.insertNodeInto(child, selectedNode, selectedNode.getChildCount() - 1);
    }

    private void saveLocaiton() {
        location.setLocationName(txtName.getText());
        location.setCalcStock(chkActive.isSelected());
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedNode.getParent();
        Object userObject = node.getUserObject();
        if (userObject.toString().equals(parentRootName)) {
            location.setParent(0);
        } else {
            Location loc = (Location) userObject;
            location.setParent(loc.getLocationId());
        }
        try {
            Location saveLoc = locationService.save(location);
            if (saveLoc != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if (lblStatus.getText().equals("EDIT")) {
                    selectedNode.setUserObject(saveLoc);
                    clear();
                    treeModel.reload(selectedNode);
                }
            }
        } catch (DataIntegrityViolationException e) {
            JOptionPane.showMessageDialog(Global.parentForm, "Duplicate Name.");
        }

    }

    private void delLoc() {
        Location loc = (Location) selectedNode.getUserObject();
        int delete = locationService.delete(loc.getLocationId().toString());
        if (delete == 1) {
            JOptionPane.showMessageDialog(Global.parentForm, "Deleted.");
        }
    }

    private void clear() {
        txtName.setText(null);
        chkActive.setSelected(false);
        lblStatus.setText("NEW");
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
        treeLoc = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Location Setup");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jScrollPane1.setViewportView(treeLoc);

        jLabel1.setText("Name");

        txtName.setName("txtName"); // NOI18N
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });

        chkActive.setText("Cal-Stock");
        chkActive.setName("chkActive"); // NOI18N

        btnSave.setBackground(ColorUtil.mainColor);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDelete.setBackground(ColorUtil.btnDelete);
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clear-button-white.png"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setName("btnDelete"); // NOI18N
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnClear.setBackground(ColorUtil.btnEdit);
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clear-button-white.png"))); // NOI18N
        btnClear.setText("Clear");
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear))
                    .addComponent(chkActive, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtName))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkActive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblStatus))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnDelete)
                            .addComponent(btnSave)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(btnClear)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
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
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        initTree();
    }//GEN-LAST:event_formComponentShown

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            saveLocaiton();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            LOGGER.error("Save Location :" + e.getMessage());
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        try {
            delLoc();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            LOGGER.error("Delete Location :" + e.getMessage());
        }// TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clear();        // TODO add your handling code here:
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        // TODO add your handling code here:
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTree treeLoc;
    private javax.swing.JTextField txtName;
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
            case "txtName":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        chkActive.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        chkActive.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        chkActive.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        btnClear.requestFocus();
                        break;

                }
                break;
            case "chkActive":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtName.requestFocus();
                        break;

                }
                break;
            case "btnSave":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        btnDelete.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        btnDelete.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnDelete.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        chkActive.requestFocus();
                        break;

                }
                break;
            case "btnDelete":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        btnClear.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        btnClear.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnClear.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        btnSave.requestFocus();
                        break;

                }
                break;
            case "btnClear":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtName.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtName.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtName.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        btnDelete.requestFocus();
                        break;

                }
                break;
        }

    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selectedNode = (DefaultMutableTreeNode) treeLoc.getLastSelectedPathComponent();
        if (selectedNode != null) {
            if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                location = (Location) selectedNode.getUserObject();
                txtName.setText(location.getLocationName());
                lblStatus.setText("EDIT");
                txtName.requestFocus();
            } else {
                txtName.setText(null);
            }
        }

    }
}
