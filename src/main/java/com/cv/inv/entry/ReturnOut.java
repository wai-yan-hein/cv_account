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
import com.cv.accountswing.entity.Supplier;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.SupplierAutoCompleter;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.RetOutHisDetail;
import com.cv.inv.entity.RetOutHis;
import com.cv.inv.entry.common.ReturnOutTableModel;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.service.RetOutDetailService;
import com.cv.inv.service.RetOutService;
import com.cv.inv.service.VouIdService;
import com.cv.inv.ui.commom.VouFormatFactory;
import com.cv.inv.entry.dialog.RetInVouSearch;
import com.cv.inv.util.GenVouNoImpl;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class ReturnOut extends javax.swing.JPanel implements SelectionObserver, KeyListener, PanelControl {

    /**
     * Creates new form ReturnOut
     *
     */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ReturnOut.class);
    private LoadingObserver loadingObserver;
    private SupplierAutoCompleter supplierAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private GenVouNoImpl vouEngine = null;
    private RetOutHis retOut = new RetOutHis();
    private boolean isShown = false;
    @Autowired
    private ReturnOutTableModel retOutTableModel;
    @Autowired
    private VouIdService vouIdService;
    @Autowired
    private RetInVouSearch retInVouSearch;
    @Autowired
    private RetOutService retOutService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private RetOutDetailService retOutDetailService;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public LoadingObserver getLoadingObserver() {
        return loadingObserver;
    }

    public ReturnOut() {
        initComponents();
    }

    private void initMain() {
        initTable();
        actionMapping();
        //initKeyListener();
        setTodayDate();
        initCombo();
        assignDefaultValue();
        initTextBoxAlign();
        initTextBoxValue();
        genVouNo();
        isShown = true;
    }

    private void initTable() {
        retOutTableModel.setParent(tblRetOut);
        retOutTableModel.setObserver(this);
        retOutTableModel.addNewRow();
        tblRetOut.setModel(retOutTableModel);
        tblRetOut.getTableHeader().setFont(Global.lableFont);
        tblRetOut.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblRetOut.getTableHeader().setForeground(ColorUtil.foreground);
        tblRetOut.setCellSelectionEnabled(true);
        tblRetOut.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblRetOut.getColumnModel().getColumn(1).setPreferredWidth(300);
        tblRetOut.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblRetOut.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblRetOut.getColumnModel().getColumn(4).setPreferredWidth(40);
        tblRetOut.getColumnModel().getColumn(5).setPreferredWidth(30);
        tblRetOut.getColumnModel().getColumn(6).setPreferredWidth(60);
        tblRetOut.getColumnModel().getColumn(7).setPreferredWidth(70);

        tblRetOut.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());//qty
        tblRetOut.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblRetOut.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());

        tblRetOut.setDefaultRenderer(Object.class, new TableCellRender());
        tblRetOut.setDefaultRenderer(Float.class, new TableCellRender());
        tblRetOut.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblRetOut.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor());
        tblRetOut.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblRetOut.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    private void initKeyListener() {
        txtVouNo.addKeyListener(this);
        txtRetOutDate.getDateEditor().getUiComponent().setName("txtRetOutDate");
        txtRetOutDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtSup.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        butGetPurItems.addKeyListener(this);
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblRetOut.requestFocus();
            if (tblRetOut.getRowCount() >= 0) {
                tblRetOut.setRowSelectionInterval(0, 0);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="calculateTotalAmount">
    private void calculateTotalAmount() {
        float totalAmount = 0.0f;
        retOutTableModel.getRetOutDetailHis();
        totalAmount = retOutTableModel.getRetOutDetailHis().stream().map(sdh -> Util1.getFloat(sdh.getAmount())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);
        txtVouPaid.setValue(totalAmount);

        txtVouBalance.setValue(totalAmount - (NumberUtil.NZero(txtVouPaid.getValue())));
    }// </editor-fold>

    private void setTodayDate() {
        txtRetOutDate.setDate(Util1.getTodayDate());
    }

    private void initCombo() {
        if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
            traderAutoCompleter = new TraderAutoCompleter(txtSup, Global.listTrader, null, false);
        } else {
            supplierAutoCompleter = new SupplierAutoCompleter(txtSup, Global.listSupplier, null);
        }
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);

    }

    private void assignDefaultValue() {
        if (traderAutoCompleter != null) {
            traderAutoCompleter.setTrader(null);
        }
        if (supplierAutoCompleter != null) {
            supplierAutoCompleter.setTrader(null);
        }
        currencyAutoCompleter.setCurrency(Global.defalutCurrency);
        locationAutoCompleter.setLocation(Global.defaultLocation);

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

    private void actionMapping() {
        //F8 event on tblRetIn
        tblRetOut.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
        tblRetOut.getActionMap().put("DELETE", actionItemDelete);
    }

    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblRetOut.getSelectedRow() >= 0) {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Return in item delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    retOutTableModel.delete(tblRetOut.getSelectedRow());
                }
            }
        }
    };

    private void requestTable() {
        tblRetOut.changeSelection(0, 0, false, false);
        tblRetOut.requestFocus();
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
        jLabel3 = new javax.swing.JLabel();
        txtSup = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        butGetPurItems = new javax.swing.JButton();
        txtRetOutDate = new com.toedter.calendar.JDateChooser();
        txtVouNo = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRetOut = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
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

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Date");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Supplier");

        txtSup.setFont(Global.textFont);
        txtSup.setName("txtSup"); // NOI18N
        txtSup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSupActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRemarkActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        txtCurrency.setEditable(false);
        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setEnabled(false);
        txtCurrency.setName("txtCurrency"); // NOI18N

        butGetPurItems.setFont(Global.lableFont);
        butGetPurItems.setText("Get Purchase Items");
        butGetPurItems.setName("butGetPurItems"); // NOI18N

        txtRetOutDate.setDateFormatString("dd/MM/yyyy");
        txtRetOutDate.setFont(Global.textFont);
        txtRetOutDate.setName("txtRetOutDate"); // NOI18N

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
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtSup, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtVouNo)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtRetOutDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                            .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butGetPurItems)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(jLabel5)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtRetOutDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtSup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butGetPurItems))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblRetOut.setFont(Global.textFont);
        tblRetOut.setModel(new javax.swing.table.DefaultTableModel(
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
        tblRetOut.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblRetOut);

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

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Total :");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Paid :");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Vou Balance :");

        txtVouTotal.setEditable(false);
        txtVouTotal.setFont(Global.amtFont);

        txtVouPaid.setEditable(false);
        txtVouPaid.setFont(Global.amtFont);

        txtVouBalance.setEditable(false);
        txtVouBalance.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtVouPaid, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                    .addComponent(txtVouTotal)
                    .addComponent(txtVouBalance))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel8, jLabel9});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
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
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 386, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSupActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSupActionPerformed

    private void txtRemarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRemarkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        if (retOutTableModel.getListRetInDetail().size() > 0) {
            requestTable();
        } else {
            txtSup.requestFocus();
        }
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butGetPurItems;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblRetOut;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtRetOutDate;
    private javax.swing.JTextField txtSup;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "RETOUT-SELECTED":
                retOut = (RetOutHis) selectObj;
                if (Util1.getNullTo(retOut.isDeleted())) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("EDIT");
                }
                txtVouNo.setText(retOut.getVouNo());
                txtVouTotal.setValue(Util1.getFloat(retOut.getVouTotal()));
                txtVouPaid.setValue(Util1.getFloat(retOut.getPaid()));
                txtVouBalance.setValue(Util1.getFloat(retOut.getBalance()));
                txtRemark.setText(retOut.getRemark());
                txtRetOutDate.setDate(retOut.getRetOutDate());
                locationAutoCompleter.setLocation(retOut.getLocation());
                currencyAutoCompleter.setCurrency(retOut.getCurrency());
                if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
                    traderAutoCompleter.setTrader(retOut.getCustomer());
                } else {
                    supplierAutoCompleter.setTrader((Supplier) retOut.getCustomer());
                }
                List<RetOutHisDetail> listRetOut = retOutDetailService.search(retOut.getVouNo());
                retOutTableModel.setRetOutDetailList(listRetOut);
                retOutTableModel.addNewRow();
                requestTable();
                break;
            case "CAL-TOTAL":
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
                    txtRetOutDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtRetOutDate":
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (sourceObj != null) {
                        String date = ((JTextFieldDateEditor) sourceObj).getText();
                        if (date.length() == 8) {
                            String toFormatDate = Util1.toFormatDate(date);
                            txtRetOutDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                        }
                        txtSup.requestFocus();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtLocation.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtSup":
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtRemark.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtSup.requestFocus();
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
                    txtRetOutDate.getDateEditor().getUiComponent().requestFocusInWindow();
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
                    txtSup.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    butGetPurItems.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "butGetPurItems":
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

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(vouIdService, "RetOut", Util1.getPeriod(Util1.getTodayDate()));
        txtVouNo.setText(vouEngine.genVouNo());
    }

    public void saveReturnOut() {
        if (isValidEntry() && retOutTableModel.isValidEntry()) {
            if (retOut.getCustomer() != null) {
                List<String> delList = retOutTableModel.getDelList();
                try {
                    retOutService.save(retOut, retOutTableModel.getListRetInDetail(), delList);
                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.updateVouNo();
                    }
                } catch (Exception ex) {
                    log.error("saveRetOut : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                }
                clear();
            }
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Trader.");
        }
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
                txtLocation.requestFocus();
            } else if (currencyAutoCompleter.getCurrency() == null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Currency cannot be blank.",
                        "Select Currency", JOptionPane.ERROR_MESSAGE);
                status = false;
                txtCurrency.requestFocus();
            } else if (retOutTableModel.getListRetInDetail().size() == 0) {
                JOptionPane.showMessageDialog(Global.parentForm, "No Return Out  record.",
                        "No data.", JOptionPane.ERROR_MESSAGE);
                status = false;
            } else {
                retOut.setVouNo(txtVouNo.getText());
                retOut.setRemark(txtRemark.getText());
                retOut.setRetOutDate(txtRetOutDate.getDate());
                retOut.setCreatedDate(Util1.getTodayDate());
                retOut.setCurrency((currencyAutoCompleter.getCurrency()));
                retOut.setLocation(locationAutoCompleter.getLocation());
                retOut.setCreatedBy(Global.loginUser.getAppUserCode());
                retOut.setVouTotal(NumberUtil.getDouble(txtVouTotal.getText()));
                retOut.setPaid(NumberUtil.getDouble(txtVouPaid.getText()));
                retOut.setBalance(NumberUtil.getDouble(txtVouBalance.getText()));
                retOut.setDeleted(Util1.getNullTo(retOut.isDeleted()));
                retOut.setRetOutDate(txtRetOutDate.getDate());
                if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
                    retOut.setCustomer(traderAutoCompleter.getTrader());
                } else {
                    retOut.setCustomer(supplierAutoCompleter.getTrader());
                }
                if (lblStatus.getText().equals("NEW")) {
                    retOut.setSession(Global.sessionId);
                    retOut.setCreatedBy(Global.loginUser.getAppUserCode());
                    retOut.setCreatedDate(Util1.getTodayDate());
                    retOut.setMacId(Global.machineId);
                } else {
                    retOut.setUpdatedBy(Global.loginUser.getAppUserCode());
                }
            }
        } catch (HeadlessException ex) {
            log.error("isValidEntry : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }

        return status;
    }

    public void clear() {
        //Clear text box.
        txtVouNo.setText("");
        txtRetOutDate.setDate(Util1.getTodayDate());
        txtSup.setText("");
        txtRemark.setText("");
        lblStatus.setText("NEW");
        vouEngine.setPeriod(Util1.getPeriod(txtRetOutDate.getDate()));
        initTextBoxValue();
        genVouNo();
        assignDefaultValue();
        retOutTableModel.clearRetOutTable();
        retOut = new RetOutHis();
    }

    public void historyReturnOut() {
        retInVouSearch.setPanelName(this.getName());
        retInVouSearch.initMain();
        retInVouSearch.setTitle("Return Out Voucher Search");
        retInVouSearch.setSize(Global.width - 200, Global.height - 200);
        retInVouSearch.setResizable(false);
        retInVouSearch.setLocationRelativeTo(this);
        retInVouSearch.setSelectionObserver(this);
        retInVouSearch.setVisible(true);
    }

    private void delReturnOutVoucher() {
        int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                "Are you sure to delete?", "Return in item delete", JOptionPane.YES_NO_OPTION);
        if (yes_no == 0) {
            if (lblStatus.getText().equals("EDIT")) {
                try {
                    retOut.setDeleted(true);
                    saveReturnOut();
                    clear();
                } catch (Exception ex) {
                    log.error("Return Out Voucher Delete :" + ex.getMessage());
                }
            }
        }
    }

    @Override
    public void print() {
    }

    @Override
    public void save() {
        saveReturnOut();
    }

    @Override
    public void delete() {
        delReturnOutVoucher();
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        historyReturnOut();
    }

    @Override
    public void refresh() {
    }

}
