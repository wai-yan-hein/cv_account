/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.filter;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.ui.editor.COAAutoCompleter;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DateAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.ui.cash.AllCash;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entry.editor.StockAutoCompleter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
@Component
public class FilterPanel extends javax.swing.JPanel implements KeyListener, SelectionObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterPanel.class);

    @Autowired
    private TraderService traderService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private COAService cOAService;
    @Autowired
    private CurrencyService currencyService;
    private DateAutoCompleter dateAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private COAAutoCompleter coaAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private SelectionObserver selectionObserver;
    @Autowired
    private TaskExecutor taskExecutor;

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    private void closePopup() {
        dateAutoCompleter.closePopup();
        traderAutoCompleter.closePopup();
        departmentAutoCompleter.closePopup();
        coaAutoCompleter.closePopup();
        currencyAutoCompleter.closePopup();
    }

    /**
     * Creates new form FilterPanel
     */
    public FilterPanel() {
        initComponents();
        initKeyListener();

    }

    public void initMain() {
        initializeData();

    }

    private void initializeData() {
        taskExecutor.execute(() -> {
            traderAutoCompleter = new TraderAutoCompleter(txtPerson, Global.listTrader, null);
            departmentAutoCompleter = new DepartmentAutoCompleter(txtDepartment, Global.listDepartment, null);
            coaAutoCompleter = new COAAutoCompleter(txtAccount, Global.listCOA, null);
            dateAutoCompleter = new DateAutoCompleter(txtDate, Global.listDateModel, null);
            currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
            dateAutoCompleter.setSelectionObserver(selectionObserver);
            departmentAutoCompleter.setSelectionObserver(selectionObserver);
            traderAutoCompleter.setSelectionObserver(selectionObserver);
            coaAutoCompleter.setSelectionObserver(selectionObserver);
            currencyAutoCompleter.setSelectionObserver(selectionObserver);
            currencyAutoCompleter.setCurrency(Global.defalutCurrency);
            txtDate.setText("Today");
        });

    }

    private void initKeyListener() {
        txtDate.addKeyListener(this);
        txtDepartment.addKeyListener(this);
        txtAccount.addKeyListener(this);
        txtPerson.addKeyListener(this);
        txtDesp.addKeyListener(this);
        txtRefrence.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        txtCreditAmt.addKeyListener(this);
        txtDebitAmt.addKeyListener(this);
    }

    public void clear() {
        txtDate.setText(null);
        txtDepartment.setText(null);
        txtAccount.setText(null);
        txtPerson.setText(null);
        txtDesp.setText(null);
        txtRefrence.setText(null);
        txtCurrency.setText(null);
        txtCreditAmt.setText(null);
        txtDebitAmt.setText(null);
        txtDate.requestFocus();
        txtDate.setText("Today");
        String depId = Global.sysProperties.get("system.default.department");
        Department dep = departmentService.findById(depId);
        if (dep != null) {
            txtDepartment.setText(dep.getDeptName());
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

        jLabel1 = new javax.swing.JLabel();
        txtDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDepartment = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPerson = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtRefrence = new javax.swing.JTextField();
        txtCurrency = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDebitAmt = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCreditAmt = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtDate.setFont(Global.textFont);
        txtDate.setName("txtDate"); // NOI18N
        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDateFocusLost(evt);
            }
        });
        txtDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDateActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        txtDepartment.setFont(Global.textFont);
        txtDepartment.setName("txtDepartment"); // NOI18N
        txtDepartment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepartmentFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDepartmentFocusLost(evt);
            }
        });
        txtDepartment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDepartmentActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Person");

        txtPerson.setFont(Global.textFont);
        txtPerson.setName("txtPerson"); // NOI18N
        txtPerson.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPersonFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPersonFocusLost(evt);
            }
        });
        txtPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPersonActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Account");

        txtAccount.setFont(Global.textFont);
        txtAccount.setName("txtAccount"); // NOI18N
        txtAccount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAccountFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAccountFocusLost(evt);
            }
        });
        txtAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAccountActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Description");

        txtDesp.setFont(Global.textFont);
        txtDesp.setName("txtDesp"); // NOI18N
        txtDesp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDespFocusLost(evt);
            }
        });
        txtDesp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDespActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Refrence");

        txtRefrence.setFont(Global.textFont);
        txtRefrence.setName("txtRefrence"); // NOI18N
        txtRefrence.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRefrenceFocusLost(evt);
            }
        });
        txtRefrence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRefrenceActionPerformed(evt);
            }
        });

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setEnabled(false);
        txtCurrency.setName("txtCurrency"); // NOI18N
        txtCurrency.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCurrencyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCurrencyFocusLost(evt);
            }
        });
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Currency");

        txtDebitAmt.setFont(Global.textFont);
        txtDebitAmt.setName("txtDebitAmt"); // NOI18N
        txtDebitAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDebitAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDebitAmtFocusLost(evt);
            }
        });
        txtDebitAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDebitAmtActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Dr Amt");

        txtCreditAmt.setFont(Global.textFont);
        txtCreditAmt.setName("txtCreditAmt"); // NOI18N
        txtCreditAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCreditAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCreditAmtFocusLost(evt);
            }
        });
        txtCreditAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCreditAmtActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Cr Amt");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(txtDepartment, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDesp, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRefrence, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPerson, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAccount, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDebitAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCreditAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel3)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDebitAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCreditAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtAccount, txtCreditAmt, txtCurrency, txtDate, txtDebitAmt, txtDepartment, txtDesp, txtPerson, txtRefrence});

    }// </editor-fold>//GEN-END:initComponents

    private void txtPersonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPersonFocusGained
        // TODO add your handling code here:
        txtPerson.selectAll();
        //traderAutoCompleter.showPopup();

    }//GEN-LAST:event_txtPersonFocusGained

    private void txtPersonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPersonFocusLost
        // TODO add your handling code here:

    }//GEN-LAST:event_txtPersonFocusLost

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void txtDepartmentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepartmentFocusGained
        // TODO add your handling code here:

        txtDepartment.selectAll();
        //departmentAutoCompleter.showPopup();

    }//GEN-LAST:event_txtDepartmentFocusGained

    private void txtAccountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusGained
        // TODO add your handling code here:

        txtAccount.selectAll();
        //coaAutoCompleter.showPopup();
    }//GEN-LAST:event_txtAccountFocusGained

    private void txtDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusGained
        // TODO add your handling code here:
        txtDate.selectAll();
    }//GEN-LAST:event_txtDateFocusGained

    private void txtCurrencyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrencyFocusGained
        // TODO add your handling code here:

        txtCurrency.selectAll();
        // currencyAutoCompleter.showPopup();
    }//GEN-LAST:event_txtCurrencyFocusGained

    private void txtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusLost
        // TODO add your handling code here:
        //dateAutoCompleter.closePopup();
        //messageBean.setValue(txtDate.getText());
    }//GEN-LAST:event_txtDateFocusLost

    private void txtDepartmentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepartmentFocusLost
        // TODO add your handling code here:

    }//GEN-LAST:event_txtDepartmentFocusLost

    private void txtAccountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusLost
        // TODO add your handling code here:
        //coaAutoCompleter.closePopup();

    }//GEN-LAST:event_txtAccountFocusLost

    private void txtCurrencyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrencyFocusLost
        // TODO add your handling code here:
        //currencyAutoCompleter.closePopup();

    }//GEN-LAST:event_txtCurrencyFocusLost

    private void txtDebitAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDebitAmtFocusGained
        // TODO add your handling code here:
        txtDebitAmt.selectAll();
    }//GEN-LAST:event_txtDebitAmtFocusGained

    private void txtDebitAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDebitAmtFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDebitAmtFocusLost

    private void txtCreditAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCreditAmtFocusGained
        // TODO add your handling code here:
        txtCreditAmt.selectAll();
    }//GEN-LAST:event_txtCreditAmtFocusGained

    private void txtCreditAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCreditAmtFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCreditAmtFocusLost

    private void txtDespFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDespFocusLost
        // TODO add your handling code here:

    }//GEN-LAST:event_txtDespFocusLost

    private void txtRefrenceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRefrenceFocusLost
        // TODO add your handling code here:

    }//GEN-LAST:event_txtRefrenceFocusLost

    private void txtDespActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDespActionPerformed
        // TODO add your handling code here:
        selectionObserver.selected("Description", Util1.isNull(txtDesp.getText(), "-"));
    }//GEN-LAST:event_txtDespActionPerformed

    private void txtRefrenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefrenceActionPerformed
        // TODO add your handling code here:
        selectionObserver.selected("Ref", Util1.isNull(txtRefrence.getText(), "-"));
    }//GEN-LAST:event_txtRefrenceActionPerformed

    private void txtDebitAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDebitAmtActionPerformed
        // TODO add your handling code here:
        selectionObserver.selected("DrAmt", Util1.isNull(txtDebitAmt.getText(), "-"));

    }//GEN-LAST:event_txtDebitAmtActionPerformed

    private void txtCreditAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCreditAmtActionPerformed
        // TODO add your handling code here:
        selectionObserver.selected("CrdAmt", Util1.isNull(txtCreditAmt.getText(), "-"));
    }//GEN-LAST:event_txtCreditAmtActionPerformed

    private void txtDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepartmentActionPerformed
        // TODO add your handling code here:
        if (txtDepartment.getText().isEmpty()) {
            selectionObserver.selected("Department", "-");
        }
    }//GEN-LAST:event_txtDepartmentActionPerformed

    private void txtPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPersonActionPerformed
        // TODO add your handling code here:
        if (txtPerson.getText().isEmpty()) {
            selectionObserver.selected("Trader", "-");
        }
    }//GEN-LAST:event_txtPersonActionPerformed

    private void txtAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAccountActionPerformed
        // TODO add your handling code here:
        if (txtAccount.getText().isEmpty()) {
            selectionObserver.selected("COA", "-");
        }
    }//GEN-LAST:event_txtAccountActionPerformed

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
        if (txtCurrency.getText().isEmpty()) {
            selectionObserver.selected("Currency", "-");
        }
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtCreditAmt;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JTextField txtDebitAmt;
    private javax.swing.JTextField txtDepartment;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JTextField txtPerson;
    private javax.swing.JTextField txtRefrence;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
        /* Object sourceObj = e.getSource();
        String ctrlName;
        
        if (sourceObj instanceof JTextField) {
        ctrlName = ((JTextField) sourceObj).getName();
        switch (ctrlName) {
        case "txtDesp":
        if (selectionObserver != null) {
        selectionObserver.selected("Description", txtDesp.getText());
        }
        break;
        case "txtRefrence":
        if (selectionObserver != null) {
        selectionObserver.selected("Ref", txtRefrence.getText());
        }
        break;
        case "txtCreditAmt":
        if (selectionObserver != null) {
        selectionObserver.selected("CrdAmt", txtCreditAmt.getText());
        }
        break;
        case "txtDebitAmt":
        if (selectionObserver != null) {
        selectionObserver.selected("DrAmt", txtDebitAmt.getText());
        }
        break;
        
        }
        }*/
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName;

        if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
            switch (ctrlName) {
                case "txtDate":
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        txtDepartment.requestFocus();
                        closePopup();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        txtCreditAmt.requestFocus();
                        closePopup();
                    }
                    tabToCashTable(e);
                    break;

                case "txtDepartment":
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        txtDesp.requestFocus();
                        closePopup();

                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        txtDate.requestFocus();
                        closePopup();

                    }
                    tabToCashTable(e);

                    break;
                case "txtPerson":
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        txtAccount.requestFocus();
                        closePopup();

                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        txtRefrence.requestFocus();
                        closePopup();

                    }
                    tabToCashTable(e);

                    break;

                case "txtAccount":
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        txtCurrency.requestFocus();
                        closePopup();

                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        txtPerson.requestFocus();
                        closePopup();

                    }
                    tabToCashTable(e);

                    break;

                case "txtDesp":
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        txtRefrence.requestFocus();
                        closePopup();

                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        txtDepartment.requestFocus();
                        closePopup();

                    }
                    tabToCashTable(e);

                    break;

                case "txtCurrency":
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        txtDebitAmt.requestFocus();
                        closePopup();

                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        txtAccount.requestFocus();
                        closePopup();

                    }
                    tabToCashTable(e);

                    break;
                case "txtRefrence":
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        txtPerson.requestFocus();
                        closePopup();

                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        txtDesp.requestFocus();
                        closePopup();

                    }
                    tabToCashTable(e);

                    break;
                case "txtDebitAmt":
                    txtCreditAmt.setText("");
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        txtCreditAmt.requestFocus();
                        closePopup();
                        if (!txtDebitAmt.getText().isEmpty()) {

                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        txtCurrency.requestFocus();
                        closePopup();
                    }
                    tabToCashTable(e);

                    break;
                case "txtCreditAmt":
                    txtDebitAmt.setText("");
                    if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        txtDate.requestFocus();
                        closePopup();
                        if (!txtCreditAmt.getText().isEmpty()) {

                        }

                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        txtDebitAmt.requestFocus();
                        closePopup();

                    }
                    tabToCashTable(e);
                    break;
            }

        }
    }

    private void tabToCashTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (selectionObserver != null) {
                closePopup();
                if (selectionObserver instanceof AllCash) {
                    AllCash allCash = (AllCash) selectionObserver;
                    allCash.setSelectionObserver(this);
                }
                selectionObserver.selected("tabToCashTable", "TAB");
            }
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            if (source.equals("TABTOTEXTBOX")) {
                txtDate.requestFocus();
            }
        }

    }
}
