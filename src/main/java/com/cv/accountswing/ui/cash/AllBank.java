/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.cash;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.FilterObserver;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.ReloadData;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.CompanyInfo;
import com.cv.accountswing.entity.SystemProperty;
import com.cv.accountswing.entity.SystemPropertyKey;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.temp.TmpOpeningClosing;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.COAOpeningDService;
import com.cv.accountswing.service.CompanyInfoService;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.service.SystemPropertyService;
import com.cv.accountswing.service.VDescriptionService;
import com.cv.accountswing.service.VGlService;
import com.cv.accountswing.service.VRefService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AllBankTableModel;
import com.cv.accountswing.ui.editor.CurrencyEditor;
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.COACellEditor;
import com.cv.accountswing.ui.editor.DespEditor;
import com.cv.accountswing.ui.editor.RefCellEditor;
import com.cv.accountswing.ui.editor.TraderCellEditor;
import com.cv.accountswing.ui.filter.FilterPanel;
import com.cv.accountswing.util.Util1;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class AllBank extends javax.swing.JPanel implements SelectionObserver,
        PanelControl, FilterObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllBank.class);
    private String stDate;
    private String enDate;
    private String desp;
    private String accId;
    private String ref;
    private String depId;
    private String traderCode;
    private String currency;
    private String traderType;

    @Autowired
    private FilterPanel filterPanel;
    @Autowired
    private VGlService vGlService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private AllBankTableModel allCashTableModel;
    @Autowired
    private COAOpeningDService coaOpDService;
    @Autowired
    private CompanyInfoService ciService;
    @Autowired
    private SystemPropertyService spService;
    @Autowired
    ReportService rService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private VRefService refService;
    @Autowired
    private VDescriptionService descriptionService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private SelectionObserver selectionObserver;
    private LoadingObserver loadingObserver;
    private ReloadData reloadData;
    private String sourceAccId;
    private final JPopupMenu popupmenu = new JPopupMenu();
    private final JLabel lblMessage = new JLabel();
    private boolean isShown = false;
    private TableFilterHeader filterHeader;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
        clearFilter();
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public String getSourceAccId() {
        return sourceAccId;
    }

    public void setSourceAccId(String sourceAccId) {
        this.sourceAccId = sourceAccId;
        LOGGER.info("Source Id :" + sourceAccId);

    }

    public void setReloadData(ReloadData reloadData) {
        this.reloadData = reloadData;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    /**
     * Creates new form CashBook
     *
     */
    public AllBank() {
        initComponents();
        initPopup();
        initKeyListener();
        initMouseLisener();
    }

    public AllBank newInstance() {
        return applicationContext.getBean(AllBank.class);
    }

    private void initMain() {
        initTable();
        clearFilter();
        isShown = true;
    }

    private void clearTextBox() {
        txtFClosing.setValue(0.0);
        txtFOpening.setValue(0.0);
        txtFCreditAmt.setValue(0.0);
        txtFDebitAmt.setValue(0.0);
    }

    private void requestFoucsTable() {
        int row = allCashTableModel.getListSize() - 1;
        //tblCash.setCellSelectionEnabled(true);
        if (row >= 0) {
            tblCash.changeSelection(row, 0, false, false);
            tblCash.requestFocus();
        }
    }

    private void initTable() {
        allCashTableModel.setSelectionObserver(this);
        allCashTableModel.setReloadData(reloadData);
        tblCash.setModel(allCashTableModel);
        tblCash.getTableHeader().setFont(Global.tblHeaderFont);
        tblCash.getTableHeader().setPreferredSize(new Dimension(25, 25));
        tblCash.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblCash.getTableHeader().setForeground(ColorUtil.foreground);
        tblCash.setCellSelectionEnabled(true);
        allCashTableModel.setSourceAccId(sourceAccId);
        allCashTableModel.setParent(tblCash);
        allCashTableModel.addNewRow();
        tblCash.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCash.getColumnModel().getColumn(0).setPreferredWidth(15);// Date
        tblCash.getColumnModel().getColumn(1).setPreferredWidth(13);// Department
        tblCash.getColumnModel().getColumn(2).setPreferredWidth(195);// Description      
        tblCash.getColumnModel().getColumn(3).setPreferredWidth(195);// Ref  
        tblCash.getColumnModel().getColumn(4).setPreferredWidth(90);// Person
        tblCash.getColumnModel().getColumn(5).setPreferredWidth(150);// Account
        tblCash.getColumnModel().getColumn(6).setPreferredWidth(1);// Curr      
        tblCash.getColumnModel().getColumn(7).setPreferredWidth(90);// Dr-Amt   
        tblCash.getColumnModel().getColumn(8).setPreferredWidth(90);// Cr-Amt  
        tblCash.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(1).setCellEditor(new DepartmentCellEditor(false));
        tblCash.getColumnModel().getColumn(2).setCellEditor(new DespEditor(descriptionService));
        tblCash.getColumnModel().getColumn(3).setCellEditor(new RefCellEditor(refService));
        tblCash.getColumnModel().getColumn(4).setCellEditor(new TraderCellEditor(false, 0));
        tblCash.getColumnModel().getColumn(5).setCellEditor(new COACellEditor(false));
        tblCash.getColumnModel().getColumn(6).setCellEditor(new CurrencyEditor());
        tblCash.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());

        tblCash.setDefaultRenderer(Double.class, new TableCellRender());
        tblCash.setDefaultRenderer(Object.class, new TableCellRender());

        tblCash.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    selectionObserver = filterPanel;
                    if (selectionObserver != null) {
                        selectionObserver.selected("TABTOTEXTBOX", "");
                    }
                }
            }

        });
        tblCash.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        /*tblCash.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblCash.getActionMap().put("ENTER-Action", actionTblCash);*/
        tblCash.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblCash.getActionMap().put("F8-Action", actionItemDeleteExp);
        /*tblCash.setDragEnabled(true);
        tblCash.setDropMode(DropMode.INSERT_ROWS);
        tblCash.setTransferHandler(new AllCashTableHandler(allCashTableModel));*/
        filterHeader = new TableFilterHeader(tblCash, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);
    }

    private void initKeyListener() {
        tblCash.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown()) {
                    if (e.getKeyCode() == KeyEvent.VK_V) {
                        allCashTableModel.copyRow();
                    }
                }
            }

        });
    }

    private void initPopup() {
        lblMessage.setForeground(ColorUtil.foreground);
        lblMessage.setFont(Global.textFont);
        lblMessage.setHorizontalAlignment(JLabel.CENTER);
        popupmenu.setBorder(BorderFactory.createLineBorder(Color.black));
        popupmenu.setBackground(ColorUtil.btnEdit);
        popupmenu.setFocusable(false);
        popupmenu.add(lblMessage);
    }

    private void initMouseLisener() {
        tblCash.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String message = getMessage();
                    if (message != null) {
                        lblMessage.setText(message);
                        popupmenu.show(tblCash, e.getX(), e.getY());
                    }
                }
            }

        });
    }

    public boolean isCellEditable(int row, int column) {
        return tblCash.getModel().isCellEditable(row, column);
    }

    private String getMessage() {
        String msg = null;
        int selectRow = tblCash.convertRowIndexToModel(tblCash.getSelectedRow());
        int column = tblCash.getSelectedColumn();
        VGl vGl = allCashTableModel.getVGl(selectRow);
        switch (column) {
            case 0://date
                msg = Util1.toDateStr(vGl.getGlDate(), "dd/MM/yyyy");
                break;
            case 1://dep
                msg = vGl.getDeptName();
                break;
            case 2://desp
                msg = vGl.getDescription();
                break;
            case 3://ref
                msg = vGl.getReference();
                break;
            case 4://person
                msg = vGl.getTraderCode();
                break;
            case 5://account
                msg = vGl.getAccountId();
                break;
            case 6://curr
                msg = vGl.getfCurName();
                break;
        }

        return msg;
    }

    private final Action actionItemDeleteExp = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteVoucher();
        }
    };

    private void deleteVoucher() {
        VGl vgl;
        int selectRow = tblCash.convertRowIndexToModel(tblCash.getSelectedRow());
        if (isCellEditable(selectRow, 0)) {
            int yes_no;
            if (tblCash.getSelectedRow() >= 0) {
                vgl = allCashTableModel.getVGl(selectRow);
                if (vgl.getGlCode() != null) {
                    yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete?",
                            "Delete", JOptionPane.YES_NO_OPTION);
                    if (tblCash.getCellEditor() != null) {
                        tblCash.getCellEditor().stopCellEditing();
                    }
                    if (yes_no == 0) {
                        allCashTableModel.deleteVGl(selectRow);
                        calDebitCredit();
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Can't delete. ");
        }

    }

    public void printVoucher() {
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            try {
                CompanyInfo ci = ciService.findById(Global.compCode);
                SystemPropertyKey key = new SystemPropertyKey();
                key.setCompCode(Global.compCode);
                key.setPropKey("system.report.path");
                SystemProperty sp = spService.findById(key);
                String userCode = Global.loginUser.getAppUserCode();
                String fileName = userCode + "_Ledger_Report.pdf";
                String reportPath = sp.getPropValue();
                //String reportPath1 = reportPath;
                String filePath = reportPath + "/temp/" + fileName;

                reportPath = reportPath + "/LedgerReport";
                key = new SystemPropertyKey();
                key.setCompCode(Global.compCode);
                key.setPropKey("system.font.path");
                sp = spService.findById(key);
                String fontPath = sp.getPropValue();

                Map<String, Object> parameters = new HashMap();
                parameters.put("p_company_name", ci.getName());
                parameters.put("p_comp_id", Global.compCode);
                parameters.put("p_trg_acc_id", sourceAccId);
                parameters.put("p_acc_id", Util1.isNull(accId, "-"));
                parameters.put("p_report_info", stDate
                        + " to " + enDate);
                parameters.put("p_from", Util1.toDateStrMYSQL(stDate, "dd/MM/yyyy"));
                parameters.put("p_to", Util1.toDateStrMYSQL(enDate, "dd/MM/yyyy"));
                parameters.put("p_desp", Util1.isNull(desp, "-"));
                parameters.put("p_from_curr", Util1.isNull(currency, "-"));
                parameters.put("p_to_curr", "-");
                parameters.put("p_ref", Util1.isNull(ref, "-"));
                parameters.put("p_dept", Util1.isNull(depId, "-"));
                parameters.put("p_vou_no", "-");
                parameters.put("p_cv_id", Util1.isNull(accId, "-"));
                parameters.put("p_report_name", this.getName());
                parameters.put("p_opening", txtFOpening.getValue());
                parameters.put("p_closing", txtFClosing.getValue());
                rService.genReport(reportPath, filePath, fontPath, parameters);
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception ex) {
                LOGGER.error("getLedgerReport : " + ex);

            }
        });

    }

    private void searchCash() {
        try {
            initializeParameter();
            if (sourceAccId != null) {
                loadingObserver.load(this.getName(), "Start");
                taskExecutor.execute(() -> {
                    LOGGER.info(this.getName() + "Start Date  :" + stDate + "-" + "End Date :" + enDate);
                    List<VGl> listVGl = vGlService.search(stDate, enDate,
                            desp, sourceAccId,
                            accId, currency, "-",
                            ref, depId, "-", traderCode,
                            "-", "-", "-", "-", "-",
                            "-", "-", "-",
                            traderType,
                            "-");
                    LOGGER.info("Search Cash Book End ...");
                    swapData(listVGl, sourceAccId);
                    allCashTableModel.setListVGl(listVGl);
                    allCashTableModel.addNewRow();
                    requestFoucsTable();
                    calOpeningClosing();
                    loadingObserver.load(this.getName(), "Stop");
                });
            } else {
                JOptionPane.showMessageDialog(Global.parentForm, "Source Account Missing.");
            }
        } catch (HeadlessException e) {
            LOGGER.error("Search Cash :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Searching Cash", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void initializeParameter() {
        stDate = Util1.isNull(stDate, Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy"));
        enDate = Util1.isNull(enDate, Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy"));
        desp = Util1.isNull(desp, "-");
        accId = Util1.isNull(accId, "-");
        currency = Util1.isNull(currency, "-");
        ref = Util1.isNull(ref, "-");
        depId = Util1.isNull(depId, "-");
        traderCode = Util1.isNull(traderCode, "-");
        traderType = Util1.isNull(traderType, "-");
        clearTextBox();
    }

    public void clearFilter() {
        stDate = null;
        enDate = null;
        desp = null;
        accId = null;
        currency = null;
        ref = null;
        depId = null;
        traderCode = null;
        txtFClosing.setValue(0.0);
        txtFCreditAmt.setValue(0.0);
        txtFDebitAmt.setValue(0.0);
        txtFOpening.setValue(0.0);
        filterPanel.clear();
        searchCash();

    }

    private void swapData(List<VGl> listVGL, String targetId) {
        if (!listVGL.isEmpty()) {
            listVGL.forEach(vgl -> {
                String sourceAcId = Util1.isNull(vgl.getSourceAcId(), "-");
                String accId = Util1.isNull(vgl.getAccountId(), "-");
                if (sourceAcId.equals(targetId)) {
                    /*if(Util1.isNullZero(vgl.getSplitId()) == 8){ //Credit Voucher
                Double tmpAmt = vgl.getDrAmt();
                vgl.setDrAmt(vgl.getCrAmt());
                vgl.setCrAmt(tmpAmt);
                } else if(Util1.isNullZero(vgl.getSplitId()) == 9){ //Debit Voucher
                Double tmpAmt = vgl.getCrAmt();
                vgl.setCrAmt(vgl.getDrAmt());
                vgl.setDrAmt(tmpAmt);
                }*/
                } else if (accId.equals(targetId)) {
                    double tmpDrAmt = 0.0;
                    if (vgl.getDrAmt() != null) {
                        tmpDrAmt = vgl.getDrAmt();
                    }
                    vgl.setDrAmt(vgl.getCrAmt());
                    vgl.setCrAmt(tmpDrAmt);

                    String tmpStr = vgl.getAccName();
                    vgl.setAccName(vgl.getSrcAccName());
                    vgl.setSrcAccName(tmpStr);
                } else {
                    vgl.setDrAmt(0.0);
                    vgl.setCrAmt(0.0);
                }
            });
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

        tblScrollPane = new javax.swing.JScrollPane();
        tblCash = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtFClosing = new javax.swing.JFormattedTextField();
        txtFDebitAmt = new javax.swing.JFormattedTextField();
        txtFCreditAmt = new javax.swing.JFormattedTextField();
        txtFOpening = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDepartment = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPerson = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRefrence = new javax.swing.JTextField();
        txtCurrency = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtBank = new javax.swing.JTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCash.setFont(Global.textFont);
        tblCash.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblCash.setToolTipText("");
        tblCash.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblCash.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblCash.setRowHeight(Global.tblRowHeight);
        tblScrollPane.setViewportView(tblCash);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Opening Balance");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Closing Balance");

        txtFClosing.setEditable(false);
        txtFClosing.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFClosing.setFont(Global.amtFont);

        txtFDebitAmt.setEditable(false);
        txtFDebitAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFDebitAmt.setFont(Global.amtFont);

        txtFCreditAmt.setEditable(false);
        txtFCreditAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFCreditAmt.setFont(Global.amtFont);

        txtFOpening.setEditable(false);
        txtFOpening.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFOpening.setFont(Global.amtFont);
        txtFOpening.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFOpeningActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtFClosing, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFOpening, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 315, Short.MAX_VALUE)
                .addComponent(txtFDebitAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFCreditAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFCreditAmt, txtFDebitAmt});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFDebitAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFCreditAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFOpening, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtFClosing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter Bar"));
        jPanel2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel2formComponentShown(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date");

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

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Department");

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

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Person");

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

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Account");

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

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Description");

        txtDesp.setFont(Global.textFont);
        txtDesp.setName("txtDesp"); // NOI18N
        txtDesp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDespFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDespFocusLost(evt);
            }
        });
        txtDesp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDespActionPerformed(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Refrence");

        txtRefrence.setFont(Global.textFont);
        txtRefrence.setName("txtRefrence"); // NOI18N
        txtRefrence.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRefrenceFocusGained(evt);
            }
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
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
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

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Currency");

        jLabel10.setText("Bank");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(txtBank))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                    .addComponent(txtDepartment))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPerson)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAccount)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDesp)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRefrence)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel5)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tblScrollPane)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        mainFrame.setFilterObserver(this);
        if (!isShown) {
            initMain();
        } else {
            //requestFoucsTable();
        }
        //searchCash();

    }//GEN-LAST:event_formComponentShown

    private void txtFOpeningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFOpeningActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFOpeningActionPerformed

    private void txtDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusGained
        // TODO add your handling code here:
        txtDate.selectAll();
    }//GEN-LAST:event_txtDateFocusGained

    private void txtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusLost
        // TODO add your handling code here:
        //dateAutoCompleter.closePopup();
        //messageBean.setValue(txtDate.getText());
    }//GEN-LAST:event_txtDateFocusLost

    private void txtDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateActionPerformed

    private void txtDepartmentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepartmentFocusGained
        // TODO add your handling code here:
        txtDepartment.selectAll();
        //departmentAutoCompleter.showPopup();
    }//GEN-LAST:event_txtDepartmentFocusGained

    private void txtDepartmentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDepartmentFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDepartmentFocusLost

    private void txtDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepartmentActionPerformed
        // TODO add your handling code here:
        if (txtDepartment.getText().isEmpty()) {
            selectionObserver.selected("Department", "-");
        }
    }//GEN-LAST:event_txtDepartmentActionPerformed

    private void txtPersonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPersonFocusGained
        // TODO add your handling code here:
        txtPerson.selectAll();
        //traderAutoCompleter.showPopup();
    }//GEN-LAST:event_txtPersonFocusGained

    private void txtPersonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPersonFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPersonFocusLost

    private void txtPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPersonActionPerformed
        // TODO add your handling code here:
        if (txtPerson.getText().isEmpty()) {
            selectionObserver.selected("Trader", "-");
        }
    }//GEN-LAST:event_txtPersonActionPerformed

    private void txtAccountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusGained
        // TODO add your handling code here:

        txtAccount.selectAll();
        //coaAutoCompleter.showPopup();
    }//GEN-LAST:event_txtAccountFocusGained

    private void txtAccountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAccountFocusLost
        // TODO add your handling code here:
        //coaAutoCompleter.closePopup();
    }//GEN-LAST:event_txtAccountFocusLost

    private void txtAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAccountActionPerformed
        // TODO add your handling code here:
        if (txtAccount.getText().isEmpty()) {
            selectionObserver.selected("COA", "-");
        }
    }//GEN-LAST:event_txtAccountActionPerformed

    private void txtDespFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDespFocusGained
        // TODO add your handling code here:
        txtDesp.selectAll();
    }//GEN-LAST:event_txtDespFocusGained

    private void txtDespFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDespFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDespFocusLost

    private void txtDespActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDespActionPerformed
        // TODO add your handling code here:
        selectionObserver.selected("Description", Util1.isNull(txtDesp.getText(), "-"));
    }//GEN-LAST:event_txtDespActionPerformed

    private void txtRefrenceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRefrenceFocusGained
        // TODO add your handling code here:
        txtRefrence.selectAll();
    }//GEN-LAST:event_txtRefrenceFocusGained

    private void txtRefrenceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRefrenceFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRefrenceFocusLost

    private void txtRefrenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefrenceActionPerformed
        // TODO add your handling code here:
        selectionObserver.selected("Ref", Util1.isNull(txtRefrence.getText(), "-"));
    }//GEN-LAST:event_txtRefrenceActionPerformed

    private void txtCurrencyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrencyFocusGained
        // TODO add your handling code here:

        txtCurrency.selectAll();
        // currencyAutoCompleter.showPopup();
    }//GEN-LAST:event_txtCurrencyFocusGained

    private void txtCurrencyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCurrencyFocusLost
        // TODO add your handling code here:
        //currencyAutoCompleter.closePopup();
    }//GEN-LAST:event_txtCurrencyFocusLost

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
        if (txtCurrency.getText().isEmpty()) {
            selectionObserver.selected("Currency", "-");
        }
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void jPanel2formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel2formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel2formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTable tblCash;
    private javax.swing.JScrollPane tblScrollPane;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtBank;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JTextField txtDepartment;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtFClosing;
    private javax.swing.JFormattedTextField txtFCreditAmt;
    private javax.swing.JFormattedTextField txtFDebitAmt;
    private javax.swing.JFormattedTextField txtFOpening;
    private javax.swing.JTextField txtPerson;
    private javax.swing.JTextField txtRefrence;
    // End of variables declaration//GEN-END:variables

    private void calDebitCredit() {
        List<VGl> listVGl = allCashTableModel.getListVGl();
        //LOGGER.info("calDebitCredit Thread Started.");
        double dbAmt = 0.0;
        double cdAmt = 0.0;
        for (VGl vgl : listVGl) {
            dbAmt += Util1.getDouble(vgl.getDrAmt());
            cdAmt += Util1.getDouble(vgl.getCrAmt());
        }
        txtFDebitAmt.setValue(dbAmt);
        txtFCreditAmt.setValue(cdAmt);
        double closing = Util1.getDouble((Double) txtFOpening.getValue()) + dbAmt - cdAmt;
        txtFClosing.setValue(closing);

    }

    private void calOpeningClosing() {
        LOGGER.info("Start calculate opening.");
        String opDate = Global.finicialPeriodFrom;
        try {
            if (stDate.equals("-")) {
                stDate = opDate;
            }
            List<TmpOpeningClosing> opBalanceGL = coaOpDService.getOpBalanceGL1(sourceAccId, opDate, stDate, 3, "MMK",
                    Global.loginUser.getAppUserCode(),
                    Util1.isNull(depId, "-"), Global.machineId.toString());
            LOGGER.info("End calculate opening.");
            if (!opBalanceGL.isEmpty()) {
                TmpOpeningClosing tmpOC = opBalanceGL.get(0);
                txtFOpening.setValue(tmpOC.getOpening());
            }
            calDebitCredit();
        } catch (Exception ex) {
            LOGGER.error("TmpOpeningClosing" + ex.getMessage());
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            String name = source.toString();
            switch (name) {
                case "tabToCashTable":
                    tblCash.requestFocus();
                    if (tblCash.getRowCount() >= 0) {
                        tblCash.setRowSelectionInterval(0, 0);
                    }
                    break;
                case "Date":
                    String[] split = selectObj.toString().split("to");
                    stDate = split[0];
                    enDate = split[1];
                    searchValidation(stDate);
                    break;
                case "Department":
                    depId = selectObj.toString();
                    searchValidation(depId);
                    break;
                case "COA":
                    accId = selectObj.toString();
                    searchValidation(accId);
                    break;
                case "Currency":
                    currency = selectObj.toString();
                    searchValidation(currency);
                    break;
                case "Trader":
                    if (selectObj instanceof Trader) {
                        Trader trader = (Trader) selectObj;
                        switch (trader.getTraderName()) {
                            case "All Customer":
                                traderType = "CUS";
                                break;
                            case "All Supplier":
                                traderType = "SUP";
                                break;
                            default:
                                traderType = "-";
                                break;
                        }
                        traderCode = trader.getCode();
                    }
                    searchValidation(traderCode);
                    break;
                case "Description":
                    desp = selectObj.toString();
                    searchValidation(desp);
                    break;
                case "Ref":
                    ref = selectObj.toString();
                    searchValidation(ref);
                    break;
                case "CAL-TOTAL":
                    calDebitCredit();
                    break;
            }

        }

    }

    private void searchValidation(String str) {

        searchCash();
    }

    @Override
    public void save() {

    }

    @Override
    public void delete() {
        deleteVoucher();
    }

    @Override
    public void newForm() {
        clearFilter();
        isShown = false;
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
        printVoucher();
    }

    @Override
    public void refresh() {
        searchCash();
    }

    @Override
    public void sendFilter(String filter) {
        if (filterHeader.isVisible()) {
            filterHeader.setVisible(false);
        } else {
            filterHeader.setVisible(true);
        }
    }

}
