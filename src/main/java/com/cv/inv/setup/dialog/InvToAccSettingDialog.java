/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.dialog;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.ui.editor.COAAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.inv.entity.AccSetting;
import com.cv.inv.setup.dialog.common.AccountSettingTableModel;
import java.util.List;
import javax.swing.JOptionPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.cv.inv.service.AccSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
@Component
public class InvToAccSettingDialog extends javax.swing.JDialog {

    private static final Logger log = LoggerFactory.getLogger(InvToAccSettingDialog.class);

    @Autowired
    private AccountSettingTableModel model;
    @Autowired
    private AccSettingService accSettingService;
    private COAAutoCompleter disAutoCompleter;
    private COAAutoCompleter vouAutoCompleter;
    private COAAutoCompleter payAutoCompleter;
    private COAAutoCompleter taxAutoCOmpleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private COAAutoCompleter sourceAccountCompleter;

    /**
     * Creates new form InvToAccSetting
     */
    public InvToAccSettingDialog() {
        super(Global.parentForm, true);
        initComponents();
    }

    public void initMain() {
        initCombo();
        tblSetting.setModel(model);
        List<AccSetting> listAcc = accSettingService.findAll();
        model.setListSetting(listAcc);

    }

    private void initCombo() {
        disAutoCompleter = new COAAutoCompleter(txtDiscount, Global.listCOA, null, false);
        payAutoCompleter = new COAAutoCompleter(txtPayment, Global.listCOA, null, false);
        taxAutoCOmpleter = new COAAutoCompleter(txtTax, Global.listCOA, null, false);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, Global.listDepartment, null, false);
        sourceAccountCompleter = new COAAutoCompleter(txtSource, Global.listCOA, null, false);

    }

    private void select() {
        if (tblSetting.getSelectedRow() >= 0) {
            int row = tblSetting.convertRowIndexToModel(tblSetting.getSelectedRow());
            AccSetting setting = model.getSetting(row);
            disAutoCompleter.setCoa(setting.getDisAccount());
            payAutoCompleter.setCoa(setting.getPayAccount());
            taxAutoCOmpleter.setCoa(setting.getTaxAccount());
            departmentAutoCompleter.setDepartment(setting.getDepartment());
            sourceAccountCompleter.setCoa(setting.getSoureAccount());
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Select Type");
        }
    }

    private void save() {
        if (tblSetting.getSelectedRow() >= 0) {
            int row = tblSetting.convertRowIndexToModel(tblSetting.getSelectedRow());
            AccSetting setting = model.getSetting(row);
            setting.setDisAccount(disAutoCompleter.getCOA());
            setting.setPayAccount(payAutoCompleter.getCOA());
            setting.setTaxAccount(taxAutoCOmpleter.getCOA());
            setting.setDepartment(departmentAutoCompleter.getDepartment());
            setting.setSoureAccount(sourceAccountCompleter.getCOA());
            accSettingService.save(setting);
            model.setSetting(setting, row);
            clear();
            JOptionPane.showMessageDialog(Global.parentForm, "Saved");
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Select Type");
        }
    }

    private void clear() {
        txtDiscount.setText(null);
        txtPayment.setText(null);
        txtTax.setText(null);
        txtSource.setText(null);
        txtDep.setText(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.ac
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblSetting = new javax.swing.JTable();
        lblDis = new javax.swing.JLabel();
        txtDiscount = new javax.swing.JTextField();
        txtPayment = new javax.swing.JTextField();
        txtTax = new javax.swing.JTextField();
        lblPay = new javax.swing.JLabel();
        lblTax = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        lblTax1 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        txtSource = new javax.swing.JTextField();
        lblTax2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblSetting.setModel(new javax.swing.table.DefaultTableModel(
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
        tblSetting.setRowHeight(Global.tblRowHeight);
        tblSetting.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSettingMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblSetting);

        lblDis.setFont(Global.lableFont);
        lblDis.setText("Discount");

        lblPay.setFont(Global.lableFont);
        lblPay.setText("Payment");

        lblTax.setFont(Global.lableFont);
        lblTax.setText("Tax");

        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblTax1.setFont(Global.lableFont);
        lblTax1.setText("Dep");

        lblTax2.setFont(Global.lableFont);
        lblTax2.setText("Source Account");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblTax, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblDis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTax1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTax2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDiscount, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPayment)
                            .addComponent(txtTax, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(txtDep, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(txtSource, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblDis, lblPay, lblTax});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDis))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPay))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTax))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTax1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTax2)
                            .addComponent(txtSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addComponent(jButton1)
                        .addGap(0, 91, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblSettingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSettingMouseClicked
        // TODO add your handling code here:
        select();
    }//GEN-LAST:event_tblSettingMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        save();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDis;
    private javax.swing.JLabel lblPay;
    private javax.swing.JLabel lblTax;
    private javax.swing.JLabel lblTax1;
    private javax.swing.JLabel lblTax2;
    private javax.swing.JTable tblSetting;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtDiscount;
    private javax.swing.JTextField txtPayment;
    private javax.swing.JTextField txtSource;
    private javax.swing.JTextField txtTax;
    // End of variables declaration//GEN-END:variables
}
