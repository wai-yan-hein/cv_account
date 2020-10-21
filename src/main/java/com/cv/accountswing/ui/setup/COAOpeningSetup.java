/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.CurrencyKey;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.COAOpeningService;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.VGlService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.setup.common.COAOpeningTableModel;
import com.cv.accountswing.util.Util1;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
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
public class COAOpeningSetup extends javax.swing.JPanel implements SelectionObserver, KeyListener, PanelControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(COAOpeningSetup.class);
    
    @Autowired
    private COAOpeningTableModel cOAOpeningTableModel;
    @Autowired
    private VGlService vGlService;
    @Autowired
    private COAOpeningService cOAOpeningService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private LoadingObserver loadingObserver;
    private boolean isShown = false;
    private String stDate;
    private String endDate;
    private String curId;
    private String depCode;
    
    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
    }
    
    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    /**
     * Creates new form COAOpeningSetup
     */
    public COAOpeningSetup() {
        initComponents();
    }
    
    private void initMain() {
        txtDate.setDate(Util1.toDate(Global.finicialPeriodFrom));
        initKeyListener();
        initCombo();
        initTable();
        searchOpening();
    }
    
    private void initCombo() {
        DepartmentAutoCompleter departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, Global.listDepartment, null);
        departmentAutoCompleter.setSelectionObserver(this);
        CurrencyAutoCompleter currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        String cuId = Global.sysProperties.get("system.default.currency");
        CurrencyKey key = new CurrencyKey();
        key.setCode(cuId);
        key.setCompCode(Global.compId);
        Currency currency = currencyService.findById(key);
        currencyAutoCompleter.setCurrency(currency);
    }
    
    private void initTable() {
        tblOpening.setModel(cOAOpeningTableModel);
        cOAOpeningTableModel.setSelectionObserver(this);
        cOAOpeningTableModel.setParent(tblOpening);
        tblOpening.getTableHeader().setFont(Global.textFont);
        tblOpening.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOpening.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblOpening.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblOpening.getColumnModel().getColumn(2).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(3).setPreferredWidth(250);
        tblOpening.getColumnModel().getColumn(4).setPreferredWidth(5);
        tblOpening.getColumnModel().getColumn(5).setPreferredWidth(10);
        tblOpening.getColumnModel().getColumn(6).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(7).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblOpening.setDefaultRenderer(Double.class, new TableCellRender());
        tblOpening.setDefaultRenderer(Object.class, new TableCellRender());
        tblOpening.setCellSelectionEnabled(true);
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        
    }
    
    private void searchOpening() {
        initializeParameter();
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            List<VGl> listVGl = vGlService.search(stDate, endDate, "-", "-", "-", curId, "-", "-", depCode,
                    "-", "-", "-", Global.compId.toString(), "OPENING", "-", "-", "-", "-", "-", "-", "-");
            btnGen.setEnabled(listVGl.isEmpty());
            cOAOpeningTableModel.setListVGl(listVGl);
            calTotalAmt(listVGl);
            //btnGen.setEnabled(false);
            loadingObserver.load(this.getName(), "Stop");
        });
    }
    
    private void initializeParameter() {
        stDate = Util1.toDateStr(Global.finicialPeriodFrom, "yyyy-MM-dd", "dd/MM/yyyy");
        endDate = Util1.toDateStr(txtDate.getDate(), "dd/MM/yyyy");
        depCode = Util1.isNull(depCode, "-");
        curId = Util1.isNull(curId, "-");
        if (txtCurrency.getText().isEmpty()) {
            curId = "-";
        }
        if (txtDep.getText().isEmpty()) {
            depCode = "-";
        }
        btnGen.setEnabled(false);
    }
    
    private void calTotalAmt(List<VGl> listVGl) {
        double drAmt = 0.0;
        double crAmt = 0.0;
        for (VGl vgl : listVGl) {
            drAmt += Util1.getDouble(vgl.getDrAmt());
            crAmt += Util1.getDouble(vgl.getCrAmt());
        }
        txtFCrAmt.setValue(crAmt);
        txtFDrAmt.setValue(drAmt);
        if (!listVGl.isEmpty()) {
            txtDisplayCur.setText(listVGl.get(0).getFromCurId());
        }
    }
    
    private boolean isValidGen() {
        boolean status = true;
        if (txtDep.getText().isEmpty() || depCode == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Select Department.");
            txtDep.requestFocus();
            status = false;
        }
        if (txtCurrency.getText().isEmpty() || curId == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Select Currency.");
            txtCurrency.requestFocus();
            status = false;
        }
        return status;
    }
    
    private void generate() {
        if (isValidGen()) {
            loadingObserver.load(this.getName(), "Start");
            taskExecutor.execute(() -> {
                btnGen.setEnabled(false);
                try {
                    String userId = Global.loginUser.getUserId().toString();
                    String coaGroup = Global.sysProperties.get("system.opening.coa.group");
                    if (coaGroup != null) {
                        cOAOpeningService.GenerateZeroGL(Util1.toDateStr(txtDate.getDate(), "dd/MM/yyyy"),
                                userId, Global.compId.toString(), curId, depCode, coaGroup);
                        searchOpening();
                        btnGen.setEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(Global.parentForm, "COA Group Openning in System Property.");
                    }
                } catch (Exception ex) {
                    LOGGER.error("GENERATE  :" + ex.getMessage());
                    JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage(), "GENERATE OPENING", JOptionPane.ERROR_MESSAGE);
                    btnGen.setEnabled(true);
                }
                
            });
        }
        
    }
    
    private void initKeyListener() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDep.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        btnGen.addKeyListener(this);
        tblOpening.addKeyListener(this);
        
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
        txtDep = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        btnGen = new javax.swing.JButton();
        txtDate = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOpening = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtDisplayCur = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtFDrAmt = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtFCrAmt = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtDep"); // NOI18N
        txtDep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDepActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

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

        btnGen.setFont(Global.lableFont);
        btnGen.setText("Genearte Zero");
        btnGen.setEnabled(false);
        btnGen.setName("btnGen"); // NOI18N
        btnGen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenActionPerformed(evt);
            }
        });

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.shortCutFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDep, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnGen)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnGen)))
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

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Currency");

        txtDisplayCur.setEditable(false);
        txtDisplayCur.setFont(Global.amtFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Dr-Amt");

        txtFDrAmt.setEditable(false);
        txtFDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFDrAmt.setFont(Global.amtFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Cr-Amt");

        txtFCrAmt.setEditable(false);
        txtFCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFCrAmt.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtDisplayCur)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtFDrAmt)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtFCrAmt)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDisplayCur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtFCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void txtDepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepActionPerformed
        // TODO add your handling code here:
        searchOpening();
    }//GEN-LAST:event_txtDepActionPerformed

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
        searchOpening();
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void btnGenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenActionPerformed
        // TODO add your handling code here:
        generate();
    }//GEN-LAST:event_btnGenActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOpening;
    private javax.swing.JTextField txtCurrency;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtDisplayCur;
    private javax.swing.JFormattedTextField txtFCrAmt;
    private javax.swing.JFormattedTextField txtFDrAmt;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        LOGGER.info("COA OPENING INTERFACE " + source.toString());
        String name = source.toString();
        switch (name) {
            case "Department":
                depCode = selectObj.toString();
                searchOpening();
                break;
            case "Currency":
                curId = selectObj.toString();
                searchOpening();
                break;
            case "CAL-TOTAL":
                calTotalAmt(cOAOpeningTableModel.getListVGl());
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
            case "txtDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDep.requestFocus();
                    
                }
                tabToTable(e);
                break;
            case "txtDep":
                /*if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                txtCurrency.requestFocus();
                }*/
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                        txtCurrency.requestFocus();
                        break;
                    /*case KeyEvent.VK_ENTER:
                        txtCurrency.requestFocus();
                        break;*/
                    case KeyEvent.VK_LEFT:
                        txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                        break;
                }
                tabToTable(e);
                
                break;
            case "txtCurrency":
                /*if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                btnGen.requestFocus();
                }*/
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDep.requestFocus();
                }
                tabToTable(e);
                
                break;
            case "btnGen":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtCurrency.requestFocus();
                }
                tabToTable(e);
                break;
            case "tblOpening":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
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
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }
}
