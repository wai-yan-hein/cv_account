/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.ReloadData;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.CompanyInfo;
import com.cv.accountswing.entity.SystemProperty;
import com.cv.accountswing.entity.SystemPropertyKey;
import com.cv.accountswing.entity.temp.TmpOpeningClosing;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.COAOpeningDService;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.CompanyInfoService;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.service.SystemPropertyService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.service.VGlService;
import com.cv.accountswing.ui.editor.CurrencyEditor;
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.ui.editor.TraderCellEditor;
import com.cv.accountswing.ui.cash.common.AllCashTableModel;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.COACellEditor;
import com.cv.accountswing.ui.filter.FilterPanel;
import com.cv.accountswing.util.Util1;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
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
public class AllCash extends javax.swing.JPanel implements SelectionObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllCash.class);
    String stDate;
    String enDate;
    String desp;
    String accId;
    String ref;
    String depId;
    String traderName;
    String currency;
    String debAmt;
    String crdAmt;
    @Autowired
    private FilterPanel filterPanel;
    @Autowired
    private VGlService vGlService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private AllCashTableModel allCashTableModel;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private COAService cOAService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private COAOpeningDService coaOpDService;
    @Autowired
    private CompanyInfoService ciService;
    @Autowired
    private SystemPropertyService spService;
    @Autowired
    ReportService rService;
    private TableRowSorter<TableModel> sorter;
    private SelectionObserver selectionObserver;
    private LoadingObserver loadingObserver;
    private ReloadData reloadData;
    private String sourceAccId;
    private JPopupMenu popupmenu;
    private boolean isShown = false;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
        clearFilter();
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public String getSourceAccId() {
        return sourceAccId;
    }

    public void setSourceAccId(String sourceAccId) {
        this.sourceAccId = sourceAccId;
        LOGGER.info("Source Id :" + sourceAccId);

    }

    public void setReloadData(ReloadData reloadData) {
        this.reloadData = reloadData;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    /**
     * Creates new form CashBook
     *
     */
    public AllCash() {
        initComponents();
        initPopup();
    }

    private void initMain() {
        filterPanel();
        initTable();
        clearFilter();
        isShown = true;
    }

    private void clearTextBox() {
        txtFClosing.setValue(0.0);
        txtFOpening.setValue(0.0);
        txtFCreditAmt.setValue(0.0);
        txtFDebitAmt.setValue(0.0);
    }

    private void requestFoucsTable() {
        int row = allCashTableModel.getListSize() - 1;
        //tblCash.setCellSelectionEnabled(true);
        tblCash.setRowSelectionInterval(row, row);
        tblCash.setColumnSelectionInterval(0, 0);
        tblCash.requestFocusInWindow();
    }

    private void initTable() {
        if (this.getName().equals("Daily Cash")) {
            allCashTableModel.setColumnName(7, "Cash In / Dr");
            allCashTableModel.setColumnName(8, "Cash Out / Cr");
        }
        allCashTableModel.setSelectionObserver(this);
        allCashTableModel.setReloadData(reloadData);
        tblCash.setModel(allCashTableModel);
        tblCash.getTableHeader().setFont(Global.tblHeaderFont);
        tblCash.getTableHeader().setPreferredSize(new Dimension(40, 40));
        //tblCash.getTableHeader().setBackground(Global.tblHeaderColor);
        sorter = new TableRowSorter<>(tblCash.getModel());
        tblCash.setRowSorter(sorter);
        tblCash.setCellSelectionEnabled(true);
        allCashTableModel.setSourceAccId(sourceAccId);
        allCashTableModel.setParent(tblCash);
        allCashTableModel.addNewRow();
        tblCash.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCash.getColumnModel().getColumn(0).setPreferredWidth(5);// Date
        tblCash.getColumnModel().getColumn(1).setPreferredWidth(15);// Department
        tblCash.getColumnModel().getColumn(2).setPreferredWidth(200);// Description      
        tblCash.getColumnModel().getColumn(3).setPreferredWidth(200);// Ref  
        tblCash.getColumnModel().getColumn(4).setPreferredWidth(100);// Person
        tblCash.getColumnModel().getColumn(5).setPreferredWidth(150);// Account
        tblCash.getColumnModel().getColumn(6).setPreferredWidth(1);// Curr      
        tblCash.getColumnModel().getColumn(7).setPreferredWidth(70);// Dr-Amt   
        tblCash.getColumnModel().getColumn(8).setPreferredWidth(70);// Cr-Amt  
        tblCash.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(1).setCellEditor(new DepartmentCellEditor());
        tblCash.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(4).setCellEditor(new TraderCellEditor());
        tblCash.getColumnModel().getColumn(5).setCellEditor(new COACellEditor());
        tblCash.getColumnModel().getColumn(6).setCellEditor(new CurrencyEditor());
        tblCash.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());

        tblCash.setDefaultRenderer(Double.class, new TableCellRender());
        tblCash.setDefaultRenderer(Object.class, new TableCellRender());

        tblCash.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    selectionObserver = filterPanel;
                    if (selectionObserver != null) {
                        selectionObserver.selected("TABTOTEXTBOX", "");
                    }
                }
            }

        });
        tblCash.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        /*tblCash.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblCash.getActionMap().put("ENTER-Action", actionTblCash);*/
        tblCash.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCash.getActionMap().put("F8-Action", actionItemDeleteExp);
    }

    private void initPopup() {
        popupmenu = new JPopupMenu("Edit");
        JMenuItem closeAll = new JMenuItem("Print");
        closeAll.addActionListener((ActionEvent e) -> {
            print();
        });
        popupmenu.add(closeAll);
        initMouseLisener();
    }

    private void initMouseLisener() {
        tblCash.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupmenu.show(tblCash, e.getX(), e.getY());
                } else if (e.getClickCount() == 2) {
                    int row = tblCash.getSelectedRow();
                    int col = tblCash.getSelectedColumn();
                    tblCash.requestFocus();
                    tblCash.editCellAt(row, col);
                }
            }

        });
    }

    private void filterPanel() {
        panelFilter.setLayout(new BorderLayout());
        filterPanel.setSelectionObserver(this);
        filterPanel.initMain();
        panelFilter.add(filterPanel, BorderLayout.CENTER);
        panelFilter.revalidate();
        panelFilter.repaint();

    }

    private final Action actionItemDeleteExp = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            VGl vgl;
            int yes_no;

            if (tblCash.getSelectedRow() >= 0) {
                vgl = allCashTableModel.getVGl(tblCash.convertRowIndexToModel(tblCash.getSelectedRow()));

                if (vgl.getGlId() != null) {
                    yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete?",
                            "Expense item delete", JOptionPane.YES_NO_OPTION);

                    tblCash.getCellEditor().stopCellEditing();
                    if (yes_no == 0) {
                        allCashTableModel.deleteVGl(tblCash.getSelectedRow());
                        calDebitCredit();
                    }
                }
            }
        }
    };

    public void print() {
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            try {
                CompanyInfo ci = ciService.findById(Global.compId);
                SystemPropertyKey key = new SystemPropertyKey();
                key.setCompCode(Global.compId);
                key.setPropKey("system.report.path");
                SystemProperty sp = spService.findById(key);
                String userId = Global.loginUser.getUserId().toString();
                String fileName = userId + "_Ledger_Report.pdf";
                String reportPath = sp.getPropValue();
                //String reportPath1 = reportPath;
                String filePath = reportPath + "/temp/" + fileName;

                reportPath = reportPath + "/LedgerReport";
                key = new SystemPropertyKey();
                key.setCompCode(Global.compId);
                key.setPropKey("system.font.path");
                sp = spService.findById(key);
                String fontPath = sp.getPropValue();

                Map<String, Object> parameters = new HashMap();
                parameters.put("p_company_name", ci.getName());
                parameters.put("p_comp_id", Global.compId);
                parameters.put("p_trg_acc_id", sourceAccId);
                parameters.put("p_acc_id", Util1.isNull(accId, "-"));
                parameters.put("p_report_info", stDate
                        + " to " + enDate);
                parameters.put("p_from", Util1.toDateStrMYSQL(stDate, "dd/MM/yyyy"));
                parameters.put("p_to", Util1.toDateStrMYSQL(enDate, "dd/MM/yyyy"));
                parameters.put("p_desp", Util1.isNull(desp, "-"));
                parameters.put("p_from_curr", Util1.isNull(currency, "-"));
                parameters.put("p_to_curr", "-");
                parameters.put("p_ref", Util1.isNull(ref, "-"));
                parameters.put("p_dept", Util1.isNull(depId, "-"));
                parameters.put("p_vou_no", "-");
                parameters.put("p_cv_id", Util1.isNull(accId, "-"));
                parameters.put("p_report_name", this.getName());
                parameters.put("p_opening", txtFOpening.getValue());
                parameters.put("p_closing", txtFClosing.getValue());
                rService.genCreditVoucher(reportPath, filePath, fontPath, parameters);
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception ex) {
                LOGGER.error("getLedgerReport : " + ex);

            }
        });

    }

    private void searchCash() {
        initializeParameter();
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            LOGGER.info(sourceAccId + "----- Searching...");
            List<VGl> listVGl = vGlService.search(stDate, enDate,
                    desp, sourceAccId,
                    accId, currency, "-",
                    ref, depId, "-", "-",
                    "-", "-", "-", "-", "-",
                    traderName, "-", "-",
                    debAmt,
                    crdAmt);
            swapData(listVGl, sourceAccId);
            allCashTableModel.setListVGl(listVGl);
            allCashTableModel.addNewRow();
            requestFoucsTable();
            LOGGER.info(sourceAccId + "----- Finished...");
        });
        calOpeningClosing();

    }

    private void initializeParameter() {
        stDate = Util1.isNull(stDate, Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy"));
        enDate = Util1.isNull(enDate, Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy"));
        desp = Util1.isNull(desp, "-");
        accId = Util1.isNull(accId, "-");
        currency = Util1.isNull(currency, "-");
        ref = Util1.isNull(ref, "-");
        depId = Util1.isNull(depId, Global.sysProperties.get("system.default.department"));
        traderName = Util1.isNull(traderName, "-");
        debAmt = Util1.isNull(debAmt, "-");
        crdAmt = Util1.isNull(crdAmt, "-");
        clearTextBox();
    }

    public void clearFilter() {
        stDate = null;
        enDate = null;
        desp = null;
        accId = null;
        currency = null;
        ref = null;
        depId = null;
        traderName = null;
        debAmt = null;
        crdAmt = null;
        txtFClosing.setValue(0.0);
        txtFCreditAmt.setValue(0.0);
        txtFDebitAmt.setValue(0.0);
        txtFOpening.setValue(0.0);
        filterPanel.clear();
        searchCash();

    }

    private void swapData(List<VGl> listVGL, String targetId) {
        for (VGl vgl : listVGL) {
            String sourceAcId = Util1.isNull(vgl.getSourceAcId(), "-");
            String accId = Util1.isNull(vgl.getAccountId(), "-");
            if (sourceAcId.equals(targetId)) {
                /*if(Util1.isNullZero(vgl.getSplitId()) == 8){ //Credit Voucher
                 Double tmpAmt = vgl.getDrAmt();
                 vgl.setDrAmt(vgl.getCrAmt());
                 vgl.setCrAmt(tmpAmt);
                 } else if(Util1.isNullZero(vgl.getSplitId()) == 9){ //Debit Voucher
                 Double tmpAmt = vgl.getCrAmt();
                 vgl.setCrAmt(vgl.getDrAmt());
                 vgl.setDrAmt(tmpAmt);
                 }*/
            } else if (accId.equals(targetId)) {
                double tmpDrAmt = 0;
                if (vgl.getDrAmt() != null) {
                    tmpDrAmt = vgl.getDrAmt();
                }
                vgl.setDrAmt(vgl.getCrAmt());
                vgl.setCrAmt(tmpDrAmt);

                String tmpStr = vgl.getAccName();
                vgl.setAccName(vgl.getSrcAccName());
                vgl.setSrcAccName(tmpStr);
            } else {
                vgl.setDrAmt(0.0);
                vgl.setCrAmt(0.0);
            }
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

        panelFilter = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCash = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtFClosing = new javax.swing.JFormattedTextField();
        txtFOpening = new javax.swing.JFormattedTextField();
        txtFDebitAmt = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtFCreditAmt = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter);
        panelFilter.setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        tblCash.setFont(Global.textFont);
        tblCash.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCash.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblCash.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblCash);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Opening Bal");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Closing Bal");

        txtFClosing.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtFClosing.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFClosing.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFClosing.setEnabled(false);
        txtFClosing.setFont(Global.amtFont);

        txtFOpening.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtFOpening.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFOpening.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFOpening.setEnabled(false);
        txtFOpening.setFont(Global.amtFont);

        txtFDebitAmt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtFDebitAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFDebitAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFDebitAmt.setEnabled(false);
        txtFDebitAmt.setFont(Global.amtFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Toatal Debit Amt");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Totoal Credit Amt");

        txtFCreditAmt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtFCreditAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFCreditAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFCreditAmt.setEnabled(false);
        txtFCreditAmt.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFOpening))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(txtFClosing, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFDebitAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFCreditAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtFOpening, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtFDebitAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(txtFCreditAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtFClosing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 968, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        if (!isShown) {
            initMain();
        } else {
            requestFoucsTable();
        }

    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JTable tblCash;
    private javax.swing.JFormattedTextField txtFClosing;
    private javax.swing.JFormattedTextField txtFCreditAmt;
    private javax.swing.JFormattedTextField txtFDebitAmt;
    private javax.swing.JFormattedTextField txtFOpening;
    // End of variables declaration//GEN-END:variables

    private void calDebitCredit() {
        List<VGl> listVGl = allCashTableModel.getListVGl();
        //LOGGER.info("calDebitCredit Thread Started.");
        double dbAmt = 0.0;
        double cdAmt = 0.0;
        for (VGl vgl : listVGl) {
            dbAmt += Util1.getDouble(vgl.getDrAmt());
            cdAmt += Util1.getDouble(vgl.getCrAmt());
        }
        txtFDebitAmt.setValue(dbAmt);
        txtFCreditAmt.setValue(cdAmt);
        double closing = Util1.getDouble((Double) txtFOpening.getValue()) + dbAmt - cdAmt;
        txtFClosing.setValue(closing);

    }

    private void calOpeningClosing() {
        taskExecutor.execute(() -> {
            String opDate = Global.finicialPeriodFrom;
            try {
                if (stDate.equals("-")) {
                    stDate = opDate;
                }
                List<TmpOpeningClosing> opBalanceGL = coaOpDService.getOpBalanceGL(sourceAccId, opDate, stDate, 3, "MMK",
                        Global.loginUser.getUserId().toString(),
                        Util1.isNull(depId, "-"));
                if (!opBalanceGL.isEmpty()) {
                    TmpOpeningClosing tmpOC = opBalanceGL.get(0);
                    txtFOpening.setValue(tmpOC.getOpening());
                }
                calDebitCredit();
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception ex) {
                LOGGER.error("TmpOpeningClosing" + ex.getMessage());
            }
        });
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            String name = source.toString();
            switch (name) {
                case "tabToCashTable":
                    tblCash.requestFocus();
                    if (tblCash.getRowCount() >= 0) {
                        tblCash.setRowSelectionInterval(0, 0);
                    }
                    break;
                case "Date":
                    String[] split = selectObj.toString().split("to");
                    stDate = split[0];
                    enDate = split[1];
                    searchValidation(stDate);
                    break;
                case "Department":
                    depId = selectObj.toString();
                    searchValidation(depId);
                    break;
                case "COA":
                    accId = selectObj.toString();
                    searchValidation(accId);
                    break;
                case "Currency":
                    currency = selectObj.toString();
                    searchValidation(currency);
                    break;
                case "Trader":
                    traderName = selectObj.toString();
                    searchValidation(traderName);
                    break;
                case "Description":
                    desp = selectObj.toString();
                    searchValidation(desp);
                    break;
                case "Ref":
                    ref = selectObj.toString();
                    searchValidation(ref);
                    break;
                case "DrAmt":
                    debAmt = selectObj.toString();
                    searchValidation(debAmt);
                    break;
                case "CrdAmt":
                    crdAmt = selectObj.toString();
                    searchValidation(crdAmt);
                    break;
                case "CAL-TOTAL":
                    calDebitCredit();
                    break;
            }

        }

    }

    private void searchValidation(String str) {

        searchCash();
    }

}
