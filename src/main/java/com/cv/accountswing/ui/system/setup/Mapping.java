/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.CurrencyKey;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.UserDefault;
import com.cv.accountswing.entity.UserDefaultKey;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.service.UserSettingService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.DepartmentTableModel;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.cash.common.TraderTableModel;
import com.cv.accountswing.ui.editor.COACellEditor;
import com.cv.accountswing.ui.editor.CurrencyEditor;
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.ui.editor.TraderCellEditor;
import com.cv.accountswing.ui.system.setup.common.COARoleTableModel;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.SaleMan;
import com.cv.inv.entity.VouStatus;
import com.cv.inv.entry.common.CurrencyTableModel;
import com.cv.inv.entry.common.LocationTableModel;
import com.cv.inv.entry.editor.LocationCellEditor;
import com.cv.inv.entry.editor.SaleManCellEditor;
import com.cv.inv.entry.editor.VouStatusCellEditor;
import com.cv.inv.service.LocationService;
import com.cv.inv.service.SaleManService;
import com.cv.inv.service.VouStatusService;
import com.cv.inv.setup.dialog.common.SaleManTableModel;
import com.cv.inv.setup.dialog.common.VouStatusTableModel;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
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
public class Mapping extends javax.swing.JPanel implements PanelControl {

    private DepartmentTableModel departmentTableModel;
    private LocationTableModel locationTableModel;
    private CurrencyTableModel currencyTableModel;
    private TraderTableModel supplierTableModel;
    private TraderTableModel customerTableModel;
    private VouStatusTableModel vouStatusTableModel;
    private SaleManTableModel saleManTableModel;
    private COARoleTableModel coaTableModel;
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
    private ApplicationMainFrame mainFrame;
    @Autowired
    private TraderService traderService;
    @Autowired
    private VouStatusService vouStatusService;
    @Autowired
    private SaleManService saleManService;
    @Autowired
    private COAService cOAService;
    private static final Logger log = LoggerFactory.getLogger(Mapping.class);

    /**
     * Creates new form Mapping
     */
    public Mapping() {
        initComponents();
    }

    public void initMain() {
        initTableDep();
        initTableLocation();
        initTableCurrency();
        initTableCustomer();
        initTableSupplier();
        initTableVoucher();
        initTableSaleMan();
        initTableCOA();
    }

    private void initTableDep() {
        departmentTableModel = new DepartmentTableModel();
        departmentTableModel.setTable(tblDep);
        departmentTableModel.addNewRow();
        tblDep.setModel(departmentTableModel);
        tblDep.getColumnModel().getColumn(0).setCellEditor(new DepartmentCellEditor(false));
        tblDep.getColumnModel().getColumn(0).setCellEditor(new DepartmentCellEditor(false));
        tblDep.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblDep.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblDep.setDefaultRenderer(Object.class, new TableCellRender());
        tblDep.getTableHeader().setFont(Global.tblHeaderFont);
    }

    private void initTableLocation() {
        locationTableModel = new LocationTableModel();
        locationTableModel.addNewRow();
        locationTableModel.setTable(tblLocation);
        tblLocation.setModel(locationTableModel);
        tblLocation.getColumnModel().getColumn(0).setCellEditor(new LocationCellEditor());
        tblLocation.getColumnModel().getColumn(0).setCellEditor(new LocationCellEditor());
        tblLocation.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblLocation.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblLocation.setDefaultRenderer(Object.class, new TableCellRender());
        tblLocation.getTableHeader().setFont(Global.tblHeaderFont);
    }

    private void initTableCurrency() {
        currencyTableModel = new CurrencyTableModel();
        currencyTableModel.addNewRow();
        currencyTableModel.setTable(tblCurrency);
        tblCurrency.setModel(currencyTableModel);
        tblCurrency.getColumnModel().getColumn(0).setCellEditor(new CurrencyEditor());
        tblCurrency.getColumnModel().getColumn(0).setCellEditor(new CurrencyEditor());
        tblCurrency.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblCurrency.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblCurrency.setDefaultRenderer(Object.class, new TableCellRender());
        tblCurrency.getTableHeader().setFont(Global.tblHeaderFont);
    }

    private void initTableCustomer() {
        customerTableModel = new TraderTableModel();
        customerTableModel.addNewRow();
        customerTableModel.setTable(tblCustomer);
        tblCustomer.setModel(customerTableModel);
        tblCustomer.getColumnModel().getColumn(0).setCellEditor(new TraderCellEditor(false, 0));
        tblCustomer.getColumnModel().getColumn(0).setCellEditor(new TraderCellEditor(false, 0));
        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblCustomer.setDefaultRenderer(Object.class, new TableCellRender());
        tblCustomer.getTableHeader().setFont(Global.tblHeaderFont);

    }

    private void initTableSupplier() {
        supplierTableModel = new TraderTableModel();
        supplierTableModel.addNewRow();
        supplierTableModel.setTable(tblSupplier);
        tblSupplier.setModel(supplierTableModel);
        tblSupplier.getColumnModel().getColumn(0).setCellEditor(new TraderCellEditor(false, 0));
        tblSupplier.getColumnModel().getColumn(0).setCellEditor(new TraderCellEditor(false, 0));
        tblSupplier.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblSupplier.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblSupplier.setDefaultRenderer(Object.class, new TableCellRender());
        tblSupplier.getTableHeader().setFont(Global.tblHeaderFont);
    }

    private void initTableVoucher() {
        vouStatusTableModel = new VouStatusTableModel();
        vouStatusTableModel.addNewRow();
        tblVoucher.setModel(vouStatusTableModel);
        tblVoucher.getColumnModel().getColumn(0).setCellEditor(new VouStatusCellEditor());
        tblVoucher.getColumnModel().getColumn(0).setCellEditor(new VouStatusCellEditor());
        tblVoucher.setDefaultRenderer(Object.class, new TableCellRender());
        tblVoucher.getTableHeader().setFont(Global.tblHeaderFont);

    }

    private void initTableSaleMan() {
        saleManTableModel = new SaleManTableModel();
        saleManTableModel.addNewRow();
        saleManTableModel.setTable(tblSaleMan);
        tblSaleMan.setModel(saleManTableModel);
        tblSaleMan.getColumnModel().getColumn(0).setCellEditor(new SaleManCellEditor());
        tblSaleMan.getColumnModel().getColumn(0).setCellEditor(new SaleManCellEditor());
        tblSaleMan.setDefaultRenderer(Object.class, new TableCellRender());
        tblSaleMan.getTableHeader().setFont(Global.tblHeaderFont);
    }

    private void initTableCOA() {
        coaTableModel = new COARoleTableModel();
        coaTableModel.addNewRow();
        coaTableModel.setTable(tblCOA);
        tblCOA.setModel(coaTableModel);
        tblCOA.getColumnModel().getColumn(0).setCellEditor(new COACellEditor(false));
        tblCOA.getColumnModel().getColumn(0).setCellEditor(new COACellEditor(false));
        tblCOA.setDefaultRenderer(Object.class, new TableCellRender());
        tblCOA.getTableHeader().setFont(Global.tblHeaderFont);
    }

    public List<Department> getRoleDepartment(String roleCode) {
        List<Department> departments = new ArrayList<>();
        List<UserDefault> listDep = userSettingService.search(roleCode, Global.compCode, Global.DEP_LIST_KEY);
        if (!listDep.isEmpty()) {
            listDep.stream().map(ud -> departmentService.findById(ud.getKey().getValue())).filter(dep -> (dep != null)).forEachOrdered(dep -> {
                departments.add(dep);
            });
        }
        return departments;
    }

    public List<Location> getRoleLocaiton(String roleCode) {
        List<Location> locations = new ArrayList<>();
        List<UserDefault> listLoc = userSettingService.search(roleCode, Global.compCode, Global.LOC_LIST_KEY);
        if (!listLoc.isEmpty()) {
            listLoc.stream().map(ud -> locationService.findById(ud.getKey().getValue())).filter(loc -> (loc != null)).forEachOrdered(loc -> {
                locations.add(loc);
            });
        }
        return locations;
    }

    public List<Currency> getRoleCurrency(String roleCode) {
        List<Currency> currencys = new ArrayList<>();
        List<UserDefault> listCur = userSettingService.search(roleCode, Global.compCode, Global.CUR_LIST_KEY);
        if (!listCur.isEmpty()) {
            listCur.stream().filter(ud -> (ud.getKey().getValue() != null)).map(ud -> currencyService.findById(new CurrencyKey(ud.getKey().getValue(), Global.compCode))).forEachOrdered(curr -> {
                currencys.add(curr);
            });
        }
        return currencys;
    }

    public List<Trader> getRoleSupplier(String roleCode) {
        List<Trader> suppliers = new ArrayList<>();
        List<UserDefault> listSup = userSettingService.search(roleCode, Global.compCode, Global.SUP_LIST_KEY);
        if (!listSup.isEmpty()) {
            listSup.stream().filter(ud -> (ud.getKey().getValue() != null)).map(ud -> traderService.findById(ud.getKey().getValue())).forEachOrdered(trader -> {
                suppliers.add(trader);
            });
        }
        return suppliers;
    }

    public List<Trader> getRoleCustomer(String roleCode) {
        List<Trader> customers = new ArrayList<>();
        List<UserDefault> listCus = userSettingService.search(roleCode, Global.compCode, Global.CUS_LIST_KEY);
        if (!listCus.isEmpty()) {
            listCus.stream().filter(ud -> (ud.getKey().getValue() != null)).map(ud -> traderService.findById(ud.getKey().getValue())).forEachOrdered(trader -> {
                customers.add(trader);
            });
        }
        return customers;
    }

    public List<VouStatus> getRoleVoucherStatus(String roleCode) {
        List<VouStatus> vouchers = new ArrayList<>();
        List<UserDefault> listVOu = userSettingService.search(roleCode, Global.compCode, Global.VOU_LIST_KEY);
        if (!listVOu.isEmpty()) {
            listVOu.stream().filter(ud -> (ud.getKey() != null)).map(ud -> vouStatusService.findById(ud.getKey().getValue())).filter(vs -> (vs != null)).forEachOrdered(vs -> {
                vouchers.add(vs);
            });
        }
        return vouchers;
    }

    public List<SaleMan> getRoleSaleMan(String roleCode) {
        List<SaleMan> saleMen = new ArrayList<>();
        List<UserDefault> listSM = userSettingService.search(roleCode, Global.compCode, Global.SALE_LIST_KEY);
        if (!listSM.isEmpty()) {
            listSM.stream().filter(ud -> (ud.getKey() != null)).map(ud -> saleManService.findById(ud.getKey().getValue())).filter(sm -> (sm != null)).forEachOrdered(sm -> {
                saleMen.add(sm);
            });
        }
        return saleMen;
    }

    public List<ChartOfAccount> getRoleCOA(String roleCode) {
        List<ChartOfAccount> coas = new ArrayList<>();
        List<UserDefault> listSM = userSettingService.search(roleCode, Global.compCode, Global.COA_LIST_KEY);
        if (!listSM.isEmpty()) {
            listSM.stream().filter(ud -> (ud.getKey() != null)).map(ud -> cOAService.findById(ud.getKey().getValue())).filter(sm -> (sm != null)).forEachOrdered(sm -> {
                coas.add(sm);
            });
        }
        return coas;
    }

    public void loadDefaultData(String roleCode) {
        log.info("loading mapping data.");
        if (roleCode != null) {
            //Department
            departmentTableModel.setListDep(getRoleDepartment(roleCode));
            departmentTableModel.addNewRow();
            //Location
            locationTableModel.setListLocation(getRoleLocaiton(roleCode));
            locationTableModel.addNewRow();
            //Currency
            currencyTableModel.setListCurrency(getRoleCurrency(roleCode));
            currencyTableModel.addNewRow();
            //Supplier
            supplierTableModel.setListTrader(getRoleSupplier(roleCode));
            supplierTableModel.addNewRow();

            //Customer
            customerTableModel.setListTrader(getRoleCustomer(roleCode));
            customerTableModel.addNewRow();
            //Voucher Status
            vouStatusTableModel.setListVou(getRoleVoucherStatus(roleCode));
            vouStatusTableModel.addNewRow();
            //Sale Man
            saleManTableModel.setListSaleMan(getRoleSaleMan(roleCode));
            saleManTableModel.addNewRow();
            //coa
            coaTableModel.setListCOA(getRoleCOA(roleCode));
            coaTableModel.addNewRow();

        }
        log.info("end loading mapping data.");
    }

    private void saveMapping() {
        String roleCode = userSetting.getRoleCode();
        if (roleCode != null) {
            //Department
            List<Department> listDep = departmentTableModel.getListDep();
            if (!listDep.isEmpty()) {
                userSettingService.delete(roleCode, Global.compCode, Global.DEP_LIST_KEY);
                listDep.stream().filter(dep -> (dep.getDeptCode() != null)).map(dep -> {
                    UserDefault ud = new UserDefault();
                    UserDefaultKey key = new UserDefaultKey();
                    key.setCompCode(Global.compCode);
                    key.setKey(Global.DEP_LIST_KEY);
                    key.setRoleCode(roleCode);
                    key.setValue(dep.getDeptCode());
                    ud.setKey(key);
                    return ud;
                }).forEachOrdered(ud -> {
                    userSettingService.save(ud);
                });

            }
            //Location
            List<Location> listLoc = locationTableModel.getListLocation();
            if (!listLoc.isEmpty()) {
                userSettingService.delete(roleCode, Global.compCode, Global.LOC_LIST_KEY);
                listLoc.stream().filter(loc -> (loc.getLocationCode() != null)).map(loc -> {
                    UserDefault ud = new UserDefault();
                    UserDefaultKey key = new UserDefaultKey();
                    key.setCompCode(Global.compCode);
                    key.setKey(Global.LOC_LIST_KEY);
                    key.setRoleCode(roleCode);
                    key.setValue(loc.getLocationCode());
                    ud.setKey(key);
                    return ud;
                }).forEachOrdered(ud -> {
                    userSettingService.save(ud);
                });

            }
            //Currency
            List<Currency> listCur = currencyTableModel.getListCurrency();
            if (!listCur.isEmpty()) {
                userSettingService.delete(roleCode, Global.compCode, Global.CUR_LIST_KEY);
                listCur.stream().filter(cur -> (cur.getKey() != null)).filter(cur -> (cur.getKey().getCode() != null)).map(cur -> {
                    UserDefault ud = new UserDefault();
                    UserDefaultKey key = new UserDefaultKey();
                    key.setCompCode(Global.compCode);
                    key.setKey(Global.CUR_LIST_KEY);
                    key.setRoleCode(roleCode);
                    key.setValue(cur.getKey().getCode());
                    ud.setKey(key);
                    return ud;
                }).forEachOrdered(ud -> {
                    userSettingService.save(ud);
                });
            }
            //Supplier
            List<Trader> listSup = supplierTableModel.getListTrader();
            if (!listSup.isEmpty()) {
                userSettingService.delete(roleCode, Global.compCode, Global.SUP_LIST_KEY);
                listSup.stream().filter(sup -> (sup.getCode() != null)).filter(sup -> (sup.getCode() != null)).map(sup -> {
                    UserDefault ud = new UserDefault();
                    UserDefaultKey key = new UserDefaultKey();
                    key.setCompCode(Global.compCode);
                    key.setKey(Global.SUP_LIST_KEY);
                    key.setRoleCode(roleCode);
                    key.setValue(sup.getCode());
                    ud.setKey(key);
                    return ud;
                }).forEachOrdered(ud -> {
                    userSettingService.save(ud);
                });
            }
            //Customer
            List<Trader> listCus = customerTableModel.getListTrader();
            if (!listCus.isEmpty()) {
                userSettingService.delete(roleCode, Global.compCode, Global.CUS_LIST_KEY);
                listCus.stream().filter(sup -> (sup.getCode() != null)).filter(sup -> (sup.getCode() != null)).map(sup -> {
                    UserDefault ud = new UserDefault();
                    UserDefaultKey key = new UserDefaultKey();
                    key.setCompCode(Global.compCode);
                    key.setKey(Global.CUS_LIST_KEY);
                    key.setRoleCode(roleCode);
                    key.setValue(sup.getCode());
                    ud.setKey(key);
                    return ud;
                }).forEachOrdered(ud -> {
                    userSettingService.save(ud);
                });
            }
            /* //Voucher Status
            List<VouStatus> listVou = vouStatusTableModel.getListVou();
            if (!listVou.isEmpty()) {
            userSettingService.delete(roleCode, Global.compCode, Global.VOU_LIST_KEY);
            listVou.stream().filter(vou -> (vou.getVouStatusCode() != null)).map(vou -> {
            UserDefault ud = new UserDefault();
            UserDefaultKey key = new UserDefaultKey();
            key.setCompCode(Global.compCode);
            key.setKey(Global.VOU_LIST_KEY);
            key.setRoleCode(roleCode);
            key.setValue(vou.getVouStatusCode());
            ud.setKey(key);
            return ud;
            }).forEachOrdered(ud -> {
            userSettingService.save(ud);
            });
            }
            //Sale Man
            List<SaleMan> listSaleM = saleManTableModel.getListSaleMan();
            if (!listSaleM.isEmpty()) {
            userSettingService.delete(roleCode, Global.compCode, Global.SALE_LIST_KEY);
            listSaleM.stream().filter(sale -> (sale.getSaleManCode() != null)).map(sale -> {
            UserDefault ud = new UserDefault();
            UserDefaultKey key = new UserDefaultKey();
            key.setCompCode(Global.compCode);
            key.setKey(Global.SALE_LIST_KEY);
            key.setRoleCode(roleCode);
            key.setValue(sale.getSaleManCode());
            ud.setKey(key);
            return ud;
            }).forEachOrdered(ud -> {
            userSettingService.save(ud);
            });
            }*/
            //COA
            List<ChartOfAccount> listCOA = coaTableModel.getListCOA();
            if (!listCOA.isEmpty()) {
                userSettingService.delete(roleCode, Global.compCode, Global.COA_LIST_KEY);
                listCOA.stream().filter(coa -> (coa.getCode() != null)).map(sale -> {
                    UserDefault ud = new UserDefault();
                    UserDefaultKey key = new UserDefaultKey();
                    key.setCompCode(Global.compCode);
                    key.setKey(Global.COA_LIST_KEY);
                    key.setRoleCode(roleCode);
                    key.setValue(sale.getCode());
                    ud.setKey(key);
                    return ud;
                }).forEachOrdered(ud -> {
                    userSettingService.save(ud);
                });
            }
            JOptionPane.showMessageDialog(Global.parentForm, "Saved Mapping");
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDep = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSupplier = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblCurrency = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblLocation = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblVoucher = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblSaleMan = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblCOA = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Department", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tblDep.setFont(Global.textFont);
        tblDep.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblDep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblDepKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblDep);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Supplier", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tblSupplier.setFont(Global.textFont);
        tblSupplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblSupplierKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblSupplier);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Customer", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tblCustomer.setFont(Global.textFont);
        tblCustomer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCustomerKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblCustomer);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Currency", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tblCurrency.setFont(Global.textFont);
        tblCurrency.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblCurrency.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCurrencyKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(tblCurrency);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Location", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tblLocation.setFont(Global.textFont);
        tblLocation.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblLocationKeyReleased(evt);
            }
        });
        jScrollPane5.setViewportView(tblLocation);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addGap(7, 7, 7))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Voucher Status", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tblVoucher.setFont(Global.textFont);
        tblVoucher.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblVoucher.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblVoucherKeyReleased(evt);
            }
        });
        jScrollPane6.setViewportView(tblVoucher);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sale Man", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tblSaleMan.setFont(Global.textFont);
        tblSaleMan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblSaleMan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblSaleManKeyReleased(evt);
            }
        });
        jScrollPane7.setViewportView(tblSaleMan);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chart Of Account", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        tblCOA.setFont(Global.textFont);
        tblCOA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblCOA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCOAKeyReleased(evt);
            }
        });
        jScrollPane8.setViewportView(tblCOA);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addGap(7, 7, 7))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
    }//GEN-LAST:event_formComponentShown

    private void tblDepKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblDepKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (tblDep.getSelectedRow() >= 0) {
                int row = tblDep.convertRowIndexToModel(tblDep.getSelectedRow());
                departmentTableModel.delete(row);
            }
        }
    }//GEN-LAST:event_tblDepKeyReleased

    private void tblSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSupplierKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (tblSupplier.getSelectedRow() >= 0) {
                int row = tblSupplier.convertRowIndexToModel(tblSupplier.getSelectedRow());
                supplierTableModel.delete(row);
            }
        }
    }//GEN-LAST:event_tblSupplierKeyReleased

    private void tblCustomerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCustomerKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (tblCustomer.getSelectedRow() >= 0) {
                int row = tblCustomer.convertRowIndexToModel(tblCustomer.getSelectedRow());
                customerTableModel.delete(row);
            }
        }
    }//GEN-LAST:event_tblCustomerKeyReleased

    private void tblCurrencyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCurrencyKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (tblCurrency.getSelectedRow() >= 0) {
                int row = tblCurrency.convertRowIndexToModel(tblCurrency.getSelectedRow());
                currencyTableModel.delete(row);
            }
        }
    }//GEN-LAST:event_tblCurrencyKeyReleased

    private void tblLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblLocationKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (tblLocation.getSelectedRow() >= 0) {
                int row = tblLocation.convertRowIndexToModel(tblLocation.getSelectedRow());
                locationTableModel.delete(row);
            }
        }
    }//GEN-LAST:event_tblLocationKeyReleased

    private void tblVoucherKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVoucherKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (tblVoucher.getSelectedRow() >= 0) {
                int row = tblVoucher.convertRowIndexToModel(tblVoucher.getSelectedRow());
                vouStatusTableModel.delete(row);
            }
        }
    }//GEN-LAST:event_tblVoucherKeyReleased

    private void tblSaleManKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSaleManKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (tblSaleMan.getSelectedRow() >= 0) {
                int row = tblSaleMan.convertRowIndexToModel(tblSaleMan.getSelectedRow());
                saleManTableModel.delete(row);
            }
        }
    }//GEN-LAST:event_tblSaleManKeyReleased

    private void tblCOAKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCOAKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (tblCOA.getSelectedRow() >= 0) {
                int row = tblCOA.convertRowIndexToModel(tblCOA.getSelectedRow());
                coaTableModel.delete(row);
            }
        }
    }//GEN-LAST:event_tblCOAKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTable tblCOA;
    private javax.swing.JTable tblCurrency;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTable tblDep;
    private javax.swing.JTable tblLocation;
    private javax.swing.JTable tblSaleMan;
    private javax.swing.JTable tblSupplier;
    private javax.swing.JTable tblVoucher;
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
        saveMapping();
    }
}
