/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.report;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.Region;
import com.cv.accountswing.entity.view.VRoleMenu;
import com.cv.accountswing.service.MenuService;
import com.cv.accountswing.service.RegionService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.report.common.RegionFilterTableModel;
import com.cv.accountswing.ui.report.common.StockFilterTableModel;
import com.cv.accountswing.ui.report.common.StockReportTableModel;
import com.cv.accountswing.util.BindingUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.RelationKey;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockBalanceTmp;
import com.cv.inv.entry.editor.BrandAutoCompleter;
import com.cv.inv.entry.editor.CategoryAutoCompleter;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.RegionCellEditor;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockTypeAutoCompleter;
import com.cv.inv.entry.editor.UnitAutoCompleter;
import com.cv.inv.service.StockReportService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.cv.inv.service.SReportService;
import com.cv.inv.service.StockBalanceTmpService;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
@Component
public class StockReports extends javax.swing.JPanel implements PanelControl {

    private static final Logger log = LoggerFactory.getLogger(StockReports.class);

    @Autowired
    private StockReportTableModel reportTableModel;
    @Autowired
    private StockFilterTableModel filterTableModel;
    @Autowired
    private StockReportService stockReportService;
    @Autowired
    private SReportService reportService;
    @Autowired
    private StockBalanceTmpService tmpService;
    @Autowired
    private RegionFilterTableModel regionFilterTableModel;
    @Autowired
    private MenuService menuService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private LocationAutoCompleter locationAutoCompleter;
    private UnitAutoCompleter unitAutoCompleter;
    private StockTypeAutoCompleter stockTypeAutoCompleter;
    private CategoryAutoCompleter categoryAutoCompleter;
    private BrandAutoCompleter brandAutoCompleter;
    private boolean isShown = false;
    private String locId;
    private String unitCode;
    private String stockTypeCode;
    private String catCode;
    private String brandCode;
    private String stockCode = "";
    private String regionCode = "";
    private String startDate;
    private String endDate;

    /**
     * Creates new form StockReport
     */
    public StockReports() {
        initComponents();
        assingDefaultValue();
    }

    private void iniitMain() {
        initCombo();
        initTable();
        isShown = true;
    }

    private void initCombo() {
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        unitAutoCompleter = new UnitAutoCompleter(txtUnit, Global.listStockUnit, null);
        stockTypeAutoCompleter = new StockTypeAutoCompleter(txtStockType, Global.listStockType, null);
        categoryAutoCompleter = new CategoryAutoCompleter(txtCategory, Global.listCategory, null);
        brandAutoCompleter = new BrandAutoCompleter(txtBrand, Global.listStockBrand, null);
        List<VRoleMenu> listReports = menuService.getReports(Global.roleCode);
        BindingUtil.BindComboFilter(cboReport, listReports, null, false, false);
    }

    private void initTable() {
        initTableReport();
        initTableStockFilter();
        initTableRegionFilter();
    }

    private void initTableReport() {
        tblReport.setModel(reportTableModel);
        tblReport.getTableHeader().setFont(Global.tblHeaderFont);
        tblReport.getTableHeader().setBackground(ColorUtil.btnEdit);
        tblReport.getTableHeader().setForeground(ColorUtil.foreground);
        tblReport.setDefaultRenderer(Object.class, new TableCellRender());
    }

    private void initTableStockFilter() {
        filterTableModel.setTable(tblStockFilter);
        filterTableModel.addNewRow();
        tblStockFilter.setModel(filterTableModel);
        tblStockFilter.getTableHeader().setFont(Global.tblHeaderFont);
        tblStockFilter.getTableHeader().setBackground(ColorUtil.btnEdit);
        tblStockFilter.getTableHeader().setForeground(ColorUtil.foreground);
        tblStockFilter.setDefaultRenderer(Object.class, new TableCellRender());
        tblStockFilter.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
    }

    private void initTableRegionFilter() {
        regionFilterTableModel.setTable(tblRegion);
        regionFilterTableModel.addNewRow();
        tblRegion.setModel(regionFilterTableModel);
        tblRegion.getTableHeader().setFont(Global.tblHeaderFont);
        tblRegion.getTableHeader().setBackground(ColorUtil.btnEdit);
        tblRegion.getTableHeader().setForeground(ColorUtil.foreground);
        tblRegion.setDefaultRenderer(Object.class, new TableCellRender());
        tblRegion.getColumnModel().getColumn(0).setCellEditor(new RegionCellEditor(regionService));
    }

    private void assingDefaultValue() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void report() {
        initializeParameters();
        int selectRow = tblReport.convertRowIndexToModel(tblReport.getSelectedRow());
        if (selectRow >= 0) {
            VRoleMenu report = reportTableModel.getReport(selectRow);
            String name = report.getMenuName();
            switch (name) {
                case "Stock Balance":
                    reportStockBalance();
                    break;
                case "Sale By Stock":
                    reportSaleByStock(report.getMenuUrl());
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(Global.parentForm, "Choose Report.");
        }

    }

    private Float getChangeWeight(String fromUnit, String toUnit, String pattern, Float weight) {
        RelationKey key = new RelationKey(fromUnit, toUnit, pattern);
        Float factor = Global.hmRelation.get(key);
        if (factor != null) {
            weight = weight * factor;
        } else {
            key = new RelationKey(toUnit, fromUnit, pattern);
            factor = Global.hmRelation.get(key);
            if (factor != null) {
                weight = weight / factor;
            } else {
                //JOptionPane.showMessageDialog(Global.parentForm, "Can't see with this unit.");
                weight = null;
            }
        }
        return weight;
    }

    private void initializeParameters() {
        stockCode = "";
        regionCode = "";
        startDate = Util1.toDateStr(txtFromDate.getDate(), "yyyy-MM-dd");
        endDate = Util1.toDateStr(txtToDate.getDate(), "yyyy-MM-dd");
        if (locationAutoCompleter.getLocation()
                != null && !txtLocation.getText().isEmpty()) {
            locId = locationAutoCompleter.getLocation().getLocationCode();
        } else {
            locId = "-";
        }

        if (unitAutoCompleter.getStockUnit()
                != null && !txtUnit.getText().isEmpty()) {
            unitCode = unitAutoCompleter.getStockUnit().getItemUnitCode();
        } else {
            unitCode = "-";
        }

        if (stockTypeAutoCompleter.getStockType()
                != null && !txtStockType.getText().isEmpty()) {
            stockTypeCode = stockTypeAutoCompleter.getStockType().getItemTypeCode();
        } else {
            stockTypeCode = "-";
        }

        if (categoryAutoCompleter.getCategory()
                != null && !txtCategory.getText().isEmpty()) {
            catCode = categoryAutoCompleter.getCategory().getCatCode();
        } else {
            catCode = "-";
        }

        if (brandAutoCompleter.getBrand()
                != null && !txtBrand.getText().isEmpty()) {
            brandCode = brandAutoCompleter.getBrand().getBrandCode();
        } else {
            brandCode = "-";
        }
        //stock code
        List<Stock> listStock = filterTableModel.getListStock();

        if (!listStock.isEmpty()) {
            stockCode = listStock.stream().filter(stock -> (stock.getStockCode() != null)).map(stock -> "'" + stock.getStockCode() + "'" + ",").reduce(stockCode, String::concat);
            if (!stockCode.isEmpty()) {
                stockCode = stockCode.substring(0, stockCode.length() - 1);
            }
        }
        if (stockCode.isEmpty()) {
            stockCode = "-";
        }
        //region coe
        List<Region> listRegion = regionFilterTableModel.getListRegion();
        if (!listRegion.isEmpty()) {
            listRegion.stream().filter(reg -> (reg.getRegCode() != null)).forEachOrdered(reg -> {
                regionCode += "'" + reg.getRegCode() + "',";
            });
            if (!regionCode.isEmpty()) {
                regionCode = regionCode.substring(0, regionCode.length() - 1);
            } else {
                regionCode = "-";
            }
        }
    }

    private void reportStockBalance() {
        reportService.generateStockBalance(stockCode, locId, Global.compCode, Global.machineId.toString());
        List<StockBalanceTmp> listStockBalance = tmpService.search(Global.machineId.toString());
        if (!unitCode.equals("-")) {
            listStockBalance.stream().map(tmp -> {
                String pattern = tmp.getStock().getPattern().getPatternCode();
                String smallUnit = tmp.getUnit();
                Float changeWt = getChangeWeight(smallUnit, unitCode, pattern, tmp.getTotalWt());
                if (changeWt != null) {
                    tmp.setChangeWt(changeWt);
                    tmp.setChangeUnit(unitCode);
                } else {

                }
                if (Util1.getFloat(tmp.getChangeWt()) % 1 != 0) {
                    int num = tmp.getChangeWt().intValue();
                    float decimal = (num - tmp.getChangeWt()) * -1;
                    tmp.setChangeWt2(getChangeWeight(unitCode, smallUnit, pattern, decimal));
                    tmp.setChangeUnit2(smallUnit);
                }
                return tmp;
            }).forEachOrdered(tmp -> {
                tmpService.save(tmp);
            });
        }
        log.info("stock balance report ready.");
        String reportPath = Global.sysProperties.get("system.report.path");
        String fontPath = Global.sysProperties.get("system.font.path");
        String compName = Global.sysProperties.get("system.report.company");
        reportPath = reportPath + "\\StockBalance";
        Map<String, Object> parameters = new HashMap();
        parameters.put("comp_name", compName);
        parameters.put("mac_id", Global.machineId);
        parameters.put("brand_code", brandCode);
        parameters.put("cat_code", catCode);
        parameters.put("stock_type_code", stockTypeCode);
        reportService.reportViewer(reportPath, reportPath, fontPath, parameters);

    }

    private void reportSaleByStock(String reportName) {
        reportService.generateSaleByStock(stockCode, regionCode, Global.machineId.toString());
        String regionFilter = "";
        List<Region> listRegion = regionFilterTableModel.getListRegion();
        if (listRegion.size() <= 1) {
            listRegion = regionService.search("-", "-", Global.compCode, "-");
        }
        for (Region reg : listRegion) {
            if (reg.getRegCode() != null) {
                regionFilter += reg.getRegionName() + ":";
            }
        }
        String reportPath = Global.sysProperties.get("system.report.path");
        String fontPath = Global.sysProperties.get("system.font.path");
        reportPath = reportPath + "\\" + reportName;
        Map<String, Object> parameters = new HashMap();
        parameters.put("from_date", startDate);
        parameters.put("to_date", endDate);
        parameters.put("machine_id", Global.machineId);
        parameters.put("region_filter", regionFilter);
        reportService.reportViewer(reportPath, reportPath, fontPath, parameters);
    }

    private void searchReports() {
        log.info("Search Reports");
        if (cboReport.getSelectedItem() instanceof VRoleMenu) {
            VRoleMenu menu = (VRoleMenu) cboReport.getSelectedItem();
            List<VRoleMenu> listReports = menuService.getReportList(Global.roleCode, menu.getKey().getMenuCode());
            reportTableModel.setListReport(listReports);
        }
    }

    private void deleteStock() {
        if (tblStockFilter.getSelectedRow() >= 0) {
            int row = tblStockFilter.convertRowIndexToModel(tblStockFilter.getSelectedRow());
            filterTableModel.delete(row);

        }
    }

    private void deleteRegion() {
        if (tblRegion.getSelectedRow() >= 0) {
            int row = tblRegion.convertRowIndexToModel(tblRegion.getSelectedRow());
            regionFilterTableModel.delete(row);
            if (tblRegion.getCellEditor() != null) {
                tblRegion.getCellEditor().stopCellEditing();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblReport = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtStockType = new javax.swing.JTextField();
        txtCategory = new javax.swing.JTextField();
        txtBrand = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtUnit = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStockFilter = new javax.swing.JTable();
        cboReport = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblRegion = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblReport.setFont(Global.textFont);
        tblReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblReport.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblReport);

        jButton1.setText("Print");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("From");

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.shortCutFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("To");

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.shortCutFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Stock Type");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Category");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Brand");

        txtStockType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockTypeFocusGained(evt);
            }
        });

        txtCategory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCategoryFocusGained(evt);
            }
        });

        txtBrand.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBrandFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Location");

        txtLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLocationFocusGained(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Unit");

        txtUnit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUnitFocusGained(evt);
            }
        });

        tblStockFilter.setFont(Global.textFont);
        tblStockFilter.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblStockFilter.setRowHeight(Global.tblRowHeight);
        tblStockFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblStockFilterKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblStockFilter);

        cboReport.setFont(Global.textFont);
        cboReport.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboReport.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboReportItemStateChanged(evt);
            }
        });
        cboReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboReportActionPerformed(evt);
            }
        });

        tblRegion.setFont(Global.textFont);
        tblRegion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblRegion.setRowHeight(Global.tblRowHeight);
        tblRegion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblRegionKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblRegion);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboReport, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtStockType)
                                .addComponent(txtCategory)
                                .addComponent(txtBrand)
                                .addComponent(txtLocation)
                                .addComponent(txtUnit)))
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jButton1))
                .addContainerGap(253, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(cboReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtStockType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addContainerGap())
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            iniitMain();
        }
    }//GEN-LAST:event_formComponentShown

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        report();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtStockTypeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockTypeFocusGained
        // TODO add your handling code here:
        txtStockType.selectAll();
    }//GEN-LAST:event_txtStockTypeFocusGained

    private void txtCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCategoryFocusGained
        // TODO add your handling code here:
        txtCategory.selectAll();
    }//GEN-LAST:event_txtCategoryFocusGained

    private void txtBrandFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBrandFocusGained
        // TODO add your handling code here:
        txtBrand.selectAll();
    }//GEN-LAST:event_txtBrandFocusGained

    private void txtLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLocationFocusGained
        // TODO add your handling code here:
        txtLocation.selectAll();
    }//GEN-LAST:event_txtLocationFocusGained

    private void txtUnitFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUnitFocusGained
        // TODO add your handling code here:
        txtUnit.selectAll();
    }//GEN-LAST:event_txtUnitFocusGained

    private void cboReportItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboReportItemStateChanged
        // TODO add your handling code here:

    }//GEN-LAST:event_cboReportItemStateChanged

    private void cboReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboReportActionPerformed
        // TODO add your handling code here:
        searchReports();
    }//GEN-LAST:event_cboReportActionPerformed

    private void tblStockFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblStockFilterKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            deleteStock();
        }
    }//GEN-LAST:event_tblStockFilterKeyReleased

    private void tblRegionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblRegionKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            deleteRegion();
        }
    }//GEN-LAST:event_tblRegionKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboReport;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblRegion;
    private javax.swing.JTable tblReport;
    private javax.swing.JTable tblStockFilter;
    private javax.swing.JTextField txtBrand;
    private javax.swing.JTextField txtCategory;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtStockType;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JTextField txtUnit;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
        isShown = false;
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
