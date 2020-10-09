/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.editor;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.ui.cash.common.TraderTableModel;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class TraderAutoCompleter implements KeyListener {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(TraderAutoCompleter.class);
    private JTable table = new JTable();
    private JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private TraderTableModel traderTableModel;
    private Trader trader;
    public AbstractCellEditor editor;
    private TableRowSorter<TableModel> sorter;
    private int x = 0;
    private boolean popupOpen = false;
    private SelectionObserver selectionObserver;

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public TraderAutoCompleter() {
    }

    public TraderAutoCompleter(JTextComponent comp, List<Trader> list,
            AbstractCellEditor editor) {
        this.textComp = comp;
        this.editor = editor;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.setFont(Global.textFont);
        traderTableModel = new TraderTableModel(list);
        table.setModel(traderTableModel);
        table.setSize(50, 50);
        table.getTableHeader().setFont(Global.textFont);
        table.setFont(Global.textFont); // NOI18N
        table.setRowHeight(Global.tblRowHeight);
        sorter = new TableRowSorter(table.getModel());
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(10);//Code
        table.getColumnModel().getColumn(1).setPreferredWidth(30);//Name

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    mouseSelect();
                }
            }
        });

        scroll.getVerticalScrollBar().setFocusable(false);
        scroll.getHorizontalScrollBar().setFocusable(false);

        popup.setBorder(BorderFactory.createLineBorder(Color.black));
        popup.setPopupSize(600, 300);

        popup.add(scroll);

        if (textComp instanceof JTextField) {
            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                    JComponent.WHEN_FOCUSED);
            textComp.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    popupOpen = true;
                    showPopup();
                }

            });
            textComp.getDocument().addDocumentListener(documentListener);
        } else {
            textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
                    KeyEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);
        }

        textComp.registerKeyboardAction(upAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                JComponent.WHEN_FOCUSED);
        textComp.registerKeyboardAction(hidePopupAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);

        popup.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
                popupOpen = false;

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        table.setRequestFocusEnabled(false);

        if (list.size() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            trader = traderTableModel.getTrader(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            ((JTextField) textComp).setText(trader.getTraderName());
            if (editor == null) {
                if (selectionObserver != null) {
                    selectionObserver.selected("Trader", trader.getTraderName());
                }
            }
        }

        popup.setVisible(false);
        popupOpen = false;
        if (editor != null) {
            editor.stopCellEditing();
        }
    }

    private Action acceptAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            mouseSelect();
            /* JComponent tf = (JComponent) e.getSource();
            TraderAutoCompleter completer = (TraderAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            
            if (completer.table.getSelectedRow() != -1) {
            trader = traderTableModel.getTrader(completer.table.convertRowIndexToModel(
            completer.table.getSelectedRow()));
            ((JTextField) completer.textComp).setText(trader.getTraderName());
            }
            
            completer.popup.setVisible(false);
            if (editor != null) {
            editor.stopCellEditing();
            }*/
        }
    };
    DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            if (editor != null) {
                popupOpen = true;
                showPopup();
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (editor != null) {
                popupOpen = true;
                showPopup();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    };

    public void closePopup() {
        popup.setVisible(false);
        popupOpen = false;
    }

    public void showPopup() {
        if (popupOpen) {
            if (!popup.isVisible()) {
                textComp.addKeyListener(this);
                //popup.setVisible(false); 
                if (textComp.isEnabled()) {
                    if (textComp instanceof JTextField) {
                        textComp.getDocument().addDocumentListener(documentListener);
                    }

                    textComp.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                            JComponent.WHEN_FOCUSED);
                    if (x == 0) {
                        x = textComp.getCaretPosition();
                    }

                    popup.show(textComp, x, textComp.getHeight());
                    log.info("Show Popup...");
                    popupOpen = false;

                } else {
                    popup.setVisible(false);
                    popupOpen = false;
                }
            }
        }
        textComp.requestFocus();
    }
    Action showAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            TraderAutoCompleter completer = (TraderAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectNextPossibleValue();
                } else {
                    if (!popupOpen) {
                        popupOpen = true;
                        completer.showPopup();
                    }

                }
            }
        }
    };
    static Action upAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            TraderAutoCompleter completer = (TraderAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectPreviousPossibleValue();
                }
            }
        }
    };
    Action hidePopupAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            TraderAutoCompleter completer = (TraderAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                completer.popup.setVisible(false);
                popupOpen = false;
            }
        }
    };

    protected void selectNextPossibleValue() {
        int si = table.getSelectedRow();

        if (si < table.getRowCount() - 1) {
            try {
                table.setRowSelectionInterval(si + 1, si + 1);
            } catch (Exception ex) {

            }
        }

        Rectangle rect = table.getCellRect(table.getSelectedRow(), 0, true);
        table.scrollRectToVisible(rect);
    }

    /**
     * Selects the previous item in the list. It won't change the selection if
     * the currently selected item is already the first item.
     */
    protected void selectPreviousPossibleValue() {
        int si = table.getSelectedRow();

        if (si > 0) {
            try {
                table.setRowSelectionInterval(si - 1, si - 1);
            } catch (Exception ex) {

            }
        }

        Rectangle rect = table.getCellRect(table.getSelectedRow(), 0, true);
        table.scrollRectToVisible(rect);
    }

    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
        this.textComp.setText(trader.getTraderName());

    }

    /*
     * KeyListener implementation
     */
    /**
     * Handle the key typed event from the text field.
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Handle the key-pressed event from the text field.
     */
    @Override
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Handle the key-released event from the text field.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        String filter = textComp.getText();
        if (filter.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            //String value = Util1.getPropValue("system.iac.filter");

            if ("N".equals("Y")) {
                sorter.setRowFilter(RowFilter.regexFilter(filter));
            } else {
                sorter.setRowFilter(startsWithFilter);
            }
            if (e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_UP) {
                if (table.getSelectedRow() >= 0) {
                    table.setRowSelectionInterval(0, 0);
                }
            }

        }
    }
    private final RowFilter<Object, Object> startsWithFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            //for (int i = entry.getValueCount() - 1; i >= 0; i--) {
            /*
             * if (NumberUtil.isNumber(textComp.getText())) { if
             * (entry.getStringValue(0).toUpperCase().startsWith(
             * textComp.getText().toUpperCase())) { return true; } } else {
             *
             * if (entry.getStringValue(1).toUpperCase().contains(
             * textComp.getText().toUpperCase())) { return true; } else if
             * (entry.getStringValue(2).toUpperCase().contains(
             * textComp.getText().toUpperCase())) { return true; }
             }
             */

            String tmp1 = entry.getStringValue(0).toUpperCase();
            String tmp2 = entry.getStringValue(1).toUpperCase();
            String tmp3 = entry.getStringValue(3).toUpperCase();
            String tmp4 = entry.getStringValue(4).toUpperCase();
            String text = textComp.getText().toUpperCase();

            return tmp1.startsWith(text) || tmp2.startsWith(text) || tmp3.startsWith(text) || tmp4.startsWith(text);
        }
    };
}
