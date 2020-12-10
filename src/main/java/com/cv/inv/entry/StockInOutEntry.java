/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.StockInOutDetail;
import com.cv.inv.entry.common.StockInOutTableModel;
import com.cv.inv.entry.editor.LocationCellEditor;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.service.VouIdService;
import com.cv.inv.util.GenVouNoImpl;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.cv.inv.service.StockInOutDetailService;

/**
 *
 * @author Lenovo
 */
@Component
public class StockInOutEntry extends javax.swing.JPanel {

    private static final Logger log = LoggerFactory.getLogger(StockInOutEntry.class);
    @Autowired
    private StockInOutTableModel outTableModel;
    @Autowired
    private StockInOutDetailService stockInOutService;
    @Autowired
    private VouIdService voudIdService;
    private GenVouNoImpl vouEngine = null;

    /**
     * Creates new form StockInOutEntry
     */
    public StockInOutEntry() {
        initComponents();
        setTodayDate();
    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void search() {
        log.info("Search Stock In Out");
        String fromDate = Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd");
        String toDate = Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd");
        String option = Util1.isNull(txtDesp.getText(), "-");
        String remak = Util1.isNull(txtRemark.getText(), "-");
        List<StockInOutDetail> listStock = stockInOutService.search(fromDate, toDate, "-", remak, option, remak);
        outTableModel.setListStock(listStock);
        outTableModel.addEmptyRow();
        requestFocusTable();

    }

    private void requestFocusTable() {
        int row = tblStock.getSelectedRowCount();
        tblStock.setRowSelectionInterval(row, row);
        tblStock.setColumnSelectionInterval(0, 0);
    }

    private void initMain() {
        genVouNo();
        initTable();
        search();
    }

    private void initTable() {
        outTableModel.addEmptyRow();
        outTableModel.setParent(tblStock);
        outTableModel.setBatchCode(txtBatchNo.getText());
        tblStock.setModel(outTableModel);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.getTableHeader().setForeground(ColorUtil.foreground);
        tblStock.getTableHeader().setBackground(ColorUtil.btnEdit);
        tblStock.setDefaultRenderer(Object.class, new TableCellRender());
        tblStock.setDefaultRenderer(Float.class, new TableCellRender());
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(30);
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(5).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(6).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(7).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(8).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(9).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(10).setPreferredWidth(20);
        tblStock.getColumnModel().getColumn(11).setPreferredWidth(150);
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
        tblStock.getColumnModel().getColumn(11).setCellEditor(new AutoClearEditor());
        tblStock.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(voudIdService, "StockInOut", Util1.getPeriod(Util1.getTodayDate()));
        txtBatchNo.setText(vouEngine.genVouNo());
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
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtBatchNo = new javax.swing.JTextField();
        btnSearch1 = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
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
        jScrollPane1.setViewportView(tblStock);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.shortCutFont);

        jLabel2.setText("From Date");

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.shortCutFont);

        jLabel3.setText("ToDate");

        jLabel4.setText("Description");

        jLabel5.setText("Remark");

        btnSearch.setBackground(ColorUtil.btnEdit);
        btnSearch.setFont(Global.lableFont);
        btnSearch.setForeground(ColorUtil.foreground);
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search-button-white.png"))); // NOI18N
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jLabel6.setText("BatchId");

        btnSearch1.setBackground(ColorUtil.btnEdit);
        btnSearch1.setFont(Global.lableFont);
        btnSearch1.setForeground(ColorUtil.foreground);
        btnSearch1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search-button-white.png"))); // NOI18N
        btnSearch1.setText("New");
        btnSearch1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearch1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtBatchNo, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDesp, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnSearch1)
                .addGap(11, 11, 11)
                .addComponent(btnSearch)
                .addGap(12, 12, 12))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtBatchNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearch)
                        .addComponent(btnSearch1))
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        initMain();
    }//GEN-LAST:event_formComponentShown

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnSearch1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearch1ActionPerformed
        // TODO add your handling code here:
        genVouNo();
    }//GEN-LAST:event_btnSearch1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearch1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblStock;
    private javax.swing.JTextField txtBatchNo;
    private javax.swing.JTextField txtDesp;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtToDate;
    // End of variables declaration//GEN-END:variables
}
