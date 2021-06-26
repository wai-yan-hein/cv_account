/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.journal;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.GlService;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.service.SeqTableService;
import com.cv.accountswing.service.VGlService;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.COACellEditor;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.ui.editor.TraderCellEditor;
import com.cv.accountswing.ui.journal.common.JournalEntryTableModel;
import com.cv.accountswing.util.Util1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class JournalEntryDialog extends javax.swing.JDialog implements KeyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JournalEntryDialog.class);
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

    @Autowired
    private JournalEntryTableModel journalTablModel;

    @Autowired
    private SeqTableService seqService;
    @Autowired
    private GlService glService;
    @Autowired
    private VGlService vGlService;
    @Autowired
    private ReportService rService;
    private SelectionObserver selectionObserver;
    private TableRowSorter<TableModel> sorter;
    private String glVouId = null;
    private CurrencyAutoCompleter autoCompleter;

    public void setGlVouId(String glVouId) {
        this.glVouId = glVouId;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    /**
     * Creates new form JournalEntryDialog
     */
    public JournalEntryDialog() {
        super(Global.parentForm, true);
        initComponents();
        initKeyListener();
    }

    private void initMain() {
        initCombo();
        initTable();
        if (!glVouId.equals("-")) {
            searchJournalByVouId();
        }
    }

    private void initCombo() {
        autoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        autoCompleter.setCurrency(Global.defalutCurrency);

    }

    private void initTable() {
        txtDate.setDate(Util1.getTodayDate());
        tblJournal.setModel(journalTablModel);
        tblJournal.setCellSelectionEnabled(true);
        journalTablModel.setParent(tblJournal);
        journalTablModel.setTtlCrdAmt(txtFCrdAmt);
        journalTablModel.setTtlDrAmt(txtFDrAmt);
        tblJournal.getTableHeader().setFont(Global.lableFont);
        tblJournal.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblJournal.getTableHeader().setForeground(ColorUtil.foreground);
        tblJournal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblJournal.getColumnModel().getColumn(0).setCellEditor(new DepartmentCellEditor(false));
        tblJournal.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(2).setCellEditor(new TraderCellEditor(false, 0));
        tblJournal.getColumnModel().getColumn(3).setCellEditor(new COACellEditor(false));
        tblJournal.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblJournal.getColumnModel().getColumn(0).setPreferredWidth(10);//dep
        tblJournal.getColumnModel().getColumn(1).setPreferredWidth(240);//Desp
        tblJournal.getColumnModel().getColumn(2).setPreferredWidth(240);//cus
        tblJournal.getColumnModel().getColumn(3).setPreferredWidth(240);//acc
        tblJournal.getColumnModel().getColumn(4).setPreferredWidth(60);//dr
        tblJournal.getColumnModel().getColumn(5).setPreferredWidth(60);//cr

        tblJournal.setDefaultRenderer(Double.class, new TableCellRender());
        tblJournal.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblJournal.getModel());
        tblJournal.setRowSorter(sorter);
        journalTablModel.addEmptyRow();
        tblJournal.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
    }

    private void searchJournalByVouId() {
        List<VGl> listVGl = vGlService.search("-", "-", "-", "-", "-", "-",
                "-", "-", "-", "-", "-", "-", "-", "GV", glVouId, "-", "-",
                "-", "-", "-", "-");
        journalTablModel.setListGV(listVGl);
        if (listVGl.size() >= 0) {
            VGl vgl = listVGl.get(0);
            txtVouNo.setText(vgl.getVouNo());
            txtDate.setDate(vgl.getGlDate());
            txtRefrence.setText(vgl.getReference());
            txtCurrency.setText(vgl.getfCurName());
            journalTablModel.addEmptyRow();
        }

        tblJournal.requestFocusInWindow();

    }

    private boolean saveGeneralVoucher() {
        boolean status = false;
        String vouNo = Util1.isNull(txtVouNo.getText(), "-");
        String strDate = Util1.toDateStr(txtDate.getDate(), "dd/MM/yyyy");
        String refrence = txtRefrence.getText();

        if (isValidEntry()) {
            java.lang.reflect.Type type = new TypeToken<List<Gl>>() {
            }.getType();
            String strJson = gson.toJson(journalTablModel.getListGV());
            List<Gl> listGl = gson.fromJson(strJson, type);
            if (glVouId.equals("-")) {
                glVouId = getVouNo(Global.machineId, "GV", strDate, Global.compCode);
            }
            if (isGeneralVoucher(listGl, glVouId, vouNo, refrence, strDate)) {
                assignGlInfo(listGl);
                try {
                    for (Gl gl : listGl) {
                        if (gl.getSourceAcId() != null) {
                            glService.save(gl);
                        }
                    }
                    if (!listGl.isEmpty()) {
                        journalTablModel.clear();
                        this.dispose();
                        if (selectionObserver != null) {
                            selectionObserver.selected("SEARCHVOUCHER", "-");
                        }
                    }
                    status = true;
                } catch (NumberFormatException ex) {
                    LOGGER.error("saveGeneralVoucher : " + ex);
                } catch (Exception ex) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage(), "Save Journal.", JOptionPane.ERROR_MESSAGE);
                    LOGGER.error("saveGeneralVoucher : " + ex);
                }
            }
        }
        return status;
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (autoCompleter.getCurrency() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Entry");
            return false;
        }
        for (VGl vgl : journalTablModel.getListGV()) {
            if (Util1.getFloat(vgl.getDrAmt()) + Util1.getFloat(vgl.getCrAmt()) > 0) {
                if (vgl.getSourceAcId() == null) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Select Account.");
                    tblJournal.requestFocus();
                }
            }

        }
        return status;
    }

    private void assignGlInfo(List<Gl> listGL) {
        listGL.forEach(gl -> {
            assignGlInfo(gl);
        });
    }

    private void assignGlInfo(Gl gl) {
        String userCode = Global.loginUser.getAppUserCode();
        String compCode = Global.compCode;

        if (gl.getGlCode() == null) {
            gl.setCompCode(compCode);
            gl.setCreatedBy(userCode);
            gl.setCreatedDate(Util1.getTodayDate());
        } else {
            gl.setModifyBy(userCode);
            gl.setModifyDate(Util1.getTodayDate());
        }
    }

    private String getVouNo(Integer macId, String type, String strDate, String compCode) {
        String period = Util1.getPeriod(strDate, "dd/MM/yyyy");
        int ttlLength = 9;
        int seqNo = seqService.getSequence(macId, type, period, compCode);
        String tmpVouNo = type.toUpperCase()
                + String.format("%0" + ttlLength + "d", seqNo) + period;

        return tmpVouNo;
    }

    private boolean isGeneralVoucher(List<Gl> listGV,
            String strGvId, String vouNo, String ref, String strGvDate) {
        boolean status = true;
        Float ttlCrdAmt = Util1.getFloat(txtFCrdAmt.getValue());
        Float ttlDrAmt = Util1.getFloat(txtFDrAmt.getValue());
        if (!Objects.equals(ttlCrdAmt, ttlDrAmt)) {
            JOptionPane.showMessageDialog(Global.parentForm, "Out of balance.");
            status = false;
            tblJournal.requestFocus();
        } else if (ttlCrdAmt + ttlDrAmt <= 0) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Total Amount.");
            status = false;
            tblJournal.requestFocus();
        }
        if (status) {
            if (vouNo.equals("-")) {
                vouNo = strGvId;
            }
            for (Gl gl : listGV) {
                gl.setGlVouNo(strGvId);
                gl.setVouNo(vouNo);
                gl.setReference(ref);
                gl.setGlDate(Util1.toDate(strGvDate, "dd/MM/yyyy"));
                gl.setTranSource("GV");
                gl.setMacId(Global.machineId);
                gl.setCompCode(Global.compCode);
                gl.setFromCurId(autoCompleter.getCurrency().getKey().getCode());
            }
        }
        /*if (listGV.size() < 2) {
        status = false;
        
        }*/
        return status;
    }

    private void initKeyListener() {
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtRefrence.addKeyListener(this);
        txtVouNo.addKeyListener(this);
        tblJournal.addKeyListener(this);
        btnSave.addKeyListener(this);
    }

    public void clear() {
        txtDate.setDate(Util1.getTodayDate());
        txtFCrdAmt.setValue(0.0);
        txtFDrAmt.setValue(0.0);
        txtRefrence.setText(null);
        txtVouNo.setText(null);
        journalTablModel.clear();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRefrence = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        btnPrint = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblJournal = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtFCrdAmt = new javax.swing.JFormattedTextField();
        txtFDrAmt = new javax.swing.JFormattedTextField();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Journal Voucher");
        setFont(Global.textFont);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Voucher No");

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N
        txtVouNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouNoActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Refrence");

        txtRefrence.setFont(Global.textFont);
        txtRefrence.setName("txtRefrence"); // NOI18N
        txtRefrence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRefrenceActionPerformed(evt);
            }
        });

        btnSave.setBackground(ColorUtil.mainColor);
        btnSave.setFont(Global.textFont);
        btnSave.setForeground(ColorUtil.foreground);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Currency");

        txtCurrency.setEditable(false);
        txtCurrency.setFont(Global.textFont);
        txtCurrency.setEnabled(false);
        txtCurrency.setName("txtRefrence"); // NOI18N
        txtCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrencyActionPerformed(evt);
            }
        });

        btnPrint.setBackground(ColorUtil.btnEdit);
        btnPrint.setFont(Global.textFont);
        btnPrint.setForeground(ColorUtil.foreground);
        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/print_18px.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setName("btnSave"); // NOI18N
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRefrence, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSave)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel7)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPrint))
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2});

        tblJournal.setFont(Global.textFont);
        tblJournal.setModel(new javax.swing.table.DefaultTableModel(
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
        tblJournal.setName("tblJournal"); // NOI18N
        tblJournal.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblJournal);

        txtFCrdAmt.setEditable(false);
        txtFCrdAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFCrdAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFCrdAmt.setEnabled(false);
        txtFCrdAmt.setFont(Global.amtFont);
        txtFCrdAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFCrdAmtActionPerformed(evt);
            }
        });

        txtFDrAmt.setEditable(false);
        txtFDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFDrAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFDrAmt.setEnabled(false);
        txtFDrAmt.setFont(Global.amtFont);
        txtFDrAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFDrAmtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(672, Short.MAX_VALUE)
                .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFCrdAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFCrdAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtVouNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouNoActionPerformed

    private void txtRefrenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefrenceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRefrenceActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        initMain();
    }//GEN-LAST:event_formComponentShown

    private void txtFCrdAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFCrdAmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFCrdAmtActionPerformed

    private void txtFDrAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFDrAmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFDrAmtActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveGeneralVoucher();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrencyActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        try {
            printJournal();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(Global.parentForm, ex.getMessage());
        }
    }//GEN-LAST:event_btnPrintActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblJournal;
    private javax.swing.JTextField txtCurrency;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JFormattedTextField txtFCrdAmt;
    private javax.swing.JFormattedTextField txtFDrAmt;
    private javax.swing.JTextField txtRefrence;
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
            case "txtDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtVouNo":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtRefrence.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtRefrence":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    tblJournal.requestFocus();
                    if (tblJournal.getRowCount() >= 0) {
                        tblJournal.setRowSelectionInterval(0, 0);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRefrence.requestFocus();
                }
                tabToTable(e);
            case "tblJournal":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                break;

        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblJournal.requestFocus();
            if (tblJournal.getRowCount() >= 0) {
                tblJournal.setRowSelectionInterval(0, 0);
            }
        }
    }

    private void printJournal() throws Exception {
        this.dispose();
        String reportPath = Global.sysProperties.get("system.report.path");
        String fontPath = Global.sysProperties.get("system.font.path");
        String filePath = reportPath + File.separator + "Journal";
        Map<String, Object> parameters = new HashMap();
        parameters.put("p_company_name", Global.companyName);
        parameters.put("p_comp_id", Global.compCode);
        parameters.put("p_vou_no", glVouId);
        rService.genReport(filePath, filePath, fontPath, parameters);
    }

}
