/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.StockInOut;
import com.cv.inv.entity.StockInOutDetail;
import com.cv.inv.entry.common.StockInOutTableModel;
import com.cv.inv.entry.dialog.StockInOutVouSearch;
import com.cv.inv.entry.editor.LocationCellEditor;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.service.StockInOutDetailService;
import com.cv.inv.service.VouIdService;
import com.cv.inv.util.GenVouNoImpl;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.cv.inv.service.StockInOutService;
import com.cv.inv.ui.commom.VouFormatFactory;
import java.text.ParseException;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Lenovo
 */
@Component
public class StockInOutEntry extends javax.swing.JPanel implements PanelControl, SelectionObserver {

    private static final Logger log = LoggerFactory.getLogger(StockInOutEntry.class);
    @Autowired
    private StockInOutTableModel outTableModel;
    @Autowired
    private StockInOutService stockInOutService;
    @Autowired
    private VouIdService voudIdService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private StockInOutDetailService stockInOutDetailService;
    private GenVouNoImpl vouEngine = null;
    private StockInOut inOut = new StockInOut();
    @Autowired
    private StockInOutVouSearch stockInOutVouSearch;

    /**
     * Creates new form StockInOutEntry
     */
    public StockInOutEntry() {
        initComponents();
        initVoucherFormat();
    }

    private void initVoucherFormat() {
        try {
            txtBatchNo.setFormatterFactory(new VouFormatFactory());
            txtDate.setDate(Util1.getTodayDate());
        } catch (ParseException ex) {
            log.error(ex.getMessage());
        }
    }

    public void initMain() {
        genVouNo();
        initTable();
    }

    private void initTable() {
        outTableModel.addNewRow();
        outTableModel.setParent(tblStock);
        outTableModel.setInTotal(txtInTotalWt);
        outTableModel.setOutTotal(txtOutTotalWt);
        tblStock.setModel(outTableModel);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.getTableHeader().setForeground(ColorUtil.foreground);
        tblStock.getTableHeader().setBackground(ColorUtil.btnEdit);
        tblStock.setDefaultRenderer(Object.class, new TableCellRender());
        tblStock.setDefaultRenderer(Float.class, new TableCellRender());
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(150);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(5).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(6).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(7).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(8).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(9).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(10).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(2).setCellEditor(new StockCellEditor());
        tblStock.getColumnModel().getColumn(4).setCellEditor(new LocationCellEditor());
        tblStock.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(7).setCellEditor(new StockUnitEditor());
        tblStock.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());
        tblStock.getColumnModel().getColumn(10).setCellEditor(new StockUnitEditor());
        tblStock.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblStock.setCellSelectionEnabled(true);
        tblStock.changeSelection(0, 0, false, false);
        tblStock.requestFocus();
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(voudIdService, "StockInOut", Util1.getPeriod(Util1.getTodayDate()));
        txtBatchNo.setText(vouEngine.genVouNo());
    }

    private void actionKeyEnter() {
        int row = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
        int column = tblStock.getSelectedColumn();
        if (row >= 0) {
            if (column == 6) {
                StockInOutDetail stockInout = outTableModel.getStockInout(row);
                if (Util1.getFloat(stockInout.getInQty()) == 0) {
                    tblStock.setColumnSelectionInterval(8, 8);
                    tblStock.setRowSelectionInterval(row, row);
                }
            }
            tblStock.requestFocus();
        }
    }

    private void saveStockInout() {
        if (isValidEntry() && outTableModel.isValidEntry()) {
            try {
                List<StockInOutDetail> listStock = outTableModel.getListStock();
                StockInOut save = stockInOutService.save(inOut, listStock);
                if (save != null) {
                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.updateVouNo();
                    }
                }
                clear();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
                log.error("Save In Out :" + e.getMessage());
            }
        }
    }

    private void clear() {
        txtDesp.setText(null);
        txtRemark.setText(null);
        txtInTotalWt.setValue(0.0);
        txtOutTotalWt.setValue(0.0);
        outTableModel.clear();
        outTableModel.addNewRow();
        txtDate.setDate(Util1.getTodayDate());
        genVouNo();
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtBatchNo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Batch No.");
            status = false;
        } else {
            inOut.setBatchCode(txtBatchNo.getText());
            inOut.setDescription(txtDesp.getText());
            inOut.setRemark(txtRemark.getText());
            inOut.setInTotal(Util1.getFloat(txtInTotalWt.getValue()));
            inOut.setOutTotal(Util1.getFloat(txtOutTotalWt.getValue()));
            inOut.setTranDate(txtDate.getDate());
            if (lblStatus.getText().equals("NEW")) {
                inOut.setCreatedBy(Global.loginUser);
                inOut.setCreatedDate(Util1.getTodayDate());
                inOut.setCompCode(Global.compCode);
                inOut.setMacId(Global.machineId);
            } else {
                inOut.setUpdatedBy(Global.loginUser);
            }
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtBatchNo = new javax.swing.JFormattedTextField();
        lblStatus = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new com.toedter.calendar.JDateChooser();
        txtOutTotalWt = new javax.swing.JFormattedTextField();
        txtInTotalWt = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblStock.setFont(Global.textFont);
        tblStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblStock.setRowHeight(Global.tblRowHeight);
        tblStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblStockKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblStock);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Description");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Batch Code");

        txtBatchNo.setEditable(false);

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date");

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.lableFont);
        txtDate.setMaxSelectableDate(new java.util.Date(253370745114000L));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtBatchNo, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDesp, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(lblStatus)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblStatus)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtBatchNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        txtOutTotalWt.setEditable(false);
        txtOutTotalWt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtOutTotalWt.setFont(Global.amtFont);

        txtInTotalWt.setEditable(false);
        txtInTotalWt.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtInTotalWt.setFont(Global.amtFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Stock Out Total");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Stock In Total");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtInTotalWt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtOutTotalWt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOutTotalWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtInTotalWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
    }//GEN-LAST:event_formComponentShown

    private void tblStockKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblStockKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            //actionKeyEnter();
        }
    }//GEN-LAST:event_tblStockKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblStock;
    private javax.swing.JFormattedTextField txtBatchNo;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JFormattedTextField txtInTotalWt;
    private javax.swing.JFormattedTextField txtOutTotalWt;
    private javax.swing.JTextField txtRemark;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
        saveStockInout();
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
        stockInOutVouSearch.initMain();
        stockInOutVouSearch.setSize(Global.width - 300, Global.height - 300);
        stockInOutVouSearch.setObserver(this);
        stockInOutVouSearch.setLocationRelativeTo(null);
        stockInOutVouSearch.setVisible(true);
    }

    @Override
    public void print() {
    }

    @Override
    public void refresh() {
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("STOCK-SEARCH")) {
            inOut = (StockInOut) selectObj;
            List<StockInOutDetail> list = stockInOutDetailService.search(inOut.getBatchCode());
            outTableModel.setListStock(list);
            txtBatchNo.setText(inOut.getBatchCode());
            txtDate.setDate(inOut.getTranDate());
            txtDesp.setText(inOut.getDescription());
            txtRemark.setText(inOut.getRemark());
            lblStatus.setText("EDIT");

        }
    }
}
