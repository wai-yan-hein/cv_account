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
import com.cv.accountswing.entity.temp.TmpOpeningClosing;
import com.cv.accountswing.entity.view.VApar;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.entity.view.VTriBalance;
import com.cv.accountswing.service.COAOpeningDService;
import com.cv.accountswing.service.CompanyInfoService;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.service.SystemPropertyService;
import com.cv.accountswing.service.VAParService;
import com.cv.accountswing.service.VGlService;
import com.cv.accountswing.service.VTriBalanceService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DateAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.ui.report.common.APARTableModel;
import com.cv.accountswing.ui.report.common.GLListingTableModel;
import com.cv.accountswing.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class AparGlReport extends javax.swing.JPanel implements SelectionObserver, PanelControl {

    private int selectRow = -1;
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AparGlReport.class);

    /**
     * Creates new form AparGlReport
     */
    @Autowired
    private APARTableModel aPARTableModel;
    @Autowired
    private GLListingTableModel glListingTableModel;
    @Autowired
    private VAParService aParService;
    @Autowired
    private COAOpeningDService coaOpDService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ReportService rService;
    @Autowired
    private CompanyInfoService ciService;
    @Autowired
    private SystemPropertyService spService;
    @Autowired
    private VGlService vGlService;
    @Autowired
    private VTriBalanceService vTriBalanceService;
    @Autowired
    private TrialBalanceDetailDialog trialBalanceDetailDialog;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private TableRowSorter<TableModel> sorter;
    private boolean isShown = false;
    private LoadingObserver loadingObserver;
    private JPopupMenu popup;
    private String stDate;
    private String enDate;
    private String cvId;
    private String dept;
    private String currency;
    private String userId;
    private String panelName;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
        clear();
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public AparGlReport() {
        initComponents();
        initPopup();
    }

    private void initMain() {
        panelName = this.getName();
        assingDefaultValue();
        initCombo();
        initTable();
        isShown = true;
    }

    private void initPopup() {
        popup = new JPopupMenu();
        JMenuItem print = new JMenuItem("Print");
        popup.add(print);
        print.addActionListener((ActionEvent e) -> {
            if (panelName.equals("AP/AR")) {
                printApar();
            } else if (panelName.equals("G/L Listing")) {
            }
        });
    }

    private void assingDefaultValue() {
        stDate = Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy");
        enDate = stDate;
        txtDate.setText("Today");
    }

    private void initTable() {
        if (panelName.equals("AP/AR")) {
            tblAPAR.setModel(aPARTableModel);
            searchAPAR();
        } else if (panelName.equals("G/L Listing")) {
            tblAPAR.setModel(glListingTableModel);
            searchGLListing();
        }
        tblAPAR.getTableHeader().setFont(Global.lableFont);
        tblAPAR.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAPAR.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblAPAR.getColumnModel().getColumn(1).setPreferredWidth(400);
        tblAPAR.getColumnModel().getColumn(2).setPreferredWidth(1);
        tblAPAR.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblAPAR.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblAPAR.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblAPAR.setDefaultRenderer(Double.class, new TableCellRender());
        tblAPAR.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblAPAR.getModel());
        tblAPAR.setRowSorter(sorter);

        tblAPAR.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popup.show(tblAPAR, e.getX(), e.getY());
                }
                if (e.getClickCount() == 2) {
                    if (tblAPAR.getSelectedRow() >= 0) {
                        selectRow = tblAPAR.convertRowIndexToModel(tblAPAR.getSelectedRow());
                        if (panelName.equals("AP/AR")) {
                            VApar apar = aPARTableModel.getAPAR(selectRow);
                            String cvId = apar.getTraderId();
                            String desp = apar.getTraderName();
                            Double netChange = apar.getClosing();

                            searchTriBalDetail(cvId, "-", desp, netChange);
                        } else if (panelName.equals("G/L Listing")) {
                            VTriBalance vtb = glListingTableModel.getTBAL(selectRow);
                            String coaId = vtb.getKey().getCoaId();
                            String coaName = vtb.getCoaName();
                            Double netChange = vtb.getClosing();
                            searchTriBalDetail("-", coaId, coaName, netChange);

                        }

                    }
                }
            }

        });

    }

    private String getTarget() {
        String targetId = "-";
        if (panelName.equals("AP/AR")) {
            VApar apar = aPARTableModel.getAPAR(selectRow);
            targetId = apar.getTraderId();
        } else if (panelName.equals("G/L Listing")) {
            VTriBalance tbal = glListingTableModel.getTBAL(selectRow);
            targetId = tbal.getKey().getCoaId();
        }
        return targetId;
    }

    private void searchAPAR() {
        loadingObserver.load(this.getName(), "Start");
        clearTable();
        initializeParameter();
        taskExecutor.execute(() -> {
            try {
                coaOpDService.genArAp(Global.compId.toString(),
                        Util1.toDateStrMYSQL(stDate, "dd/MM/yyyy"), Global.finicialPeriodFrom,
                        Util1.toDateStrMYSQL(enDate, "dd/MM/yyyy"), "-",
                        currency, dept, cvId,
                        userId);
                List<VApar> listApar = aParService.getApAr(userId,
                        Global.compId.toString());
                aPARTableModel.setListAPAR(listApar);
                calAPARTotalAmount(listApar);
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception ex) {
                LOGGER.error("SEARCH APAR -----" + ex.getMessage());
            }
        });

    }

    private void searchGLListing() {
        loadingObserver.load(this.getName(), "Start");
        clearTable();
        initializeParameter();
        taskExecutor.execute(() -> {
            try {
                LOGGER.info("START DATE :" + stDate + "---" + "END DATE :" + enDate);
                coaOpDService.genTriBalance(Global.compId.toString(),
                        Util1.toDateStrMYSQL(stDate, "dd/MM/yyyy"),
                        Global.finicialPeriodFrom, Util1.toDateStrMYSQL(enDate, "dd/MM/yyyy"),
                        "-", currency, dept, cvId, userId);
                List<VTriBalance> listVTB = vTriBalanceService.getTriBalance(userId, Global.compId.toString());
                glListingTableModel.setListTBAL(listVTB);
                calGLTotlaAmount(listVTB); 
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception ex) {
                LOGGER.error("searchGLListing -----" + ex.getMessage());
                loadingObserver.load(this.getName(), "Stop");
            }
        });

    }

    private void searchTriBalDetail(String cvId, String coaId, String desp, Double netChange) {
        List<VGl> listVGL = vGlService.searchGlDrCr(Util1.isNull(stDate, "-"),
                Util1.isNull(enDate, "-"), coaId, currency, dept,
                cvId, Global.compId.toString(),
                "DR");
        swapDrCrAmt(listVGL, getTarget());
        calculateOpening();
        openTBDDialog(listVGL, desp, netChange);

    }

    private void calculateOpening() {
        List<TmpOpeningClosing> opBalanceGL;
        try {
            String minuStDate = Util1.addDateTo(stDate, -1);
            opBalanceGL = coaOpDService.getOpBalanceGL(getTarget(),
                    minuStDate,
                    enDate, 3, "MMK",
                    Global.loginUser.getUserId().toString(),
                    Util1.isNull(dept, "-"));
            if (!opBalanceGL.isEmpty()) {
                TmpOpeningClosing tmpOC = opBalanceGL.get(0);
                trialBalanceDetailDialog.setOpeningAmt(tmpOC.getOpening());
                LOGGER.info("OPENING :" + tmpOC.getOpening());
                //txtFOpening.setValue(tmpOC.getOpening());
            }
        } catch (Exception ex) {
            LOGGER.error("Calculation Opening :" + ex.getMessage());
        }

    }

    private void openTBDDialog(List<VGl> listVGl, String traderName, Double netChange) {
        trialBalanceDetailDialog.setDesp(traderName);
        trialBalanceDetailDialog.setNetChange(netChange);
        trialBalanceDetailDialog.setListVGl(listVGl);
        trialBalanceDetailDialog.setSize(Global.width - 200, Global.height - 200);
        trialBalanceDetailDialog.setLocationRelativeTo(null);
        trialBalanceDetailDialog.setVisible(true);
    }

    private void swapDrCrAmt(List<VGl> listVGL, String targetId) {
        listVGL.forEach(vgl -> {
            String sourceAcId = Util1.isNull(vgl.getSourceAcId(), "-");
            //String accId = Util1.isNull(vgl.getAccountId(), "-");
            /*if (sourceAcId.equals(targetId)) {
            double tmpAmt;
            if (Util1.isNullZero(vgl.getSplitId()) == 8) { //Credit Voucher
            tmpAmt = Util1.getDouble(vgl.getDrAmt());
            vgl.setDrAmt(Util1.getDouble(vgl.getCrAmt()));
            vgl.setCrAmt(tmpAmt);
            } else if (Util1.isNullZero(vgl.getSplitId()) == 9) { //Debit Voucher
            tmpAmt = Util1.getDouble(vgl.getCrAmt());
            vgl.setCrAmt(Util1.getDouble(vgl.getDrAmt()));
            vgl.setDrAmt(tmpAmt);
            }
            } else */
            if (!sourceAcId.equals(targetId)) {
                double tmpDrAmt = 0.0;
                if (vgl.getDrAmt() != null) {
                    tmpDrAmt = vgl.getDrAmt();
                }
                vgl.setDrAmt(Util1.getDouble(vgl.getCrAmt()));
                vgl.setCrAmt(tmpDrAmt);

                String tmpStr = vgl.getAccName();
                vgl.setAccName(vgl.getSrcAccName());
                vgl.setSrcAccName(tmpStr);
            } else {
                vgl.setCrAmt(Util1.getDouble(vgl.getCrAmt()));
                vgl.setDrAmt((Util1.getDouble(vgl.getDrAmt())));
            }
        });
    }

    private void calAPARTotalAmount(List<VApar> listApar) {
        String curr = listApar.get(0).getKey().getCurrency();
        double ttlDrAmt = 0.0;
        double ttlCrAmt = 0.0;
        for (VApar apar : listApar) {
            ttlDrAmt += Util1.getDouble(apar.getDrAmt());
            ttlCrAmt += Util1.getDouble(apar.getCrAmt());
        }
        txtFTotalCrAmt.setValue(ttlCrAmt);
        txtFTotalDrAmt.setValue(ttlDrAmt);
        txtDisplayCurr.setText(curr);
        txtFOFB.setValue(ttlDrAmt - ttlCrAmt);
    }

    private void calGLTotlaAmount(List<VTriBalance> listTB) {
        if (!listTB.isEmpty()) {
            String curr = listTB.get(0).getKey().getCurrId();
            double ttlDrAmt = 0.0;
            double ttlCrAmt = 0.0;
            for (VTriBalance tb : listTB) {
                ttlDrAmt += Util1.getDouble(tb.getDrAmt());
                ttlCrAmt += Util1.getDouble(tb.getCrAmt());
            }
            txtFTotalCrAmt.setValue(ttlCrAmt);
            txtFTotalDrAmt.setValue(ttlDrAmt);
            txtDisplayCurr.setText(curr);
            txtFOFB.setValue(ttlDrAmt - ttlCrAmt);
        }
    }

    private void initializeParameter() {
        dept = Util1.isNull(txtDep.getText(), "-");
        cvId = Util1.isNull(txtPerson.getText(), "-1");
        currency = Global.sysProperties.get("system.default.currency");
        stDate = Util1.isNull(stDate, Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy"));
        enDate = Util1.isNull(enDate, Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy"));
        userId = Global.loginUser.getUserId().toString();
    }

    private void initCombo() {
        DateAutoCompleter dateAutoCompleter = new DateAutoCompleter(txtDate,
                Global.listDateModel, null);
        dateAutoCompleter.setSelectionObserver(this);

        TraderAutoCompleter traderAutoCompleter = new TraderAutoCompleter(txtPerson,
                Global.listTrader, null);
        traderAutoCompleter.setSelectionObserver(this);

        DepartmentAutoCompleter departmentAutoCompleter = new DepartmentAutoCompleter(txtDep,
                Global.listDepartment, null);
        departmentAutoCompleter.setSelectionObserver(this);
        CurrencyAutoCompleter currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency,
                Global.listCurrency, null);
        currencyAutoCompleter.setSelectionObserver(this);
        currencyAutoCompleter.setCurrency(Global.defalutCurrency);
    }

    public void printApar() {
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            try {
                CompanyInfo ci = ciService.findById(Global.compId);
                SystemPropertyKey key = new SystemPropertyKey();
                key.setCompCode(Global.compId);
                key.setPropKey("system.report.path");
                SystemProperty sp = spService.findById(key);
                String fileName = userId + "_apar.pdf";
                String reportPath = sp.getPropValue();
                String imgPath = reportPath;
                String filePath = reportPath + "/temp/" + fileName;

                reportPath = reportPath + "APAR";
                key = new SystemPropertyKey();
                key.setCompCode(Global.compId);
                key.setPropKey("system.font.path");
                sp = spService.findById(key);
                String fontPath = sp.getPropValue();

                Map<String, Object> parameters = new HashMap();
                parameters.put("p_company_name", ci.getName());
                parameters.put("p_comp_id", Global.compId.toString());
                parameters.put("img_path", imgPath);

                rService.genCreditVoucher(reportPath, filePath, fontPath, parameters);
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception ex) {
                LOGGER.error("PRINT APAR REPORT :::" + ex.getMessage());
            }
        });

    }

    public void clear() {
        txtCurrency.setText(null);
        txtDate.setText(null);
        txtDep.setText(null);
        txtPerson.setText(null);
    }

    private void search() {
        if (panelName.equals("AP/AR")) {
            searchAPAR();
        } else if (panelName.equals("G/L Listing")) {
            searchGLListing();
        }
    }

    private void clearTable() {
        if (panelName.equals("AP/AR")) {
            aPARTableModel.clear();
        } else if (panelName.equals("G/L Listing")) {
            glListingTableModel.clear();
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
        jLabel1 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        txtPerson = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAPAR = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtDisplayCurr = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtFTotalDrAmt = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtFTotalCrAmt = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtFOFB = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtDate.setFont(Global.lableFont);
        txtDate.setName("txtDate"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtDep"); // NOI18N
        txtDep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDepActionPerformed(evt);
            }
        });

        txtPerson.setFont(Global.textFont);
        txtPerson.setName("txtPerson"); // NOI18N
        txtPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPersonActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Person");

        txtCurrency.setFont(Global.lableFont);
        txtCurrency.setToolTipText("");
        txtCurrency.setEnabled(false);
        txtCurrency.setName("txtCurrency"); // NOI18N
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Currency");

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
                .addComponent(txtPerson)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtCurrency)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblAPAR.setFont(Global.textFont);
        tblAPAR.setModel(new javax.swing.table.DefaultTableModel(
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
        tblAPAR.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblAPAR);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Currency");

        txtDisplayCurr.setFont(Global.lableFont);
        txtDisplayCurr.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDisplayCurr.setEnabled(false);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Total Dr-Amt");

        txtFTotalDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFTotalDrAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFTotalDrAmt.setEnabled(false);
        txtFTotalDrAmt.setFont(Global.amtFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Total Cr-Amt");

        txtFTotalCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFTotalCrAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFTotalCrAmt.setEnabled(false);
        txtFTotalCrAmt.setFont(Global.amtFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Out of Balance");

        txtFOFB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFOFB.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFOFB.setEnabled(false);
        txtFOFB.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtDisplayCurr)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(txtFTotalDrAmt)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(txtFTotalCrAmt)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtFOFB)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtDisplayCurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtFTotalDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtFTotalCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtFOFB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
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
        search();
    }//GEN-LAST:event_txtDepActionPerformed

    private void txtPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPersonActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_txtPersonActionPerformed

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_txtCurrencyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblAPAR;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JTextField txtDisplayCurr;
    private javax.swing.JFormattedTextField txtFOFB;
    private javax.swing.JFormattedTextField txtFTotalCrAmt;
    private javax.swing.JFormattedTextField txtFTotalDrAmt;
    private javax.swing.JTextField txtPerson;
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
                    dept = selectObj.toString();
                    break;
                case "Currency":
                    currency = selectObj.toString();
                    break;
                case "Trader":
                    cvId = selectObj.toString();
                    break;
            }
            search();

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
        assingDefaultValue();
        search();
        isShown = false;
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }
}
