/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.editor;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.AppUser;
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
public class AppUserCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppUserCellEditor.class);
    private JComponent component = null;
    private AppUserAutoCompleter completer;

    //private List<Medicine> listTrader = new ArrayList();
    public AppUserCellEditor() {

    }

    @Override
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        JTextField jtf = new JTextField();
        jtf.setFont(Global.textFont);
        jtf.setHighlighter(null);

        //List<Medicine> listTrader = dao.findAll("Medicine", "active = true");
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
        }
        completer = new AppUserAutoCompleter(jtf, Global.listAppUser, this);

        return component;
    }

    @Override
    public Object getCellEditorValue() {
        Object obj;
        AppUser appUser = completer.getAppUser();

        if (appUser != null) {
            obj = appUser;
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
