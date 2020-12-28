/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.dialog;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.inv.entity.Order;
import com.cv.inv.entry.common.OrderSearchByCustomerTableModel;
import com.cv.inv.service.OrderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class OrderSearchByCustomerDialog extends javax.swing.JDialog {

    @Autowired
    private OrderSearchByCustomerTableModel model;
    @Autowired
    private OrderService orderService;
    private Trader customer;
    private SelectionObserver observer;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public Trader getCustomer() {
        return customer;
    }

    public void setCustomer(Trader customer) {
        this.customer = customer;
        if (this.customer != null) {
            lblCusName.setText(this.customer.getTraderName());
        }
    }

    /**
     * Creates new form OrderSearchByCustomerDialog
     */
    public OrderSearchByCustomerDialog() {
        super(Global.parentForm, true);
        initComponents();
    }

    public void initMain() {
        initTable();
        search();

    }

    private void initTable() {
        tblOrder.setModel(model);
        tblOrder.getTableHeader().setFont(Global.tblHeaderFont);
        tblOrder.getTableHeader().setForeground(ColorUtil.foreground);
        tblOrder.getTableHeader().setBackground(ColorUtil.btnEdit);
        tblOrder.setDefaultRenderer(Object.class, new TableCellRender());
        tblOrder.setDefaultRenderer(Float.class, new TableCellRender());

    }

    private void search() {
        if (customer != null) {
            List<Order> findActiveOrder = orderService.findActiveOrder("-", "-", customer.getId().toString(), "-");
            model.setListOrder(findActiveOrder);
        }
    }

    private void select() {
        if (tblOrder.getSelectedRow() >= 0) {
            int row = tblOrder.convertRowIndexToModel(tblOrder.getSelectedRow());
            Order order = model.getOrder(row);
            observer.selected("ORDER", order);
            this.dispose();
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

        jPanel1 = new javax.swing.JPanel();
        lblCusName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrder = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Order History");

        lblCusName.setFont(Global.menuFont);
        lblCusName.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCusName, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCusName, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                .addContainerGap())
        );

        tblOrder.setFont(Global.textFont);
        tblOrder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblOrder.setRowHeight(Global.tblRowHeight);
        tblOrder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOrderMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblOrder);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblOrderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrderMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            select();
        }
    }//GEN-LAST:event_tblOrderMouseClicked

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCusName;
    private javax.swing.JTable tblOrder;
    // End of variables declaration//GEN-END:variables
}
