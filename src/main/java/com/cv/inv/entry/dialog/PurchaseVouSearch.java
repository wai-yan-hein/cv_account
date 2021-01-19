/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.dialog;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.Supplier;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.SupplierAutoCompleter;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.PurHis;
import com.cv.inv.entity.PurHisDetail;
import com.cv.inv.entry.PurchaseEntry;
import com.cv.inv.entry.common.CodeTableModel;
import com.cv.inv.entry.common.PurVouSearchTableModel;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.VouStatusAutoCompleter;
import com.cv.inv.service.PurchaseDetailService;
import com.cv.inv.service.PurchaseHisService;
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
public class PurchaseVouSearch extends javax.swing.JDialog implements KeyListener {

    /**
     * Creates new form purVouSearchDialog
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseVouSearch.class);
    @Autowired
    private PurVouSearchTableModel purVouTableModel;
    @Autowired
    private CodeTableModel codeTableModel;
    @Autowired
    private PurchaseHisService purHisService;
    @Autowired
    private PurchaseEntry pur;
    @Autowired
    private PurchaseDetailService pdService;
    private VouStatusAutoCompleter vouCompleter;
    private SupplierAutoCompleter traderAutoCompleter;

    public PurchaseVouSearch() {
        super(Global.parentForm, true);
        initComponents();
        initKeyListener();
    }

    public void initMain() {
        initCombo();
        initTableVoucher();
        initTableStock();
        assignDefaultValue();
        setTodayDate();
        search();
    }

    private void initCombo() {
        traderAutoCompleter = new SupplierAutoCompleter(txtCus, Global.listSupplier, null);
        vouCompleter = new VouStatusAutoCompleter(txtVouStatus, Global.listVou, null);
    }

    private void initTableVoucher() {
        tblVoucher.setModel(purVouTableModel);
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);
        tblVoucher.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblVoucher.getTableHeader().setForeground(ColorUtil.foreground);
        tblVoucher.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblVoucher.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblVoucher.getColumnModel().getColumn(2).setPreferredWidth(40);
        tblVoucher.getColumnModel().getColumn(3).setPreferredWidth(130);
        tblVoucher.getColumnModel().getColumn(4).setPreferredWidth(15);
        tblVoucher.getColumnModel().getColumn(5).setPreferredWidth(30);
        tblVoucher.setDefaultRenderer(Double.class, new TableCellRender());
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
    }

    private void initTableStock() {
        tblStock.setModel(codeTableModel);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        codeTableModel.addEmptyRow();
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblStock.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
    }

    private void assignDefaultValue() {
        traderAutoCompleter.setTrader((Supplier) Global.defaultSupplier);
        vouCompleter.setVouStatus(Global.defaultVouStatus);

    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void search() {
        LOGGER.info("Search History.");
        String customerId;
        String vouStatusId;
        String fromDate = Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd");
        String toDate = Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd");

        if (txtCus.getText().isEmpty() || traderAutoCompleter.getTrader() == null) {
            customerId = "-";
        } else {
            customerId = traderAutoCompleter.getTrader().getCode();
        }
        if (vouCompleter.getVouStatus() == null || txtVouStatus.getText().isEmpty()) {
            vouStatusId = "-";
        } else {
            vouStatusId = vouCompleter.getVouStatus().getVouStatusCode();
        }

        String remark = Util1.isNull(txtRemark.getText(), "-");
        List<PurHis> listHis = purHisService.search(fromDate, toDate, customerId, vouStatusId, remark);
        purVouTableModel.setListPurHis(listHis);
        calAmount();

    }

    private void calAmount() {
        float ttlAmt = 0.0f;
        if (!purVouTableModel.getListPurHis().isEmpty()) {
            ttlAmt = purVouTableModel.getListPurHis().stream().map(p -> p.getVouTotal()).reduce(ttlAmt, (accumulator, _item) -> accumulator + _item);
            txtTotalAmt.setValue(ttlAmt);
            txtTotalRecord.setValue(purVouTableModel.getListPurHis().size());
        }
    }

    private void initKeyListener() {
        txtFromDate.getDateEditor().getUiComponent().setName("txtFromDate");
        txtFromDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtToDate.getDateEditor().getUiComponent().setName("txtToDate");
        txtToDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtVouStatus.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        txtVouStatus.addKeyListener(this);
        txtMachine.addKeyListener(this);
        txtUser.addKeyListener(this);
        txtCusGroup.addKeyListener(this);
        tblStock.addKeyListener(this);
    }

    private void select() {

        int row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
        PurHis vs = purVouTableModel.getSelectVou(row);
        if (vs != null) {
            String vouNo = vs.getVouNo();
            PurHis dmgHis = purHisService.findById(vouNo);
            List<PurHisDetail> listDetail = pdService.search(vouNo);
            this.dispose();
            pur.setPurchaseVoucher(dmgHis, listDetail);
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
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        txtCus = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        txtVouStatus = new javax.swing.JTextField();
        txtRemark = new javax.swing.JTextField();
        txtMachine = new javax.swing.JTextField();
        txtUser = new javax.swing.JTextField();
        txtCusGroup = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        lblTtlRecord = new javax.swing.JLabel();
        lblTtlAmount = new javax.swing.JLabel();
        btnSelect = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        txtTotalRecord = new javax.swing.JFormattedTextField();
        txtTotalAmt = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Purchase Voucher Search");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Customer");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Vou No");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("V-Status");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Ref.Vou.");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Machine");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("User");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Date");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Cus-G");

        txtFromDate.setDateFormatString("dd/MM/yyyy");

        jLabel3.setText("To");

        txtToDate.setDateFormatString("dd/MM/yyyy");

        txtCus.setName("txtCus"); // NOI18N
        txtCus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusFocusGained(evt);
            }
        });

        txtVouNo.setName("txtVouNo"); // NOI18N

        txtVouStatus.setName("txtVouStatus"); // NOI18N
        txtVouStatus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouStatusFocusGained(evt);
            }
        });

        txtRemark.setName("txtRemark"); // NOI18N

        txtMachine.setName("txtMachine"); // NOI18N

        txtUser.setName("txtUser"); // NOI18N

        txtCusGroup.setName("txtCusGroup"); // NOI18N

        tblStock.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblStock);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel12))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtCus)
                            .addComponent(txtVouNo)
                            .addComponent(txtVouStatus)
                            .addComponent(txtRemark)
                            .addComponent(txtMachine)
                            .addComponent(txtUser)
                            .addComponent(txtCusGroup)))))
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
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtVouStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtMachine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addContainerGap())
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

    private void txtVouStatusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouStatusFocusGained
        txtVouStatus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouStatusFocusGained

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSelect;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblTtlAmount;
    private javax.swing.JLabel lblTtlRecord;
    private javax.swing.JTable tblStock;
    private javax.swing.JTable tblVoucher;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtCusGroup;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtMachine;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JFormattedTextField txtTotalAmt;
    private javax.swing.JFormattedTextField txtTotalRecord;
    private javax.swing.JTextField txtUser;
    private javax.swing.JTextField txtVouNo;
    private javax.swing.JTextField txtVouStatus;
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
                    txtCusGroup.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtToDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtToDate":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtFromDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCus.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtCus":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtToDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtVouNo":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouStatus.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtVouStatus":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtVouStatus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtMachine.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtMachine":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtUser.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtUser":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtMachine.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCusGroup.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtCusGroup":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtUser.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtFromDate.requestFocus();
                }
                tabToTable(e);
                break;
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblStock.requestFocus();
            if (tblStock.getRowCount() >= 0) {
                tblStock.setRowSelectionInterval(0, 0);
            }
        }
    }
}
