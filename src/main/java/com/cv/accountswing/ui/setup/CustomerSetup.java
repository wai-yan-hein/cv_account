/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.FilterObserver;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Customer;
import com.cv.accountswing.entity.Region;
import com.cv.accountswing.entity.TraderType;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.CustomerService;
import com.cv.accountswing.service.RegionService;
import com.cv.accountswing.service.TraderTypeService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.setup.common.CustomerTabelModel;
import com.cv.accountswing.util.BindingUtil;
import com.cv.accountswing.util.Util1;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class CustomerSetup extends javax.swing.JPanel implements KeyListener, PanelControl, FilterObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerSetup.class);
    private int selectRow = -1;
    private TableRowSorter<TableModel> sorter;
    private Customer customer;
    @Autowired
    private CustomerTabelModel customerTabelModel;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private COAService coaService;
    @Autowired
    private TraderTypeService traderTypeService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private CustomerImportDialog importDialog;
    private LoadingObserver loadingObserver;
    private boolean isShown = false;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    /**
     * Creates new form CustomerSetup
     */
    public CustomerSetup() {
        initComponents();
        initKeyListener();
    }

    private void initMain() {
        loadingObserver.load(this.getName(), "Start");
        initCombo();
        initTable();
        isShown = true;
    }

    private void initCombo() {

        List<ChartOfAccount> listCOA = coaService.search("-", "-", Global.compId.toString(), "2", "-", "-", "-");
        BindingUtil.BindComboFilter(cboAccount, listCOA, null, true, false);
        BindingUtil.BindComboFilter(cboPriceType, traderTypeService.findAll(), null, true, false);
        BindingUtil.BindComboFilter(cboRegion, regionService.search("-", "-", Global.compId.toString(), "-"), null, true, false);
    }

    private void initTable() {
        tblCustomer.setModel(customerTabelModel);
        tblCustomer.getTableHeader().setFont(Global.textFont);
        tblCustomer.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblCustomer.getTableHeader().setForeground(ColorUtil.foreground);
        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(40);// Code
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(320);// Name
        tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(40);// Active   
        tblCustomer.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCustomer.setDefaultRenderer(Object.class, new TableCellRender());
        tblCustomer.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblCustomer.getSelectedRow() >= 0) {
                    selectRow = tblCustomer.convertRowIndexToModel(tblCustomer.getSelectedRow());
                    setCustomer(customerTabelModel.getCustomer(selectRow));
                }

            }
        });
        sorter = new TableRowSorter(tblCustomer.getModel());
        tblCustomer.setRowSorter(sorter);
        searchCustomer();

    }

    private void searchCustomer() {
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            try {
                List<Customer> listcustomer = customerService.search("-", "-", "-", "-", "-");
                customerTabelModel.setListCustomer(listcustomer);
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception e) {
                LOGGER.error("Search Customer :" + e.getMessage());
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Search Customer ", JOptionPane.ERROR_MESSAGE);
                loadingObserver.load(this.getName(), "Stop");
            }
        });

    }

    private void setCustomer(Customer cus) {
        txtCusCode.setText(cus.getTraderId());
        txtConPerson.setText(cus.getContactPerson());
        txtCusName.setText(cus.getTraderName());
        txtCusEmail.setText(cus.getEmail());
        txtCusPhone.setText(cus.getPhone());
        cboRegion.setSelectedItem(cus.getRegion());
        cboPriceType.setSelectedItem(cus.getTraderType());
        cboAccount.setSelectedItem(cus.getAccount());
        txtCusAddress.setText(cus.getAddress());
        chkActive.setSelected(cus.getActive());
        txtCreditLimit.setText(Util1.getString(cus.getCreditLimit()));
        txtCreditTerm.setText(Util1.getString(cus.getCreditDays()));
        lblStatus.setText("EDIT");

    }

    private boolean isValidEntry() {
        boolean status;
        if (txtCusName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Customer Name can't be empty");
            status = false;
        } else {
            customer = new Customer();
            if (lblStatus.getText().equals("EDIT")) {
                Customer cus = customerTabelModel.getCustomer(selectRow);
                customer.setId(cus.getId());
            }
            customer.setTraderId(txtCusCode.getText());
            customer.setTraderName(txtCusName.getText());
            customer.setContactPerson(txtConPerson.getText());
            customer.setPhone(txtCusPhone.getText());
            customer.setEmail(txtCusEmail.getText());
            customer.setAddress(txtCusAddress.getText());

            if (cboRegion.getSelectedItem() instanceof Region) {
                customer.setRegion((Region) cboRegion.getSelectedItem());
            }
            if (cboPriceType.getSelectedItem() instanceof TraderType) {
                customer.setTraderType((TraderType) cboPriceType.getSelectedItem());
            }
            if (cboAccount.getSelectedItem() instanceof ChartOfAccount) {
                customer.setAccount((ChartOfAccount) cboAccount.getSelectedItem());
            }
            customer.setActive(chkActive.isSelected());
            customer.setCompCode(Global.compId);
            customer.setUpdatedDate(Util1.getTodayDate());
            customer.setCreditLimit(Util1.getInteger(txtCreditLimit.getText()));
            customer.setCreditDays(Util1.getInteger(txtCreditTerm.getText()));
            //customer.setAppShortName(TOOL_TIP_TEXT_KEY);
            //customer.setAppTraderCode(TOOL_TIP_TEXT_KEY);

            status = true;
        }
        return status;
    }

    private void saveCustomer() {
        if (isValidEntry()) {
            String traderCodeLength = Global.sysProperties.get("system.trader.id.length");
            Customer save = customerService.save(customer, traderCodeLength);
            if (save.getTraderId() != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if (lblStatus.getText().equals("NEW")) {
                    customerTabelModel.addCustomer(customer);
                    Global.listTrader.add(customer);
                } else {
                    customerTabelModel.setCustomer(selectRow, customer);
                }
                clear();
            }
        }
    }

    public void clear() {
        txtCusCode.setText(null);
        txtCusName.setText(null);
        txtCusEmail.setText(null);
        txtCusPhone.setText(null);
        cboAccount.setSelectedItem(null);
        cboPriceType.setSelectedItem(null);
        cboRegion.setSelectedItem(null);
        txtCusAddress.setText(null);
        chkActive.setSelected(Boolean.TRUE);
        txtCreditLimit.setText(null);
        lblStatus.setText("NEW");
        txtCusName.requestFocus();
        txtConPerson.setText(null);
        txtCreditTerm.setText(null);
    }

    private void setTableFilter(String filter) {
        sorter.setRowFilter(RowFilter.regexFilter(filter));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelEntry = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCusCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCusName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCusPhone = new javax.swing.JTextField();
        txtCusEmail = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCusAddress = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCreditLimit = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtConPerson = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        cboAccount = new javax.swing.JComboBox<>();
        cboRegion = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        cboPriceType = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtCreditTerm = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Code/ID");

        txtCusCode.setEditable(false);
        txtCusCode.setFont(Global.textFont);
        txtCusCode.setName("txtCusCode"); // NOI18N
        txtCusCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCusCodeKeyReleased(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        txtCusName.setFont(Global.textFont);
        txtCusName.setName("txtCusName"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Phone");

        txtCusPhone.setFont(Global.textFont);
        txtCusPhone.setName("txtCusPhone"); // NOI18N

        txtCusEmail.setFont(Global.textFont);
        txtCusEmail.setName("txtCusEmail"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Email");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Address");

        txtCusAddress.setFont(Global.textFont);
        txtCusAddress.setName("txtCusAddress"); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("A/C Type");

        txtCreditLimit.setFont(Global.textFont);
        txtCreditLimit.setName("txtCreditLimit"); // NOI18N

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Credit Limit");

        txtConPerson.setFont(Global.textFont);
        txtConPerson.setName("txtConPerson"); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Contact Person");

        btnSave.setBackground(ColorUtil.mainColor);
        btnSave.setFont(Global.lableFont);
        btnSave.setForeground(ColorUtil.foreground);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnClear.setBackground(ColorUtil.btnEdit);
        btnClear.setFont(Global.lableFont);
        btnClear.setForeground(ColorUtil.foreground);
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clear-button-white.png"))); // NOI18N
        btnClear.setText("Clear");
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Region");

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        cboAccount.setFont(Global.textFont);
        cboAccount.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cboRegion.setFont(Global.textFont);
        cboRegion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Price Type");

        cboPriceType.setFont(Global.textFont);
        cboPriceType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Credit Term");

        txtCreditTerm.setFont(Global.textFont);
        txtCreditTerm.setName("txtCreditTerm"); // NOI18N

        jButton1.setBackground(ColorUtil.btnEdit);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(ColorUtil.foreground);
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/import-button.png"))); // NOI18N
        jButton1.setText("Import");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelEntryLayout = new javax.swing.GroupLayout(panelEntry);
        panelEntry.setLayout(panelEntryLayout);
        panelEntryLayout.setHorizontalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtConPerson, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtCusName, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                    .addComponent(txtCusCode)
                    .addComponent(txtCusPhone)
                    .addComponent(txtCusEmail)
                    .addComponent(txtCusAddress)
                    .addComponent(txtCreditLimit)
                    .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboAccount, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboRegion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboPriceType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCreditTerm)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)
                        .addContainerGap())))
        );

        panelEntryLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnClear, btnSave});

        panelEntryLayout.setVerticalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtConPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCusPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCusEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(cboRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCusAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(cboAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(cboPriceType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtCreditLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtCreditTerm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkActive)
                .addGap(2, 2, 2)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(lblStatus)
                    .addComponent(jButton1)
                    .addComponent(btnClear))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelEntryLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtConPerson, txtCreditLimit, txtCusAddress, txtCusCode, txtCusEmail, txtCusName, txtCusPhone});

        tblCustomer.setFont(Global.textFont);
        tblCustomer.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCustomer.setName("tblCustomer"); // NOI18N
        tblCustomer.setRowHeight(Global.tblRowHeight);
        tblCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCustomerKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblCustomer);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            saveCustomer();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            LOGGER.error("Save Customer :" + e.getMessage());

        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtCusCodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCusCodeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCusCodeKeyReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        mainFrame.setFilterObserver(this);
        if (!isShown) {
            initMain();
        }
        txtCusName.requestFocus();
        clear();
    }//GEN-LAST:event_formComponentShown

    private void tblCustomerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCustomerKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_tblCustomerKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        importDialog.setSize(Global.width - 400, Global.height - 400);
        importDialog.setLocationRelativeTo(null);
        importDialog.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cboAccount;
    private javax.swing.JComboBox<String> cboPriceType;
    private javax.swing.JComboBox<String> cboRegion;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelEntry;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtConPerson;
    private javax.swing.JTextField txtCreditLimit;
    private javax.swing.JTextField txtCreditTerm;
    private javax.swing.JTextField txtCusAddress;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusEmail;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtCusPhone;
    // End of variables declaration//GEN-END:variables

    private void initKeyListener() {
        txtCusCode.addKeyListener(this);
        txtCusName.addKeyListener(this);
        txtCusPhone.addKeyListener(this);
        txtCusAddress.addKeyListener(this);
        cboPriceType.getEditor().getEditorComponent().addKeyListener(this);
        cboRegion.getEditor().getEditorComponent().addKeyListener(this);
        cboAccount.getEditor().getEditorComponent().addKeyListener(this);

        cboPriceType.getEditor().getEditorComponent().setName("cboPriceType");
        cboRegion.getEditor().getEditorComponent().setName("cboRegion");
        cboAccount.getEditor().getEditorComponent().setName("cboAccount");

        txtCusEmail.addKeyListener(this);
        cboAccount.addKeyListener(this);
        txtCreditLimit.addKeyListener(this);
        txtCreditTerm.addKeyListener(this);
        txtConPerson.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnSave.addKeyListener(this);
        btnClear.addKeyListener(this);
        tblCustomer.addKeyListener(this);

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object sourceObj = e.getSource();
        String ctrlName = "-";

        if (sourceObj instanceof JComboBox) {
            ctrlName = ((JComboBox) sourceObj).getName();
        } else if (sourceObj instanceof JFormattedTextField) {
            ctrlName = ((JFormattedTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTextComponent) {
            ctrlName = ((JTextComponent) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtCusCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
                break;
            case "txtCusName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtConPerson.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();

                }
                tabToTable(e);
                break;
            case "txtConPerson":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusPhone.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCusName.requestFocus();

                }
                tabToTable(e);
                break;
            case "txtCusPhone":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusEmail.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtConPerson.requestFocus();

                }
                tabToTable(e);

                break;
            case "txtCusEmail":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    cboRegion.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCusPhone.requestFocus();

                }
                tabToTable(e);

                break;
            case "cboRegion":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtCusAddress.requestFocus();
                        break;
                    /*case KeyEvent.VK_UP:
                        txtCusEmail.requestFocus();
                        break;
                     */
                    case KeyEvent.VK_RIGHT:
                        txtCusAddress.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtCusEmail.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "txtCusAddress":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    cboAccount.requestFocus();

                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    cboRegion.requestFocus();
                }
                tabToTable(e);

                break;
            case "cboAccount":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        cboPriceType.requestFocus();
                        break;
                    /*case KeyEvent.VK_UP:
                        txtCusAddress.requestFocus();
                        break;
                     */
                    case KeyEvent.VK_RIGHT:
                        cboPriceType.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        txtCusAddress.requestFocus();
                        break;
                }
                tabToTable(e);

                break;
            case "cboPriceType":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        txtCreditLimit.requestFocus();
                        break;
                    /*case KeyEvent.VK_UP:
                        cboAccount.requestFocus();
                        break;
                     */
                    case KeyEvent.VK_RIGHT:
                        txtCreditLimit.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboAccount.requestFocus();
                        break;
                }
                tabToTable(e);

                break;

            case "txtCreditTerm":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCreditLimit.requestFocus();
                }
                tabToTable(e);

                break;
            case "txtCreditLimit":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCreditTerm.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    cboPriceType.requestFocus();
                }
                tabToTable(e);

                break;
            case "chkActive":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnSave.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtConPerson.requestFocus();
                }
                tabToTable(e);

                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnClear.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    chkActive.requestFocus();
                }
                tabToTable(e);

                break;
            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
                tabToTable(e);

                break;
            case "tblCustomer":
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    selectRow = tblCustomer.convertRowIndexToModel(tblCustomer.getSelectedRow());
                    setCustomer(customerTabelModel.getCustomer(selectRow));
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCusName.requestFocus();
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCusName.requestFocus();
                }
                break;
            default:
                break;
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblCustomer.requestFocus();
            if (tblCustomer.getRowCount() >= 0) {
                tblCustomer.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void save() {
        saveCustomer();
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
        searchCustomer();
    }

    @Override
    public void sendFilter(String filter) {
        setTableFilter(filter);
    }

}
