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
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.CurrencyKey;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.StockOpValue;
import com.cv.accountswing.entity.SystemProperty;
import com.cv.accountswing.entity.SystemPropertyKey;
import com.cv.accountswing.entity.view.VStockOpValue;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.StockOpValueService;
import com.cv.accountswing.service.SystemPropertyService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DateAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
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
    private Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private StockOpValueService sovService;
    @Autowired
    private JournalStockOpeningTableModel journalStockOpeningTableModel;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private SystemPropertyService spService;
    @Autowired
    private CurrencyService curService;
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
    private String depId;

    /**
     * Creates new form JournalStockOpening
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
    }

    private void initTable() {
        tblOpening.setModel(journalStockOpeningTableModel);
        tblOpening.getTableHeader().setFont(Global.textFont);
        tblOpening.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOpening.setDefaultRenderer(Double.class, new TableCellRender());
        tblOpening.setDefaultRenderer(Object.class, new TableCellRender());

        if (journalStockOpeningTableModel.getListStockOpening().isEmpty()) {
            searchOpening();
        }
    }

    private void searchOpening() {
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            List listOp = sovService.search(stDate, enDate,
                    Util1.isNull(coaCode, "-"), Util1.isNull(currency, "-"),
                    Util1.isNull(depId, "-"), Global.compId.toString());
            List<VStockOpValue> listOPValue = (List<VStockOpValue>) (List<?>) listOp;
            journalStockOpeningTableModel.setListStockOpening(listOPValue);
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
        DepartmentAutoCompleter departmentAutoCompleter = new DepartmentAutoCompleter(txtDepartment, Global.listDepartment, null);
        departmentAutoCompleter.setSelectionObserver(this);
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
        btnNewEntry.addKeyListener(this);

    }

    private boolean isValidEntry() {
        boolean status = true;
        if (depId == null) {
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
            String userId = Global.loginUser.getUserId().toString();
            SystemPropertyKey key = new SystemPropertyKey();
            key.setCompCode(Global.compId);
            key.setPropKey("system.stockop.parentid");
            SystemProperty sp = spService.findById(key);
            String[] coaIds = sp.getPropValue().split(",");
            CurrencyKey curKey = new CurrencyKey();
            curKey.setCode(currency);
            curKey.setCompCode(Global.compId);
            Currency curr = curService.findById(curKey);
            Department oDept = deptService.findById(depId);

            List<VStockOpValue> listVSO = new ArrayList();
            for (String tmpId : coaIds) {
                List<ChartOfAccount> listCOA = coaService.getAllChild(tmpId, Global.compId.toString());
                listCOA.stream().map(coa -> {
                    VStockOpValue tmpVSOV = new VStockOpValue();
                    tmpVSOV.getKey().setCoaCode(coa.getCode());
                    tmpVSOV.getKey().setCompId(Global.compId);
                    tmpVSOV.getKey().setCurrency(currency);
                    tmpVSOV.getKey().setDeptCode(depId);
                    tmpVSOV.setCoaCodeUsr(coa.getCoaCodeUsr());
                    tmpVSOV.setCoaNameEng(coa.getCoaNameEng());
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setCurrName(curr.getCurrencyName());
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setDeptCodeUsr(oDept.getUsrCode());
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setDeptName(oDept.getDeptName());
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setCreatedBy(userId);
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setAmount(0.0);
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    String strSOV = gson.toJson(tmpVSOV);
                    StockOpValue sov = gson.fromJson(strSOV, StockOpValue.class);
                    sov.getKey().setTranDate(Util1.toDate(stDate));
                    sov.setCreatedDate(new Date());
                    try {
                        sovService.save(sov, userId);
                    } catch (Exception e) {
                        LOGGER.error("Save StockOpening :" + e.getMessage());
                        JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Save StockOpening", JOptionPane.ERROR_MESSAGE);
                    }
                    tmpVSOV.getKey().setTranDate(Util1.toDate(stDate));
                    return tmpVSOV;
                }).map(tmpVSOV -> {
                    tmpVSOV.setCreatedDate(new Date());
                    return tmpVSOV;
                }).forEachOrdered(tmpVSOV -> {
                    listVSO.add(tmpVSOV);
                });
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        txtDepartment = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        btnNewEntry = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOpening = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

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

        btnNewEntry.setFont(Global.textFont);
        btnNewEntry.setText("New Entry");
        btnNewEntry.setName("btnNewEntry"); // NOI18N
        btnNewEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewEntryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDepartment, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnNewEntry)
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
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNewEntry))
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
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
    }//GEN-LAST:event_formComponentShown

    private void btnNewEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewEntryActionPerformed
        // TODO add your handling code here:
        getNewStockOpValue();
    }//GEN-LAST:event_btnNewEntryActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNewEntry;
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
                    depId = selectObj.toString();
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
                    btnNewEntry.requestFocus();
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
                    btnNewEntry.requestFocus();
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
}
