/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.StartWithRowFilter;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Region;
import com.cv.accountswing.entity.Supplier;
import com.cv.accountswing.entity.TraderType;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.RegionService;
import com.cv.accountswing.service.SupplierService;
import com.cv.accountswing.service.TraderTypeService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.setup.common.SupplierTabelModel;
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

/**
 *
 * @author Lenovo
 */
@Component
public class SupplierSetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(Supplier.class);
    private int selectRow = -1;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private Supplier customer;
    @Autowired
    private SupplierTabelModel supplierTabelModel;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private COAService coaService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private TraderTypeService traderTypeService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private ApplicationMainFrame mainFrame;
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
    public SupplierSetup() {
        initComponents();
        initKeyListener();
    }

    private void initMain() {
        initCombo();
        initTable();
        clear();
        isShown = true;
    }

    private void initCombo() {
        List<ChartOfAccount> listCOA = coaService.search("-", "-", Global.compId.toString(), "3", "-", "-", "-");
        BindingUtil.BindComboFilter(cboAccount, listCOA, null, true, false);
        BindingUtil.BindComboFilter(cboPriceType, traderTypeService.findAll(), null, true, false);
        BindingUtil.BindComboFilter(cboRegion, regionService.search("-", "-", Global.compId.toString(), "-"), null, true, false);

    }

    private void initTable() {
        tblCustomer.setModel(supplierTabelModel);
        tblCustomer.getTableHeader().setFont(Global.textFont);
        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(40);// Code
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(320);// Name
        tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(40);// Active 
        tblCustomer.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCustomer.setDefaultRenderer(Object.class, new TableCellRender());
        tblCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCustomer.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblCustomer.getSelectedRow() >= 0) {
                    selectRow = tblCustomer.convertRowIndexToModel(tblCustomer.getSelectedRow());
                    setCustomer(supplierTabelModel.getCustomer(selectRow));

                }

            }
        });
        swrf = new StartWithRowFilter(txtCusFilter);
        sorter = new TableRowSorter(tblCustomer.getModel());
        tblCustomer.setRowSorter(sorter);
        searchSupplier();
    }

    private void searchSupplier() {
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            try {
                List<Supplier> listSupplier = supplierService.search("-", "-", "-", "-", "-");
                supplierTabelModel.setListCustomer(listSupplier);
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception e) {
                LOGGER.error("Search Supplier " + e.getMessage());
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Search Supplier", JOptionPane.ERROR_MESSAGE);
                loadingObserver.load(this.getName(), "Stop");
            }
        });

    }

    private void setCustomer(Supplier cus) {
        txtCusCode.setText(cus.getTraderId());
        txtCusName.setText(cus.getTraderName());
        txtCusEmail.setText(cus.getEmail());
        txtCusPhone.setText(cus.getPhone());
        cboRegion.setSelectedItem(cus.getRegion());
        cboPriceType.setSelectedItem(cus.getTraderType());
        txtCusAddress.setText(cus.getAddress());
        chkActive.setSelected(cus.getActive());
        cboAccount.setSelectedItem(cus.getAccount());
        //txtCreditLimit.setText(Util1.getString(cus.getCreditLimit()));
        lblStatus.setText("EDIT");

    }

    private boolean isValidEntry() {
        boolean status;
        if (txtCusName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Supplier Name can't be empty");
            status = false;
        } else {
            customer = new Supplier();
            if (lblStatus.getText().equals("EDIT")) {
                Supplier cus = supplierTabelModel.getCustomer(selectRow);
                customer.setId(cus.getId());
            }
            customer.setTraderId(txtCusCode.getText());
            customer.setTraderName(txtCusName.getText());
            customer.setPhone(txtCusPhone.getText());
            customer.setEmail(txtCusEmail.getText());
            customer.setAddress(txtCusAddress.getText());
            customer.setRegion((Region) cboRegion.getSelectedItem());
            customer.setTraderType((TraderType) cboPriceType.getSelectedItem());
            customer.setActive(chkActive.isSelected());
            customer.setAccount((ChartOfAccount) cboAccount.getSelectedItem());
            customer.setCompCode(Global.compId);
            customer.setUpdatedDate(Util1.getTodayDate());
            //customer.setCreditLimit(Util1.getInteger(txtCreditLimit.getText()));
            //customer.setAppShortName(TOOL_TIP_TEXT_KEY);
            //customer.setAppTraderCode(TOOL_TIP_TEXT_KEY);

            status = true;
        }
        return status;
    }

    private void saveCustomer() {
        if (isValidEntry()) {
            Supplier save = supplierService.save(customer);
            if (save.getTraderId() != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if (lblStatus.getText().equals("NEW")) {
                    supplierTabelModel.addCustomer(customer);
                } else {
                    supplierTabelModel.setCustomer(selectRow, customer);
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
        cboRegion.setSelectedItem(null);
        cboPriceType.setSelectedItem(null);
        txtCusAddress.setText(null);
        chkActive.setSelected(Boolean.TRUE);
        //txtCreditLimit.setText(null);
        lblStatus.setText("NEW");
        txtCusName.requestFocus();
        txtCusFilter.setText(null);
        cboAccount.setSelectedItem(null);
    }

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
        txtRemark.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnSave.addKeyListener(this);
        btnClear.addKeyListener(this);
        tblCustomer.addKeyListener(this);

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
        txtRemark = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        cboAccount = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        lblStatus = new javax.swing.JLabel();
        cboRegion = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cboPriceType = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();
        txtCusFilter = new javax.swing.JTextField();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelEntry.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                panelEntryComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Code");

        txtCusCode.setEditable(false);
        txtCusCode.setFont(Global.textFont);
        txtCusCode.setName("txtCusCode"); // NOI18N

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
        jLabel6.setText("Account");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Remark");

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnClear.setFont(Global.lableFont);
        btnClear.setText("Clear");
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        cboAccount.setFont(Global.textFont);
        cboAccount.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboAccount.setName("cboAccount"); // NOI18N

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Region");

        chkActive.setFont(Global.lableFont);
        chkActive.setSelected(true);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        cboRegion.setFont(Global.textFont);
        cboRegion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboRegion.setToolTipText("");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Price Type");

        cboPriceType.setFont(Global.textFont);
        cboPriceType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboPriceType.setName("cboAccount"); // NOI18N

        javax.swing.GroupLayout panelEntryLayout = new javax.swing.GroupLayout(panelEntry);
        panelEntry.setLayout(panelEntryLayout);
        panelEntryLayout.setHorizontalGroup(
            panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEntryLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClear)
                        .addContainerGap())
                    .addGroup(panelEntryLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                            .addComponent(txtCusName)
                            .addComponent(txtCusCode)
                            .addComponent(txtCusPhone)
                            .addComponent(txtCusEmail)
                            .addComponent(txtCusAddress)
                            .addComponent(cboAccount, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboRegion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboPriceType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
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
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboPriceType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelEntryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnSave)
                    .addComponent(lblStatus))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelEntryLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCusAddress, txtCusCode, txtCusEmail, txtCusName, txtCusPhone, txtRemark});

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
        tblCustomer.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tblCustomerComponentShown(evt);
            }
        });
        jScrollPane2.setViewportView(tblCustomer);

        txtCusFilter.setFont(Global.textFont);
        txtCusFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCusFilterKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                    .addComponent(txtCusFilter))
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)))
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

    private void txtCusFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCusFilterKeyReleased
        // TODO add your handling code here:

        if (txtCusFilter.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtCusFilterKeyReleased

    private void tblCustomerComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tblCustomerComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_tblCustomerComponentShown

    private void panelEntryComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelEntryComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_panelEntryComponentShown

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        clear();
        txtCusName.requestFocus();

    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cboAccount;
    private javax.swing.JComboBox<String> cboPriceType;
    private javax.swing.JComboBox<String> cboRegion;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JTextField txtCusAddress;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusEmail;
    private javax.swing.JTextField txtCusFilter;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtCusPhone;
    private javax.swing.JTextField txtRemark;
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
                    txtCusPhone.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();

                }
                tabToTable(e);
                break;
            case "txtCusPhone":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCusEmail.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCusName.requestFocus();

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
                    case KeyEvent.VK_UP:
                        txtCusEmail.requestFocus();
                        break;
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
                    case KeyEvent.VK_UP:
                        txtCusAddress.requestFocus();
                        break;
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
                        txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        cboAccount.requestFocus();
                        break;
                    case KeyEvent.VK_RIGHT:
                        txtRemark.requestFocus();
                        break;
                    case KeyEvent.VK_LEFT:
                        cboAccount.requestFocus();
                        break;
                }
                tabToTable(e);
                break;
            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
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
                    txtRemark.requestFocus();
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
                    setCustomer(supplierTabelModel.getCustomer(selectRow));
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
        searchSupplier();
    }

}
