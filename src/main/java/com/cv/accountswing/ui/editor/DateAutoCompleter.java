/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.editor;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.DateModel;
import com.cv.accountswing.ui.dailog.DatePickerDialog;
import com.cv.accountswing.ui.cash.common.DateTableModel;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.util.Util1;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
import javax.swing.text.JTextComponent;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lenovo
 */
public class DateAutoCompleter implements KeyListener, SelectionObserver {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DateAutoCompleter.class);
    private JTable table = new JTable();
    private JPopupMenu popup = new JPopupMenu();
    private JTextComponent textComp;
    private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
    private DateTableModel dateTableModel;
    private DateModel dateModel;
    public AbstractCellEditor editor;
    private int x = 0;
    private boolean popupOpen = true;
    private DatePickerDialog datePickerDialog = new DatePickerDialog();
    private String stDate;
    private String endDate;
    private SelectionObserver selectionObserver;
    Image image = new ImageIcon(getClass().getResource("/images/date.png")).getImage();

    public void setSelectionObserver(SelectionObserver selectionObserver) {
        this.selectionObserver = selectionObserver;
    }

    public DateAutoCompleter() {
    }

    public DateAutoCompleter(JTextComponent comp, List<DateModel> list,
            AbstractCellEditor editor) {
        if (list == null) {
            list = generateDate();
        }
        this.textComp = comp;
        this.editor = editor;
        textComp.putClientProperty(AUTOCOMPLETER, this);
        textComp.setFont(Global.textFont);

        dateTableModel = new DateTableModel(list);
        table.setModel(dateTableModel);
        table.setTableHeader(null);
        table.setFont(Global.textFont); // NOI18N
        table.setRowHeight(Global.tblRowHeight);
        table.setDefaultRenderer(Object.class, new TableCellRender());
        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        table.setFocusable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);//Code

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    mouseSelect();
                }
            }
        });

        scroll.getVerticalScrollBar().setFocusable(false);
        scroll.getHorizontalScrollBar().setFocusable(false);

        popup.setBorder(BorderFactory.createLineBorder(Color.black));
        popup.setPopupSize(170, 400);

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
                textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
                popupOpen = false;
                if (!popupOpen) {
                    if (editor != null) {
                        editor.stopCellEditing();
                    }
                }

            }
        });

        table.setRequestFocusEnabled(false);

        if (list.size() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    public void mouseSelect() {
        if (table.getSelectedRow() != -1) {
            dateModel = dateTableModel.getDate(table.convertRowIndexToModel(
                    table.getSelectedRow()));
            ((JTextField) textComp).setText(dateModel.getText());
            search(dateModel);
        }
        popupOpen = false;
        popup.setVisible(false);
        if (editor != null) {
            editor.stopCellEditing();

        }
    }

    private void search(DateModel date) {
        if (date.getText().equals("Custom")) {
            datePickerDialog.setIconImage(image);
            datePickerDialog.setObserver(this);
            datePickerDialog.setLocationRelativeTo(null);
            datePickerDialog.setVisible(true);
        }
        if (!date.getText().equals("Custom")) {
            generateDate(date);
            selectionObserver.selected("Date", stDate + "to" + endDate);
        }

    }

    private void generateDate(DateModel date) {
        String text = date.getText();
        LocalDate localDate = LocalDate.now();
        if (text.equals("Today") || text.equals("-")) {
            stDate = Util1.toDateStr(Util1.getTodayDate(), "dd/MM/yyyy");
            endDate = stDate;
        } else if (text.equals("Yesterday")) {
            LocalDate minusDays = localDate.minusDays(1);
            Date yesterday = java.sql.Date.valueOf(minusDays);
            stDate = Util1.toDateStr(yesterday, "dd/MM/yyyy");
            endDate = stDate;

        } else if (text.equals("All")) {
            stDate = Global.finicialPeriodFrom;
            endDate = Global.finicialPeriodTo;
        } else if (text.contains("to")) {
            String[] split = text.split("to");
            stDate = split[0];
            endDate = split[1];
        } else {
            stDate = date.getStartDate();
            endDate = date.getEndDate();
        }
    }

    private Action acceptAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            mouseSelect();
        }
    };
    DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {

        }

        @Override
        public void removeUpdate(DocumentEvent e) {

        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    };

    public void closePopup() {
        popup.setVisible(false);
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
            DateAutoCompleter completer = (DateAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
            if (tf.isEnabled()) {
                if (completer.popup.isVisible()) {
                    completer.selectNextPossibleValue();
                } else {
                    popupOpen = true;
                    completer.showPopup();
                }
            }
        }
    };
    Action upAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            DateAutoCompleter completer = (DateAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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
            DateAutoCompleter completer = (DateAutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
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

    public DateModel getDate() {
        return dateModel;
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
        /*String filter = textComp.getText();
        
        if (filter.length() == 0) {
        sorter.setRowFilter(null);
        } else {
        //String value = Util1.getPropValue("system.iac.filter");
        
        if ("N".equals("Y")) {
        sorter.setRowFilter(RowFilter.regexFilter(filter));
        } else {
        sorter.setRowFilter(startsWithFilter);
        }
        try {
        if (e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_UP) {
        table.setRowSelectionInterval(0, 0);
        }
        } catch (Exception ex) {
        }
        }*/
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

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            if (source.toString().equals("DatePickerDialog")) {
                ((JTextField) textComp).setText(selectObj.toString());
                if (selectionObserver != null) {
                    selectionObserver.selected("Date", selectObj);
                    popup.setVisible(false);
                    if (editor != null) {
                        editor.stopCellEditing();

                    }
                }

            }
        }
    }

    private List<DateModel> generateDate() {
        Date finDate = Util1.toDate(Global.finicialPeriodFrom);
        LocalDate now = finDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        List<DateModel> listDateModel = new ArrayList<>();
        DateModel all = new DateModel();
        all.setText("All");
        listDateModel.add(all);
        listDateModel.add(new DateModel(getMonthShortName(now.getMonth()), now.getMonthValue(), now.getYear(), "Today"));
        DateModel date = new DateModel(getMonthShortName(now.getMonth()), now.getMonthValue(), now.getYear(), "Yesterday");
        date.setDay(now.getDayOfMonth() - 1);
        listDateModel.add(date);
        listDateModel.add(new DateModel(getMonthShortName(now.getMonth()), now.getMonthValue(), now.getYear(), "Custom"));

        int oddMonth = 12;
        /*for (int i = 1; i < minusMonth; i++) {
        LocalDate earlier = now.minusMonths(i);
        LocalDate monthBegin = earlier.withDayOfMonth(1);
        LocalDate monthEnd = earlier.plusMonths(1).withDayOfMonth(1).minusDays(1);
        Month month = earlier.getMonth();
        int value = earlier.getMonth().getValue();
        int year = earlier.getYear();
        DateModel date = new DateModel(getMonthShortName(month), value, year, "-");
        date.setStartDate(monthBegin.toString());
        date.setEndDate(monthEnd.toString());
        listDateModel.add(date);
        }*/
        for (int i = 0; i < oddMonth; i++) {
            LocalDate next = now.plusMonths(i);
            LocalDate monthBegin = next.withDayOfMonth(1);
            LocalDate monthEnd = next.plusMonths(1).withDayOfMonth(1).minusDays(1);
            Month month = next.getMonth();
            int value = next.getMonth().getValue();
            int year = next.getYear();
            DateModel d = new DateModel(getMonthShortName(month), value, year, "-");
            d.setStartDate(monthBegin.toString());
            d.setEndDate(monthEnd.toString());
            listDateModel.add(d);
        }

        Global.listDateModel = listDateModel;
        return listDateModel;
    }

    private String getMonthShortName(Month month) {

        String strMonth = month.toString();
        if (strMonth.length() >= 4) {
            strMonth = strMonth.substring(0, 3);
        }
        return strMonth;
    }

}
