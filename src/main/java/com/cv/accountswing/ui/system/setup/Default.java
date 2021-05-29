/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.CurrencyKey;
import com.cv.accountswing.entity.Customer;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.RoleStatus;
import com.cv.accountswing.entity.RoleStatusKey;
import com.cv.accountswing.entity.Supplier;
import com.cv.accountswing.entity.UserDefault;
import com.cv.accountswing.entity.UserDefaultKey;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.CustomerService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.RoleStatusService;
import com.cv.accountswing.service.SupplierService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.service.UserSettingService;
import com.cv.accountswing.ui.ApplicationMainFrame;

import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import java.util.List;
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
public class Default extends javax.swing.JPanel implements PanelControl {

    private static final Logger log = LoggerFactory.getLogger(Default.class);
    @Autowired
    private UserSettingService userSettingService;
    @Autowired
    private RoleSetting userSetting;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RoleStatusService statusService;
    @Autowired
    private TraderService traderServoce;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private TraderAutoCompleter supplierAutoCompleter;
    private TraderAutoCompleter customerAutoCompleter;

    /**
     * Creates new form Default
     */
    public Default() {
        initComponents();
    }

    public void initMain() {
        initAutoCompleter();
    }

    private void initAutoCompleter() {
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, Global.listDepartment, null, false);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCur, Global.listCurrency, null);
        supplierAutoCompleter = new TraderAutoCompleter(txtSup, Global.listTrader, null, false, 0);
        customerAutoCompleter = new TraderAutoCompleter(txtCus, Global.listTrader, null, false, 0);
    }

    public Department getDepartment(String roleCode) {
        Department dep = null;
        List<UserDefault> listDep = userSettingService.search(roleCode, Global.compCode, Global.DEP_KEY);
        if (!listDep.isEmpty()) {
            dep = departmentService.findById(listDep.get(0).getKey().getValue());
        }
        return dep;
    }

    public Currency getCurrency(String roleCode) {
        Currency cur = null;
        List<UserDefault> listCur = userSettingService.search(roleCode, Global.compCode, Global.CUR_KEY);
        if (!listCur.isEmpty()) {
            CurrencyKey curKey = new CurrencyKey();
            curKey.setCompCode(Global.compCode);
            curKey.setCode(listCur.get(0).getKey().getValue());
            cur = currencyService.findById(curKey);
        }
        return cur;
    }

    public Supplier getSuppplier(String roleCode) {
        Supplier sup = null;
        List<UserDefault> listSup = userSettingService.search(roleCode, Global.compCode, Global.SUP_KEY);
        if (!listSup.isEmpty()) {
            sup = supplierService.findById(listSup.get(0).getKey().getValue());
        }
        return sup;
    }

    public Customer getCutomer(String roleCode) {
        Customer cus = null;
        List<UserDefault> listCus = userSettingService.search(roleCode, Global.compCode, Global.CUS_KEY);
        if (!listCus.isEmpty()) {
            cus = customerService.findById(listCus.get(0).getKey().getValue());
        }
        return cus;
    }

    public void loadDefaultData(String roleCode) {
        log.info("load default data. ");
        if (roleCode != null) {
            //
            departmentAutoCompleter.setDepartment(getDepartment(roleCode));
            currencyAutoCompleter.setCurrency(getCurrency(roleCode));
            supplierAutoCompleter.setTrader(getSuppplier(roleCode));
            customerAutoCompleter.setTrader(getCutomer(roleCode));
            chkCBDel.setSelected(statusService.checkPermission(roleCode, Global.CB_DEL_KEY));
            chkRCBDel.setSelected(statusService.checkPermission(roleCode, Global.CB_DEL_USR_KEY));

        }
    }

    private void saveDefault() {
        String roleCode = userSetting.getRoleCode();
        if (roleCode != null) {
            if (departmentAutoCompleter.getDepartment() != null) {
                String defaultDep = departmentAutoCompleter.getDepartment().getDeptCode();
                UserDefault def = new UserDefault();
                UserDefaultKey key = new UserDefaultKey();
                key.setRoleCode(roleCode);
                key.setKey(Global.DEP_KEY);
                key.setValue(defaultDep);
                key.setCompCode(Global.compCode);
                def.setKey(key);
                userSettingService.save(def);
            }
            if (currencyAutoCompleter.getCurrency() != null) {
                String code = currencyAutoCompleter.getCurrency().getKey().getCode();
                UserDefault def = new UserDefault();
                UserDefaultKey key = new UserDefaultKey();
                key.setRoleCode(roleCode);
                key.setKey(Global.CUR_KEY);
                key.setValue(code);
                key.setCompCode(Global.compCode);
                def.setKey(key);
                userSettingService.save(def);
            }
            if (supplierAutoCompleter.getTrader() != null) {
                String code = supplierAutoCompleter.getTrader().getCode();
                UserDefault def = new UserDefault();
                UserDefaultKey key = new UserDefaultKey();
                key.setRoleCode(roleCode);
                key.setKey(Global.SUP_KEY);
                key.setValue(code);
                key.setCompCode(Global.compCode);
                def.setKey(key);
                userSettingService.save(def);
            }
            if (customerAutoCompleter.getTrader() != null) {
                String code = supplierAutoCompleter.getTrader().getCode();
                UserDefault def = new UserDefault();
                UserDefaultKey key = new UserDefaultKey();
                key.setRoleCode(roleCode);
                key.setKey(Global.CUS_KEY);
                key.setValue(code);
                key.setCompCode(Global.compCode);
                def.setKey(key);
                userSettingService.save(def);
            }
            //Cash Book Delete
            RoleStatusKey key = new RoleStatusKey(roleCode, Global.CB_DEL_KEY);
            RoleStatus status = new RoleStatus();
            status.setRoleKey(key);
            status.setStatus(chkCBDel.isSelected());
            statusService.save(status);
            RoleStatusKey key1 = new RoleStatusKey(roleCode, Global.CB_DEL_USR_KEY);
            RoleStatus status1 = new RoleStatus();
            status1.setRoleKey(key1);
            status1.setStatus(chkRCBDel.isSelected());
            statusService.save(status1);

            /*            if (vouStatusAutoCompleter.getVouStatus() != null) {
            String code = vouStatusAutoCompleter.getVouStatus().getVouStatusCode();
            UserDefault def = new UserDefault();
            UserDefaultKey key = new UserDefaultKey();
            key.setRoleCode(roleCode);
            key.setKey(Global.VOU_KEY);
            key.setValue(code);
            key.setCompCode(Global.compCode);
            def.setKey(key);
            userSettingService.save(def);
            }
            if (saleManAutoCompleter.getSaleMan() != null) {
            String code = saleManAutoCompleter.getSaleMan().getSaleManCode();
            UserDefault def = new UserDefault();
            UserDefaultKey key = new UserDefaultKey();
            key.setRoleCode(roleCode);
            key.setKey(Global.SALE_KEY);
            key.setValue(code);
            key.setCompCode(Global.compCode);
            def.setKey(key);
            userSettingService.save(def);
            }*/
            JOptionPane.showMessageDialog(Global.parentForm, "Saved.");
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Select User Account");
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
        txtDep = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCur = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtCus = new javax.swing.JTextField();
        txtSup = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        chkCBDel = new javax.swing.JCheckBox();
        chkRCBDel = new javax.swing.JCheckBox();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Department");

        txtDep.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

        txtCur.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Customer");

        txtCus.setFont(Global.textFont);

        txtSup.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Supplier");

        chkCBDel.setText("Cash Book Edit / Delete");

        chkRCBDel.setText("Cash Book Edit / Delete By User");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chkCBDel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkRCBDel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkCBDel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkRCBDel)
                .addContainerGap(137, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(txtSup, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtCur, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3, jLabel4, jLabel5});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtSup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkCBDel;
    private javax.swing.JCheckBox chkRCBDel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtCur;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtSup;
    // End of variables declaration//GEN-END:variables

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

    @Override
    public void refresh() {
    }

    @Override
    public void save() {
        saveDefault();
    }
}
