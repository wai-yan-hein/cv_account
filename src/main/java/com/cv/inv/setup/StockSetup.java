/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.FilterObserver;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.StartWithRowFilter;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.util.BindingUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Category;
import com.cv.inv.entity.StockBrand;
import com.cv.inv.entity.StockType;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.UnitPattern;
import com.cv.inv.service.StockService;
import com.cv.inv.service.UnitPatternService;
import com.cv.inv.setup.dialog.CategorySetupDialog;
import com.cv.inv.setup.dialog.CharacterNoSetupDialog;
import com.cv.inv.setup.dialog.StockBrandSetupDialog;
import com.cv.inv.setup.dialog.StockTypeSetupDialog;
import com.cv.inv.setup.dialog.StockUnitSetupDailog;
import com.cv.inv.setup.dialog.common.StockTableModel;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class StockSetup extends javax.swing.JPanel implements KeyListener, PanelControl, FilterObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockSetup.class);
    Image image = new ImageIcon(getClass().getResource("/images/new-button.png")).getImage();
    private int selectRow = -1;
    @Autowired
    private StockTypeSetupDialog itemTypeSetupDialog;
    @Autowired
    private CategorySetupDialog categorySetupDailog;
    @Autowired
    private StockBrandSetupDialog itemBrandDailog;
    @Autowired
    private StockUnitSetupDailog itemUnitSetupDailog;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockTableModel stockTableModel;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private StockImportDialog importDialog;
    @Autowired
    private UnitPatternService unitPatternService;
    private Stock stock = new Stock();
    private LoadingObserver loadingObserver;
    private boolean isShown = false;
    private TableFilterHeader filterHeader;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
        clear();
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    /**
     * Creates new form StockSetup
     */
    public StockSetup() {
        initComponents();
        initKeyListener();
    }

    private void initMain() {
        txtStockCode.requestFocus();
        initCombo();
        initTable();
        searchStock();
        clear();
        isShown = true;

    }

    private void initTable() {
        stockTableModel.setListStock(Global.listStock);
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblStock.getTableHeader().setForeground(ColorUtil.foreground);
        tblStock.setModel(stockTableModel);

        tblStock.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblStock.setDefaultRenderer(Object.class, new TableCellRender());
        tblStock.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblStock.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblStock.getSelectedRow() >= 0) {
                    selectRow = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
                    setStock(selectRow);
                }
            }
        });
        filterHeader = new TableFilterHeader(tblStock, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);

    }

    private void setStock(int row) {
        stock = stockTableModel.getStock(row);
        txtBarCode.setText(stock.getBarcode());
        //txtExpire.setText(stock.getExpireDate());
        txtPurPrice.setText(Util1.getString(stock.getPurPrice()));
        txtRemark.setText(stock.getRemark());
        txtShortName.setText(stock.getShortName());
        txtStockCode.setText(stock.getUserCode());
        txtStockName.setText(stock.getStockName());
        chkActive.setSelected(Util1.getBoolean(stock.getIsActive()));
        cboBrand.setSelectedItem(stock.getBrand());
        cboCategory.setSelectedItem(stock.getCategory());
        txtSaleWt.setText(stock.getSaleWeight().toString());
        cboSaleUnit.setSelectedItem(stock.getSaleUnit());
        cboStockType.setSelectedItem(stock.getStockType());
        cboUnitPattern.setSelectedItem(stock.getPattern());
        txtPurWt.setText(stock.getPurWeight().toString());
        cboPurUnit.setSelectedItem(stock.getPurUnit());
        txtSalePrice.setText(Util1.getString(stock.getSalePriceN()));
        txtSalePriceA.setText(Util1.getString(stock.getSalePriceA()));
        txtSalePriceB.setText((Util1.getString(stock.getSalePriceB())));
        txtSalePriceC.setText(Util1.getString(stock.getSalePriceC()));
        txtSalePriceD.setText(Util1.getString(stock.getSalePriceD()));
        txtSalePriceStd.setText(Util1.getString(stock.getSttCostPrice()));
        txtExpDate.setDate(stock.getExpireDate());
        txtStockCode.setEnabled(false);
        lblStatus.setText("EDIT");
    }

    private void searchStock() {
        Global.listStock = stockService.findAll();
        stockTableModel.setListStock(Global.listStock);
    }

    private void setStockSize() {
        txtTotalCount.setText(String.valueOf(Global.listStock.size()));
    }

    private void initCombo() {
        BindingUtil.BindComboFilter(cboStockType, Global.listStockType, null, true, false);
        BindingUtil.BindComboFilter(cboCategory, Global.listCategory, null, true, false);
        BindingUtil.BindComboFilter(cboBrand, Global.listStockBrand, null, true, false);
        BindingUtil.BindComboFilter(cboSaleUnit, Global.listStockUnit, null, true, false);
        BindingUtil.BindComboFilter(cboPurUnit, Global.listStockUnit, null, true, false);
        BindingUtil.BindComboFilter(cboUnitPattern, unitPatternService.findAll(), null, true, false);

    }

    private boolean isValidEntry() {
        boolean status = true;

        if (txtStockName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stock name must not be blank.",
                    "Stock name.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtStockName.requestFocus();
        } else if (cboStockType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "You must choose stock type.",
                    "Stock Type.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboStockType.requestFocus();
        } else if (cboUnitPattern.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Stock Unit Pattern cannot be blank.",
                    "Stock Unit Pattern.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtStockName.requestFocus();
        } else if (Util1.getDouble(txtPurWt.getText()) <= 0) {
            JOptionPane.showMessageDialog(this, "Purchase Weight  cannot be blank.",
                    "Stock Unit Pattern.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtPurWt.requestFocus();
        } else if (cboPurUnit.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Purchase Unit  cannot be blank.",
                    "Stock Unit Pattern.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboPurUnit.requestFocus();
        } else if (Util1.getDouble(txtSaleWt.getText()) <= 0) {
            JOptionPane.showMessageDialog(this, "Sale Weight  cannot be blank.",
                    "Stock Unit Pattern.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboPurUnit.requestFocus();
        } else if (isDuplicate()) {
            JOptionPane.showMessageDialog(this, "Duplicate stock code.",
                    "Stock Code.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else {
            stock.setExpireDate(txtExpDate.getDate());
            stock.setUserCode(txtStockCode.getText().trim());
            stock.setStockType((StockType) cboStockType.getSelectedItem());
            stock.setStockName(txtStockName.getText().trim());
            stock.setCategory((Category) cboCategory.getSelectedItem());
            stock.setBrand((StockBrand) cboBrand.getSelectedItem());
            stock.setIsActive(chkActive.isSelected());
            stock.setBarcode(txtBarCode.getText().trim());
            stock.setShortName(txtShortName.getText().trim());
            stock.setPurPrice(Util1.getFloat(txtPurPrice.getText()));
            stock.setPurWeight(Util1.getFloat(txtPurWt.getText()));
            stock.setPurUnit((StockUnit) cboPurUnit.getSelectedItem());
            stock.setSaleWeight(Util1.getFloat(txtSaleWt.getText()));
            stock.setSaleUnit((StockUnit) cboSaleUnit.getSelectedItem());
            stock.setPattern((UnitPattern) cboUnitPattern.getSelectedItem());
            stock.setSalePriceN(Util1.getFloat(txtSalePrice.getText()));
            stock.setSalePriceA(Util1.getFloat(txtSalePriceA.getText()));
            stock.setSalePriceB(Util1.getFloat(txtSalePriceB.getText()));
            stock.setSalePriceC(Util1.getFloat(txtSalePriceC.getText()));
            stock.setSalePriceD(Util1.getFloat(txtSalePriceD.getText()));
            stock.setSttCostPrice(Util1.getFloat(txtSalePriceStd.getText()));

            if (lblStatus.getText().equals("NEW")) {
                stock.setMacId(Global.machineId);
                stock.setCompCode(Global.compCode);
                stock.setCreatedDate(Util1.getTodayDate());
                stock.setCreatedBy(Global.loginUser);
            } else {
                stock.setUpdatedBy(Global.loginUser);
            }
        }

        return status;
    }

    private void saveStock() {
        if (isValidEntry()) {
            Stock saveStock = stockService.save(stock);
            if (saveStock != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if (lblStatus.getText().equals("NEW")) {
                    Global.listStock.add(saveStock);
                    setStockSize();

                } else {
                    Global.listStock.set(selectRow, saveStock);
                }
                clear();
            }
        }
    }

    private boolean isDuplicate() {
        boolean status = false;
        /* String stockCode = txtStockCode.getText();
        if (!stockCode.isEmpty()) {
            if (lblStatus.getText().equals("NEW")) {
                Stock s = stockService.findById(stockCode);
                if (s != null) {
                    status = true;
                }
            }
        }*/
        return status;
    }

    public void clear() {
        txtBarCode.setText(null);
        txtExpDate.setDate(null);
        txtRemark.setText("");
        txtShortName.setText(null);
        txtStockCode.setText(null);
        txtStockName.setText(null);
        chkActive.setSelected(true);
        txtPurPrice.setText(null);
        cboBrand.setSelectedItem(null);
        cboCategory.setSelectedItem(null);
        cboPurUnit.setSelectedItem(null);
        cboSaleUnit.setSelectedItem(null);
        cboStockType.setSelectedItem(null);
        cboUnitPattern.setSelectedItem(null);
        txtSalePriceStd.setText(null);
        txtPurWt.setText(null);
        txtSalePrice.setText(null);
        txtSalePriceA.setText(null);
        txtSalePriceB.setText(null);
        txtSalePriceC.setText(null);
        txtSalePriceD.setText(null);
        txtSaleWt.setText(null);
        lblStatus.setText("NEW");
        txtStockCode.setEnabled(true);
        txtStockCode.requestFocus();
        stockTableModel.refresh();
        stock = new Stock();

    }

    private void initKeyListener() {
        txtBarCode.addKeyListener(this);
        txtExpDate.getDateEditor().getUiComponent().setName("txtExpDate");
        txtExpDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtSalePriceStd.addKeyListener(this);
        txtSalePrice.addKeyListener(this);
        txtSalePriceA.addKeyListener(this);
        txtSalePriceB.addKeyListener(this);
        txtSalePriceC.addKeyListener(this);
        txtSalePriceD.addKeyListener(this);
        txtPurPrice.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtShortName.addKeyListener(this);
        txtStockCode.addKeyListener(this);
        txtStockName.addKeyListener(this);
        txtTotalCount.addKeyListener(this);
        txtSaleWt.addKeyListener(this);
        txtPurWt.addKeyListener(this);
        cboBrand.getEditor().getEditorComponent().addKeyListener(this);
        cboCategory.getEditor().getEditorComponent().addKeyListener(this);
        cboPurUnit.getEditor().getEditorComponent().addKeyListener(this);
        cboSaleUnit.getEditor().getEditorComponent().addKeyListener(this);
        cboStockType.getEditor().getEditorComponent().addKeyListener(this);
        cboUnitPattern.getEditor().getEditorComponent().addKeyListener(this);

        cboBrand.getEditor().getEditorComponent().setName("cboBrand");
        cboCategory.getEditor().getEditorComponent().setName("cboCategory");
        cboPurUnit.getEditor().getEditorComponent().setName("cboPurPrice");
        cboSaleUnit.getEditor().getEditorComponent().setName("cboSaleUnit");
        cboStockType.getEditor().getEditorComponent().setName("cboStockType");
        cboUnitPattern.getEditor().getEditorComponent().setName("cboUnitPattern");

        chkActive.addKeyListener(this);
        btnAddBrand.addKeyListener(this);
        btnAddCategory.addKeyListener(this);
        btnAddItemType.addKeyListener(this);
        btnUnit.addKeyListener(this);
        tblStock.addKeyListener(this);
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
        jLabel1 = new javax.swing.JLabel();
        txtStockCode = new javax.swing.JTextField();
        cboStockType = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        btnAddItemType = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtStockName = new javax.swing.JTextField();
        cboCategory = new javax.swing.JComboBox<>();
        btnAddCategory = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cboBrand = new javax.swing.JComboBox<>();
        btnAddBrand = new javax.swing.JButton();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtBarCode = new javax.swing.JTextField();
        txtShortName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnUnit = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtPurPrice = new javax.swing.JTextField();
        cboPurUnit = new javax.swing.JComboBox<>();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cboSaleUnit = new javax.swing.JComboBox<>();
        txtSaleWt = new javax.swing.JTextField();
        txtSalePrice = new javax.swing.JTextField();
        txtSalePriceA = new javax.swing.JTextField();
        txtSalePriceB = new javax.swing.JTextField();
        txtSalePriceC = new javax.swing.JTextField();
        txtSalePriceD = new javax.swing.JTextField();
        txtSalePriceStd = new javax.swing.JTextField();
        txtExpDate = new com.toedter.calendar.JDateChooser();
        txtPurWt = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        cboUnitPattern = new javax.swing.JComboBox<>();
        btnAddItemType1 = new javax.swing.JButton();
        txtTotalCount = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblStock.setFont(Global.textFont);
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
        tblStock.setName("tblStock"); // NOI18N
        tblStock.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblStock);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Stock Code");

        txtStockCode.setFont(Global.textFont);
        txtStockCode.setName("txtStockCode"); // NOI18N

        cboStockType.setFont(Global.textFont);
        cboStockType.setName("cboStockType"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Stock Type");

        btnAddItemType.setBackground(ColorUtil.btnEdit);
        btnAddItemType.setFont(Global.lableFont);
        btnAddItemType.setForeground(ColorUtil.foreground);
        btnAddItemType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-button.png"))); // NOI18N
        btnAddItemType.setText("Stock Type");
        btnAddItemType.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        btnAddItemType.setName("btnAddItemType"); // NOI18N
        btnAddItemType.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemTypeActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Stock Name");

        txtStockName.setFont(Global.textFont);
        txtStockName.setName("txtStockName"); // NOI18N
        txtStockName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtStockNameFocusGained(evt);
            }
        });

        cboCategory.setFont(Global.textFont);

        btnAddCategory.setBackground(ColorUtil.btnEdit);
        btnAddCategory.setFont(Global.lableFont);
        btnAddCategory.setForeground(ColorUtil.foreground);
        btnAddCategory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-button.png"))); // NOI18N
        btnAddCategory.setText("Category");
        btnAddCategory.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        btnAddCategory.setName("btnAddCategory"); // NOI18N
        btnAddCategory.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddCategory.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCategoryActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Category");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Brand Name");

        cboBrand.setFont(Global.textFont);

        btnAddBrand.setBackground(ColorUtil.btnEdit);
        btnAddBrand.setFont(Global.lableFont);
        btnAddBrand.setForeground(ColorUtil.foreground);
        btnAddBrand.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-button.png"))); // NOI18N
        btnAddBrand.setText("Brand");
        btnAddBrand.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        btnAddBrand.setName("btnAddBrand"); // NOI18N
        btnAddBrand.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddBrand.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddBrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBrandActionPerformed(evt);
            }
        });

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Remark");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Bar Code");

        txtBarCode.setFont(Global.textFont);
        txtBarCode.setName("txtBarCode"); // NOI18N
        txtBarCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBarCodeFocusGained(evt);
            }
        });

        txtShortName.setFont(Global.textFont);
        txtShortName.setName("txtShortName"); // NOI18N
        txtShortName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtShortNameFocusGained(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Short Name");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Licene Exp Date");

        btnUnit.setBackground(ColorUtil.btnEdit);
        btnUnit.setFont(Global.lableFont);
        btnUnit.setForeground(ColorUtil.foreground);
        btnUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-button.png"))); // NOI18N
        btnUnit.setText("Unit");
        btnUnit.setName("btnUnit"); // NOI18N
        btnUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnitActionPerformed(evt);
            }
        });

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Pur Unit & Price");

        txtPurPrice.setFont(Global.textFont);
        txtPurPrice.setToolTipText("Purchase Price");
        txtPurPrice.setName("txtPurPrice"); // NOI18N
        txtPurPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurPriceFocusGained(evt);
            }
        });

        cboPurUnit.setFont(Global.textFont);
        cboPurUnit.setToolTipText("Choose Unit");
        cboPurUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPurUnitActionPerformed(evt);
            }
        });

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sale", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        jLabel11.setFont(Global.lableFont);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Sale Price");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Sale Price A");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Sale Price B");

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Sale Price C");

        jLabel17.setFont(Global.lableFont);
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Sale Price D");

        jLabel18.setFont(Global.lableFont);
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Cost Price Std");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Weight & Unit");

        cboSaleUnit.setFont(Global.textFont);

        txtSaleWt.setFont(Global.textFont);
        txtSaleWt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSaleWt.setName("txtSaleWt"); // NOI18N
        txtSaleWt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleWtFocusGained(evt);
            }
        });

        txtSalePrice.setFont(Global.textFont);
        txtSalePrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePrice.setName("txtSalePrice"); // NOI18N
        txtSalePrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceFocusGained(evt);
            }
        });

        txtSalePriceA.setFont(Global.textFont);
        txtSalePriceA.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceA.setName("txtSalePriceA"); // NOI18N
        txtSalePriceA.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceAFocusGained(evt);
            }
        });

        txtSalePriceB.setFont(Global.textFont);
        txtSalePriceB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceB.setName("txtSalePriceB"); // NOI18N
        txtSalePriceB.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceBFocusGained(evt);
            }
        });

        txtSalePriceC.setFont(Global.textFont);
        txtSalePriceC.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceC.setName("txtSalePriceC"); // NOI18N
        txtSalePriceC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceCFocusGained(evt);
            }
        });

        txtSalePriceD.setFont(Global.textFont);
        txtSalePriceD.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceD.setName("txtSalePriceD"); // NOI18N
        txtSalePriceD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceDFocusGained(evt);
            }
        });

        txtSalePriceStd.setFont(Global.textFont);
        txtSalePriceStd.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalePriceStd.setName("txtSalePriceStd"); // NOI18N
        txtSalePriceStd.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalePriceStdFocusGained(evt);
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSalePrice))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSalePriceA))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSalePriceB))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSalePriceC))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSalePriceD))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSalePriceStd, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSaleWt)
                        .addGap(18, 18, 18)
                        .addComponent(cboSaleUnit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboSaleUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtSaleWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel11)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSalePrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSalePriceA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSalePriceB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSalePriceC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSalePriceD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSalePriceStd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        txtExpDate.setDateFormatString("dd/MM/yyyy");
        txtExpDate.setFont(Global.textFont);
        txtExpDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtExpDateFocusGained(evt);
            }
        });

        txtPurWt.setFont(Global.textFont);
        txtPurWt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPurWt.setName("txtPurWt"); // NOI18N
        txtPurWt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurWtFocusGained(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Unit Pattern");

        cboUnitPattern.setFont(Global.textFont);

        btnAddItemType1.setBackground(ColorUtil.btnEdit);
        btnAddItemType1.setFont(Global.lableFont);
        btnAddItemType1.setForeground(ColorUtil.foreground);
        btnAddItemType1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-button.png"))); // NOI18N
        btnAddItemType1.setText("Import");
        btnAddItemType1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        btnAddItemType1.setName("btnAddItemType"); // NOI18N
        btnAddItemType1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddItemType1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemType1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txtStockCode))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(cboStockType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBarCode, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtShortName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txtExpDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUnit))
                            .addComponent(txtStockName)
                            .addComponent(cboCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboBrand, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtPurWt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboPurUnit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtPurPrice)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkActive)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblStatus))
                            .addComponent(cboUnitPattern, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAddItemType)
                .addGap(18, 18, 18)
                .addComponent(btnAddCategory)
                .addGap(18, 18, 18)
                .addComponent(btnAddBrand)
                .addGap(18, 18, 18)
                .addComponent(btnAddItemType1)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAddCategory, btnAddItemType});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtStockCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboStockType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtStockName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBarCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtShortName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(btnUnit))
                    .addComponent(txtExpDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(cboUnitPattern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cboPurUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkActive)
                    .addComponent(lblStatus)
                    .addComponent(txtPurPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPurWt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddCategory)
                    .addComponent(btnAddBrand)
                    .addComponent(btnAddItemType1)
                    .addComponent(btnAddItemType))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtTotalCount.setEditable(false);
        txtTotalCount.setFont(Global.lableFont);
        txtTotalCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Total Stock  :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(18, 18, 18)
                        .addComponent(txtTotalCount)))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotalCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboPurUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPurUnitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboPurUnitActionPerformed

    private void btnAddItemTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemTypeActionPerformed
        // TODO add your handling code here:
        itemTypeSetupDialog.setIconImage(image);
        itemTypeSetupDialog.initMain();
        itemTypeSetupDialog.setSize(Global.width / 2, Global.height / 2);
        itemTypeSetupDialog.setLocationRelativeTo(null);
        itemTypeSetupDialog.setVisible(true);
    }//GEN-LAST:event_btnAddItemTypeActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        mainFrame.setFilterObserver(this);
        if (!isShown) {
            initMain();
        }
    }//GEN-LAST:event_formComponentShown

    private void btnAddCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCategoryActionPerformed
        // TODO add your handling code here:
        categorySetupDailog.setIconImage(image);
        categorySetupDailog.initMain();
        categorySetupDailog.setSize(Global.width / 2, Global.height / 2);
        categorySetupDailog.setLocationRelativeTo(null);
        categorySetupDailog.setVisible(true);
    }//GEN-LAST:event_btnAddCategoryActionPerformed

    private void btnAddBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBrandActionPerformed
        // TODO add your handling code here:
        itemBrandDailog.setIconImage(image);
        itemBrandDailog.initMain();
        itemBrandDailog.setSize(Global.width / 2, Global.height / 2);
        itemBrandDailog.setLocationRelativeTo(null);
        itemBrandDailog.setVisible(true);
    }//GEN-LAST:event_btnAddBrandActionPerformed

    private void btnUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnitActionPerformed
        // TODO add your handling code here:
        itemUnitSetupDailog.setIconImage(image);
        itemUnitSetupDailog.initMain();
        itemUnitSetupDailog.setSize(Global.width / 2, Global.height / 2);
        itemUnitSetupDailog.setLocationRelativeTo(null);
        itemUnitSetupDailog.setVisible(true);

    }//GEN-LAST:event_btnUnitActionPerformed

    private void txtStockNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtStockNameFocusGained
        // TODO add your handling code here:
        txtStockName.selectAll();
    }//GEN-LAST:event_txtStockNameFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        // TODO add your handling code here:
        txtRemark.selectAll();
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtBarCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBarCodeFocusGained
        // TODO add your handling code here:
        txtBarCode.selectAll();
    }//GEN-LAST:event_txtBarCodeFocusGained

    private void txtShortNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtShortNameFocusGained
        // TODO add your handling code here:
        txtShortName.selectAll();
    }//GEN-LAST:event_txtShortNameFocusGained

    private void txtExpDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtExpDateFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txtExpDateFocusGained

    private void txtPurPriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurPriceFocusGained
        // TODO add your handling code here:
        txtPurPrice.selectAll();
    }//GEN-LAST:event_txtPurPriceFocusGained

    private void txtPurWtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurWtFocusGained
        // TODO add your handling code here:
        txtPurWt.selectAll();
    }//GEN-LAST:event_txtPurWtFocusGained

    private void txtSaleWtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleWtFocusGained
        // TODO add your handling code here:
        txtSaleWt.selectAll();
    }//GEN-LAST:event_txtSaleWtFocusGained

    private void txtSalePriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceFocusGained
        // TODO add your handling code here:
        txtSalePrice.selectAll();
    }//GEN-LAST:event_txtSalePriceFocusGained

    private void txtSalePriceAFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceAFocusGained
        // TODO add your handling code here:
        txtSalePriceA.selectAll();
    }//GEN-LAST:event_txtSalePriceAFocusGained

    private void txtSalePriceBFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceBFocusGained
        // TODO add your handling code here:
        txtSalePriceB.selectAll();
    }//GEN-LAST:event_txtSalePriceBFocusGained

    private void txtSalePriceCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceCFocusGained
        // TODO add your handling code here:
        txtSalePriceC.selectAll();
    }//GEN-LAST:event_txtSalePriceCFocusGained

    private void txtSalePriceDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceDFocusGained
        // TODO add your handling code here:
        txtSalePriceD.selectAll();
    }//GEN-LAST:event_txtSalePriceDFocusGained

    private void txtSalePriceStdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalePriceStdFocusGained
        // TODO add your handling code here:
        txtSalePriceStd.selectAll();
    }//GEN-LAST:event_txtSalePriceStdFocusGained

    private void btnAddItemType1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemType1ActionPerformed
        // TODO add your handling code here:
        importDialog.setSize(Global.width - 400, Global.height - 400);
        importDialog.setLocationRelativeTo(null);
        importDialog.setVisible(true);
    }//GEN-LAST:event_btnAddItemType1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBrand;
    private javax.swing.JButton btnAddCategory;
    private javax.swing.JButton btnAddItemType;
    private javax.swing.JButton btnAddItemType1;
    private javax.swing.JButton btnUnit;
    private javax.swing.JComboBox<String> cboBrand;
    private javax.swing.JComboBox<String> cboCategory;
    private javax.swing.JComboBox<String> cboPurUnit;
    private javax.swing.JComboBox<String> cboSaleUnit;
    private javax.swing.JComboBox<String> cboStockType;
    private javax.swing.JComboBox<String> cboUnitPattern;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblStock;
    private javax.swing.JTextField txtBarCode;
    private com.toedter.calendar.JDateChooser txtExpDate;
    private javax.swing.JTextField txtPurPrice;
    private javax.swing.JTextField txtPurWt;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSalePrice;
    private javax.swing.JTextField txtSalePriceA;
    private javax.swing.JTextField txtSalePriceB;
    private javax.swing.JTextField txtSalePriceC;
    private javax.swing.JTextField txtSalePriceD;
    private javax.swing.JTextField txtSalePriceStd;
    private javax.swing.JTextField txtSaleWt;
    private javax.swing.JTextField txtShortName;
    private javax.swing.JTextField txtStockCode;
    private javax.swing.JTextField txtStockName;
    private javax.swing.JTextField txtTotalCount;
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

        if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        } else if (sourceObj instanceof JFormattedTextField) {
            ctrlName = ((JFormattedTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTextComponent) {
            ctrlName = ((JTextComponent) sourceObj).getName();
        } else if (sourceObj instanceof JTextFieldDateEditor) {
            ctrlName = ((JTextFieldDateEditor) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtStockCode":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        cboStockType.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        cboStockType.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        //btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        cboStockType.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        //btnSave.requestFocus();
                        break;
                }
                tabToTable(e);
                break;
            case "cboStockType":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtStockName.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtStockCode.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnAddItemType.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtStockCode.requestFocus();
                        break;
                }
                tabToTable(e);
                break;
            case "btnAddItemType":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtStockName.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboStockType.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtStockName.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboStockType.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtStockName.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtStockName":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        cboCategory.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        cboCategory.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboStockType.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboStockType.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        cboCategory.requestFocus();
                        break;

                }
                tabToTable(e);

                break;
            case "cboCategory":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        cboBrand.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboStockType.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnAddCategory.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtStockName.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "btnAddCategory":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        cboBrand.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboCategory.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        cboBrand.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboCategory.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        cboBrand.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "cboBrand":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboCategory.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnAddBrand.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboCategory.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "btnAddBrand":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboBrand.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboBrand.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtRemark.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtRemark":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboBrand.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboBrand.requestFocus();
                        break;

                }
                tabToTable(e);

                break;
            case "txtBarCode":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtShortName.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtShortName.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtShortName.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtRemark.requestFocus();
                        break;

                }
                tabToTable(e);

                break;
            case "txtShortName":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtExpDate.requestFocusInWindow();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtExpDate.requestFocusInWindow();
                        break;
                    case KeyEvent.VK_UP:
                        txtBarCode.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtExpDate.requestFocusInWindow();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtBarCode.requestFocus();
                        break;

                }
                tabToTable(e);

                break;
            case "txtExpDate":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        cboUnitPattern.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtPurWt.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtPurWt.requestFocus();
                        break;
                    /*case KeyEvent.VK_RIGHT:
                        btnChNo.requestFocus();
                        break;
                        case KeyEvent.VK_LEFT:
                        txtShortName.requestFocus();
                        break;
                     */
                }
                tabToTable(e);
                break;
            case "cboUnitPattern":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtPurWt.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtPurWt.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtPurWt.requestFocus();
                        break;
                    /*case KeyEvent.VK_RIGHT:
                        btnChNo.requestFocus();
                        break;
                        case KeyEvent.VK_LEFT:
                        txtShortName.requestFocus();
                        break;
                     */
                }
                tabToTable(e);
                break;
            case "txtPurWt":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        cboPurUnit.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        cboPurUnit.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtExpDate.requestFocus();
                        break;
                    /*case KeyEvent.VK_RIGHT:
                        btnChNo.requestFocus();
                        break;
                        case KeyEvent.VK_LEFT:
                        txtShortName.requestFocus();
                        break;
                     */
                }
                tabToTable(e);
                break;
            case "btnChNo":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        btnUnit.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtExpDate.requestFocusInWindow();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnUnit.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtExpDate.requestFocusInWindow();
                        break;

                }
                tabToTable(e);

                break;
            case "btnUnit":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtPurPrice.requestFocus();
                        break;

                    case KeyEvent.VK_RIGHT:
                        txtPurPrice.requestFocus();
                        break;

                }
                tabToTable(e);

                break;
            case "txtPurPrice":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtSaleWt.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtSaleWt.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        btnUnit.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtSaleWt.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        btnUnit.requestFocus();
                        break;
                }
                tabToTable(e);
                break;
            case "cboPurPrice":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtPurWt.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtPurWt.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "chkActive":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtSaleWt.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtSaleWt.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtSaleWt.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtPurPrice.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "cboSaleUnit":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtSalePrice.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtSaleWt.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtSalePrice.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtSaleWt.requestFocus();
                        break;
                }
                tabToTable(e);
                break;
            case "txtSaleWt":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        cboSaleUnit.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        chkActive.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        cboSaleUnit.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        chkActive.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "cboPurUnit":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtSalePrice.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboSaleUnit.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtSalePrice.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboSaleUnit.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtSalePrice":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtSalePriceA.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtSalePriceA.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboSaleUnit.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtSalePriceA.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboSaleUnit.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtSalePriceA":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtSalePriceB.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtSalePriceB.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtSalePrice.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtSalePriceB.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtSalePrice.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtSalePriceB":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtSalePriceC.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtSalePriceC.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtSalePriceA.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtSalePriceC.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtSalePriceA.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtSalePriceC":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtSalePriceD.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtSalePriceD.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtSalePriceB.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtSalePriceD.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtSalePriceB.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtSalePriceD":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtSalePriceStd.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtSalePriceStd.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtSalePriceC.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtSalePriceStd.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtSalePriceC.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtSalePriceStd":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        //btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        //btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtSalePriceC.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        //btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtSalePriceC.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "btnNew":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        //btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        //btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtSalePriceStd.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        //btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtSalePriceStd.requestFocus();
                        break;
                }
                tabToTable(e);
                break;
            case "btnSave":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtStockCode.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtStockCode.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        //btnNew.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtStockCode.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        //btnNew.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "tblStock":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        selectRow = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
                        setStock(selectRow);
                        break;
                    case KeyEvent.VK_UP:
                        selectRow = tblStock.convertRowIndexToModel(tblStock.getSelectedRow());
                        setStock(selectRow);
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (e.isControlDown()) {
                            txtStockCode.requestFocus();
                        }
                        break;
                }
                break;
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (tblStock.getRowCount() >= 0) {
                tblStock.requestFocusInWindow();
                tblStock.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void save() {
        saveStock();
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
        clear();
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
        searchStock();
    }

    @Override
    public void sendFilter(String filter) {
        if (filterHeader.isVisible()) {
            filterHeader.setVisible(false);
        } else {
            filterHeader.setVisible(true);
        }
    }
}
