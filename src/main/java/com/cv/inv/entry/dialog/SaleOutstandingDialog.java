/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.dialog;

import com.cv.accountswing.common.Global;
import com.cv.inv.entry.common.SaleOutstandTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class SaleOutstandingDialog extends javax.swing.JDialog {

    /**
     * Creates new form SaleOutstandingDialog
     */
    @Autowired
    private SaleOutstandTableModel saleOutTableModel;

    public SaleOutstandingDialog() {
        super(Global.parentForm, true);
        initComponents();
    }

    public void initMain() {
        initSaleOutTable();
    }

    private void initSaleOutTable() {
        tblSaleOutstand.setModel(saleOutTableModel);
        tblSaleOutstand.getTableHeader().setFont(Global.tblHeaderFont);
        tblSaleOutstand.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblSaleOutstand.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblSaleOutstand.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblSaleOutstand.getColumnModel().getColumn(3).setPreferredWidth(60);
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
        tblSaleOutstand = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sale Outstanding Entry");

        tblSaleOutstand.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblSaleOutstand);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblSaleOutstand;
    // End of variables declaration//GEN-END:variables
}
