/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.FilterObserver;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.COAOpening;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.view.VCOAOpening;
import com.cv.accountswing.service.COAOpeningService;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.OpeningService;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.util.Util1;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import com.cv.accountswing.service.VOpeningService;
import com.cv.accountswing.ui.editor.COACellEditor;
import com.cv.accountswing.ui.editor.COAL2AutoCompleter;
import com.cv.accountswing.ui.editor.CurrencyEditor;
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.ui.editor.RegionAutoCompleter;
import com.cv.accountswing.ui.editor.TraderCellEditor;
import com.cv.accountswing.ui.setup.common.OpeningTableModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.awt.HeadlessException;
import java.io.File;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

/**
 *
 * @author Lenovo
 */
@Component
public class COAOpeningSetup extends javax.swing.JPanel implements SelectionObserver,
        KeyListener, PanelControl, FilterObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(COAOpeningSetup.class);
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private final ImageIcon process = new ImageIcon(this.getClass().getResource("/images/process.gif"));
    @Autowired
    private OpeningTableModel openingTableModel;
    @Autowired
    private VOpeningService openingService;
    @Autowired
    private COAOpeningService cOAOpeningService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private COAService cOAService;
    @Autowired
    private COAOptionDialog cOAOptionDialog;
    @Autowired
    private OpeningService opService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ReportService rService;
    private TableFilterHeader filterHeader;
    private LoadingObserver loadingObserver;
    private boolean isShown = false;
    private boolean isSearch = false;
    private String stDate;
    private String endDate;
    private String curId;
    private String depCode;
    private String regionCode = "-";
    private String coaParent = "-";
    private DepartmentAutoCompleter departmentAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    /**
     * Creates new form COAOpeningSetup
     */
    public COAOpeningSetup() {
        initComponents();
    }

    private void initMain() {
        txtDate.setDate(Util1.toDate(Global.finicialPeriodFrom));
        initKeyListener();
        initCombo();
        initTable();
        searchOpening();
        isShown = true;
    }

    private void initCombo() {
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, Global.listDepartment, null, true);
        departmentAutoCompleter.setSelectionObserver(this);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        currencyAutoCompleter.setCurrency(Global.defalutCurrency);
        List<ChartOfAccount> listCOA = cOAService.search("-", "-", Global.compCode, "2", "-", "-", "-");
        COAL2AutoCompleter coaAutoCompleter = new COAL2AutoCompleter(txtCOA, listCOA, null);
        coaAutoCompleter.setSelectionObserver(this);
        RegionAutoCompleter regionAutoCompleter = new RegionAutoCompleter(txtRegion, Global.listRegion, null);
        regionAutoCompleter.setSelectionObserver(this);
    }

    private void initTable() {
        openingTableModel.setOpDate(txtDate);
        openingTableModel.setDepAutoCompleter(departmentAutoCompleter);
        tblOpening.setModel(openingTableModel);
        openingTableModel.setSelectionObserver(this);
        openingTableModel.setParent(tblOpening);
        tblOpening.getTableHeader().setFont(Global.textFont);
        tblOpening.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblOpening.getTableHeader().setForeground(ColorUtil.foreground);
        tblOpening.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOpening.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblOpening.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblOpening.getColumnModel().getColumn(2).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(3).setPreferredWidth(250);
        tblOpening.getColumnModel().getColumn(4).setPreferredWidth(5);
        tblOpening.getColumnModel().getColumn(5).setPreferredWidth(10);
        tblOpening.getColumnModel().getColumn(6).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(7).setPreferredWidth(20);
        tblOpening.getColumnModel().getColumn(0).setCellEditor(new COACellEditor(false));
        tblOpening.getColumnModel().getColumn(2).setCellEditor(new TraderCellEditor(false, 0));
        tblOpening.getColumnModel().getColumn(4).setCellEditor(new DepartmentCellEditor(false));
        tblOpening.getColumnModel().getColumn(5).setCellEditor(new CurrencyEditor());
        tblOpening.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblOpening.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblOpening.setDefaultRenderer(Double.class, new TableCellRender());
        tblOpening.setDefaultRenderer(Object.class, new TableCellRender());
        tblOpening.setCellSelectionEnabled(true);
        tblOpening.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        filterHeader = new TableFilterHeader(tblOpening, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);
    }

    private void searchOpening() {
        if (!isSearch) {
            isSearch = true;
            initializeParameter();
            loadingObserver.load(this.getName(), "Start");

            final String traderType;
            if (chkCustomer.isSelected() && chkSupplier.isSelected()) {
                traderType = "CUSSUP";
            } else if (chkCustomer.isSelected() && chkCOA.isSelected()) {
                traderType = "CUSCOA";
            } else if (chkSupplier.isSelected() && chkCOA.isSelected()) {
                traderType = "SUPCOA";
            } else if (chkCustomer.isSelected()) {
                traderType = "CUS";
            } else if (chkSupplier.isSelected()) {
                traderType = "SUP";
            } else if (chkCOA.isSelected()) {
                traderType = "COA";
            } else {
                traderType = "-";
            }
            taskExecutor.execute(() -> {
                openingTableModel.clear();
                List<VCOAOpening> listOpening = openingService.search(stDate, "-",
                        "-", Global.compCode, depCode, curId, traderType, coaParent,
                        regionCode);
                openingTableModel.setListOpening(listOpening);
                btnGen.setEnabled(listOpening.isEmpty());
                openingTableModel.addNewRow();
                calTotalAmt(listOpening);
                //btnGen.setEnabled(false);
                loadingObserver.load(this.getName(), "Stop");
                isSearch = false;
            });
        }
    }

    private void initializeParameter() {
        stDate = Global.finicialPeriodFrom;
        endDate = Util1.toDateStr(txtDate.getDate(), "dd/MM/yyyy");
        depCode = departmentAutoCompleter.getDepartment() == null ? "-" : departmentAutoCompleter.getDepartment().getDeptCode();
        curId = currencyAutoCompleter.getCurrency() == null ? "-" : currencyAutoCompleter.getCurrency().getKey().getCode();
        if (txtRegion.getText().isEmpty()) {
            regionCode = "-";
        }
        if (txtCOA.getText().isEmpty()) {
            coaParent = "-";
        }
        btnGen.setEnabled(false);
    }

    private void calTotalAmt(List<VCOAOpening> listVGl) {
        double drAmt = 0.0;
        double crAmt = 0.0;
        for (VCOAOpening vgl : listVGl) {
            drAmt += Util1.getDouble(vgl.getDrAmt());
            crAmt += Util1.getDouble(vgl.getCrAmt());
        }
        txtFCrAmt.setValue(crAmt);
        txtFDrAmt.setValue(drAmt);
        txtOB.setValue(drAmt - crAmt);
    }

    private boolean isValidGen() {
        boolean status = true;
        if (departmentAutoCompleter.getDepartment() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Select Department.");
            txtDep.requestFocus();
            status = false;
        }
        if (currencyAutoCompleter.getCurrency() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Select Currency.");
            txtCurrency.requestFocus();
            status = false;
        }
        return status;
    }

    private void selectOption() {
        if (isValidGen()) {
            cOAOptionDialog.initTable();
            cOAOptionDialog.setObserver(this);
            cOAOptionDialog.setSize(Global.width - 350, Global.height - 350);
            cOAOptionDialog.setLocationRelativeTo(null);
            cOAOptionDialog.setVisible(true);
        }
    }

    private void generate(String coaGroup) {
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            btnGen.setEnabled(false);
            try {
                String userCode = Global.loginUser.getAppUserCode();
                if (coaGroup != null) {
                    cOAOpeningService.generateZeroOpening(Util1.toDateStr(txtDate.getDate(), "dd/MM/yyyy"),
                            userCode, Global.compCode, curId, depCode, coaGroup);
                    searchOpening();
                    btnGen.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(Global.parentForm, "COA Group Openning in System Property.");
                }
            } catch (Exception ex) {
                LOGGER.error("GENERATE  :" + ex.getMessage());
                JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage(), "GENERATE OPENING", JOptionPane.ERROR_MESSAGE);
                btnGen.setEnabled(true);
            }
        });

    }

    private void initKeyListener() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDep.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        btnGen.addKeyListener(this);
        tblOpening.addKeyListener(this);

    }

    private void saveOpening() {
        try {
            if (isValidEntry()) {
                JDialog loading = Util1.getLoading(Global.parentForm, process);
                taskExecutor.execute(() -> {
                    List<VCOAOpening> listVO = openingTableModel.getListOpening();
                    listVO.stream().map(voa -> gson.fromJson(gson.toJson(voa), COAOpening.class)).map(c -> {
                        if (Util1.isNull(c.getTranSource())) {
                            c.setDrAmt(Util1.getDouble(c.getDrAmt()));
                            c.setCrAmt(Util1.getDouble(c.getCrAmt()));
                            c.setTranSource("OPENING");
                        }
                        return c;
                    }).forEachOrdered(c -> {
                        if (!Util1.isNull(c.getSourceAccId())
                                && !Util1.isNull(c.getDepCode())
                                && !Util1.isNull(c.getCurCode())) {
                            opService.save(c);
                        }
                    });
                    loading.setVisible(false);
                    JOptionPane.showMessageDialog(Global.parentForm, "Saved Opening.");
                });
                loading.setVisible(true);
            }
        } catch (JsonSyntaxException | HeadlessException e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Save Opening Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private boolean isValidEntry() {
        boolean status = true;
        if (Util1.getFloat(txtOB.getValue()) != 0) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Out of balance");
        }
        if (!openingTableModel.isIsValid()) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Can't Saved.");
        }
        return status;
    }

    private void printVoucher() throws Exception {
        String depId = departmentAutoCompleter.getDepartment() == null ? "-" : departmentAutoCompleter.getDepartment().getDeptCode();
        String depName = depId.equals("-") ? "All" : departmentService.findById(depId).getDeptName();
        String date = Util1.toDateStr(txtDate.getDate(), "yyyy-MM-dd");
        String reportPath = Global.sysProperties.get("system.report.path");
        //String reportPath1 = reportPath;
        String fontPath = Global.sysProperties.get("system.font.path");
        String filePath = reportPath + File.separator + "OpeningTri";
        Map<String, Object> parameters = new HashMap();
        parameters.put("p_company_name", Global.companyName);
        parameters.put("p_comp_code", Global.compCode);
        parameters.put("p_report_info", "Opening Date - " + Util1.toDateStr(date, "yyyy-MM-dd", "dd/MM/yyyy"));
        parameters.put("p_op_date", date);
        parameters.put("p_dept_code", depId);
        parameters.put("p_dept_name", "Dept : " + depName);
        rService.genReport(filePath, filePath, fontPath, parameters);
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
        jLabel2 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        btnGen = new javax.swing.JButton();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txtRegion = new javax.swing.JTextField();
        chkCustomer = new javax.swing.JCheckBox();
        chkSupplier = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        txtCOA = new javax.swing.JTextField();
        chkCOA = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOpening = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtFDrAmt = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtFCrAmt = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtOB = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtDep"); // NOI18N
        txtDep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDepActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

        txtCurrency.setEditable(false);
        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setEnabled(false);
        txtCurrency.setName("txtCurrency"); // NOI18N
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        btnGen.setBackground(ColorUtil.mainColor);
        btnGen.setFont(Global.lableFont);
        btnGen.setForeground(ColorUtil.foreground);
        btnGen.setText("Genearte Zero");
        btnGen.setEnabled(false);
        btnGen.setName("btnGen"); // NOI18N
        btnGen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenActionPerformed(evt);
            }
        });

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.shortCutFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Region");

        txtRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRegionActionPerformed(evt);
            }
        });

        chkCustomer.setText("Customer");
        chkCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCustomerActionPerformed(evt);
            }
        });

        chkSupplier.setText("Supplier");
        chkSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSupplierActionPerformed(evt);
            }
        });

        jLabel8.setText("COA");

        txtCOA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCOAActionPerformed(evt);
            }
        });

        chkCOA.setText("COA");
        chkCOA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCOAActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDep)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCurrency)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRegion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCOA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCustomer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkSupplier)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkCOA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGen)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnGen)
                        .addComponent(jLabel7)
                        .addComponent(txtRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkCustomer)
                        .addComponent(chkSupplier)
                        .addComponent(jLabel8)
                        .addComponent(txtCOA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkCOA)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblOpening.setFont(Global.textFont);
        tblOpening.setModel(new javax.swing.table.DefaultTableModel(
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
        tblOpening.setName("tblOpening"); // NOI18N
        tblOpening.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblOpening);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Dr-Amt");

        txtFDrAmt.setEditable(false);
        txtFDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFDrAmt.setFont(Global.amtFont);
        txtFDrAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFDrAmtActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Cr-Amt");

        txtFCrAmt.setEditable(false);
        txtFCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFCrAmt.setFont(Global.amtFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Out Of Balance");

        txtOB.setEditable(false);
        txtOB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOB.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFCrAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txtOB))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtFCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
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
    }//GEN-LAST:event_formComponentShown

    private void txtDepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDepActionPerformed
        // TODO add your handling code here:
        //searchOpening();
    }//GEN-LAST:event_txtDepActionPerformed

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
        //searchOpening();
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void btnGenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenActionPerformed
        // TODO add your handling code here:
        selectOption();
    }//GEN-LAST:event_btnGenActionPerformed

    private void txtRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRegionActionPerformed
        //searchOpening();
    }//GEN-LAST:event_txtRegionActionPerformed

    private void txtCOAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCOAActionPerformed
        //searchOpening();
    }//GEN-LAST:event_txtCOAActionPerformed

    private void chkCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCustomerActionPerformed
        searchOpening();
    }//GEN-LAST:event_chkCustomerActionPerformed

    private void chkSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSupplierActionPerformed
        searchOpening();
    }//GEN-LAST:event_chkSupplierActionPerformed

    private void chkCOAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCOAActionPerformed
        searchOpening();
    }//GEN-LAST:event_chkCOAActionPerformed

    private void txtFDrAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFDrAmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFDrAmtActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGen;
    private javax.swing.JCheckBox chkCOA;
    private javax.swing.JCheckBox chkCustomer;
    private javax.swing.JCheckBox chkSupplier;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOpening;
    private javax.swing.JTextField txtCOA;
    private javax.swing.JTextField txtCurrency;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDep;
    private javax.swing.JFormattedTextField txtFCrAmt;
    private javax.swing.JFormattedTextField txtFDrAmt;
    private javax.swing.JFormattedTextField txtOB;
    private javax.swing.JTextField txtRegion;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        LOGGER.info("COA OPENING INTERFACE " + source.toString());
        String name = source.toString();
        switch (name) {
            case "Department":
                depCode = selectObj.toString();
                searchOpening();
                break;
            case "Currency":
                curId = selectObj.toString();
                searchOpening();
                break;
            case "CAL-TOTAL":
                calTotalAmt(openingTableModel.getListOpening());
                break;
            case "RegionList":
                regionCode = selectObj.toString();
                searchOpening();
                break;
            case "COAL2":
                coaParent = selectObj.toString();
                searchOpening();
                break;
            case "COA-GROUP":
                LOGGER.info("COA Group : " + selectObj.toString());
                generate(selectObj.toString());
                break;

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
        Object sourceObj = e.getSource();
        String ctrlName = "-";
        if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTextFieldDateEditor) {
            ctrlName = ((JTextFieldDateEditor) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDep.requestFocus();

                }
                tabToTable(e);
                break;
            case "txtDep":
                /*if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                txtCurrency.requestFocus();
                }*/
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                        txtCurrency.requestFocus();
                        break;
                    /*case KeyEvent.VK_ENTER:
                        txtCurrency.requestFocus();
                        break;*/
                    case KeyEvent.VK_LEFT:
                        txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                        break;
                }
                tabToTable(e);

                break;
            case "txtCurrency":
                /*if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                btnGen.requestFocus();
                }*/
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDep.requestFocus();
                }
                tabToTable(e);

                break;
            case "btnGen":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtCurrency.requestFocus();
                }
                tabToTable(e);
                break;
            case "tblOpening":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblOpening.requestFocus();
            if (tblOpening.getRowCount() >= 0) {
                tblOpening.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void save() {
        saveOpening();
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
        isShown = false;
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
        try {
            printVoucher();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
        }
    }

    @Override
    public void refresh() {
        searchOpening();
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
