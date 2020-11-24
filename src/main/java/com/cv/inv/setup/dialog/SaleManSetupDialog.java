/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.dialog;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.inv.entity.SaleMan;
import com.cv.inv.service.SaleManService;
import com.cv.inv.setup.common.SaleManTableModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class SaleManSetupDialog extends javax.swing.JDialog implements KeyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaleManSetupDialog.class);

    @Autowired
    private SaleManTableModel saleManTableModel;
    @Autowired
    private SaleManService saleManService;
    private int selectRow = - 1;
    private SaleMan saleMan;

    public SaleManSetupDialog() {
        super(Global.parentForm, true);
        initComponents();
    }

    public void initMain() {
        initTable();
        searchSaleMan();
        initKeyListener();
        txtId.requestFocus();
    }

    private void initTable() {
        tblSaleMan.setModel(saleManTableModel);
        //sorter = new TableRowSorter<>(tblSaleMan.getModel());
        //tblSaleMan.setRowSorter(sorter);
        tblSaleMan.getTableHeader().setFont(Global.lableFont);
        tblSaleMan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSaleMan.getColumnModel().getColumn(0).setPreferredWidth(1);
        tblSaleMan.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblSaleMan.setDefaultRenderer(Object.class, new TableCellRender());
        tblSaleMan.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblSaleMan.getSelectedRow() >= 0) {
                    selectRow = tblSaleMan.convertRowIndexToModel(tblSaleMan.getSelectedRow());
                    setSaleMan(saleManTableModel.getSaleMan(selectRow));
                }
            }
        });

    }

    private void setSaleMan(SaleMan saleMan) {
        txtId.setText(saleMan.getSaleManId());
        txtName.setText(saleMan.getSaleManName());
        txtPhone.setText(saleMan.getPhone());
        txtAddress.setText(saleMan.getAddress());
        lblStatus.setText("EDIT");
        chkActive.setSelected(saleMan.getActive());
    }

    private void clear() {
        txtName.setText(null);
        txtId.setText(null);
        txtAddress.setText(null);
        txtPhone.setText(null);
        lblStatus.setText("NEW");
        chkActive.setSelected(false);
        saleManTableModel.refresh();
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtId.getText().isEmpty() && txtName.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Code length is less then 6 character.",
                    "Code length", JOptionPane.ERROR_MESSAGE);
            txtId.requestFocusInWindow();
        } else {
            saleMan = new SaleMan();
            saleMan.setSaleManId(txtId.getText());
            saleMan.setSaleManName(txtName.getText());
            saleMan.setPhone(txtPhone.getText());
            saleMan.setAddress(txtAddress.getText());
            saleMan.setActive(chkActive.isSelected());
        }
        return status;
    }

    private void save() {
        if (isValidEntry()) {
            SaleMan saveSaleMan = saleManService.save(saleMan);
            if (saveSaleMan != null) {
                LOGGER.info("SALEMAN ID :" + saveSaleMan.getSaleManId());
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if ("NEW".equals(lblStatus.getText())) {
                    Global.listSaleMan.add(saveSaleMan);
                } else {
                    Global.listSaleMan.set(selectRow, saveSaleMan);
                }
            }
            clear();
        }
    }

    private void searchSaleMan() {
        saleManTableModel.setListSaleMan(Global.listSaleMan);
    }

    private void delete() {
        SaleMan sale = saleManTableModel.getSaleMan(selectRow);
        int delete = saleManService.delete(sale.getSaleManId());
        if (delete == 1) {
            JOptionPane.showMessageDialog(Global.parentForm, "Deleted");
            saleManTableModel.deleteSaleMan(selectRow);
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Error in server.");
        }
        clear();
    }

    private void initKeyListener() {
        txtId.addKeyListener(this);
        txtName.addKeyListener(this);
        txtPhone.addKeyListener(this);
        txtAddress.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnSave.addKeyListener(this);
        btnClear.addKeyListener(this);
        btnDelete.addKeyListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSaleMan = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sale Man Setup");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblSaleMan.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblSaleMan);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("ID");

        txtId.setName("txtId"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        txtName.setName("txtName"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Phone");

        txtPhone.setName("txtPhone"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Address");

        txtAddress.setName("txtAddress"); // NOI18N

        chkActive.setFont(Global.lableFont);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        btnClear.setBackground(ColorUtil.btnEdit);
        btnClear.setFont(Global.lableFont);
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clear-button-white.png"))); // NOI18N
        btnClear.setText("Clear");
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnDelete.setBackground(ColorUtil.btnDelete);
        btnDelete.setFont(Global.lableFont);
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete-button-white.png"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setName("btnDelete"); // NOI18N
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnSave.setBackground(ColorUtil.mainColor);
        btnSave.setFont(Global.lableFont);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clear-button-white.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
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
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(chkActive)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPhone)
                            .addComponent(txtName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtId, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtAddress)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
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
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkActive)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(btnClear)
                    .addComponent(btnDelete)
                    .addComponent(btnSave))
                .addContainerGap(138, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                    .addComponent(txtFilter))
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
                        .addGap(11, 11, 11))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(5, 5, 5)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try {
            save();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            LOGGER.error("Save SaleMan :" + e.getMessage());
        }

    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        try {
            delete();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            LOGGER.error("Delete SaleMan :" + e.getMessage());
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblSaleMan;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
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
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtId":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtPhone.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtId.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtPhone":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtAddress.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtName.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtAddress":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtPhone.requestFocus();
                }
                tabToTable(e);
                break;
            case "chkActive":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnSave.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtAddress.requestFocus();
                }
                tabToTable(e);
                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnDelete.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    chkActive.requestFocus();
                }
                tabToTable(e);
                break;
            case "btnDelete":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnClear.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
                tabToTable(e);
                break;
            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtId.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnDelete.requestFocus();
                }
                tabToTable(e);
                break;
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblSaleMan.requestFocus();
            if (tblSaleMan.getRowCount() >= 0) {
                tblSaleMan.setRowSelectionInterval(0, 0);
            }
        }
    }
}
