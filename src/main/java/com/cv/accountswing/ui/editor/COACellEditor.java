/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.editor;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.service.COAService;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.TableCellEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class COACellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final Logger LOGGER = LoggerFactory.getLogger(COACellEditor.class);
    private JComponent component = null;
    private COAAutoCompleter completer;
    //private List<Medicine> listCOA = new ArrayList();

    public COACellEditor(final COAService service) {
        if (Global.listCOA == null) {
            Timer timer = new Timer(500, (ActionEvent e) -> {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        //LOGGER.info("start : " + new Date());
                        if (Global.listCOA == null) {
                            Global.listCOA = service.search("-", "-", "-", "-", "-", "-", "-");
                        }
                        //LOGGER.info("end : " + new Date());
                    }
                };
                thread.start();
                /*try {
                LOGGER.info("start : " + new Date());
                if (dao.getRowCount("select count(*) from item_type_mapping where group_id ="+ Global.loginUser.getUserId()) > 0) {
                Global.listCOA = dao.findAll("Medicine", "active = true and medTypeId.itemTypeCode in (select a.key.itemType.itemTypeCode from ItemTypeMapping a)");
                } else {
                Global.listCOA = dao.findAll("Medicine", "active = true");
                }
                LOGGER.info("end : " + new Date());
                } catch (Exception ex) {
                LOGGER.error("SaleTableCodeCellEditor : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                }*/
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    @Override
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        jtf.setFont(Global.textFont);
        //List<Medicine> listCOA = dao.findAll("Medicine", "active = true");

        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();

                if ((keyEvent.isControlDown() && (keyCode == KeyEvent.VK_F8))
                        || (keyEvent.isShiftDown() && (keyCode == KeyEvent.VK_F8))
                        || (keyCode == KeyEvent.VK_F5)
                        || (keyCode == KeyEvent.VK_F7)
                        || (keyCode == KeyEvent.VK_F9)
                        || (keyCode == KeyEvent.VK_F10)
                        || (keyCode == KeyEvent.VK_ESCAPE)) {
                    stopCellEditing();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }

            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }
        };

        jtf.addKeyListener(keyListener);
        component = jtf;
        if (value != null) {
            jtf.setText(value.toString());
            jtf.selectAll();
        }
        completer = new COAAutoCompleter(jtf, Global.listCOA, this);

        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj;
        ChartOfAccount coa = completer.getCOA();

        if (coa != null) {
            obj = coa;
        } else {
            obj = ((JTextField) component).getText();
        }

        return obj;

    }

    /*
     * To prevent mouse click cell editing and 
     * function key press
     */
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return false;
        } else if (anEvent instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) anEvent;

            if (ke.isActionKey()) {//Function key
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

}
