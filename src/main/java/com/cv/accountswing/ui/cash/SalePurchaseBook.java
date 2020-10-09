/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.service.VGlService;
import com.cv.accountswing.ui.editor.CurrencyEditor;
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.ui.editor.TraderCellEditor;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.SalePurchaseTableModel;
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
import java.util.List;
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
public class SalePurchaseBook extends javax.swing.JPanel implements SelectionObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalePurchaseBook.class);
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
    private SalePurchaseTableModel spTableModel;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private COAService cOAService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private CurrencyService currencyService;
    
    @Autowired
    ReportService rService;
    private TableRowSorter<TableModel> sorter;
    private SelectionObserver selectionObserver;
    private LoadingObserver loadingObserver;
    private String sourceAccId;
    private JPopupMenu popupmenu;
    private boolean isShown = false;
    private String panelName;

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

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    /**
     * Creates new form CashBook
     *
     */
    public SalePurchaseBook() {
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

        txtFTotalAmt.setValue(0.0);
    }

    private void requestFoucsTable() {
        int row = spTableModel.getListSize() - 1;
        //tblCash.setCellSelectionEnabled(true);
        tblCash.setRowSelectionInterval(row, row);
        tblCash.setColumnSelectionInterval(0, 0);
        tblCash.requestFocusInWindow();
    }

    private void initTable() {
        panelName = this.getName();
        spTableModel.setSourceName(panelName);
        spTableModel.setSelectionObserver(this);
        tblCash.setModel(spTableModel);
        tblCash.getTableHeader().setFont(Global.tblHeaderFont);
        tblCash.getTableHeader().setPreferredSize(new Dimension(40, 40));
        //tblCash.getTableHeader().setBackground(Global.tblHeaderColor);
        sorter = new TableRowSorter<>(tblCash.getModel());
        tblCash.setRowSorter(sorter);
        tblCash.setCellSelectionEnabled(true);
        spTableModel.setSourceAccId(sourceAccId);
        spTableModel.setParent(tblCash);
        spTableModel.addNewRow();
        tblCash.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCash.getColumnModel().getColumn(0).setPreferredWidth(5);// Date
        tblCash.getColumnModel().getColumn(1).setPreferredWidth(15);// Department
        tblCash.getColumnModel().getColumn(2).setPreferredWidth(200);// Description      
        tblCash.getColumnModel().getColumn(3).setPreferredWidth(200);// Ref  
        tblCash.getColumnModel().getColumn(4).setPreferredWidth(100);// Person
        tblCash.getColumnModel().getColumn(5).setPreferredWidth(150);// Account
        tblCash.getColumnModel().getColumn(6).setPreferredWidth(1);// Curr      
        tblCash.getColumnModel().getColumn(7).setPreferredWidth(70);// Dr-Amt   
        tblCash.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(1).setCellEditor(new DepartmentCellEditor(departmentService));
        tblCash.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(4).setCellEditor(new TraderCellEditor(traderService));
        tblCash.getColumnModel().getColumn(5).setCellEditor(new COACellEditor(cOAService));
        tblCash.getColumnModel().getColumn(6).setCellEditor(new CurrencyEditor(currencyService));
        tblCash.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());

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
                vgl = spTableModel.getVGl(tblCash.convertRowIndexToModel(tblCash.getSelectedRow()));

                if (vgl.getGlId() != null) {
                    yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete?",
                            "Expense item delete", JOptionPane.YES_NO_OPTION);

                    tblCash.getCellEditor().stopCellEditing();
                    if (yes_no == 0) {
                        spTableModel.deleteVGl(tblCash.getSelectedRow());
                        calTotalAmt();
                    }
                }
            }
        }
    };

    public void print() {
        /*loadingObserver.load(this.getName(), "Start");
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
        
        rService.genCreditVoucher(reportPath, filePath, fontPath, parameters);
        loadingObserver.load(this.getName(), "Stop");
        } catch (Exception ex) {
        LOGGER.error("getLedgerReport : " + ex);
        
        }
        });*/

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
            spTableModel.setListVGl(listVGl);
            spTableModel.addNewRow();
            requestFoucsTable();
            calTotalAmt();
            LOGGER.info(sourceAccId + "----- Finished...");
        });

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
        txtFTotalAmt.setValue(0.0);

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
        jLabel4 = new javax.swing.JLabel();
        txtFTotalAmt = new javax.swing.JFormattedTextField();

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

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Total Amt");

        txtFTotalAmt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtFTotalAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFTotalAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFTotalAmt.setEnabled(false);
        txtFTotalAmt.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(638, 638, 638)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFTotalAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtFTotalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JTable tblCash;
    private javax.swing.JFormattedTextField txtFTotalAmt;
    // End of variables declaration//GEN-END:variables

    private void calTotalAmt() {
        List<VGl> listVGl = spTableModel.getListVGl();
        double ttlAmt = 0.0;
        if (panelName.equals("Sale")) {
            for (VGl vgl : listVGl) {
                ttlAmt += Util1.getDouble(vgl.getCrAmt());
                //cdAmt += Util1.getDouble(vgl.getCrAmt());
            }
        } else {
            for (VGl vgl : listVGl) {
                ttlAmt += Util1.getDouble(vgl.getDrAmt());
                //cdAmt += Util1.getDouble(vgl.getCrAmt());
            }
        }

        txtFTotalAmt.setValue(ttlAmt);
        loadingObserver.load(this.getName(), "Stop");
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
                    calTotalAmt();
                    break;
            }

        }

    }

    private void searchValidation(String str) {

        searchCash();
    }

}
