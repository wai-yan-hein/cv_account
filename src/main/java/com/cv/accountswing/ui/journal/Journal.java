/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.journal;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.view.VGeneralVoucher;
import com.cv.accountswing.service.VGvService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.journal.common.JournalTableModel;
import com.cv.accountswing.util.Util1;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
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
public class Journal extends javax.swing.JPanel implements KeyListener, SelectionObserver, PanelControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(Journal.class);
    private int selectRow = -1;
    private final ImageIcon jIcon = new ImageIcon(this.getClass().getResource("/images/journal.png"));
    @Autowired
    private VGvService vGvService;
    @Autowired
    private JournalTableModel journalTableModel;
    @Autowired
    private JournalEntryDialog journalEntryDialog;
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
     * Creates new form Journal
     */
    public Journal() {
        initComponents();
        initKeyListener();
        txtFromDate.requestFocusInWindow();
    }

    private void initMain() {
        setTodayDate();
        initTable();
        searchGV();
        isShown = true;
    }

    private void initTable() {
        tblJournal.setModel(journalTableModel);
        tblJournal.getTableHeader().setFont(Global.lableFont);
        tblJournal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblJournal.getColumnModel().getColumn(0).setPreferredWidth(10);//Date
        tblJournal.getColumnModel().getColumn(1).setPreferredWidth(300);//Vou
        tblJournal.getColumnModel().getColumn(2).setPreferredWidth(300);//Ref
        tblJournal.setDefaultRenderer(Object.class, new TableCellRender());
        tblJournal.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (tblJournal.getSelectedRow() >= 0) {
                        selectRow = tblJournal.convertRowIndexToModel(tblJournal.getSelectedRow());
                        VGeneralVoucher vGl = journalTableModel.getVGl(selectRow);
                        openJournalEntryDialog(vGl.getGvVouNo());
                    }
                }
            }

        });

    }

    private void setTodayDate() {
        txtFromDate.setDate(Util1.getTodayDate());
        txtToDate.setDate(Util1.getTodayDate());
    }

    private void searchGV() {
        LOGGER.info("searchGV");
        loadingObserver.load(this.getName(), "Start");
        taskExecutor.execute(() -> {
            try {
                String fromDate = Util1.toDateStr(txtFromDate.getDate(), "dd/MM/yyyy");
                String toDate = Util1.toDateStr(txtToDate.getDate(), "dd/MM/yyyy");
                String vouNo = txtVouNo.getText();
                String refrence = txtRefrence.getText();
                List<VGeneralVoucher> listGV = vGvService.search(fromDate, toDate, Util1.isNull(vouNo, "-"),
                        Util1.isNull(refrence, "-"), Global.compId.toString(),
                        "-");
                journalTableModel.setListGV(listGV);
                loadingObserver.load(this.getName(), "Stop");
            } catch (Exception e) {
                LOGGER.error("Search Journal Voucher :" + e.getMessage());
                JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Search Journal", JOptionPane.ERROR_MESSAGE);

            }
        });

    }

    private void initKeyListener() {
        txtFromDate.getDateEditor().getUiComponent().setName("txtFromDate");
        txtToDate.getDateEditor().getUiComponent().setName("txtToDate");
        txtFromDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtToDate.getDateEditor().getUiComponent().addKeyListener(this);
        txtVouNo.addKeyListener(this);
        txtRefrence.addKeyListener(this);
        btnEntry.addKeyListener(this);
    }

    /*private void initPropertyChangeListener() {
    txtFromDate.getDateEditor().addPropertyChangeListener(
    new PropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent e) {
    if ("date".equals(e.getPropertyName())) {
    Date newValue = Util1.toJavaDate(e.getNewValue());
    Date oldValue = Util1.toJavaDate(e.getOldValue());
    if (!Util1.isSameDate(newValue, oldValue)) {
    searchGV();
    }
    }
    }
    });
    txtToDate.getDateEditor().addPropertyChangeListener(
    new PropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent e) {
    if ("date".equals(e.getPropertyName())) {
    Date newValue = Util1.toJavaDate(e.getNewValue());
    Date oldValue = Util1.toJavaDate(e.getOldValue());
    if (!Util1.isSameDate(newValue, oldValue)) {
    searchGV();
    }
    }
    }
    });
    }*/
    private void openJournalEntryDialog(String gvId) {
        journalEntryDialog.setIconImage(jIcon.getImage());
        journalEntryDialog.setSelectionObserver(this);
        journalEntryDialog.clear();
        journalEntryDialog.setGlVouId(gvId);
        journalEntryDialog.setSize(Global.width - 200, Global.height - 200);
        journalEntryDialog.setResizable(false);
        journalEntryDialog.setLocationRelativeTo(null);
        journalEntryDialog.setVisible(true);

    }

    public void clear() {
        setTodayDate();
        txtVouNo.setText(null);
        txtRefrence.setText(null);

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
        txtVouNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtRefrence = new javax.swing.JTextField();
        btnEntry = new javax.swing.JButton();
        txtFromDate = new com.toedter.calendar.JDateChooser();
        txtToDate = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblJournal = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("From");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("To");

        txtVouNo.setFont(Global.shortCutFont);
        txtVouNo.setName("txtVouNo"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Vou");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Refrence");

        txtRefrence.setFont(Global.shortCutFont);
        txtRefrence.setName("txtRefrence"); // NOI18N
        txtRefrence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRefrenceActionPerformed(evt);
            }
        });

        btnEntry.setBackground(Global.mainColor);
        btnEntry.setFont(Global.lableFont);
        btnEntry.setForeground(new java.awt.Color(255, 255, 255));
        btnEntry.setText("+");
        btnEntry.setToolTipText("New Journal");
        btnEntry.setName("btnEntry"); // NOI18N
        btnEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntryActionPerformed(evt);
            }
        });

        txtFromDate.setDateFormatString("dd/MM/yyyy");
        txtFromDate.setFont(Global.shortCutFont);

        txtToDate.setDateFormatString("dd/MM/yyyy");
        txtToDate.setFont(Global.shortCutFont);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtVouNo)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtRefrence)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEntry)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(txtRefrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEntry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtToDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFromDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtFromDate, txtRefrence, txtToDate, txtVouNo});

        tblJournal.setFont(Global.textFont);
        tblJournal.setModel(new javax.swing.table.DefaultTableModel(
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
        tblJournal.setName("tblJournal"); // NOI18N
        tblJournal.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblJournal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntryActionPerformed
        // TODO add your handling code here:
        openJournalEntryDialog("-");
    }//GEN-LAST:event_btnEntryActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }

    }//GEN-LAST:event_formComponentShown

    private void txtRefrenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefrenceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRefrenceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEntry;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblJournal;
    private com.toedter.calendar.JDateChooser txtFromDate;
    private javax.swing.JTextField txtRefrence;
    private com.toedter.calendar.JDateChooser txtToDate;
    private javax.swing.JTextField txtVouNo;
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

        if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        } else if (sourceObj instanceof JTextFieldDateEditor) {
            ctrlName = ((JTextFieldDateEditor) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtFromDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtFromDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtToDate.getDateEditor().getUiComponent().requestFocusInWindow();
                    searchGV();
                }
                tabToTable(e);
                break;
            case "txtToDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String date = ((JTextFieldDateEditor) sourceObj).getText();
                    if (date.length() == 8) {
                        String toFormatDate = Util1.toFormatDate(date);
                        txtToDate.setDate(Util1.toDate(toFormatDate, "dd/MM/yyyy"));
                    }
                    txtVouNo.requestFocus();
                    searchGV();
                }
                tabToTable(e);
                break;
            case "txtVouNo":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtRefrence.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtToDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                tabToTable(e);
                break;
            case "txtRefrence":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    btnEntry.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtVouNo.requestFocus();
                }
                tabToTable(e);
            case "btnEntry":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtFromDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    txtRefrence.requestFocus();
                }
                tabToTable(e);

                break;
            case "tblJournal":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtFromDate.getDateEditor().getUiComponent().requestFocusInWindow();
                }
                break;

        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblJournal.requestFocus();
            if (tblJournal.getRowCount() >= 0) {
                tblJournal.setRowSelectionInterval(0, 0);
            }
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (source.toString().equals("SEARCHVOUCHER")) {
            searchGV();
        }
    }

    @Override
    public void save() {
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
        searchGV();
    }

}
