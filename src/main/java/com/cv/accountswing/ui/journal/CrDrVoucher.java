/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.journal;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.view.VCrDrVoucher;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.VCrDrVoucherService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.COAAutoCompleter;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.journal.common.CrDrVoucherTableModel;
import com.cv.accountswing.util.Util1;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class CrDrVoucher extends javax.swing.JPanel implements KeyListener, SelectionObserver, PanelControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrDrVoucher.class);
    private int selectRow = -1;
    @Autowired
    private CrDrVoucherTableModel voucherTableModel;
    @Autowired
    private VCrDrVoucherService crdrService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private COAService cOAService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private CrDrVoucherEntry creditVoucherEntry;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private LoadingObserver loadingObserver;
    private boolean isShown = false;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    /* from, to, sourceAcId,
    frmCurr, dept, vouNo, compCode, deptName, splitId, fromDesp,
    naration, remark, toDesp);*/
    private String stDate;
    private String endDate;
    private String sourceAcId;
    private String fromCurr;
    private String depId;
    private String vouNo;
    private String fromDesp;
    private String naration;
    private String remark;
    private String splitId;

    public void setSplitId(String splitId) {
        this.splitId = splitId;
    }

    public void setSourceAcId(String sourceAcId) {
        this.sourceAcId = sourceAcId;
    }

    /**
     * Creates new form CreditVoucher
     */
    public CrDrVoucher() {
        initComponents();
        initKeyListener();

    }

    private void initMain() {
        setTodayDate();
        initPropertyChangeListener();
        initAutoComplete();
        initTable();
        searchCreditVoucher();
        isShown = true;
    }

    private void initPropertyChangeListener() {

        /*txtFromDate.getDateEditor().addPropertyChangeListener(
        new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
        if ("date".equals(e.getPropertyName())) {
        Date newValue = Util1.toJavaDate(e.getNewValue());
        Date oldValue = Util1.toJavaDate(e.getOldValue());
        if (!Util1.isSameDate(newValue, oldValue)) {
        searchCreditVoucher();
        }
        }
        
        }
        });
        txtToDate.getDateEditor().addPropertyChangeListener(
        new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
        if ("date".equals(e.getPropertyName())) {
        
        Date newValue = Util1.toJavaDate(e.getNewValue());
        Date oldValue = Util1.toJavaDate(e.getOldValue());
        if (!Util1.isSameDate(newValue, oldValue)) {
        searchCreditVoucher();
        }
        }
        
        }
        });*/
    }

    private void initAutoComplete() {
        if (Global.listCOA == null) {
            Global.listCOA = cOAService.search("-", "-", "-", "-", "-", "-", "-");
        }
        if (Global.listDepartment == null) {
            Global.listDepartment = departmentService.search("-", "-", "-", "-", "-");
        }
        if (Global.listCurrency == null) {
            Global.listCurrency = currencyService.search("-", "-", "-");
        }
        COAAutoCompleter cOAAutoCompleter = new COAAutoCompleter(txtAccount, Global.listCOA, null);
        cOAAutoCompleter.setSelectionObserver(this);
        DepartmentAutoCompleter departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, Global.listDepartment, null);
        departmentAutoCompleter.setSelectionObserver(this);
        CurrencyAutoCompleter currencyAutoCompleter = new CurrencyAutoCompleter();
        currencyAutoCompleter.setSelectionObserver(this);
    }

    private void initTable() {
        voucherTableModel.setSplidId(splitId);
        tblCredit.setModel(voucherTableModel);
        tblCredit.getTableHeader().setFont(Global.lableFont);
        tblCredit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCredit.setDefaultRenderer(Double.class, new TableCellRender());
        tblCredit.setDefaultRenderer(Object.class, new TableCellRender());
        tblCredit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (tblCredit.getSelectedRow() >= 0) {
                        selectRow = tblCredit.convertRowIndexToModel(tblCredit.getSelectedRow());
                        VCrDrVoucher vGl = voucherTableModel.getVGl(selectRow);
                        openCrdVoucherEntryDialog(vGl.getVoucherNo());
                    }
                }
            }

        });

    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void initKeyListener() {
        txtFromDate.getDateEditor().getUiComponent().setName("txtFromDate");
        txtToDate.getDateEditor().getUiComponent().setName("txtToDate");
        txtFromDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtToDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtAccount.addKeyListener(this);
        txtDep.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        txtFrom.addKeyListener(this);
        txtNaration.addKeyListener(this);
        txtRemark.addKeyListener(this);
    }

    private void searchCreditVoucher() {
        LOGGER.info(this.getName() + "Search...");
        loadingObserver.load(this.getName(), "Start");
        stDate = Util1.toDateStr(txtFromDate.getDate(), "dd/MM/yyyy");
        endDate = Util1.toDateStr(txtToDate.getDate(), "dd/MM/yyyy");
        vouNo = Util1.isNull(txtVouNo.getText(), "-");
        fromDesp = Util1.isNull(txtFrom.getText(), "-");
        naration = Util1.isNull(txtNaration.getText(), "-");
        remark = Util1.isNull(txtRemark.getText(), "-");
        if (txtCurrency.getText().isEmpty()) {
            fromCurr = "-";
        }
        if (txtAccount.getText().isEmpty()) {
            sourceAcId = "-";
        }
        if (txtDep.getText().isEmpty()) {
            depId = "-";
        }

        taskExecutor.execute(() -> {
            List<VCrDrVoucher> listCD = crdrService.search(stDate, endDate, sourceAcId, fromCurr, depId, vouNo,
                    Global.compId.toString(), "-", splitId, fromDesp, naration,
                    remark, "-");
            voucherTableModel.setListVCD(listCD);
            loadingObserver.load(this.getName(), "Stop");
        });

    }

    private void openCrdVoucherEntryDialog(String vouId) {
        String vouType;
        if (splitId.equals("8")) {
            vouType = "CR";
            creditVoucherEntry.setTitle("Credit Voucher Entry");
        } else {
            vouType = "DR";
            creditVoucherEntry.setTitle("Debit Voucher Entry");
        }
        creditVoucherEntry.clear();
        creditVoucherEntry.setSize(Global.width - 40, Global.height - 200);
        creditVoucherEntry.setVoucherId(vouId);
        creditVoucherEntry.setVoucherType(vouType);
        creditVoucherEntry.setLocationRelativeTo(null);
        creditVoucherEntry.setVisible(true);
    }

    public void clear() {
        setTodayDate();
        txtAccount.setText(null);
        txtCurrency.setText(null);
        txtDep.setText(null);
        txtFrom.setText(null);
        txtNaration.setText(null);
        txtRemark.setText(null);
        txtVouNo.setText(null);
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
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtNaration = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCredit = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel1ComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From");

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.lableFont);
        txtFromDate.setMaximumSize(new java.awt.Dimension(400, 400));
        txtFromDate.setName("txtFromDate"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Voucher No");

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N
        txtVouNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouNoActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Account");

        txtAccount.setFont(Global.textFont);
        txtAccount.setName("txtAccount"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtDep"); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Currency");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setName("txtCurrency"); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("From");

        txtFrom.setFont(Global.textFont);
        txtFrom.setName("txtFrom"); // NOI18N
        txtFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFromActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Naration");

        txtNaration.setFont(Global.textFont);
        txtNaration.setName("txtNaration"); // NOI18N
        txtNaration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNarationActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRemarkActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("To");

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.lableFont);
        txtToDate.setMaximumSize(new java.awt.Dimension(400, 400));
        txtToDate.setName("txtDate"); // NOI18N

        jButton1.setFont(Global.lableFont);
        jButton1.setText("+");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAccount, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(txtDep))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNaration, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1))
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNaration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1))
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtAccount, txtCurrency, txtDep, txtFrom, txtFromDate, txtNaration, txtRemark, txtToDate, txtVouNo});

        tblCredit.setFont(Global.textFont);
        tblCredit.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCredit.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblCredit);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel1ComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1ComponentShown

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        txtFromDate.requestFocusInWindow();
        if (!isShown) {
            initMain();
        }

    }//GEN-LAST:event_formComponentShown

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        openCrdVoucherEntryDialog("-");

    }//GEN-LAST:event_jButton1ActionPerformed

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentHidden

    private void txtVouNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouNoActionPerformed
        // TODO add your handling code here:
        searchCreditVoucher();
    }//GEN-LAST:event_txtVouNoActionPerformed

    private void txtFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFromActionPerformed
        // TODO add your handling code here
        searchCreditVoucher();
    }//GEN-LAST:event_txtFromActionPerformed

    private void txtNarationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNarationActionPerformed
        // TODO add your handling code here:
        searchCreditVoucher();
    }//GEN-LAST:event_txtNarationActionPerformed

    private void txtRemarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRemarkActionPerformed
        // TODO add your handling code here:
        searchCreditVoucher();
    }//GEN-LAST:event_txtRemarkActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCredit;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtFrom;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtNaration;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";
        if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JDateChooser) {
            ctrlName = ((JDateChooser) sourceObj).getName();
        }
        LOGGER.info("Control Name :" + ctrlName);
        switch (ctrlName) {
            case "txtVouNo":
                vouNo = txtVouNo.getText();
                LOGGER.info(vouNo);
                break;
            case "txtFrom":
                fromDesp = txtFrom.getText();
                break;
            case "txtNaration":
                naration = txtNaration.getText();
                break;
            case "txtRemark":
                remark = txtRemark.getText();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";

        if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTextFieldDateEditor) {
            ctrlName = ((JTextFieldDateEditor) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtFromDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtFromDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtToDate.getDateEditor().getUiComponent().requestFocusInWindow();
                    searchCreditVoucher();
                }
                tabToTable(e);
                break;
            case "txtToDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtToDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtVouNo.requestFocus();
                    searchCreditVoucher();
                }
                tabToTable(e);
                break;
            case "txtVouNo":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtAccount.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtToDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtAccount":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDep.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtDep":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCurrency.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtAccount.requestFocus();
                }
                tabToTable(e);

                break;
            case "txtCurrency":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtFrom.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDep.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtFrom":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtNaration.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtCurrency.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtNaration":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtRemark.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtFrom.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtFromDate.getDateEditor().getUiComponent().requestFocusInWindow();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtNaration.requestFocus();
                }
                tabToTable(e);
                break;

        }

    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblCredit.requestFocus();
            if (tblCredit.getRowCount() >= 0) {
                tblCredit.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            String name = source.toString();
            switch (name) {

                case "Department":
                    depId = selectObj.toString();
                    break;
                case "COA":
                    sourceAcId = selectObj.toString();
                    break;
                case "Currency":
                    fromCurr = selectObj.toString();
                    break;
            }
            searchCreditVoucher();

        }
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
        isShown = false;
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

}
