/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.dialog;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.ChargeType;
import com.cv.inv.service.ChargeTypeService;
import com.cv.inv.setup.dialog.common.ChargeTypeTableModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
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
public class ChargeTypeSetupDialog extends javax.swing.JDialog implements KeyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChargeTypeSetupDialog.class);

    @Autowired
    private ChargeTypeTableModel typeTableModel;
    @Autowired
    private ChargeTypeService typeService;
    private int selectRow = - 1;
    private ChargeType chargeType = new ChargeType();

    public ChargeTypeSetupDialog() {
        super(Global.parentForm, true);
        initComponents();
        txtChargeType.requestFocus();
    }

    public void initMain() {
        initTable();
        searchChargeType();
        initKeyListener();
    }

    private void initTable() {
        tblChargeType.setModel(typeTableModel);
        //sorter = new TableRowSorter<>(tblChargeType.getModel());
        //tblChargeType.setRowSorter(sorter);
        tblChargeType.getTableHeader().setFont(Global.lableFont);
        tblChargeType.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblChargeType.getTableHeader().setForeground(ColorUtil.foreground);
        tblChargeType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblChargeType.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblChargeType.setDefaultRenderer(Object.class, new TableCellRender());
        tblChargeType.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblChargeType.getSelectedRow() >= 0) {
                    selectRow = tblChargeType.convertRowIndexToModel(tblChargeType.getSelectedRow());
                    setChargeType(typeTableModel.getChargerType(selectRow));
                }
            }
        });

    }

    private void setChargeType(ChargeType ch) {
        chargeType = ch;
        txtChargeType.setText(chargeType.getChargeTypeDesp());
        txtCode.setText(chargeType.getUserCode());
        lblStatus.setText("EDIT");
    }

    private void searchChargeType() {
        typeTableModel.setListChargeType(Global.listChargeType);
    }

    private void clear() {
        txtCode.setText(null);
        txtChargeType.setText(null);
        lblStatus.setText("NEW");
        chargeType = new ChargeType();
    }

    private void save() {
        if (isValidEntry()) {
            ChargeType saveChargeType = typeService.save(chargeType);
            if (saveChargeType != null) {
                LOGGER.info("CHARGE-TYPE DESP :" + saveChargeType.getChargeTypeDesp());
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if ("NEW".equals(lblStatus.getText())) {
                    typeTableModel.addChargeType(saveChargeType);
                } else {
                    typeTableModel.setChargeType(saveChargeType, selectRow);
                }
            }
            clear();
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtChargeType.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Charge Type Description cannot be blank.");
            txtChargeType.requestFocus();
        } else {
            chargeType.setChargeTypeDesp(txtChargeType.getText());
            if (lblStatus.getText().equals("NEW")) {
                chargeType.setUserCode(txtCode.getText());
                chargeType.setCompCode(Global.compCode);
                chargeType.setCreatedBy(Global.loginUser);
                chargeType.setCreatedDate(Util1.getTodayDate());
                chargeType.setMacId(Global.machineId);
            } else {
                chargeType.setUpdatedBy(Global.loginUser);
            }
        }
        return status;
    }

    private void delete() {
        ChargeType charge = typeTableModel.getChargerType(selectRow);
        int delete = typeService.delete(charge.getChargeTypeCode());
        if (delete == 1) {
            JOptionPane.showMessageDialog(Global.parentForm, "Deleted");
            typeTableModel.deleteChargeType(selectRow);
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Error in server.");
        }
        clear();
    }

    private void initKeyListener() {
        txtChargeType.addKeyListener(this);
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

        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblChargeType = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtChargeType = new javax.swing.JTextField();
        btnClear = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Charge Type Setup");

        tblChargeType.setFont(Global.textFont);
        tblChargeType.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblChargeType);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Charge Type");

        txtChargeType.setName("txtChargeType"); // NOI18N

        btnClear.setBackground(ColorUtil.btnEdit);
        btnClear.setFont(Global.lableFont);
        btnClear.setForeground(ColorUtil.foreground);
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
        btnDelete.setForeground(ColorUtil.foreground);
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
        btnSave.setForeground(ColorUtil.foreground);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblStatus.setText("NEW");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Code");

        txtCode.setName("txtChargeType"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCode)
                            .addComponent(txtChargeType))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtChargeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnDelete)
                    .addComponent(btnSave)
                    .addComponent(lblStatus))
                .addContainerGap(275, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblChargeType;
    private javax.swing.JTextField txtChargeType;
    private javax.swing.JTextField txtCode;
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
        }
        switch (ctrlName) {
            case "txtChargeType":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnSave.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
                tabToTable(e);
                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnDelete.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtChargeType.requestFocus();
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
                    txtChargeType.requestFocus();
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
            tblChargeType.requestFocus();
            if (tblChargeType.getRowCount() >= 0) {
                tblChargeType.setRowSelectionInterval(0, 0);
            }
        }
    }
}
