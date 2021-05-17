/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.common.PanelControl;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.setup.common.COAGroupChildTableModel;
import com.cv.accountswing.ui.setup.common.COAGroupTableModel;
import com.cv.accountswing.ui.setup.common.COAHeadTableModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author MyoGyi
 */
@Component
public class COASetup extends javax.swing.JPanel implements KeyListener, PanelControl {

    private int selectRow = -1;
    @Autowired
    private COAService coaService;
    @Autowired
    private COAHeadTableModel coaHeadTableModel;
    @Autowired
    private COAGroupTableModel coaGroupTableModel;
    @Autowired
    private COAGroupChildTableModel cOAGroupChildTableModel;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationMainFrame mainFrame;
    private LoadingObserver loadingObserver;
    private boolean isShown = false;
    private TableFilterHeader filterHeader;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    /**
     * Creates new form CoaSetup
     */
    public COASetup() {
        initComponents();
        //ActionMapping();

    }

    private void initMain() {
        initKeyListener();
        initTable();
        isShown = true;
    }

    private void initTable() {
        tblCOAHead();
        tblCOAGroup();
        tblCOA();
    }

    private void tblCOAHead() {
        tblCoaHead.setModel(coaHeadTableModel);
        tblCoaHead.getTableHeader().setFont(Global.tblHeaderFont);
        tblCoaHead.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblCoaHead.getTableHeader().setForeground(ColorUtil.foreground);
        tblCoaHead.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblCoaHead.getSelectedRow() >= 0) {
                    selectRow = tblCoaHead.convertRowIndexToModel(tblCoaHead.getSelectedRow());
                    getCOAGroup(selectRow);
                }
            }
        });
        tblCoaHead.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCoaHead.getColumnModel().getColumn(0).setPreferredWidth(20);// Code
        tblCoaHead.getColumnModel().getColumn(1).setPreferredWidth(400);// Name
        tblCoaHead.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblCoaHead.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblCoaHead.setDefaultRenderer(Object.class, new TableCellRender());
        tblCoaHead.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        filterHeader = new TableFilterHeader(tblCoaHead, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);
        searchHead();
    }

    private void searchHead() {
        List<ChartOfAccount> listCOA = coaService.getParent(Global.compCode);
        coaHeadTableModel.setlistCoaHead(listCOA);

    }

    private void tblCOAGroup() {
        tblCoaGroup.setCellSelectionEnabled(true);
        tblCoaGroup.getTableHeader().setFont(Global.tblHeaderFont);
        tblCoaGroup.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblCoaGroup.getTableHeader().setForeground(ColorUtil.foreground);
        tblCoaGroup.setModel(coaGroupTableModel);
        tblCoaGroup.getTableHeader().setFont(Global.textFont);
        coaGroupTableModel.setParent(tblCoaGroup);
        tblCoaGroup.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblCoaGroup.getSelectedRow() >= 0) {
                    selectRow = tblCoaGroup.convertRowIndexToModel(tblCoaGroup.getSelectedRow());
                    getCOAGroupChild(selectRow);
                }
            }
        });
        tblCoaGroup.getColumnModel().getColumn(0).setPreferredWidth(10);// Sys Code
        tblCoaGroup.getColumnModel().getColumn(1).setPreferredWidth(20);// Usr Code
        tblCoaGroup.getColumnModel().getColumn(2).setPreferredWidth(400);// Name
        tblCoaGroup.getColumnModel().getColumn(3).setPreferredWidth(10);// Active
        tblCoaGroup.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblCoaGroup.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCoaGroup.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblCoaGroup.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCoaGroup.setDefaultRenderer(Object.class, new TableCellRender());
        tblCoaGroup.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        filterHeader = new TableFilterHeader(tblCoaGroup, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);

    }

    private void tblCOA() {
        tblCOAGroupChild.setCellSelectionEnabled(true);
        tblCOAGroupChild.getTableHeader().setFont(Global.tblHeaderFont);
        tblCOAGroupChild.setModel(cOAGroupChildTableModel);
        tblCOAGroupChild.getTableHeader().setFont(Global.textFont);
        tblCOAGroupChild.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblCOAGroupChild.getTableHeader().setForeground(ColorUtil.foreground);
        cOAGroupChildTableModel.setParent(tblCOAGroupChild);
        tblCOAGroupChild.getColumnModel().getColumn(0).setPreferredWidth(10);// Sys Code
        tblCOAGroupChild.getColumnModel().getColumn(1).setPreferredWidth(20);// Usr Code
        tblCOAGroupChild.getColumnModel().getColumn(2).setPreferredWidth(400);// Name
        tblCOAGroupChild.getColumnModel().getColumn(3).setPreferredWidth(10);// Active
        tblCOAGroupChild.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        tblCOAGroupChild.getColumnModel().getColumn(1).setCellEditor(new AutoClearEditor());
        tblCOAGroupChild.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblCOAGroupChild.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCOAGroupChild.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCOAGroupChild.setDefaultRenderer(Object.class, new TableCellRender());
        tblCOAGroupChild.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        filterHeader = new TableFilterHeader(tblCOAGroupChild, AutoChoices.ENABLED);
        filterHeader.setPosition(TableFilterHeader.Position.TOP);
        filterHeader.setFont(Global.textFont);
        filterHeader.setVisible(false);

    }

    private void getCOAGroup(int row) {
        clear();
        ChartOfAccount c = coaHeadTableModel.getChartOfAccount(row);
        if (c.getCode() != null) {
            List<ChartOfAccount> listCoaGroup = coaService.search("-", "-", Global.compCode, "-", c.getCode(), "-", "-");
            coaGroupTableModel.setCoaHeadCode(c.getCode());
            coaGroupTableModel.setListCOA(listCoaGroup);
            coaGroupTableModel.addEmptyRow();
            lblCoaGroup.setText(c.getCoaNameEng());
            reqCoaGroup();
        }
    }

    private void reqCoaGroup() {
        int row = tblCoaGroup.getRowCount();
        if (row >= 0) {
            tblCoaGroup.setRowSelectionInterval(row - 1, row - 1);
            tblCoaGroup.setColumnSelectionInterval(1, 1);
            tblCoaGroup.requestFocus();
        }
    }

    private void getCOAGroupChild(int row) {
        ChartOfAccount coa = coaGroupTableModel.getChartOfAccount(row);
        if (coa.getCode() != null) {
            List<ChartOfAccount> listCoaChild = coaService.search("-", "-", Global.compCode, "-", coa.getCode(), "-", "-");
            cOAGroupChildTableModel.setListCOA(listCoaChild);
            cOAGroupChildTableModel.setCoaGroupCode(coa.getCode());
            cOAGroupChildTableModel.addEmptyRow();
            lblCoaChild.setText(coa.getCoaNameEng());
            reqCOAGroupChild();
        } else {
            cOAGroupChildTableModel.clear();
            lblCoaChild.setText("...");
        }
    }

    private void reqCOAGroupChild() {
        int row = tblCOAGroupChild.getRowCount();
        if (row >= 0) {
            tblCOAGroupChild.setRowSelectionInterval(row - 1, row - 1);
            tblCOAGroupChild.setColumnSelectionInterval(1, 1);
            tblCOAGroupChild.requestFocus();
        }
    }

    private void clear() {
        coaGroupTableModel.clear();
        cOAGroupChildTableModel.clear();
        lblCoaChild.setText("...");
        lblCoaGroup.setText("...");
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
        tblCoaHead = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCoaGroup = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCOAGroupChild = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        lblCoaChild = new javax.swing.JLabel();
        lblCoaGroup = new javax.swing.JLabel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCoaHead.setFont(Global.textFont);
        tblCoaHead.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCoaHead.setName("tblCoaHead"); // NOI18N
        tblCoaHead.setRowHeight(Global.tblRowHeight);
        tblCoaHead.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblCoaHeadKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblCoaHead);

        tblCoaGroup.setFont(Global.textFont);
        tblCoaGroup.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCoaGroup.setName("tblCoaGroup"); // NOI18N
        tblCoaGroup.setRowHeight(Global.tblRowHeight);
        tblCoaGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblCoaGroupKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblCoaGroup);

        tblCOAGroupChild.setFont(Global.textFont);
        tblCOAGroupChild.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCOAGroupChild.setName("tblCOAGroupChild"); // NOI18N
        tblCOAGroupChild.setRowHeight(Global.tblRowHeight);
        jScrollPane3.setViewportView(tblCOAGroupChild);

        jLabel1.setFont(Global.menuFont);
        jLabel1.setText("Account Head");

        lblCoaChild.setFont(Global.menuFont);
        lblCoaChild.setText("...");

        lblCoaGroup.setFont(Global.menuFont);
        lblCoaGroup.setText("...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(4, 4, 4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblCoaGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCoaChild, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblCoaGroup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblCoaChild, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addGap(10, 10, 10))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, lblCoaChild, lblCoaGroup});

    }// </editor-fold>//GEN-END:initComponents

    private void tblCoaGroupKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCoaGroupKeyPressed

    }//GEN-LAST:event_tblCoaGroupKeyPressed

    private void tblCoaHeadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCoaHeadKeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            /* showCoaGroup(tblCoaHead.convertRowIndexToModel(tblCoaHead.getSelectedRow()));
            tblCoaGroup.requestFocus();
            tblCoaGroup.setRowSelectionInterval(0, 0);*/
        }
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            /*showCoaGroup(tblCoaHead.convertRowIndexToModel(tblCoaHead.getSelectedRow()));*/

        }
    }//GEN-LAST:event_tblCoaHeadKeyReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCoaChild;
    private javax.swing.JLabel lblCoaGroup;
    private javax.swing.JTable tblCOAGroupChild;
    private javax.swing.JTable tblCoaGroup;
    private javax.swing.JTable tblCoaHead;
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

        }
        switch (ctrlName) {
            case "tblCoaHead":
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    int rowHead = tblCoaHead.convertRowIndexToModel(tblCoaHead.getSelectedRow());
                    getCOAGroup(rowHead);
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    tblCoaGroup.requestFocus();
                    if (tblCoaGroup.getRowCount() >= 0) {
                        tblCoaGroup.setRowSelectionInterval(0, 0);
                        tblCoaGroup.setColumnSelectionInterval(1, 1);
                    }
                }
                break;
            case "tblCoaGroup":
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    int row = tblCoaGroup.convertRowIndexToModel(tblCoaGroup.getSelectedRow());
                    getCOAGroupChild(row);
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    tblCOAGroupChild.requestFocus();
                    if (tblCOAGroupChild.getRowCount() >= 0) {
                        tblCOAGroupChild.setRowSelectionInterval(0, 0);
                        tblCOAGroupChild.setColumnSelectionInterval(1, 1);
                    }
                }
                break;
            case "tblCOAGroupChild":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    tblCoaHead.requestFocus();
                    tblCoaHead.requestFocus();
                    if (tblCoaHead.getRowCount() >= 0) {
                        tblCoaHead.setRowSelectionInterval(0, 0);
                        tblCoaHead.setColumnSelectionInterval(1, 1);
                    }
                }
                break;
            default:
                break;

        }
    }

    private void initKeyListener() {
        tblCoaHead.addKeyListener(this);
        tblCoaGroup.addKeyListener(this);
        tblCOAGroupChild.addKeyListener(this);
    }

    @Override
    public void save() {
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
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
        searchHead();
    }

}
