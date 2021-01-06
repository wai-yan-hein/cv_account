/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.entry;

import com.cv.accountswing.common.ColorUtil;
import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.SelectionObserver;
import com.cv.accountswing.entity.Customer;
import com.cv.accountswing.service.CustomerService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.inv.entry.common.CustomerGridTabelModel;
import com.cv.inv.entry.dialog.OrderDialog;
import com.cv.inv.setup.dialog.CustomerOrderSetup;
import java.awt.Image;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
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
public class CustomerGrid extends javax.swing.JPanel {

    private static final Logger log = LoggerFactory.getLogger(CustomerGrid.class);

    private final Image orderImage = new ImageIcon(this.getClass().getResource("/images/purchase_order_26px.png")).getImage();
    private final Image cusImage = new ImageIcon(this.getClass().getResource("/images/add_user_male_26px.png")).getImage();
    private final ImageIcon phoneIcon = new ImageIcon(this.getClass().getResource("/images/ringer_volume_30px.png"));

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerGridTabelModel gridTabelModel;
    @Autowired
    private OrderDialog orderDialog;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private CustomerOrderSetup customerOrderSetup;
    private TableRowSorter<TableModel> sorter;
    private boolean isShown = false;
    private SelectionObserver observer;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    /**
     * Creates new form CustomerGrid
     */
    public CustomerGrid() {
        initComponents();
    }

    public void initMain() {
        if (!isShown) {
            initTable();
            searchCustomerList();
            isShown = true;
        }
    }

    private void initTable() {
        tblCustomerList.setModel(gridTabelModel);
        tblCustomerList.getTableHeader().setFont(Global.tblHeaderFont);
        tblCustomerList.getTableHeader().setBackground(ColorUtil.btnEdit);
        tblCustomerList.getTableHeader().setForeground(ColorUtil.foreground);
        tblCustomerList.getColumnModel().getColumn(0).setPreferredWidth(10);
        tblCustomerList.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblCustomerList.getColumnModel().getColumn(2).setPreferredWidth(300);
        tblCustomerList.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblCustomerList.getColumnModel().getColumn(2).setPreferredWidth(300);
        tblCustomerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblCustomerList.setDefaultRenderer(Object.class, new TableCellRender());
        sorter = new TableRowSorter(tblCustomerList.getModel());
        tblCustomerList.setRowSorter(sorter);
        txtPhone.requestFocus();

    }

    private void searchCustomerList() {
        List<Customer> listCustomer = customerService.search("-", "-", "-", "-", Global.compCode);
        gridTabelModel.setListCustomer(listCustomer);
    }

    private void select() {
        if (tblCustomerList.getSelectedRow() >= 0) {
            int row = tblCustomerList.convertRowIndexToModel(tblCustomerList.getSelectedRow());
            Customer customer = gridTabelModel.getCustomer(row);
            orderDialog.setIconImage(orderImage);
            orderDialog.setCustomer(customer);
            orderDialog.initMain();
            orderDialog.setSize(Global.width - 400, Global.height - 400);
            orderDialog.setLocationRelativeTo(null);
            orderDialog.setVisible(true);
        }
    }

    private void newCusomer() {
        customerOrderSetup.setIconImage(cusImage);
        customerOrderSetup.initAutoCompleter();
        customerOrderSetup.setLocationRelativeTo(null);
        customerOrderSetup.setVisible(true);
    }
    private final RowFilter<Object, Object> nameFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String text = txtName.getText().toUpperCase();
            String tmp1 = entry.getValue(1).toString().toUpperCase();
            return tmp1.startsWith(text);
        }

    };
    private final RowFilter<Object, Object> phoneFilter = new RowFilter<Object, Object>() {
        @Override
        public boolean include(RowFilter.Entry<? extends Object, ? extends Object> entry) {
            String text = txtPhone.getText().toUpperCase();
            String tmp1 = entry.getValue(2).toString().toUpperCase();
            tmp1 = tmp1.replaceAll("[-+^]*", "");
            boolean contains = tmp1.contains(text);
            return contains;

        }
    };

    public void setPhoneNumber(String phoneNo) {
        if (isShown) {
            log.info("Message Recieved.");
            if (!txtPhone.getText().equals(phoneNo)) {
                int confirm = JOptionPane.showConfirmDialog(Global.parentForm, "Do you want to search customer info.",
                        "Phone call received", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, phoneIcon);
                if (confirm == JOptionPane.YES_OPTION) {
                    txtPhone.setText(phoneNo);
                    sorter.setRowFilter(phoneFilter);
                }
            }
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCustomerList = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtPhone = new javax.swing.JTextField();
        btnNewCustomer = new javax.swing.JButton();
        txtName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setName(""); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        tblCustomerList.setFont(Global.textFont);
        tblCustomerList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {}
            },
            new String [] {

            }
        ));
        tblCustomerList.setRowHeight(Global.tblRowHeight);
        tblCustomerList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomerListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCustomerList);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                .addContainerGap())
        );

        txtPhone.setFont(Global.textFont);
        txtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPhoneKeyTyped(evt);
            }
        });

        btnNewCustomer.setBackground(ColorUtil.btnEdit);
        btnNewCustomer.setFont(Global.lableFont);
        btnNewCustomer.setForeground(ColorUtil.foreground);
        btnNewCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-button.png"))); // NOI18N
        btnNewCustomer.setText("New Customer");
        btnNewCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewCustomerActionPerformed(evt);
            }
        });

        txtName.setFont(Global.textFont);
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNameKeyTyped(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Name Search");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Phone Search");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtName)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(txtPhone)
                .addGap(18, 18, 18)
                .addComponent(btnNewCustomer)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNewCustomer)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtPhoneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneKeyTyped
        // TODO add your handling code here:
        sorter.setRowFilter(phoneFilter);

    }//GEN-LAST:event_txtPhoneKeyTyped

    private void tblCustomerListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCustomerListMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            select();
        }
    }//GEN-LAST:event_tblCustomerListMouseClicked

    private void btnNewCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewCustomerActionPerformed
        // TODO add your handling code here:
        newCusomer();
    }//GEN-LAST:event_btnNewCustomerActionPerformed

    private void txtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyTyped
        // TODO add your handling code here:
        sorter.setRowFilter(nameFilter);
    }//GEN-LAST:event_txtNameKeyTyped

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        txtName.requestFocus();
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNewCustomer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCustomerList;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone;
    // End of variables declaration//GEN-END:variables
}
