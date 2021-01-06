/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup.dialog;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.StartWithRowFilter;
import com.cv.accountswing.ui.cash.common.AutoClearEditor;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.inv.entity.UnitPattern;
import com.cv.inv.entity.UnitRelation;
import com.cv.inv.entry.editor.StockUnitEditor;
import com.cv.inv.service.RelationService;
import com.cv.inv.service.UnitPatternService;
import com.cv.inv.setup.dialog.common.RelationTableModel;
import com.cv.inv.setup.dialog.common.UnitPatternTableModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class RelationSetupDialog extends javax.swing.JDialog implements KeyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationSetupDialog.class);
    @Autowired
    private RelationTableModel relationTableModel;
    @Autowired
    private UnitPatternTableModel patternTableModel;
    @Autowired
    private UnitPatternService patternService;
    @Autowired
    private RelationService relationService;
    private TableRowSorter<TableModel> sorter;
    private StartWithRowFilter swrf;
    private int selectRow = -1;

    /**
     * Creates new form ItemTypeSetupDialog
     */
    public RelationSetupDialog() {
        super(Global.parentForm, true);
        initComponents();
    }

    public void initMain() {
        initTable();
        searchRelation();
        initCombo();
    }

    private void initCombo() {
    }

    private void searchRelation() {
        //relationTableModel.setListRelation();
    }

    private void initTable() {
        initTablePattern();
        tblRelation.setModel(relationTableModel);
        relationTableModel.setParent(tblRelation);
        tblRelation.getTableHeader().setFont(Global.lableFont);
        tblRelation.getTableHeader().setBackground(ColorUtil.tblHeaderColor);
        tblRelation.getTableHeader().setForeground(ColorUtil.foreground);
        tblRelation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRelation.getColumnModel().getColumn(0).setCellEditor(new StockUnitEditor());
        tblRelation.getColumnModel().getColumn(1).setCellEditor(new StockUnitEditor());
        tblRelation.getColumnModel().getColumn(2).setCellEditor(new AutoClearEditor());
        tblRelation.setDefaultRenderer(Double.class, new TableCellRender());
        tblRelation.setDefaultRenderer(Object.class, new TableCellRender());
        tblRelation.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblRelation.getSelectedRow() >= 0) {
                }
            }
        });
        tblRelation.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        swrf = new StartWithRowFilter(txtFilter);
        sorter = new TableRowSorter(tblRelation.getModel());
        tblRelation.setRowSorter(sorter);

    }

    private void initTablePattern() {
        tblPattern.setModel(patternTableModel);
        tblPattern.getTableHeader().setFont(Global.tblHeaderFont);
        tblPattern.getTableHeader().setBackground(ColorUtil.btnEdit);
        tblPattern.getTableHeader().setForeground(ColorUtil.foreground);
        tblPattern.setDefaultRenderer(Object.class, new TableCellRender());
        tblPattern.getColumnModel().getColumn(0).setCellEditor(new AutoClearEditor());
        patternTableModel.setListCategory(patternService.findAll());
        tblPattern.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblPattern.getSelectedRow() >= 0) {
                    selectRow = tblPattern.convertRowIndexToModel(tblPattern.getSelectedRow());
                    UnitPattern pattern = patternTableModel.getPattern(selectRow);
                    relationTableModel.setPatternId(pattern.getPatternCode());
                    searchUnitRelation(pattern.getPatternCode());
                }
            }
        });

    }

    private void foucsRelationTable() {
        int row = relationTableModel.getListRelation().size();
        tblRelation.setColumnSelectionInterval(0, 0);
        tblRelation.setRowSelectionInterval(row - 1, row - 1);
    }

    private void searchUnitRelation(String patternId) {
        if (patternId != null) {
            List<UnitRelation> listRelation = relationService.search(patternId);
            relationTableModel.setListRelation(listRelation);
            relationTableModel.addNewRow();
            foucsRelationTable();
        } else {
            relationTableModel.clear();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblRelation = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPattern = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Relation Setup");
        setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);

        tblRelation.setFont(Global.textFont);
        tblRelation.setModel(new javax.swing.table.DefaultTableModel(
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
        tblRelation.setName("tblRelation"); // NOI18N
        tblRelation.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblRelation);

        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        tblPattern.setFont(Global.textFont);
        tblPattern.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblPattern.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblPattern);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        // TODO add your handling code here:

        if (txtFilter.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(swrf);
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblPattern;
    private javax.swing.JTable tblRelation;
    private javax.swing.JTextField txtFilter;
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
        }
        switch (ctrlName) {

            case "txtName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                tabToTable(e);

                break;

            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                tabToTable(e);

                break;
            case "btnDelete":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                tabToTable(e);

                break;
            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                tabToTable(e);

                break;
        }
    }

    private void tabToTable(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblRelation.requestFocus();
            if (tblRelation.getRowCount() >= 0) {
                tblRelation.setRowSelectionInterval(0, 0);
            }
        }
    }
}
