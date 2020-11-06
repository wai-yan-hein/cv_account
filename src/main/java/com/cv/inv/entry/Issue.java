/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.StockOutstanding;
import com.cv.inv.entry.common.IssueTableModel;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.service.VouIdService;
import com.cv.inv.util.GenVouNoImpl;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class Issue extends javax.swing.JPanel implements SelectionObserver, KeyListener, PanelControl {

    /**
     * Creates new form Issue
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Issue.class);
    private LoadingObserver loadingObserver;
    private boolean isShown = false;
    private GenVouNoImpl vouEngine = null;
    @Autowired
    private IssueTableModel issueTableModel;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private VouIdService voudIdService;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public Issue() {
        initComponents();

    }

    private void initMain() {
        initTable();
        initKeyListener();
        setTodayDate();
        initCombo();
        genVouNo();
        isShown = true;
    }

    private void initTable() {
        tblIssue.setModel(issueTableModel);
        issueTableModel.setParent(tblIssue);
        tblIssue.getTableHeader().setFont(Global.lableFont);
        issueTableModel.addEmptyRow();
        issueTableModel.setCallBack(this);
        tblIssue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblIssue.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblIssue.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblIssue.getColumnModel().getColumn(2).setPreferredWidth(30);
        tblIssue.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblIssue.getColumnModel().getColumn(4).setPreferredWidth(30);
        tblIssue.getColumnModel().getColumn(5).setPreferredWidth(150);
        tblIssue.getColumnModel().getColumn(6).setPreferredWidth(40);
        tblIssue.getColumnModel().getColumn(7).setPreferredWidth(2);
        tblIssue.getColumnModel().getColumn(8).setPreferredWidth(10);
        tblIssue.getColumnModel().getColumn(9).setPreferredWidth(30);

        tblIssue.getColumnModel().getColumn(2).setCellEditor(new StockCellEditor());
        tblIssue.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblIssue.getColumnModel().getColumn(8).setCellEditor(new StockUnitEditor());

        tblIssue.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblIssue.setDefaultRenderer(Object.class, new TableCellRender());
        tblIssue.setDefaultRenderer(Float.class, new TableCellRender());
        tblIssue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblIssue.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            //     txtRecNo.setText(Integer.toString(tblDamage.getSelectedRow() + 1));
        });
    }

    private void initKeyListener() {
        txtIssueId.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        lblNew.addKeyListener(this);
        btnBorrow.addKeyListener(this);
        btnOutstanding.addKeyListener(this);
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblIssue.requestFocus();
            if (tblIssue.getRowCount() >= 0) {
                tblIssue.setRowSelectionInterval(0, 0);
            }
        }
    }

    private void setTodayDate() {
        txtDate.setDate(Util1.getTodayDate());
    }

    private void initCombo() {
        LocationAutoCompleter locAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        locAutoCompleter.setSelectionObserver(this);
    }

    private void borrow() {
        System.out.println("Borrow");
        StockOutstanding outs = new StockOutstanding();
        outs.setTranOption("Borrow");
        outs.setInvId(txtIssueId.getText());
        outs.setTranDate(txtDate.getDate());
        issueTableModel.add(outs);
    }

    private void clear() {
        issueTableModel.removeListDetail();
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(voudIdService, "Damage", Util1.getPeriod(txtDate.getDate()));
        txtIssueId.setText(vouEngine.genVouNo());
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
        txtIssueId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        lblNew = new javax.swing.JLabel();
        btnBorrow = new javax.swing.JButton();
        btnOutstanding = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        txtDate = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblIssue = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Issus Id");

        txtIssueId.setFont(Global.textFont);
        txtIssueId.setName("txtIssueId"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Date");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N

        lblNew.setFont(Global.lableFont);
        lblNew.setText("NEW");
        lblNew.setName("lblNew"); // NOI18N

        btnBorrow.setFont(Global.lableFont);
        btnBorrow.setText("Borrow");
        btnBorrow.setName("btnBorrow"); // NOI18N
        btnBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrowActionPerformed(evt);
            }
        });

        btnOutstanding.setFont(Global.lableFont);
        btnOutstanding.setText("Oustanding");
        btnOutstanding.setName("btnOutstanding"); // NOI18N
        btnOutstanding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOutstandingActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.lableFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtIssueId, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(lblNew)
                .addGap(18, 18, 18)
                .addComponent(btnBorrow)
                .addGap(18, 18, 18)
                .addComponent(btnOutstanding))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtIssueId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNew)
                        .addComponent(btnBorrow)
                        .addComponent(btnOutstanding))
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        tblIssue.setFont(Global.textFont);
        tblIssue.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblIssue);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        txtIssueId.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void btnOutstandingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOutstandingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnOutstandingActionPerformed

    private void btnBorrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrowActionPerformed
        borrow();
    }//GEN-LAST:event_btnBorrowActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBorrow;
    private javax.swing.JButton btnOutstanding;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblNew;
    private javax.swing.JTable tblIssue;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtIssueId;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRemark;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {

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
        if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTextFieldDateEditor) {
            ctrlName = ((JTextFieldDateEditor) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtIssueId":
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_UP) {
                    btnOutstanding.requestFocus();
                } else {
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtDate":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtIssueId.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtLocation.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtLocation":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtRemark.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtLocation.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    btnBorrow.requestFocus();
                }
                tabToTable(e);
                break;
            case "btnBorrow":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    btnOutstanding.requestFocus();
                }
                tabToTable(e);
                break;
            case "btnOutstanding":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    btnBorrow.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT
                        || e.getKeyCode() == KeyEvent.VK_UP) {
                    txtIssueId.requestFocus();
                }
                tabToTable(e);
                break;
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
        clear();
    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
