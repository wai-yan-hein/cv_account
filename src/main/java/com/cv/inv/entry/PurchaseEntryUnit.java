/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.PurHis;
import com.cv.inv.entity.PurHisDetail;
import com.cv.inv.entry.common.PurchaseEntryTableModelUnit;
import com.cv.inv.entry.dialog.PurchaseVouSearch;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.LocationCellEditor;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.entry.editor.VouStatusAutoCompleter;
import com.cv.inv.service.PurchaseDetailService;
import com.cv.inv.service.PurchaseHisService;
import com.cv.inv.service.VouIdService;
import com.cv.inv.util.GenVouNoImpl;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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
public class PurchaseEntryUnit extends javax.swing.JPanel implements SelectionObserver, KeyListener, PanelControl {

    /**
     * Creates new form PurchaseEntry
     *
     */
    private final Image historyIcon = new ImageIcon(this.getClass().getResource("/images/history_icon.png")).getImage();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PurchaseEntryUnit.class);
    @Autowired
    private PurchaseEntryTableModelUnit purTableModel;
    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private VouIdService vouIdService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private PurchaseVouSearch pvSearchDialog;
    @Autowired
    private PurchaseHisService phService;
    private boolean isShown = false;
    private LocationAutoCompleter locCompleter;
    private VouStatusAutoCompleter vouCompleter;
    private CurrencyAutoCompleter currAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private LoadingObserver loadingObserver;
    private final PurHis ph = new PurHis();
    //private Gl ph;
    private GenVouNoImpl vouEngine;
    private List<PurHisDetail> listDetail = new ArrayList();

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
        clear();
    }

    public LoadingObserver getLoadingObserver() {
        return loadingObserver;
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public PurchaseEntryUnit() {
        initComponents();
        initKeyListener();
        initTextBoxValue();
        initTextBoxFormat();
    }

    public void initMain() {
        initCombo();
        actionMapping();
        assignDefalutValue();
        initPurTable();
        isShown = true;
        // calculateTotalAmount();
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(vouIdService, "RetIn", Util1.getPeriod(txtPurDate.getDate()));
        txtVouNo.setText(vouEngine.genVouNo());
    }

    private void initPurTable() {
        tblPurchase.setModel(purTableModel);
        purTableModel.setParent(tblPurchase);
        purTableModel.setLocationAutoCompleter(locCompleter);
        purTableModel.addNewRow();
        purTableModel.setCallBack(this);
        purTableModel.setTxtTotalAmt(txtVouTotal);
        tblPurchase.getTableHeader().setFont(Global.lableFont);
        tblPurchase.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblPurchase.getTableHeader().setForeground(ColorUtil.foreground);
        tblPurchase.setCellSelectionEnabled(true);
        tblPurchase.getColumnModel().getColumn(0).setPreferredWidth(50);//code
        tblPurchase.getColumnModel().getColumn(1).setPreferredWidth(300);//description
        tblPurchase.getColumnModel().getColumn(2).setPreferredWidth(50);//dept
        tblPurchase.getColumnModel().getColumn(3).setPreferredWidth(50);//location
        tblPurchase.getColumnModel().getColumn(4).setPreferredWidth(10);//qty
        tblPurchase.getColumnModel().getColumn(5).setPreferredWidth(10);//std-wt
        tblPurchase.getColumnModel().getColumn(6).setPreferredWidth(5);//unit
        tblPurchase.getColumnModel().getColumn(7).setPreferredWidth(30);//avg-wt
        tblPurchase.getColumnModel().getColumn(8).setPreferredWidth(40);//pur price
        tblPurchase.getColumnModel().getColumn(9).setPreferredWidth(40);//amount
        tblPurchase.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());//code
        tblPurchase.getColumnModel().getColumn(2).setCellEditor(new DepartmentCellEditor());
        tblPurchase.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor());//loc
        tblPurchase.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        tblPurchase.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//std-wt
        tblPurchase.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor());//unit
        tblPurchase.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());//avg-wt
        tblPurchase.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());//pur price
        tblPurchase.getColumnModel().getColumn(9).setCellEditor(new AutoClearEditor());//amt
        //table render
        tblPurchase.setDefaultRenderer(Float.class, new TableCellRender());
        tblPurchase.setDefaultRenderer(Double.class, new TableCellRender());
        tblPurchase.setDefaultRenderer(Object.class, new TableCellRender());
        //foucs enter
        tblPurchase.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPurchase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initKeyListener() {
        txtVouNo.addKeyListener(this);
        txtPurDate.getDateEditor().getUiComponent().setName("txtPurDate");
        txtPurDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtDueDate.getDateEditor().getUiComponent().setName("txtDueDate");
        txtDueDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtSupplier.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtRefNo.addKeyListener(this);
        txtCurrency.addKeyListener(this);
        //txtLocation.addKeyListener(this);
        txtVouStatus.addKeyListener(this);
        tblPurchase.addKeyListener(this);
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
        txtVouPaid.setValue(0.00);
        txtVouBalance.setValue(0.00);
        txtDiscP.setValue(0.0);
        txtTaxP.setText("0");
    }

    private void initTextBoxFormat() {
        txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouDiscount.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTax.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscP.setFormatterFactory(NumberUtil.getDecimalFormat());
    }

    private void initCombo() {
        vouCompleter = new VouStatusAutoCompleter(txtVouStatus, Global.listVou, null);
        vouCompleter.setSelectionObserver(this);
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        currAutoCompleter.setSelectionObserver(this);
        traderAutoCompleter = new TraderAutoCompleter(txtSupplier, Global.listTrader, null);
        traderAutoCompleter.setSelectionObserver(this);
        locCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        locCompleter.setLocation(Global.defaultLocation);
        locCompleter.setSelectionObserver(this);
    }

    private void assignDefalutValue() {
        try {
            txtPurDate.setDate(Util1.getTodayDate());
            currAutoCompleter.setCurrency(Global.defalutCurrency);
            vouCompleter.setVouStatus(Global.defaultVouStatus);
            traderAutoCompleter.setTrader(Global.defaultSupplier);
            genVouNo();
        } catch (Exception e) {
            LOGGER.info("Assign Default Value :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, "Defalut Values are missing in System Property.");
        }

    }

    public void clear() {
        txtRefNo.setText(null);
        txtRemark.setText(null);
        initTextBoxValue();
        purTableModel.clear();
        lblStatus.setText("NEW");
        genVouNo();
    }

    public void savePurchase() {
        if (isValidEntry() && purTableModel.isValidEntry()) {
            List<String> delList = purTableModel.getDelList();
            try {
                purchaseDetailService.save(ph, purTableModel.getListPurDetail(), delList);
                clear();
                vouEngine.updateVouNo();
                genVouNo();
            } catch (Exception ex) {
                LOGGER.error("Save Purchase :" + ex.getMessage());
                JOptionPane.showMessageDialog(Global.parentForm, "Could'nt saved.");
            }

        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtVouNo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid voucher no.",
                    "Invalid Voucher ID.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (traderAutoCompleter.getTrader() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Supplier cannot be blank.",
                    "No supplier.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtSupplier.requestFocus();
        } else if (vouCompleter.getVouStatus() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Choose vou status.",
                    "No vou status.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtVouStatus.requestFocus();
        } else if (locCompleter.getLocation() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Choose Location.",
                    "No Location", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else {
            ph.setPurInvId(txtVouNo.getText());
            ph.setDueDate(txtDueDate.getDate());
            ph.setLocation(locCompleter.getLocation());
            ph.setVouStatus(vouCompleter.getVouStatus());
            ph.setRemark(txtRemark.getText());
            ph.setVouTotal(NumberUtil.getDouble(txtVouTotal.getText()));
            ph.setPaid(NumberUtil.getDouble(txtVouPaid.getText()));
            ph.setBalance(NumberUtil.getDouble(txtVouBalance.getText()));
            ph.setTaxAmt(NumberUtil.getDouble(txtTax.getText()));
            ph.setRefNo(txtRefNo.getText());
            ph.setIntgUpdStatus(null);
            ph.setDeleted(Util1.getNullTo(ph.getDeleted()));
            if (lblStatus.getText().equals("NEW")) {
                ph.setPurDate(txtPurDate.getDate());
            } else {
                Date tmpDate = txtPurDate.getDate();
                if (!Util1.isSameDate(tmpDate, ph.getPurDate())) {
                    ph.setPurDate(txtPurDate.getDate());
                }
            }
            ph.setCurrency((currAutoCompleter.getCurrency())); //Need to change
            ph.setCustomerId(traderAutoCompleter.getTrader());
            if (lblStatus.getText().equals("NEW")) {
                ph.setCreatedBy(Global.loginUser);
                ph.setSession(Global.sessionId);
            } else {
                ph.setUpdatedBy(Global.loginUser.getUserId().toString());
                ph.setUpdatedDate(Util1.getTodayDate());
            }

            try {
                if (tblPurchase.getCellEditor() != null) {
                    tblPurchase.getCellEditor().stopCellEditing();
                }

            } catch (Exception ex) {

            }
        }

        return status;
    }

    public void setPurchaseVoucher(PurHis purHis, List<PurHisDetail> listDetailHis) {
        if (!lblStatus.getText().equals("NEW")) {
            clear();
        }
        if (purHis != null) {
            if (purHis.getDeleted()) {
                lblStatus.setText("DELETED");
            } else {
                lblStatus.setText("EDIT");
            }
            txtVouNo.setText(purHis.getPurInvId());
            txtRemark.setText(purHis.getRemark());
            txtPurDate.setDate(purHis.getPurDate());
            txtDueDate.setDate(purHis.getDueDate());
            locCompleter.setLocation(purHis.getLocation());
            traderAutoCompleter.setTrader(purHis.getCustomerId());
            vouCompleter.setVouStatus(purHis.getVouStatus());
            if (purHis.getDeptCode() != null) {
                departmentAutoCompleter.setDepartment(purHis.getDeptCode());
            }
            currAutoCompleter.setCurrency(purHis.getCurrency());
            txtVouTotal.setValue(purHis.getVouTotal()); 
            txtVouPaid.setText(purHis.getPaid().toString());
            if (purHis.getDiscount() == null) {
                txtVouDiscount.setText("0.0");
            } else {
                txtVouDiscount.setText(purHis.getDiscount().toString());
            }
            txtVouBalance.setText(purHis.getBalance().toString());
            if (purHis.getDiscP() == null) {
                txtDiscP.setText("0.0");
            } else {
                txtDiscP.setText(purHis.getDiscP().toString());
            }
            if (purHis.getTaxP() == null) {
                txtTaxP.setText("0.0");
            } else {
                txtTaxP.setText(purHis.getTaxP().toString());
            }
            if (purHis.getTaxAmt() == null) {
                txtTax.setText("0.0");
            } else {
                txtTax.setText(purHis.getTaxAmt().toString());
            }
            purTableModel.setListPurDetail(listDetailHis);
        }
    }

    private void actionMapping() {
        //F8 event on tblSale    KeyEvent.getKeyCode() == KeyEvent.VK_DELETE
        tblPurchase.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
        tblPurchase.getActionMap().put("DELETE", actionItemDelete);

        //Enter event on tblSale
//        tblPurchase.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
//        tblPurchase.getActionMap().put("ENTER-Action", actionTblPurEnterKey);
    }
    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblPurchase.getSelectedRow() >= 0) {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Damage item delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    purTableModel.delete(tblPurchase.getSelectedRow());
                }
            }
        }
    };

    private void deletePurchase() {
        int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                "Are you sure to delete?", "Damage item delete", JOptionPane.YES_NO_OPTION);
        if (yes_no == 0) {
            String vouNo = txtVouNo.getText();
            if (lblStatus.getText().equals("EDIT")) {
                phService.delete(vouNo);
                clear();
            }
        }
    }

    private void calculateTotalAmount() {
        double totalAmount = 0.0;
        listDetail = purTableModel.getListPurDetail();
        totalAmount = listDetail.stream().map(pd -> Util1.getDouble(pd.getPurAmt())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
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

        Double grossTotal = Util1.NZeroDouble(txtVouTotal.getText());
        Double discount = Util1.NZeroDouble(txtVouDiscount.getText());
        Double tax = Util1.NZeroDouble(txtTax.getText());
        Double grandTotal = (grossTotal + tax) - discount;
        Double balance = grandTotal - (NumberUtil.NZero(txtVouPaid.getValue()));
        txtVouBalance.setText(balance.toString());
    }

    private void setAllLocation() {
        List<PurHisDetail> listPurDetail = purTableModel.getListPurDetail();
        if (listPurDetail != null) {
            listPurDetail.forEach(pd -> {
                pd.setLocation(locCompleter.getLocation());
            });
        }
        purTableModel.setListPurDetail(listPurDetail);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtRefNo = new javax.swing.JTextField();
        txtCurrency = new javax.swing.JTextField();
        txtDueDate = new com.toedter.calendar.JDateChooser();
        txtPurDate = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        txtVouStatus = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPurchase = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        txtDiscP = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        txtVouDiscount = new javax.swing.JFormattedTextField();
        txtTaxP = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtTax = new javax.swing.JFormattedTextField();
        txtVouPaid = new javax.swing.JFormattedTextField();
        txtVouBalance = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Pur Date");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Supplier");

        txtSupplier.setFont(Global.textFont);
        txtSupplier.setName("txtSupplier"); // NOI18N
        txtSupplier.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSupplierFocusGained(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Ref No");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Currency");

        txtRefNo.setFont(Global.textFont);
        txtRefNo.setName("txtRefNo"); // NOI18N

        txtCurrency.setEditable(false);
        txtCurrency.setFont(Global.textFont);
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrency.setEnabled(false);
        txtCurrency.setName("txtCurrency"); // NOI18N

        txtDueDate.setDateFormatString("dd/MM/yyyy");
        txtDueDate.setFont(Global.lableFont);

        txtPurDate.setDateFormatString("dd/MM/yyyy");
        txtPurDate.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Vou Status");

        txtVouStatus.setFont(Global.textFont);
        txtVouStatus.setName("txtVouStatus"); // NOI18N
        txtVouStatus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouStatusFocusGained(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Credit Term");

        txtVouNo.setEditable(false);
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtVouStatus"); // NOI18N
        txtLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLocationFocusGained(evt);
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(19, 19, 19)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtSupplier, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtPurDate, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                    .addComponent(txtRemark))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRefNo, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLocation)
                    .addComponent(txtVouStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel6, jLabel7});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3, jLabel4});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPurDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(txtVouStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(txtRefNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel8, txtDueDate});

        tblPurchase.setFont(Global.textFont);
        tblPurchase.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblPurchase.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblPurchase);

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        jButton1.setFont(Global.lableFont);
        jButton1.setText("Outstanding");

        jButton2.setFont(Global.lableFont);
        jButton2.setText("Item Promo ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Total :");

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Discount :");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Tax :");

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Paid :");

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Vou Balance :");

        txtVouTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtVouTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTotal.setFont(Global.amtFont);

        txtDiscP.setFont(Global.amtFont);
        txtDiscP.setName("txtDiscP"); // NOI18N

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("%");

        txtVouDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouDiscount.setFont(Global.amtFont);
        txtVouDiscount.setName("txtVouDiscount"); // NOI18N

        txtTaxP.setFont(Global.amtFont);
        txtTaxP.setName("txtTaxP"); // NOI18N

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("%");

        txtTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTax.setFont(Global.amtFont);
        txtTax.setName("txtTax"); // NOI18N

        txtVouPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouPaid.setFont(Global.amtFont);
        txtVouPaid.setName("txtVouPaid"); // NOI18N

        txtVouBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouBalance.setFont(Global.amtFont);
        txtVouBalance.setName("txtVouBalance"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel12)
                    .addComponent(jLabel14)
                    .addComponent(jLabel11)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouPaid, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtTaxP, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(txtDiscP, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVouDiscount, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTax))))
                    .addComponent(txtVouTotal))
                .addGap(2, 2, 2))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(txtVouDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtVouPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        txtVouNo.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void txtSupplierFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSupplierFocusGained
        // TODO add your handling code here:
        txtSupplier.selectAll();
    }//GEN-LAST:event_txtSupplierFocusGained

    private void txtVouStatusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouStatusFocusGained
        txtVouStatus.selectAll();
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVouStatusFocusGained

    private void txtLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLocationFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblPurchase;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JFormattedTextField txtDiscP;
    private com.toedter.calendar.JDateChooser txtDueDate;
    private javax.swing.JTextField txtLocation;
    private com.toedter.calendar.JDateChooser txtPurDate;
    private javax.swing.JTextField txtRefNo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JFormattedTextField txtTax;
    private javax.swing.JTextField txtTaxP;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouDiscount;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JTextField txtVouStatus;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            switch (source.toString()) {

                case "STM-TOTAL":
                    calculateTotalAmount();
                    break;
                case "Location":
                    setAllLocation();
                    break;

            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblPurchase.requestFocus();
            if (tblPurchase.getRowCount() >= 0) {
                tblPurchase.setRowSelectionInterval(0, 0);
            }
        }
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
                    txtVouStatus.requestFocus();
                } else {
                    txtPurDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtPurDate":
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtPurDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtSupplier.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtDueDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtSupplier":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtPurDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouStatus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {

                    tblPurchase.setColumnSelectionInterval(0, 0);
                    tblPurchase.requestFocus();
                    //    tblPurchase.getSelectionModel().getSelectionMode();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtRefNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtSupplier.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouStatus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtDueDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCurrency.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtDueDate":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtPurDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtDueDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtRefNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouStatus.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtRefNo":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtDueDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtSupplier.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouStatus.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtCurrency":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouStatus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtRefNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtVouStatus":
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCurrency.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDueDate.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouNo.requestFocus();
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

            case "txtPaid":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateTotalAmount();
                    txtVouBalance.requestFocus();
                }
                break;
        }
    }

    @Override
    public void delete() {
        deletePurchase();
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        pvSearchDialog.setIconImage(historyIcon);
        pvSearchDialog.initMain();
        pvSearchDialog.setSize(Global.width - 500, Global.height - 300);
        pvSearchDialog.setLocationRelativeTo(null);
        pvSearchDialog.setVisible(true);
    }

    @Override
    public void print() {
    }

    @Override
    public void save() {
        savePurchase();
    }

    @Override
    public void refresh() {
    }

}