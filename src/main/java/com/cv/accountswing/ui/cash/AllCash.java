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
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.temp.TmpOpeningClosing;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.COAOpeningDService;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.service.RoleStatusService;
import com.cv.accountswing.service.VDescriptionService;
import com.cv.accountswing.service.VGlService;
import com.cv.accountswing.service.VRefService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.editor.CurrencyEditor;
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.ui.cash.common.AllCashTableModel;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.COACellEditor;
import com.cv.accountswing.ui.editor.DespEditor;
import com.cv.accountswing.ui.editor.RefCellEditor;
import com.cv.accountswing.ui.editor.TraderCellEditor;
import com.cv.accountswing.ui.filter.FilterPanel;
import com.cv.accountswing.util.Util1;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
import javax.swing.table.TableColumn;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.IFilterEditor;
import net.coderazzi.filters.gui.IFilterHeaderObserver;
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
public class AllCash extends javax.swing.JPanel implements SelectionObserver,
        PanelControl, FilterObserver {

    private static final Logger log = LoggerFactory.getLogger(AllCash.class);
    private String stDate;
    private String enDate;
    private String desp = "";
    private String accCode;
    private String ref = "";
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
    private AllCashTableModel allCashTableModel;
    @Autowired
    private COAOpeningDService coaOpDService;
    @Autowired
    private ReportService rService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private VRefService refService;
    @Autowired
    private VDescriptionService descriptionService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private RoleStatusService statusService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private COAService coaService;
    private SelectionObserver selectionObserver;
    private LoadingObserver loadingObserver;
    private ReloadData reloadData;
    private String sourceAccId;
    private final JPopupMenu popupmenu = new JPopupMenu();
    private final JLabel lblMessage = new JLabel();
    private boolean isShown = false;
    private TableFilterHeader filterHeader;
    private boolean isSearch = false;

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
        log.info("Source Id :" + sourceAccId);

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
    public AllCash() {
        initComponents();
        initPopup();
        initMouseLisener();
    }

    public AllCash newInstance() {
        return applicationContext.getBean(AllCash.class);
    }

    private void initMain() {
        filterPanel();
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
        try {
            int row = allCashTableModel.getListSize() - 1;
            if (row >= 0) {
                tblCash.changeSelection(row, 0, false, false);
                tblCash.requestFocus();
            }
        } catch (Exception e) {
        }
    }

    private void initTable() {
        allCashTableModel.setTxtDate(filterPanel.getTxtDate());
        allCashTableModel.setDateAutoCompleter(filterPanel.getDateAutoCompleter());
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
        tblCash.getColumnModel().getColumn(0).setPreferredWidth(20);// Date
        tblCash.getColumnModel().getColumn(1).setPreferredWidth(13);// Department
        tblCash.getColumnModel().getColumn(2).setPreferredWidth(180);// Description      
        tblCash.getColumnModel().getColumn(3).setPreferredWidth(180);// Ref  
        tblCash.getColumnModel().getColumn(4).setPreferredWidth(90);// Ref  
        tblCash.getColumnModel().getColumn(5).setPreferredWidth(90);// Person
        tblCash.getColumnModel().getColumn(6).setPreferredWidth(150);// Account
        tblCash.getColumnModel().getColumn(7).setPreferredWidth(1);// Curr      
        tblCash.getColumnModel().getColumn(8).setPreferredWidth(90);// Dr-Amt   
        tblCash.getColumnModel().getColumn(9).setPreferredWidth(90);// Cr-Amt  
        tblCash.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(1).setCellEditor(new DepartmentCellEditor(false));
        tblCash.getColumnModel().getColumn(2).setCellEditor(new DespEditor(descriptionService));
        tblCash.getColumnModel().getColumn(3).setCellEditor(new RefCellEditor(refService));
        tblCash.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(5).setCellEditor(new TraderCellEditor(false, 2));
        tblCash.getColumnModel().getColumn(6).setCellEditor(new COACellEditor(false));
        tblCash.getColumnModel().getColumn(7).setCellEditor(new CurrencyEditor());
        tblCash.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());
        tblCash.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());

        tblCash.setDefaultRenderer(Double.class, new TableCellRender());
        tblCash.setDefaultRenderer(Object.class, new TableCellRender());

        tblCash.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == 222) {
                    allCashTableModel.copyRow();
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
        double drAmt = 0;
        double crAmt = 0;
        for (int row = 0; row < tblCash.getRowCount(); row++) {
            drAmt += Util1.getDouble(tblCash.getValueAt(row, 8));
            crAmt += Util1.getDouble(tblCash.getValueAt(row, 9));
        }
        double closing = Util1.getDouble(Util1.getDouble(txtFOpening.getValue()) + drAmt - crAmt);
        txtFCreditAmt.setValue(crAmt);
        txtFDebitAmt.setValue(drAmt);
        txtFClosing.setValue(closing);
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

    private void filterPanel() {
        panelFilter.setLayout(new BorderLayout());
        filterPanel.setSelectionObserver(this);
        filterPanel.initMain();
        panelFilter.add(filterPanel, BorderLayout.CENTER);
        panelFilter.revalidate();
        panelFilter.repaint();

    }

    private final Action actionItemDeleteExp = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteVoucher();
        }
    };

    private void closeCellEditor() {
        if (tblCash.getCellEditor() != null) {
            tblCash.getCellEditor().stopCellEditing();
        }
    }

    private void deleteVoucher() {
        closeCellEditor();
        int selectRow = tblCash.convertRowIndexToModel(tblCash.getSelectedRow());
        int yes_no;
        if (tblCash.getSelectedRow() >= 0) {
            VGl vgl = allCashTableModel.getVGl(selectRow);
            if (vgl.getGlCode() != null) {
                yes_no = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete?",
                        "Delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    if (statusService.checkPermission(Global.roleCode, Global.CB_DEL_KEY)) {
                        allCashTableModel.deleteVGl(selectRow);
                    } else if (statusService.checkPermission(Global.roleCode, Global.CB_DEL_USR_KEY)) {
                        if (Util1.isNull(vgl.getCreatedBy(), "-").equals(Global.loginUser.getAppUserCode())) {
                            allCashTableModel.deleteVGl(selectRow);
                        } else {
                            pdDailog();
                        }
                    } else {
                        pdDailog();
                    }
                    calDebitCredit();
                }
            }
        }
    }

    private void pdDailog() {
        JOptionPane.showMessageDialog(Global.parentForm,
                "Your have no permission to delete.",
                "Permission Denied", JOptionPane.WARNING_MESSAGE);
    }

    public void printVoucher() {
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            try {
                String date = filterPanel.getTxtDate().getText();
                String depName = depId.equals("-") ? "All" : departmentService.findById(depId).getDeptName();
                String reportPath = Global.sysProperties.get("system.report.path");
                //String reportPath1 = reportPath;
                String fontPath = Global.sysProperties.get("system.font.path");
                String filePath = reportPath + File.separator + "LedgerReport";
                Map<String, Object> parameters = new HashMap();
                parameters.put("p_company_name", Global.companyName);
                parameters.put("p_comp_id", Global.compCode);
                parameters.put("p_trg_acc_id", sourceAccId);
                parameters.put("p_acc_id", Util1.isNull(accCode, "-"));
                parameters.put("p_report_info", date);
                parameters.put("p_from", Util1.toDateStrMYSQL(stDate, "dd/MM/yyyy"));
                parameters.put("p_to", Util1.toDateStrMYSQL(enDate, "dd/MM/yyyy"));
                parameters.put("p_desp", Util1.isNull(desp, "-"));
                parameters.put("p_from_curr", Util1.isNull(currency, "-"));
                parameters.put("p_to_curr", "-");
                parameters.put("p_ref", Util1.isNull(ref, "-"));
                parameters.put("p_dept", Util1.isNull(depId, "-"));
                parameters.put("p_vou_no", "-");
                parameters.put("p_cv_id", Util1.isNull(accCode, "-"));
                parameters.put("p_report_name", this.getName());
                parameters.put("p_opening", txtFOpening.getValue());
                parameters.put("p_closing", txtFClosing.getValue());
                parameters.put("p_dept_name", "Dept : " + depName);
                rService.genReport(filePath, filePath, fontPath, parameters);
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception ex) {
                log.error("getLedgerReport : " + ex.getMessage());

            }
        });

    }

    private void searchCash() {
        try {
            if (!isSearch) {
                isSearch = true;
                initializeParameter();
                if (sourceAccId != null) {
                    loadingObserver.load(this.getName(), "Start");
                    taskExecutor.execute(() -> {
                        log.info(this.getName() + "Start Date  :" + stDate + "-" + "End Date :" + enDate);
                        List<VGl> listVGl = vGlService.search(stDate, enDate,
                                desp, sourceAccId,
                                getCOACode(accCode, Global.compCode), currency, "-",
                                ref, depId, "-", traderCode,
                                "-", "-", "-", "-", "-",
                                "-", "-", "-",
                                traderType,
                                "-");
                        log.info("Search Cash Book End ...");
                        swapData(listVGl, sourceAccId);
                        allCashTableModel.setListVGl(listVGl);
                        allCashTableModel.addNewRow();
                        requestFoucsTable();
                        calOpeningClosing();
                        isSearch = false;
                        loadingObserver.load(this.getName(), "Stop");
                    });
                } else {
                    JOptionPane.showMessageDialog(Global.parentForm, "Source Account Missing.");
                }
            }
        } catch (HeadlessException e) {
            log.error("Search Cash :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Searching Cash", JOptionPane.ERROR_MESSAGE);
        }

    }

    public String getCOACode(String code, String compCode) {
        String tmp = "-";
        List<ChartOfAccount> listCoA = coaService.getAllChild(code, Global.compCode);
        if (!listCoA.isEmpty()) {
            tmp = "";
            tmp = listCoA.stream().map(coa -> String.format("'%s',", coa.getCode())).reduce(tmp, String::concat);
        }
        tmp = tmp.substring(0, tmp.length() - 1);
        return Util1.isNull(tmp, "-");
    }

    private void initializeParameter() {
        stDate = Util1.isNull(stDate, Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy"));
        enDate = Util1.isNull(enDate, Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy"));
        desp = desp.equals("All") ? "-" : Util1.isNull(desp, "-");
        accCode = Util1.isNull(accCode, "-");
        currency = Util1.isNull(currency, "-");
        ref = ref.equals("All") ? "-" : Util1.isNull(ref, "-");
        depId = Util1.isNull(depId, "-");
        traderCode = Util1.isNull(traderCode, "-");
        traderType = Util1.isNull(traderType, "-");
        clearTextBox();
    }

    public void clearFilter() {
        stDate = null;
        enDate = null;
        desp = "";
        accCode = null;
        currency = null;
        ref = "";
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
                String accCode = Util1.isNull(vgl.getAccountId(), "-");
                if (sourceAcId.equals(targetId)) {
                } else if (accCode.equals(targetId)) {
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

        panelFilter = new javax.swing.JPanel();
        tblScrollPane = new javax.swing.JScrollPane();
        tblCash = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtFClosing = new javax.swing.JFormattedTextField();
        txtFDebitAmt = new javax.swing.JFormattedTextField();
        txtFCreditAmt = new javax.swing.JFormattedTextField();
        txtFOpening = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter);
        panelFilter.setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 221, Short.MAX_VALUE)
                .addComponent(txtFDebitAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFCreditAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFCreditAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFDebitAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFOpening, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtFClosing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tblScrollPane))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JTable tblCash;
    private javax.swing.JScrollPane tblScrollPane;
    private javax.swing.JFormattedTextField txtFClosing;
    private javax.swing.JFormattedTextField txtFCreditAmt;
    private javax.swing.JFormattedTextField txtFDebitAmt;
    private javax.swing.JFormattedTextField txtFOpening;
    // End of variables declaration//GEN-END:variables

    private void calDebitCredit() {
        List<VGl> listVGl = allCashTableModel.getListVGl();
        //log.info("calDebitCredit Thread Started.");
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
        log.info("Start calculate opening.");
        String opDate = Global.finicialPeriodFrom;
        try {
            if (stDate.equals("-")) {
                stDate = opDate;
            }
            List<TmpOpeningClosing> opBalanceGL = coaOpDService.getOpBalanceGL1(sourceAccId, opDate, stDate, 3, "MMK",
                    Global.loginUser.getAppUserCode(),
                    Util1.isNull(depId, "-"), Global.machineId.toString());
            log.info("End calculate opening.");
            if (!opBalanceGL.isEmpty()) {
                TmpOpeningClosing tmpOC = opBalanceGL.get(0);
                txtFOpening.setValue(tmpOC.getOpening());
            }
            calDebitCredit();
        } catch (Exception ex) {
            log.error("TmpOpeningClosing" + ex.getMessage());
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
                    accCode = selectObj.toString();
                    searchValidation(accCode);
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
