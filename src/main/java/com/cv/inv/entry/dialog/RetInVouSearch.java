/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.dialog;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CustomerAutoCompleter;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.RetInHis;
import com.cv.inv.entity.RetOutHis;
import com.cv.inv.entry.common.CodeTableModel;
import com.cv.inv.entry.common.RetInVouSearchTableModel;
import com.cv.inv.entry.common.RetOutVouSearchTableModel;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.service.RetInService;
import com.cv.inv.service.RetOutService;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class RetInVouSearch extends javax.swing.JDialog implements KeyListener, SelectionObserver {

    private static final Logger log = LoggerFactory.getLogger(RetInVouSearch.class);
    private SelectionObserver observer;
    private int selectRow = - 1;
    private LocationAutoCompleter locationAutoCompleter;
    private CustomerAutoCompleter traderAutoCompleter;
    private String panelName;
    private String titleName;
    @Autowired
    private RetInVouSearchTableModel retInVouSearchTableModel;
    @Autowired
    private RetOutVouSearchTableModel retOutVouSearchTableModel;
    @Autowired
    private CodeTableModel codeTableModel;
    @Autowired
    private RetInService retInService;
    @Autowired
    private RetOutService retOutService;

    /**
     * Creates new form ItemTypeSetupDialog
     */
    public RetInVouSearch() {
        super(Global.parentForm, true);
        initComponents();
        initKeyListener();
    }

    public void initMain() {
        initCodeTable();
        initVouTable();
        initCombo();
        setTodayDate();
        actionMapping();
        assignDefaultValue();
        search();

    }

    private void initKeyListener() {
        txtCus.addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtLocation.addKeyListener(this);
    }

    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public void setSelectionObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    private void initCodeTable() {
        tblStock.setModel(codeTableModel);
        codeTableModel.setParent(tblStock);
        codeTableModel.addNewRow();
        tblStock.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblStock.getTableHeader().setForeground(ColorUtil.foreground);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblStock.setDefaultRenderer(String.class, new TableCellRender());
        tblStock.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblStock.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
    }

    private void initVouTable() {
        switch (panelName) {
            case "Return In":
                tblVou.setModel(retInVouSearchTableModel);
                break;
            case "Return Out":
                tblVou.setModel(retOutVouSearchTableModel);
                break;
            default:
                break;

        }

        retInVouSearchTableModel.setParent(tblVou);
        tblVou.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblVou.getTableHeader().setForeground(ColorUtil.foreground);
        tblVou.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblVou.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblVou.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblVou.getColumnModel().getColumn(3).setPreferredWidth(20);
        tblVou.getColumnModel().getColumn(4).setPreferredWidth(30);
        tblVou.setDefaultRenderer(String.class, new TableCellRender());
        tblVou.setDefaultRenderer(Object.class, new TableCellRender());
        tblVou.setDefaultRenderer(Double.class, new TableCellRender());
        tblVou.setDefaultRenderer(Float.class, new TableCellRender());
        //tblVou.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
        tblVou.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");

    }

    private void clear() {
        //assignDefaultValue();
        txtCus.setText(null);
        txtVouNo.setText(null);
        setTodayDate();
        switch (panelName) {
            case "Return In":
                retInVouSearchTableModel.clearList();

                break;
            case "Return Out":
                retOutVouSearchTableModel.clearList();
                break;

            default:
                break;

        }
        codeTableModel.clearList();

    }

    private void assignDefaultValue() {
        locationAutoCompleter.setLocation(Global.defaultLocation);
        traderAutoCompleter.setTrader(Global.defaultCustomer);

    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void select() {
        if (tblVou.getSelectedRow() >= 0) {
            switch (panelName) {
                case "Return In":
                    int row = tblVou.convertRowIndexToModel(tblVou.getSelectedRow());
                    RetInHis inVou = retInVouSearchTableModel.getSelectVou(row);
                    observer.selected("RETIN-SELECTED", inVou);
                    break;
                case "Return Out":
                    int oRow = tblVou.convertRowIndexToModel(tblVou.getSelectedRow());
                    RetOutHis outVou = retOutVouSearchTableModel.getSelectVou(oRow);
                    observer.selected("RETOUT-SELECTED", outVou);
                    break;
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Please select.");
        }
    }

    private void search() {
        String locId;
        String cusId;
        String vouNo;
        String filterCodes = codeTableModel.getFilterCodeStr();
        String fDate = Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd");
        String tDate = Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd");
        if (locationAutoCompleter.getLocation() == null || txtLocation.getText().isEmpty()) {
            locId = "-";
        } else {
            locId = locationAutoCompleter.getLocation().getLocationCode();
        }
        if (traderAutoCompleter.getTrader() == null || txtCus.getText().isEmpty()) {
            cusId = "-";
        } else {
            cusId = traderAutoCompleter.getTrader().getCode();
        }
        if (txtVouNo.getText().isEmpty()) {
            vouNo = "-";
        } else {
            vouNo = txtVouNo.getText();
        }
        if (filterCodes == null) {
            filterCodes = "-";
        }
        try {
            switch (panelName) {
                case "Return In":
                    log.info("Return In Voucher Searching.");
                    List<RetInHis> listRetIn = retInService.search(fDate, tDate,
                            cusId, locId, vouNo, filterCodes);
                    retInVouSearchTableModel.setListRetInHis(listRetIn);
                    break;
                case "Return Out":
                    log.info("Return Out Voucher Searching.");
                    List<RetOutHis> listRetOut = retOutService.search(fDate, tDate,
                            cusId, locId, vouNo, filterCodes);
                    retOutVouSearchTableModel.setListReturnHis(listRetOut);
                    break;
                default:
                    break;
            }

            lblTtlRecord.setText("Total Record:" + retInVouSearchTableModel.getRowCount());

        } catch (Exception ex) {
            log.error("Search RetInVouSearch.." + ex.getMessage());
        }

    }

    private void actionMapping() {
        tblStock.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblStock.getActionMap().put("F8-Action", actionStockDelete);
    }
    private final Action actionStockDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblStock.getSelectedRow() >= 0) {
                codeTableModel.delete(tblStock.getSelectedRow());
            }
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        txtCus = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblVou = new javax.swing.JTable();
        lblTtlRecord = new javax.swing.JLabel();
        Search = new javax.swing.JButton();
        btnSelect = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Return In Voucher Search");
        setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        setName(""); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("From");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Location");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Customer");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("VouNo");

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.lableFont);
        txtFromDate.setName("txtFromDate"); // NOI18N

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.lableFont);
        txtToDate.setName("txtToDate"); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("To");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N

        txtCus.setFont(Global.textFont);
        txtCus.setName("txtCus"); // NOI18N

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        tblStock.setFont(Global.textFont);
        tblStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {}
            },
            new String [] {

            }
        ));
        tblStock.setRowHeight(Global.tblRowHeight);
        jScrollPane4.setViewportView(tblStock);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel6)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                                        .addGap(1, 1, 1))
                                    .addComponent(txtLocation)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txtCus)))
                        .addGap(7, 7, 7))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtVouNo)
                                .addGap(1, 1, 1)))
                        .addContainerGap())))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel3, jLabel4, jLabel5});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addContainerGap())
        );

        tblVou.setFont(Global.textFont);
        tblVou.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblVou.setRowHeight(Global.tblRowHeight);
        tblVou.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVouMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblVou);

        lblTtlRecord.setText("Total Records : 0");

        Search.setBackground(ColorUtil.mainColor);
        Search.setFont(Global.lableFont);
        Search.setForeground(ColorUtil.foreground);
        Search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search-button-white.png"))); // NOI18N
        Search.setText("Search");
        Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchActionPerformed(evt);
            }
        });

        btnSelect.setBackground(ColorUtil.mainColor);
        btnSelect.setFont(Global.lableFont);
        btnSelect.setForeground(ColorUtil.foreground);
        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/select-button.png"))); // NOI18N
        btnSelect.setText("Select");
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        btnClear.setBackground(ColorUtil.btnEdit);
        btnClear.setFont(Global.lableFont);
        btnClear.setForeground(ColorUtil.foreground);
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clear-button-white.png"))); // NOI18N
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTtlRecord)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSelect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnSelect)
                                .addComponent(Search)
                                .addComponent(btnClear))
                            .addComponent(lblTtlRecord)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_SearchActionPerformed

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        // TODO add your handling code here:
        select();
    }//GEN-LAST:event_btnSelectActionPerformed

    private void tblVouMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVouMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            select();
        }
    }//GEN-LAST:event_tblVouMouseClicked

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Search;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSelect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblTtlRecord;
    private javax.swing.JTable tblStock;
    private javax.swing.JTable tblVou;
    private javax.swing.JTextField txtCus;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtLocation;
    private com.toedter.calendar.JDateChooser txtToDate;
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
        if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTextFieldDateEditor) {
            ctrlName = ((JTextFieldDateEditor) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtFromDate":
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtToDate.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (sourceObj != null) {
                        String date = ((JTextFieldDateEditor) sourceObj).getText();
                        if (date.length() == 8) {
                            String toFormatDate = Util1.toFormatDate(date);
                            txtFromDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                        }
                        txtCus.requestFocus();
                    }
                }
                break;
            case "txtToDate":
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtLocation.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (sourceObj != null) {
                        String date = ((JTextFieldDateEditor) sourceObj).getText();
                        if (date.length() == 8) {
                            String toFormatDate = Util1.toFormatDate(date);
                            txtToDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                        }
                        txtLocation.requestFocus();
                    }
                }
                break;
            case "txtCus":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (txtCus.getText().isEmpty()) {
                        traderAutoCompleter.closePopup();
                    }
                }
                break;
            case "txtVouNo":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    tabToTable(e);
                }
                break;
            case "txtLocation":
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (txtLocation.getText().isEmpty()) {
                        locationAutoCompleter.closePopup();
                    }
                }

        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            tblStock.requestFocus();
        }
    }

    private void initCombo() {
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        locationAutoCompleter.setSelectionObserver(this);
        traderAutoCompleter = new CustomerAutoCompleter(txtCus, Global.listCustomer, null);
        locationAutoCompleter.setSelectionObserver(this);

    }

    @Override
    public void selected(Object source, Object selectObj) {
    }
}
