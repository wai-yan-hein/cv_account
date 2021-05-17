/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.setup.common.COAOptionTableModel;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class COAOptionDialog extends javax.swing.JDialog {

    @Autowired
    private COAOptionTableModel model;
    @Autowired
    private COAService coaService;
    private SelectionObserver observer;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form COAOptionDialog
     */
    public COAOptionDialog() {
        super(Global.parentForm, true);
        initComponents();
    }

    public void initTable() {
        tblOption.getTableHeader().setFont(Global.tblHeaderFont);
        tblOption.getTableHeader().setBackground(ColorUtil.btnEdit);
        tblOption.getTableHeader().setForeground(ColorUtil.foreground);
        tblOption.setDefaultRenderer(Object.class, new TableCellRender());
        tblOption.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblOption.setModel(model);
        searchHead();
    }

    private void searchHead() {
        List<ChartOfAccount> listCOA = coaService.getParent(Global.compCode);
        if (!listCOA.isEmpty()) {
            listCOA.forEach(coa -> {
                coa.setActive(Boolean.FALSE);
            });
            model.setListCoaHead(listCOA);
        }

    }

    private void select() {
        String coaGroup = "";
        List<ChartOfAccount> listFilter = new ArrayList<>();
        List<ChartOfAccount> listCoaHead = model.getListCoaHead();
        if (!listCoaHead.isEmpty()) {
            listCoaHead.stream().filter(coa -> (coa.isActive())).forEachOrdered(coa -> {
                listFilter.add(coa);
            });
        }
        if (!listFilter.isEmpty()) {
            for (ChartOfAccount coa : listFilter) {
                String code = coa.getCode();
                coaGroup += "'" + code + "',";
            }
        }
        coaGroup = coaGroup.substring(0, coaGroup.length() - 1);
        observer.selected("COA-GROUP", coaGroup);
        this.dispose();
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
        tblOption = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select COA Group ");

        tblOption.setFont(Global.textFont);
        tblOption.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblOption.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblOption);

        jButton1.setBackground(ColorUtil.btnEdit);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(ColorUtil.foreground);
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        select();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOption;
    // End of variables declaration//GEN-END:variables
}
