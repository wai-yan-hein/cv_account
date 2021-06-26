/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.FilterObserver;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.temp.TmpOpeningClosing;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.entity.view.VTriBalance;
import com.cv.accountswing.service.COAOpeningDService;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.service.VGlService;
import com.cv.accountswing.service.VTriBalanceService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.COAAutoCompleter;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DateAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.report.common.GLListingTableModel;
import com.cv.accountswing.util.Util1;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.IFilterEditor;
import net.coderazzi.filters.gui.IFilterHeaderObserver;
import net.coderazzi.filters.gui.TableFilterHeader;
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
public class GLReport extends javax.swing.JPanel implements SelectionObserver,
        PanelControl, FilterObserver, KeyListener {

    private int selectRow = -1;
    private static final Logger log = LoggerFactory.getLogger(GLReport.class);
    private final ImageIcon account = new ImageIcon(getClass().getResource("/images/accountant.png"));

    /**
     * Creates new form AparGlReport
     */
    @Autowired
    private GLListingTableModel glListingTableModel;
    @Autowired
    private COAOpeningDService coaOpDService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ReportService rService;
    @Autowired
    private VGlService vGlService;
    @Autowired
    private VTriBalanceService vTriBalanceService;
    @Autowired
    private TrialBalanceDetailDialog trialBalanceDetailDialog;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private COAService coaService;
    private COAAutoCompleter cOAAutoCompleter;
    private boolean isShown = false;
    private LoadingObserver loadingObserver;
    private JPopupMenu popup;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private String stDate;
    private String enDate;
    private String traderCode;
    private String dept;
    private String currency;
    private String userCode;
    private String traderType;
    private String coaCode;
    private TableFilterHeader filterHeader;
    private boolean isGLCal = false;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
        clear();
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public GLReport() {
        initComponents();
        initKeyListener();
    }

    private void initKeyListener() {
        txtDep.addKeyListener(this);
        txtCOA.addKeyListener(this);
    }

    private void initMain() {
        assingDefaultValue();
        initCombo();
        initTable();
        search();
        isShown = true;
    }

    private void assingDefaultValue() {
        stDate = Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy");
        enDate = stDate;
        txtDate.setText("Today");
    }

    private void initTable() {
        tblGL.setModel(glListingTableModel);
        tblGL.getTableHeader().setFont(Global.lableFont);
        tblGL.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblGL.getTableHeader().setForeground(ColorUtil.foreground);
        tblGL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblGL.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblGL.getColumnModel().getColumn(1).setPreferredWidth(400);
        tblGL.getColumnModel().getColumn(2).setPreferredWidth(1);
        tblGL.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblGL.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblGL.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblGL.setDefaultRenderer(Double.class, new TableCellRender());
        tblGL.setDefaultRenderer(Object.class, new TableCellRender());

        tblGL.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popup.show(tblGL, e.getX(), e.getY());
                }
                if (e.getClickCount() == 2) {
                    if (tblGL.getSelectedRow() >= 0) {
                        selectRow = tblGL.convertRowIndexToModel(tblGL.getSelectedRow());
                        VTriBalance vtb = glListingTableModel.getTBAL(selectRow);
                        String coaId = vtb.getKey().getCoaId();
                        String coaName = vtb.getCoaName();
                        searchTriBalDetail(coaId, coaName);
                    }
                }
            }

        });
        filterHeader = new TableFilterHeader(tblGL, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);
        filterHeader.addHeaderObserver(new IFilterHeaderObserver() {
            @Override
            public void tableFilterEditorCreated(TableFilterHeader tfh, IFilterEditor ife, TableColumn tc) {
                log.info("tableFilterEditorCreated");
            }

            @Override
            public void tableFilterEditorExcluded(TableFilterHeader tfh, IFilterEditor ife, TableColumn tc) {
                log.info("tableFilterEditorExcluded");
            }

            @Override
            public void tableFilterUpdated(TableFilterHeader tfh, IFilterEditor ife, TableColumn tc) {
                calAmount();
            }
        });

    }

    private void calAmount() {
        double drAmt = 0.0;
        double crAmt = 0.0;
        for (int row = 0; row < tblGL.getRowCount(); row++) {
            drAmt += Util1.getDouble(tblGL.getValueAt(row, 3));
            crAmt += Util1.getDouble(tblGL.getValueAt(row, 4));
        }
        txtFTotalDrAmt.setValue(drAmt);
        txtFTotalCrAmt.setValue(crAmt);
    }

    private String getTarget() {
        String targetId = "-";
        VTriBalance tbal = glListingTableModel.getTBAL(selectRow);
        targetId = tbal.getKey().getCoaId();
        return targetId;
    }

    private void searchGLListing() {
        loadingObserver.load(this.getName(), "Start");
        if (!isGLCal) {
            log.info("G/L Calculation Start.");
            isGLCal = true;
            clearTable();
            initializeParameter();
            taskExecutor.execute(() -> {
                try {
                    coaOpDService.genTriBalance1(Global.compCode, Global.finicialPeriodFrom, Util1.toDateStrMYSQL(enDate, "dd/MM/yyyy"),
                            "-", currency, dept, traderCode, userCode, Global.machineId.toString());
                    List<VTriBalance> listVTB = vTriBalanceService.getTriBalance(Global.machineId.toString(), coaCode);
                    glListingTableModel.setListTBAL(listVTB);
                    calGLTotlaAmount(listVTB);
                    isGLCal = false;
                    log.info("G/L Calculation End.");
                    loadingObserver.load(this.getName(), "Stop");
                } catch (Exception ex) {
                    isGLCal = false;
                    log.error("searchGLListing : " + ex.getMessage());
                    loadingObserver.load(this.getName(), "Stop");
                }
            });
        }

    }

    private void searchTriBalDetail(String coaId, String desp) {
        try {
            List<VGl> listVGL = vGlService.searchGlDrCr(Util1.isNull(stDate, "-"),
                    Util1.isNull(enDate, "-"), coaId, currency, dept,
                    traderCode, Global.compCode,
                    "DR");
            swapDrCrAmt(listVGL, getTarget());
            calculateOpening(traderCode);
            openTBDDialog(listVGL, desp, traderCode);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            loadingObserver.load(this.getName(), "Stop");
        }

    }

    private void calculateOpening(String traderCode) {
        List<TmpOpeningClosing> opBalanceGL;
        try {
            if (!traderCode.equals("-")) {
                String opDate;
                if (txtDate.getText().equals("All")) {
                    opDate = stDate;
                } else {
                    opDate = enDate;
                }
                opBalanceGL = coaOpDService.getOpBalanceByTrader(getTarget(),
                        Global.finicialPeriodFrom,
                        Util1.toDateStrMYSQL(opDate, "dd/MM/yyyy"), 3, "MMK",
                        Global.loginUser.getAppUserCode(),
                        Util1.isNull(dept, "-"), traderCode,
                        Global.machineId.toString(), Global.compCode
                );
                if (!opBalanceGL.isEmpty()) {
                    double opening;
                    TmpOpeningClosing tmpOC = opBalanceGL.get(0);
                    if (Util1.getDouble(tmpOC.getCrAmt()) > Util1.getDouble(Util1.getDouble(tmpOC.getDrAmt()))) {
                        opening = tmpOC.getCrAmt() * -1;
                    } else {
                        opening = tmpOC.getDrAmt();
                    }
                    trialBalanceDetailDialog.setOpeningAmt(opening);
                    log.info("OPENING :" + tmpOC.getOpening());
                } else {
                    trialBalanceDetailDialog.setOpeningAmt(0.0);
                }
            } else {
                opBalanceGL = coaOpDService.getOpBalanceGL1(getTarget(),
                        Global.finicialPeriodFrom,
                        stDate, 3, "MMK",
                        Global.loginUser.getAppUserCode(),
                        Util1.isNull(dept, "-"), Global.machineId.toString());
                if (!opBalanceGL.isEmpty()) {
                    TmpOpeningClosing tmpOC = opBalanceGL.get(0);
                    trialBalanceDetailDialog.setOpeningAmt(tmpOC.getOpening());
                    log.info("OPENING :" + tmpOC.getOpening());
                    //txtFOpening.setValue(tmpOC.getOpening());
                } else {
                    trialBalanceDetailDialog.setOpeningAmt(0.0);
                }
            }

        } catch (Exception ex) {
            log.error("Calculation Opening :" + ex.getMessage());
        }

    }

    private void openTBDDialog(List<VGl> listVGl, String traderName, String traderCode) {
        trialBalanceDetailDialog.setIconImage(account.getImage());
        trialBalanceDetailDialog.setDesp(traderName);
        trialBalanceDetailDialog.setTraderCode(traderCode);
        trialBalanceDetailDialog.setTargetId(getTarget());
        trialBalanceDetailDialog.setListVGl(listVGl);
        trialBalanceDetailDialog.setSize(Global.width - 100, Global.height - 100);
        trialBalanceDetailDialog.setLocationRelativeTo(null);
        trialBalanceDetailDialog.setVisible(true);
    }

    private void swapDrCrAmt(List<VGl> listVGL, String targetId) {
        listVGL.forEach(vgl -> {
            String sourceAcId = Util1.isNull(vgl.getSourceAcId(), "-");
            if (!sourceAcId.equals(targetId)) {
                double tmpDrAmt = 0.0f;
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

    private void calGLTotlaAmount(List<VTriBalance> listTB) {
        double ttlDrAmt = 0.0;
        double ttlCrAmt = 0.0;
        double ttlNet = 0.0;
        double outBal = 0.0;
        for (VTriBalance tb : listTB) {
            ttlDrAmt += Util1.getDouble(tb.getDrAmt());
            ttlCrAmt += Util1.getDouble(tb.getCrAmt());
            ttlNet += Util1.getDouble(tb.getClosing());
        }
        txtFTotalCrAmt.setValue(Util1.toFormatPattern(ttlCrAmt));
        txtFTotalDrAmt.setValue(Util1.toFormatPattern(ttlDrAmt));
        txtFNetChange.setValue(Util1.toFormatPattern(ttlNet));
        if (ttlDrAmt > ttlCrAmt) {
            outBal = ttlDrAmt - ttlCrAmt;
        } else {
            outBal = ttlCrAmt - ttlDrAmt;
        }
        txtFOFB.setValue(Util1.toFormatPattern(outBal));
    }

    private void initializeParameter() {
        dept = Util1.isNull(dept, "-");
        traderCode = Util1.isNull(traderCode, "-");
        currency = Global.defalutCurrency.getKey().getCode();
        stDate = Util1.isNull(stDate, Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy"));
        enDate = Util1.isNull(enDate, Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy"));
        userCode = Global.loginUser.getAppUserCode();
        traderType = Util1.isNull(traderType, "-");
        ChartOfAccount coa = cOAAutoCompleter.getCOA();
        coaCode = getCOACode(coa.getCode(), Global.compCode);
    }

    public String getCOACode(String code, String compCode) {
        String tmp = "-";
        List<ChartOfAccount> listCoA = coaService.getAllChild(code, Global.compCode);
        if (!listCoA.isEmpty()) {
            tmp = "";
            tmp = listCoA.stream().map(coa -> String.format("'%s',", coa.getCode())).reduce(tmp, String::concat);
        }
        tmp = tmp.substring(0, tmp.length() - 1);
        return tmp;
    }

    private void initCombo() {
        DateAutoCompleter dateAutoCompleter = new DateAutoCompleter(txtDate,
                Global.listDateModel, null);
        dateAutoCompleter.setSelectionObserver(this);
        List<ChartOfAccount> listCOALv2 = coaService.getLevelOneTwo(Global.compCode);
        cOAAutoCompleter = new COAAutoCompleter(txtCOA, listCOALv2, null, true);
        cOAAutoCompleter.setSelectionObserver(this);
        cOAAutoCompleter.setCoa(new ChartOfAccount("-", "All"));
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep,
                Global.listDepartment, null, true);
        departmentAutoCompleter.setSelectionObserver(this);
        departmentAutoCompleter.setDepartment(new Department("-", "All"));
        CurrencyAutoCompleter currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency,
                Global.listCurrency, null);
        currencyAutoCompleter.setSelectionObserver(this);
        currencyAutoCompleter.setCurrency(Global.defalutCurrency);
    }

    private void printGLListing() {
        try {
            String depName = "-".equals(dept) ? "All" : departmentService.findById(dept).getDeptName();
            String date = txtDate.getText();
            String compName = Global.sysProperties.get("system.report.company");
            String reportPath = Global.sysProperties.get("system.report.path");
            String fontPath = Global.sysProperties.get("system.font.path");
            String filePath = reportPath + File.separator + "TriBalance";
            Map<String, Object> p = new HashMap();
            p.put("p_company_name", compName);
            p.put("p_comp_id", Global.compCode);
            p.put("p_mac_id", Global.machineId);
            p.put("p_report_info", date);
            p.put("p_dept_name", "Dept : " + depName);
            rService.genReport(filePath, filePath, fontPath, p);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(Global.parentForm, "Report", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            log.error("printGLListing : " + ex.getMessage());
        }

    }

    public void clear() {
        txtCurrency.setText(null);
        txtDate.setText(null);
        txtDep.setText(null);
        txtCOA.setText(null);
    }

    private void search() {
        searchGLListing();
    }

    private void clearTable() {
        glListingTableModel.clear();
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
        txtCOA = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblGL = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtFTotalDrAmt = new javax.swing.JFormattedTextField();
        txtFTotalCrAmt = new javax.swing.JFormattedTextField();
        txtFNetChange = new javax.swing.JFormattedTextField();
        txtFOFB = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, java.awt.Color.lightGray));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtDate.setFont(Global.lableFont);
        txtDate.setName("txtDate"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtDep"); // NOI18N
        txtDep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDepFocusGained(evt);
            }
        });
        txtDep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDepActionPerformed(evt);
            }
        });

        txtCOA.setFont(Global.textFont);
        txtCOA.setName("txtCOA"); // NOI18N
        txtCOA.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCOAFocusGained(evt);
            }
        });
        txtCOA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCOAActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("COA");

        txtCurrency.setFont(Global.lableFont);
        txtCurrency.setToolTipText("");
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
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
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDep, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtCOA, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
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
                    .addComponent(txtCOA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblGL.setFont(Global.textFont);
        tblGL.setModel(new javax.swing.table.DefaultTableModel(
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
        tblGL.setRowHeight(Global.tblRowHeight);
        tblGL.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblGLKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblGL);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, java.awt.Color.lightGray));

        txtFTotalDrAmt.setEditable(false);
        txtFTotalDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFTotalDrAmt.setFont(Global.amtFont);

        txtFTotalCrAmt.setEditable(false);
        txtFTotalCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFTotalCrAmt.setFont(Global.amtFont);

        txtFNetChange.setEditable(false);
        txtFNetChange.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFNetChange.setFont(Global.amtFont);

        txtFOFB.setEditable(false);
        txtFOFB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFOFB.setFont(Global.amtFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Out Of Balance");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtFTotalDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFTotalCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtFNetChange, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txtFOFB))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFTotalDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFTotalCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFNetChange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFOFB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        mainFrame.setFilterObserver(this);
        if (!isShown) {
            initMain();
        }
        //search();
    }//GEN-LAST:event_formComponentShown

    private void txtDepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_txtDepActionPerformed

    private void txtCOAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCOAActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_txtCOAActionPerformed

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void txtDepFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepFocusGained
        // TODO add your handling code here:
        txtDep.selectAll();
    }//GEN-LAST:event_txtDepFocusGained

    private void txtCOAFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCOAFocusGained
        // TODO add your handling code here:
        txtCOA.selectAll();
    }//GEN-LAST:event_txtCOAFocusGained

    private void tblGLKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblGLKeyReleased

    }//GEN-LAST:event_tblGLKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblGL;
    private javax.swing.JTextField txtCOA;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JFormattedTextField txtFNetChange;
    private javax.swing.JFormattedTextField txtFOFB;
    private javax.swing.JFormattedTextField txtFTotalCrAmt;
    private javax.swing.JFormattedTextField txtFTotalDrAmt;
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
                case "COA":
                    searchGLListing();
                    break;
                case "Trader":
                    if (selectObj instanceof Trader) {
                        Trader trader = (Trader) selectObj;
                        switch (trader.getTraderName()) {
                            case "All Customer":
                                traderType = "CUS";
                                traderCode = "-";
                                break;
                            case "All Supplier":
                                traderType = "SUP";
                                traderCode = "-";
                                break;
                            default:
                                traderCode = trader.getCode();
                                traderType = "-";
                                break;
                        }
                    }
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
        printGLListing();
    }

    @Override
    public void refresh() {
        search();
    }

    @Override
    public void sendFilter(String filter) {
        if (filterHeader.isVisible()) {
            filterHeader.setVisible(false);
        } else {
            filterHeader.setVisible(true);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object source = e.getSource();
        String ctrlName = "-";
        if (source instanceof JTextField) {
            ctrlName = ((JTextField) source).getName();
        }
        switch (ctrlName) {
            case "txtDep":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_BACK_SPACE:
                        if (txtDep.getText().isEmpty()) {
                            dept = "-";
                            departmentAutoCompleter.closePopup();
                            search();
                        }
                        break;
                    case KeyEvent.VK_DELETE: {
                        if (txtDep.getText().isEmpty()) {
                            dept = "-";
                            departmentAutoCompleter.closePopup();
                            search();
                        }
                        break;
                    }
                }
        }

    }
}
