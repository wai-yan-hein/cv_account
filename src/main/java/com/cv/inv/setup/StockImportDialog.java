/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.setup;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.Font;
import com.cv.accountswing.service.FontService;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockType;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entity.UnitPattern;
import com.cv.inv.service.StockService;
import com.cv.inv.setup.dialog.common.StockImportTableModel;
import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
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
public class StockImportDialog extends javax.swing.JDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockImportDialog.class);
    @Autowired
    private StockImportTableModel importTableModel;
    @Autowired
    private FontService fontService;
    @Autowired
    private StockService stockService;
    @Autowired
    private TaskExecutor taskExecutor;
    private final List<Stock> listStock = new ArrayList<>();
    private final HashMap<Integer, Integer> hmZG = new HashMap<>();
    private List<Font> listFont;
    private final ImageIcon loadingIcon = new ImageIcon(this.getClass().getResource("/images/process.gif"));
    private String traderType;

    public String getTraderType() {
        return traderType;
    }

    public void setTraderType(String traderType) {
        this.traderType = traderType;
    }

    /**
     * Creates new form CustomerImportDialog
     */
    public StockImportDialog() {
        super(Global.parentForm, true);
        initComponents();
    }

    private void initFont() {
        listFont = fontService.getAll();

    }

    private void initMain() {
        tblCustomer.setModel(importTableModel);
        tblCustomer.setRowHeight(Global.tblRowHeight);
        tblCustomer.getTableHeader().setFont(Global.tblHeaderFont);
        if (listFont == null) {
            initFont();
        }
    }

    private void chooseFile() {
        FileDialog dialog = new FileDialog(Global.parentForm, "Choose CSV File", FileDialog.LOAD);
        dialog.setDirectory("D:\\");
        dialog.setFile(".csv");
        dialog.setVisible(true);
        String directory = dialog.getFile();
        LOGGER.info("File Path :" + directory);
        if (directory != null) {
            readFile(dialog.getDirectory() + "\\" + directory);
        }

    }

    private void readFile(String path) {
        String line;
        String splitBy = ",";
        int lineCount = 0;

        try {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "UTF8"))) {
                while ((line = br.readLine()) != null) //returns a Boolean value
                {
                    Stock stock = new Stock();
                    String[] data = line.split(splitBy);    // use comma as separator
                    String code = null;
                    String stockCode = null;
                    String name = null;
                    String stockWt = null;
                    String stockTypeId = null;
                    String patternId = null;
                    lineCount++;
                    try {
                        code = data[0].replace("\"", "");
                        stockCode = data[1].replace("\"", "");
                        name = data[2].replace("\"", "");
                        stockWt = data[3];
                        stockTypeId = data[4].replace("\"", "");
                        patternId = data[5].replace("\"", "");
                    } catch (IndexOutOfBoundsException e) {
                        JOptionPane.showMessageDialog(Global.parentForm, "FORMAT ERROR IN LINE:" + lineCount);
                    }

                    stock.setMigCode(code);
                    stock.setBarcode(stockCode);
                    stock.setStockName(name);
                    stock.setStockType(new StockType(stockTypeId, "-"));
                    stock.setSaleWeight(Util1.getFloat(stockWt));
                    stock.setSaleUnit(new StockUnit("p"));
                    stock.setPurWeight(Util1.getFloat(stockWt));
                    stock.setPurUnit(new StockUnit("p"));
                    stock.setPattern(new UnitPattern(patternId));
                    stock.setCompCode(Global.compCode);
                    stock.setCreatedBy(Global.loginUser);
                    stock.setCreatedDate(Util1.getTodayDate());
                    stock.setIsActive(Boolean.TRUE);
                    listStock.add(stock);

                }
            }
            if (chkIntegra.isSelected()) {
                toZawgyiFont();
            }
            importTableModel.setListStock(listStock);
        } catch (IOException e) {
            LOGGER.error("Read CSV File :" + e.getMessage());

        }
    }

    private void toZawgyiFont() {
        if (listFont != null) {
            listFont.forEach(f -> {
                hmZG.put(f.getIntegraKeyCode(), f.getKey().getZawgyiKeyCode());
            });
        }
        LOGGER.info("Integra Font to Zawgyi Text");
        listStock.forEach(cus -> {
            cus.setStockName(getZawgyiText(cus.getStockName()));
        });
    }

    private String getZawgyiText(String text) {
        String tmpStr = "";

        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                String tmpS = Character.toString(text.charAt(i));
                int tmpChar = (int) text.charAt(i);

                if (hmZG.containsKey(tmpChar)) {
                    char tmpc = (char) hmZG.get(tmpChar).intValue();
                    if (tmpStr.isEmpty()) {
                        tmpStr = Character.toString(tmpc);
                    } else {
                        tmpStr = tmpStr + Character.toString(tmpc);
                    }
                } else if (tmpS.equals("ƒ")) {
                    if (tmpStr.isEmpty()) {
                        tmpStr = "ႏ";
                    } else {
                        tmpStr = tmpStr + "ႏ";
                    }
                } else if (tmpStr.isEmpty()) {
                    tmpStr = tmpS;
                } else {
                    tmpStr = tmpStr + tmpS;
                }
            }
        }

        return tmpStr;
    }

    private void saveCustomer() {
        JDialog loading = Util1.getLoading(this, loadingIcon);
        taskExecutor.execute(() -> {
            importTableModel.getListStock().forEach(stock -> {
                stockService.save(stock, "NEW");
            });
            importTableModel.clear();
            loading.setVisible(false);
        });
        loading.setVisible(true);

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
        tblCustomer = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        chkIntegra = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tblCustomer.setFont(Global.textFont);
        tblCustomer.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblCustomer);

        jButton1.setBackground(ColorUtil.mainColor);
        jButton1.setFont(Global.lableFont);
        jButton1.setForeground(ColorUtil.foreground);
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(ColorUtil.btnEdit);
        jButton2.setFont(Global.lableFont);
        jButton2.setForeground(ColorUtil.foreground);
        jButton2.setText("Choose File");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        chkIntegra.setText("Integra Font");

        jButton3.setBackground(ColorUtil.btnEdit);
        jButton3.setFont(Global.lableFont);
        jButton3.setForeground(ColorUtil.foreground);
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clear-button-white.png"))); // NOI18N
        jButton3.setText("Clear");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(chkIntegra)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(chkIntegra)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        chooseFile();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        initMain();
    }//GEN-LAST:event_formComponentShown

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        importTableModel.clear();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        saveCustomer();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkIntegra;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCustomer;
    // End of variables declaration//GEN-END:variables
}