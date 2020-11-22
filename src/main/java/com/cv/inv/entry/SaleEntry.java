/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.inv.entry.dialog.SaleVouSearch;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.KeyPropagate;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.CurrencyKey;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.ui.ApplicationMainFrame;
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
import com.cv.inv.entity.SaleHis;
import com.cv.inv.entity.SaleMan;
import com.cv.inv.entity.VouStatus;
import com.cv.inv.entry.common.SaleEntryTableModel;
import com.cv.inv.entry.dialog.SaleOutstandingDialog;
import com.cv.inv.entry.editor.SaleManAutoCompleter;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.VouStatusAutoCompleter;
import com.cv.inv.service.LocationService;
import com.cv.inv.service.SaleDetailService;
import com.cv.inv.service.SaleHisService;
import com.cv.inv.service.SaleManService;
import com.cv.inv.service.VouIdService;
import com.cv.inv.service.VouStatusService;
import com.cv.inv.ui.commom.VouFormatFactory;
import com.cv.inv.util.GenVouNoImpl;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class SaleEntry extends javax.swing.JPanel implements SelectionObserver, KeyListener, KeyPropagate, PanelControl {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SaleEntry.class.getName());

    private List<SaleDetailHis> listDetail = new ArrayList();
    @Autowired
    private SaleEntryTableModel saleTableModel;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private VouStatusService vouStatusService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private VouIdService voudIdService;
    @Autowired
    private SaleDetailService saleDetailService;
    @Autowired
    private SaleVouSearch vouSearchDialog;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private SaleHisService saleHisService;
    @Autowired
    private SaleManService saleManService;
    @Autowired
    private SaleOutstandingDialog saleOutDailog;
    private VouStatusAutoCompleter vouCompleter;
    private CurrencyAutoCompleter currAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private SaleManAutoCompleter saleManCompleter;
    private LoadingObserver loadingObserver;
    private SelectionObserver selectionObserver;
    private String sourceAccId;
    private final StockUP stockUp = new StockUP();
    private GenVouNoImpl vouEngine = null;
    private final SaleHis saleHis = new SaleHis();
    private final boolean isShown = false;

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
        initKeyListener();
        initTextBoxValue();
        initTextBoxFormat();
    }

    public void initMain() {
        initCombo();
        initSaleTable();
        assignDefaultValue();
        genVouNo();
        actionMapping();
    }

    private void initSaleTable() {
        tblSale.setModel(saleTableModel);
        saleTableModel.setParent(tblSale);
        saleTableModel.addEmptyRow();
        saleTableModel.setTxtTotalItem(txtTotalItem);
        saleTableModel.setSelectionObserver(this);
        tblSale.getTableHeader().setFont(Global.tblHeaderFont);
        tblSale.getTableHeader().setPreferredSize(new Dimension(30, 30));
        tblSale.setCellSelectionEnabled(true);
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(450);//Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(60);//Dep
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(60);//Location
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(1);//Qty
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(1);//Std-Wt
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(1);//Unit
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(40);//Sale Price
        tblSale.getColumnModel().getColumn(8).setPreferredWidth(40);//Amount

        addSaleTableModelListener();

        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor());
        tblSale.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        //cboDept
        JComboBox cboDepartmentCell = new JComboBox();
        cboDepartmentCell.setFont(Global.textFont);
        BindingUtil.BindCombo(cboDepartmentCell, departmentService.findAll());
        tblSale.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cboDepartmentCell));
        saleTableModel.setDepartment((Department) cboDepartmentCell.getSelectedItem());
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(30);
        //cboLoc
        JComboBox cboLocationCell = new JComboBox();
        cboLocationCell.setFont(Global.textFont);
        BindingUtil.BindCombo(cboLocationCell, locationService.findAll());
        tblSale.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cboLocationCell));
        saleTableModel.setLocation((Location) cboLocationCell.getSelectedItem());
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(30);

        tblSale.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblSale.setDefaultRenderer(Object.class, new TableCellRender());
        tblSale.setDefaultRenderer(Double.class, new TableCellRender());
        tblSale.setDefaultRenderer(Float.class, new TableCellRender());
        tblSale.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");

        tblSale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSale.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            txtRecNo.setText(Integer.toString(tblSale.getSelectedRow() + 1));
        });
    }

    private void initCombo() {
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        currAutoCompleter.setSelectionObserver(this);
        traderAutoCompleter = new TraderAutoCompleter(txtCus, Global.listTrader, null);
        traderAutoCompleter.setSelectionObserver(this);
        vouCompleter = new VouStatusAutoCompleter(txtVouStatus, Global.listVou, null);
        vouCompleter.setSelectionObserver(this);
        saleManCompleter = new SaleManAutoCompleter(txtSaleman, Global.listSaleMan, null);
        saleManCompleter.setSelectionObserver(this);
    }

    private void initKeyListener() {
        txtSaleDate.getDateEditor().getUiComponent().setName("txtSaleDate");
        txtSaleDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtCus.addKeyListener(this);
        txtSaleman.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        txtVouStatus.addKeyListener(this);
        tblSale.addKeyListener(this);
        txtVouPaid.addKeyListener(this);
        txtVouBalance.addKeyListener(this);
        txtTaxP.addKeyListener(this);
        txtTax.addKeyListener(this);
        txtDiscP.addKeyListener(this);
        txtVouDiscount.addKeyListener(this);
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
        txtTotalItem.setText("0");
    }

    private void initTextBoxFormat() {
        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtVouDiscount.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtTax.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtGrandTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtVouPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        } catch (ParseException ex) {
            LOGGER.error("setFormatterFactory : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());

        }
    }

    private void assignDefaultValue() {
        try {
            txtSaleDate.setDate(Util1.getTodayDate());
            if (Global.defaultTrader != null) {
                traderAutoCompleter.setTrader(Global.defaultTrader);
                selected("CustomerList", Global.defaultTrader);
            }
            currAutoCompleter.setCurrency(Global.defalutCurrency);
            vouCompleter.setVouStatus(Global.defaultVouStatus);
            if (Global.defaultSaleMan != null) {
                saleManCompleter.setSaleMan(Global.defaultSaleMan);
            }
        } catch (Exception e) {
            LOGGER.info("Assign Default Value :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, "Defalut Values are missing in System Property.");
        }
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(voudIdService, "SaleEntry", Util1.getPeriod(txtSaleDate.getDate()));
        txtVouNo.setText(vouEngine.genVouNo());
    }

    private void clear() {
        saleTableModel.removeListDetail();
        txtRecNo.setText("0");
        txtTotalItem.setText("0");
        initTextBoxValue();
        assignDefaultValue();
        lblStatus.setText("NEW");
    }

    public boolean saveSale() {
        boolean status = false;
        if (isValidEntry() && saleTableModel.isValidEntry()) {
            List<String> deleteList = saleTableModel.getDelList();
            try {
                String vouStatus = lblStatus.getText();
                saleDetailService.save(saleHis, saleTableModel.getListSaleDetail(), vouStatus, deleteList);
                clear();
                vouEngine.updateVouNo();
                genVouNo();
                status = true;
            } catch (Exception ex) {
                LOGGER.error("Save Sale :" + ex.getMessage());
                JOptionPane.showMessageDialog(Global.parentForm, "Could'nt saved.");
            }
        }
        return status;
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (txtVouNo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid sale voucher no.",
                    "Sale Vou No", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (traderAutoCompleter.getTrader() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Customer cannot be blank.",
                    "No customer.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCus.requestFocusInWindow();
        } else if (vouCompleter.getVouStatus() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Choose vou status.",
                    "No vou status.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtVouStatus.requestFocusInWindow();
        } else if (currAutoCompleter.getCurrency() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Choose Currency.",
                    "No vou status.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCurrency.requestFocusInWindow();
        } else {
            saleHis.setVouNo(txtVouNo.getText());
            saleHis.setCreditTerm(txtDueDate.getDate());
            saleHis.setSaleManId(saleManCompleter.getSaleMan());
            saleHis.setVouStatusId(vouCompleter.getVouStatus());
            saleHis.setRemark(txtRemark.getText());
            saleHis.setVouTotal(NumberUtil.getDouble(txtVouTotal.getText()));
            saleHis.setDiscP(NumberUtil.getDouble(txtDiscP.getText()));
            saleHis.setDiscount(NumberUtil.getDouble(txtVouDiscount.getText()));
            saleHis.setTaxP(NumberUtil.getDouble(txtTaxP.getText()));
            saleHis.setTaxAmt(NumberUtil.getDouble(txtTax.getText()));
            saleHis.setGrandTotal(NumberUtil.getDouble(txtGrandTotal.getText()));
            saleHis.setPaid(NumberUtil.getDouble(txtVouPaid.getText()));
            saleHis.setVouBalance(NumberUtil.getDouble(txtVouBalance.getText()));
            saleHis.setFromCurId(currAutoCompleter.getCurrency().getKey().getCode());
            saleHis.setDeleted(Util1.getNullTo(saleHis.getDeleted()));
            saleHis.setTraderId(traderAutoCompleter.getTrader());

            if (lblStatus.getText().equals("NEW")) {
                saleHis.setSaleDate(txtSaleDate.getDate());
                saleHis.setCreatedBy(Global.loginUser.getUserId().toString());
                saleHis.setSession(Global.sessionId);
            } else {
                Date tmpDate = txtSaleDate.getDate();
                if (!Util1.isSameDate(tmpDate, saleHis.getSaleDate())) {
                    saleHis.setSaleDate(txtSaleDate.getDate());
                }
                saleHis.setUpdatedBy(Global.loginUser.getUserId().toString());
                saleHis.setUpdatedDate(Util1.getTodayDate());
            }
            try {
                if (tblSale.getCellEditor() != null) {
                    tblSale.getCellEditor().stopCellEditing();
                }

            } catch (Exception ex) {

            }
        }
        return status;
    }

    private void deleteSale() {
        int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                "Are you sure to delete?", "Sale item delete", JOptionPane.YES_NO_OPTION);
        if (yes_no == 0) {
            String vouNo = txtVouNo.getText();
            if (lblStatus.getText().equals("EDIT")) {
                saleHisService.delete(vouNo);
                clear();
            }
        }

    }

    private void actionMapping() {
        tblSale.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
        tblSale.getActionMap().put("DELETE", actionItemDelete);

    }
    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblSale.getSelectedRow() >= 0) {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Sale item delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    saleTableModel.delete(tblSale.getSelectedRow());
                }
            }
        }
    };

    private void addSaleTableModelListener() {
        tblSale.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();
                if (column >= 0) {
                    switch (column) {
                        case 0: //Code
                        case 4: //Qty
                        case 5://Std-Wt
                        case 6: //Unit
                        case 7: //Sale price
                            calculateTotalAmount();
                            break;
                    }
                }
            }
        });
    }

    private void calculateTotalAmount() {
        double totalVouBalance;
        Double totalAmount = new Double(0);
        listDetail = saleTableModel.getListSaleDetail();

        totalAmount = listDetail.stream().map(sdh -> NumberUtil.NZero(sdh.getAmount())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);
        //cal discAmt
        double discp = NumberUtil.NZero(txtDiscP.getText());
        double discountAmt = (totalAmount * (discp / 100));
        txtVouDiscount.setValue(discountAmt);
        //calculate taxAmt
        double taxp = NumberUtil.NZero(txtTaxP.getText());
        double afterDiscountAmt = totalAmount - discountAmt;
        double totalTax = (afterDiscountAmt * taxp) / 100;
        txtTax.setValue(totalTax);

        txtGrandTotal.setValue(NumberUtil.NZero(txtVouTotal.getValue())
                + NumberUtil.NZero(txtTax.getValue())
                - NumberUtil.NZero(txtVouDiscount.getValue()));

        totalVouBalance = NumberUtil.NZero(txtGrandTotal.getValue()) - (NumberUtil.NZero(txtVouPaid.getValue()));
        txtVouBalance.setValue(totalVouBalance);
    }

    public void historySale() {
        vouSearchDialog.initMain();
        vouSearchDialog.setSize(Global.width - 250, Global.height - 120);
        vouSearchDialog.setLocationRelativeTo(null);
        vouSearchDialog.setVisible(true);
    }

    public void setSaleVoucher(SaleHis saleHis, List<SaleDetailHis> listSaleDetail) {
        if (!lblStatus.getText().equals("NEW")) {
            clear();
        }
        if (saleHis != null) {
            if (saleHis.getDeleted()) {
                lblStatus.setText("DELETED");
            } else {
                lblStatus.setText("EDIT");
            }
            txtVouNo.setText(saleHis.getVouNo());
            txtVouStatus.setText(saleHis.getVouStatusId().getStatusDesp());
            txtCus.setText(saleHis.getTraderId().getTraderName());
            txtSaleman.setText(saleHis.getSaleManId().getSaleManName());
            txtDueDate.setDate(saleHis.getCreditTerm());
            txtCurrency.setText(saleHis.getFromCurId());
            txtRemark.setText(saleHis.getRemark());
            txtSaleDate.setDate(saleHis.getSaleDate());
            txtVouTotal.setValue(saleHis.getVouTotal());
            txtDiscP.setText(Util1.getString(saleHis.getDiscP()));
            txtVouDiscount.setValue(saleHis.getDiscount());
            txtTaxP.setText(Util1.getString(saleHis.getTaxP()));
            txtTax.setValue(saleHis.getTaxAmt());
            txtGrandTotal.setValue(saleHis.getGrandTotal());
            txtVouPaid.setValue(saleHis.getPaid());
            txtVouBalance.setValue(saleHis.getVouBalance());
            saleTableModel.setListDetail(listSaleDetail);
            txtTotalItem.setText(Integer.toString(listSaleDetail.size() - 1));
        }
    }

    private void saleOutstand() {
        if (listDetail.size() > 1) {
            saleOutDailog.initMain();
            saleOutDailog.setSize(Global.width / 2, Global.height / 2);
            saleOutDailog.setLocationRelativeTo(null);
            saleOutDailog.setVisible(true);
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
        jLabel17 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCus = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtSaleman = new javax.swing.JTextField();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDueDate = new com.toedter.calendar.JDateChooser();
        txtSaleDate = new com.toedter.calendar.JDateChooser();
        txtCurrency = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        txtVouStatus = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        chkPrintOption = new javax.swing.JCheckBox();
        chkVouComp = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtRecNo = new javax.swing.JTextField();
        txtTotalItem = new javax.swing.JTextField();
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
        btnSaleOutStand = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSale = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Vou No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Customer");

        txtCus.setFont(Global.textFont);
        txtCus.setName("txtCus"); // NOI18N
        txtCus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusFocusGained(evt);
            }
        });
        txtCus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Sale Man");

        txtSaleman.setFont(Global.textFont);
        txtSaleman.setName("txtSaleman"); // NOI18N
        txtSaleman.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSalemanFocusGained(evt);
            }
        });

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Sale Date");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Credit Term");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        txtDueDate.setDateFormatString("dd/MM/yyyy");
        txtDueDate.setFont(Global.textFont);

        txtSaleDate.setDateFormatString("dd/MM/yyyy");
        txtSaleDate.setFont(Global.textFont);

        txtCurrency.setEditable(false);
        txtCurrency.setFont(Global.textFont);
        txtCurrency.setEnabled(false);
        txtCurrency.setName("txtCurrency"); // NOI18N

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Vou Status");

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        txtVouStatus.setFont(Global.textFont);
        txtVouStatus.setName("txtVouStatus"); // NOI18N
        txtVouStatus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouStatusFocusGained(evt);
            }
        });
        txtVouStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouStatusActionPerformed(evt);
            }
        });

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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtCus, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel20)
                        .addGap(18, 18, 18)
                        .addComponent(txtVouStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtSaleman))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel21)
                .addGap(18, 18, 18)
                .addComponent(txtRemark)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtVouNo, txtVouStatus});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20)
                        .addComponent(txtVouStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel21)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtCus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtSaleman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        chkPrintOption.setFont(Global.lableFont);
        chkPrintOption.setText("Vou Printer");

        chkVouComp.setFont(Global.lableFont);
        chkVouComp.setText("Vou Comp");

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Rec no:");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Total Item:");

        txtRecNo.setEditable(false);
        txtRecNo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecNo.setBorder(null);

        txtTotalItem.setEditable(false);
        txtTotalItem.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalItem.setBorder(null);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkPrintOption)
                            .addComponent(chkVouComp))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel11))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTotalItem, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRecNo, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 8, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPrintOption)
                    .addComponent(jLabel11)
                    .addComponent(txtRecNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(chkVouComp)
                    .addComponent(txtTotalItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        txtVouTotal.setEditable(false);
        txtVouTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTotal.setFont(Global.amtFont);

        jLabel7.setText("%");

        txtVouDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouDiscount.setFont(Global.amtFont);
        txtVouDiscount.setName("txtVouDiscount"); // NOI18N

        txtTaxP.setFont(Global.amtFont);
        txtTaxP.setName("txtTaxP"); // NOI18N

        txtTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTax.setFont(Global.amtFont);
        txtTax.setName("txtTax"); // NOI18N

        txtGrandTotal.setEditable(false);
        txtGrandTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGrandTotal.setFont(Global.amtFont);

        txtVouPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouPaid.setFont(Global.amtFont);
        txtVouPaid.setName("txtVouPaid"); // NOI18N

        txtVouBalance.setEditable(false);
        txtVouBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouBalance.setFont(Global.amtFont);
        txtVouBalance.setName("txtVouBalance"); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Vou Balance :");

        jLabel15.setText("%");

        txtDiscP.setFont(Global.amtFont);
        txtDiscP.setName("txtDiscP"); // NOI18N

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
                    .addComponent(txtGrandTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addComponent(txtVouPaid)
                    .addComponent(txtVouBalance))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel14)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTaxP)
                            .addComponent(txtDiscP))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouDiscount)
                            .addComponent(txtTax))
                        .addGap(1, 1, 1))))
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

        btnSaleOutStand.setFont(Global.lableFont);
        btnSaleOutStand.setText("Outstanding");
        btnSaleOutStand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaleOutStandActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSaleOutStand)
                .addContainerGap(207, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSaleOutStand)
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
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        LOGGER.info("Sale Entry Main");
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        txtVouNo.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void txtCusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusActionPerformed
        //getCustomer();
    }//GEN-LAST:event_txtCusActionPerformed

    private void btnSaleOutStandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaleOutStandActionPerformed
        saleOutstand();
    }//GEN-LAST:event_btnSaleOutStandActionPerformed


    private void txtCusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusFocusGained
        txtCus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusFocusGained

    private void txtSalemanFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSalemanFocusGained
        txtSaleman.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalemanFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained

        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtVouStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouStatusActionPerformed

    private void txtVouStatusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouStatusFocusGained
        txtVouStatus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouStatusFocusGained

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
            case "CustomerList":
                try {
                Trader cus = (Trader) selectObj;
                if (cus != null) {
                    txtCus.setText(cus.getTraderName());

                    if (cus != null) {
                        txtCus.setText(cus.getTraderName());
                        if (cus.getTraderType() != null) {
                            saleTableModel.setCusType(cus.getTraderType().getDescription());
                        } else {
                            saleTableModel.setCusType("N");
                        }
                    } else {
                        txtCus.setText(null);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("selected CustomerList : " + selectObj.toString() + " - " + ex.getMessage());
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
                    txtRemark.requestFocus();
                } else {
                    txtVouStatus.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtCus":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtVouStatus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouStatus.requestFocus();
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
                    txtVouStatus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    //  txtSaleDate.getDateEditor().getUiComponent().requestFocusInWindow();
                    tblSale.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCurrency.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtVouStatus":
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtSaleDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtSaleDate.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN
                        || e.getKeyCode() == KeyEvent.VK_RIGHT) {
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
            case "txtCurrency":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtSaleman.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtDueDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtDiscP":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateTotalAmount();
                    txtTaxP.requestFocus();
                }
                break;
            case "txtTaxP":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateTotalAmount();
                    txtVouBalance.requestFocus();
                }
                break;
            case "txtVouPaid":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateTotalAmount();
                    txtVouBalance.requestFocus();
                }
                break;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSaleOutStand;
    private javax.swing.JCheckBox chkPrintOption;
    private javax.swing.JCheckBox chkVouComp;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblSale;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtDiscP;
    private com.toedter.calendar.JDateChooser txtDueDate;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JTextField txtRecNo;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtSaleDate;
    private javax.swing.JTextField txtSaleman;
    private javax.swing.JFormattedTextField txtTax;
    private javax.swing.JTextField txtTaxP;
    private javax.swing.JTextField txtTotalItem;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouDiscount;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JTextField txtVouStatus;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void delete() {
        deleteSale();
    }

    @Override
    public void print() {
    }

    @Override
    public void save() {
        saveSale();
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        historySale();
    }

    @Override
    public void refresh() {
    }
}
