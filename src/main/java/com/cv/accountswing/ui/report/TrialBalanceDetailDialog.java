/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.view.VGl;
import com.cv.accountswing.service.VGlService;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.report.common.CrAmtTableModel;
import com.cv.accountswing.ui.report.common.DrAmtTableModel;
import com.cv.accountswing.util.Util1;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
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
public class TrialBalanceDetailDialog extends javax.swing.JDialog implements PanelControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrialBalanceDetailDialog.class);

    @Autowired
    private CrAmtTableModel crAmtTableModel;
    @Autowired
    private DrAmtTableModel drAmtTableModel;
    @Autowired
    private EditCashDialog editCashDialog;
    @Autowired
    private VGlService vGlService;
    private TableRowSorter<TableModel> sorter;
    private String desp;
    private Double netChange;
    private List<VGl> listVGl;
    private Double openingAmt = 0.0;
    private boolean isShown = false;

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

    public void setNetChange(Double netChange) {
        this.netChange = netChange;
        txtFNetChange.setValue(this.netChange);
    }

    public void setDesp(String desp) {
        this.desp = desp;
        txtName.setText(this.desp);
    }

    /**
     * Creates new form TrialBalanceDetailDialog
     */
    public TrialBalanceDetailDialog() {
        super(new JFrame(), true);
        initComponents();
        ImageIcon size = new ImageIcon(getClass().getResource("/images/logo.png"));
        setIconImage(size.getImage());
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
                    VGl vGl = crAmtTableModel.getVGl(tblCr.convertRowIndexToModel(tblCr.getSelectedRow()));
                    vGl = vGlService.findById(vGl.getGlId());
                    editCash(vGl);
                }
            }

        });
        tblDr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    VGl vGl = drAmtTableModel.getVGl(tblDr.convertRowIndexToModel(tblDr.getSelectedRow()));
                    vGl = vGlService.findById(vGl.getGlId());
                    editCash(vGl);
                }
            }

        });

    }

    private void editCash(VGl vgl) {
        editCashDialog.setSize(Global.width - 1000, Global.height - 380);
        editCashDialog.setLocationRelativeTo(null);
        editCashDialog.setvGl(vgl);
        editCashDialog.setVisible(true);
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
        tblCr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCr.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblCr.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblCr.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblCr.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblCr.setDefaultRenderer(Double.class, new TableCellRender());
        tblCr.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblCr.getModel());
        tblCr.setRowSorter(sorter);

    }

    private void tblDR() {
        drAmtTableModel.clear();
        tblDr.setModel(drAmtTableModel);
        tblDr.getTableHeader().setFont(Global.lableFont);
        tblDr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDr.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblDr.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblDr.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblDr.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblDr.setDefaultRenderer(Double.class, new TableCellRender());
        tblDr.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter<>(tblDr.getModel());
        tblDr.setRowSorter(sorter);

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
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDr = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCr = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtFNetChange = new javax.swing.JFormattedTextField();
        txtOpening = new javax.swing.JFormattedTextField();
        txtClosing = new javax.swing.JFormattedTextField();
        txtFDrAmt = new javax.swing.JFormattedTextField();
        txtFCrAmt = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

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

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Name");

        txtName.setEditable(false);
        txtName.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtName)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Net Change");

        txtFNetChange.setEditable(false);
        txtFNetChange.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtFNetChange.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFNetChange.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtFNetChange)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtFNetChange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtOpening.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtOpening.setFont(Global.amtFont);
        txtOpening.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOpeningActionPerformed(evt);
            }
        });

        txtClosing.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClosing.setFont(Global.amtFont);

        txtFDrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFDrAmt.setFont(Global.amtFont);
        txtFDrAmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFDrAmtActionPerformed(evt);
            }
        });

        txtFCrAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFCrAmt.setFont(Global.amtFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Total Dr-Amt");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Total Cr-Amt");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Opening Amt");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Closing Amt");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
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
                                        .addComponent(txtFDrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFCrAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtClosing, txtFCrAmt, txtFDrAmt, txtOpening});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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
        if (!isShown) {
            initMain();
        }
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

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblCr;
    private javax.swing.JTable tblDr;
    private javax.swing.JFormattedTextField txtClosing;
    private javax.swing.JFormattedTextField txtFCrAmt;
    private javax.swing.JFormattedTextField txtFDrAmt;
    private javax.swing.JFormattedTextField txtFNetChange;
    private javax.swing.JTextField txtName;
    private javax.swing.JFormattedTextField txtOpening;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
    }
}
