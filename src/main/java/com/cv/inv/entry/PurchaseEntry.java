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
import com.cv.accountswing.entity.Supplier;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.ui.editor.SupplierAutoCompleter;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.PurHis;
import com.cv.inv.entity.PurHisDetail;
import com.cv.inv.entry.common.PurchaseEntryTableModel;
import com.cv.inv.entry.dialog.PurchaseVouSearch;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.LocationCellEditor;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.VouStatusAutoCompleter;
import com.cv.inv.service.PurchaseDetailService;
import com.cv.inv.service.PurchaseHisService;
import com.cv.inv.service.SReportService;
import com.cv.inv.service.VouIdService;
import com.cv.inv.ui.commom.VouFormatFactory;
import com.cv.inv.util.GenVouNoImpl;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class PurchaseEntry extends javax.swing.JPanel implements SelectionObserver, KeyListener, PanelControl {

    /**
     * Creates new form PurchaseEntry
     *
     */
    private final Image historyIcon = new ImageIcon(this.getClass().getResource("/images/history_icon.png")).getImage();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PurchaseEntry.class);
    @Autowired
    private PurchaseEntryTableModel purTableModel;
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
    private SupplierAutoCompleter supplierAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private LoadingObserver loadingObserver;
    private PurHis ph = new PurHis();
    //private Gl ph;
    private GenVouNoImpl vouEngine;
    private List<PurHisDetail> listDetail = new ArrayList();
    private String voucherNo = "-";
    @Autowired
    private SReportService reportService;

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

    public PurchaseEntry() {
        initComponents();
        initKeyListener();
        initTextBoxValue();
        initTextBoxFormat();
        actionMapping();
    }

    public void initMain() {
        initCombo();
        assignDefalutValue();
        initPurTable();
        genVouNo();
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(vouIdService, "Purchase", Util1.getPeriod(txtPurDate.getDate()));
        LOGGER.info("Voucher No :" + vouEngine.genVouNo());
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
        tblPurchase.getColumnModel().getColumn(6).setPreferredWidth(30);//avg-wt
        tblPurchase.getColumnModel().getColumn(7).setPreferredWidth(40);//pur price
        tblPurchase.getColumnModel().getColumn(8).setPreferredWidth(40);//amount
        tblPurchase.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());//code
        tblPurchase.getColumnModel().getColumn(2).setCellEditor(new DepartmentCellEditor(false));
        tblPurchase.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor());//loc
        tblPurchase.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());//qty
        tblPurchase.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());//std-wt
        tblPurchase.getColumnModel().getColumn(6).setCellEditor(new AutoClearEditor());//avg-wt
        tblPurchase.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());//pur price
        tblPurchase.getColumnModel().getColumn(8).setCellEditor(new AutoClearEditor());//amt
        //table render
        tblPurchase.setDefaultRenderer(Object.class, new TableCellRender());
        tblPurchase.setDefaultRenderer(Float.class, new TableCellRender());
        //foucs enter
        tblPurchase.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tblPurchase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void focusTable() {
        tblPurchase.changeSelection(0, 0, false, false);
        tblPurchase.requestFocus();
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
        txtTaxP.setValue(0.0);
    }

    private void initTextBoxFormat() {
        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtVouDiscount.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtTax.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtVouPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtDiscP.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtTaxP.setFormatterFactory(NumberUtil.getDecimalFormat());
        } catch (ParseException ex) {
            LOGGER.error("initTextBoxFormat :" + ex.getMessage());
        }
    }

    private void initCombo() {
        if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
            traderAutoCompleter = new TraderAutoCompleter(txtSupplier, Global.listTrader, null, false);
        } else {
            supplierAutoCompleter = new SupplierAutoCompleter(txtSupplier, Global.listSupplier, null);
        }
        vouCompleter = new VouStatusAutoCompleter(txtVouStatus, Global.listVou, null);
        vouCompleter.setSelectionObserver(this);
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        currAutoCompleter.setSelectionObserver(this);
        locCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        locCompleter.setLocation(Global.defaultLocation);
        locCompleter.setSelectionObserver(this);
    }

    private void assignDefalutValue() {
        try {
            txtPurDate.setDate(Util1.getTodayDate());
            currAutoCompleter.setCurrency(Global.defalutCurrency);
            vouCompleter.setVouStatus(Global.defaultVouStatus);
        } catch (Exception e) {
            LOGGER.info("Assign Default Value :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, "Defalut Values are missing in System Property.");
        }

    }

    public void clear() {
        assignDefalutValue();
        txtRefNo.setText(null);
        txtRemark.setText(null);
        initTextBoxValue();
        purTableModel.clear();
        lblStatus.setText("NEW");
        ph = new PurHis();
        genVouNo();
    }

    public boolean savePurchase() {
        boolean status = true;
        if (isValidEntry() && purTableModel.isValidEntry()) {
            if (ph.getTrader() != null) {
                List<String> delList = purTableModel.getDelList();
                try {
                    purchaseDetailService.save(ph, purTableModel.getListPurDetail(), delList);
                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.updateVouNo();
                    }
                    clear();
                } catch (Exception ex) {
                    status = false;
                    LOGGER.error("Save Purchase :" + ex.getMessage());
                    JOptionPane.showMessageDialog(Global.parentForm, "Could'nt saved.");
                }
            } else {
                JOptionPane.showMessageDialog(Global.parentForm, "Invalid Supplier.");
            }

        }
        return status;
    }

    private boolean isValidEntry() {
        boolean status = true;
        if (txtVouNo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid voucher no.",
                    "Invalid Voucher ID.", JOptionPane.ERROR_MESSAGE);
            status = false;
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
            ph.setVouNo(txtVouNo.getText());
            ph.setDueDate(txtDueDate.getDate());
            ph.setLocation(locCompleter.getLocation());
            ph.setVouStatus(vouCompleter.getVouStatus());
            ph.setRemark(txtRemark.getText());
            ph.setPaid(Util1.getFloat(txtVouPaid.getValue()));
            ph.setBalance(Util1.getFloat(txtVouBalance.getValue()));
            ph.setTaxP(Util1.getFloat(txtTaxP.getValue()));
            ph.setTaxAmt(Util1.getFloat(txtTax.getValue()));
            ph.setRefNo(txtRefNo.getText());
            ph.setDeleted(Util1.getNullTo(ph.getDeleted()));
            ph.setDiscP(Util1.getFloat(txtDiscP.getValue()));
            ph.setDiscount(Util1.getFloat(txtVouDiscount.getValue()));
            ph.setCurrency(currAutoCompleter.getCurrency());
            ph.setPurDate(txtPurDate.getDate());
            if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
                ph.setTrader(traderAutoCompleter.getTrader());
            } else {
                ph.setTrader(supplierAutoCompleter.getTrader());
            }
            if (lblStatus.getText().equals("NEW")) {
                ph.setCreatedBy(Global.loginUser);
                ph.setSession(Global.sessionId);
                ph.setMacId(Global.machineId);
            } else {
                ph.setUpdatedBy(Global.loginUser.getAppUserCode());
            }

            //cal total
            Float vouTotal = 0.0f;
            vouTotal = purTableModel.getListPurDetail().stream().filter(pd -> (pd.getStock() != null)).map(pd -> pd.getPurAmt()).reduce(vouTotal, (accumulator, _item) -> accumulator + _item);
            ph.setVouTotal(vouTotal);

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
        if (purHis != null) {
            ph = purHis;
            if (purHis.getDeleted()) {
                lblStatus.setText("DELETED");
            } else {
                lblStatus.setText("EDIT");
            }
            txtVouNo.setText(ph.getVouNo());
            txtRemark.setText(ph.getRemark());
            txtPurDate.setDate(ph.getPurDate());
            txtDueDate.setDate(ph.getDueDate());
            locCompleter.setLocation(ph.getLocation());
            vouCompleter.setVouStatus(ph.getVouStatus());
            currAutoCompleter.setCurrency(ph.getCurrency());
            txtVouTotal.setValue(Util1.getFloat(ph.getVouTotal()));
            txtVouPaid.setValue(Util1.getFloat(ph.getPaid()));
            txtVouDiscount.setValue(Util1.getFloat(ph.getDiscount()));
            txtDiscP.setValue(Util1.getFloat(ph.getDiscP()));
            txtVouBalance.setValue(Util1.getFloat(ph.getBalance()));
            txtTaxP.setValue(Util1.getFloat(ph.getTaxP()));
            txtTax.setValue(Util1.getFloat(ph.getTaxAmt()));
            if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
                traderAutoCompleter.setTrader(ph.getTrader());
            } else {
                supplierAutoCompleter.setTrader((Supplier) ph.getTrader());
            }
            purTableModel.setListPurDetail(listDetailHis);
            purTableModel.addNewRow();
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
                try {
                    phService.delete(vouNo);
                } catch (Exception ex) {
                    LOGGER.error("Delete Purchase Voucher :" + ex.getMessage());
                }
                clear();
            }
        }
    }

    private void calculateTotalAmount() {
        float totalAmount = 0.0f;
        float discountAmt = 0.0f;
        listDetail = purTableModel.getListPurDetail();
        totalAmount = listDetail.stream().map(pd -> Util1.getFloat(pd.getPurAmt())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);
        //cal discAmt
        if (Util1.getFloat(txtDiscP.getValue()) > 0) {
            float discp = Util1.getFloat(txtDiscP.getValue());
            discountAmt = (totalAmount * (discp / 100));
            txtVouDiscount.setValue(discountAmt);
        }

        //calculate taxAmt
        if (Util1.getFloat(txtTaxP.getValue()) > 0) {
            float afterDiscountAmt = totalAmount - discountAmt;
            float totalTax = (afterDiscountAmt * Util1.getFloat(txtTaxP.getValue())) / 100;
            txtTax.setValue(totalTax);
        }

        float grossTotal = Util1.getFloat(txtVouTotal.getValue());
        float discount = Util1.getFloat(txtVouDiscount.getValue());
        float tax = Util1.getFloat(txtTax.getValue());
        float grandTotal = (grossTotal + tax) - discount;
        float balance = grandTotal - (Util1.getFloat(txtVouPaid.getValue()));
        txtVouBalance.setValue(balance);
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

    private void printSaveVoucher() {
        boolean save;
        if (lblStatus.getText().equals("EDIT")) {
            voucherNo = txtVouNo.getText();
            save = true;
        } else {
            voucherNo = txtVouNo.getText();
            save = savePurchase();
        }
        if (save) {
            String reportName = Global.sysProperties.get("system.purchase.report");
            if (reportName != null) {
                String compName = Global.sysProperties.get("system.report.company");
                String address = Global.sysProperties.get("system.report.address");
                String phone = Global.sysProperties.get("system.report.phone");
                String reportPath = Global.sysProperties.get("system.report.path");
                String fontPath = Global.sysProperties.get("system.font.path");
                reportPath = reportPath + "\\" + reportName;
                Map<String, Object> parameters = new HashMap();
                parameters.put("company_name", compName);
                parameters.put("address", address);
                parameters.put("phone", phone);
                parameters.put("vou_no", voucherNo);
                reportService.reportViewer(reportPath, fontPath, fontPath, parameters);
            } else {
                JOptionPane.showMessageDialog(Global.parentForm, "Report Name Not Found.");
            }
        }
    }

    private void requesTable() {
        tblPurchase.changeSelection(0, 0, false, false);
        tblPurchase.requestFocus();
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
        jLabel15 = new javax.swing.JLabel();
        txtTax = new javax.swing.JFormattedTextField();
        txtVouPaid = new javax.swing.JFormattedTextField();
        txtVouBalance = new javax.swing.JFormattedTextField();
        txtTaxP = new javax.swing.JFormattedTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, java.awt.Color.lightGray));

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
                        .addComponent(txtVouNo, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
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
                    .addComponent(txtRefNo, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLocation)
                    .addComponent(txtVouStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
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

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, java.awt.Color.lightGray));

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

        txtVouTotal.setEditable(false);
        txtVouTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtVouTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouTotal.setFont(Global.amtFont);

        txtDiscP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscP.setFont(Global.amtFont);
        txtDiscP.setName("txtDiscP"); // NOI18N
        txtDiscP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscPFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscPFocusLost(evt);
            }
        });
        txtDiscP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscPActionPerformed(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("%");

        txtVouDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouDiscount.setFont(Global.amtFont);
        txtVouDiscount.setName("txtVouDiscount"); // NOI18N
        txtVouDiscount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVouDiscountFocusLost(evt);
            }
        });
        txtVouDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouDiscountActionPerformed(evt);
            }
        });

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("%");

        txtTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTax.setFont(Global.amtFont);
        txtTax.setName("txtTax"); // NOI18N
        txtTax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTaxFocusLost(evt);
            }
        });
        txtTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxActionPerformed(evt);
            }
        });

        txtVouPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouPaid.setFont(Global.amtFont);
        txtVouPaid.setName("txtVouPaid"); // NOI18N
        txtVouPaid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVouPaidFocusLost(evt);
            }
        });
        txtVouPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouPaidActionPerformed(evt);
            }
        });

        txtVouBalance.setEditable(false);
        txtVouBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouBalance.setFont(Global.amtFont);
        txtVouBalance.setName("txtVouBalance"); // NOI18N

        txtTaxP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txtTaxP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTaxP.setFont(Global.amtFont);
        txtTaxP.setName("txtDiscP"); // NOI18N
        txtTaxP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTaxPFocusLost(evt);
            }
        });
        txtTaxP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxPActionPerformed(evt);
            }
        });

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
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDiscP, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(txtTaxP, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
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
                    .addComponent(jLabel15)
                    .addComponent(txtTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        mainFrame.setControl(this);
        if (purTableModel.getListPurDetail().size() > 0) {
            requesTable();
        } else {
            txtSupplier.requestFocus();
        }
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

    private void txtVouDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouDiscountActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouDiscountActionPerformed

    private void txtVouDiscountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouDiscountFocusLost
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouDiscountFocusLost

    private void txtTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtTaxActionPerformed

    private void txtTaxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxFocusLost
        // TODO add your handling code here:
        calculateTotalAmount();

    }//GEN-LAST:event_txtTaxFocusLost

    private void txtTaxPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxPActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();

    }//GEN-LAST:event_txtTaxPActionPerformed

    private void txtTaxPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxPFocusLost
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtTaxPFocusLost

    private void txtVouPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouPaidActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouPaidActionPerformed

    private void txtVouPaidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouPaidFocusLost
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouPaidFocusLost

    private void txtDiscPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscPActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtDiscPActionPerformed

    private void txtDiscPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscPFocusLost
        // TODO add your handling code here:
        calculateTotalAmount();

    }//GEN-LAST:event_txtDiscPFocusLost

    private void txtDiscPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscPFocusGained
        // TODO add your handling code here:
        txtDiscP.select(0, txtDiscP.getText().length() - 1);
    }//GEN-LAST:event_txtDiscPFocusGained


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
    private javax.swing.JFormattedTextField txtTaxP;
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
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouStatus.requestFocus();
                } else {
                    txtPurDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtPurDate":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouStatus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

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

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouStatus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtDueDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCurrency.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtDueDate":

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtPurDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtSupplier.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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

                tabToTable(e);
                break;
            case "txtVouStatus":

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
                    txtTaxP.requestFocus();
                }
                break;
            case "txtVouDiscount":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtTaxP.requestFocus();
                }
                break;
            case "txtTaxP":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouPaid.requestFocus();
                }
                break;
            case "txtTax":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouPaid.requestFocus();
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
        printSaveVoucher();
    }

    @Override
    public void save() {
        savePurchase();
    }

    @Override
    public void refresh() {
    }

}
