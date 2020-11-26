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
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.CurrencyKey;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.RetOutDetailHis;
import com.cv.inv.entity.RetOutHis;
import com.cv.inv.entry.common.ReturnOutTableModel;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.service.LocationService;
import com.cv.inv.service.RetOutDetailService;
import com.cv.inv.service.RetOutService;
import com.cv.inv.service.VouIdService;
import com.cv.inv.ui.commom.VouFormatFactory;
import com.cv.inv.ui.util.RetInVouSearch;
import com.cv.inv.util.GenVouNoImpl;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;
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
public class ReturnOut extends javax.swing.JPanel implements SelectionObserver, KeyListener, PanelControl {

    /**
     * Creates new form ReturnOut
     *
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ReturnOut.class);
    private LoadingObserver loadingObserver;
    private List<RetOutDetailHis> listDetail = ObservableCollections.observableList(new ArrayList());
    private TraderAutoCompleter traderAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private GenVouNoImpl vouEngine = null;
    private Long glId;
    private RetOutHis retOut = new RetOutHis();
    private boolean isShown = false;
    @Autowired
    private ReturnOutTableModel retOutTableModel;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private VouIdService vouIdService;
    @Autowired
    private RetOutService outService;
    @Autowired
    private RetInVouSearch retInVouSearch;
    @Autowired
    private TraderService traderService;
    @Autowired
    private RetOutService retOutService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private RetOutDetailService retOutDetailService;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public ReturnOut() {
        initComponents();
    }

    private void initMain() {
        try {
            initTable();
            actionMapping();
            initKeyListener();
            setTodayDate();
            initCombo();
            assignDefaultValue();
            initTextBoxAlign();
            initTextBoxValue();
            genVouNo();
            calculateTotalAmount();

        } catch (Exception ex) {
            LOGGER.error("ReturnOut : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());

        }

    }

    private void initTable() {
        tblRetOut.setModel(retOutTableModel);
        retOutTableModel.setParent(tblRetOut);
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

        tblRetOut.setDefaultRenderer(Double.class, new TableCellRender());
        tblRetOut.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblRetOut.setDefaultRenderer(Object.class, new TableCellRender());
        tblRetOut.setDefaultRenderer(Float.class, new TableCellRender());
        tblRetOut.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblRetOut.getColumnModel().getColumn(5).setCellEditor(new StockUnitEditor());
        addRetOutTableModelListener();
        tblRetOut.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblRetOut.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        retOutTableModel.addNewRow();

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

    private void addRetOutTableModelListener() {
        tblRetOut.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();

                //if (column >= 0) {
                //Need to add action for updating table
                calculateTotalAmount();
                //}
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="calculateTotalAmount">
    private void calculateTotalAmount() {
        Double totalAmount = new Double(0);
        listDetail = retOutTableModel.getRetOutDetailHis();

        try {
            for (RetOutDetailHis sdh : listDetail) {
                totalAmount += NumberUtil.NZero(sdh.getAmount());
            }
            txtVouTotal.setValue(totalAmount);
            txtVouPaid.setValue(totalAmount);

        } catch (NullPointerException ex) {
            LOGGER.error(ex.toString());
        }

        txtVouBalance.setValue(totalAmount - (NumberUtil.NZero(txtVouPaid.getValue())));
    }// </editor-fold>

    private void setTodayDate() {
        txtRetOutDate.setDate(Util1.getTodayDate());;
    }

    private void initCombo() {
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        currencyAutoCompleter.setSelectionObserver(this);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        locationAutoCompleter.setSelectionObserver(this);
        traderAutoCompleter = new TraderAutoCompleter(txtSup, Global.listTrader, null);
        traderAutoCompleter.setSelectionObserver(this);
    }

    private void assignDefaultValue() {
        String currCode = Global.sysProperties.get("system.parent.currency");
        String locId = Global.sysProperties.get("system.default.location");
        Currency currency = currencyService.findById(new CurrencyKey(currCode, Global.compId));
        Location location = locationService.findById(locId);
        currencyAutoCompleter.setCurrency(currency);
        locationAutoCompleter.setLocation(location);

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

    private void actionMapping() {
        //F8 event on tblRetIn
        tblRetOut.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
        tblRetOut.getActionMap().put("DELETE", actionItemDelete);
    }

    private Action actionItemDelete = new AbstractAction() {
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
    private final Action actionTblRetOutEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                tblRetOut.getCellEditor().stopCellEditing();
            } catch (Exception ex) {
            }
        }
    };

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

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Return Out Date");

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

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setName("txtCurrency"); // NOI18N

        butGetPurItems.setFont(Global.lableFont);
        butGetPurItems.setText("Get Purchase Items");
        butGetPurItems.setName("butGetPurItems"); // NOI18N

        txtRetOutDate.setDateFormatString("dd/MM/yyyy");
        txtRetOutDate.setFont(Global.lableFont);
        txtRetOutDate.setName("txtRetOutDate"); // NOI18N

        txtVouNo.setEditable(false);
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRetOutDate, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                    .addComponent(txtSup)
                    .addComponent(txtRemark))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                    .addComponent(txtCurrency)
                    .addComponent(butGetPurItems))
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

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Total :");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Paid :");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Vou Balance :");

        txtVouTotal.setEditable(false);

        txtVouPaid.setEditable(false);

        txtVouBalance.setEditable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 11, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtVouTotal)
                    .addComponent(txtVouPaid)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)))
        );
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
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
        txtVouNo.requestFocus();
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
            case "RetOutVouList":
        try {
                RetOutHis vRetOut = (RetOutHis) selectObj;
                retOut = outService.findById(vRetOut.getRetOutId());
                if (Util1.getNullTo(retOut.isDeleted())) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("EDIT");
                }

                txtVouNo.setText(retOut.getRetOutId());
                txtVouTotal.setText(retOut.getVouTotal().toString());
                txtVouPaid.setText(retOut.getPaid().toString());
                txtVouBalance.setText(retOut.getBalance().toString());
                txtRemark.setText(retOut.getRemark());
                txtRetOutDate.setDate(retOut.getRetOutDate());
                Trader trader = retOut.getCustomer();
                traderAutoCompleter.setTrader(trader);
                locationAutoCompleter.setLocation(retOut.getLocation());
                currencyAutoCompleter.setCurrency(retOut.getCurrency());

                List<RetOutDetailHis> listRetOut = retOutDetailService.search(retOut.getRetOutId());
                retOutTableModel.setRetOutDetailList(listRetOut);

            } catch (Exception ex) {
                LOGGER.error("selected : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());

            }

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
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtRetOutDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtSup.requestFocus();
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
        if (isValidEntry()) {
            List<String> delList = retOutTableModel.getDelList();
            try {
                outService.save(retOut, retOutTableModel.getListRetInDetail(), delList);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
            } catch (Exception ex) {
                LOGGER.error("saveRetOut : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            }

            clear();
        }
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
            } else if (txtSup.getText() == null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Supplier cannot be blank.",
                        "No Supplier.", JOptionPane.ERROR_MESSAGE);
                status = false;
                txtSup.requestFocusInWindow();
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
            } else if (listDetail.size() == 1) {
                JOptionPane.showMessageDialog(Global.parentForm, "No Purchase record.",
                        "No data.", JOptionPane.ERROR_MESSAGE);
                status = false;
            } else {

                retOut.setRetOutId(txtVouNo.getText());
                retOut.setCustomer(traderAutoCompleter.getTrader());
                retOut.setRemark(txtRemark.getText());
                retOut.setRetOutDate(txtRetOutDate.getDate());
                retOut.setCreatedDate(Util1.getTodayDate());
                retOut.setCurrency((currencyAutoCompleter.getCurrency()));
                retOut.setLocation(locationAutoCompleter.getLocation());
                retOut.setCreatedBy(Global.loginUser.getUserId().toString());
                retOut.setVouTotal(NumberUtil.getDouble(txtVouTotal.getText()));
                retOut.setPaid(NumberUtil.getDouble(txtVouPaid.getText()));
                retOut.setBalance(NumberUtil.getDouble(txtVouBalance.getText()));
                retOut.setDeleted(Util1.getNullTo(retOut.isDeleted()));
                if (lblStatus.getText().equals("NEW")) {
                    retOut.setRetOutDate(txtRetOutDate.getDate());
                } else {
                    Date tmpDate = txtRetOutDate.getDate();
                    if (!Util1.isSameDate(tmpDate, retOut.getRetOutDate())) {
                        retOut.setRetOutDate(txtRetOutDate.getDate());
                    }
                }
                if (lblStatus.getText().equals("NEW")) {
                    retOut.setCreatedBy(Global.loginUser.getUserId().toString());
                    retOut.setSession(Global.sessionId);
                } else {
                    retOut.setUpdatedBy(Global.loginUser.getUserId().toString());
                    retOut.setUpdatedDate(Util1.getTodayDate());
                }

            }

        } catch (Exception ex) {
            LOGGER.error("isValidEntry : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

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
        //   deleteRetOutDetail();
    }

//    private void newForm() {
//        clear();
//    }
    public void historyReturnOut() {
        retInVouSearch.setPanelName(this.getName());
        retInVouSearch.initMain();
        retInVouSearch.setTitle("Return Out Voucher Search");
        retInVouSearch.setSize(Global.width - 400, Global.height - 200);
        retInVouSearch.setResizable(false);
        //retInVouSearch.setLocation(Global.width/2, Global.height/2);
        retInVouSearch.setLocationRelativeTo(this);
        retInVouSearch.setSelectionObserver(this);
        retInVouSearch.setVisible(true);
    }

    public void deleteReturnOut() {
        /*if (Util1.getNullTo(gl.getDeleted())) {
        JOptionPane.showConfirmDialog(Global.parentForm, "Voucher already deleted.",
        "Return Out voucher delete", JOptionPane.ERROR);
        } else {
        int yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete?",
        "Return Out voucher delete", JOptionPane.YES_NO_OPTION);
        
        if (yes_no == 0) {
        gl.setDeleted(true);
        save();
        }
        }*/

    }

    private void deleteRetOutDetail() {
        int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                "Are you sure to delete?", "Return in item delete", JOptionPane.YES_NO_OPTION);
        if (yes_no == 0) {
            String vouNo = txtVouNo.getText();
            if (lblStatus.getText().equals("EDIT")) {
                outService.delete(vouNo);
                clear();
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
        deleteReturnOut();
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
