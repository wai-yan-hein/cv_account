/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.TransferDetailHis;
import com.cv.inv.entity.TransferHis;
import com.cv.inv.entry.common.TransferTableModel;
import com.cv.inv.entry.dialog.TransferSearchDialog;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.service.TransferDetailHisService;
import com.cv.inv.service.TransferHisService;
import com.cv.inv.service.VouIdService;
import com.cv.inv.util.GenVouNoImpl;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class Transfer extends javax.swing.JPanel implements SelectionObserver, KeyListener, PanelControl {

    /**
     * Creates new form Transfer
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Issue.class);
    private LoadingObserver loadingObserver;
    private boolean isShown = false;
    private GenVouNoImpl vouEngine = null;
    private TransferHis tran = new TransferHis();
    private LocationAutoCompleter locAutoCompleter;
    private LocationAutoCompleter locCompleter;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }
    @Autowired
    private TransferTableModel tranTableModel;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private VouIdService voudIdService;
    @Autowired
    private TransferHisService thService;
    @Autowired
    private TransferSearchDialog tranSearchDialog;
    @Autowired
    private TransferDetailHisService tDetailService;

    public Transfer() {
        initComponents();
    }

    private void initMain() {
        initTable();
        initKeyListener();
        setTodayDate();
        initCombo();
        actionMapping();
        genVouNo();
        isShown = true;
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(voudIdService, "Transfer", Util1.getPeriod(txtTranDate.getDate()));
        txtVouNo.setText(vouEngine.genVouNo());
    }

    private void initTable() {
        tblTransfer.setModel(tranTableModel);
        tranTableModel.setParent(tblTransfer);
        tblTransfer.getTableHeader().setFont(Global.lableFont);
        tblTransfer.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblTransfer.getTableHeader().setForeground(ColorUtil.foreground);
        tranTableModel.setCallBack(this);
        tranTableModel.addEmptyRow();
        tblTransfer.setCellSelectionEnabled(true);
        tblTransfer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTransfer.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblTransfer.getColumnModel().getColumn(1).setPreferredWidth(300);
        tblTransfer.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblTransfer.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblTransfer.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblTransfer.getColumnModel().getColumn(5).setPreferredWidth(40);
        tblTransfer.getColumnModel().getColumn(6).setPreferredWidth(15);
        tblTransfer.getColumnModel().getColumn(7).setPreferredWidth(50);
        tblTransfer.getColumnModel().getColumn(8).setPreferredWidth(30);
        tblTransfer.getColumnModel().getColumn(9).setPreferredWidth(50);

        tblTransfer.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblTransfer.setDefaultRenderer(Object.class, new TableCellRender());
        tblTransfer.setDefaultRenderer(Float.class, new TableCellRender());
        tblTransfer.setDefaultRenderer(Double.class, new TableCellRender());

        tblTransfer.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblTransfer.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor());
        tblTransfer.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//qty
        tblTransfer.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblTransfer.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());
        tblTransfer.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());

        tblTransfer.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
    }

    private void initKeyListener() {
        txtVouNo.addKeyListener(this);
        txtTranDate.getDateEditor().getUiComponent().setName("txtTranDate");
        txtTranDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtFrom.addKeyListener(this);
        txtTo.addKeyListener(this);
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblTransfer.requestFocus();
            if (tblTransfer.getRowCount() >= 0) {
                tblTransfer.setRowSelectionInterval(0, 0);
            }
        }
    }

    private void setTodayDate() {
        txtTranDate.setDate(Util1.getTodayDate());;
    }

    private void initCombo() {
        locAutoCompleter = new LocationAutoCompleter(txtFrom, Global.listLocation, null);
        locAutoCompleter.setSelectionObserver(this);
        locCompleter = new LocationAutoCompleter(txtTo, Global.listLocation, null);
        locCompleter.setSelectionObserver(this);
    }

    private void actionMapping() {
        //F8 event on tblSale
        tblTransfer.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
        tblTransfer.getActionMap().put("DELETE", actionItemDelete);

    }
    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblTransfer.getSelectedRow() >= 0) {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Damage item delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                     tranTableModel.delete(tblTransfer.getSelectedRow());
                }
            }
        }
    };

    private void clear() {
        lblStatus.setText("NEW");
        if (Global.loginDate != null) {
            txtTranDate.setDate(Util1.toDate(Global.loginDate, "dd/MM/yyyy"));
        } else {
            txtTranDate.setDate(Util1.getTodayDate());
        }
        txtRemark.setText(null);
        txtTotalAmount.setText("0.0");
        tranTableModel.clearData();
//        locCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
//        locCompleter.setSelectionObserver(this);
        //   locCompleter.setLocation(null);
        //retOutTableModel.setLocation((LocationH2) cboLocation.getSelectedItem());
        tran = new TransferHis();
        genVouNo();
        isShown = false;
    }

    private boolean saveTransfer() {
        boolean status = false;
        if (isValidEntry()) {
            List<TransferDetailHis> listDmgDetail = tranTableModel.getDetail();
            List<String> delList = tranTableModel.getDelList();

            try {
                String vouStatus = lblStatus.getText();
                thService.save(tran, listDmgDetail, vouStatus, delList);
                vouEngine.updateVouNo();
                genVouNo();
                status = true;
            } catch (Exception ex) {
                LOGGER.error("save Transfer : " + tran.getTranVouId() + " : " + ex.getMessage());
            }
        }
        return status;
    }

    private boolean isValidEntry() {
        boolean status = true;

        tran.setTranVouId(txtVouNo.getText());
        tran.setRemark(txtRemark.getText());
        if (lblStatus.getText().equals("NEW")) {
            tran.setTranDate(txtTranDate.getDate());
            tran.setCreatedBy(Global.loginUser);
            tran.setSession(Global.sessionId);
        } else {
            Date tmpDate = txtTranDate.getDate();
            if (!Util1.isSameDate(tmpDate, tran.getTranDate())) {
                tran.setTranDate(txtTranDate.getDate());
            }
            tran.setUpdatedBy(Global.loginUser);
            tran.setUpdatedDate(Util1.getTodayDate());
        }
        tran.setDeleted(Util1.getNullTo(tran.isDeleted()));
        tran.setVouTotal(Util1.getDouble(txtTotalAmount.getText()));
        if (locCompleter.getLocation() != null && locAutoCompleter.getLocation() != null) {
            tran.setToLocation(locCompleter.getLocation());
            tran.setFromLocation(locAutoCompleter.getLocation());
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Location cannot be null.",
                    "Invalid.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        return status;
    }
       public void deleteTransfer() {
        int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                "Are you sure to delete?", "Transfer item delete", JOptionPane.YES_NO_OPTION);
        if (yes_no == 0) {
            String vouNo = txtVouNo.getText();
            if (lblStatus.getText().equals("EDIT")) {
                thService.delete(vouNo);
                clear();
            }
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
        txtVouNo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTo = new javax.swing.JTextField();
        txtTranDate = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransfer = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtTotalAmount = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Tran Date");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("From");

        txtFrom.setFont(Global.textFont);
        txtFrom.setName("txtFrom"); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("To");

        txtTo.setFont(Global.textFont);
        txtTo.setName("txtTo"); // NOI18N

        txtTranDate.setDateFormatString("dd/MM/yyyy");
        txtTranDate.setFont(Global.lableFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(16, 16, 16)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtTranDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txtRemark))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(txtTo))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel4)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtTranDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblTransfer.setFont(Global.textFont);
        tblTransfer.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblTransfer);

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblStatus))
        );

        txtTotalAmount.setFont(Global.amtFont);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Total :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 3, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(447, 447, 447)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        txtVouNo.requestFocus();
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblTransfer;
    private javax.swing.JTextField txtFrom;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtTo;
    private javax.swing.JTextField txtTotalAmount;
    private com.toedter.calendar.JDateChooser txtTranDate;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
   switch (source.toString()) {
            case "TransferVouList":
        try {
                TransferHis vRetIn = (TransferHis) selectObj;
                tran = thService.findById(vRetIn.getTranVouId());

                if (Util1.getNullTo(tran.isDeleted())) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("EDIT");
                }

                txtVouNo.setText(tran.getTranVouId());
                txtRemark.setText(tran.getRemark());
                txtTranDate.setDate(tran.getTranDate());
                locCompleter.setLocation(tran.getToLocation());
                locAutoCompleter.setLocation(tran.getFromLocation());
                List<TransferDetailHis> listDetail = tDetailService.search(tran.getTranVouId());
                tranTableModel.setTransferDetailList(listDetail);

            } catch (Exception ex) {
                LOGGER.error("selected : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());

            }

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
        if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTextFieldDateEditor) {
            ctrlName = ((JTextFieldDateEditor) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtVouNo":
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_UP) {
                    txtTo.requestFocus();
                } else {
                    txtTranDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtTranDate":
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_UP) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtFrom.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtTranDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtRemark.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtTranDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT
                        || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtFrom.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtTo.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtFrom":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtTranDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtTo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtTo":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtFrom.requestFocus();
                }
                tabToTable(e);
                break;
        }
    }

    @Override
    public void save() {
        if (saveTransfer()) {
            clear();
        }
    }

    @Override
    public void delete() {
        deleteTransfer();
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
        tranSearchDialog.setSize(Global.width - 200, Global.height - 200);
        tranSearchDialog.setLocationRelativeTo(null);
        tranSearchDialog.setSelectionObserver(this);
        tranSearchDialog.setVisible(true);
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
