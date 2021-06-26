/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.helper.BalanceSheetRetObj;
import com.cv.accountswing.service.COAOpeningDService;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DateAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.util.Util1;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
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
public class BalanceSheet extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private static final Logger log = LoggerFactory.getLogger(BalanceSheet.class);

    private LoadingObserver loadingObserver;
    private String stDate;
    private String enDate;
    private String currency;
    private boolean isShown = false;
    @Autowired
    private ReportService rService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private COAOpeningDService dService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    /**
     * Creates new form ProtfitAndLost
     */
    public BalanceSheet() {
        initComponents();
    }

    private void initMain() {
        initCombo();
        txtDate.setText("Today");
        stDate = Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy");
        enDate = stDate;
    }

    private void initCombo() {
        DateAutoCompleter dateAutoCompleter = new DateAutoCompleter(txtDate, Global.listDateModel, null);
        dateAutoCompleter.setSelectionObserver(this);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, Global.listDepartment, null, true);
        departmentAutoCompleter.setSelectionObserver(this);
        departmentAutoCompleter.setDepartment(new Department("-", "All"));
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        currencyAutoCompleter.setSelectionObserver(this);
        currencyAutoCompleter.setCurrency(Global.defalutCurrency);

    }

    private void calBalanceSheet() {
        loadingObserver.load(this.getName(), "Start");
        btnCalculate.setEnabled(false);
        currency = currencyAutoCompleter.getCurrency().getKey().getCode();
        String depCode = departmentAutoCompleter.getDepartment().getDeptCode();
        String bsProcess = Global.sysProperties.get("system.balancesheet.process");
        String plProcess = Global.sysProperties.get("system.profitlost.process");
        String invCOA = Global.sysProperties.get("system.inventory.coa");
        if (Util1.isNull(bsProcess, plProcess, invCOA)) {
            JOptionPane.showMessageDialog(Global.parentForm, "Calculation Process is not assigned.");
            btnCalculate.setEnabled(true);
            loadingObserver.load(this.getName(), "Stop");
        } else {
            taskExecutor.execute(() -> {
                try {
                    //generating tri
                    dService.genTriBalance1(Global.compCode, Global.finicialPeriodFrom, Util1.toDateStrMYSQL(enDate, "dd/MM/yyyy"),
                            "-", currency, depCode, "-", "-", Global.machineId.toString());
                    log.info("calculate tri");
                    //genreateing pl
                    rService.getProfitLost(plProcess, stDate, enDate, Util1.isNull(depCode, "-"),
                            currency, Global.compCode, Global.loginUser.getAppUserCode(),
                            Global.machineId.toString(), invCOA);
                    log.info("calculate pl");
                    //genereating balance
                    rService.genBalanceSheet(stDate, enDate, depCode,
                            Global.compCode, currency, Global.machineId.toString(), bsProcess);
                    log.info("Balance Sheet Generation Success. ");
                    btnCalculate.setEnabled(true);
                    loadingObserver.load(this.getName(), "Stop");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
                    log.error("getBalanceSheet : " + ex.getMessage());
                    btnCalculate.setEnabled(true);
                }
            });
        }

    }

    private void printBalSheet() {
        taskExecutor.execute(() -> {
            try {
                loadingObserver.load(this.getName(), "Start");
                String reportInfo = txtDate.getText();
                String rpPath = Global.sysProperties.get("system.report.path") + File.separator;
                String reportPath = rpPath + "BalanceSheetDetail";
                String fontPath = Global.sysProperties.get("system.font.path");
                BalanceSheetRetObj bs = rService.getBSCalculateValue(Global.compCode, Global.machineId.toString());
                log.info("fixed" + bs.getFixedAss());
                log.info("current" + bs.getCurrentAss());
                log.info("capital" + bs.getCapital());
                log.info("liab" + bs.getLiabilitie());
                //==================================================================
                Map<String, Object> parameters = new HashMap();
                parameters.put("p_company_name", Global.companyName);
                parameters.put("p_comp_id", Global.compCode);
                parameters.put("SUBREPORT_DIR", rpPath);
                parameters.put("p_mac_id", Global.machineId);
                parameters.put("p_report_info", reportInfo);
                parameters.put("p_print_date", Util1.getReportDate());
                parameters.put("p_total_asset", bs.getFixedAss() + bs.getCurrentAss());
                parameters.put("p_total", bs.getCapital() + bs.getLiabilitie() + bs.getProfit());
                rService.genReport(reportPath, reportPath, fontPath, parameters);
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
            }
        });
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
        txtDep = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnCalculate = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtDate.setFont(Global.textFont);

        txtDep.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setEnabled(false);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

        btnCalculate.setFont(Global.lableFont);
        btnCalculate.setText("Calculate");
        btnCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateActionPerformed(evt);
            }
        });

        btnPrint.setFont(Global.lableFont);
        btnPrint.setText("Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
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
                .addComponent(txtDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDep)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtCurrency)
                .addGap(18, 18, 18)
                .addComponent(btnCalculate)
                .addGap(18, 18, 18)
                .addComponent(btnPrint)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(btnCalculate)
                    .addComponent(btnPrint))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
    }//GEN-LAST:event_formComponentShown

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        print();
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateActionPerformed
        // TODO add your handling code here:
        calBalanceSheet();
    }//GEN-LAST:event_btnCalculateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculate;
    private javax.swing.JButton btnPrint;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
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
                    break;
                case "Department":
                    break;
                case "Currency":
                    currency = selectObj.toString();
                    break;

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
        printBalSheet();
    }

    @Override
    public void refresh() {
        calBalanceSheet();
    }
}
