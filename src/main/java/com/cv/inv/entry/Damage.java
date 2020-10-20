/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.DamageDetailHis;
import com.cv.inv.entity.DamageHis;
import com.cv.inv.entity.Stock;
import com.cv.inv.entry.common.DamageTableModel;
import com.cv.inv.entry.dialog.DamageSearchDialog;
import com.cv.inv.entry.editor.LocationAutoCompleter;
import com.cv.inv.entry.editor.StockCellEditor;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.service.DamageHisService;
import com.cv.inv.service.StockService;
import com.cv.inv.service.VouIdService;
import com.cv.inv.util.GenVouNoImpl;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Component
public class Damage extends javax.swing.JPanel implements SelectionObserver, KeyListener, PanelControl {

    /**
     * Creates new form Damage
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Damage.class);
    private LoadingObserver loadingObserver;
    private SelectionObserver selectionObserver;
    private LocationAutoCompleter locCompleter;
    private List<DamageDetailHis> listDetail = new ArrayList();
    private DamageHis rohh2 = new DamageHis();
    private GenVouNoImpl vouEngine = null;
    @Autowired
    private DamageTableModel damageTableModel;
    @Autowired
    private VouIdService voudIdService;
    @Autowired
    private StockService stockService;
    @Autowired
    private DamageHisService dhService;
    @Autowired
    private DamageSearchDialog dmgSearchDialog;
    @Autowired
    private ApplicationMainFrame mainFrame;

    public Damage() {
        initComponents();
        //   addNewRow();
        initKeyListener();
    }

    private void initMain() {
        initCombo();
        initTable();
        actionMapping();
        initKeyListener();
        setTodayDate();
        //   assignDefalutValue();
        genVouNo();
    }

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    private void initTable() {
        tblDamage.setModel(damageTableModel);
        damageTableModel.setParent(tblDamage);
        damageTableModel.addEmptyRow();
        damageTableModel.setCallBack(this);
        tblDamage.getTableHeader().setFont(Global.lableFont);
        //  tblDamage.setCellSelectionEnabled(true);

        tblDamage.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblDamage.getColumnModel().getColumn(1).setPreferredWidth(300);
        tblDamage.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblDamage.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblDamage.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblDamage.getColumnModel().getColumn(5).setPreferredWidth(60);

        tblDamage.getColumnModel().getColumn(0).setCellEditor(new StockCellEditor());
        tblDamage.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblDamage.getColumnModel().getColumn(3).setCellEditor(new StockUnitEditor());

        tblDamage.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblDamage.setDefaultRenderer(Object.class, new TableCellRender());
        tblDamage.setDefaultRenderer(Float.class, new TableCellRender());
        tblDamage.setDefaultRenderer(Double.class, new TableCellRender());
        tblDamage.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDamage.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                //     txtRecNo.setText(Integer.toString(tblDamage.getSelectedRow() + 1));
            }
        });
    }

    private void initKeyListener() {
        txtVouNo.addKeyListener(this);
        txtLocation.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtDate.getDateEditor().getUiComponent().setName("txtDate");
        txtDate.getDateEditor().getUiComponent().addKeyListener(this);
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblDamage.requestFocus();
            if (tblDamage.getRowCount() >= 0) {
                tblDamage.setRowSelectionInterval(0, 0);
            }
        }
    }

    private void setTodayDate() {
        txtDate.setDate(Util1.getTodayDate());
    }

    private void initCombo() {
        locCompleter = new LocationAutoCompleter(txtLocation, Global.listLocation, null);
        locCompleter.setSelectionObserver(this);
    }

    private void addNewRow() {
        DamageDetailHis sale = new DamageDetailHis();
        sale.setStock(new Stock());
        listDetail.add(sale);
    }

    private void genVouNo() {
        vouEngine = new GenVouNoImpl(voudIdService, "Damage", Util1.getPeriod(txtDate.getDate()));
        txtVouNo.setText(vouEngine.genVouNo());
    }

    private boolean saveDamage() {
        boolean status = false;
        if (isValidEntry() && damageTableModel.isValidEntry()) {
            List<DamageDetailHis> listDmgDetail = damageTableModel.getDetail();

            try {
                String vouStatus = lblStatus.getText();
                dhService.save(rohh2, listDmgDetail, vouStatus);
                vouEngine.updateVouNo();
                genVouNo();
                status = true;
            } catch (Exception ex) {
                LOGGER.error("save Damage : " + rohh2.getDmgVouId() + " : " + ex.getMessage());
            }
        }
        return status;

    }

    private boolean isValidEntry() {
        boolean status = true;

        rohh2.setDmgVouId(txtVouNo.getText());
        rohh2.setRemark(txtRemark.getText());
        if (lblStatus.getText().equals("NEW")) {
            rohh2.setDmgDate(txtDate.getDate());
            rohh2.setCreatedBy(Global.loginUser);
            rohh2.setSession(Global.sessionId);
        } else {
            Date tmpDate = txtDate.getDate();
            if (!Util1.isSameDate(tmpDate, rohh2.getDmgDate())) {
                rohh2.setDmgDate(txtDate.getDate());
            }
            rohh2.setUpdatedBy(Global.loginUser);
            rohh2.setUpdatedDate(Util1.getTodayDate());
        }
        rohh2.setDmgVouId(txtVouNo.getText());
        rohh2.setLocation(locCompleter.getLocation());
        rohh2.setDeleted(Util1.getNullTo(rohh2.isDeleted()));
        rohh2.setTotalAmount(Util1.getDouble(txtTotalAmount.getText()));

        return status;
    }

    private void clear() {
        lblStatus.setText("NEW");
        if (Global.loginDate != null) {
            txtDate.setDate(Util1.toDate(Global.loginDate, "dd/MM/yyyy"));
        } else {
            txtDate.setDate(Util1.getTodayDate());
        }
        txtRemark.setText(null);
        txtTotalAmount.setText("0.0");
        damageTableModel.clearData();
        //retOutTableModel.setLocation((LocationH2) cboLocation.getSelectedItem());
        rohh2 = new DamageHis();
        genVouNo();
    }

    private void deleteDamage() {
        int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                "Are you sure to delete?", "Damage item delete", JOptionPane.YES_NO_OPTION);
        if (yes_no == 0) {
            String vouNo = txtVouNo.getText();
            if (lblStatus.getText().equals("EDIT")) {
                dhService.delete(vouNo);
                clear();
            }
        }

    }

    private void actionMapping() {
        //F8 event on tblSale
        tblDamage.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblDamage.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event on tblSale
        tblDamage.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblDamage.getActionMap().put("ENTER-Action", actionTblPurEnterKey);
    }
    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblDamage.getSelectedRow() >= 0) {
                int yes_no = JOptionPane.showConfirmDialog(Global.parentForm,
                        "Are you sure to delete?", "Damage item delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    damageTableModel.delete(tblDamage.getSelectedRow());
                }
            }
        }
    };
    private final Action actionTblPurEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                tblDamage.getCellEditor().stopCellEditing();
            } catch (Exception ex) {
            }
        }
    };

    public void setDamageVoucher(DamageHis dmgHis, List<DamageDetailHis> listDetailHis) {
        if (!lblStatus.getText().equals("NEW")) {
            clear();
        }
        if (dmgHis != null) {
            if (dmgHis.isDeleted()) {
                lblStatus.setText("DELETED");
            } else {
                lblStatus.setText("EDIT");
            }
            txtVouNo.setText(dmgHis.getDmgVouId());
            txtRemark.setText(dmgHis.getRemark());
            txtDate.setDate(dmgHis.getDmgDate());
            locCompleter.setLocation(dmgHis.getLocation());
            txtTotalAmount.setText(dmgHis.getTotalAmount().toString());
            damageTableModel.setListDetail(listDetailHis);
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
        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        txtDate = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDamage = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtTotalAmount = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setFont(Global.textFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Date");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Location");

        txtLocation.setFont(Global.textFont);
        txtLocation.setName("txtLocation"); // NOI18N

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setName("txtRemark"); // NOI18N

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.setFont(Global.lableFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtVouNo)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(txtLocation)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtRemark)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel8)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        tblDamage.setFont(Global.textFont);
        tblDamage.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblDamage);

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtTotalAmount.setFont(Global.amtFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Total Amount :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 13, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
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
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1071, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(9, 9, 9))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        mainFrame.setControl(this);
        initMain();
        txtVouNo.requestFocus();
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblDamage;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtTotalAmount;
    private javax.swing.JTextField txtVouNo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            switch (source.toString()) {

                case "STM-TOTAL":
                    Double grossTotal = damageTableModel.getTotal();
                    txtTotalAmount.setText(grossTotal.toString());
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
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtDate":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouNo.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtLocation.requestFocus();
                }
                tabToTable(e);
                break;
            case "txtLocation":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtRemark.requestFocus();
                }
                tabToTable(e);
                break;

            case "txtRemark":
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtLocation.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
                break;
        }
    }

    @Override
    public void save() {
        if (saveDamage()) {
            clear();
        }
    }

    @Override
    public void delete() {
        deleteDamage();
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        dmgSearchDialog.setLocationRelativeTo(null);
        dmgSearchDialog.setVisible(true);
    }

    @Override
    public void print() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
