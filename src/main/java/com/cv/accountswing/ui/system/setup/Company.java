/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.BusinessType;
import com.cv.accountswing.entity.CompanyInfo;
import com.cv.accountswing.service.BusinessTypeService;
import com.cv.accountswing.service.CompanyInfoService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.system.setup.common.CompanyTableModel;
import com.cv.accountswing.util.BindingUtil;
import com.cv.accountswing.util.Util1;
import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
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
public class Company extends javax.swing.JPanel implements KeyListener, PanelControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(Company.class);

    private int selectRow = -1;
    private CompanyInfo companyInfo = new CompanyInfo();
    @Autowired
    private CompanyInfoService compInfoService;
    @Autowired
    private CompanyTableModel companyTableModel;
    @Autowired
    private BusinessTypeService companyTypeService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private LoadingObserver loadingObserver;
    private boolean isShown = false;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    /**
     * Creates new form Company
     */
    public Company() {
        initComponents();
    }

    public void initMain() {
        initCombo();
        initKeyListener();
        initTable();
        isShown = true;
    }

    private void initTable() {
        tblCompany.setModel(companyTableModel);
        tblCompany.getTableHeader().setFont(Global.textFont);
        tblCompany.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCompany.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblCompany.getSelectedRow() >= 0) {
                    selectRow = tblCompany.convertRowIndexToModel(tblCompany.getSelectedRow());
                    CompanyInfo com = companyTableModel.getCompany(selectRow);
                    setCompanyInfo(com);
                }

            }
        });
        searchCompany();
        txtCode.requestFocus();

    }

    private void searchCompany() {
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            try {
                companyTableModel.setListCompany(compInfoService.search("-", "-", "-", "-", "-", "-"));
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception e) {
                LOGGER.error("search Company " + e.getMessage());
                JOptionPane.showMessageDialog(Global.parentForm, btnSave);
                loadingObserver.load(this.getName(), "Stop");
            }
        });

    }

    private void initCombo() {
        BindingUtil.BindComboFilter(cboBusiness, companyTypeService.getAllBusinessType(), null, true, false);
    }

    private void setCompanyInfo(CompanyInfo cInfo) {
        companyInfo = cInfo;
        txtCode.setText(cInfo.getCompCode());
        txtShortCode.setText(cInfo.getShortCode());
        txtName.setText(cInfo.getName());
        txtPhone.setText(cInfo.getPhone());
        txtEmail.setText(cInfo.getEmail());
        txtSecurityCode.setText(cInfo.getSecurityCode());
        txtAddress.setText(cInfo.getAddress());
        txtFromDate.setText(Util1.toDateStr(cInfo.getFinicialPeriodFrom(), "dd-MM-yyyy"));
        txtToDate.setText(Util1.toDateStr(cInfo.getFinicialPeriodTo(), "dd-MM-yyyy"));
        if (cInfo.getActive() != null) {
            chkActive.setSelected(cInfo.getActive());
        }
        lblStatus.setText("EDIT");

    }

    private void saveCompany() {
        if (isValidEntry()) {
            try {
                String status = lblStatus.getText();
                String userId = Global.loginUser.getUserId().toString();
                CompanyInfo saveCom = compInfoService.save(companyInfo, status, userId, "-");
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if (status.equals("NEW")) {
                    companyTableModel.addCompany(saveCom);
                } else {
                    companyTableModel.setCompany(selectRow, saveCom);
                }
                clear();
            } catch (HeadlessException e) {
                LOGGER.error("Save Company :" + e.getMessage());
                JOptionPane.showMessageDialog(Global.parentForm, "Could'nt saved.");
            }
        }
    }

    private boolean isValidEntry() {
        boolean status;
        if (txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Name");
            txtName.requestFocus();
            status = false;
        } else {
            //companyInfo = new CompanyInfo();
            companyInfo.setCompCode(txtCode.getText());
            companyInfo.setShortCode(txtShortCode.getText());
            companyInfo.setName(txtName.getText());
            companyInfo.setPhone(txtPhone.getText());
            companyInfo.setEmail(txtEmail.getText());
            companyInfo.setSecurityCode(txtSecurityCode.getText());
            companyInfo.setAddress(txtAddress.getText());
            companyInfo.setFinicialPeriodFrom(Util1.toDate(txtFromDate.getText(), "dd-MM-yyyy"));
            companyInfo.setFinicialPeriodTo(Util1.toDate(txtToDate.getText(), "dd-MM-yyyy"));
            companyInfo.setActive(chkActive.isSelected());
            BusinessType businessType = (BusinessType) cboBusiness.getSelectedItem();
            companyInfo.setBusinessType(businessType.getId());
            status = true;
        }
        return status;

    }

    private void clear() {
        txtCode.setText(null);
        txtShortCode.setText(null);
        txtName.setText(null);
        txtPhone.setText(null);
        txtEmail.setText(null);
        txtSecurityCode.setText(null);
        txtAddress.setText(null);
        txtFromDate.setText(null);
        txtToDate.setText(null);
        chkActive.setSelected(Boolean.FALSE);
        lblStatus.setText("NEW");
        companyInfo = new CompanyInfo();
    }

    private void initKeyListener() {
        txtCode.addKeyListener(this);
        txtShortCode.addKeyListener(this);
        txtName.addKeyListener(this);
        txtPhone.addKeyListener(this);
        txtEmail.addKeyListener(this);
        txtSecurityCode.addKeyListener(this);
        txtAddress.addKeyListener(this);
        txtFromDate.addKeyListener(this);
        txtToDate.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnSave.addKeyListener(this);
        btnClear.addKeyListener(this);
        tblCompany.addKeyListener(this);
        cboBusiness.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cboBusiness.requestFocus();
                }
                tabToTable(e);

            }

        });
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
        tblCompany = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtShortCode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtSecurityCode = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtFromDate = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtToDate = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        btnClear = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        cboBusiness = new javax.swing.JComboBox<>();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCompany.setFont(Global.textFont);
        tblCompany.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCompany.setName("tblCompany"); // NOI18N
        tblCompany.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblCompany);

        jLabel1.setFont(Global.textFont);
        jLabel1.setText("Code");

        txtCode.setFont(Global.textFont);
        txtCode.setName("txtCode"); // NOI18N

        jLabel2.setFont(Global.textFont);
        jLabel2.setText("Short Code");

        txtShortCode.setFont(Global.textFont);
        txtShortCode.setName("txtShortCode"); // NOI18N

        jLabel3.setFont(Global.textFont);
        jLabel3.setText("Name");

        txtName.setFont(Global.textFont);
        txtName.setName("txtName"); // NOI18N

        txtPhone.setFont(Global.textFont);
        txtPhone.setName("txtPhone"); // NOI18N

        jLabel4.setFont(Global.textFont);
        jLabel4.setText("Phone");

        txtEmail.setFont(Global.textFont);
        txtEmail.setName("txtEmail"); // NOI18N

        jLabel5.setFont(Global.textFont);
        jLabel5.setText("Email");

        jLabel6.setFont(Global.textFont);
        jLabel6.setText("Security Code");

        txtSecurityCode.setFont(Global.textFont);
        txtSecurityCode.setName("txtSecurityCode"); // NOI18N

        txtAddress.setFont(Global.textFont);
        txtAddress.setName("txtAddress"); // NOI18N

        jLabel7.setFont(Global.textFont);
        jLabel7.setText("Address");

        txtFromDate.setFont(Global.textFont);
        txtFromDate.setName("txtFromDate"); // NOI18N

        jLabel8.setFont(Global.textFont);
        jLabel8.setText("From Date");

        jLabel9.setFont(Global.textFont);
        jLabel9.setText("To Date");

        txtToDate.setFont(Global.textFont);
        txtToDate.setName("txtToDate"); // NOI18N

        jLabel10.setFont(Global.textFont);
        jLabel10.setText("Business");

        chkActive.setFont(Global.textFont);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        btnClear.setBackground(ColorUtil.btnEdit);
        btnClear.setFont(Global.textFont);
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clear-button-white.png"))); // NOI18N
        btnClear.setText("Clear");
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnSave.setBackground(ColorUtil.mainColor);
        btnSave.setFont(Global.textFont);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        cboBusiness.setName("cboBusiness"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCode))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtShortCode, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPhone, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSecurityCode, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnClear))
                            .addComponent(cboBusiness, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtShortCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtSecurityCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cboBusiness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSave)
                            .addComponent(btnClear)
                            .addComponent(chkActive)
                            .addComponent(lblStatus))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            save();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
            LOGGER.error("Save Company :" + e.getMessage());
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        txtCode.requestFocus();
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cboBusiness;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCompany;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFromDate;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtSecurityCode;
    private javax.swing.JTextField txtShortCode;
    private javax.swing.JTextField txtToDate;
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
        }
        switch (ctrlName) {
            case "txtCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtShortCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtShortCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCode.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtPhone.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCode.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtPhone":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtEmail.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtName.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtEmail":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtSecurityCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtPhone.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtSecurityCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtAddress.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtEmail.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtAddress":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtFromDate.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtSecurityCode.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtFromDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtToDate.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtAddress.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtToDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    cboBusiness.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtFromDate.requestFocus();
                }
                tabToTable(e);
                break;
            case "chkActive":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnSave.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    cboBusiness.requestFocus();
                }
                tabToTable(e);
                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnClear.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    chkActive.requestFocus();
                }
                tabToTable(e);
                break;
            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
                tabToTable(e);
                break;
            case "tblCompany":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCode.requestFocus();
                }
                break;
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblCompany.requestFocus();
            if (tblCompany.getRowCount() >= 0) {
                tblCompany.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        saveCompany();
    }

    @Override
    public void refresh() {
        searchCompany();
    }

}
