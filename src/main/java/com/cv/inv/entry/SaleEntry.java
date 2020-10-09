/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.KeyPropagate;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.util.BindingUtil;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.StockUP;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.SaleDetailHis;
import com.cv.inv.entity.Stock;
import com.cv.inv.entry.common.SaleEntryTableModel;
import com.cv.inv.entry.common.StockInfo;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.SaleManAutoCompleter;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.VouStatusAutoCompleter;
import com.cv.inv.service.LocationService;
import com.cv.inv.service.StockService;
import com.cv.inv.service.StockUnitService;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class SaleEntry extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, StockInfo {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SaleEntry.class.getName());

    private final List<SaleDetailHis> listDetail = new ArrayList();
    @Autowired
    private SaleEntryTableModel saleTableModel;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockUnitService unitService;
    @Autowired
    private LocationService locService;
    private TaskExecutor taskExecutor;
    private LoadingObserver loadingObserver;
    private SelectionObserver selectionObserver;
    private String sourceAccId;
    private StockUP stockUp = new StockUP();

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public String getSourceAccId() {
        return sourceAccId;
    }

    public void setSourceAccId(String sourceAccId) {
        this.sourceAccId = sourceAccId;
        LOGGER.info("Source Id :" + sourceAccId);

    }

    /**
     * Creates new form SaleEntry1
     */
    public SaleEntry() {
        initComponents();
    }

    private void initMain() {
        initCombo();
        initSaleTable();
        initKeyListener();
        setTodayDate();
        initTextBoxValue();
        initTextBoxFormat();
        actionMapping();
        addNewRow();
    }

    private void initSaleTable() {
        tblSale.setModel(saleTableModel);
        saleTableModel.setParent(tblSale);
        saleTableModel.addEmptyRow();
        saleTableModel.setSelectionObserver(this);
        tblSale.getTableHeader().setFont(Global.tblHeaderFont);
        tblSale.getTableHeader().setPreferredSize(new Dimension(40, 40));
        tblSale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSale.setCellSelectionEnabled(true);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(200);//Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(50);//Exp-Date
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(30);//Qty
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(30);//Std-Wt
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(15);//Unit
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(40);//Sale Price
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(20);//Discount
        tblSale.getColumnModel().getColumn(8).setPreferredWidth(50);//Discount Type
        tblSale.getColumnModel().getColumn(9).setPreferredWidth(40);//Amount
        tblSale.getColumnModel().getColumn(10).setPreferredWidth(90);//Location

        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblSale.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(3).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(5).setCellEditor(new SaleTableUnitCellEditor());

        if (Util1.getPropValue("system.sale.detail.location").equals("Y")) {
            JComboBox cboLocationCell = new JComboBox();
            cboLocationCell.setFont(Global.textFont);
            BindingUtil.BindCombo(cboLocationCell, locService.findAll());
            tblSale.getColumnModel().getColumn(10).setCellEditor(new DefaultCellEditor(cboLocationCell));
            saleTableModel.setLocation((Location) cboLocationCell.getSelectedItem());
            tblSale.getColumnModel().getColumn(10).setPreferredWidth(30);
        } else {
            tblSale.getColumnModel().getColumn(10).setPreferredWidth(0);//Location
            tblSale.getColumnModel().getColumn(10).setMaxWidth(0);
            tblSale.getColumnModel().getColumn(10).setMaxWidth(0);
        }
        tblSale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblSale.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblSale.setDefaultRenderer(Object.class, new TableCellRender());
        tblSale.setDefaultRenderer(Double.class, new TableCellRender());
        tblSale.setDefaultRenderer(Float.class, new TableCellRender());

    }

    private void initCombo() {
        CurrencyAutoCompleter currencyAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        currencyAutoCompleter.setSelectionObserver(this);
        LocationAutoCompleter locAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        locAutoCompleter.setSelectionObserver(this);
        TraderAutoCompleter traderAutoCompleter = new TraderAutoCompleter(txtCus, Global.listTrader, null);
        traderAutoCompleter.setSelectionObserver(this);
        VouStatusAutoCompleter vouAutoCompleter = new VouStatusAutoCompleter(txtVouStatus, Global.listVou, null);
        vouAutoCompleter.setSelectionObserver(this);
        SaleManAutoCompleter saleManAutoCompleter = new SaleManAutoCompleter(txtSaleman, Global.listSaleMan, null);
        saleManAutoCompleter.setSelectionObserver(this);
    }

    private void initKeyListener() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtDept.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        txtSaleman.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtVouStatus.addKeyListener(this);
        tblSale.addKeyListener(this);

    }

    private void setTodayDate() {
        txtSaleDate.setDate(Util1.getTodayDate());
    }

    private void initTextBoxValue() {
        txtVouTotal.setValue(0.00);
        txtVouDiscount.setValue(0.00);
        txtTax.setValue(0.00);
        txtGrandTotal.setValue(0.00);
        txtVouPaid.setValue(0.00);
        txtVouBalance.setValue(0.00);
        txtTaxP.setText("0");
        txtDiscP.setText("0");

    }

    private void initTextBoxFormat() {
        txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouDiscount.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTax.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtGrandTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
    }

    private void addNewRow() {
        SaleDetailHis sale = new SaleDetailHis();
        sale.setStockCode(new Stock());
        listDetail.add(sale);
    }

    @Override
    public void getStockInfo(String stockCode) {
        Stock stock;

        if (!stockCode.trim().isEmpty()) {
            stock = (Stock) stockService.findById(stockCode);
            if (stock != null) {
                selected("StockList", stock);
            }
        }
    }

    private class SaleTableUnitCellEditor extends javax.swing.AbstractCellEditor implements TableCellEditor {

        JComponent component = null;
        int colIndex = -1;
        private Object oldValue;
        // This method is called when a cell value is edited by the user.

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            oldValue = value;
            colIndex = vColIndex;

            if (isSelected) {
                // cell (and perhaps other cells) are selected
            }

            String stockCode = listDetail.get(rowIndex).getStockCode().getStockCode();

            try {
                switch (vColIndex) {
                    case 0: //Code column
                        JTextField code = new JTextField();
                        component = code;
                        if (value != null) {
                            ((JTextField) component).setText(value.toString());
                        }
                        ((JTextField) component).selectAll();
                        break;
                    case 5: //Unit column
                        JComboBox jb = new JComboBox();

                        BindingUtil.BindCombo(jb, stockUp.getUnitList(stockCode));
                        component = jb;
                        break;
                    case 6: //Price column
                        JTextField jtf = new JTextField();
                        component = jtf;
                        if (value != null) {
                            ((JTextField) component).setText(value.toString());
                        }
                        ((JTextField) component).selectAll();

                        String unit = listDetail.get(rowIndex).getItemUnit().getItemUnitCode();
                        break;
                }
            } catch (Exception ex) {
                LOGGER.error("getTableCellEditorComponent : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            // Configure the component with the specified value
            //((JTextField) component).setText("");

            // Return the configured component
            return component;
        }

        // This method is called when editing is completed.
        // It must return the new value to be stored in the cell.
        @Override
        public Object getCellEditorValue() {
            Object obj = null;

            if (component instanceof JComboBox) {
                obj = ((JComboBox) component).getSelectedItem();
            } else if (component instanceof JTextField) {
                obj = ((JTextField) component).getText();
            }

            switch (colIndex) {
                case 0: //Code
                    if (obj != null) {
                        getStockInfo(obj.toString());
                    }

                    tblSale.setColumnSelectionInterval(0, 0);
                    break;
            }

            return obj;
        }

        /*
         * To prevent mouse click cell editing
         */
        @Override
        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                return false;
            } else {
                return true;
            }
        }
    }

    private void actionMapping() {
        //Enter event on tblSale
        tblSale.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblSale.getActionMap().put("ENTER-Action", actionTblSaleEnterKey);
    }

    private Action actionTblSaleEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                tblSale.getCellEditor().stopCellEditing();
            } catch (Exception ex) {
            }

            int row = tblSale.getSelectedRow();
            int col = tblSale.getSelectedColumn();

            SaleDetailHis sdh = listDetail.get(row);

            if (col == 0 && sdh.getStockCode().getStockCode() != null) {
                tblSale.setColumnSelectionInterval(3, 3); //Move to Qty
            } else if (col == 1 && sdh.getStockCode().getStockCode() != null) {
                tblSale.setColumnSelectionInterval(3, 3); //Move to Qty
            } else if (col == 2 && sdh.getStockCode().getStockCode() != null) {
                tblSale.setColumnSelectionInterval(3, 3); //Move to Qty
            } else if (col == 3 && sdh.getStockCode().getStockCode() != null) {
                tblSale.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 4 && sdh.getStockCode().getStockCode() != null) {
                tblSale.setColumnSelectionInterval(6, 6); //Move to Unit
            } else if (col == 5 && sdh.getStockCode().getStockCode() != null) {
                tblSale.setColumnSelectionInterval(6, 6); //Move to Sale Price
            } else if (col == 6 && sdh.getStockCode().getStockCode() != null) {
                tblSale.setColumnSelectionInterval(10, 10); //Move to Discount
            } else if (col == 7 && sdh.getStockCode().getStockCode() != null) {
                tblSale.setColumnSelectionInterval(10, 10); //Move to Charge Type
            } else if (col == 8 && sdh.getStockCode().getStockCode() != null) {
                tblSale.setColumnSelectionInterval(10, 10); //Move to Charge Type
            } else if (col == 9 && sdh.getStockCode().getStockCode() != null) {
                tblSale.setColumnSelectionInterval(10, 10); //Move to Charge Type
            } else if (col == 10 && sdh.getStockCode().getStockCode() != null) {
                if ((row + 1) <= listDetail.size()) {
                    tblSale.setRowSelectionInterval(row + 1, row + 1);
                }
                tblSale.setColumnSelectionInterval(0, 0); //Move to Code
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

        jPanel1 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtDept = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCus = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtSaleman = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtSaleDate = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        txtDueDate = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        txtCurrency = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtVouStatus = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        chkPrintOption = new javax.swing.JCheckBox();
        chkVouComp = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtVouDiscount = new javax.swing.JFormattedTextField();
        txtTaxP = new javax.swing.JTextField();
        txtTax = new javax.swing.JFormattedTextField();
        txtGrandTotal = new javax.swing.JFormattedTextField();
        txtVouPaid = new javax.swing.JFormattedTextField();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtDiscP = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSale = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Vou No");

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Dept");

        txtDept.setFont(Global.textFont);
        txtDept.setName("txtDept"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Customer");

        txtCus.setFont(Global.textFont);
        txtCus.setName("txtCus"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Sale Man");

        txtSaleman.setFont(Global.textFont);
        txtSaleman.setName("txtSaleman"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Sale Date");

        txtSaleDate.setDateFormatString("dd/MM/yyyy");
        txtSaleDate.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Credit Term");

        txtDueDate.setDateFormatString("dd/MM/yyyy");
        txtDueDate.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        txtCurrency.setFont(Global.textFont);
        txtCurrency.setName("txtCurrency"); // NOI18N

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Vou Status");

        txtVouStatus.setFont(Global.textFont);
        txtVouStatus.setName("txtVouStatus"); // NOI18N

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel17)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCus)
                    .addComponent(txtSaleman)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtVouNo)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtDept)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCurrency)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel9)
                    .addComponent(jLabel21))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouStatus)
                    .addComponent(txtLocation)
                    .addComponent(txtRemark))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtSaleman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel20)
                                .addComponent(txtVouStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel21))
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel10.setText("NEW");

        chkPrintOption.setFont(Global.lableFont);
        chkPrintOption.setText("Vou Printer");

        chkVouComp.setFont(Global.lableFont);
        chkVouComp.setText("Vou Comp");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Rec no:");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Total Item:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkPrintOption)
                            .addComponent(chkVouComp))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel11))))
                .addGap(0, 26, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPrintOption)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(chkVouComp))
                .addGap(4, 4, 4))
        );

        jLabel13.setFont(Global.lableFont);
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Gross Total :");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Discount :");

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Tax( + ) :");

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Grand Total :");

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Paid :");

        txtVouTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTotal.setFont(Global.amtFont);

        jLabel7.setText("%");

        txtVouDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouDiscount.setFont(Global.amtFont);

        txtTaxP.setFont(Global.amtFont);

        txtTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTax.setFont(Global.amtFont);

        txtGrandTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGrandTotal.setFont(Global.amtFont);

        txtVouPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouPaid.setFont(Global.amtFont);

        txtVouBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouBalance.setFont(Global.amtFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Vou Balance :");

        jLabel15.setText("%");

        txtDiscP.setFont(Global.amtFont);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtGrandTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                    .addComponent(txtVouPaid)
                    .addComponent(txtVouBalance))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel14)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouTotal)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTaxP, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                            .addComponent(txtDiscP))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTax, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jLabel7)
                            .addComponent(txtVouDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)
                            .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(txtGrandTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(txtVouPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton2.setFont(Global.lableFont);
        jButton2.setText("New");

        jButton3.setFont(Global.lableFont);
        jButton3.setText("History");

        jButton4.setFont(Global.lableFont);
        jButton4.setText("Save");

        jButton5.setFont(Global.lableFont);
        jButton5.setText("Delete");

        jButton6.setFont(Global.lableFont);
        jButton6.setText("Outstanding");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblSale.setFont(Global.textFont);
        tblSale.setModel(new javax.swing.table.DefaultTableModel(
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
        tblSale.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblSale);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        initMain();
        txtVouNo.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblSale.requestFocus();
            if (tblSale.getRowCount() >= 0) {
                tblSale.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void keyEvent(KeyEvent e) {

    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "StockList":
                Stock stock = (Stock) selectObj;
                int selectRow = tblSale.getSelectedRow();
                saleTableModel.setStock(stock, selectRow);
                stockUp.add(stock);
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
                    txtRemark.requestFocus();
                } else {
                    txtDept.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtCus":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtDept.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDept.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtSaleman.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDueDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtSaleman":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDept.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtSaleDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCurrency.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtDept":
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtSaleDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtVouStatus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtSaleDate":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtSaleman.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    //txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtSaleDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtDueDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    //txtLocation.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtDueDate":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtSaleDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    //txtCus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtDueDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    //txtVouStatus.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtVouStatus":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtLocation.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDueDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtRemark.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtLocation":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtSaleDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouStatus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtCurrency":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtSaleman.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtLocation.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtDueDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkPrintOption;
    private javax.swing.JCheckBox chkVouComp;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblSale;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtDept;
    private javax.swing.JTextField txtDiscP;
    private com.toedter.calendar.JDateChooser txtDueDate;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtSaleDate;
    private javax.swing.JTextField txtSaleman;
    private javax.swing.JFormattedTextField txtTax;
    private javax.swing.JTextField txtTaxP;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouDiscount;
    private javax.swing.JTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JTextField txtVouStatus;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables

}
