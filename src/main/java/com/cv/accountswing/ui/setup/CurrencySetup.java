/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.CurrencyKey;
import com.cv.accountswing.service.CurrencyService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.setup.common.CurrencyTabelModel;
import com.cv.accountswing.util.Util1;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class CurrencySetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ChartOfAccountSetup.class);

    private int selectRow = -1;
    private Currency currency;
    @Autowired
    private CurrencyService curService;
    @Autowired
    private CurrencyTabelModel currencyTabelModel;
    @Autowired
    private TaskExecutor taskExecutor;
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
     * Creates new form CurrencySetup
     */
    public CurrencySetup() {
        initComponents();
    }

    private void initMain() {
        loadingObserver.load(this.getName(), "Start");
        initKeyListener();
        initTable();
        isShown = true;
    }

    private void initTable() {
        taskExecutor.execute(() -> {
            tblCurrency.setModel(currencyTabelModel);
            tblCurrency.getTableHeader().setFont(Global.textFont);
            List<Currency> listCurr = curService.search("-", "-", Global.compId.toString());
            currencyTabelModel.setlistCurrency(listCurr);
            tblCurrency.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblCurrency.getColumnModel().getColumn(0).setPreferredWidth(20);// Code
            tblCurrency.getColumnModel().getColumn(1).setPreferredWidth(320);// Name
            tblCurrency.getColumnModel().getColumn(2).setPreferredWidth(15);// Symbol      
            tblCurrency.getColumnModel().getColumn(3).setPreferredWidth(10);// Symbol  
            tblCurrency.setDefaultRenderer(Boolean.class, new TableCellRender());
            tblCurrency.setDefaultRenderer(Object.class, new TableCellRender());

            tblCurrency.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
                if (e.getValueIsAdjusting()) {
                    if (tblCurrency.getSelectedRow() >= 0) {
                        selectRow = tblCurrency.convertRowIndexToModel(tblCurrency.getSelectedRow());

                        if (selectRow >= 0) {
                            Currency c = currencyTabelModel.getCurrency(selectRow);
                            setCurrency(c);
                        }
                    }

                }
            });
            loadingObserver.load(this.getName(), "Stop");
        });

    }

    private void saveCurrency() {
        currency = new Currency();
        CurrencyKey key = new CurrencyKey();
        key.setCompCode(Global.compId);
        key.setCode(txtCurrCode.getText());
        currency.setKey(key);
        currency.setActive(chkActive.isSelected());
        currency.setCurrencyName(txtCurrName.getText());
        currency.setCurrencySymbol(txtCurrSymbol.getText());

        if (isValidCurrency(currency, lblStatus.getText())) {
            Currency save = curService.save(currency);
            if (save != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if (lblStatus.getText().equals("EDIT")) {
                    currencyTabelModel.setCurrency(selectRow, currency);
                } else {
                    currencyTabelModel.addCurrency(currency);
                }

                clear();
            }
        }
    }

    private void setCurrency(Currency currency) {
        txtCurrCode.setText(currency.getKey().getCode());
        txtCurrName.setText(currency.getCurrencyName());
        txtCurrSymbol.setText(currency.getCurrencySymbol());
        chkActive.setSelected(currency.getActive());
        lblStatus.setText("EDIT");
    }

    public void clear() {
        txtCurrCode.setText(null);
        txtCurrName.setText(null);
        txtCurrSymbol.setText(null);
        chkActive.setSelected(Boolean.TRUE);
        lblStatus.setText("NEW");
        txtCurrCode.requestFocus();
    }

    private void initKeyListener() {
        txtCurrCode.addKeyListener(this);
        txtCurrName.addKeyListener(this);
        txtCurrSymbol.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnSave.addKeyListener(this);
        btnClear.addKeyListener(this);
        tblCurrency.addKeyListener(this);
    }

    private boolean isValidCurrency(Currency cur, String editStatus) {
        boolean status = true;

        if (Util1.isNull(cur.getKey().getCode(), "-").equals("-")) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid currency code.");
        } else if (Util1.isNull(cur.getCurrencyName(), "-").equals("-")) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid currency name.");
        } else if (editStatus.equals("NEW")) {
            List<Currency> listCurr = curService.search(cur.getKey().getCode(), "-", Global.compId.toString());

            if (listCurr != null) {
                if (listCurr.size() > 0) {
                    status = false;
                    JOptionPane.showMessageDialog(Global.parentForm, "Duplicate currency code.");
                }
            }
        }

        if (status) {
            if (editStatus.equals("NEW")) {
                cur.setCreatedBy(Global.loginUser.getUserId().toString());
                cur.setCreatedDt(Util1.getTodayDate());
            } else {
                cur.setUpdatedBy(Global.loginUser.getUserId().toString());
                cur.setUpdatedDt(Util1.getTodayDate());
            }
        }

        return status;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCurrency = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCurrCode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCurrName = new javax.swing.JTextField();
        txtCurrSymbol = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        btnClear = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        jButton1.setText("jButton1");

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCurrency.setFont(Global.textFont);
        tblCurrency.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCurrency.setName("tblCurrency"); // NOI18N
        tblCurrency.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblCurrency);

        jPanel1.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Code");

        txtCurrCode.setFont(Global.textFont);
        txtCurrCode.setName("txtCurrCode"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Name");

        txtCurrName.setFont(Global.textFont);
        txtCurrName.setName("txtCurrName"); // NOI18N

        txtCurrSymbol.setFont(Global.textFont);
        txtCurrSymbol.setName("txtCurrSymbol"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Symbol");

        chkActive.setFont(Global.lableFont);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        btnClear.setFont(Global.lableFont);
        btnClear.setText("Clear");
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtCurrCode))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtCurrName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtCurrSymbol)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(110, 110, 110)
                                .addComponent(btnSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClear)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCurrCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCurrName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCurrSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnSave)
                    .addComponent(lblStatus))
                .addContainerGap(376, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            save();
        } catch (Exception e) {
            LOGGER.error("Save Currency :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Save Currency", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        txtCurrCode.requestFocus();

    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCurrency;
    private javax.swing.JTextField txtCurrCode;
    private javax.swing.JTextField txtCurrName;
    private javax.swing.JTextField txtCurrSymbol;
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
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        }
        LOGGER.info("Control Name Key Released:" + ctrlName);
        switch (ctrlName) {
            case "txtCurrCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCurrName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtCurrName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCurrSymbol.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCurrCode.requestFocus();
                }
                tabToTable(e);

                break;
            case "txtCurrSymbol":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCurrName.requestFocus();
                }
                tabToTable(e);

                break;
            case "chkActive":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnSave.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCurrSymbol.requestFocus();
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
                    txtCurrCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
                tabToTable(e);

                break;
            case "tblCurrency":
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    selectRow = tblCurrency.convertRowIndexToModel(tblCurrency.getSelectedRow());
                    Currency curr = currencyTabelModel.getCurrency(selectRow);
                    setCurrency(curr);
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCurrCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtCurrCode.requestFocus();
                }

                break;
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblCurrency.requestFocus();
            if (tblCurrency.getRowCount() >= 0) {
                tblCurrency.setRowSelectionInterval(0, 0);
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
    }

    @Override
    public void print() {
    }

    @Override
    public void save() {
        saveCurrency();
    }
}
