/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.ui.util;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.RetInHis;
import com.cv.inv.entity.view.VRetOut;
import com.cv.inv.entry.common.CodeTableModel;
import com.cv.inv.entry.common.RetInVouSearchTableModel;
import com.cv.inv.entry.common.RetOutVouSearchTableModel;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.service.LocationService;
import com.cv.inv.service.RetInService;
import com.cv.inv.service.VRetOutService;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(RetInVouSearch.class);
    private SelectionObserver observer;
    private int selectRow = - 1;
    private LocationAutoCompleter locationAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private final JDialog parent = this;
    private String panelName;
    private String titleName;
    @Autowired
    private RetInVouSearchTableModel retInVouSearchTableModel;
    @Autowired
    private RetOutVouSearchTableModel retOutVouSearchTableModel;
    @Autowired
    private CodeTableModel codeTableModel;
    @Autowired
    private LocationService locationService;
    @Autowired
    private RetInService retInService;
    @Autowired
    private VRetOutService vRetOutService;

    /**
     * Creates new form ItemTypeSetupDialog
     */
    public RetInVouSearch() {
        super(new Frame(), true);
        initComponents();
    }

    public void initMain() {
        initCodeTable();
        initVouTable();
        initKeyListener();
        initCombo();
        setTodayDate();
        //assignDefaultValue();
        initKeyListener();
        addSelectionListenerTblVou();
        actionMapping();

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

    private void initKeyListener() {

    }

    private void initCodeTable() {
        tblStock.setModel(codeTableModel);
        codeTableModel.setParent(tblStock);
        codeTableModel.addNewRow();
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
        String locId = Global.sysProperties.get("system.default.location");
        Location location = locationService.findById(locId);
        locationAutoCompleter.setLocation(location);

    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void search() {
        String locId;
        String cusId;
        String vouNo;
        String filterCodes = codeTableModel.getFilterCodeStr();
        String fDate = Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd HH:mm:ss");
        String tDate = Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd HH:mm:ss");
        if (locationAutoCompleter.getLocation() != null) {
            locId = locationAutoCompleter.getLocation().getLocationId().toString();
        } else {
            locId = "-";
        }
        if (traderAutoCompleter.getTrader() != null) {
            cusId = String.valueOf(traderAutoCompleter.getTrader().getId());

        } else {
            cusId = "-";
        }
        if (!txtVouNo.getText().equals("")) {
            vouNo = txtVouNo.getText();
        } else {
            vouNo = "-";
        }
        if (filterCodes == null) {
            filterCodes = "-";
        }
        try {
            switch (panelName) {
                case "Return In":
                    List<RetInHis> listRetIn = retInService.search(fDate, tDate,
                            cusId, locId, vouNo, filterCodes);
                    retInVouSearchTableModel.setListRetInHis(listRetIn);
                    break;
                case "Return Out":
                    List<VRetOut> listRetOut = vRetOutService.search(fDate, tDate,
                            cusId, locId, vouNo, filterCodes, String.valueOf(Global.compId));
                    retOutVouSearchTableModel.setListGl(listRetOut);
                    break;

                default:
                    break;

            }

            lblTtlRecord.setText("Total Record:" + retInVouSearchTableModel.getRowCount());

        } catch (Exception ex) {
            LOGGER.error("Search RetInVouSearch.." + ex.getMessage());
        }

    }

    private void addSelectionListenerTblVou() {
        //Define table selection model to single row selection.
        tblVou.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblVou.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectRow = tblVou.getSelectedRow();
            }
        });
    }

    private void actionMapping() {
        tblStock.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblStock.getActionMap().put("F8-Action", actionStockDelete);
    }
    private Action actionStockDelete = new AbstractAction() {

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
        jPanel2 = new javax.swing.JPanel();
        btnSelect = new javax.swing.JButton();
        Search = new javax.swing.JButton();
        lblTtlRecord = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblVou = new javax.swing.JTable();
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

        jLabel2.setText("Date");

        jLabel3.setText("Location");

        jLabel4.setText("Customer");

        jLabel5.setText("VouNo");

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.lableFont);
        txtFromDate.setName("txtFromDate"); // NOI18N

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.lableFont);
        txtToDate.setName("txtToDate"); // NOI18N

        jLabel6.setText("To");

        txtLocation.setName("txtLocation"); // NOI18N

        txtCus.setName("txtCus"); // NOI18N

        txtVouNo.setName("txtVouNo"); // NOI18N

        tblStock.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(tblStock);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(txtVouNo))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(27, 27, 27)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addGap(12, 12, 12)
                                .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnSelect.setText("Select");
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        Search.setText("Search");
        Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchActionPerformed(evt);
            }
        });

        lblTtlRecord.setText("Total Records : 0");

        tblVou.setModel(new javax.swing.table.DefaultTableModel(
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
        tblVou.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVouMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblVou);

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblTtlRecord)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                        .addComponent(Search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSelect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear)
                        .addContainerGap())
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelect)
                    .addComponent(Search)
                    .addComponent(lblTtlRecord)
                    .addComponent(btnClear)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        if (evt.getClickCount() == 2 && selectRow >= 0) {
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
    private javax.swing.JPanel jPanel2;
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
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtFromDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtCus.requestFocus();
                }
                break;
            case "txtToDate":
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtLocation.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtToDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtLocation.requestFocus();
                }
                break;
            case "txtCus":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouNo.requestFocus();
                }
                break;
            case "txtVouNo":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    tabToTable(e);
                }
                break;

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
        traderAutoCompleter = new TraderAutoCompleter(txtCus, Global.listTrader, null);
        locationAutoCompleter.setSelectionObserver(this);

    }

    @Override
    public void selected(Object source, Object selectObj) {
    }

    private void select() {
        if (selectRow >= 0) {
            switch (panelName) {
                case "Return In":
                    observer.selected("RetInVouList",
                            retInVouSearchTableModel.getSelectVou(tblVou.convertRowIndexToModel(selectRow)));

                    break;
                case "Return Out":
                    observer.selected("RetOutVouList",
                            retOutVouSearchTableModel.getSelectVou(tblVou.convertRowIndexToModel(selectRow)));
                    break;
                default:
                    break;
            }

            if (parent instanceof JDialog) {
                ((JDialog) parent).dispose();
            }
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Please select return in voucher.",
                    "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

}
