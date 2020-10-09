/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui;

import com.cv.accountswing.AccountSwingApplication;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.ReloadData;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.view.VRoleMenu;
import com.cv.accountswing.entity.view.VUsrCompAssign;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.MenuService;
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
import com.cv.accountswing.ui.setup.RoleAssignSetup;
import com.cv.accountswing.ui.setup.SupplierSetup;
import com.cv.accountswing.ui.system.setup.Company;
import com.cv.accountswing.ui.system.setup.MenuSetup;
import com.cv.accountswing.ui.system.setup.RoleSetup;
import com.cv.accountswing.ui.system.setup.SystemPropertySetup;
import com.cv.accountswing.ui.system.setup.UserSetup;
import com.cv.inv.entry.Adjustment;
import com.cv.inv.entry.Damage;
import com.cv.inv.entry.Issue;
import com.cv.inv.entry.PurchaseEntry;
import com.cv.inv.entry.ReturnIn;
import com.cv.inv.entry.ReturnOut;
import com.cv.inv.entry.SaleEntry;
import com.cv.inv.entry.StockReceive;
import com.cv.inv.entry.Transfer;
import com.cv.inv.service.LocationService;
import com.cv.inv.service.SaleManService;
import com.cv.inv.service.VouStatusService;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.MachineInfo;
import com.cv.inv.entry.StockReceiving;
import com.cv.inv.service.MachineInfoService;
import com.cv.inv.service.RelationService;
import com.cv.inv.service.StockUnitService;
import com.cv.inv.service.StockService;
import com.cv.inv.setup.OtherSetup;
import com.cv.inv.setup.StockSetup;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
@Component
public class ApplicationMainFrame extends javax.swing.JFrame implements ReloadData,
        SelectionObserver, LoadingObserver, KeyListener {

    private final ConfigurableApplicationContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMainFrame.class);
    private final HashMap<String, Integer> hmTabIndex = new HashMap();
    private final HashMap<String, JLabel> hmTabLoading = new HashMap();
    JPopupMenu popupmenu;
    private final String clear = "Clear";
    private final String close = "Close";
    private final String print = "Print";
    private final String save = "Save";
    private final String delete = "Delete";
    private final String history = "History";
    ImageIcon online = null;
    ImageIcon offline = null;

    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        LOGGER.info("Selected Menu : " + menuName);
        JPanel panel = getPanel(menuName);
        addTabMain(panel, menuName);
    };

    /*@Autowired
    private CareServerSender sender;
    @Autowired
    private CareServerListener listener;*/
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
    private AllCash dailyCash;
    @Autowired
    private AllCash pettyCash;
    @Autowired
    private AllCash adjCash;
    @Autowired
    private AllCash pettyCashZay;
    @Autowired
    private Journal journal;
    @Autowired
    private MenuSetup menuSetup;
    @Autowired
    private AllCash bankSaving;
    @Autowired
    private AllCash homeSafe;
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
    private UsrCompRoleService usrCompRoleService;
    @Autowired
    private UserService userService;
    @Autowired
    private COAService cOAService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private DepartmentService departmentService;
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
    private OtherSetup otherSetup;
    @Autowired
    private StockReceiving stockReceiving;
    @Autowired
    private ReturnIn retIn;
    @Autowired
    private ReturnOut retOut;

    //Inventory Setup
    @Autowired
    private StockSetup stockSetup;
    @Autowired
    private SaleEntry saleEntry;
    @Autowired
    private PurchaseEntry purchaseEntry;
    @Autowired
    private ReturnIn returnIn;
    @Autowired
    private ReturnOut returnOut;
    @Autowired
    private Damage damage;
    @Autowired
    private Transfer transfer;
    @Autowired
    private Adjustment adjustment;
    @Autowired
    private Issue issue;
    @Autowired
    private StockReceive stockReceive;

    @Autowired
    private LocationService locationService;
    @Autowired
    private VouStatusService vouService;
    @Autowired
    private SaleManService saleManService;
    @Autowired
    private StockService stockService;
    @Autowired
    private MachineInfoService machineInfoService;
    @Autowired
    private StockUnitService stockUnitService;
    @Autowired
    private RelationService relationService;

    //@Autowired
    //private TaskExecutor threadPoolTaskExecutor;
    /**
     * Creates new form ApplicationMainFrame
     *
     * @param context
     */
    public ApplicationMainFrame(ConfigurableApplicationContext context) {
        System.setProperty("java.awt.headless", "false");
        initComponents();
        initKeyFoucsManager();
        this.context = context;
        ImageIcon size = new ImageIcon(getClass().getResource("/images/logo.png"));
        setIconImage(size.getImage());

    }

    private JPanel getPanel(String panelName) {
        //  LOGGER.info("getPanel : " + panelName);
        switch (panelName) {
            case "Customer":
                customerSetup.setName(panelName);
                customerSetup.setLoadingObserver(this);
                return customerSetup;
            case "Currency":
                currencySetup.setName(panelName);
                currencySetup.setLoadingObserver(this);
                return currencySetup;
            case "Supplier":
                supplierSetup.setName(panelName);
                supplierSetup.setLoadingObserver(this);
                return supplierSetup;
            case "Role Assign":
                roleAssignSetup.setName(panelName);
                roleAssignSetup.setLoadingObserver(this);
                return roleAssignSetup;
            case "Department":
                departmentSetup.setName(panelName);
                departmentSetup.setLoadingObserver(this);
                return departmentSetup;
            case "Region":
                regionSetup.setName(panelName);
                regionSetup.setLoadingObserver(this);
                return regionSetup;
            case "Chart of Account":
                chartOfAccountSetup.setName(panelName);
                chartOfAccountSetup.setLoadingObserver(this);
                return chartOfAccountSetup;
            case "Manage Project":
                manageProjectSetup.setName(panelName);
                manageProjectSetup.setLoadingObserver(this);
                return manageProjectSetup;
            case "COA Setup":
                coaSetup.setName(panelName);
                coaSetup.setLoadingObserver(this);
                return coaSetup;
            case "Daily Cash":
                dailyCash.setName(panelName);
                dailyCash.setLoadingObserver(this);
                dailyCash.setReloadData(this);
                dailyCash.setSourceAccId(Global.sysProperties.get("system.cashbook"));
                return dailyCash;
            case "Patty Cash":
                pettyCash.setName(panelName);
                pettyCash.setLoadingObserver(this);
                pettyCash.setReloadData(this);
                pettyCash.setSourceAccId(Global.sysProperties.get("system.pattycash"));
                return pettyCash;
            case "Adj Cash":
                adjCash.setName(panelName);
                adjCash.setLoadingObserver(this);
                adjCash.setReloadData(this);
                adjCash.setSourceAccId(Global.sysProperties.get("system.adjcash"));
                return adjCash;
            case "Petty Cash Zay":
                pettyCashZay.setName(panelName);
                pettyCashZay.setLoadingObserver(this);
                pettyCashZay.setReloadData(this);
                pettyCashZay.setSourceAccId(Global.sysProperties.get("system.pattycash.zay"));
                return pettyCashZay;
            case "Bank - Saving":
                bankSaving.setName(panelName);
                bankSaving.setLoadingObserver(this);
                bankSaving.setReloadData(this);
                bankSaving.setSourceAccId(Global.sysProperties.get("system.bankbook"));
                return bankSaving;
            case "Home Safe":
                homeSafe.setName(panelName);
                homeSafe.setLoadingObserver(this);
                homeSafe.setReloadData(this);
                homeSafe.setSourceAccId(Global.sysProperties.get("system.bank.homesave"));
                return homeSafe;
            case "Purchase":
                purchaseBook.setName(panelName);
                purchaseBook.setLoadingObserver(this);
                purchaseBook.setSourceAccId(Global.sysProperties.get("system.purchasebook"));
                return purchaseBook;
            case "Sale":
                saleBook.setName(panelName);
                saleBook.setLoadingObserver(this);
                saleBook.setSourceAccId(Global.sysProperties.get("system.salebook"));
                return saleBook;
            case "Journal":
                journal.setName(panelName);
                journal.setLoadingObserver(this);
                return journal;
            case "Journal Stock Opening":
                journalStockOpening.setName(panelName);
                journalStockOpening.setLoadingObserver(this);
                //journalStockOpening.initTable();
                return journalStockOpening;
            case "Credit Voucher":
                crdrVoucher.setSplitId("8");
                crdrVoucher.setSourceAcId(Global.sysProperties.get("system.creditvoucher"));
                crdrVoucher.setName(panelName);
                crdrVoucher.setLoadingObserver(this);
                return crdrVoucher;
            case "Debit Voucher":
                debitVoucher.setSplitId("9");
                debitVoucher.setSourceAcId(Global.sysProperties.get("system.creditvoucher"));
                debitVoucher.setName(panelName);
                debitVoucher.setLoadingObserver(this);
                return debitVoucher;
            case "AP/AR":
                aPARReport.setName(panelName);
                aPARReport.setLoadingObserver(this);
                return aPARReport;
            case "G/L Listing":
                glListingReport.setName(panelName);
                glListingReport.setLoadingObserver(this);
                return glListingReport;
            case "Profit & Lost":
                profitAndLost.setName(panelName);
                profitAndLost.setLoadingObserver(this);
                return profitAndLost;
            case "Balance Sheet":
                balanceSheet.setName(panelName);
                balanceSheet.setLoadingObserver(this);
                return balanceSheet;
            case "Menu":
                menuSetup.setName(panelName);
                menuSetup.setLoadingObserver(this);
                return menuSetup;
            case "System Property":
                systemPropertySetup.setName(panelName);
                systemPropertySetup.setLoadingObserver(this);
                return systemPropertySetup;
            case "Company":
                company.initTable();
                return company;
            case "User Setup":
                user.setName(panelName);
                user.setLoadingObserver(this);
                return user;
            case "Role Setup":
                roleSetup.setName(panelName);
                return roleSetup;
            case "C.O.A Opening":
                cOAOpeningSetup.setName(panelName);
                cOAOpeningSetup.setLoadingObserver(this);
                return cOAOpeningSetup;
            //Inventory Menu
            case "Stock Setup":
                stockSetup.setName(panelName);
                stockSetup.setLoadingObserver(this);
                return stockSetup;
            case "Stock Other Setup":
                otherSetup.setName(panelName);
                otherSetup.setLoadingObserver(this);
                return otherSetup;
            case "Sale Entry":
                saleEntry.setName(panelName);
                saleEntry.setLoadingObserver(this);
                return saleEntry;
            case "Purchase Entry":
                purchaseEntry.setName(panelName);
                purchaseEntry.setLoadingObserver(this);
                return purchaseEntry;
            case "Return In":
                returnIn.setName(panelName);
                returnIn.setLoadingObserver(this);
                return returnIn;
            case "Return Out":
                returnOut.setName(panelName);
                returnOut.setLoadingObserver(this);
                return returnOut;
            case "Damage":
                damage.setName(panelName);
                damage.setLoadingObserver(this);
                return damage;
            case "Transfer":
                transfer.setName(panelName);
                transfer.setLoadingObserver(this);
                return transfer;
            case "Adjustment":
                adjustment.setName(panelName);
                adjustment.setLoadingObserver(this);
                return adjustment;
            case "Issue":
                issue.setName(panelName);
                issue.setLoadingObserver(this);
                return issue;
            case "Receive":
                stockReceive.setName(panelName);
                stockReceive.setLoadingObserver(this);
                return stockReceive;
            default:
                return null;
        }
    }

    private void initKeyFoucsManager() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((KeyEvent ke) -> {
            switch (ke.getID()) {
                case KeyEvent.KEY_PRESSED:
                    switch (ke.getKeyCode()) {
                        case KeyEvent.VK_F7:
                            toolBar(getCurrentPanelName(), print);
                            break;
                        case KeyEvent.VK_F10:
                            toolBar(getCurrentPanelName(), clear);
                            break;
                    }
                    break;
            }
            return false;
        });
    }

    private void addTabMain(JPanel panel, String menuName) {
        Integer tabIndex = tabMain.getTabCount() - 1;
        tabMain.setSelectedIndex(tabIndex);
        tabMain.add(panel);
        tabMain.setTabComponentAt(tabMain.indexOfComponent(panel), setTitlePanel(tabMain, panel, menuName));
        tabMain.setSelectedComponent(panel);
    }

    private void showCloseAllPopup() {
        initPopup();
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
        JLabel loading = new JLabel();
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/loading_tab_20.gif"));
        loading.setIcon(icon);
        loading.setVisible(false);
        titlePanel.add(loading);
        hmTabLoading.put(title, loading);
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
                toolBar(panel.getName(), close);

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
            toolBar(getCurrentPanelName(), this.clear);

        });
        printReport.addActionListener((ActionEvent e) -> {
            toolBar(getCurrentPanelName(), this.print);

        });
        popupmenu.add(printReport);
        popupmenu.add(clearData);
        popupmenu.add(closeAll);
    }

    private String getCurrentPanelName() {
        String panelName = "";
        if (tabMain.getSelectedComponent() instanceof JPanel) {
            JPanel panel = (JPanel) tabMain.getSelectedComponent();

            panelName = panel.getName();
        }
        return panelName;

    }

    private void initializeData() {

        this.setTitle(this.getTitle() + "(" + Global.loginUser.getUserName() + ")");
        taskExecutor.execute(() -> {
            List listCI = usrCompRoleService.getAssignCompany(Global.loginUser.getUserId().toString());
            if (listCI.size() > 0) {
                VUsrCompAssign vuca = (VUsrCompAssign) listCI.get(0);
                Global.finicialPeriodFrom = Util1.toDateStr(vuca.getFinicialPeriodFrom(), "yyyy-MM-dd");
                Global.finicialPeriodTo = Util1.toDateStr(vuca.getFinicialPeriodTo(), "yyyy-MM-dd");
            }
            Global.listAppUser = userService.search("-", "-", "-", "-");
            Global.listCOA = cOAService.search("-", "-", Global.compId.toString(), "3", "-", "-", "-");
            Global.listCurrency = currencyService.search("-", "-", Global.compId.toString());
            Global.listDepartment = departmentService.search("-", "-", Global.compId.toString(),
                    "-", "-");
            Global.listTrader = traderService.searchTrader("-", "-", "-", "-", "-", Global.compId.toString());
            Global.listLocation = locationService.findAll();
            Global.listVou = vouService.findAll();
            Global.listSaleMan = saleManService.findAll();
            Global.listStock = stockService.findAll();
            Global.listStockUnit = stockUnitService.findAll();
            Global.listRelation = relationService.findAll();
            Global.listRelation.forEach(ur -> {
                Global.hmRelation.put(ur.getUnitKey(), ur.getFactor());
            });
            
            getMachinceInfo();
        });

    }

    private void initMenu() {
        showCloseAllPopup();
        taskExecutor.execute(() -> {
            List<VRoleMenu> listVRM = menuService.getParentChildMenu(Global.roleId.toString());
            listVRM.forEach((menu) -> {
                if (menu.getChild() != null) {
                    if (!menu.getChild().isEmpty()) {
                        JMenu parent = new JMenu();
                        parent.setText(menu.getMenuName());
                        parent.setFont(Global.menuFont);

                        //Need to add action listener
                        //====================================
                        menuBar.add(parent);
                        addChildMenu(parent, menu.getChild());
                    } else {  //No Child
                        JMenu jmenu = new JMenu();
                        jmenu.setText(menu.getMenuName());
                        jmenu.setFont(Global.menuFont);

                        //Need to add action listener
                        //====================================
                        menuBar.add(jmenu);
                    }
                } else {  //No Child
                    JMenu jmenu = new JMenu();
                    jmenu.setText(menu.getMenuName());
                    jmenu.setFont(Global.menuFont);

                    //Need to add action listener
                    //====================================
                    menuBar.add(jmenu);
                }
            });
            addMargin();
        });

    }

    private void addChildMenu(JMenu parent, List<VRoleMenu> listVRM) {
        listVRM.forEach((vrMenu) -> {
            if (vrMenu.getChild() != null) {
                if (!vrMenu.getChild().isEmpty()) {
                    JMenu menu = new JMenu();
                    menu.setText(vrMenu.getMenuName());
                    menu.setFont(Global.menuFont);

                    //Need to add action listener
                    //====================================
                    parent.add(menu);
                    addChildMenu(menu, vrMenu.getChild());
                } else {  //No Child
                    JMenuItem menuItem = new JMenuItem();
                    menuItem.setText(vrMenu.getMenuName());
                    //Need to add action listener
                    menuItem.addActionListener(menuListener);
                    menuItem.setFont(Global.menuFont);

                    //====================================
                    parent.add(menuItem);
                }
            } else {  //No Child
                JMenuItem menuItem = new JMenuItem();
                menuItem.setText(vrMenu.getMenuName());
                //Need to add action listener
                menuItem.addActionListener(menuListener);
                menuItem.setFont(Global.menuFont);
                //====================================
                parent.add(menuItem);
            }
        });
    }

    private void assignWindoInfo() {
        Global.x = this.getX();
        Global.y = this.getY();
        Global.height = this.getHeight();
        Global.width = this.getWidth();
    }

    @Override
    public void reload(String msg, Object data) {
        LOGGER.info("MainFrame reload : " + msg);
        switch (msg) {
            case "SENT-INV":
                lblLog.setText(data.toString());
                break;
            default:
                break;

        }
        /*threadPoolTaskExecutor.execute(() -> {
            try {
                LOGGER.info("msg : " + msg);
                switch (msg) {
                    case "NEW-INIT":
                        break;
                    case "NEW-SYSPROP":
                        for (Map.Entry<String, Integer> entry : hmTabIndex.entrySet()) {
                            switch (entry.getKey()) {
                                case "Sale":
                                    Sale sale1 = (Sale) tabMain.getComponent(entry.getValue());
                                    sale1.initLabelDesp();
                                    break;
                            }
                        }
                        break;
                    case "SYNC-MEDICINE":
                        Medicine med = (Medicine) data;
                        LOGGER.info("call syncMedicineH2");
                        syncMedicineH2(med);
                        break;
                    case "SALE-VOUSEARCH":
                        ReloadData rd = Global.hmReloadData.get(msg);
                        if (rd != null) {
                            rd.reload(msg, data);
                        }
                        break;
                    case "SEARCH-SessionCheck":
                        rd = Global.hmReloadData.get(msg);
                        if (rd != null) {
                            rd.reload(msg, data);
                          
                        }
                        break;
                          case "SessionCheckTtl":
                        rd = Global.hmReloadData.get(msg);
                        if (rd != null) {
                            rd.reload(msg, data);
                          
                        }
                        break;
                    case "GET-SALEVOUCHER":
                        int index = hmTabIndex.get("Sale");
                        Sale sale2 = (Sale) tabMain.getComponent(index);
                        //SaleHis sh = (SaleHis)data;
                        if (sale2 != null) {
                            sale2.setSaleVoucher(data.toString());
                        }
                        break;
                    case "GET-PaymentVoucher":
                        index = hmTabIndex.get("Payment");
                        CustomerPayment cp = (CustomerPayment) tabMain.getComponent(index);
                        if (cp != null) {
                            cp.assignData(data.toString());
                        }
                        break;
                    case "SAVE-CustomerPayment":
                        index = hmTabIndex.get("Payment");
                        CustomerPayment cp1 = (CustomerPayment) tabMain.getComponent(index);
                        if (cp1 != null) {
                            cp1.setAckData(data.toString());
                        }
                        break;
                    case "SEARCH-SaleVoucherPayment":
                        index = hmTabIndex.get("Payment Search");
                        SearchCustomerPayment scp = (SearchCustomerPayment) tabMain.getComponent(index);
                        if (scp != null) {
                            scp.assignData(data.toString());
                        }
                        break;
                    case "PUR-VOUSEARCH":
                        rd = Global.hmReloadData.get(msg);
                        if (rd != null) {
                            rd.reload(msg, data);
                        }
                        break;
                    case "RETURNOUT-VOUSEARCH":
                        rd = Global.hmReloadData.get(msg);
                        if (rd != null) {
                            rd.reload(msg, data);
                        }
                        break;
                    case "GET-PURVOUCHER":
                        index = hmTabIndex.get("Purchase");
                        Purchase pur = (Purchase) tabMain.getComponent(index);
                        //SaleHis sh = (SaleHis)data;
                        if (pur != null) {
                            pur.setPurVoucher(data.toString());
                        }
                        break;
                    case "GET-RETURNOUTVOUCHER":
                        index = hmTabIndex.get("Return Out");
                        ReturnOut retOutS1 = (ReturnOut) tabMain.getComponent(index);
                        //SaleHis sh = (SaleHis)data;
                        if (retOutS1 != null) {
                            retOutS1.setRetOutVoucher(data.toString());
                        }
                        break;
                case "RETURNIN-VOUSEARCH":
                            rd = Global.hmReloadData.get(msg);
                            if (rd != null) {
                                rd.reload(msg, data);
                            }
                            break;
                case "GET-RETURNINVOUCHER":
                    index = hmTabIndex.get("Return In");
                    ReturnIn retInS = (ReturnIn) tabMain.getComponent(index);
                    //SaleHis sh = (SaleHis)data;
                    if (retInS != null) {
                        retInS.setReturnInVoucher(data.toString());
                    }
                    break;
                case "DAMAGE-VOUSEARCH":
                    rd = Global.hmReloadData.get(msg);
                    if (rd != null) {
                        rd.reload(msg, data);
                            }
                    break;
                case "GET-DAMAGEVOUCHER":
                    index = hmTabIndex.get("Damage");
                    Damage dmg = (Damage) tabMain.getComponent(index);
                    //SaleHis sh = (SaleHis)data;
                            if (dmg != null) {
                                dmg.setDamageVoucher(data.toString());
                            }
                            break;
                case "TRANSFER-VOUSEARCH":
                    rd = Global.hmReloadData.get(msg);
                    if (rd != null) {
                        rd.reload(msg, data);
                    }
                    break;
                    
                case "GET-TRANSFERVOUCHER":
                    index = hmTabIndex.get("Transfer");
                    Transfer tran = (Transfer) tabMain.getComponent(index);
                    //SaleHis sh = (SaleHis)data;
                    if (tran != null) {
                    tran.setTranVoucher(data.toString());
                    }
                    break;
                case "ACK":
                    ReloadData rd1 = Global.hmReloadData.get(data.toString());
                    if (rd1 != null) {
                        rd1.reload(msg, data);
                    }
                    break;
                }
            }catch (Exception ex) {
                LOGGER.error("reloadData " + msg + " : " + ex.getMessage());
            }
         });*/
    }

    public void autoSyncStart() {
        //executor.scheduleAtFixedRate(periodicTask, 0, 3, TimeUnit.MINUTES);
    }

    private void addMargin() {
        java.awt.Component[] components = menuBar.getComponents();
        for (java.awt.Component component : components) {
            JMenu menu = (JMenu) component;
            menu.setBorder(BorderFactory.createCompoundBorder(menu.getBorder(), BorderFactory.createEmptyBorder(30, 0, 30, 30)));
        }
        revalidate();
        repaint();
    }

    private void online(boolean status) {
        if (online == null || offline == null) {
            ImageIcon onlineIcon = new ImageIcon(this.getClass().getResource("/images/online-status.png"));
            ImageIcon offlineIcon = new ImageIcon(this.getClass().getResource("/images/offline-status.png"));
            BufferedImage on = resizeImage(onlineIcon.getImage(), 30, 30, false);
            BufferedImage off = resizeImage(offlineIcon.getImage(), 30, 30, false);
            online = new ImageIcon(on);
            offline = new ImageIcon(off);
            lblLog.setText(null);
        }

        if (status) {
            lblLog.setIcon(online);
        } else {
            lblLog.setIcon(offline);
        }
    }

    BufferedImage resizeImage(Image originalImage,
            int scaledWidth, int scaledHeight,
            boolean preserveAlpha) {
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }

    private void toolBar(String menuName, String type) {
        switch (menuName) {
            case "Customer":
                switch (type) {
                    case clear:
                        customerSetup.clear();
                        break;
                    case close:
                        customerSetup.setIsShown(false);
                        break;
                    /* case print:
                        dailyCash.print();
                        break;*/
                }
                break;
            case "Currency":
                switch (type) {
                    case clear:
                        currencySetup.clear();
                        break;
                    case close:
                        currencySetup.setIsShown(false);
                        break;
                    /* case print:
                        currencySetup.print();
                        break;*/
                }
                break;
            case "Supplier":
                switch (type) {
                    case clear:
                        currencySetup.clear();
                        break;
                    case close:
                        currencySetup.setIsShown(false);
                        break;
                    /* case print:
                        currencySetup.print();
                        break;*/
                }
                break;
            case "Role Assign":
                switch (type) {
                    /*case clear:
                     roleAssignSetup.clear();
                     break;*/
                    case close:
                        roleAssignSetup.setIsShown(false);
                        break;
                    /* case print:
                        currencySetup.print();
                        break;*/
                }
                break;
            case "Department":
                switch (type) {
                    case clear:
                        departmentSetup.clear();
                        break;
                    case close:
                        departmentSetup.setIsShown(false);
                        break;
                    /* case print:
                        currencySetup.print();
                        break;*/
                }
                break;
            case "Region":
                switch (type) {
                    case clear:
                        regionSetup.clear();
                        break;
                    case close:
                        regionSetup.setIsShown(false);
                        break;
                    /* case print:
                        currencySetup.print();
                        break;*/
                }
                break;
            case "Chart of Account":
                switch (type) {
                    case clear:
                        chartOfAccountSetup.clear();
                        break;
                    case close:
                        chartOfAccountSetup.setIsShown(false);
                        break;
                    /* case print:
                        currencySetup.print();
                        break;*/
                }
                break;
            case "Daily Cash":
                switch (type) {
                    case clear:
                        dailyCash.clearFilter();
                        break;
                    case close:
                        dailyCash.setIsShown(false);
                        break;
                    case print:
                        dailyCash.print();
                        break;
                }
                break;
            case "Patty Cash":
                switch (type) {
                    case clear:
                        pettyCash.clearFilter();
                        break;
                    case close:
                        pettyCash.setIsShown(false);
                        break;
                    case print:
                        pettyCash.print();
                        break;
                }
                break;
            case "Adj Cash":
                switch (type) {
                    case clear:
                        adjCash.clearFilter();
                        break;
                    case close:
                        adjCash.setIsShown(false);
                        break;
                    case print:
                        adjCash.print();
                        break;
                }
                break;
            case "Petty Cash Zay":
                switch (type) {
                    case clear:
                        pettyCashZay.clearFilter();
                        break;
                    case close:
                        pettyCashZay.setIsShown(false);
                        break;
                    case print:
                        pettyCashZay.print();
                        break;
                }
                break;
            case "Bank - Saving":
                switch (type) {
                    case clear:
                        bankSaving.clearFilter();
                        break;
                    case close:
                        bankSaving.setIsShown(false);
                        break;
                    case print:
                        bankSaving.print();
                        break;
                }
                break;
            case "Home Safe":
                switch (type) {
                    case clear:
                        homeSafe.clearFilter();
                        break;
                    case close:
                        homeSafe.setIsShown(false);
                        break;
                    case print:
                        homeSafe.print();
                        break;
                }
                break;
            case "Purchase":
                switch (type) {
                    case clear:
                        purchaseBook.clearFilter();
                        break;
                    case close:
                        purchaseBook.setIsShown(false);
                        break;
                    case print:
                        purchaseBook.print();
                        break;
                }
            case "Sale":
                switch (type) {
                    case clear:
                        saleBook.clearFilter();
                        break;
                    case close:
                        saleBook.setIsShown(false);
                        break;
                    case print:
                        saleBook.print();
                        break;
                }
                break;
            case "Stock Setup":
                switch (type) {
                    case clear:
                        stockSetup.clear();
                        break;
                    case close:
                        stockSetup.setIsShown(false);
                        break;
                    case print:
                        //saleBook.print();
                        break;
                }
                break;
            case "Journal":
                switch (type) {
                    case clear:
                        journal.clear();
                        break;
                    case close:
                        journal.setIsShown(false);
                        break;
                    case print:
                        //saleBook.print();
                        break;
                }
                break;
            case "Credit Voucher":
                switch (type) {
                    case clear:
                        crdrVoucher.clear();
                        break;
                    case close:
                        crdrVoucher.setIsShown(false);
                        break;
                    case print:
                        //saleBook.print();
                        break;
                }
            case "System Property":
                switch (type) {
                    case clear:
                        systemPropertySetup.clear();
                        break;
                    case close:
                        systemPropertySetup.setIsShown(false);
                        break;
                    case print:
                        //saleBook.print();
                        break;
                }
                break;
            case "User Setup":
                switch (type) {
                    case clear:
                        user.clear();
                        break;
                    case close:
                        user.setIsShown(false);
                        break;
                    case print:
                        //saleBook.print();
                        break;
                }
                break;
            case "AP/AR":
                switch (type) {
                    case clear:
                        aPARReport.clear();
                        break;
                    case close:
                        aPARReport.setIsShown(false);
                        break;
                    case print:
                        aPARReport.printApar();
                        break;
                }
                break;
<<<<<<< HEAD
            case "Return In":
                switch (type) {
                    case clear:
                        retIn.clear();
                        break;
                    case save:
                        retIn.save();
                        break;
                    case history:
                        retIn.history();
                        break;
                    case delete:
                        retIn.delete();
                        break;
                }

                break;
            case "Return Out":
                switch (type) {
                    case clear:
                        retOut.clear();
                        break;
                    case save:
                        retOut.save();
                        break;
                    case history:
                        retOut.history();
                        break;
                    case delete:
                        retOut.delete();
                        break;
                }

=======
            case "Purchase Entry":
                switch (type) {
                    case save:
                        purchaseEntry.save();
                        break;
                    case clear:
                        purchaseEntry.clear();
                        break;
                    case close:
                        purchaseEntry.setIsShown(false);
                        break;

                }
>>>>>>> 2645f0594b7ed96750a08ea9446f2c4710390d82
                break;

            default:
                break;

        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnPrint = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnHistory = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        lblLog = new javax.swing.JLabel();
        tabMain = new javax.swing.JTabbedPane();
        menuBar = new javax.swing.JMenuBar();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Core Account\n");
        setFont(Global.lableFont);
        addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                formAncestorResized(evt);
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

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        btnPrint.setFont(Global.lableFont);
        btnPrint.setText("F7 - Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        btnDelete.setFont(Global.lableFont);
        btnDelete.setText("F8 - Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnClear.setFont(Global.lableFont);
        btnClear.setText("F10 - Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnLogout.setFont(Global.lableFont);
        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnHistory.setFont(Global.lableFont);
        btnHistory.setText("F6-History");
        btnHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoryActionPerformed(evt);
            }
        });

        btnSave.setFont(Global.lableFont);
        btnSave.setText("F5-Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblLog.setFont(Global.textFont);
        lblLog.setText("Welcome");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnHistory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogout)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLog, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSave))
                        .addComponent(btnDelete, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClear, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLogout, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(tabMain)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
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
        LOGGER.info("formAncestorResized");
    }//GEN-LAST:event_formAncestorResized

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        LOGGER.info("formWindowStateChanged");
        assignWindoInfo();
    }//GEN-LAST:event_formWindowStateChanged

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        LOGGER.info("formWindowOpened");
        assignWindoInfo();
        initMenu();
        initializeData();
        //tabChangeListener();
    }//GEN-LAST:event_formWindowOpened

    private void tabMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabMainStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tabMainStateChanged

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        toolBar(getCurrentPanelName(), delete);
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        toolBar(getCurrentPanelName(), print);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        toolBar(getCurrentPanelName(), clear);

    }//GEN-LAST:event_btnClearActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        dispose();
        AccountSwingApplication.restart();

    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoryActionPerformed
        // TODO add your handling code here:
        toolBar(getCurrentPanelName(), history);

    }//GEN-LAST:event_btnHistoryActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        toolBar(getCurrentPanelName(), save);
    }//GEN-LAST:event_btnSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnHistory;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblLog;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTabbedPane tabMain;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {

    }

    @Override
    public void load(Object source, Object selectObj) {
        if (source != null) {
            String parent = source.toString();
            String status = selectObj.toString();
            switch (status) {
                case "Start":
                    hmTabLoading.get(parent).setVisible(true);
                    break;
                case "Stop":
                    hmTabLoading.get(parent).setVisible(false);
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e
    ) {
    }

    @Override
    public void keyPressed(KeyEvent e
    ) {
    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
        if (e.getKeyCode() == KeyEvent.VK_F7) {
            LOGGER.info("PRESSED F7");
            toolBar(getCurrentPanelName(), clear);
        }
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            LOGGER.info("PRESSED F5");
            toolBar(getCurrentPanelName(), save);
        }
        if (e.getKeyCode() == KeyEvent.VK_F6) {
            LOGGER.info("PRESSED F6");
            toolBar(getCurrentPanelName(), history);
        }
    }

    private void getMachinceInfo() {
        Global.machineName = Util1.getComputerName();
        try {
            Global.machineId = machineInfoService.getMax(Global.machineName);
            if (Global.machineId == 0) {
                String machineName = Util1.getComputerName();
                String ipAddress = Util1.getIPAddress();
                MachineInfo machine = new MachineInfo();

                machine.setIpAddress(ipAddress);
                machine.setMachineName(machineName);
                machineInfoService.save(machine);
                Global.machineId = machineInfoService.getMax(Global.machineName);

            }

        } catch (Exception ex) {
            LOGGER.error("getMachieInfo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());

        }
    }
}
