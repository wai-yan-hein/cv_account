/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Customer;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.inv.service.RetInService;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.CustomerAutoCompleter;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.RetInHisDetail;
import com.cv.inv.entity.RetInHis;
import com.cv.inv.entry.common.ReturnInTableModel;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.service.RetInDetailService;
import com.cv.inv.service.VouIdService;
import com.cv.inv.ui.commom.VouFormatFactory;
import com.cv.inv.entry.dialog.RetInVouSearch;
import com.cv.inv.util.GenVouNoImpl;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Wai Yan
 */
@Component
public class ReturnIn extends javax.swing.JPanel implements SelectionObserver, KeyListener, PanelControl {

    /**
     * Creates new form ReturnIn
     */
    private final Image historyIcon = new ImageIcon(this.getClass().getResource("/images/history_icon.png")).getImage();
    private static final Logger log = LoggerFactory.getLogger(ReturnIn.class);
    private LoadingObserver loadingObserver;
    private CustomerAutoCompleter customerAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private RetInHis retIn = new RetInHis();
    private GenVouNoImpl vouEngine = null;
    private boolean isShown = false;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public LoadingObserver getLoadingObserver() {
        return loadingObserver;
    }

    @Autowired
    private ReturnInTableModel returnInTableModel;
    @Autowired
    private RetInService retInService;
    @Autowired
    private RetInDetailService retInDetailService;
    @Autowired
    private VouIdService voudIdService;
    @Autowired
    private RetInVouSearch retInVouSearch;
    @Autowired
    private ApplicationMainFrame mainFrame;

    public ReturnIn() {
        initComponents();
        actionMapping();
        //initKeyListener();
    }

    private void initMain() {
        setTodayDate();
        initCombo();
        initTable();
        assignDefaultValue();
        initTextBoxAlign();
        initTextBoxValue();
        genVouNo();
        isShown = true;
    }

    private void initTable() {
        returnInTableModel.setObserver(this);
        tblReturnIn.setModel(returnInTableModel);
        returnInTableModel.setParent(tblReturnIn);
        returnInTableModel.addNewRow();
        tblReturnIn.getTableHeader().setFont(Global.lableFont);
        tblReturnIn.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblReturnIn.getTableHeader().setForeground(ColorUtil.foreground);
        tblReturnIn.setCellSelectionEnabled(true);
        tblReturnIn.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblReturnIn.getColumnModel().getColumn(1).setPreferredWidth(300);
        tblReturnIn.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblReturnIn.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblReturnIn.getColumnModel().getColumn(4).setPreferredWidth(40);
        tblReturnIn.getColumnModel().getColumn(5).setPreferredWidth(30);
        tblReturnIn.getColumnModel().getColumn(6).setPreferredWidth(60);
        tblReturnIn.getColumnModel().getColumn(7).setPreferredWidth(70);

        tblReturnIn.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblReturnIn.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor());
        tblReturnIn.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//qty
        tblReturnIn.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblReturnIn.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());

        tblReturnIn.setDefaultRenderer(Float.class, new TableCellRender());
        tblReturnIn.setDefaultRenderer(Double.class, new TableCellRender());
        tblReturnIn.setDefaultRenderer(Object.class, new TableCellRender());

        tblReturnIn.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblReturnIn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblReturnIn.changeSelection(0, 0, false, false);
        tblReturnIn.requestFocus();
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
        if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
            traderAutoCompleter = new TraderAutoCompleter(txtCus, Global.listTrader, null, false,0);
        } else {
            customerAutoCompleter = new CustomerAutoCompleter(txtCus, Global.listCustomer, null);
        }
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);

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
        } catch (ParseException ex) {
            log.error("setFormatterFactory : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

    }

    private void initTextBoxValue() {

        txtVouTotal.setValue(0.0);
        txtVouPaid.setValue(0.0);
        txtVouBalance.setValue(0.0);

    }

    private void calculateTotalAmount() {
        float totalAmount = 0.0f;
        totalAmount = returnInTableModel.getRetInDetailHis().stream().map(sdh -> Util1.getFloat(sdh.getAmount())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);
        txtVouPaid.setValue(totalAmount);
        txtVouBalance.setValue(totalAmount - (Util1.getFloat(txtVouPaid.getValue())));
    }

    private void requestTable() {
        tblReturnIn.changeSelection(0, 0, false, false);
        tblReturnIn.requestFocus();
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, java.awt.Color.lightGray));

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

        txtCurrency.setEditable(false);
        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setEnabled(false);
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
                        .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtRetInDate, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
                    .addComponent(txtCus))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(butGetSaleItem)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtCurrency))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6});

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

        tblReturnIn.setFont(Global.textFont);
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
        tblReturnIn.setRowHeight(Global.tblRowHeight);
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

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, java.awt.Color.lightGray));

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total :");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Paid :");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Vou Balance :");

        txtVouTotal.setEditable(false);
        txtVouTotal.setFont(Global.amtFont);

        txtVouPaid.setEditable(false);
        txtVouPaid.setFont(Global.amtFont);
        txtVouPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouPaidActionPerformed(evt);
            }
        });

        txtVouBalance.setEditable(false);
        txtVouBalance.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtVouPaid, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .addComponent(txtVouTotal)
                    .addComponent(txtVouBalance))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel2, jLabel9});

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
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
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        if (returnInTableModel.getListRetInDetail().size() > 0) {
            requestTable();
        } else {
            txtCus.requestFocus();
        }
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
        switch (source.toString()) {
            case "RETIN-SELECTED":
                retIn = (RetInHis) selectObj;
                if (Util1.getNullTo(retIn.isDeleted())) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("EDIT");
                }
                txtVouNo.setText(retIn.getVouNo());
                txtVouTotal.setValue(Util1.getFloat(retIn.getVouTotal()));
                txtVouPaid.setValue(Util1.getFloat(retIn.getPaid()));
                txtVouBalance.setValue(Util1.getFloat(retIn.getBalance()));
                txtRemark.setText(retIn.getRemark());
                txtRetInDate.setDate(retIn.getRetInDate());
                locationAutoCompleter.setLocation(retIn.getLocation());
                currencyAutoCompleter.setCurrency(retIn.getCurrency());
                List<RetInHisDetail> listRetinDetail = retInDetailService.search(retIn.getVouNo());
                returnInTableModel.setRetInDetailList(listRetinDetail);
                if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
                    traderAutoCompleter.setTrader(retIn.getCustomer());
                } else {
                    customerAutoCompleter.setTrader((Customer) retIn.getCustomer());
                }
                returnInTableModel.addNewRow();
                requestTable();
                break;
            case "TOTAL-AMT":
                calculateTotalAmount();
                break;
        }

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
                    if (sourceObj != null) {
                        String date = ((JTextFieldDateEditor) sourceObj).getText();
                        if (date.length() == 8) {
                            String toFormatDate = Util1.toFormatDate(date);
                            txtRetInDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                        }
                        txtCus.requestFocus();
                    }
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
        //F8 event on tblRetIn
        tblReturnIn.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
        tblReturnIn.getActionMap().put("DELETE", actionItemDelete);
    }

    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblReturnIn.getSelectedRow() >= 0) {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Return in item delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    returnInTableModel.delete(tblReturnIn.getSelectedRow());
                }
            }
        }
    };

    private void assignDefaultValue() {
        if (traderAutoCompleter != null) {
            traderAutoCompleter.setTrader(null);
        }
        if (customerAutoCompleter != null) {
            customerAutoCompleter.setTrader(null);
        }
        currencyAutoCompleter.setCurrency(Global.defalutCurrency);
        locationAutoCompleter.setLocation(Global.defaultLocation);
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
        genVouNo();
    }

    @Override
    public void newForm() {
        clear();
    }

    public void saveReturnIn() {
        if (isValidEntry() && returnInTableModel.isValidEntry()) {
            if (retIn.getCustomer() != null) {
                List<String> delList = returnInTableModel.getDelList();
                try {
                    retInService.save(retIn, returnInTableModel.getListRetInDetail(), delList);
                    clear();
                    vouEngine.updateVouNo();
                } catch (Exception ex) {
                    log.error("Save Purchase :" + ex.getMessage());
                    JOptionPane.showMessageDialog(Global.parentForm, "Could'nt saved.");
                }
            } else {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Customer");
            }

        }
    }

    public void deleteReturnIn() {

        int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                "Are you sure to delete?", "Return in item delete", JOptionPane.YES_NO_OPTION);
        if (yes_no == 0) {
            if (lblStatus.getText().equals("EDIT")) {
                try {
                    retIn.setDeleted(true);
                    saveReturnIn();
                    clear();
                } catch (Exception ex) {
                    log.error("Return In Voucher Delete :" + ex.getMessage());
                }
            }
        }
    }

    public void historyReturnIn() {
        retInVouSearch.setIconImage(historyIcon);
        retInVouSearch.setPanelName(this.getName());
        retInVouSearch.initMain();
        retInVouSearch.setTitle("Return In Voucher Search");
        retInVouSearch.setSize(Global.width - 200, Global.height - 200);
        retInVouSearch.setResizable(false);
        retInVouSearch.setLocationRelativeTo(null);
        retInVouSearch.setSelectionObserver(this);
        retInVouSearch.setVisible(true);
    }

    private boolean isValidEntry() {
        boolean status = true;
        try {
            if (txtVouNo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid voucher no.",
                        "Invalid Voucher ID", JOptionPane.ERROR_MESSAGE);
                status = false;
            } else if (locationAutoCompleter.getLocation() == null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Location cannot be blank.",
                        "Select Location.", JOptionPane.ERROR_MESSAGE);
                status = false;
                txtLocation.requestFocusInWindow();
            } else if (currencyAutoCompleter.getCurrency() == null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Currency cannot be blank.",
                        "Select Currency", JOptionPane.ERROR_MESSAGE);
                status = false;
                txtCurrency.requestFocusInWindow();
            } else if (returnInTableModel.getListRetInDetail().size() == 0) {
                JOptionPane.showMessageDialog(Global.parentForm, "No Return In record.",
                        "No data.", JOptionPane.ERROR_MESSAGE);
                status = false;
            } else {
                retIn.setVouNo(txtVouNo.getText());
                retIn.setRemark(txtRemark.getText());
                retIn.setRetInDate(txtRetInDate.getDate());
                retIn.setCreatedDate(Util1.getTodayDate());
                retIn.setCurrency(currencyAutoCompleter.getCurrency());
                retIn.setLocation(locationAutoCompleter.getLocation());
                retIn.setPaid(Util1.getFloat(txtVouPaid.getValue()));
                retIn.setBalance(Util1.getFloat(txtVouBalance.getValue()));
                retIn.setCreatedBy(Global.loginUser);
                retIn.setDeleted(Util1.getNullTo(retIn.isDeleted()));
                retIn.setVouTotal(Util1.getFloat(txtVouTotal.getValue()));
                if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
                    retIn.setCustomer(traderAutoCompleter.getTrader());
                } else {
                    retIn.setCustomer(customerAutoCompleter.getTrader());
                }
                if (lblStatus.getText().equals("NEW")) {
                    retIn.setCreatedBy(Global.loginUser);
                    retIn.setSession(Global.sessionId);
                    retIn.setMacId(Global.machineId);
                } else {
                    retIn.setUpdatedBy(Global.loginUser);
                    retIn.setUpdatedDate(Util1.getTodayDate());
                }
            }
        } catch (HeadlessException ex) {
            log.error("isValidEntry : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }

        return status;
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(voudIdService, "RetIn", Util1.getPeriod(txtRetInDate.getDate()));
        txtVouNo.setText(vouEngine.genVouNo());
    }

    @Override
    public void print() {
    }

    @Override
    public void save() {
        saveReturnIn();
    }

    @Override
    public void delete() {
        deleteReturnIn();
    }

    @Override
    public void history() {
        historyReturnIn();
    }

    @Override
    public void refresh() {
    }

}
