/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.CurrencyKey;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.service.CurrencyService;
import com.cv.inv.service.RetInService;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.RetInDetailHis;
import com.cv.inv.entry.common.ReturnInTableModel;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockUnitCellEditor;
import com.cv.inv.service.LocationService;
import com.cv.inv.service.VouIdService;
import com.cv.inv.ui.commom.VouFormatFactory;
import com.cv.inv.ui.util.RetInVouSearch;
import com.cv.inv.util.GenVouNoImpl;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.jdesktop.observablecollections.ObservableCollections;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class ReturnIn extends javax.swing.JPanel implements SelectionObserver, KeyListener {

    /**
     * Creates new form ReturnIn
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ReturnIn.class);
    private LoadingObserver loadingObserver;
    private List<RetInDetailHis> listDetail = ObservableCollections.observableList(new ArrayList());
    private TraderAutoCompleter traderAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private Gl gl;
    private GenVouNoImpl vouEngine = null;
    private String cusId;
    private String currCode;
    private String locId;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }
    @Autowired
    private ReturnInTableModel returnInTableModel;

    @Autowired
    private RetInService retInService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private VouIdService voudIdService;
    @Autowired
    private RetInVouSearch retInVouSearch;

    public ReturnIn() {
        initComponents();
    }

    private void initMain() {
        initTable();
        actionMapping();
        initKeyListener();
        setTodayDate();
        initCombo();
        assignDefaultValue();
        initTextBoxAlign();
        initTextBoxValue();
        genVouNo();
    }

    private void initTable() {
        tblReturnIn.setModel(returnInTableModel);
        returnInTableModel.setParent(tblReturnIn);
        tblReturnIn.getTableHeader().setFont(Global.lableFont);
        tblReturnIn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblReturnIn.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblReturnIn.getColumnModel().getColumn(1).setPreferredWidth(300);
        tblReturnIn.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblReturnIn.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblReturnIn.getColumnModel().getColumn(4).setPreferredWidth(40);
        tblReturnIn.getColumnModel().getColumn(5).setPreferredWidth(30);
        tblReturnIn.getColumnModel().getColumn(6).setPreferredWidth(60);
        tblReturnIn.getColumnModel().getColumn(7).setPreferredWidth(70);
        tblReturnIn.setDefaultRenderer(Float.class, new TableCellRender());
        tblReturnIn.setDefaultRenderer(Double.class, new TableCellRender());
        tblReturnIn.setDefaultRenderer(Object.class, new TableCellRender());
        returnInTableModel.addNewRow();
        addRetInTableModelListener();
        tblReturnIn.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblReturnIn.getColumnModel().getColumn(5).setCellEditor(new StockUnitCellEditor());
        tblReturnIn.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");

    }

    private void initKeyListener() {
        txtVouNo.addKeyListener(this);
        txtRetInDate.getDateEditor().getUiComponent().setName("txtRetInDate");
        txtRetInDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtCus.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        butGetSaleItem.addKeyListener(this);
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblReturnIn.requestFocus();
            if (tblReturnIn.getRowCount() >= 0) {
                tblReturnIn.setRowSelectionInterval(0, 0);
            }
        }
    }

    private void setTodayDate() {
        txtRetInDate.setDate(Util1.getTodayDate());
    }

    private void initCombo() {
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        currencyAutoCompleter.setSelectionObserver(this);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        locationAutoCompleter.setSelectionObserver(this);
        traderAutoCompleter = new TraderAutoCompleter(txtCus, Global.listTrader, null);
        traderAutoCompleter.setSelectionObserver(this);

    }

    private void initTextBoxAlign() {
        txtVouTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouPaid.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);
        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtVouPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        } catch (Exception ex) {
            LOGGER.error("setFormatterFactory : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());

        }

    }

    private void initTextBoxValue() {

        txtVouTotal.setValue(0.0);
        txtVouPaid.setValue(0.0);
        txtVouBalance.setValue(0.0);

    }

    private void addRetInTableModelListener() {
        tblReturnIn.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();

                //if (column >= 0) {
                //Need to add action for updating table
                listDetail = returnInTableModel.getRetInDetailHis();
                calculateTotalAmount();
                //}
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="calculateTotalAmount">
    private void calculateTotalAmount() {
        Double totalAmount = new Double(0);

        try {
            for (RetInDetailHis sdh : listDetail) {
                totalAmount += NumberUtil.NZero(sdh.getAmount());
            }
            txtVouTotal.setValue(totalAmount);
            txtVouPaid.setValue(totalAmount);

        } catch (NullPointerException ex) {
            LOGGER.error(ex.toString());
        }

        txtVouBalance.setValue(totalAmount - (NumberUtil.NZero(txtVouPaid.getValue())));
    }// </editor-fold>

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
        jLabel3 = new javax.swing.JLabel();
        txtCus = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        butGetSaleItem = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtRetInDate = new com.toedter.calendar.JDateChooser();
        txtVouNo = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReturnIn = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        txtVouPaid = new javax.swing.JFormattedTextField();
        txtVouBalance = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont
        );
        jLabel1.setText("Vou No");

        jLabel3.setFont(Global.lableFont
        );
        jLabel3.setText("Customer");

        txtCus.setFont(Global.textFont);
        txtCus.setName("txtCus"); // NOI18N
        txtCus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont
        );
        jLabel4.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel5.setFont(Global.lableFont
        );
        jLabel5.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N

        jLabel6.setFont(Global.lableFont
        );
        jLabel6.setText("Currency");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setName("txtCurrency"); // NOI18N
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        butGetSaleItem.setFont(Global.lableFont);
        butGetSaleItem.setText("Get Sale Item");
        butGetSaleItem.setName("butGetSaleItem"); // NOI18N
        butGetSaleItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGetSaleItemActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Return In Date");

        txtRetInDate.setDateFormatString("dd/MM/yyyy");
        txtRetInDate.setFont(Global.lableFont);

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtRetInDate, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                    .addComponent(txtCus))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtLocation))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(butGetSaleItem)
                            .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel5)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtRetInDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butGetSaleItem))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblReturnIn.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblReturnIn);

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total :");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Paid :");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Vou Balance :");

        txtVouTotal.setEditable(false);

        txtVouPaid.setEditable(false);
        txtVouPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouPaidActionPerformed(evt);
            }
        });

        txtVouBalance.setEditable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 13, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtVouTotal)
                    .addComponent(txtVouPaid)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtVouPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void butGetSaleItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGetSaleItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_butGetSaleItemActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        initMain();
        txtVouNo.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void txtVouPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouPaidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouPaidActionPerformed

    private void txtCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butGetSaleItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblReturnIn;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtRetInDate;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {

    }

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
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTextFieldDateEditor) {
            ctrlName = ((JTextFieldDateEditor) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtVouNo":
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCurrency.requestFocus();
                } else {
                    txtRetInDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtRetInDate":
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtRetInDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtCus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtLocation.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtCus":
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN
                        || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtLocation.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtLocation":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRetInDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtCurrency":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtLocation.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtCus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    butGetSaleItem.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "butGetSaleItem":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN
                        || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
        }
    }

    private void actionMapping() {
        //Enter event on tblSale
        //tblReturnIn.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        //tblReturnIn.getActionMap().put("ENTER-Action", actionTblRetInEnterKey);

        //F8 event on tblRetIn
        tblReturnIn.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblReturnIn.getActionMap().put("F8-Action", actionItemDelete);
    }

    private Action actionTblRetInEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                tblReturnIn.getCellEditor().stopCellEditing();
            } catch (Exception ex) {
            }

            int row = tblReturnIn.getSelectedRow();
            int col = tblReturnIn.getSelectedColumn();
            listDetail = returnInTableModel.getCurrentRow();
            RetInDetailHis sdh = listDetail.get(row);

            if (col == 0 && sdh.getStock().getStockCode() != null) {
                tblReturnIn.setColumnSelectionInterval(3, 3); //Move to Qty
                returnInTableModel.addNewRow();
            } else if (col == 1 && sdh.getStock().getStockCode() != null) {
                tblReturnIn.setColumnSelectionInterval(3, 3); //Move to Qty
            } else if (col == 2 && sdh.getStock().getStockCode() != null) {
                tblReturnIn.setColumnSelectionInterval(3, 3); //Move to Qty
            } else if (col == 3 && sdh.getQty() != null) {
                tblReturnIn.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 4 && sdh.getStdWt() != null) {
                tblReturnIn.setColumnSelectionInterval(5, 5); //Move to Unit
            } else if (col == 5 && sdh.getStockUnit().getItemUnitName() != null) {
                tblReturnIn.setColumnSelectionInterval(6, 6); //Move to Sale Price
            } else if (col == 6 && sdh.getPrice() != null) {
                tblReturnIn.setColumnSelectionInterval(7, 7); //Move to Discount
            } else if (col == 7) {
                if ((row + 1) <= listDetail.size()) {
                    tblReturnIn.setRowSelectionInterval(row + 1, row + 1);
                }
                tblReturnIn.setColumnSelectionInterval(0, 0); //Move to Code
            }

        }
    };

    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            RetInDetailHis retdh;

            if (tblReturnIn.getSelectedRow() >= 0) {
                retdh = listDetail.get(tblReturnIn.getSelectedRow());

                int n = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete?",
                        "Sale item delete", JOptionPane.YES_NO_OPTION);

                if (retdh.getStock() != null && n == 0) {
                    try {
                        listDetail.remove(tblReturnIn.getSelectedRow());
                        calculateTotalAmount();
                    } catch (Exception ex) {
                        LOGGER.error("actionItemDelete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    }

                }
            }
        }
    };

    private void assignDefaultValue() {
        String currCode = Global.sysProperties.get("system.parent.currency");
        String locId = Global.sysProperties.get("system.default.location");
        Currency currency = currencyService.findById(new CurrencyKey(currCode, Global.compId));
        Location location = locationService.findById(locId);
        currencyAutoCompleter.setCurrency(currency);
        locationAutoCompleter.setLocation(location);

    }

    public void clear() {
        //Clear text box.
        txtVouNo.setText("");
        txtRetInDate.setDate(Util1.getTodayDate());
        txtCus.setText("");
        txtRemark.setText("");
        lblStatus.setText("NEW");
        vouEngine.setPeriod(Util1.getPeriod(txtRetInDate.getDate()));
        initTextBoxValue();
        genVouNo();
        assignDefaultValue();
        returnInTableModel.clearRetInTable();
    }

    public void newForm() {
        clear();
    }

    public void save() {
        if (isValidEntry() && returnInTableModel.isValidEntry()) {
            try {
                retInService.save(gl, listDetail);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
            } catch (Exception ex) {
                LOGGER.error("saveRetIN : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            }

            newForm();
        }

    }

    public void history() {
        retInVouSearch.initMain();
        retInVouSearch.setSize(Global.width - 400, Global.height - 200);
        retInVouSearch.setResizable(false);
        //retInVouSearch.setLocation(Global.width/2, Global.height/2);
        retInVouSearch.setLocationRelativeTo(this);
        retInVouSearch.setSelectionObserver(this);
        retInVouSearch.setVisible(true);
    }

    private boolean isValidEntry() {
        boolean status = true;
        Location location = null;
        Currency currency = null;
        Trader trader = null;
        try {
            if (locationAutoCompleter.getLocation() != null) {
            location = locationAutoCompleter.getLocation();
        }
        if (currencyAutoCompleter.getCurrency() != null) {
            currency = currencyAutoCompleter.getCurrency();
        }
        if (traderAutoCompleter.getTrader() != null) {
            trader = traderAutoCompleter.getTrader();

        }
        if (txtVouNo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid voucher no.",
                    "Invalid Voucher ID", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (txtCus.getText() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Customer cannot be blank.",
                    "No customer.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCus.requestFocusInWindow();
        } else if (location.getLocationId() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Location cannot be blank.",
                    "Select Location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocusInWindow();
        } else if (currency.getKey().getCode() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Currency cannot be blank.",
                    "Select Currency", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCurrency.requestFocusInWindow();
        } else {
            gl = new Gl();
            gl.setVouNo(txtVouNo.getText());
            gl.setTraderId(NumberUtil.NZeroL(trader.getId()));
            gl.setRemark(txtRemark.getText());
            gl.setGlDate(txtRetInDate.getDate());
            gl.setCreatedDate(Util1.getTodayDate());
            gl.setCurrency(currency.getKey().getCode());
            gl.setCompId(Global.compId);
            gl.setSplitId(6);
            gl.setTranSource("ÄCCOUNT-RETOUT");
            gl.setLocation(location.getLocationId());
            gl.setCreatedBy(Global.loginUser.getUserId().toString());
            gl.setVouTotal(NumberUtil.getDouble(txtVouTotal.getText()));
            gl.setPaid(NumberUtil.getDouble(txtVouPaid.getText()));
            gl.setBalance(NumberUtil.getDouble(txtVouBalance.getText()));

        }
        } catch (Exception ex) {
            LOGGER.error("isValidEntry : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            
        }
        
        return status;
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(voudIdService, "RetIn", Util1.getPeriod(txtRetInDate.getDate()));
        txtVouNo.setText(vouEngine.genVouNo());
    }

}
