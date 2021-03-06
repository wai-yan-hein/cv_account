/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.ReportService;
import com.cv.accountswing.service.VGlService;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.journal.JournalEntryDialog;
import com.cv.accountswing.ui.report.common.CrAmtTableModel;
import com.cv.accountswing.ui.report.common.DrAmtTableModel;
import com.cv.accountswing.util.Util1;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
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
public class TrialBalanceDetailDialog extends javax.swing.JDialog implements SelectionObserver {

    private static final Logger log = LoggerFactory.getLogger(TrialBalanceDetailDialog.class);
    private final ImageIcon editIcon = new ImageIcon(getClass().getResource("/images/edit_property.png"));

    @Autowired
    private CrAmtTableModel crAmtTableModel;
    @Autowired
    private DrAmtTableModel drAmtTableModel;
    @Autowired
    private EditCashDialog editCashDialog;
    @Autowired
    private VGlService vGlService;
    @Autowired
    private JournalEntryDialog journalEntryDialog;
    @Autowired
    private ReportService rService;
    private TableRowSorter<TableModel> sorter;
    private String desp;
    private List<VGl> listVGl;
    private Double openingAmt = 0.0;
    private String targetId;
    private String traderCode;

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Double getOpeningAmt() {
        return openingAmt;
    }

    public void setOpeningAmt(Double openingAmt) {
        this.openingAmt = openingAmt;
    }

    public List<VGl> getListVGl() {
        return listVGl;
    }

    public void setListVGl(List<VGl> listVGl) {
        this.listVGl = listVGl;
    }

    public void setDesp(String desp) {
        this.desp = desp;
        lblName.setText(this.desp);
    }

    /**
     * Creates new form TrialBalanceDetailDialog
     */
    public TrialBalanceDetailDialog() {
        super(Global.parentForm, true);
        initComponents();
        initTableListener();
    }

    private void initMain() {
        initTable();
    }

    private void initTableListener() {
        tblCr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    log.info("Edit Cash CR Open.");
                    VGl vGl = crAmtTableModel.getVGl(tblCr.convertRowIndexToModel(tblCr.getSelectedRow()));
                    vGl = vGlService.findById(vGl.getGlCode());
                    editCash(vGl, "CR");
                }
            }

        });
        tblDr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    log.info("Edit Cash DR Open.");
                    VGl vGl = drAmtTableModel.getVGl(tblDr.convertRowIndexToModel(tblDr.getSelectedRow()));
                    vGl = vGlService.findById(vGl.getGlCode());
                    editCash(vGl, "DR");
                }
            }

        });

    }

    private void editCash(VGl vgl, String type) {
        if (vgl.getTranSource() == null) {
            editCashDialog.setIconImage(editIcon.getImage());
            editCashDialog.setdType(type);
            editCashDialog.setSelectionObserver(this);
            editCashDialog.setSize(Global.width - 1000, Global.height - 300);
            editCashDialog.setLocationRelativeTo(null);
            editCashDialog.setvGl(vgl);
            editCashDialog.setVisible(true);
        } else {
            if (vgl.getTranSource().equals("GV")) {
                //journalEntryDialog.setSelectionObserver(this);
                journalEntryDialog.clear();
                journalEntryDialog.setGlVouId(vgl.getGlVouNo());
                journalEntryDialog.setSize(Global.width - 200, Global.height - 200);
                journalEntryDialog.setResizable(false);
                journalEntryDialog.setLocationRelativeTo(null);
                journalEntryDialog.setVisible(true);
            }
        }
    }

    private void initTable() {
        tblCR();
        tblDR();
        if (listVGl != null) {
            double ttlDrAmt = 0.0;
            double ttlCrAmt = 0.0;
            for (VGl vgl : listVGl) {
                if (vgl.getDrAmt() > 0) {
                    ttlDrAmt += vgl.getDrAmt();
                    drAmtTableModel.addVGl(vgl);
                }
                if (vgl.getCrAmt() > 0) {
                    ttlCrAmt += vgl.getCrAmt();
                    crAmtTableModel.addVGl(vgl);
                }
            }
            txtDrCount.setValue(drAmtTableModel.getListVGl().size());
            txtCrCount.setValue(crAmtTableModel.getListVGl().size());
            txtFCrAmt.setValue(Util1.toFormatPattern(ttlCrAmt));
            txtFDrAmt.setValue(Util1.toFormatPattern(ttlDrAmt));
            String closingAmt = Util1.toFormatPattern(openingAmt + ttlDrAmt - ttlCrAmt);
            txtOpening.setValue(Util1.toFormatPattern(openingAmt));
            txtClosing.setValue(closingAmt);
        }
    }

    private void tblCR() {
        crAmtTableModel.clear();
        tblCr.setModel(crAmtTableModel);
        tblCr.getTableHeader().setFont(Global.lableFont);
        tblCr.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblCr.getTableHeader().setForeground(ColorUtil.foreground);
        tblCr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCr.setRowHeight(Global.tblRowHeight);
        tblCr.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblCr.getColumnModel().getColumn(1).setPreferredWidth(170);
        tblCr.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblCr.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblCr.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblCr.setDefaultRenderer(Double.class, new TableCellRender());
        tblCr.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblCr.getModel());
        tblCr.setRowSorter(sorter);

    }

    private void tblDR() {
        drAmtTableModel.clear();
        tblDr.setModel(drAmtTableModel);
        tblDr.setRowHeight(Global.tblRowHeight);
        tblDr.getTableHeader().setFont(Global.lableFont);
        tblDr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDr.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblDr.getTableHeader().setForeground(ColorUtil.foreground);
        tblDr.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblDr.getColumnModel().getColumn(1).setPreferredWidth(170);
        tblDr.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblDr.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblDr.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblDr.setDefaultRenderer(Double.class, new TableCellRender());
        tblDr.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblDr.getModel());
        tblDr.setRowSorter(sorter);

    }

    private void print() {

        try {
            String compName = Global.sysProperties.get("system.report.company");
            String reportPath = Global.sysProperties.get("system.report.path");
            String fontPath = Global.sysProperties.get("system.font.path");
            String filePath = reportPath + File.separator + "APAR";
            Map<String, Object> parameters = new HashMap();
            parameters.put("p_company_name", compName);
            parameters.put("p_comp_id", Global.compCode);
            parameters.put("p_user_id", Global.loginUser.getAppUserCode());
            parameters.put("p_report_info", "Testing");
            parameters.put("trader_code", traderCode);
            rService.genReport(filePath, filePath, fontPath, parameters);
        } catch (Exception ex) {
            log.error("PRINT APAR REPORT :::" + ex.getMessage());
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

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDr = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCr = new javax.swing.JTable();
        txtOpening = new javax.swing.JFormattedTextField();
        txtClosing = new javax.swing.JFormattedTextField();
        txtFDrAmt = new javax.swing.JFormattedTextField();
        txtFCrAmt = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtCrCount = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDrCount = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();

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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Trial Balance");
        setBackground(new java.awt.Color(255, 255, 255));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBackground(ColorUtil.mainColor);

        lblName.setFont(Global.menuFont);
        lblName.setForeground(new java.awt.Color(255, 255, 255));
        lblName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblName.setText("Name");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/print_20px.png"))); // NOI18N
        jButton1.setText("Print");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        tblDr.setFont(Global.textFont);
        tblDr.setModel(new javax.swing.table.DefaultTableModel(
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
        tblDr.setName("Debit"); // NOI18N
        tblDr.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblDr);

        tblCr.setFont(Global.textFont);
        tblCr.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCr.setName("Credit"); // NOI18N
        tblCr.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblCr);

        txtOpening.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOpening.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtOpening.setEnabled(false);
        txtOpening.setFont(Global.amtFont);
        txtOpening.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOpeningActionPerformed(evt);
            }
        });

        txtClosing.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClosing.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtClosing.setEnabled(false);
        txtClosing.setFont(Global.amtFont);

        txtFDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFDrAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFDrAmt.setEnabled(false);
        txtFDrAmt.setFont(Global.amtFont);
        txtFDrAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFDrAmtActionPerformed(evt);
            }
        });

        txtFCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFCrAmt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtFCrAmt.setEnabled(false);
        txtFCrAmt.setFont(Global.amtFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Total Dr-Amt");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total Cr-Amt");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Opening Amt");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Closing Amt");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        txtCrCount.setBorder(null);
        txtCrCount.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel7.setText("Total Count : ");

        txtDrCount.setBorder(null);
        txtDrCount.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jLabel4.setText("Total Count : ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDrCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCrCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtDrCount))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCrCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtClosing, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtOpening, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtFCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtClosing, txtFCrAmt, txtFDrAmt, txtOpening});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOpening, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClosing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        initMain();
    }//GEN-LAST:event_formComponentShown

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusLost

    private void txtOpeningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOpeningActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOpeningActionPerformed

    private void txtFDrAmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFDrAmtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFDrAmtActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        print();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblName;
    private javax.swing.JTable tblCr;
    private javax.swing.JTable tblDr;
    private javax.swing.JFormattedTextField txtClosing;
    private javax.swing.JFormattedTextField txtCrCount;
    private javax.swing.JFormattedTextField txtDrCount;
    private javax.swing.JFormattedTextField txtFCrAmt;
    private javax.swing.JFormattedTextField txtFDrAmt;
    private javax.swing.JFormattedTextField txtOpening;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {

        if (source.equals("SAVE-GL-DR")) {
            int selectRow = tblDr.convertRowIndexToModel(tblDr.getSelectedRow());
            VGl vgl = (VGl) selectObj;
            if (!vgl.getSourceAcId().equals(targetId)) {
                vgl = swapDrCr(vgl);
            }
            drAmtTableModel.setVGl(selectRow, vgl);
        }
        if (source.equals("SAVE-GL-CR")) {
            int selectRow = tblDr.convertRowIndexToModel(tblCr.getSelectedRow());
            VGl vgl = (VGl) selectObj;
            if (!vgl.getSourceAcId().equals(targetId)) {
                vgl = swapDrCr(vgl);
            }
            crAmtTableModel.setVGl(selectRow, vgl);
        }
    }

    private VGl swapDrCr(VGl vgl) {
        vgl.setDrAmt(Util1.getDouble(vgl.getCrAmt()));
        vgl.setCrAmt(Util1.getDouble(vgl.getDrAmt()));
        return vgl;
    }
}
