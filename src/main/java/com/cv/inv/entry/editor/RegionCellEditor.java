/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry.editor;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.Region;
import com.cv.accountswing.service.RegionService;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class RegionCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegionCellEditor.class);
    private JComponent component = null;
    private RegionAutoCompleter completer;
    private final RegionService regionService;

    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
        }

        @Override
        public void focusGained(FocusEvent e) {
            JTextField jtf = (JTextField) e.getSource();
            jtf.setCaretPosition(jtf.getText().length());
        }

    };

    //private List<Medicine> listDepartment = new ArrayList();
    public RegionCellEditor(RegionService regionService) {
        this.regionService = regionService;
    }

    @Override
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        jtf.setFont(Global.textFont);
        jtf.setHighlighter(null);
        //List<Medicine> listDepartment = dao.findAll("Medicine", "active = true");
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
        jtf.addFocusListener(fa);
        component = jtf;
        if (value != null) {
            jtf.setText(value.toString());
            //.selectAll();
        }
        completer = new RegionAutoCompleter(jtf, Global.listRegion, this, regionService);

        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj;
        Region region = completer.getRegion();

        if (region != null) {
            obj = region;
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
