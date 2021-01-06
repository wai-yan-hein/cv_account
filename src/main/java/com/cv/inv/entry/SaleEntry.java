/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.accountswing.common.ColorUtil;
import com.cv.inv.entry.dialog.SaleVouSearch;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.KeyPropagate;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Customer;
import com.cv.accountswing.entity.Region;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.CurrencyAutoCompleter;
import com.cv.accountswing.ui.editor.CustomerAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentAutoCompleter;
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Order;
import com.cv.inv.entity.OrderDetail;
import com.cv.inv.entity.SaleHisDetail;
import com.cv.inv.entity.SaleHis;
import com.cv.inv.entry.common.SaleEntryTableModel;
import com.cv.inv.entry.dialog.OrderSearchDialog;
import com.cv.inv.entry.dialog.SaleOutstandingDialog;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.LocationCellEditor;
import com.cv.inv.entry.editor.SaleManAutoCompleter;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.VouStatusAutoCompleter;
import com.cv.inv.service.OrderDetailService;
import com.cv.inv.service.SReportService;
import com.cv.inv.service.SaleDetailService;
import com.cv.inv.service.SaleHisService;
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
import java.util.Date;
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
import javax.swing.event.ListSelectionEvent;
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
    private final Image historyIcon = new ImageIcon(this.getClass().getResource("/images/history_icon.png")).getImage();

    private List<SaleHisDetail> listDetail = new ArrayList();
    @Autowired
    private SaleEntryTableModel saleTableModel;
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
    private SaleOutstandingDialog saleOutDailog;
    @Autowired
    private SReportService reportService;
    @Autowired
    private OrderDetailService detailService;
    @Autowired
    private OrderSearchDialog orderSearchDialog;
    private VouStatusAutoCompleter vouCompleter;
    private CurrencyAutoCompleter currAutoCompleter;
    private CustomerAutoCompleter traderAutoCompleter;
    private SaleManAutoCompleter saleManCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private DepartmentAutoCompleter departmentAutoCompleter;
    private LoadingObserver loadingObserver;
    private SelectionObserver selectionObserver;
    private String sourceAccId;
    private GenVouNoImpl vouEngine = null;
    private SaleHis saleHis = new SaleHis();
    private boolean isShown = false;
    private String voucherNo = "-";
    private Region region;
    private String orderCode;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public LoadingObserver getLoadingObserver() {
        return loadingObserver;
    }

    public SelectionObserver getSelectionObserver() {
        return selectionObserver;
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
        actionMapping();
    }

    public void initMain() {
        initCombo();
        initSaleTable();
        assignDefaultValue();
        genVouNo();
    }

    private void initSaleTable() {
        tblSale.setModel(saleTableModel);
        saleTableModel.setParent(tblSale);
        saleTableModel.setLocationAutoCompleter(locationAutoCompleter);
        saleTableModel.addEmptyRow();
        saleTableModel.setTxtTotalItem(txtTotalItem);
        saleTableModel.setSelectionObserver(this);
        tblSale.getTableHeader().setFont(Global.tblHeaderFont);
        tblSale.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblSale.getTableHeader().setForeground(ColorUtil.foreground);
        tblSale.getTableHeader().setBackground(ColorUtil.btnEdit);
        tblSale.getTableHeader().setForeground(ColorUtil.foreground);
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
        tblSale.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblSale.getColumnModel().getColumn(2).setCellEditor(new DepartmentCellEditor());
        tblSale.getColumnModel().getColumn(3).setCellEditor(new LocationCellEditor());
        tblSale.getColumnModel().getColumn(4).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(5).setCellEditor(new AutoClearEditor());
        tblSale.getColumnModel().getColumn(6).setCellEditor(new StockUnitEditor());
        tblSale.getColumnModel().getColumn(7).setCellEditor(new AutoClearEditor());
        tblSale.setDefaultRenderer(Object.class, new TableCellRender());
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
        traderAutoCompleter = new CustomerAutoCompleter(txtCus, Global.listCustomer, null);
        traderAutoCompleter.setSelectionObserver(this);
        vouCompleter = new VouStatusAutoCompleter(txtVouStatus, Global.listVou, null);
        saleManCompleter = new SaleManAutoCompleter(txtSaleman, Global.listSaleMan, null);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        locationAutoCompleter.setSelectionObserver(this);
        locationAutoCompleter.setLocation(Global.defaultLocation);
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
        txtTaxP.setValue(0.0);
        txtDiscP.setValue(0.0);
        txtTotalItem.setText("0");
        txtRecNo.setText("0");
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
            txtTaxP.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtDiscP.setFormatterFactory(NumberUtil.getDecimalFormat());
        } catch (ParseException ex) {
            LOGGER.error("setFormatterFactory : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());

        }
    }

    private void assignDefaultValue() {
        try {
            txtSaleDate.setDate(Util1.getTodayDate());
            //traderAutoCompleter.setTrader(Global.defaultCustomer);
            currAutoCompleter.setCurrency(Global.defalutCurrency);
            vouCompleter.setVouStatus(Global.defaultVouStatus);
            saleManCompleter.setSaleMan(Global.defaultSaleMan);
            txtVouNo.requestFocus();
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
        saleTableModel.clearDelList();
        initTextBoxValue();
        assignDefaultValue();
        genVouNo();
        saleHis = new SaleHis();
        lblStatus.setText("NEW");

    }

    public boolean saveSale() {
        boolean status = false;
        try {
            if (isValidEntry() && saleTableModel.isValidEntry()) {
                List<String> deleteList = saleTableModel.getDelList();
                String vouStatus = lblStatus.getText();
                saleDetailService.save(saleHis, saleTableModel.getListSaleDetail(), vouStatus, deleteList);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                clear();
                genVouNo();
                status = true;
            }
        } catch (Exception ex) {
            LOGGER.error("Save Sale :" + ex.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, "Could'nt saved.");
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
            txtCus.requestFocus();
        } else if (vouCompleter.getVouStatus() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Choose vou status.",
                    "No vou status.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtVouStatus.requestFocus();
        } else if (currAutoCompleter.getCurrency() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Choose Currency.",
                    "No Currency.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCurrency.requestFocus();
        } else if (locationAutoCompleter.getLocation() == null) {
            JOptionPane.showMessageDialog(Global.parentForm, "Choose Location.",
                    "No Location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else if (Util1.getFloat(txtVouTotal.getValue()) <= 0) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Voucher.",
                    "No Sale Record.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else {
            saleHis.setVouNo(txtVouNo.getText());
            saleHis.setCreditTerm(txtDueDate.getDate());
            saleHis.setSaleManId(saleManCompleter.getSaleMan());
            saleHis.setVouStatusId(vouCompleter.getVouStatus());
            saleHis.setRemark(txtRemark.getText());
            saleHis.setDiscP(NumberUtil.getDouble(txtDiscP.getText()));
            saleHis.setDiscount(NumberUtil.getDouble(txtVouDiscount.getText()));
            saleHis.setTaxP(NumberUtil.getDouble(txtTaxP.getText()));
            saleHis.setTaxAmt(NumberUtil.getDouble(txtTax.getText()));
            saleHis.setGrandTotal(NumberUtil.getDouble(txtGrandTotal.getText()));
            saleHis.setPaid(NumberUtil.getDouble(txtVouPaid.getText()));
            saleHis.setVouBalance(NumberUtil.getDouble(txtVouBalance.getText()));
            saleHis.setCurrency(currAutoCompleter.getCurrency());
            saleHis.setDeleted(Util1.getNullTo(saleHis.getDeleted()));
            saleHis.setTraderId(traderAutoCompleter.getTrader());
            saleHis.setAddress(txtAddress.getText());
            saleHis.setOrderCode(orderCode);
            saleHis.setRegion(region);
            if (lblStatus.getText().equals("NEW")) {
                saleHis.setSaleDate(txtSaleDate.getDate());
                saleHis.setCreatedBy(Global.loginUser);
                saleHis.setSession(Global.sessionId);
            } else {
                Date tmpDate = txtSaleDate.getDate();
                if (!Util1.isSameDate(tmpDate, saleHis.getSaleDate())) {
                    saleHis.setSaleDate(txtSaleDate.getDate());
                }
                saleHis.setUpdatedBy(Global.loginUser);
                saleHis.setUpdatedDate(Util1.getTodayDate());
            }
            //cal total
            Float vouTotal = 0.0f;
            vouTotal = saleTableModel.getListSaleDetail().stream().filter(sd -> (sd.getStock() != null)).map(sd -> sd.getAmount()).reduce(vouTotal, (accumulator, _item) -> accumulator + _item);
            saleHis.setVouTotal(vouTotal.doubleValue());
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
        if (lblStatus.getText().equals("EDIT")) {
            int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                    "Are you sure to delete?", "Sale item delete", JOptionPane.YES_NO_OPTION);
            if (yes_no == 0) {
                String vouNo = txtVouNo.getText();
                try {
                    saleHisService.delete(vouNo);
                    clear();
                } catch (Exception ex) {
                    LOGGER.error("Delete Sale Voucher :" + ex.getMessage());
                }
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
                    calculateTotalAmount();
                }
            }
        }
    };

    private void calculateTotalAmount() {
        double totalVouBalance;
        Double totalAmount = 0.0;
        listDetail = saleTableModel.getListSaleDetail();
        totalAmount = listDetail.stream().map(sdh -> NumberUtil.NZero(sdh.getAmount())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);
        //cal discAmt
        if (Util1.getDouble(txtDiscP.getValue()) > 0) {
            double discp = Util1.getDouble(txtDiscP.getValue());
            double discountAmt = (totalAmount * (discp / 100));
            txtVouDiscount.setValue(discountAmt);
        }

        //calculate taxAmt
        if (Util1.getDouble(txtTaxP.getValue()) > 0) {
            double taxp = Util1.getDouble(txtTaxP.getValue());
            double afterDiscountAmt = totalAmount - Util1.getDouble(txtVouDiscount.getValue());
            double totalTax = (afterDiscountAmt * taxp) / 100;
            txtTax.setValue(totalTax);
        }

        txtGrandTotal.setValue(Util1.getDouble(txtVouTotal.getValue())
                + Util1.getDouble(txtTax.getValue())
                - Util1.getDouble(txtVouDiscount.getValue()));

        totalVouBalance = Util1.getDouble(txtGrandTotal.getValue()) - (Util1.getDouble(txtVouPaid.getValue()));
        txtVouBalance.setValue(totalVouBalance);
    }

    public void historySale() {
        vouSearchDialog.setIconImage(historyIcon);
        vouSearchDialog.initMain();
        vouSearchDialog.setSize(Global.width - 500, Global.height - 300);
        vouSearchDialog.setLocationRelativeTo(null);
        vouSearchDialog.setVisible(true);
    }

    public void setSaleVoucher(SaleHis sh, List<SaleHisDetail> listSaleDetail) {
        if (!lblStatus.getText().equals("NEW")) {
            clear();
        }
        if (sh != null) {
            if (sh.getDeleted()) {
                lblStatus.setText("DELETED");
            } else {
                lblStatus.setText("EDIT");
            }
            txtVouNo.setText(sh.getVouNo());
            vouCompleter.setVouStatus(sh.getVouStatusId());
            traderAutoCompleter.setTrader((Customer) sh.getTraderId());
            if (sh.getSaleManId() != null) {
                saleManCompleter.setSaleMan(sh.getSaleManId());
            }
            txtAddress.setText(sh.getAddress());
            txtDueDate.setDate(sh.getCreditTerm());
            currAutoCompleter.setCurrency(sh.getCurrency());
            txtRemark.setText(sh.getRemark());
            txtSaleDate.setDate(sh.getSaleDate());
            txtVouTotal.setValue(Util1.getDouble(sh.getVouTotal()));
            txtDiscP.setValue(Util1.getDouble(sh.getDiscP()));
            txtVouDiscount.setValue(Util1.getDouble(sh.getDiscount()));
            txtTaxP.setValue(Util1.getDouble(sh.getTaxP()));
            txtTax.setValue(Util1.getDouble(sh.getTaxAmt()));
            txtGrandTotal.setValue(Util1.getDouble(sh.getGrandTotal()));
            txtVouPaid.setValue(Util1.getDouble(sh.getPaid()));
            txtVouBalance.setValue(Util1.getDouble(sh.getVouBalance()));
            txtTotalItem.setText(Integer.toString(listSaleDetail.size() - 1));
            saleHis.setCreatedBy(sh.getCreatedBy());
            saleHis.setUpdatedBy(sh.getUpdatedBy());
            saleTableModel.setListDetail(listSaleDetail);
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

    private void setAllLocation() {
        List<SaleHisDetail> listSaleDetail = saleTableModel.getListSaleDetail();
        if (listSaleDetail != null) {
            listSaleDetail.forEach(sd -> {
                sd.setLocation(locationAutoCompleter.getLocation());
            });
        }
        saleTableModel.setListDetail(listSaleDetail);
    }

    private void printSaveVoucher() {
        boolean save;
        if (lblStatus.getText().equals("EDIT")) {
            voucherNo = txtVouNo.getText();
            save = true;
        } else {
            voucherNo = txtVouNo.getText();
            save = saveSale();
        }
        if (save) {
            String compName = Global.sysProperties.get("system.report.company");
            String address = Global.sysProperties.get("system.report.address");
            String phone = Global.sysProperties.get("system.report.phone");
            String reportPath = Global.sysProperties.get("system.report.path");
            String fontPath = Global.sysProperties.get("system.font.path");
            reportPath = reportPath + "\\SaleVoucherByAddress";
            Map<String, Object> parameters = new HashMap();
            parameters.put("company_name", compName);
            parameters.put("address", address);
            parameters.put("phone", phone);
            parameters.put("vou_no", voucherNo);
            reportService.reportViewer(reportPath, fontPath, fontPath, parameters);
        }
    }

    private void orderSearch() {
        orderSearchDialog.setObserver(this);
        orderSearchDialog.initMain();
        orderSearchDialog.setIconImage(historyIcon);
        orderSearchDialog.setSize(Global.width - 500, Global.height - 300);
        orderSearchDialog.setLocationRelativeTo(null);
        orderSearchDialog.setVisible(true);

    }

    private void searchOrder(Order order) {
        traderAutoCompleter.setTrader((Customer) order.getTrader());
        txtRemark.setText(order.getDesp());
        txtAddress.setText(order.getOrderAddres());
        orderCode = order.getOrderCode();
        region = order.getTrader().getRegion();
        lblStatus.setText("NEW");
        List<OrderDetail> listOD = detailService.search(order.getOrderCode());
        if (!listOD.isEmpty()) {
            saleTableModel.clear();
            listOD.stream().map(od -> {
                SaleHisDetail sd = new SaleHisDetail();
                sd.setStock(od.getStock());
                sd.setQuantity(od.getQty());
                sd.setPrice(od.getPrice());
                sd.setAmount(od.getAmount());
                sd.setStdWeight(od.getStock().getSaleWeight());
                sd.setSaleUnit(od.getStock().getSaleUnit());
                return sd;
            }).forEachOrdered(sd -> {
                saleTableModel.addSale(sd);
            });
            calculateTotalAmount();
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
        jLabel22 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
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
        txtTax = new javax.swing.JFormattedTextField();
        txtGrandTotal = new javax.swing.JFormattedTextField();
        txtVouPaid = new javax.swing.JFormattedTextField();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtDiscP = new javax.swing.JFormattedTextField();
        txtTaxP = new javax.swing.JFormattedTextField();
        jPanel4 = new javax.swing.JPanel();
        btnSaleOutStand = new javax.swing.JButton();
        btnOrderList = new javax.swing.JButton();
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
        txtVouNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
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
        txtCurrency.setDisabledTextColor(new java.awt.Color(0, 0, 0));
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

        jLabel22.setFont(Global.lableFont);
        jLabel22.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtRemark"); // NOI18N
        txtLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLocationFocusGained(evt);
            }
        });

        jLabel23.setFont(Global.lableFont);
        jLabel23.setText("Addresss");

        txtAddress.setFont(Global.textFont);
        txtAddress.setName("txtRemark"); // NOI18N
        txtAddress.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAddressFocusGained(evt);
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
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAddress)
                            .addComponent(txtLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE))
                        .addGap(1, 1, 1)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtVouNo, txtVouStatus});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel21, jLabel22, jLabel23});

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
                    .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtSaleman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel23)
                        .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        txtTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTax.setFont(Global.amtFont);
        txtTax.setName("txtTax"); // NOI18N
        txtTax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTaxFocusGained(evt);
            }
        });
        txtTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTaxActionPerformed(evt);
            }
        });

        txtGrandTotal.setEditable(false);
        txtGrandTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGrandTotal.setFont(Global.amtFont);

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

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Vou Balance :");

        jLabel15.setText("%");

        txtDiscP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscP.setFont(Global.amtFont);
        txtDiscP.setName("txtDiscP"); // NOI18N
        txtDiscP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscPFocusLost(evt);
            }
        });
        txtDiscP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscPActionPerformed(evt);
            }
        });

        txtTaxP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTaxP.setFont(Global.amtFont);
        txtTaxP.setName("txtTaxP"); // NOI18N
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
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtGrandTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
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
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
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
                            .addComponent(txtTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)
                            .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        btnOrderList.setBackground(ColorUtil.btnEdit);
        btnOrderList.setFont(Global.lableFont);
        btnOrderList.setForeground(ColorUtil.foreground);
        btnOrderList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search_property_16px.png"))); // NOI18N
        btnOrderList.setText("Order List");
        btnOrderList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrderListActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSaleOutStand)
                .addGap(18, 18, 18)
                .addComponent(btnOrderList)
                .addContainerGap(114, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaleOutStand)
                    .addComponent(btnOrderList))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblSale.setFont(Global.textFont);
        tblSale.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
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

    private void txtLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLocationFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLocationFocusGained

    private void txtDiscPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscPActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtDiscPActionPerformed

    private void txtVouPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouPaidActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouPaidActionPerformed

    private void txtVouPaidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouPaidFocusLost
        // TODO add your handling code here:
        calculateTotalAmount();

    }//GEN-LAST:event_txtVouPaidFocusLost

    private void txtDiscPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscPFocusLost
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtDiscPFocusLost

    private void txtVouDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouDiscountActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouDiscountActionPerformed

    private void txtVouDiscountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouDiscountFocusLost
        // TODO add your handling code here:
        calculateTotalAmount();

    }//GEN-LAST:event_txtVouDiscountFocusLost

    private void txtTaxPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxPActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();

    }//GEN-LAST:event_txtTaxPActionPerformed

    private void txtTaxPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxPFocusLost
        // TODO add your handling code here:
        calculateTotalAmount();

    }//GEN-LAST:event_txtTaxPFocusLost

    private void txtTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTaxActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtTaxActionPerformed

    private void txtTaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxFocusGained
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtTaxFocusGained

    private void btnOrderListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrderListActionPerformed
        // TODO add your handling code here:
        orderSearch();

    }//GEN-LAST:event_btnOrderListActionPerformed

    private void txtAddressFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAddressFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddressFocusGained
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
                    if (cus.getTraderType() != null) {
                        saleTableModel.setCusType(cus.getTraderType().getDescription());
                        txtCus.setText(cus.getTraderName());
                        if (cus.getTraderType() != null) {
                            saleTableModel.setCusType(cus.getTraderType().getDescription());
                        } else {
                            saleTableModel.setCusType("N");
                        }
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("selected CustomerList : " + selectObj.toString() + " - " + ex.getMessage());
            }
            break;
            case "SALE-TOTAL":
                calculateTotalAmount();
                break;
            case "Location":
                setAllLocation();
                break;
            case "ORDER":
                Order od = (Order) selectObj;
                searchOrder(od);
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
                    if (sourceObj != null) {
                        String date = ((JTextFieldDateEditor) sourceObj).getText();
                        if (date.length() == 8) {
                            String toFormatDate = Util1.toFormatDate(date);
                            txtSaleDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                        }
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
                    if (sourceObj != null) {
                        String date = ((JTextFieldDateEditor) sourceObj).getText();
                        if (date.length() == 8) {
                            String toFormatDate = Util1.toFormatDate(date);
                            txtDueDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                        }
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
                    txtTaxP.requestFocus();
                }
                break;
            case "txtTaxP":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtVouBalance.requestFocus();
                }
                break;
            case "txtVouDiscount":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtTaxP.requestFocus();
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
    private javax.swing.JButton btnOrderList;
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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
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
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private javax.swing.JFormattedTextField txtDiscP;
    private com.toedter.calendar.JDateChooser txtDueDate;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRecNo;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtSaleDate;
    private javax.swing.JTextField txtSaleman;
    private javax.swing.JFormattedTextField txtTax;
    private javax.swing.JFormattedTextField txtTaxP;
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
        printSaveVoucher();
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
