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
import com.cv.accountswing.entity.CompanyInfo;
import com.cv.accountswing.entity.SystemProperty;
import com.cv.accountswing.entity.SystemPropertyKey;
import com.cv.accountswing.entity.helper.ProfitAndLostRetObj;
import com.cv.accountswing.service.CompanyInfoService;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.service.SystemPropertyService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DateAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.util.Util1;
import java.util.HashMap;
import java.util.Map;
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

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BalanceSheet.class);

    private LoadingObserver loadingObserver;
    private String stDate;
    private String enDate;
    private String depId;
    private String currency;
    private boolean isShown = false;
    @Autowired
    private SystemPropertyService spService;
    @Autowired
    private ReportService rService;
    @Autowired
    private CompanyInfoService ciService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private CurrencyAutoCompleter currencyAutoCompleter;

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
        DepartmentAutoCompleter departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, Global.listDepartment, null);
        departmentAutoCompleter.setSelectionObserver(this);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        currencyAutoCompleter.setSelectionObserver(this);
        currencyAutoCompleter.setCurrency(Global.defalutCurrency);

    }

    private void calBalanceSheet() {
        loadingObserver.load(this.getName(), "Start");
        currency = currencyAutoCompleter.getCurrency().getKey().getCode();
        taskExecutor.execute(() -> {
            try {
                String userId = Global.loginUser.getUserCode();
                CompanyInfo ci = ciService.findById(Global.compCode);
                String from = Global.finicialPeriodFrom;
                rService.genBalanceSheet(from, enDate, depId, userId, Global.compCode, currency);

                SystemPropertyKey key = new SystemPropertyKey();
                key.setCompCode(Global.compCode);
                key.setPropKey("system.report.path");
                SystemProperty sp = spService.findById(key);
                String fileName = userId + "_Ledger_Report.pdf";
                String reportPath = sp.getPropValue();
                //String reportPath1 = reportPath;
                String filePath = reportPath + "/temp/" + fileName;

                reportPath = reportPath + "LedgerReport";
                key = new SystemPropertyKey();
                key.setCompCode(Global.compCode);
                key.setPropKey("system.font.path");
                sp = spService.findById(key);
                String fontPath = sp.getPropValue();

                Map<String, Object> parameters = new HashMap();
                parameters.put("p_company_name", ci.getName());
                parameters.put("p_comp_id", Global.compCode);
                parameters.put("p_report_info", from
                        + " to " + enDate);
                parameters.put("p_from", from);
                parameters.put("p_to", enDate);
                parameters.put("p_user_id", userId);
                rService.genCreditVoucher(reportPath, filePath, fontPath, parameters);
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception ex) {
                LOGGER.error("getBalanceSheet : " + ex.getMessage());

            }
        });

    }

    private void printBalSheet() {
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            String userId = Global.loginUser.getUserCode();
            try {
                CompanyInfo ci = ciService.findById(Global.compCode);
                SystemPropertyKey key = new SystemPropertyKey();
                key.setCompCode(Global.compCode);
                key.setPropKey("system.report.path");
                SystemProperty sp = spService.findById(key);
                String fileName = userId + "_profit_lost.pdf";
                String reportPath = sp.getPropValue();
                String reportPath1 = reportPath;
                String filePath = reportPath + "/temp/" + fileName;

                reportPath = reportPath + "ProfitAndLost";
                key = new SystemPropertyKey();
                key.setCompCode(Global.compCode);
                key.setPropKey("system.font.path");
                sp = spService.findById(key);
                String fontPath = sp.getPropValue();

                //Need to calculate sub todal
                ProfitAndLostRetObj obj = rService.getPLCalculateValue(userId, Global.compCode);
                LOGGER.info("cost of sale : " + obj.getCostOfSale());
                //==================================================================

                Map<String, Object> parameters = new HashMap();
                parameters.put("p_company_name", ci.getName());
                parameters.put("p_comp_id", Global.compCode);
                parameters.put("SUBREPORT_DIR", reportPath1);
                parameters.put("p_user_id", userId);
                parameters.put("gross_profit", obj.getGrossProfit());
                parameters.put("net_profit", obj.getNetProfit());
                parameters.put("p_report_info", stDate
                        + " to " + enDate);
                parameters.put("p_cost_of_sale", obj.getCostOfSale());

                rService.genCreditVoucher(reportPath, filePath, fontPath, parameters);
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception ex) {
                LOGGER.error("getProfitAndLostReport : " + ex);
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
                    depId = selectObj.toString();
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
