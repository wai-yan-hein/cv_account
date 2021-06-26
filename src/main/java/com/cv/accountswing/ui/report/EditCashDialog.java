/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.entity.Supplier;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.GlService;
import com.cv.accountswing.ui.cash.common.AllCashTableModel;
import com.cv.accountswing.ui.editor.COAAutoCompleter;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.editor.SupplierAutoCompleter;
import com.cv.accountswing.util.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.DateFormat;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class EditCashDialog extends javax.swing.JDialog {

    private final Logger LOGGER = LoggerFactory.getLogger(AllCashTableModel.class);
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private DepartmentAutoCompleter departmentAutoCompleter;
    private SupplierAutoCompleter traderAutoCompleter;
    private COAAutoCompleter cOAAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private String dType;
    private SelectionObserver selectionObserver;

    public SelectionObserver getSelectionObserver() {
        return selectionObserver;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public String getdType() {
        return dType;
    }

    public void setdType(String dType) {
        this.dType = dType;
    }

    @Autowired
    private GlService glService;

    private VGl vGl;

    public VGl getvGl() {
        return vGl;
    }

    public void setvGl(VGl vGl) {
        this.vGl = vGl;
    }

    /**
     * Creates new form EditCashDialog
     */
    public EditCashDialog() {
        super(new JFrame(), true);
        initComponents();
    }

    private void setData(VGl vgl) {
        txtName.setText(vgl.getSrcAccName());
        txtDate.setDate(vgl.getGlDate());
        txtCashIn.setValue(vgl.getDrAmt());
        txtCashOut.setValue(vgl.getCrAmt());
        txtDesp.setText(vgl.getDescription());
        txtRef.setText(vgl.getReference());
        currencyAutoCompleter.setCurrency(Global.defalutCurrency);
        departmentAutoCompleter.setDepartment(new Department(vgl.getDeptId(), vgl.getDeptName()));
        cOAAutoCompleter.setCoa(new ChartOfAccount(vgl.getAccountId(), vgl.getAccName()));
        if (vgl.getTraderCode() != null) {
            traderAutoCompleter.setTrader(new Supplier(vgl.getTraderCode(), vgl.getTraderName()));
        }
    }

    private void initCombo() {
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, Global.listDepartment, null, false);
        traderAutoCompleter = new SupplierAutoCompleter(txtPerson, Global.listSupplier, null);
        cOAAutoCompleter = new COAAutoCompleter(txtAccount, Global.listCOA, null, false);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
    }

    private void save() {
        if (isValidEntry()) {
            try {
                vGl.setAccountId(cOAAutoCompleter.getCOA().getCode());
                vGl.setDeptId(departmentAutoCompleter.getDepartment().getDeptCode());
                vGl.setfCurName(currencyAutoCompleter.getCurrency().getKey().getCode());
                vGl.setGlDate(txtDate.getDate());
                vGl.setDescription(txtDesp.getText());
                vGl.setReference(txtRef.getText());
                vGl.setDrAmt(Util1.getDouble(txtCashIn.getValue()));
                vGl.setCrAmt(Util1.getDouble(txtCashOut.getValue()));
                if (traderAutoCompleter.getTrader() != null) {
                    vGl.setTraderCode(traderAutoCompleter.getTrader().getCode());
                }
                Gl gl = gson.fromJson(gson.toJson(vGl), Gl.class);
                glService.save(gl);
                sendToParent(vGl);
            } catch (Exception ex) {
                LOGGER.error("Save GL :" + ex.getMessage());
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage(), "Save Gl", JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    private void sendToParent(VGl vGl) {
        if (dType.equals("DR")) {
            selectionObserver.selected("SAVE-GL-DR", vGl);
        } else {
            selectionObserver.selected("SAVE-GL-CR", vGl);
        }
        this.dispose();
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (cOAAutoCompleter.getCOA() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Select Account");
            txtAccount.requestFocus();
        }
        if (departmentAutoCompleter.getDepartment() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Select Department");
            txtDep.requestFocus();
        }
        if (currencyAutoCompleter.getCurrency() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Select Currency");
        }
        if (txtCashIn.getValue() == null && txtCashOut.getValue() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Cash In/ Cash Out Amount.");
        }
        return status;
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
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRef = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPerson = new javax.swing.JTextField();
        txtAccount = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        txtCashIn = new javax.swing.JFormattedTextField();
        txtCashOut = new javax.swing.JFormattedTextField();
        panel = new javax.swing.JPanel();
        txtName = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Cash");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setNextFocusableComponent(txtDep);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setNextFocusableComponent(txtDesp);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Description");

        txtDesp.setFont(Global.textFont);
        txtDesp.setNextFocusableComponent(txtRef);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Ref");

        txtRef.setFont(Global.textFont);
        txtRef.setNextFocusableComponent(txtPerson);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Person");

        txtPerson.setFont(Global.textFont);
        txtPerson.setNextFocusableComponent(txtAccount);

        txtAccount.setFont(Global.textFont);
        txtAccount.setNextFocusableComponent(txtCashIn);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Account");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Currency");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setEnabled(false);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Cash In / Dr");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Cash Out / Cr");

        jButton1.setBackground(ColorUtil.mainColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(ColorUtil.foreground);
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtCashIn.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtCashIn.setFont(Global.textFont);
        txtCashIn.setNextFocusableComponent(txtCashOut);

        txtCashOut.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtCashOut.setFont(Global.textFont);
        txtCashOut.setNextFocusableComponent(jButton1);

        panel.setBackground(ColorUtil.mainColor);
        panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        panel.setFont(Global.textFont);

        txtName.setFont(Global.menuFont);
        txtName.setForeground(new java.awt.Color(255, 255, 255));
        txtName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtName.setText("Name");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                            .addComponent(txtDep)
                            .addComponent(txtDesp)
                            .addComponent(txtRef)
                            .addComponent(txtPerson)
                            .addComponent(txtAccount)
                            .addComponent(txtCurrency)
                            .addComponent(txtCashIn)
                            .addComponent(txtCashOut)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtCashIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtCashOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        initCombo();
        setData(vGl);
    }//GEN-LAST:event_formComponentShown

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        save();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        int yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Do you want to save changes", "Save Cash", JOptionPane.YES_NO_OPTION);
        if (yes_no == JOptionPane.YES_OPTION) {
            save();
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel panel;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JFormattedTextField txtCashIn;
    private javax.swing.JFormattedTextField txtCashOut;
    private javax.swing.JTextField txtCurrency;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JLabel txtName;
    private javax.swing.JTextField txtPerson;
    private javax.swing.JTextField txtRef;
    // End of variables declaration//GEN-END:variables
}
