/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.journal;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.CompanyInfo;
import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.entity.SystemProperty;
import com.cv.accountswing.entity.SystemPropertyKey;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.CompanyInfoService;
import com.cv.accountswing.service.GlService;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.service.SeqTableService;
import com.cv.accountswing.service.SystemPropertyService;
import com.cv.accountswing.service.VGlService;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.COAAutoCompleter;
import com.cv.accountswing.ui.editor.COACellEditor;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.editor.TraderCellEditor;
import com.cv.accountswing.ui.journal.common.CrDrVoucherEntryTableModel;
import com.cv.accountswing.util.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
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
public class CrDrVoucherEntry extends javax.swing.JDialog implements KeyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrDrVoucherEntry.class);
    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private int selectRow = -1;
    @Autowired
    private CrDrVoucherEntryTableModel voucherEntryTableModel;
    @Autowired
    private GlService glService;
    @Autowired
    private VGlService vglService;
    @Autowired
    private SeqTableService seqService;
    @Autowired
    private SystemPropertyService spService;
    @Autowired
    private ReportService rService;
    @Autowired
    private CompanyInfoService ciService;
    @Autowired
    private TaskExecutor taskExecutor;
    private JPopupMenu popupMenu;
    private String voucherType;
    private String depCode = "-";
    private String accCode = "-";
    private String currency = "-";
    private String deleteIds = "-";
    private String voucherId;
    private COAAutoCompleter cOAAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private CurrencyAutoCompleter currencyAutoCompleter;

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    /**
     * Creates new form CreditVoucherEntry
     */
    public CrDrVoucherEntry() {
        super(new JFrame(), false);
        initComponents();
        initKeyListener();
    }

    private void initMain() {
        initTable();
        searchVoucher();
        initCombo();
        setTodayDate();
        initPopup();
    }

    private void initKeyListener() {
        txtFromDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtFromDate.getDateEditor().getUiComponent().setName("txtFromDate");
        txtAccount.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        txtDep.addKeyListener(this);
        txtFrom.addKeyListener(this);
        txtNaration.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtVouNo.addKeyListener(this);
        tblCredit.addKeyListener(this);
        btnSave.addKeyListener(this);
    }

    private void searchVoucher() {
        taskExecutor.execute(() -> {
            if (!voucherId.equals("-")) {
                List<VGl> search = vglService.search("-", "-", "-", "-", "-", "-", "-", "-", "-", voucherId, "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-");
                voucherEntryTableModel.setListVGl(search);
                txtFromDate.setDate(search.get(0).getGlDate());
                txtVouNo.setText(search.get(0).getVouNo());
            }
        });

    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());

    }

    private void initTable() {
        voucherEntryTableModel.setVouType(voucherType);
        voucherEntryTableModel.setTxtFTotalAmt(txtFTotolAmt);
        tblCredit.setModel(voucherEntryTableModel);
        tblCredit.getTableHeader().setFont(Global.lableFont);
        tblCredit.setCellSelectionEnabled(true);
        tblCredit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCredit.getColumnModel().getColumn(0).setCellEditor(new TraderCellEditor());
        tblCredit.getColumnModel().getColumn(1).setCellEditor(new COACellEditor());
        tblCredit.getColumnModel().getColumn(0).setPreferredWidth(150);
        tblCredit.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblCredit.getColumnModel().getColumn(2).setPreferredWidth(130);
        tblCredit.getColumnModel().getColumn(0).setPreferredWidth(130);
        tblCredit.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblCredit.setDefaultRenderer(Double.class, new TableCellRender());
        tblCredit.setDefaultRenderer(Object.class, new TableCellRender());
        voucherEntryTableModel.setParent(tblCredit);
        voucherEntryTableModel.addEmptyRow();
        tblCredit.getInputMap().put(KeyStroke.getKeyStroke("F8"), "Del-Action");
        tblCredit.getActionMap().put("Del-Action", actionDel);
    }
    private final Action actionDel = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectRow = tblCredit.convertRowIndexToModel(tblCredit.getSelectedRow());
            VGl vGl = voucherEntryTableModel.getVGl(selectRow);
            if (vGl != null) {
                String glID = vGl.getGlId().toString();
                int showConfirmDialog = JOptionPane.showConfirmDialog(Global.parentForm, "Are you sure to delete");
                if (showConfirmDialog == 1) {
                    if (deleteIds.equals("-")) {
                        deleteIds = glID;
                    } else {
                        deleteIds = deleteIds + "," + glID;
                    }
                    LOGGER.info("Delete Ids :" + deleteIds);
                    voucherEntryTableModel.remove(selectRow);
                }

            }
        }
    };

    private void initCombo() {
        cOAAutoCompleter = new COAAutoCompleter(txtAccount, Global.listCOA, null);
        departmentAutoCompleter = new DepartmentAutoCompleter(txtDep, Global.listDepartment, null);
        currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
    }

    private void initPopup() {
        popupMenu = new JPopupMenu("Edit");
        JMenuItem closeAll = new JMenuItem("Print");
        closeAll.addActionListener((ActionEvent e) -> {
            printCrDrVOu();
        });
        popupMenu.add(closeAll);
        initMouseLisener();
    }

    private void initMouseLisener() {
        tblCredit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(tblCredit, e.getX(), e.getY());
                }
            }

        });
    }

    private void printCrDrVOu() {
        taskExecutor.execute(() -> {
            try {
                String vouNo = txtVouNo.getText();
                String userId = Global.loginUser.getUserId().toString();
                String type;
                if (voucherType.equals("CR")) {
                    type = "system.creditvoucher";
                } else {
                    type = "system.debitvoucher";
                }
                CompanyInfo ci = ciService.findById(Global.compId);
                SystemPropertyKey key = new SystemPropertyKey();
                key.setCompCode(Global.compId);
                key.setPropKey("system.report.path");
                SystemProperty sp = spService.findById(key);
                String fileName = userId + vouNo + ".pdf";
                String reportPath = sp.getPropValue();
                String imagePath = reportPath;
                String filePath = reportPath + "/temp/" + fileName;
                key = new SystemPropertyKey();
                key.setCompCode(Global.compId);
                key.setPropKey(type);
                sp = spService.findById(key);
                reportPath = reportPath + "\\" + sp.getPropValue();
                key = new SystemPropertyKey();
                key.setCompCode(Global.compId);
                key.setPropKey("system.font.path");
                sp = spService.findById(key);
                String fontPath = sp.getPropValue();

                Map<String, Object> parameters = new HashMap();
                parameters.put("p_vou_no", vouNo);
                parameters.put("p_company_name", ci.getName());
                parameters.put("p_comp_id", Global.compId);
                parameters.put("img_path", imagePath);

                rService.genCreditVoucher(reportPath, filePath, fontPath, parameters);

            } catch (Exception ex) {
                LOGGER.error("creditVoucher : " + ex);

            }
        });

    }

    private void saveCreditVoucher() {
        if (isValidVoucher()) {
            clear();
            JOptionPane.showMessageDialog(Global.parentForm, "Saved");
        }
    }

    private boolean isValidVoucher() {
        boolean status;
        if (accCode.equals("-")) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Account");
            txtAccount.requestFocus();
        } else if (depCode.equals("-")) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Department");
            txtDep.requestFocus();
        } else if (currency.equals("-")) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Currency");
            txtCurrency.requestFocus();
        } else {
            String strVoucher;
            List<VGl> listVGl = voucherEntryTableModel.getListVGl();
            if (!listVGl.isEmpty()) {
                listVGl.stream().map(vgl -> {
                    vgl.setGlDate(txtFromDate.getDate());
                    return vgl;
                }).map(vgl -> {
                    vgl.setSourceAcId(accCode);
                    return vgl;
                }).map(vgl -> {
                    vgl.setDeptId(depCode);
                    return vgl;
                }).map(vgl -> {
                    vgl.setFromCurId(currency);
                    return vgl;
                }).map(vgl -> {
                    vgl.setRemark(txtRemark.getText());
                    return vgl;
                }).map(vgl -> {
                    vgl.setDescription(txtFrom.getText());
                    return vgl;
                }).forEachOrdered(vgl -> {
                    vgl.setNaration(txtNaration.getText());
                });
                strVoucher = gson.toJson(listVGl);
                java.lang.reflect.Type listType = new TypeToken<List<Gl>>() {
                }.getType();
                List<Gl> listGV = gson.fromJson(strVoucher, listType);
                String strVouNo = txtVouNo.getText();
                if (strVouNo.isEmpty()) {
                    strVouNo = getVouNo(depCode, Util1.toDateStr(txtFromDate.getDate(), "dd/MM/yyyy"),
                            Global.compId.toString(), depCode, voucherType);
                }

                for (Gl gl : listGV) {
                    gl.setVouNo(strVouNo);
                    switch (voucherType) {
                        case "CR":
                            gl.setSplitId(8);
                            break;
                        case "DR":
                            gl.setSplitId(9);
                            break;
                    }
                }

                assignGlInfo(listGV);
                try {
                    glService.saveBatchGL(listGV);
                    //Delete row
                    if (!deleteIds.equals("-")) {
                        String[] ids = deleteIds.split(",");
                        for (String id : ids) {
                            Long lid = Long.parseLong(id);
                            glService.delete(lid,"GL-DELETE");
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.info("saveVoucher : " + ex);

                }
            }
            String vouNo = listVGl.get(0).getVouNo();
            txtVouNo.setText(vouNo);
            status = true;
        }
        return status;
    }

    private void assignGlInfo(Gl gl) {

        if (gl.getGlId() == null) {
            gl.setCompId(Global.compId);
            gl.setCreatedBy(Global.loginUser.getUserId().toString());
            gl.setCreatedDate(Util1.getTodayDate());
        } else {
            gl.setModifyBy(Global.loginUser.getUserId().toString());
            gl.setModifyDate(Util1.getTodayDate());
        }
    }

    private void assignGlInfo(List<Gl> listGL) {
        listGL.forEach(gl -> {
            assignGlInfo(gl);
        });
    }

    public String getVouNo(String deptCodeUser, String strDate, String compCode, String deptCode, String type) {
        Integer period = Util1.getDatePart(Util1.toDate(strDate, "dd/MM/yyyy"), "yyyy");
        int ttlLength = 4;
        int seqNo = seqService.getSequence(type.toUpperCase() + deptCode + period, period.toString(), compCode);
        String tmpVouNo = deptCodeUser.toUpperCase() + type.toUpperCase() + period
                + String.format("%0" + ttlLength + "d", seqNo);
        return tmpVouNo;
    }

    public void clear() {
        txtAccount.setText(null);
        txtCurrency.setText(null);
        txtDep.setText(null);
        txtFrom.setText(null);
        txtNaration.setText(null);
        txtRemark.setText(null);
        txtVouNo.setText(null);
        voucherEntryTableModel.clear();

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
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAccount = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDep = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtNaration = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCredit = new javax.swing.JTable();
        txtFTotolAmt = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnSave1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel1ComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From");

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.textFont);
        txtFromDate.setName("txtFromDate"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Voucher No");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Account");

        txtAccount.setFont(Global.textFont);
        txtAccount.setName("txtAccount"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Department");

        txtDep.setFont(Global.textFont);
        txtDep.setName("txtDep"); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Currency");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setName("txtCurrency"); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("From");

        txtFrom.setFont(Global.textFont);
        txtFrom.setName("txtFrom"); // NOI18N

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Naration");

        txtNaration.setFont(Global.textFont);
        txtNaration.setName("txtNaration"); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAccount, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(txtDep))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNaration, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNaration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblCredit.setFont(Global.textFont);
        tblCredit.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCredit.setName("tblCredit"); // NOI18N
        tblCredit.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblCredit);

        txtFTotolAmt.setEditable(false);
        txtFTotolAmt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtFTotolAmt.setFont(Global.amtFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Total Amt");

        btnSave.setBackground(ColorUtil.mainColor);
        btnSave.setFont(Global.textFont);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnSave1.setFont(Global.textFont);
        btnSave1.setText("Print");
        btnSave1.setName("btnSave"); // NOI18N
        btnSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(txtFTotolAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFTotolAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(btnSave)
                    .addComponent(btnSave1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel1ComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1ComponentShown

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        initMain();
    }//GEN-LAST:event_formComponentShown

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            saveCreditVoucher();
        } catch (Exception e) {
            LOGGER.error("Save CrDrVoucher :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Save CrDrVoucher", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSave1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSave1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCredit;
    private javax.swing.JTextField txtAccount;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtDep;
    private javax.swing.JFormattedTextField txtFTotolAmt;
    private javax.swing.JTextField txtFrom;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtNaration;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

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
            case "txtFromDate":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtVouNo.requestFocus();
                        break;
                }
                tabToTable(e);
                break;
            case "txtVouNo":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtAccount.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtAccount.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtFromDate.requestFocusInWindow();
                        break;
                }
                tabToTable(e);

                break;
            case "txtAccount":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtDep.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtDep.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtVouNo.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtDep":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtCurrency.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtCurrency.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtAccount.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtCurrency":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtFrom.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtFrom.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtDep.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtFrom":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtNaration.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtNaration.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtCurrency.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtNaration":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtFrom.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtRemark":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtNaration.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "btnSave":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtFromDate.requestFocusInWindow();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtFromDate.requestFocusInWindow();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtRemark.requestFocus();
                        break;
                }
                tabToTable(e);
                break;
            case "tblCredit":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtFromDate.requestFocusInWindow();
                }
                break;

        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblCredit.requestFocus();
            if (tblCredit.getRowCount() >= 0) {
                tblCredit.setRowSelectionInterval(0, 0);
            }
        }
    }
}
