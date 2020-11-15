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
import com.cv.inv.entity.StockReceiveDetailHis;
import com.cv.inv.entity.StockReceiveHis;
import com.cv.inv.entry.common.StockReceiveTableModel;
import com.cv.inv.entry.dialog.StockReceiveListDialog;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.service.StockReceiveDetailHisService;
import com.cv.inv.service.StockReceiveHisService;
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
import javax.swing.JButton;
import javax.swing.JOptionPane;
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
public class StockReceive extends javax.swing.JPanel implements SelectionObserver, KeyListener, PanelControl {

    /**
     * Creates new form StockReceive
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Issue.class);
    private LoadingObserver loadingObserver;
    private boolean isShown = false;
    private GenVouNoImpl vouEngine = null;
    private StockReceiveHis rohh2 = new StockReceiveHis();
    private LocationAutoCompleter locCompleter;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }
    @Autowired
    private StockReceiveTableModel receiveTableModel;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private VouIdService voudIdService;
    @Autowired
    private StockReceiveHisService rhService;
    @Autowired
    private StockReceiveListDialog receiveSearchDialog;
    @Autowired
    private StockReceiveDetailHisService rDetailService;

    public StockReceive() {
        initComponents();
    }

    private void initMain() {
        initTable();
        initKeyListener();
        setTodayDate();
        initCombo();
        genVouNo();
        actionMapping();
        isShown = true;
    }

    private void initTable() {
        tblStockReceive.setModel(receiveTableModel);
        receiveTableModel.setParent(tblStockReceive);
        tblStockReceive.getTableHeader().setFont(Global.lableFont);
        receiveTableModel.addEmptyRow();
        receiveTableModel.setCallBack(this);

        tblStockReceive.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStockReceive.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblStockReceive.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblStockReceive.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblStockReceive.getColumnModel().getColumn(3).setPreferredWidth(30);
        tblStockReceive.getColumnModel().getColumn(4).setPreferredWidth(200);
        tblStockReceive.getColumnModel().getColumn(5).setPreferredWidth(30);
        tblStockReceive.getColumnModel().getColumn(6).setPreferredWidth(30);
        tblStockReceive.getColumnModel().getColumn(7).setPreferredWidth(10);
        tblStockReceive.getColumnModel().getColumn(8).setPreferredWidth(2);
        tblStockReceive.getColumnModel().getColumn(9).setPreferredWidth(10);

        tblStockReceive.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblStockReceive.setDefaultRenderer(Object.class, new TableCellRender());
        tblStockReceive.setDefaultRenderer(Float.class, new TableCellRender());
        tblStockReceive.setDefaultRenderer(Object.class, new TableCellRender());

        tblStockReceive.getColumnModel().getColumn(3).setCellEditor(new StockCellEditor());
        tblStockReceive.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblStockReceive.getColumnModel().getColumn(8).setCellEditor(new StockUnitEditor());

    }

    private void initKeyListener() {
        txtId.addKeyListener(this);
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtRemark.addKeyListener(this);
        btnOutstanding.addKeyListener(this);
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblStockReceive.requestFocus();
            if (tblStockReceive.getRowCount() >= 0) {
                tblStockReceive.setRowSelectionInterval(0, 0);
            }
        }
    }

    private void setTodayDate() {
        txtDate.setDate(Util1.getTodayDate());;
    }

    private void initCombo() {
        locCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        locCompleter.setSelectionObserver(this);
    }

    private void clear() {
        receiveTableModel.removeListDetail();

        lblStatus.setText("NEW");
        if (Global.loginDate != null) {
            txtDate.setDate(Util1.toDate(Global.loginDate, "dd/MM/yyyy"));
        } else {
            txtDate.setDate(Util1.getTodayDate());
        }
        txtRemark.setText(null);
        genVouNo();
        isShown = false;
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(voudIdService, "Damage", Util1.getPeriod(txtDate.getDate()));
        txtId.setText(vouEngine.genVouNo());
    }

    private void actionMapping() {
        //F8 event on tblRetIn
        tblStockReceive.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
        tblStockReceive.getActionMap().put("DELETE", actionItemDelete);
    }
    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblStockReceive.getSelectedRow() >= 0) {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Return in item delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    receiveTableModel.delete(tblStockReceive.getSelectedRow());
                }
            }
        }
    };

    public void deleteReceive() {
        int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                "Are you sure to delete?", "Return in item delete", JOptionPane.YES_NO_OPTION);
        if (yes_no == 0) {
            String vouNo = txtId.getText();
            if (lblStatus.getText().equals("EDIT")) {
                  rhService.delete(vouNo);
                clear();
            }
        }
    }

    private boolean saveReceive() {
        boolean status = false;
        if (isValidEntry()) {
            List<StockReceiveDetailHis> listDmgDetail = receiveTableModel.getDetail();
            List<String> delList = receiveTableModel.getDelList();

            try {
                String vouStatus = lblStatus.getText();
                rhService.save(rohh2, listDmgDetail, vouStatus, delList);
                vouEngine.updateVouNo();
                genVouNo();
                status = true;
            } catch (Exception ex) {
                LOGGER.error("save Receive : " + rohh2.getReceivedId() + " : " + ex.getMessage());
            }
        }
        return status;
    }

    private boolean isValidEntry() {
        boolean status = true;

        rohh2.setReceivedId(txtId.getText());
        rohh2.setRemark(txtRemark.getText());
        if (lblStatus.getText().equals("NEW")) {
            rohh2.setReceiveDate(txtDate.getDate());
            rohh2.setCreatedBy(Global.loginUser);
        } else {
            Date tmpDate = txtDate.getDate();
            if (!Util1.isSameDate(tmpDate, rohh2.getReceiveDate())) {
                rohh2.setReceiveDate(txtDate.getDate());
            }
            rohh2.setUpdatedBy(Global.loginUser);
            rohh2.setUpdatedDate(Util1.getTodayDate());
        }
        rohh2.setDeleted(Util1.getNullTo(rohh2.isDeleted()));
        if (locCompleter.getLocation() != null) {
            rohh2.setLocation(locCompleter.getLocation());
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Location cannot be null.",
                    "Invalid.", JOptionPane.ERROR_MESSAGE);
            status = false;
        }

        return status;
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
        txtId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnOutstanding = new javax.swing.JButton();
        txtDate = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStockReceive = new javax.swing.JTable();
        lblStatus = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("ID");

        txtId.setFont(Global.textFont);
        txtId.setName("txtId"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Date");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("NEW");

        btnOutstanding.setFont(Global.lableFont);
        btnOutstanding.setText("Oustanding");
        btnOutstanding.setName("btnOutstanding"); // NOI18N

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
                .addComponent(txtId, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(btnOutstanding)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(btnOutstanding))
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblStockReceive.setFont(Global.textFont);
        tblStockReceive.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblStockReceive);

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 926, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
            if (txtId != null) {
                receiveTableModel.setReceiveId(txtId.getText());
            }
        }
        txtId.requestFocus();
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOutstanding;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblStockReceive;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRemark;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "ReceiveVouList":
        try {
                StockReceiveHis vRetIn = (StockReceiveHis) selectObj;
                rohh2 = rhService.findById(vRetIn.getReceivedId());

                if (Util1.getNullTo(rohh2.isDeleted())) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("EDIT");
                }

                txtId.setText(rohh2.getReceivedId());
                txtRemark.setText(rohh2.getRemark());
                txtDate.setDate(rohh2.getReceiveDate());
                locCompleter.setLocation(rohh2.getLocation());
                List<StockReceiveDetailHis> listDetail = rDetailService.search(rohh2.getReceivedId());
                receiveTableModel.setStockReceiveDetailList(listDetail);

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
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTextFieldDateEditor) {
            ctrlName = ((JTextFieldDateEditor) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtId":
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_UP) {
                    txtRemark.requestFocus();
                } else {
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtDate":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtId.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_RIGHT) {
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
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtRemark.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtLocation.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    btnOutstanding.requestFocus();
                }
                tabToTable(e);
                break;
            case "btnOutstanding":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtId.requestFocus();
                }
                tabToTable(e);
                break;
        }
    }

    @Override
    public void save() {
        if (saveReceive()) {
            clear();
        }
    }

    @Override
    public void delete() {
        deleteReceive();
    }

    @Override
    public void newForm() {
    }

    @Override
    public void history() {
        receiveSearchDialog.setSize(Global.width - 200, Global.height - 200);
        receiveSearchDialog.setLocationRelativeTo(null);
        receiveSearchDialog.setSelectionObserver(this);
        receiveSearchDialog.setVisible(true);

    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
