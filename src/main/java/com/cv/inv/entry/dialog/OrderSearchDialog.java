/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.dialog;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Order;
import com.cv.inv.entry.PurchaseEntry;
import com.cv.inv.entry.common.OrderSearchTableModel;
import com.cv.inv.service.OrderService;
import com.cv.inv.service.PurchaseDetailService;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class OrderSearchDialog extends javax.swing.JDialog implements KeyListener {

    /**
     * Creates new form purVouSearchDialog
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSearchDialog.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private PurchaseEntry pur;
    @Autowired
    private PurchaseDetailService pdService;
    @Autowired
    private OrderSearchTableModel orderSearchTableModel;
    private TraderAutoCompleter traderAutoCompleter;
    private SelectionObserver observer;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public OrderSearchDialog() {
        super(Global.parentForm, true);
        initComponents();
        initKeyListener();
    }

    public void initMain() {
        initCombo();
        initTableVoucher();
        assignDefaultValue();
        setTodayDate();
        search();
    }

    private void initCombo() {
        traderAutoCompleter = new TraderAutoCompleter(txtCus, Global.listTrader, null);
    }

    private void initTableVoucher() {
        tblVoucher.setModel(orderSearchTableModel);
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblVoucher.getTableHeader().setForeground(ColorUtil.foreground);
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(40);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(130);
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblVoucher.setDefaultRenderer(Double.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
    }

    private void assignDefaultValue() {
        //traderAutoCompleter.setTrader(Global.defaultSupplier);
    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void search() {
        LOGGER.info("Search History.");
        String customerId;
        String orderCode;
        String fromDate = Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd");
        String toDate = Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd");
        if (traderAutoCompleter.getTrader() == null || txtCus.getText().isEmpty()) {
            customerId = "-";
        } else {
            customerId = traderAutoCompleter.getTrader().getId().toString();
        }
        orderCode = Util1.isNull(txtOrderno.getText(), "-");
        List<Order> findActiveOrder = orderService.findActiveOrder(fromDate, toDate, customerId, orderCode);
        orderSearchTableModel.setListOrder(findActiveOrder);
        calAmount();

    }

    private void calAmount() {
        double ttlAmt = 0.0;
        if (!orderSearchTableModel.getListOrder().isEmpty()) {

        }
    }

    private void initKeyListener() {
        txtFromDate.getDateEditor().getUiComponent().setName("txtFromDate");
        txtFromDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtToDate.getDateEditor().getUiComponent().setName("txtToDate");
        txtToDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtOrderno.addKeyListener(this);
        txtCus.addKeyListener(this);

    }

    private void select() {
        if (tblVoucher.getSelectedRow() >= 0) {
            int row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
            Order od = orderSearchTableModel.getOrder(row);
            observer.selected("ORDER", od);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "Please select the voucher.",
                    "No Voucher Selected", JOptionPane.ERROR_MESSAGE);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        txtCus = new javax.swing.JTextField();
        txtOrderno = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        lblTtlRecord = new javax.swing.JLabel();
        lblTtlAmount = new javax.swing.JLabel();
        btnSelect = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        txtTotalRecord = new javax.swing.JFormattedTextField();
        txtTotalAmt = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Order List Search");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Customer");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Order No");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Date");

        txtFromDate.setDateFormatString("dd/MM/yyyy");

        jLabel3.setText("To");

        txtToDate.setDateFormatString("dd/MM/yyyy");

        txtCus.setName("txtCus"); // NOI18N
        txtCus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusFocusGained(evt);
            }
        });

        txtOrderno.setName("txtOrderno"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtCus)
                    .addComponent(txtOrderno)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel11)
                        .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtOrderno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(387, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jLabel3, txtFromDate, txtToDate});

        tblVoucher.setFont(Global.textFont);
        tblVoucher.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVoucher.setRowHeight(Global.tblRowHeight);
        tblVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblVoucher);

        lblTtlRecord.setFont(Global.lableFont);
        lblTtlRecord.setText("Total Record : ");

        lblTtlAmount.setFont(Global.lableFont);
        lblTtlAmount.setText("Total Amount :");

        btnSelect.setBackground(ColorUtil.mainColor);
        btnSelect.setFont(Global.lableFont);
        btnSelect.setForeground(ColorUtil.foreground);
        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/select-button.png"))); // NOI18N
        btnSelect.setText("Select");
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        btnSearch.setBackground(ColorUtil.mainColor);
        btnSearch.setFont(Global.lableFont);
        btnSearch.setForeground(ColorUtil.foreground);
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search-button.png"))); // NOI18N
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        txtTotalRecord.setEditable(false);
        txtTotalRecord.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalRecord.setFont(Global.amtFont);

        txtTotalAmt.setEditable(false);
        txtTotalAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalAmt.setFont(Global.amtFont);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTtlRecord)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTotalRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTtlAmount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTotalAmt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSelect))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(btnSelect)
                    .addComponent(lblTtlRecord)
                    .addComponent(lblTtlAmount)
                    .addComponent(txtTotalRecord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        select();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSelectActionPerformed

    private void tblVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherMouseClicked
        if (evt.getClickCount() == 2) {
            select();
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_tblVoucherMouseClicked

    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        txtCus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusFocusGained

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSelect;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTtlAmount;
    private javax.swing.JLabel lblTtlRecord;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtCus;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtOrderno;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JFormattedTextField txtTotalAmt;
    private javax.swing.JFormattedTextField txtTotalRecord;
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
        if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTextFieldDateEditor) {
            ctrlName = ((JTextFieldDateEditor) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtFromDate":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtToDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                break;
            case "txtToDate":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtFromDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCus.requestFocus();
                }
                break;
            case "txtCus":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtToDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtOrderno.requestFocus();
                }
                break;

        }
    }

}
