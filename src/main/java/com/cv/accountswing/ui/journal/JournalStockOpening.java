/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.journal;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.CurrencyKey;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.StockOpValue;
import com.cv.accountswing.entity.view.VStockOpValue;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.StockOpValueService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.COACellEditor;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.CurrencyEditor;
import com.cv.accountswing.ui.editor.DateAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.ui.journal.common.JournalStockOpeningTableModel;
import com.cv.accountswing.ui.setup.ChartOfAccountSetup;
import com.cv.accountswing.util.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class JournalStockOpening extends javax.swing.JPanel implements SelectionObserver, KeyListener, PanelControl {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ChartOfAccountSetup.class);
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private StockOpValueService sovService;
    @Autowired
    private JournalStockOpeningTableModel tableModel;
    @Autowired
    private DepartmentService deptService;
    @Autowired
    private COAService coaService;
    @Autowired
    private ApplicationMainFrame mainFrame;

    private LoadingObserver loadingObserver;
    private boolean isShown = false;
    private String stDate;
    private String enDate;
    private String coaCode;
    private String currency;
    private String depCode;
    private DepartmentAutoCompleter departmentAutoCompleter;

    public LoadingObserver getLoadingObserver() {
        return loadingObserver;
    }

    /**
     * Creates new form JournalStockOpening
     *
     * @param loadingObserver
     */
    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public JournalStockOpening() {
        initComponents();
    }

    private void initMain() {
        initCombo();
        initKeyListener();
        setTodayDate();
        initTable();
        isShown = true;
    }

    private void initTable() {
        tblOpening.setModel(tableModel);
        tableModel.setParent(tblOpening);
        tblOpening.getTableHeader().setFont(Global.textFont);
        tblOpening.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblOpening.getTableHeader().setForeground(ColorUtil.foreground);
        tblOpening.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOpening.setDefaultRenderer(Double.class, new TableCellRender());
        tblOpening.setDefaultRenderer(Object.class, new TableCellRender());
        tblOpening.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(1).setCellEditor(new DepartmentCellEditor(false));
        tblOpening.getColumnModel().getColumn(2).setCellEditor(new COACellEditor(false));
        tblOpening.getColumnModel().getColumn(4).setCellEditor(new CurrencyEditor());
        tblOpening.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");

    }

    private void searchOpening() {
        loadingObserver.load(this.getName(), "Start");
        depCode = departmentAutoCompleter.getDepartment() == null ? "-" : departmentAutoCompleter.getDepartment().getDeptCode();
        taskExecutor.execute(() -> {
            List<VStockOpValue> listOp = sovService.search(stDate, enDate,
                    Util1.isNull(coaCode, "-"), Util1.isNull(currency, "-"),
                    Util1.isNull(depCode, "-"), Global.compCode);
            tableModel.setListStockOpening(listOp);
            tableModel.addEmptyRow();
            loadingObserver.load(this.getName(), "Stop");
        });
    }

    private void setTodayDate() {
        stDate = Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy");
        enDate = Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy");
        txtDate.setText("Today");
    }

    private void initCombo() {
        DateAutoCompleter dateAutoCompleter = new DateAutoCompleter(txtDate, Global.listDateModel, null);
        dateAutoCompleter.setSelectionObserver(this);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDepartment, Global.listDepartment, null, true);
        departmentAutoCompleter.setSelectionObserver(this);
        departmentAutoCompleter.setDepartment(new Department("-", "All"));
        CurrencyAutoCompleter currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        currencyAutoCompleter.setSelectionObserver(this);
        currencyAutoCompleter.setCurrency(Global.defalutCurrency);
        txtDate.requestFocus();
    }

    private void initKeyListener() {
        txtCurrency.addKeyListener(this);
        txtDate.addKeyListener(this);
        txtDepartment.addKeyListener(this);
        tblOpening.addKeyListener(this);
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (depCode == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Select Department.");
            txtDepartment.requestFocus();
            status = false;
        } else if (stDate == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Select Date.");
            txtDate.requestFocus();
            status = false;
        }
        return status;
    }

    private void getNewStockOpValue() {
        if (isValidEntry()) {
            String userCode = Global.loginUser.getAppUserCode();
            String[] coaIds = Global.sysProperties.get("system.stockop.parentid").split(",");
            CurrencyKey curKey = new CurrencyKey();
            curKey.setCode(currency);
            curKey.setCompCode(Global.compCode);
            Department oDept = deptService.findById(depCode);
            List<VStockOpValue> listVSO = new ArrayList();
            for (String tmpId : coaIds) {
                List<ChartOfAccount> listCOA = coaService.getAllChild(tmpId, Global.compCode);
                listCOA.stream().map(coa -> {
                    VStockOpValue tmpVSOV = new VStockOpValue();
                    tmpVSOV.getKey().setCoaCode(coa.getCode());
                    tmpVSOV.getKey().setCompCode(Global.compCode);
                    tmpVSOV.getKey().setCurrency(currency);
                    tmpVSOV.getKey().setDeptCode(depCode);
                    tmpVSOV.setCoaCodeUsr(coa.getCoaCodeUsr());
                    tmpVSOV.setCoaNameEng(coa.getCoaNameEng());
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setCurrName(Global.defalutCurrency.getCurrencyName());
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setDeptCodeUsr(oDept.getUsrCode());
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setDeptName(oDept.getDeptName());
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setCreatedBy(userCode);
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setAmount(0.0);
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    String strSOV = gson.toJson(tmpVSOV);
                    StockOpValue sov = gson.fromJson(strSOV, StockOpValue.class);
                    sov.getKey().setTranDate(Util1.toDate(stDate, "dd/MM/yyyy"));
                    sov.setCreatedDate(new Date());
                    sov.getKey().setCurrency(Global.defalutCurrency.getKey().getCode());
                    try {
                        sovService.save(sov, userCode);
                    } catch (Exception e) {
                        LOGGER.error("Save StockOpening :" + e.getMessage());
                        JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Save StockOpening", JOptionPane.ERROR_MESSAGE);
                    }
                    tmpVSOV.getKey().setTranDate(Util1.toDate(stDate, "dd/MM/yyyy"));
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setCreatedDate(new Date());
                    return tmpVSOV;
                }).forEachOrdered(tmpVSOV -> {
                    listVSO.add(tmpVSOV);
                });
            }
        }
        searchOpening();
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
        txtDate = new javax.swing.JTextField();
        txtDepartment = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOpening = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(Global.textFont);
        jLabel1.setText("Date");

        txtDate.setFont(Global.textFont);
        txtDate.setName("txtDate"); // NOI18N

        txtDepartment.setFont(Global.textFont);
        txtDepartment.setName("txtDepartment"); // NOI18N

        jLabel2.setFont(Global.textFont);
        jLabel2.setText("Department");

        jLabel3.setFont(Global.textFont);
        jLabel3.setText("Currency");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setEnabled(false);
        txtCurrency.setName("txtCurrency"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDepartment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtCurrency)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblOpening.setFont(Global.textFont);
        tblOpening.setModel(new javax.swing.table.DefaultTableModel(
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
        tblOpening.setName("tblOpening"); // NOI18N
        tblOpening.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblOpening);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        searchOpening();
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOpening;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDepartment;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            String name = source.toString();
            switch (name) {
                case "Date":
                    String[] split = selectObj.toString().split("to");
                    stDate = split[0];
                    enDate = split[1];
                    searchOpening();
                    break;
                case "Department":
                    depCode = selectObj.toString();
                    searchOpening();
                    break;
                case "Currency":
                    currency = selectObj.toString();
                    searchOpening();
                    break;
                default:
                    break;
            }
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

        if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDepartment.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    //btnNewEntry.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtDepartment":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDate.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtCurrency":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDepartment.requestFocus();
                }
                tabToTable(e);
                break;
            case "btnNewEntry":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDate.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtCurrency.requestFocus();
                }
            case "tblOpening":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDate.requestFocus();
                }
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblOpening.requestFocus();
            if (tblOpening.getRowCount() >= 0) {
                tblOpening.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
        deleteOpening();
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

    @Override
    public void refresh() {
        searchOpening();
    }

    private void deleteOpening() {
        int row = tblOpening.convertRowIndexToModel(tblOpening.getSelectedRow());
        tableModel.delete(row);
    }
}
