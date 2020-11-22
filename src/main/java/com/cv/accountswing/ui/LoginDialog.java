/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.entity.AppUser;
import com.cv.accountswing.service.UserService;
import com.cv.accountswing.util.Util1;
import com.cv.inv.entity.MachineInfo;
import com.cv.inv.service.MachineInfoService;
import java.awt.HeadlessException;
//import com.cv.accounts.service.UserService;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author winswe
 */
@Component
public class LoginDialog extends javax.swing.JDialog implements KeyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginDialog.class);
    private boolean login = false;
    private int loginAttempt = 0;
    private boolean isRegister = false;
    private final FocusAdapter fa = new FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            Object sourceObj = evt.getSource();
            if (sourceObj instanceof JComboBox) {
                JComboBox jcb = (JComboBox) sourceObj;
                LOGGER.info("Control Name : " + jcb.getName());
            } else if (sourceObj instanceof JFormattedTextField) {
                JFormattedTextField jftf = (JFormattedTextField) sourceObj;
                jftf.selectAll();
                LOGGER.info("Control Name : " + jftf.getName());
            } else if (sourceObj instanceof JTextField) {
                JTextField jtf = (JTextField) sourceObj;
                jtf.selectAll();
                LOGGER.info("Control Name : " + jtf.getName());
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {

        }
    };

    @Autowired
    private UserService usrService;
    @Autowired
    private MachineInfoService machineInfoService;

    /**
     * Creates new form LoginDialog
     */
    public LoginDialog() {
        super(new javax.swing.JFrame(), true);
        initComponents();
        //Init
        initKeyListener();
        initFocusListener();
    }

    public void checkMachineRegister() {
        try {
            Global.machineName = Util1.getComputerName();
            Global.machineId = machineInfoService.getMax(Global.machineName);
            if (Global.machineId == 0) {
                isRegister = false;
                JOptionPane.showMessageDialog(Global.parentForm, "Your account is not registed in this machine");
            } else {
                isRegister = true;
            }
        } catch (Exception ex) {
            LOGGER.error("getMachineInfo Error : {}", ex.getMessage());
        }
    }

    private void register() {
        try {
            String machineName = Util1.getComputerName();
            String ipAddress = Util1.getIPAddress();
            MachineInfo machine = new MachineInfo();
            machine.setIpAddress(ipAddress);
            machine.setMachineName(machineName);
            machineInfoService.save(machine);
            Global.machineId = machineInfoService.getMax(Global.machineName);
        } catch (Exception ex) {
            LOGGER.error("Register Error : {}", ex.getMessage());
        }
    }

    //KeyListener implementation
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

        if (sourceObj instanceof JComboBox) {
            ctrlName = ((JComboBox) sourceObj).getName();
        } else if (sourceObj instanceof JFormattedTextField) {
            ctrlName = ((JFormattedTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        }

        switch (ctrlName) {
            case "txtLoginName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtPassword.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    butClear.requestFocus();
                }
                break;
            case "txtPassword":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER: //Login
                        login();
                        break;
                    case KeyEvent.VK_DOWN:
                        butLogin.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        txtLoginName.requestFocus();
                        break;
                }
                break;
            case "butLogin":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        login();
                        break;
                    case KeyEvent.VK_DOWN:
                        butClear.requestFocus();
                        break;
                }
                break;
            case "butClear":
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        clear();
                        break;
                    case KeyEvent.VK_DOWN:
                        txtLoginName.requestFocus();
                        break;
                    case KeyEvent.VK_UP:
                        butLogin.requestFocus();
                        break;
                }
        }
    }
    //======End KeyListener implementation ======

    public boolean isLogin() {
        return login;
    }

    private void login() {
        if (txtLoginName.getText().isEmpty() || txtPassword.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Invalid user name or password.",
                    "Authentication error.", JOptionPane.ERROR_MESSAGE);
            loginAttempt++;
        } else {
            try {
                AppUser user = usrService.login(
                        txtLoginName.getText(), String.copyValueOf(txtPassword.getPassword())
                );
                if (user == null) {
                    JOptionPane.showMessageDialog(this, "Invalid user name or password.",
                            "Authentication error.", JOptionPane.ERROR_MESSAGE);
                    loginAttempt++;
                } else { //Login success
                    if (user.getUserId() == 1) {
                        if (!isRegister) {
                            register();
                            isRegister = true;
                        }
                    }
                    if (isRegister) {
                        Global.loginUser = user;
                        login = true;
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(Global.parentForm, "This machine is not registered yet.");
                        login = false;
                        this.dispose();
                    }
                }
            } catch (HeadlessException ex) {
                LOGGER.error("login : " + ex.getMessage());
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Authentication error.", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (loginAttempt >= 3) {
            this.dispose();
        }
    }

    public void clear() {
        txtLoginName.setText(null);
        txtPassword.setText(null);
        txtLoginName.requestFocus();
    }

    private void initKeyListener() {
        txtLoginName.addKeyListener(this);
        txtPassword.addKeyListener(this);
        butClear.addKeyListener(this);
        butLogin.addKeyListener(this);
    }

    private void initFocusListener() {
        txtLoginName.addFocusListener(fa);
        txtPassword.addFocusListener(fa);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtLoginName = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        butClear = new javax.swing.JButton();
        butLogin = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Login Core Account");
        setFont(Global.lableFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Login Name ");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Password");

        txtLoginName.setFont(Global.lableFont);
        txtLoginName.setName("txtLoginName"); // NOI18N

        txtPassword.setFont(Global.lableFont);
        txtPassword.setName("txtPassword"); // NOI18N

        butClear.setFont(Global.lableFont);
        butClear.setText("Clear");
        butClear.setName("butClear"); // NOI18N

        butLogin.setFont(Global.lableFont);
        butLogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/login-button.png"))); // NOI18N
        butLogin.setText("Login");
        butLogin.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        butLogin.setName("butLogin"); // NOI18N
        butLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLoginActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 191, Short.MAX_VALUE)
                        .addComponent(butLogin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                            .addComponent(txtLoginName, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtLoginName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butClear)
                    .addComponent(butLogin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, txtLoginName, txtPassword});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butLoginActionPerformed
        login();
    }//GEN-LAST:event_butLoginActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTextField txtLoginName;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
