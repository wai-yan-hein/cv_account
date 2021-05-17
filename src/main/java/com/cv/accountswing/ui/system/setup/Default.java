/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.CurrencyKey;
import com.cv.accountswing.entity.Customer;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Supplier;
import com.cv.accountswing.entity.UserDefault;
import com.cv.accountswing.entity.UserDefaultKey;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.CustomerService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.SupplierService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.service.UserSettingService;

import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.inv.entity.Location;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.SaleManAutoCompleter;
import com.cv.inv.entry.editor.VouStatusAutoCompleter;
import com.cv.inv.service.LocationService;
import com.cv.inv.service.SaleManService;
import com.cv.inv.service.VouStatusService;
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
public class Default extends javax.swing.JPanel {

    private static final Logger log = LoggerFactory.getLogger(Default.class);
    @Autowired
    private UserSettingService userSettingService;
    @Autowired
    private RoleSetting userSetting;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private TraderService traderServoce;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private VouStatusService vouStatusService;
    @Autowired
    private SaleManService saleManService;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;
    private TraderAutoCompleter supplierAutoCompleter;
    private TraderAutoCompleter customerAutoCompleter;
    private VouStatusAutoCompleter vouStatusAutoCompleter;
    private SaleManAutoCompleter saleManAutoCompleter;

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
        locationAutoCompleter = new LocationAutoCompleter(txtLoc, Global.listLocation, null);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCur, Global.listCurrency, null);
        supplierAutoCompleter = new TraderAutoCompleter(txtSup, Global.listTrader, null, false, 0);
        customerAutoCompleter = new TraderAutoCompleter(txtCus, Global.listTrader, null, false, 0);
        //vouStatusAutoCompleter = new VouStatusAutoCompleter(txtVou, Global.listVou, null);
        //saleManAutoCompleter = new SaleManAutoCompleter(txtSaleMan, Global.listSaleMan, null);
    }

    public Department getDepartment(String roleCode) {
        Department dep = null;
        List<UserDefault> listDep = userSettingService.search(roleCode, Global.compCode, Global.DEP_KEY);
        if (!listDep.isEmpty()) {
            dep = departmentService.findById(listDep.get(0).getKey().getValue());
        }
        return dep;
    }

    public Location getLocation(String roleCode) {
        Location loc = null;
        List<UserDefault> listLoc = userSettingService.search(roleCode, Global.compCode, Global.LOC_KEY);
        if (!listLoc.isEmpty()) {
            loc = locationService.findById(listLoc.get(0).getKey().getValue());
        }
        return loc;
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
            locationAutoCompleter.setLocation(getLocation(roleCode));
            currencyAutoCompleter.setCurrency(getCurrency(roleCode));
            supplierAutoCompleter.setTrader(getSuppplier(roleCode));
            customerAutoCompleter.setTrader(getCutomer(roleCode));
            /*List<UserDefault> listVou = userSettingService.search(roleCode, Global.compCode, Global.VOU_KEY);
            if (!listVou.isEmpty()) {
            vouStatusAutoCompleter.setVouStatus(vouStatusService.findById(listVou.get(0).getKey().getValue()));
            } else {
            vouStatusAutoCompleter.setVouStatus(null);
            }
            List<UserDefault> listSaleMan = userSettingService.search(roleCode, Global.compCode, Global.SALE_KEY);
            if (!listSaleMan.isEmpty()) {
            saleManAutoCompleter.setSaleMan(saleManService.findById(listSaleMan.get(0).getKey().getValue()));
            } else {
            saleManAutoCompleter.setSaleMan(null);
            }*/
        }
    }

    private void save() {
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
            if (locationAutoCompleter.getLocation() != null) {
                String defLoc = locationAutoCompleter.getLocation().getLocationCode();
                UserDefault def = new UserDefault();
                UserDefaultKey key = new UserDefaultKey();
                key.setRoleCode(roleCode);
                key.setKey(Global.LOC_KEY);
                key.setValue(defLoc);
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
            JOptionPane.showMessageDialog(Global.parentForm, "Saved  Default Setting.");
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
        jLabel2 = new javax.swing.JLabel();
        txtLoc = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCur = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtCus = new javax.swing.JTextField();
        txtSup = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtVou = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtSaleMan = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Department");

        txtDep.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Location");

        txtLoc.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

        txtCur.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Customer");

        txtCus.setFont(Global.textFont);

        txtSup.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Supplier");

        txtVou.setEditable(false);
        txtVou.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Voucher Status");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Sale Man");

        txtSaleMan.setEditable(false);
        txtSaleMan.setFont(Global.textFont);

        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(18, 18, 18)
                            .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addGap(18, 18, 18)
                            .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addComponent(jLabel2))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtLoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtCur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addGap(18, 18, 18)
                            .addComponent(txtSup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addGap(18, 18, 18)
                            .addComponent(txtVou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addGap(18, 18, 18)
                            .addComponent(txtSaleMan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCur, txtCus, txtDep, txtLoc, txtSaleMan, txtSup, txtVou});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtLoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtSup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtVou, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtSaleMan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        save();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField txtCur;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtLoc;
    private javax.swing.JTextField txtSaleMan;
    private javax.swing.JTextField txtSup;
    private javax.swing.JTextField txtVou;
    // End of variables declaration//GEN-END:variables
}
