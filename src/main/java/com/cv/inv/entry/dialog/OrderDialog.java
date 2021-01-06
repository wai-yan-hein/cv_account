/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.dialog;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Customer;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CustomerAutoCompleter;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Order;
import com.cv.inv.entity.OrderDetail;
import com.cv.inv.entry.common.OrderTableModel;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.service.OrderDetailService;
import com.cv.inv.service.OrderService;
import com.cv.inv.service.VouIdService;
import com.cv.inv.ui.commom.VouFormatFactory;
import com.cv.inv.util.GenVouNoImpl;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class OrderDialog extends javax.swing.JDialog implements SelectionObserver {

    private static final Logger log = LoggerFactory.getLogger(OrderDialog.class);
    private final Image historyIcon = new ImageIcon(this.getClass().getResource("/images/history_icon.png")).getImage();

    @Autowired
    private OrderTableModel orderTableModel;
    @Autowired
    private VouIdService vouIdService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderSearchByCustomerDialog byCustomerDialog;
    @Autowired
    private OrderDetailService detailService;
    private GenVouNoImpl vouEngine;
    private Customer customer;
    private CustomerAutoCompleter traderAutoCompleter;
    private Order order;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Creates new form OrderDialog
     */
    public OrderDialog() {
        super(Global.parentForm, true);
        try {
            initComponents();
            txtOrderId.setFormatterFactory(new VouFormatFactory());
        } catch (ParseException ex) {
        }

    }

    public void initMain() {
        initTable();
        initAutoCompleter();
        genVouNo();
        lblStatus.setText("NEW");
    }

    private void initAutoCompleter() {
        traderAutoCompleter = new CustomerAutoCompleter(txtCustomer, Global.listCustomer, null);
        traderAutoCompleter.setTrader(customer);
        txtAddress.setText(customer.getAddress());
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(vouIdService, "Order", Util1.getPeriod(Util1.getTodayDate()));
        txtOrderId.setText(vouEngine.genVouNo());
    }

    private void initTable() {
        clear();
        orderTableModel.setObserver(this);
        orderTableModel.setTable(tblOrder);
        orderTableModel.addNewRow();
        tblOrder.setModel(orderTableModel);
        tblOrder.getTableHeader().setFont(Global.tblHeaderFont);
        tblOrder.getTableHeader().setBackground(ColorUtil.btnEdit);
        tblOrder.getTableHeader().setForeground(ColorUtil.foreground);
        tblOrder.getColumnModel().getColumn(0).setPreferredWidth(15);
        tblOrder.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblOrder.getColumnModel().getColumn(2).setPreferredWidth(15);
        tblOrder.getColumnModel().getColumn(3).setPreferredWidth(20);
        tblOrder.getColumnModel().getColumn(4).setPreferredWidth(20);
        tblOrder.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblOrder.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblOrder.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());

        tblOrder.setDefaultRenderer(Float.class, new TableCellRender());
        tblOrder.setDefaultRenderer(Object.class, new TableCellRender());
        tblOrder.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
    }

    private void saveOrder() {
        if (isValidEntry()) {
            try {
                orderService.save(order, orderTableModel.getListOrder());
                orderTableModel.clear();
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                this.dispose();
            } catch (Exception e) {
                log.error("Save Order :" + e.getMessage());
            }
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtOrderId.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Order Id.");
        }
        if (txtCustomer.getText().isEmpty() || traderAutoCompleter.getTrader() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Customer.");
        }
        if (status) {
            order = new Order();
            order.setOrderCode(txtOrderId.getText());
            order.setDesp(txtDesp.getText());
            order.setTrader(traderAutoCompleter.getTrader());
            order.setIsOrder(Boolean.TRUE);
            order.setOrderDate(Util1.getTodayDate());
            order.setOrderTotal(Util1.getFloat(txtTotalAmt.getValue()));
            order.setOrderAddres(txtAddress.getText());
        }
        return status;
    }

    private void searchOrderByCustoemr() {
        if (traderAutoCompleter.getTrader() != null) {
            byCustomerDialog.setObserver(this);
            byCustomerDialog.setCustomer(traderAutoCompleter.getTrader());
            byCustomerDialog.initMain();
            byCustomerDialog.setIconImage(historyIcon);
            byCustomerDialog.setSize(Global.width - 800, Global.height - 500);
            byCustomerDialog.setLocationRelativeTo(null);
            byCustomerDialog.setVisible(true);
        }
    }

    private void calTotalAmount() {
        if (orderTableModel.getListOrder() != null) {
            float ttlAmt = 0.0f;
            ttlAmt = orderTableModel.getListOrder().stream().map(od -> Util1.getFloat(od.getAmount())).reduce(ttlAmt, (accumulator, _item) -> accumulator + _item);
            txtTotalAmt.setValue(ttlAmt);
        }
    }

    private void searchByOrderCode(Order order) {
        traderAutoCompleter.setTrader((Customer) order.getTrader());
        txtDesp.setText(order.getDesp());
        txtOrderId.setText(order.getOrderCode());
        txtTotalAmt.setValue(Util1.getFloat(order.getOrderTotal()));
        lblStatus.setText("EDIT");
        List<OrderDetail> search = detailService.search(order.getOrderCode());
        orderTableModel.setListOrder(search);
        orderTableModel.addNewRow();

    }

    private void clear() {
        orderTableModel.clear();
        txtCustomer.setText(null);
        txtDesp.setText(null);
        txtOrderId.setText(null);
        txtTotalAmt.setValue(0.0);

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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        txtOrderId = new javax.swing.JFormattedTextField();
        Address = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrder = new javax.swing.JTable();
        btnSave = new javax.swing.JButton();
        btnSave1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtTotalAmt = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Order");

        jPanel1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jPanel1AncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Order No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Remark");

        txtDesp.setFont(Global.textFont);
        txtDesp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDespActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Customer");

        txtCustomer.setFont(Global.textFont);
        txtCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCustomerActionPerformed(evt);
            }
        });

        txtOrderId.setEditable(false);
        txtOrderId.setFont(Global.textFont);

        Address.setFont(Global.lableFont);
        Address.setText("Address");

        txtAddress.setFont(Global.textFont);
        txtAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddressActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtOrderId)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtCustomer)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDesp)
                .addGap(18, 18, 18)
                .addComponent(Address)
                .addGap(18, 18, 18)
                .addComponent(txtAddress)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOrderId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Address)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblOrder.setFont(Global.textFont);
        tblOrder.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblOrder);

        btnSave.setBackground(ColorUtil.btnEdit);
        btnSave.setFont(Global.lableFont);
        btnSave.setForeground(ColorUtil.foreground);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnSave1.setBackground(ColorUtil.btnEdit);
        btnSave1.setFont(Global.lableFont);
        btnSave1.setForeground(ColorUtil.foreground);
        btnSave1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/time_machine_18px.png"))); // NOI18N
        btnSave1.setText("History");
        btnSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Total Amount :");

        txtTotalAmt.setEditable(false);
        txtTotalAmt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtTotalAmt.setFont(Global.amtFont);

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txtTotalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSave1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSave))))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnSave1)
                    .addComponent(jLabel4)
                    .addComponent(txtTotalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus))
                .addGap(9, 9, 9))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtDespActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDespActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDespActionPerformed

    private void txtCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomerActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveOrder();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void jPanel1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jPanel1AncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1AncestorAdded

    private void btnSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave1ActionPerformed
        // TODO add your handling code here:
        searchOrderByCustoemr();
    }//GEN-LAST:event_btnSave1ActionPerformed

    private void txtAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddressActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Address;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSave1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblOrder;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtOrderId;
    private javax.swing.JFormattedTextField txtTotalAmt;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("CAL-TOTAL")) {
            calTotalAmount();
        }
        if (source.toString().equals("ORDER")) {
            Order od = (Order) selectObj;
            searchByOrderCode(od);
        }
    }

}
