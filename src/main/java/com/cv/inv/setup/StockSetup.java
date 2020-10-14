/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.StartWithRowFilter;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.util.BindingUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Category;
import com.cv.inv.entity.StockBrand;
import com.cv.inv.entity.StockType;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entity.Stock;
import com.cv.inv.service.CategoryService;
import com.cv.inv.service.CharacterNoService;
import com.cv.inv.service.StockService;
import com.cv.inv.setup.dialog.CategorySetupDialog;
import com.cv.inv.setup.dialog.CharacterNoSetupDialog;
import com.cv.inv.setup.dialog.StockBrandSetupDialog;
import com.cv.inv.setup.dialog.StockTypeSetupDialog;
import com.cv.inv.setup.dialog.StockUnitSetupDailog;
import com.cv.inv.setup.common.StockTableModel;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import com.cv.inv.service.StockUnitService;
import com.cv.inv.service.StockBrandService;
import com.cv.inv.service.StockTypeService;

/**
 *
 * @author Lenovo
 */
@Component
public class StockSetup extends javax.swing.JPanel implements KeyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockSetup.class);
    Image image = new ImageIcon(getClass().getResource("/images/setting.png")).getImage();
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
    private CharacterNoSetupDialog characterNoDialog;
    @Autowired
    private StockTypeService itemTypeService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StockBrandService itemBrandService;
    @Autowired
    private StockUnitService itemUnitService;
    @Autowired
    private StockService stockService;
    @Autowired
    private CharacterNoService characterNoService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private StockTableModel stockTableModel;
    private Stock stock;
    private LoadingObserver loadingObserver;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private boolean isShown = false;

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
        isShown = true;

    }

    private void initTable() {
        tblStock.getTableHeader().setFont(Global.tblHeaderFont);
        tblStock.setModel(stockTableModel);
        sorter = new TableRowSorter<>(tblStock.getModel());
        tblStock.setRowSorter(sorter);

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

    }

    private void setStock(int row) {
        Stock stock = stockTableModel.getStock(row);
        txtBarCode.setText(stock.getBarcode());
        //txtExpire.setText(stock.getExpireDate());
        txtPurPrice.setText(Util1.getString(stock.getPurPrice()));
        txtRemark.setText(stock.getRemark());
        txtShortName.setText(stock.getShortName());
        txtStockCode.setText(stock.getStockCode());
        txtStockName.setText(stock.getStockName());
        chkActive.setSelected(Util1.getBoolean(stock.getIsActive()));
        cboBrand.setSelectedItem(stock.getBrand());
        cboCategory.setSelectedItem(stock.getCategory());

        txtFSaleUnitNo.setValue(stock.getSaleMeasure());
        cboSaleUnit.setSelectedItem(stock.getSaleUnit());
        cboStockType.setSelectedItem(stock.getStockType());
        txtFPurPriceNo.setValue(stock.getPurPriceMeasure());
        cboPurPrice.setSelectedItem(stock.getPurPriceUnit());
        txtFSalePrice.setValue(stock.getSalePriceN());
        txtFSalePriceA.setValue(stock.getSalePriceA());
        txtFSalePriceB.setValue((stock.getSalePriceB()));
        txtFSalePriceC.setValue(stock.getSalePriceC());
        txtFSalePriceD.setValue(stock.getSalePriceD());
        txtFCostPrice.setValue(stock.getSttCostPrice());
        txtStockCode.setEnabled(false);
        lblStatus.setText("EDIT");
    }

    private void searchStock() {
        stockTableModel.setListStock(Global.listStock);
    }

    private void initCombo() {
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            if (Global.listStockType.isEmpty()) {
                Global.listStockType = itemTypeService.findAll();
            }
            if (Global.listCategory.isEmpty()) {
                Global.listCategory = categoryService.findAll();
            }
            if (Global.listStockBrand.isEmpty()) {
                Global.listStockBrand = itemBrandService.findAll();
            }
            if (Global.listStockUnit.isEmpty()) {
                Global.listStockUnit = itemUnitService.findAll();
            }
            if (Global.listCharNo.isEmpty()) {
                Global.listCharNo = characterNoService.findAll();
            }
            BindingUtil.BindComboFilter(cboStockType, Global.listStockType, null, true, false);
            BindingUtil.BindComboFilter(cboCategory, Global.listCategory, null, true, false);
            BindingUtil.BindComboFilter(cboBrand, Global.listStockBrand, null, true, false);
            BindingUtil.BindComboFilter(cboSaleUnit, Global.listStockUnit, null, true, false);
            BindingUtil.BindComboFilter(cboPurPrice, Global.listStockUnit, null, true, false);

        });
        loadingObserver.load(this.getName(), "Stop");

    }

    private boolean isValidEntry() {
        boolean status = true;

        if (txtStockName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stock name must not be blank.",
                    "Stock name.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtStockName.requestFocusInWindow();
        } else if (cboStockType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "You must choose stock type.",
                    "Stock Type null error.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboStockType.requestFocusInWindow();
        } else if (txtStockName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stock Name cannot be blank.",
                    "Stock Name null error.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtStockName.requestFocusInWindow();
        } else if (isDuplicate()) {
            JOptionPane.showMessageDialog(this, "Duplicate stock code.",
                    "Stock Code.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else {
            if (txtStockCode.getText().isEmpty()) {
                // txtStockCode.setText(getMedCode(txtStockName.getText()));
            }

            if (txtExpDate.getDate() != null) {
                stock.setExpireDate(txtExpDate.getDate());
            }
            stock = new Stock();
            stock.setStockCode(txtStockCode.getText().trim());
            stock.setStockType((StockType) cboStockType.getSelectedItem());
            stock.setStockName(txtStockName.getText().trim());
            stock.setCategory((Category) cboCategory.getSelectedItem());
            stock.setBrand((StockBrand) cboBrand.getSelectedItem());
            stock.setIsActive(chkActive.isSelected());
            stock.setBarcode(txtBarCode.getText().trim());
            stock.setShortName(txtShortName.getText().trim());
            stock.setPurPrice(Util1.getFloat(txtPurPrice.getText()));
            stock.setPurPriceMeasure(Util1.getFloat(txtFPurPriceNo.getValue()));
            stock.setPurPriceUnit((StockUnit) cboPurPrice.getSelectedItem());
            stock.setSaleMeasure(Util1.getFloat(txtFSaleUnitNo.getValue()));
            stock.setSaleUnit((StockUnit) cboSaleUnit.getSelectedItem());

            stock.setSalePriceN(Util1.getDouble(txtFSalePrice.getValue()));
            stock.setSalePriceA(Util1.getDouble(txtFSalePriceA.getValue()));
            stock.setSalePriceB(Util1.getDouble(txtFSalePriceB.getValue()));
            stock.setSalePriceC(Util1.getDouble(txtFSalePriceC.getValue()));
            stock.setSalePriceD(Util1.getDouble(txtFSalePriceD.getValue()));
            stock.setSttCostPrice(Util1.getDouble(txtFCostPrice.getValue()));
            LOGGER.info("Price" + txtFSalePriceA.getValue());

            if (lblStatus.getText().equals("NEW")) {
                stock.setCreatedBy(Global.loginUser);
            } else {
                stock.setUpdatedBy(Global.loginUser);
            }
        }

        return status;
    }

    private void save() {
        if (isValidEntry()) {
            StockType sType = (StockType) cboStockType.getSelectedItem();
            Stock saveStock = stockService.save(stock, sType, lblStatus.getText());
            if (saveStock != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if (lblStatus.getText().equals("NEW")) {
                    Global.listStock.add(saveStock);
                } else {
                    Global.listStock.set(selectRow, saveStock);
                }
                clear();
            }
        }
    }

    private boolean isDuplicate() {
        boolean status = false;
        String stockCode = txtStockCode.getText();
        if (!stockCode.isEmpty()) {
            if (lblStatus.getText().equals("NEW")) {
                Stock stock = stockService.findById(stockCode);
                if (stock != null) {
                    status = true;
                }
            }
        }
        return status;
    }

    public void clear() {
        txtBarCode.setText(null);
        txtExpDate.setDate(null);
        txtFilter.setText(null);
        txtRemark.setText("");
        txtShortName.setText(null);
        txtStockCode.setText(null);
        txtStockName.setText(null);
        chkActive.setSelected(true);
        txtPurPrice.setText(null);
        cboBrand.setSelectedItem(null);
        cboCategory.setSelectedItem(null);
        cboPurPrice.setSelectedItem(null);
        cboSaleUnit.setSelectedItem(null);
        cboStockType.setSelectedItem(null);
        txtFCostPrice.setValue(0.0);
        txtFPurPriceNo.setValue(0.0);
        txtFSalePrice.setValue(0.0);
        txtFSalePriceA.setValue(0.0);
        txtFSalePriceB.setValue(0.0);
        txtFSalePriceC.setValue(0.0);
        txtFSalePriceD.setValue(0.0);
        txtFSaleUnitNo.setValue(0.0);
        lblStatus.setText("NEW");
        txtStockCode.setEnabled(true);
        txtStockCode.requestFocus();
        stockTableModel.refresh();

    }

    private void initKeyListener() {
        txtBarCode.addKeyListener(this);
        txtExpDate.getDateEditor().getUiComponent().setName("txtExpDate");
        txtExpDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtFCostPrice.addKeyListener(this);
        txtFSalePrice.addKeyListener(this);
        txtFSalePriceA.addKeyListener(this);
        txtFSalePriceB.addKeyListener(this);
        txtFSalePriceC.addKeyListener(this);
        txtFSalePriceD.addKeyListener(this);
        txtFilter.addKeyListener(this);
        txtPurPrice.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtShortName.addKeyListener(this);
        txtStockCode.addKeyListener(this);
        txtStockName.addKeyListener(this);
        txtTotalCount.addKeyListener(this);
        cboBrand.getEditor().getEditorComponent().addKeyListener(this);
        cboCategory.getEditor().getEditorComponent().addKeyListener(this);
        cboPurPrice.getEditor().getEditorComponent().addKeyListener(this);
        cboSaleUnit.getEditor().getEditorComponent().addKeyListener(this);
        cboStockType.getEditor().getEditorComponent().addKeyListener(this);

        cboBrand.getEditor().getEditorComponent().setName("cboBrand");
        cboCategory.getEditor().getEditorComponent().setName("cboCategory");
        cboPurPrice.getEditor().getEditorComponent().setName("cboPurPrice");
        cboSaleUnit.getEditor().getEditorComponent().setName("cboSaleUnit");
        cboStockType.getEditor().getEditorComponent().setName("cboStockType");

        chkActive.addKeyListener(this);
        btnAddBrand.addKeyListener(this);
        btnAddCategory.addKeyListener(this);
        btnAddItemType.addKeyListener(this);
        btnNew.addKeyListener(this);
        btnChNo.addKeyListener(this);
        btnSave.addKeyListener(this);
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
        txtFilter = new javax.swing.JTextField();
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
        btnChNo = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtPurPrice = new javax.swing.JTextField();
        cboPurPrice = new javax.swing.JComboBox<>();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtFSalePrice = new javax.swing.JFormattedTextField();
        txtFSalePriceA = new javax.swing.JFormattedTextField();
        txtFSalePriceB = new javax.swing.JFormattedTextField();
        txtFSalePriceC = new javax.swing.JFormattedTextField();
        txtFSalePriceD = new javax.swing.JFormattedTextField();
        txtFCostPrice = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        txtFSaleUnitNo = new javax.swing.JFormattedTextField();
        cboSaleUnit = new javax.swing.JComboBox<>();
        txtFPurPriceNo = new javax.swing.JFormattedTextField();
        txtExpDate = new com.toedter.calendar.JDateChooser();
        txtTotalCount = new javax.swing.JTextField();

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

        txtFilter.setFont(Global.textFont);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Stock Code");

        txtStockCode.setFont(Global.textFont);
        txtStockCode.setName("txtStockCode"); // NOI18N

        cboStockType.setFont(Global.textFont);
        cboStockType.setName("cboStockType"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Stock Type");

        btnAddItemType.setFont(Global.lableFont);
        btnAddItemType.setText("+");
        btnAddItemType.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddItemType.setName("btnAddItemType"); // NOI18N
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

        btnAddCategory.setFont(Global.lableFont);
        btnAddCategory.setText("+");
        btnAddCategory.setName("btnAddCategory"); // NOI18N
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

        btnAddBrand.setFont(Global.lableFont);
        btnAddBrand.setText("+");
        btnAddBrand.setName("btnAddBrand"); // NOI18N
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

        btnUnit.setFont(Global.lableFont);
        btnUnit.setText("Unit");
        btnUnit.setName("btnUnit"); // NOI18N
        btnUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnitActionPerformed(evt);
            }
        });

        btnChNo.setFont(Global.lableFont);
        btnChNo.setText("Char No");
        btnChNo.setName("btnChNo"); // NOI18N
        btnChNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChNoActionPerformed(evt);
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

        cboPurPrice.setFont(Global.textFont);
        cboPurPrice.setToolTipText("Choose Unit");
        cboPurPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPurPriceActionPerformed(evt);
            }
        });

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnNew.setFont(Global.lableFont);
        btnNew.setText("New");
        btnNew.setName("btnNew"); // NOI18N
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sale Unit & Price", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

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

        txtFSalePrice.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtFSalePrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFSalePrice.setFont(Global.textFont);
        txtFSalePrice.setName("txtFSalePrice"); // NOI18N
        txtFSalePrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFSalePriceFocusGained(evt);
            }
        });

        txtFSalePriceA.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtFSalePriceA.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFSalePriceA.setFont(Global.textFont);
        txtFSalePriceA.setName("txtFSalePriceA"); // NOI18N
        txtFSalePriceA.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFSalePriceAFocusGained(evt);
            }
        });

        txtFSalePriceB.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtFSalePriceB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFSalePriceB.setFont(Global.textFont);
        txtFSalePriceB.setName("txtFSalePriceB"); // NOI18N
        txtFSalePriceB.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFSalePriceBFocusGained(evt);
            }
        });

        txtFSalePriceC.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtFSalePriceC.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFSalePriceC.setFont(Global.textFont);
        txtFSalePriceC.setName("txtFSalePriceC"); // NOI18N
        txtFSalePriceC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFSalePriceCFocusGained(evt);
            }
        });

        txtFSalePriceD.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtFSalePriceD.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFSalePriceD.setFont(Global.textFont);
        txtFSalePriceD.setName("txtFSalePriceD"); // NOI18N
        txtFSalePriceD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFSalePriceDFocusGained(evt);
            }
        });

        txtFCostPrice.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtFCostPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFCostPrice.setFont(Global.textFont);
        txtFCostPrice.setName("txtFCostPrice"); // NOI18N
        txtFCostPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFCostPriceFocusGained(evt);
            }
        });

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Weight & Unit");

        txtFSaleUnitNo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtFSaleUnitNo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFSaleUnitNo.setToolTipText("Sale Weight");
        txtFSaleUnitNo.setFont(Global.textFont);
        txtFSaleUnitNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFSaleUnitNoFocusGained(evt);
            }
        });
        txtFSaleUnitNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFSaleUnitNoActionPerformed(evt);
            }
        });

        cboSaleUnit.setFont(Global.textFont);

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
                            .addComponent(txtFSalePrice, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFSalePriceA, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFSalePriceB, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFSalePriceC, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFSalePriceD, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFCostPrice)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFSaleUnitNo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboSaleUnit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboSaleUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtFSaleUnitNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel11)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFSalePriceA)
                    .addComponent(txtFSalePriceB)
                    .addComponent(txtFSalePriceC)
                    .addComponent(txtFSalePriceD)
                    .addComponent(txtFCostPrice)
                    .addComponent(txtFSalePrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        txtFPurPriceNo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtFPurPriceNo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFPurPriceNo.setToolTipText("Purchase Weight");
        txtFPurPriceNo.setFont(Global.textFont);
        txtFPurPriceNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFPurPriceNoFocusGained(evt);
            }
        });

        txtExpDate.setDateFormatString("dd/MM/yyyy");
        txtExpDate.setFont(Global.textFont);
        txtExpDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtExpDateFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                .addComponent(cboStockType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddItemType))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txtStockCode))))
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
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(cboBrand, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddBrand))
                            .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBarCode, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtShortName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txtExpDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnChNo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUnit))
                            .addComponent(txtStockName)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cboCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddCategory))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(txtFPurPriceNo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboPurPrice, 0, 112, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPurPrice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkActive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblStatus))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave)
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

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
                    .addComponent(jLabel2)
                    .addComponent(btnAddItemType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtStockName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddCategory)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddBrand)
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
                        .addComponent(btnUnit)
                        .addComponent(btnChNo))
                    .addComponent(txtExpDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cboPurPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkActive)
                    .addComponent(lblStatus)
                    .addComponent(txtFPurPriceNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPurPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSave)
                    .addComponent(btnNew))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtTotalCount.setEditable(false);
        txtTotalCount.setFont(Global.lableFont);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalCount, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtFilter, txtTotalCount});

    }// </editor-fold>//GEN-END:initComponents

    private void cboPurPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPurPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboPurPriceActionPerformed

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

    private void btnChNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChNoActionPerformed
        // TODO add your handling code here:
        characterNoDialog.setIconImage(image);
        characterNoDialog.initMain();
        characterNoDialog.setSize(Global.width / 2, Global.height / 2);
        characterNoDialog.setLocationRelativeTo(null);
        characterNoDialog.setVisible(true);

    }//GEN-LAST:event_btnChNoActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            save();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            LOGGER.error("Save Stock :" + e.getMessage());
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnNewActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:
        if (txtFilter.getText().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

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

    private void txtFPurPriceNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFPurPriceNoFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFPurPriceNoFocusGained

    private void txtPurPriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurPriceFocusGained
        // TODO add your handling code here:
        txtPurPrice.selectAll();
    }//GEN-LAST:event_txtPurPriceFocusGained

    private void txtFSaleUnitNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFSaleUnitNoFocusGained
        // TODO add your handling code here:
        txtFSaleUnitNo.selectAll();
    }//GEN-LAST:event_txtFSaleUnitNoFocusGained

    private void txtFSalePriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFSalePriceFocusGained
        // TODO add your handling code here:
        txtFSalePrice.selectAll();
    }//GEN-LAST:event_txtFSalePriceFocusGained

    private void txtFSalePriceAFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFSalePriceAFocusGained
        // TODO add your handling code here:
        txtFSalePriceA.selectAll();
    }//GEN-LAST:event_txtFSalePriceAFocusGained

    private void txtFSalePriceBFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFSalePriceBFocusGained
        // TODO add your handling code here:
        txtFSalePriceB.selectAll();
    }//GEN-LAST:event_txtFSalePriceBFocusGained

    private void txtFSalePriceCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFSalePriceCFocusGained
        // TODO add your handling code here:
        txtFSalePriceC.selectAll();
    }//GEN-LAST:event_txtFSalePriceCFocusGained

    private void txtFSalePriceDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFSalePriceDFocusGained
        // TODO add your handling code here:
        txtFSalePriceD.selectAll();
    }//GEN-LAST:event_txtFSalePriceDFocusGained

    private void txtFCostPriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFCostPriceFocusGained
        // TODO add your handling code here:
        txtFCostPrice.selectAll();
    }//GEN-LAST:event_txtFCostPriceFocusGained

    private void txtFSaleUnitNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFSaleUnitNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFSaleUnitNoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBrand;
    private javax.swing.JButton btnAddCategory;
    private javax.swing.JButton btnAddItemType;
    private javax.swing.JButton btnChNo;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUnit;
    private javax.swing.JComboBox<String> cboBrand;
    private javax.swing.JComboBox<String> cboCategory;
    private javax.swing.JComboBox<String> cboPurPrice;
    private javax.swing.JComboBox<String> cboSaleUnit;
    private javax.swing.JComboBox<String> cboStockType;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JFormattedTextField txtFCostPrice;
    private javax.swing.JFormattedTextField txtFPurPriceNo;
    private javax.swing.JFormattedTextField txtFSalePrice;
    private javax.swing.JFormattedTextField txtFSalePriceA;
    private javax.swing.JFormattedTextField txtFSalePriceB;
    private javax.swing.JFormattedTextField txtFSalePriceC;
    private javax.swing.JFormattedTextField txtFSalePriceD;
    private javax.swing.JFormattedTextField txtFSaleUnitNo;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtPurPrice;
    private javax.swing.JTextField txtRemark;
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
        LOGGER.info("CONTROL NAME :" + ctrlName);
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
                        btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        cboStockType.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        btnSave.requestFocus();
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
                        txtPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtShortName.requestFocus();
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
                    case KeyEvent.VK_UP:
                        btnChNo.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        btnChNo.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtPurPrice":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        cboPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        cboPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        btnUnit.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        cboPurPrice.requestFocus();
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
                        chkActive.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        chkActive.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtPurPrice.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "chkActive":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        cboSaleUnit.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        cboSaleUnit.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboPurPrice.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        cboSaleUnit.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboPurPrice.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "cboSaleUnit":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtFSalePrice.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtFSaleUnitNo.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtFSalePrice.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtFSaleUnitNo.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "cboPurUnit":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtFSalePrice.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboSaleUnit.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtFSalePrice.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboSaleUnit.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtFSalePrice":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtFSalePriceA.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtFSalePriceA.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboSaleUnit.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtFSalePriceA.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboSaleUnit.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtFSalePriceA":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtFSalePriceB.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtFSalePriceB.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtFSalePrice.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtFSalePriceB.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtFSalePrice.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtFSalePriceB":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtFSalePriceC.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtFSalePriceC.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtFSalePriceA.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtFSalePriceC.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtFSalePriceA.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtFSalePriceC":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtFSalePriceD.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtFSalePriceD.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtFSalePriceB.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtFSalePriceD.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtFSalePriceB.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtFSalePriceD":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtFCostPrice.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtFCostPrice.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtFSalePriceC.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtFCostPrice.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtFSalePriceC.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtFCostPrice":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        btnNew.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        btnNew.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtFSalePriceC.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnNew.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtFSalePriceC.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "btnNew":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_DOWN:
                        btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtFCostPrice.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        btnSave.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtFCostPrice.requestFocus();
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
                        btnNew.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtStockCode.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        btnNew.requestFocus();
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
}
