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
import com.cv.accountswing.ui.editor.DepartmentCellEditor;
import com.cv.accountswing.ui.editor.TraderAutoCompleter;
import com.cv.accountswing.util.NumberUtil;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Order;
import com.cv.inv.entity.OrderDetail;
import com.cv.inv.entity.SaleHisDetail;
import com.cv.inv.entity.SaleHis;
import com.cv.inv.entry.common.SaleEntryTableModel;
import com.cv.inv.entry.common.StockBalanceTableModel;
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
import java.io.File;
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
    @Autowired
    private StockBalanceTableModel stockBalanceTableModel;
    private VouStatusAutoCompleter vouCompleter;
    private CurrencyAutoCompleter currAutoCompleter;
    private CustomerAutoCompleter customerAutoCompleter;
    private TraderAutoCompleter traderAutoCompleter;
    private SaleManAutoCompleter saleManCompleter;
    private LocationAutoCompleter locationAutoCompleter;
    private LoadingObserver loadingObserver;
    private SelectionObserver selectionObserver;
    private GenVouNoImpl vouEngine = null;
    private SaleHis saleHis = new SaleHis();
    private boolean isShown = false;
    private String voucherNo = "-";
    private Region region;
    private String orderCode;
    private String stockName = "Stock Name";

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
        initStockBalanceTable();
        assignDefaultValue();
        genVouNo();
    }

    private void initSaleTable() {
        tblSale.setModel(saleTableModel);
        saleTableModel.setBtnProgress(btnProgress);
        saleTableModel.setLblStockName(lblStockName);
        saleTableModel.setParent(tblSale);
        saleTableModel.setLocationAutoCompleter(locationAutoCompleter);
        saleTableModel.addNewRow();
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
        tblSale.getColumnModel().getColumn(2).setCellEditor(new DepartmentCellEditor(false));
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

    }

    private void requesTable() {

        tblSale.changeSelection(0, 0, false, false);
        tblSale.requestFocus();
    }

    private void initCombo() {
        if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
            traderAutoCompleter = new TraderAutoCompleter(txtCus, Global.listTrader, null, false);
        } else {
            customerAutoCompleter = new CustomerAutoCompleter(txtCus, Global.listCustomer, null);
        }
        currAutoCompleter = new CurrencyAutoCompleter(txtCurrency, Global.listCurrency, null);
        vouCompleter = new VouStatusAutoCompleter(txtVouStatus, Global.listVou, null);
        saleManCompleter = new SaleManAutoCompleter(txtSaleman, Global.listSaleMan, null);
        locationAutoCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        locationAutoCompleter.setLocation(Global.defaultLocation);
        locationAutoCompleter.setSelectionObserver(this);
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
        txtVouDiscount.setText("0.0");
        txtTax.setText("0.0");
        txtGrandTotal.setValue(0.00);
        txtVouPaid.setText("0.0");
        txtVouBalance.setValue(0.00);
        txtTaxP.setText("0.0");
        txtDiscP.setText("0.0");
        txtTotalItem.setText("0");
        txtRecNo.setText("0");
        txtLocation.setText(null);
    }

    private void initTextBoxFormat() {
        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
            //txtVouDiscount.setFormatterFactory(NumberUtil.getDecimalFormat());
            //txtTax.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtGrandTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
            //txtVouPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
            txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
            //txtTaxP.setFormatterFactory(NumberUtil.getDecimalFormat());
            //txtDiscP.setFormatterFactory(NumberUtil.getDecimalFormat());
        } catch (ParseException ex) {
            LOGGER.error("setFormatterFactory : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());

        }
    }

    private void initStockBalanceTable() {
        String isStock = Global.sysProperties.get("system.sale.stock.balance");
        if (Util1.isNull(isStock, "0").equals("0")) {
            panelStockCal.setVisible(false);
        } else {
            tblStockBalance.setModel(stockBalanceTableModel);
            tblStockBalance.getColumnModel().getColumn(0).setPreferredWidth(100);//Unit
            tblStockBalance.getColumnModel().getColumn(1).setPreferredWidth(140);//Cost Price
            tblStockBalance.getTableHeader().setFont(Global.textFont);
            tblStockBalance.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
            tblStockBalance.getTableHeader().setForeground(ColorUtil.foreground);
            tblStockBalance.getTableHeader().setBackground(ColorUtil.btnEdit);
            tblStockBalance.getTableHeader().setForeground(ColorUtil.foreground);
            tblStockBalance.setDefaultRenderer(Object.class, new TableCellRender());
            tblStockBalance.setDefaultRenderer(Float.class, new TableCellRender());
            tblStockBalance.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }

    private void assignDefaultValue() {
        try {
            txtSaleDate.setDate(Util1.getTodayDate());
            currAutoCompleter.setCurrency(Global.defalutCurrency);
            vouCompleter.setVouStatus(Global.defaultVouStatus);
            saleManCompleter.setSaleMan(Global.defaultSaleMan);
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
        stockBalanceTableModel.clearList();
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
                if (saleHis.getTraderId() != null) {
                    List<String> deleteList = saleTableModel.getDelList();
                    String vouStatus = lblStatus.getText();
                    saleDetailService.save(saleHis, saleTableModel.getListSaleDetail(), vouStatus, deleteList);
                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.updateVouNo();
                    }
                    clear();
                    genVouNo();
                    status = true;
                } else {
                    JOptionPane.showMessageDialog(Global.parentForm, "Invalid Customer.");
                }
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
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Amount.",
                    "No Sale Record.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocation.requestFocus();
        } else {
            saleHis.setVouNo(txtVouNo.getText());
            saleHis.setCreditTerm(txtDueDate.getDate());
            saleHis.setSaleMan(saleManCompleter.getSaleMan());
            saleHis.setVouStatus(vouCompleter.getVouStatus());
            saleHis.setRemark(txtRemark.getText());
            saleHis.setDiscP(Util1.getFloat(txtDiscP.getText()));
            saleHis.setDiscount(Util1.getFloat(txtVouDiscount.getText()));
            saleHis.setTaxP(Util1.getFloat(txtTaxP.getText()));
            saleHis.setTaxAmt(Util1.getFloat(txtTax.getText()));
            saleHis.setGrandTotal(Util1.getFloat(txtGrandTotal.getValue()));
            saleHis.setPaid(Util1.getFloat(txtVouPaid.getText()));
            saleHis.setVouBalance(Util1.getFloat(txtVouBalance.getValue()));
            saleHis.setCurrency(currAutoCompleter.getCurrency());
            saleHis.setDeleted(Util1.getNullTo(saleHis.getDeleted()));
            saleHis.setAddress(txtAddress.getText());
            saleHis.setOrderCode(orderCode);
            saleHis.setRegion(region);
            saleHis.setLocation(locationAutoCompleter.getLocation());
            saleHis.setSaleDate(txtSaleDate.getDate());
            if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
                saleHis.setTraderId(traderAutoCompleter.getTrader());
            } else {
                saleHis.setTraderId(customerAutoCompleter.getTrader());
            }
            if (lblStatus.getText().equals("NEW")) {
                saleHis.setCreatedBy(Global.loginUser);
                saleHis.setSession(Global.sessionId);
                saleHis.setMacId(Global.machineId);
            } else {
                saleHis.setUpdatedBy(Global.loginUser);
                saleHis.setUpdatedDate(Util1.getTodayDate());
            }
            //cal total
            Float vouTotal = 0.0f;
            vouTotal = saleTableModel.getListSaleDetail().stream().filter(sd -> (sd.getStock() != null)).map(sd -> sd.getAmount()).reduce(vouTotal, (accumulator, _item) -> accumulator + _item);
            saleHis.setVouTotal(vouTotal);
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
        float totalVouBalance;
        float totalAmount = 0.0f;
        listDetail = saleTableModel.getListSaleDetail();
        totalAmount = listDetail.stream().map(sdh -> Util1.getFloat(sdh.getAmount())).reduce(totalAmount, (accumulator, _item) -> accumulator + _item);
        txtVouTotal.setValue(totalAmount);
        //cal discAmt
        if (Util1.getFloat(txtDiscP.getText()) > 0) {
            float discp = Util1.getFloat(txtDiscP.getText());
            float discountAmt = (totalAmount * (discp / 100));
            txtVouDiscount.setText(Util1.getString(discountAmt));
        }

        //calculate taxAmt
        if (Util1.getFloat(txtTaxP.getText()) > 0) {
            float taxp = Util1.getFloat(txtTaxP.getText());
            float afterDiscountAmt = totalAmount - Util1.getFloat(txtVouDiscount.getText());
            float totalTax = (afterDiscountAmt * taxp) / 100;
            txtTax.setText(Util1.getString(totalTax));
        }

        txtGrandTotal.setValue(Util1.getDouble(txtVouTotal.getValue())
                + Util1.getDouble(txtTax.getText())
                - Util1.getDouble(txtVouDiscount.getText()));

        totalVouBalance = Util1.getFloat(txtGrandTotal.getValue()) - (Util1.getFloat(txtVouPaid.getText()));
        txtVouBalance.setValue(totalVouBalance);
    }

    public void historySale() {
        vouSearchDialog.setIconImage(historyIcon);
        vouSearchDialog.initMain();
        vouSearchDialog.setSize(Global.width - 300, Global.height - 300);
        vouSearchDialog.setLocationRelativeTo(null);
        vouSearchDialog.setVisible(true);
    }

    public void setSaleVoucher(SaleHis sh, List<SaleHisDetail> listSaleDetail) {
        if (sh != null) {
            if (sh.getDeleted()) {
                lblStatus.setText("DELETED");
            } else {
                lblStatus.setText("EDIT");
            }
            saleHis = sh;
            txtVouNo.setText(saleHis.getVouNo());
            vouCompleter.setVouStatus(saleHis.getVouStatus());
            saleManCompleter.setSaleMan(saleHis.getSaleMan());
            txtAddress.setText(saleHis.getAddress());
            txtDueDate.setDate(saleHis.getCreditTerm());
            currAutoCompleter.setCurrency(saleHis.getCurrency());
            txtRemark.setText(saleHis.getRemark());
            txtSaleDate.setDate(saleHis.getSaleDate());
            txtVouTotal.setValue(Util1.getFloat(saleHis.getVouTotal()));
            txtDiscP.setText(Util1.getString(saleHis.getDiscP()));
            txtVouDiscount.setText(Util1.getString(saleHis.getDiscount()));
            txtTaxP.setText(Util1.getString(saleHis.getTaxP()));
            txtTax.setText(Util1.getString(saleHis.getTaxAmt()));
            txtGrandTotal.setValue(Util1.getFloat(saleHis.getGrandTotal()));
            txtVouPaid.setText(Util1.getString(saleHis.getPaid()));
            txtVouBalance.setValue(Util1.getFloat(saleHis.getVouBalance()));
            txtTotalItem.setText(Integer.toString(listSaleDetail.size() - 1));
            locationAutoCompleter.setLocation(saleHis.getLocation());
            if (Util1.isNull(Global.sysProperties.get("system.customer.supplier"), "-").equals("1")) {
                traderAutoCompleter.setTrader(saleHis.getTraderId());
            } else {
                customerAutoCompleter.setTrader((Customer) saleHis.getTraderId());
            }
            saleTableModel.setListDetail(listSaleDetail);
            saleTableModel.addNewRow();
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
            String reportName = Global.sysProperties.get("system.sale.report");
            if (reportName != null) {
                String compName = Global.sysProperties.get("system.report.company");
                String address = Global.sysProperties.get("system.report.address");
                String phone = Global.sysProperties.get("system.report.phone");
                String reportPath = Global.sysProperties.get("system.report.path");
                String fontPath = Global.sysProperties.get("system.font.path");
                reportPath = reportPath + File.separator + reportName;
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

    private void orderSearch() {
        orderSearchDialog.setObserver(this);
        orderSearchDialog.initMain();
        orderSearchDialog.setIconImage(historyIcon);
        orderSearchDialog.setSize(Global.width - 300, Global.height - 300);
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

    private void calStockBalance(boolean refresh) {
        if (tblSale.getSelectedRow() >= 0) {
            int row = tblSale.convertRowIndexToModel(tblSale.getSelectedRow());
            saleTableModel.calStockBalance(row, refresh);
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
        btnSaleOutStand = new javax.swing.JButton();
        btnOrderList = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtGrandTotal = new javax.swing.JFormattedTextField();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtVouPaid = new javax.swing.JTextField();
        txtDiscP = new javax.swing.JTextField();
        txtTaxP = new javax.swing.JTextField();
        txtVouDiscount = new javax.swing.JTextField();
        txtTax = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSale = new javax.swing.JTable();
        panelStockCal = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStockBalance = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        lblStockName = new javax.swing.JLabel();
        btnProgress = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, java.awt.Color.lightGray));

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
                    .addComponent(txtSaleDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCurrency, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtRemark))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAddress)
                            .addComponent(txtLocation))
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
        btnOrderList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/list_18px.png"))); // NOI18N
        btnOrderList.setText("Order List");
        btnOrderList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrderListActionPerformed(evt);
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
                            .addComponent(chkPrintOption)
                            .addComponent(chkVouComp))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtTotalItem, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtRecNo, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                                .addComponent(btnSaleOutStand))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(btnOrderList)))
                .addContainerGap(66, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnOrderList, btnSaleOutStand});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnOrderList)
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRecNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSaleOutStand))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkPrintOption)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(chkVouComp))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, java.awt.Color.lightGray));

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

        txtGrandTotal.setEditable(false);
        txtGrandTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGrandTotal.setFont(Global.amtFont);

        txtVouBalance.setEditable(false);
        txtVouBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouBalance.setFont(Global.amtFont);
        txtVouBalance.setName("txtVouBalance"); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Vou Balance :");

        jLabel15.setText("%");

        txtVouPaid.setFont(Global.amtFont);
        txtVouPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouPaid.setName("txtVouPaid"); // NOI18N
        txtVouPaid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouPaidFocusGained(evt);
            }
        });
        txtVouPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouPaidActionPerformed(evt);
            }
        });

        txtDiscP.setFont(Global.amtFont);
        txtDiscP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscP.setName("txtDiscP"); // NOI18N
        txtDiscP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscPFocusGained(evt);
            }
        });

        txtTaxP.setFont(Global.amtFont);
        txtTaxP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTaxP.setName("txtTaxP"); // NOI18N
        txtTaxP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTaxPFocusGained(evt);
            }
        });

        txtVouDiscount.setFont(Global.lableFont);
        txtVouDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVouDiscount.setName("txtVouDiscount"); // NOI18N
        txtVouDiscount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVouDiscountFocusGained(evt);
            }
        });

        txtTax.setFont(Global.amtFont);
        txtTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTax.setName("txtTax"); // NOI18N
        txtTax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTaxFocusGained(evt);
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
                    .addComponent(txtGrandTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                    .addComponent(txtVouBalance)
                    .addComponent(txtVouPaid))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel14)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDiscP, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .addComponent(txtTaxP))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVouDiscount, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .addComponent(txtTax)))
                    .addComponent(txtVouTotal))
                .addContainerGap())
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
                            .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtVouDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jLabel15)
                            .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
        tblSale.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSaleMouseClicked(evt);
            }
        });
        tblSale.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblSaleKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblSale);

        tblStockBalance.setFont(Global.textFont);
        tblStockBalance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        tblStockBalance.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblStockBalance);

        jPanel4.setBackground(ColorUtil.btnEdit);
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, java.awt.Color.lightGray));

        lblStockName.setFont(Global.textFont);
        lblStockName.setForeground(ColorUtil.foreground);
        lblStockName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStockName.setText("Stock Name");

        btnProgress.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/synchronize_16px.png"))); // NOI18N
        btnProgress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProgressActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStockName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProgress)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStockName)
                    .addComponent(btnProgress))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelStockCalLayout = new javax.swing.GroupLayout(panelStockCal);
        panelStockCal.setLayout(panelStockCalLayout);
        panelStockCalLayout.setHorizontalGroup(
            panelStockCalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStockCalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelStockCalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelStockCalLayout.setVerticalGroup(
            panelStockCalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelStockCalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(0, 0, 0)
                        .addComponent(panelStockCal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addComponent(panelStockCal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        LOGGER.info("Sale Entry Main");
        mainFrame.setControl(this);
        if (saleTableModel.getListSaleDetail().size() > 0) {
            requesTable();
        } else {
            txtCus.requestFocus();
        }

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

    private void btnOrderListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrderListActionPerformed
        // TODO add your handling code here:
        orderSearch();

    }//GEN-LAST:event_btnOrderListActionPerformed

    private void txtAddressFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAddressFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddressFocusGained

    private void tblSaleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSaleMouseClicked
        // TODO add your handling code here:
        calStockBalance(false);
    }//GEN-LAST:event_tblSaleMouseClicked

    private void tblSaleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSaleKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DOWN || evt.getKeyCode() == KeyEvent.VK_UP) {
            calStockBalance(false);
        }
    }//GEN-LAST:event_tblSaleKeyReleased

    private void btnProgressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProgressActionPerformed
        // TODO add your handling code here:
        calStockBalance(true);
    }//GEN-LAST:event_btnProgressActionPerformed

    private void txtVouPaidFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouPaidFocusGained
        // TODO add your handling code here:
        txtVouPaid.selectAll();
    }//GEN-LAST:event_txtVouPaidFocusGained

    private void txtVouPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouPaidActionPerformed
        // TODO add your handling code here:
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouPaidActionPerformed

    private void txtDiscPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscPFocusGained
        // TODO add your handling code here:
        txtDiscP.selectAll();
    }//GEN-LAST:event_txtDiscPFocusGained

    private void txtTaxPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxPFocusGained
        // TODO add your handling code here:
        txtTax.selectAll();
    }//GEN-LAST:event_txtTaxPFocusGained

    private void txtVouDiscountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVouDiscountFocusGained
        // TODO add your handling code here:
        txtVouDiscount.selectAll();
    }//GEN-LAST:event_txtVouDiscountFocusGained

    private void txtTaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTaxFocusGained
        // TODO add your handling code here:
        txtTax.selectAll();
    }//GEN-LAST:event_txtTaxFocusGained
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
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRemark.requestFocus();
                } else {
                    txtVouStatus.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtCus":

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

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtSaleDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtRemark":

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtSaleDate.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtSaleDate":

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    //txtRemark.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    //txtCus.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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
    private javax.swing.JButton btnProgress;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStockName;
    private javax.swing.JPanel panelStockCal;
    private javax.swing.JTable tblSale;
    private javax.swing.JTable tblStockBalance;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtCurrency;
    private javax.swing.JTextField txtCus;
    private javax.swing.JTextField txtDiscP;
    private com.toedter.calendar.JDateChooser txtDueDate;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRecNo;
    private javax.swing.JTextField txtRemark;
    private com.toedter.calendar.JDateChooser txtSaleDate;
    private javax.swing.JTextField txtSaleman;
    private javax.swing.JTextField txtTax;
    private javax.swing.JTextField txtTaxP;
    private javax.swing.JTextField txtTotalItem;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JTextField txtVouDiscount;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JTextField txtVouPaid;
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
