/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui;

import com.cv.accountswing.AccountSwingApplication;
import com.cv.accountswing.common.FilterObserver;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.NetworkDetector;
import com.cv.accountswing.common.NetworkObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.ReloadData;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.SystemProperty;
import com.cv.accountswing.entity.view.VRoleMenu;
import com.cv.accountswing.entity.view.VUsrCompAssign;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.CustomerService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.MenuService;
import com.cv.accountswing.service.RegionService;
import com.cv.accountswing.service.SupplierService;
import com.cv.accountswing.service.SystemPropertyService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.service.UserService;
import com.cv.accountswing.service.UsrCompRoleService;
import com.cv.accountswing.ui.cash.AllCash;
import com.cv.accountswing.ui.cash.SalePurchaseBook;
import com.cv.accountswing.ui.journal.CrDrVoucher;
import com.cv.accountswing.ui.journal.Journal;
import com.cv.accountswing.ui.journal.JournalStockOpening;
import com.cv.accountswing.ui.report.AparGlReport;
import com.cv.accountswing.ui.report.BalanceSheet;
import com.cv.accountswing.ui.report.ProtfitAndLost;
import com.cv.accountswing.ui.setup.COAOpeningSetup;
import com.cv.accountswing.ui.setup.ChartOfAccountSetup;
import com.cv.accountswing.ui.setup.COASetup;
import com.cv.accountswing.ui.setup.CurrencySetup;
import com.cv.accountswing.ui.setup.CustomerSetup;
import com.cv.accountswing.ui.setup.DepartmentSetup;
import com.cv.accountswing.ui.setup.ManageProjectSetup;
import com.cv.accountswing.ui.setup.RegionSetup;
import com.cv.accountswing.ui.system.setup.RoleAssignSetup;
import com.cv.accountswing.ui.setup.StaffSetup;
import com.cv.accountswing.ui.setup.SupplierSetup;
import com.cv.accountswing.ui.system.setup.Company;
import com.cv.accountswing.ui.system.setup.Default;
import com.cv.accountswing.ui.system.setup.Mapping;
import com.cv.accountswing.ui.system.setup.MenuSetup;
import com.cv.accountswing.ui.system.setup.RoleSetup;
import com.cv.accountswing.ui.system.setup.SystemPropertySetup;
import com.cv.accountswing.ui.system.setup.RoleSetting;
import com.cv.accountswing.ui.system.setup.UserSetup;
import com.cv.accountswing.util.Util1;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
@Component
public class ApplicationMainFrame extends javax.swing.JFrame implements ReloadData,
        SelectionObserver, LoadingObserver, KeyListener, NetworkObserver {

    private final ConfigurableApplicationContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMainFrame.class);
    private final HashMap<String, JLabel> hmTabLoading = new HashMap();
    private final HashMap<String, JPanel> hmPanel = new HashMap<>();
    private JPopupMenu popupmenu;
    private final ImageIcon onlineIcon = new ImageIcon(this.getClass().getResource("/images/signal_20px.png"));
    private final ImageIcon offlineIcon = new ImageIcon(this.getClass().getResource("/images/no_connection_20px.png"));
    private final ImageIcon lowIcon = new ImageIcon(this.getClass().getResource("/images/low_connection_20px.png"));
    private final ImageIcon loadingIcon = new ImageIcon(this.getClass().getResource("/images/dual-loading.gif"));
    private final ImageIcon companyIcon = new ImageIcon(this.getClass().getResource("/images/museum_50px.png"));
    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String className = actionMenu.getName();
        String menuName = actionMenu.getText();
        JPanel panel = getPanel(className);
        addTabMain(panel, menuName);
    };
    /*@Autowired
    private CareServerSender sender;
    @Autowired
    private CareServerListener listener;*/
    @Value("${ping.server.ip}")
    private String pingServer;
    @Autowired
    private MenuService menuService;

    @Autowired
    private CustomerSetup customerSetup;
    @Autowired
    private CurrencySetup currencySetup;
    @Autowired
    private SupplierSetup supplierSetup;
    @Autowired
    private RoleAssignSetup roleAssignSetup;
    @Autowired
    private DepartmentSetup departmentSetup;
    @Autowired
    private RegionSetup regionSetup;
    @Autowired
    private ChartOfAccountSetup chartOfAccountSetup;
    @Autowired
    private ManageProjectSetup manageProjectSetup;
    @Autowired
    private COASetup coaSetup;
    @Autowired
    private AllCash allCash;
    @Autowired
    private Journal journal;
    @Autowired
    private MenuSetup menuSetup;

    @Autowired
    private SalePurchaseBook saleBook;
    @Autowired
    private SalePurchaseBook purchaseBook;
    @Autowired
    private Company company;
    @Autowired
    private UserSetup user;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private SystemPropertySetup systemPropertySetup;
    @Autowired
    private JournalStockOpening journalStockOpening;
    @Autowired
    private CrDrVoucher crdrVoucher;
    @Autowired
    private CrDrVoucher debitVoucher;
    @Autowired
    private AparGlReport aPARReport;
    @Autowired
    private AparGlReport glListingReport;
    @Autowired
    private UserService userService;
    @Autowired
    private COAService cOAService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private ProtfitAndLost profitAndLost;
    @Autowired
    private BalanceSheet balanceSheet;
    @Autowired
    private RoleSetup roleSetup;
    @Autowired
    private COAOpeningSetup cOAOpeningSetup;
    @Autowired
    private Default defaultMap;
    @Autowired
    private RoleSetting roleSetting;
    @Autowired
    private RegionService regionService;
    @Autowired
    private StaffSetup staffSetup;
    @Autowired
    private Mapping mapping;
    @Autowired 
    private SystemPropertyService systemPropertyService;
    private PanelControl control;
    private FilterObserver filterObserver;

    public void setFilterObserver(FilterObserver filterObserver) {
        this.filterObserver = filterObserver;
    }

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    public PanelControl getControl() {
        return control;
    }

    public void setControl(PanelControl control) {
        this.control = control;
    }

    /**
     * Creates new form ApplicationMainFrame
     *
     * @param context
     */
    public ApplicationMainFrame(ConfigurableApplicationContext context) {
        //System.setProperty("java.awt.headless", "false");
        initComponents();
        initKeyFoucsManager();
        initToolBar();
        initPopup();
        showCloseAllPopup();
        this.context = context;
    }

    private void initToolBar() {
        toolBar.addSeparator();
        toolBar.setBorderPainted(true);

    }

    @Override
    public void setIconImage(Image image) {
        ImageIcon size = new ImageIcon(getClass().getResource("/images/logo.png"));
        super.setIconImage(size.getImage()); //To change body of generated methods, choose Tools | Templates.
    }

    private JPanel getPanel(String className) {
        String[] split = className.split(",");
        String cName = split[0]; // group name
        String sourceId = split[1];
        String menuName = split[2];
        switch (cName) {
            case "Setup":
                switch (menuName) {
                    case "Account Group":
                        chartOfAccountSetup.setName(menuName);
                        chartOfAccountSetup.setLoadingObserver(this);
                        return chartOfAccountSetup;
                    case "Department":
                        departmentSetup.setName(menuName);
                        departmentSetup.setLoadingObserver(this);
                        return departmentSetup;
                    case "Region":
                        regionSetup.setName(menuName);
                        regionSetup.setLoadingObserver(this);
                        return regionSetup;
                    case "Customer":
                        customerSetup.setName(menuName);
                        customerSetup.setLoadingObserver(this);
                        return customerSetup;
                    case "Staff And Others":
                        staffSetup.setName(menuName);
                        staffSetup.setLoadingObserver(this);
                        return staffSetup;
                    case "Currency":
                        currencySetup.setName(menuName);
                        currencySetup.setLoadingObserver(this);
                        return currencySetup;
                    case "Supplier":
                        supplierSetup.setName(menuName);
                        supplierSetup.setLoadingObserver(this);
                        return supplierSetup;
                    case "COA":
                        coaSetup.setName(menuName);
                        coaSetup.setLoadingObserver(this);
                        return coaSetup;
                    case "Opening Balance":
                        cOAOpeningSetup.setName(menuName);
                        cOAOpeningSetup.setLoadingObserver(this);
                        return cOAOpeningSetup;
                    default:
                        return null;
                }
            case "Bank":
                JPanel panelBank = hmPanel.get(menuName);
                if (panelBank == null) {
                    //dynamic bank
                    AllCash bank = allCash.newInstance();
                    bank.setName(menuName);
                    bank.setLoadingObserver(this);
                    bank.setReloadData(this);
                    bank.setSourceAccId(sourceId);
                    panelBank = bank;
                    hmPanel.put(menuName, bank);
                }

                return panelBank;
            case "AllCash":
                JPanel panelCash = hmPanel.get(menuName);
                if (panelCash == null) {
                    AllCash cash = allCash.newInstance();
                    cash.setName(menuName);
                    cash.setLoadingObserver(this);
                    cash.setReloadData(this);
                    cash.setSourceAccId(sourceId);
                    panelCash = cash;
                    hmPanel.put(menuName, cash);
                }
                //dynamic cash

                return panelCash;
            case "DayBook":
                switch (menuName) {
                    case "Purchase":
                        String sourcePur = sourceId;
                        if (!sourcePur.equals("-")) {
                            purchaseBook.setName(menuName);
                            purchaseBook.setLoadingObserver(this);
                            purchaseBook.setSourceAccId(sourcePur);
                            return purchaseBook;
                        } else {
                            return null;
                        }
                    case "Sale":
                        String sourceAcc = sourceId;
                        if (!sourceAcc.equals("-")) {
                            saleBook.setName(menuName);
                            saleBook.setLoadingObserver(this);
                            saleBook.setSourceAccId(sourceAcc);
                            return saleBook;
                        } else {
                            return null;
                        }
                    default:
                        return null;
                }
            case "Journal":
                switch (menuName) {
                    case "Journal":
                        journal.setName(menuName);
                        journal.setLoadingObserver(this);
                        return journal;
                    case "Journal Stock Opening":
                        journalStockOpening.setName(menuName);
                        journalStockOpening.setLoadingObserver(this);
                        //journalStockOpening.initTable();
                        return journalStockOpening;
                    case "Credit Voucher":
                        crdrVoucher.setSplitId("8");
                        crdrVoucher.setSourceAcId(Global.sysProperties.get("system.creditvoucher"));
                        crdrVoucher.setName(menuName);
                        crdrVoucher.setLoadingObserver(this);
                        return crdrVoucher;
                    case "Debit Voucher":
                        debitVoucher.setSplitId("9");
                        debitVoucher.setSourceAcId(Global.sysProperties.get("system.creditvoucher"));
                        debitVoucher.setName(menuName);
                        debitVoucher.setLoadingObserver(this);
                        return debitVoucher;
                    default:
                        return null;
                }
            case "System":
                switch (menuName) {
                    case "Menu":
                        menuSetup.setName(menuName);
                        menuSetup.setLoadingObserver(this);
                        return menuSetup;
                    case "System Property":
                        systemPropertySetup.setName(menuName);
                        systemPropertySetup.setLoadingObserver(this);
                        return systemPropertySetup;
                    case "Company":
                        company.setName(menuName);
                        company.setLoadingObserver(this);
                        return company;
                    case "User Setup":
                        user.setName(menuName);
                        user.setLoadingObserver(this);
                        return user;
                    case "Role Setup":
                        roleSetup.setName(menuName);
                        return roleSetup;
                    case "Role Setting":
                        roleSetting.setName(menuName);
                        return roleSetting;
                    case "Role Assign":
                        roleAssignSetup.setName(menuName);
                        roleAssignSetup.setLoadingObserver(this);
                        return roleAssignSetup;
                    default:
                        return null;

                }
            case "Report":
                switch (menuName) {
                    case "AR/AP":
                        aPARReport.setName(menuName);
                        aPARReport.setLoadingObserver(this);
                        return aPARReport;
                    case "G/L Listing":
                        glListingReport.setName(menuName);
                        glListingReport.setLoadingObserver(this);
                        return glListingReport;
                    case "Profit & Lost":
                        profitAndLost.setName(menuName);
                        profitAndLost.setLoadingObserver(this);
                        return profitAndLost;
                    case "Balance Sheet":
                        balanceSheet.setName(menuName);
                        balanceSheet.setLoadingObserver(this);
                        return balanceSheet;
                    case "Stock Report":
                        //stockReports.setName(menuName);
                        //return stockReports;
                    default:
                        return null;
                }

            default:
                return null;
        }
    }

    private void initKeyFoucsManager() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((KeyEvent ke) -> {
            switch (ke.getID()) {
                case KeyEvent.KEY_PRESSED:
                    if (control != null) {
                        switch (ke.getKeyCode()) {
                            case KeyEvent.VK_F5:
                                control.save();
                                break;
                            case KeyEvent.VK_F6:
                                control.print();
                                break;
                            case KeyEvent.VK_F7:
                                control.refresh();
                                break;
                            case KeyEvent.VK_F8:
                                control.delete();
                                break;
                            case KeyEvent.VK_F9:
                                control.history();
                                break;
                            case KeyEvent.VK_F10:
                                control.newForm();
                                break;
                        }
                        break;
                    }
            }
            return false;
        });
    }

    private void addTabMain(JPanel panel, String menuName) {
        if (panel != null) {
            tabMain.add(panel);
            tabMain.setTabComponentAt(tabMain.indexOfComponent(panel), setTitlePanel(tabMain, panel, menuName));
            tabMain.setSelectedComponent(panel);
        }
    }

    private void showCloseAllPopup() {
        tabMain.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupmenu.show(tabMain, e.getX(), e.getY());
                }
            }

        });
    }

    private JPanel setTitlePanel(final JTabbedPane tabbedPane, final JPanel panel, String title) {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        //loading
        JLabel loading = new JLabel(loadingIcon);
        titlePanel.add(loading);
        loading.setVisible(false);
        hmTabLoading.put(panel.getName(), loading);

        // title button
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(Global.menuFont);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        titlePanel.add(titleLbl);

        // close button
        JLabel closeButton = new JLabel("x", SwingConstants.RIGHT);
        closeButton.setFont(Global.menuFont);
        closeButton.setToolTipText("Click to close");
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabbedPane.remove(panel);
                if (control != null) {
                    control.newForm();
                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.BLACK);
            }

        });

        titlePanel.setName(title);
        titlePanel.add(closeButton);
        return titlePanel;
    }

    private void initPopup() {
        popupmenu = new JPopupMenu("Edit");
        JMenuItem closeAll = new JMenuItem("Close All");
        JMenuItem clearData = new JMenuItem("Clear");
        JMenuItem printReport = new JMenuItem("Print");
        closeAll.addActionListener((ActionEvent e) -> {
            tabMain.removeAll();
        });
        clearData.addActionListener((ActionEvent e) -> {

        });
        printReport.addActionListener((ActionEvent e) -> {

        });
        popupmenu.add(printReport);
        popupmenu.add(clearData);
        popupmenu.add(closeAll);
    }

    public void loadSysProperties() {
        try {
            List<SystemProperty> listSys = systemPropertyService.search("-", Global.compCode, "-");
            HashMap<String, String> hmList = new HashMap<>();
            listSys.forEach(sys -> {
                hmList.put(sys.getKey().getPropKey(), sys.getPropValue());
            });
            Global.sysProperties = hmList;
        } catch (Exception e) {
            LOGGER.error("Connection Timeout :" + e.getMessage());
            System.exit(1);
        }
    }

    public void companyUserRoleAssign() {
        UsrCompRoleService usrCompRoleService = context.getBean(UsrCompRoleService.class);
        List<VUsrCompAssign> listVUCA = usrCompRoleService.
                getAssignCompany(Global.loginUser.getAppUserCode());

        if (listVUCA == null) {
            JOptionPane.showMessageDialog(new JFrame(),
                    "No company assign to the user",
                    "Invalid Compay Access", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } else if (listVUCA.isEmpty()) {
            JOptionPane.showMessageDialog(new JFrame(),
                    "No company assign to the user",
                    "Invalid Compay Access", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } else if (listVUCA.size() > 1) {
            CompanyDialog companyDialog = new CompanyDialog();
            companyDialog.setIconImage(companyIcon.getImage());
            companyDialog.setListCompany(listVUCA);
            companyDialog.initTable();
            companyDialog.setLocationRelativeTo(null);
            companyDialog.setVisible(true);
        } else {
            VUsrCompAssign vuca = listVUCA.get(0);
            Global.roleCode = vuca.getKey().getRoleCode();
            Global.compCode = vuca.getKey().getCompCode();
            Global.companyName = vuca.getCompName();
            Global.finicialPeriodFrom = Util1.toDateStr(vuca.getFinicialPeriodFrom(), "yyyy-MM-dd");
            Global.finicialPeriodTo = Util1.toDateStr(vuca.getFinicialPeriodTo(), "yyyy-MM-dd");
            LOGGER.info("Role Id : " + Global.roleCode);
            LOGGER.info("Company Id : " + Global.compCode);
        }
    }

    private void initializeData() {
        this.setTitle(this.getTitle() + "(" + Global.loginUser.getUserName() + ")");
        lblCompanyName.setText(Global.companyName);
        try {

            Global.listAppUser = userService.search("-", "-", "-", "-");
            //coa
            List<ChartOfAccount> roleCOA = mapping.getRoleCOA(Global.roleCode);
            Global.listCOA = roleCOA.isEmpty() ? cOAService.search("-", "-", Global.compCode, "3", "-", "-", "-") : roleCOA;
            //currency
            List<Currency> roleCurrency = mapping.getRoleCurrency(Global.roleCode);
            Global.listCurrency = roleCurrency.isEmpty() ? currencyService.search("-", "-", Global.compCode) : roleCurrency;
            //department
            List<Department> roleDepartment = mapping.getRoleDepartment(Global.roleCode);
            Global.listDepartment = roleDepartment.isEmpty() ? departmentService.search("-", "-", Global.compCode,
                    "-", "-") : roleDepartment;
            //
            Global.listCustomer = customerService.search("-", "-", "-", "-", Global.compCode);
            Global.listSupplier = supplierService.search("-", "-", "-", "-", Global.compCode);
            Global.listTrader = traderService.searchTrader("-", "-", "-", "-", "-", Global.compCode, "-");

            Global.listRegion = regionService.search("-", "-", Global.compCode, "-");

            /*if (Global.listRelation != null) {
            Global.listRelation.forEach(ur -> {
            Global.hmRelation.put(ur.getUnitKey(), ur.getFactor());
            });
            }*/
            Global.defalutCurrency = defaultMap.getCurrency(Global.roleCode);
            //Default department
            Global.defaultDepartment = defaultMap.getDepartment(Global.roleCode);
            //Defatult Loction

            //Default VouStatus
            //Default Customer
            Global.defaultCustomer = defaultMap.getCutomer(Global.roleCode);
            //Default Supplier
            Global.defaultSupplier = defaultMap.getSuppplier(Global.roleCode);
        } catch (Exception e) {
            LOGGER.error("Initialize Data :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Initialize Data", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

    }

    public void initMenu() {
        LOGGER.info("init menu.");
        menuBar.removeAll();
        taskExecutor.execute(() -> {
            List<VRoleMenu> listVRM = menuService.getParentChildMenu(Global.roleCode, "Menu");
            listVRM.forEach((menu) -> {
                if (menu.getIsAllow()) {
                    if (menu.getChild() != null) {
                        if (!menu.getChild().isEmpty()) {
                            JMenu parent = new JMenu();
                            if (Util1.isNull(Global.sysProperties.get("system.menu.use.mm"), "-").equals("1")) {
                                parent.setText(menu.getMenuNameMM());
                            } else {
                                parent.setText(menu.getMenuName());
                            }
                            parent.setFont(Global.menuFont);
                            parent.setName(menu.getMenuClass() + ","
                                    + Util1.isNull(menu.getSoureAccCode(), "-") + ","
                                    + menu.getMenuName());
                            //Need to add action listener
                            //====================================
                            menuBar.add(parent);
                            addChildMenu(parent, menu.getChild());
                        } else {  //No Child
                            JMenu jmenu = new JMenu();
                            if (Util1.isNull(Global.sysProperties.get("system.menu.use.mm"), "-").equals("1")) {
                                jmenu.setText(menu.getMenuNameMM());
                            } else {
                                jmenu.setText(menu.getMenuName());
                            }
                            jmenu.setFont(Global.menuFont);
                            jmenu.setName(menu.getMenuClass() + ","
                                    + Util1.isNull(menu.getSoureAccCode(), "-") + ","
                                    + menu.getMenuName());
                            //Need to add action listener
                            //====================================
                            menuBar.add(jmenu);
                        }
                    } else {  //No Child
                        JMenu jmenu = new JMenu();
                        if (Util1.isNull(Global.sysProperties.get("system.menu.use.mm"), "-").equals("1")) {
                            jmenu.setText(menu.getMenuNameMM());
                        } else {
                            jmenu.setText(menu.getMenuName());
                        }
                        jmenu.setFont(Global.menuFont);
                        jmenu.setName(menu.getMenuClass() + ","
                                + Util1.isNull(menu.getSoureAccCode(), "-") + ","
                                + menu.getMenuName());                        //Need to add action listener
                        //====================================
                        menuBar.add(jmenu);
                    }
                }
            });
            addMargin();
            LOGGER.info("init menu end");
        });

    }

    private void addChildMenu(JMenu parent, List<VRoleMenu> listVRM) {
        listVRM.forEach((vrMenu) -> {
            if (vrMenu.getIsAllow()) {
                if (vrMenu.getChild() != null) {
                    if (!vrMenu.getChild().isEmpty()) {
                        JMenu menu = new JMenu();
                        if (Util1.isNull(Global.sysProperties.get("system.menu.use.mm"), "-").equals("1")) {
                            menu.setText(vrMenu.getMenuNameMM());
                        } else {
                            menu.setText(vrMenu.getMenuName());
                        }
                        menu.setFont(Global.menuFont);
                        menu.setName(vrMenu.getMenuClass() + ","
                                + Util1.isNull(vrMenu.getSoureAccCode(), "-") + ","
                                + vrMenu.getMenuName());
                        //Need to add action listener
                        //====================================
                        parent.add(menu);
                        addChildMenu(menu, vrMenu.getChild());
                    } else {  //No Child
                        JMenuItem menuItem = new JMenuItem();
                        if (Util1.isNull(Global.sysProperties.get("system.menu.use.mm"), "-").equals("1")) {
                            menuItem.setText(vrMenu.getMenuNameMM());
                        } else {
                            menuItem.setText(vrMenu.getMenuName());
                        }
                        menuItem.addActionListener(menuListener);
                        menuItem.setFont(Global.menuFont);
                        menuItem.setName(vrMenu.getMenuClass() + ","
                                + Util1.isNull(vrMenu.getSoureAccCode(), "-") + "-"
                                + vrMenu.getMenuName());
                        //====================================
                        parent.add(menuItem);
                    }
                } else {  //No Child
                    JMenuItem menuItem = new JMenuItem();
                    if (Util1.isNull(Global.sysProperties.get("system.menu.use.mm"), "-").equals("1")) {
                        menuItem.setText(vrMenu.getMenuNameMM());
                    } else {
                        menuItem.setText(vrMenu.getMenuName());
                    }
                    menuItem.setName(vrMenu.getMenuClass() + ","
                            + Util1.isNull(vrMenu.getSoureAccCode(), "-") + ","
                            + vrMenu.getMenuName());                    //Need to add action listener
                    menuItem.addActionListener(menuListener);
                    menuItem.setFont(Global.menuFont);
                    //====================================
                    parent.add(menuItem);
                }
            }
        });
    }

    private void assignWindoInfo() {
        Global.x = this.getX();
        Global.y = this.getY();
        Global.height = this.getHeight();
        Global.width = this.getWidth();
        //lblCompanyName.setText(Global.companyName);
    }

    @Override
    public void reload(String msg, Object data) {
        LOGGER.info("MainFrame reload : " + msg);
        switch (msg) {
            case "SENT-INV":
                //lblCompanyName.setText(data.toString());
                break;
            default:
                break;
        }
    }

    public void startNetworkDetector() {
        LOGGER.info("Network Dector Start.");
        NetworkDetector detector = new NetworkDetector();
        detector.setNetworkObserver(this);
        detector.setIpAddress(pingServer);
        scheduler.scheduleAtFixedRate(detector, Duration.ofSeconds(10));
    }

    private void addMargin() {
        java.awt.Component[] components = menuBar.getComponents();
        for (java.awt.Component component : components) {
            JMenu menu = (JMenu) component;
            menu.setBorder(BorderFactory.createCompoundBorder(menu.getBorder(), BorderFactory.createEmptyBorder(25, 0, 20, 25)));
        }
        revalidate();
        repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnHistory = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnFilter = new javax.swing.JButton();
        lblNeworkImage = new javax.swing.JLabel();
        lblNeworkPing = new javax.swing.JLabel();
        lblCompanyName = new javax.swing.JLabel();
        tabMain = new javax.swing.JTabbedPane();
        menuBar = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Core Account\n");
        setFont(Global.lableFont);
        setMinimumSize(new java.awt.Dimension(1024, 720));
        addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                formAncestorResized(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.setFocusable(false);

        toolBar.setOpaque(false);

        btnSave.setFont(Global.lableFont);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save_20px.png"))); // NOI18N
        btnSave.setToolTipText("F5 - Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        toolBar.add(btnSave);

        btnPrint.setFont(Global.lableFont);
        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/print_20px.png"))); // NOI18N
        btnPrint.setToolTipText("F6 - Print");
        btnPrint.setFocusable(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        toolBar.add(btnPrint);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh_20px.png"))); // NOI18N
        btnRefresh.setToolTipText("F7 - Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        toolBar.add(btnRefresh);

        btnDelete.setFont(Global.lableFont);
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/trash_20px.png"))); // NOI18N
        btnDelete.setToolTipText("F8 - Delete");
        btnDelete.setFocusable(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        toolBar.add(btnDelete);

        btnHistory.setFont(Global.lableFont);
        btnHistory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/time_machine_20px.png"))); // NOI18N
        btnHistory.setToolTipText("F9-History");
        btnHistory.setFocusable(false);
        btnHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoryActionPerformed(evt);
            }
        });
        toolBar.add(btnHistory);

        btnClear.setFont(Global.lableFont);
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add_file_20px.png"))); // NOI18N
        btnClear.setToolTipText("F10 - New");
        btnClear.setFocusable(false);
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        toolBar.add(btnClear);

        btnLogout.setFont(Global.lableFont);
        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logout_rounded_down_20px.png"))); // NOI18N
        btnLogout.setToolTipText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        toolBar.add(btnLogout);

        btnFilter.setFont(Global.lableFont);
        btnFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/slider_20px.png"))); // NOI18N
        btnFilter.setToolTipText("Filter Bar");
        btnFilter.setFocusable(false);
        btnFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });
        toolBar.add(btnFilter);

        lblNeworkImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/no_connection_20px.png"))); // NOI18N

        lblNeworkPing.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblNeworkPing.setText("2344ms");
        lblNeworkPing.setToolTipText("Server Connection Status");
        lblNeworkPing.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        lblCompanyName.setFont(Global.menuFont);
        lblCompanyName.setForeground(new java.awt.Color(0, 0, 255));
        lblCompanyName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCompanyName.setText("Company Name");
        lblCompanyName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCompanyNameMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblCompanyNameMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblCompanyName, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(lblNeworkImage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNeworkPing)
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNeworkImage, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNeworkPing, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblCompanyName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4)))
                .addContainerGap())
        );

        tabMain.setBackground(new java.awt.Color(255, 255, 255));
        tabMain.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabMain.setFont(Global.menuFont);
        tabMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabMainStateChanged(evt);
            }
        });

        menuBar.setFont(Global.menuFont);
        menuBar.setPreferredSize(new java.awt.Dimension(0, 40));
        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabMain, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        context.close();
        if (Global.sock != null) {
            try {
                Global.sock.close();
            } catch (IOException ex) {
                LOGGER.error("formWindowClosed : " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //lblMidInfo.setText("Application existing.");
    }//GEN-LAST:event_formWindowClosing

    private void formAncestorResized(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_formAncestorResized
    }//GEN-LAST:event_formAncestorResized

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        //LOGGER.info("formWindowStateChanged");
        assignWindoInfo();
    }//GEN-LAST:event_formWindowStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        //LOGGER.info("formWindowOpened");
        assignWindoInfo();
        initializeData();
        initMenu();
        //tabChangeListener();
    }//GEN-LAST:event_formWindowOpened

    private void tabMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabMainStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tabMainStateChanged

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.delete();
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.print();
        }
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.newForm();
        }

    }//GEN-LAST:event_btnClearActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        dispose();
        AccountSwingApplication.restart();

    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoryActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.history();
        }

    }//GEN-LAST:event_btnHistoryActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.save();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentResized

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        if (control != null) {
            control.refresh();
        }
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        // TODO add your handling code here:
        if (filterObserver != null) {
            filterObserver.sendFilter("-");
        }
    }//GEN-LAST:event_btnFilterActionPerformed

    private void lblCompanyNameMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCompanyNameMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_lblCompanyNameMouseEntered

    private void lblCompanyNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCompanyNameMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_lblCompanyNameMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnHistory;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblCompanyName;
    private javax.swing.JLabel lblNeworkImage;
    private javax.swing.JLabel lblNeworkPing;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.equals("DELIVERED")) {
        }
    }

    @Override
    public void load(Object source, Object selectObj) {

        if (source != null) {
            String parent = source.toString();
            String status = selectObj.toString();
            switch (status) {
                case "Start":
                    JLabel sLoading = hmTabLoading.get(parent);
                    taskExecutor.execute(() -> {
                        //LOGGER.info("Loading Visible Start");
                        sLoading.setVisible(true);
                        //LOGGER.info("Loading Visible End");
                    });

                    break;
                case "Stop":
                    JLabel eLoading = hmTabLoading.get(parent);
                    taskExecutor.execute(() -> {
                        //LOGGER.info("Loading Invisbile Start");
                        eLoading.setVisible(false);
                        //LOGGER.info("Loading Invisbile End");
                    });

                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e
    ) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void sendPingTime(long time) {
        showNetworkIcon(time);
    }

    private void showNetworkIcon(long time) {
        //LOGGER.info("Network Ping :" + time);
        if (time < 0) {
            lblNeworkImage.setIcon(offlineIcon);
            lblNeworkPing.setForeground(Color.gray);
            lblNeworkPing.setText("Offline");
        }
        if (time < 100 && time > 1) {
            lblNeworkImage.setIcon(onlineIcon);
            lblNeworkPing.setForeground(Color.green);
            lblNeworkPing.setText(time + "ms");

        }
        if (time > 100) {
            lblNeworkImage.setIcon(lowIcon);
            lblNeworkPing.setForeground(Color.red);
            lblNeworkPing.setText(time + "ms");
        }
    }
}
