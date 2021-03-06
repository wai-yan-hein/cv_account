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
import com.cv.accountswing.common.TreeTransferHandler;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.Gl;
import com.cv.accountswing.entity.Menu;
import com.cv.accountswing.entity.Privilege;
import com.cv.accountswing.entity.PrivilegeKey;
import com.cv.accountswing.entity.Trader;
import com.cv.accountswing.entity.UserRole;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.GlService;
import com.cv.accountswing.service.MenuService;
import com.cv.accountswing.ui.ApplicationMainFrame;
import com.cv.accountswing.service.PrivilegeService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.service.UserRoleService;
import com.cv.accountswing.util.BindingUtil;
import com.cv.accountswing.util.Util1;
import java.awt.FileDialog;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Lenovo
 */
@Component
public class ChartOfAccountSetup extends javax.swing.JPanel implements
        MouseListener,
        TreeSelectionListener, KeyListener,
        PanelControl {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ChartOfAccountSetup.class);
    private DefaultMutableTreeNode selectedNode;
    DefaultTreeModel treeModel;
    private final String parentRootName = "Core Account";
    private DefaultMutableTreeNode treeRoot;
    @Autowired
    COAService coaServcie;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private MenuService menuService;
    @Autowired
    private ApplicationMainFrame mainFrame;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private ChartOfAccountImportDialog importDialog;
    @Autowired
    private TraderService traderService;
    @Autowired
    private GlService glService;
    JPopupMenu popupmenu;
    private LoadingObserver loadingObserver;
    private final HashMap<String, Menu> hmMenu = new HashMap<>();
    private boolean isShown = false;
    private ChartOfAccount chartOfAccount;
    private boolean isNew = false;

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
    }

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    private final ActionListener menuListener = (java.awt.event.ActionEvent evt) -> {
        JMenuItem actionMenu = (JMenuItem) evt.getSource();
        String menuName = actionMenu.getText();
        log.info("Selected Menu : " + menuName);
        switch (menuName) {
            case "New":
                newCOA();
                break;
            case "Delete":
                deleteCOA();
                break;
            case "Import":
                importCOA();
                break;
            default:
                break;
        }

    };

    /**
     * Creates new form COASetup
     */
    public ChartOfAccountSetup() {
        initComponents();
        initKeyListener();
        initPopup();
    }

    private void initMain() {
        initTree();
        initCombo();
        isShown = true;
    }

    private void initCombo() {
        List<Menu> listMenu = menuService.getParentChildMenu();
        BindingUtil.BindCombo(cboMenu, listMenu, null, false);
        listMenu.forEach((menu) -> {
            if (menu.getChild() != null) {
                menu.getChild().stream().filter(m -> (m.getSoureAccCode() != null)).forEachOrdered(m -> {
                    hmMenu.put(m.getSoureAccCode(), menu);
                });
            }
        });
        cboMenu.setSelectedItem(null);
    }

    private void newCOA() {
        ChartOfAccount coa = new ChartOfAccount();
        coa.setCoaNameEng("New Chart of Account");
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(coa);
        if (selectedNode != null) {
            selectedNode.add(child);
            treeModel.insertNodeInto(child, selectedNode, selectedNode.getChildCount() - 1);
            treeCOA.setSelectionInterval(selectedNode.getChildCount(), selectedNode.getChildCount());

        }
        isNew = true;
    }

    private void saveChartAcc() {
        String parentCode;
        String option;
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
        if (parentNode != null) {
            Object userObject = parentNode.getUserObject();
            if (userObject.toString().equals(parentRootName)) {
                parentCode = "#";
                option = "SYS";
            } else {
                ChartOfAccount pCoa = (ChartOfAccount) parentNode.getUserObject();
                parentCode = pCoa.getCode();
                option = "USR";
            }
            if (isNew) {
                chartOfAccount.setCreatedBy(Global.loginUser.getAppUserCode());
                chartOfAccount.setCreatedDate(Util1.getTodayDate());
            }
            int level = selectedNode.getLevel();
            chartOfAccount.setCode(txtSysCode.getText());
            chartOfAccount.setCoaNameEng(txtName.getText());
            chartOfAccount.setCoaCodeUsr(txtUsrCode.getText());
            chartOfAccount.setCompCode(Global.compCode);
            chartOfAccount.setCoaParent(parentCode);
            chartOfAccount.setCoaLevel(level);
            chartOfAccount.setModifiedBy(Global.loginUser.getAppUserCode());
            chartOfAccount.setModifiedDate(Util1.getTodayDate());
            chartOfAccount.setOption(option);
            chartOfAccount.setActive(chkActive.isSelected());
            chartOfAccount.setMacId(Global.machineId);
            ChartOfAccount coaSave = coaServcie.save(chartOfAccount);
            if (coaSave != null) {
                Global.listCOA.add(coaSave);
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                if (lblStatus.getText().equals("EDIT")) {
                    selectedNode.setUserObject(coaSave);
                    treeModel.reload(selectedNode);
                    setEnabledControl(false);
                    clear();
                }
            }
        }

    }

    private void deleteCOA() {
        try {
            if (selectedNode != null) {
                ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
                if (coa != null) {
                    String code = coa.getCode();
                    if (isValidDel(code)) {
                        coaServcie.delete(code, Global.compCode);
                        treeModel.removeNodeFromParent(selectedNode);
                        treeModel.reload(selectedNode);
                    } else {
                        JOptionPane.showMessageDialog(Global.parentForm, "Can't delete this account is already used.");
                    }
                }
            }
        } catch (HeadlessException e) {
            log.error("Delete ChartOfAccount :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Delete ChartOfAccount", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidDel(String code) {
        boolean status;
        List<Gl> listGl = glService.search("-", "-", "-", code, "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-");
        status = listGl.isEmpty();
        if (status) {
            List<Trader> search = traderService.search("-", code);
            status = search.isEmpty();
        }
        return status;
    }

    private void initPopup() {
        popupmenu = new JPopupMenu("Edit");
        JMenuItem cut = new JMenuItem("New");
        JMenuItem copy = new JMenuItem("Delete");
        JMenuItem importCOA = new JMenuItem("Import");
        cut.addActionListener(menuListener);
        copy.addActionListener(menuListener);
        importCOA.addActionListener(menuListener);
        popupmenu.add(cut);
        popupmenu.add(copy);
        popupmenu.add(importCOA);
    }

    private void initTree() {
        loadingObserver.load(this.getName(), "Start");
        treeCOA.setDragEnabled(true);
        treeCOA.setDropMode(DropMode.ON_OR_INSERT);
        treeCOA.setTransferHandler(new TreeTransferHandler());
        treeCOA.getSelectionModel().setSelectionMode(
                TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        treeRoot = new DefaultMutableTreeNode(parentRootName);
        taskExecutor.execute(() -> {
            createTreeNode("#", treeRoot);
            treeModel.setRoot(treeRoot);
            loadingObserver.load(this.getName(), "Stop");
        });
        treeModel = (DefaultTreeModel) treeCOA.getModel();
        treeModel.setRoot(treeRoot);
        //treMenu.addPropertyChangeListener(propertyChangeListener);
    }

    private void createTreeNode(String parentMenuID, DefaultMutableTreeNode treeRoot) {
        List<ChartOfAccount> listChild = coaServcie.getChild(Global.compCode, parentMenuID);
        listChild.forEach(child -> {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(child);
            treeRoot.add(root);
            createTreeNode(child.getCode(), root);
        });

    }

    private void setCOA(ChartOfAccount coa) {
        chartOfAccount = coa;
        setEnabledControl(true);
        txtSysCode.setText(chartOfAccount.getCode());
        txtName.setText(chartOfAccount.getCoaNameEng());
        txtUsrCode.setText(chartOfAccount.getCoaCodeUsr());
        chkActive.setSelected(Util1.getBoolean(chartOfAccount.isActive()));
        lblStatus.setText("EDIT");
        if (chartOfAccount.getCoaLevel() != null) {
            if (chartOfAccount.getCoaLevel() == 3) {
                btnCreate.setEnabled(true);
                Menu menu = hmMenu.get(chartOfAccount.getCode());
                cboMenu.setSelectedItem(menu == null ? null : menu);
            } else {
                btnCreate.setEnabled(false);
            }
        }
        txtUsrCode.requestFocus();
    }

    public void clear() {
        txtSysCode.setText(null);
        txtName.setText(null);
        txtUsrCode.setText(null);
        chkActive.setSelected(false);
        treeCOA.requestFocus();
        chartOfAccount = new ChartOfAccount();
        isNew = false;
    }

    private void initKeyListener() {
        txtName.addKeyListener(this);
        txtSysCode.addKeyListener(this);
        txtUsrCode.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnSave.addKeyListener(this);
        treeCOA.addKeyListener(this);
        btnClear.addKeyListener(this);
        treeCOA.addMouseListener(this);
        treeCOA.addTreeSelectionListener(this);

    }

    private void setEnabledControl(boolean status) {
        txtUsrCode.setEnabled(status);
        txtName.setEnabled(status);
        btnSave.setEnabled(status);
        btnClear.setEnabled(status);
        chkActive.setEnabled(status);
        btnImport.setEnabled(status);

    }

    private void saveMenu() {
        if (cboMenu.getSelectedItem() != null) {
            if (cboMenu.getSelectedItem() instanceof Menu) {
                ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
                Menu selectMenu = (Menu) cboMenu.getSelectedItem();
                Menu menu = new Menu();
                menu.setMenuName(coa.getCoaNameEng());
                menu.setMenuClass(selectMenu.getMenuClass());
                menu.setParent(selectMenu.getCode());
                menu.setSoureAccCode(coa.getCode());
                menu.setMenuType("Menu");
                menu.setCompCode(Global.compCode);
                menu.setMacId(Global.machineId);
                try {
                    Menu saveMenu = menuService.saveMenu(menu);
                    if (saveMenu != null) {
                        String menuId = saveMenu.getCode();
                        List<UserRole> listUser = userRoleService.search("-", Global.compCode);
                        if (!listUser.isEmpty()) {
                            listUser.stream().map(role -> {
                                Privilege p = new Privilege();
                                PrivilegeKey key = new PrivilegeKey(role.getRoleCode(), menuId);
                                p.setKey(key);
                                return p;
                            }).map(p -> {
                                p.setIsAllow(Boolean.FALSE);
                                return p;
                            }).forEachOrdered(p -> {
                                privilegeService.save(p);
                            });
                        }
                        JOptionPane.showMessageDialog(Global.parentForm, "Successfully Created");

                    }
                } catch (HeadlessException e) {
                    JOptionPane.showMessageDialog(Global.parentForm, e.getMessage());
                    log.info("Save Menu :" + e.getMessage());
                }

            }
        }
    }

    private void importCOA() {
        ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
        Integer cLevel = coa == null ? 0 : coa.getCoaLevel();
        String cCode = cLevel == 2 ? coa.getCode() : null;
        if (cCode != null) {
            FileDialog dialog = new FileDialog(Global.parentForm, "Choose CSV File", FileDialog.LOAD);
            dialog.setDirectory("D:\\");
            dialog.setFile(".csv");
            dialog.setVisible(true);
            String directory = dialog.getFile();
            log.info("File Path :" + directory);
            String path = dialog.getDirectory() != null ? dialog.getDirectory() + "\\" + directory : "";
            readFile(path, cCode);
        }

    }

    private void readFile(String path, String parentCode) {
        String line;
        int lineCount = 0;
        try {
            try ( BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "UTF8"))) {
                while ((line = br.readLine()) != null) {
                    ChartOfAccount coa = new ChartOfAccount();
                    lineCount++;
                    coa.setOption("USR");
                    coa.setCoaLevel(3);
                    coa.setCoaParent(parentCode);
                    coa.setCoaNameEng(line);
                    coa.setCompCode(Global.compCode);
                    coa.setActive(Boolean.TRUE);
                    coa.setCreatedDate(Util1.getTodayDate());
                    coa.setCreatedBy(Global.loginUser.getAppUserCode());
                    coa.setMacId(Global.machineId);
                    coaServcie.save(coa);
                }
                log.info("Import Sucess : " + lineCount);
            }

        } catch (IOException e) {
            log.error("Read CSV File :" + e.getMessage());

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
        treeCOA = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSysCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtUsrCode = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        btnSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();
        panelMenu = new javax.swing.JPanel();
        cboMenu = new javax.swing.JComboBox<>();
        btnCreate = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();

        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                formComponentRemoved(evt);
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        treeCOA.setFont(Global.textFont);
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Root");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("color");
        treeNode1.add(treeNode2);
        treeCOA.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeCOA.setName("treeCOA"); // NOI18N
        jScrollPane1.setViewportView(treeCOA);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel1.setFont(Global.textFont);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("System Code");

        txtSysCode.setEditable(false);
        txtSysCode.setFont(Global.textFont);
        txtSysCode.setName("txtSysCode"); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("User Code");

        txtUsrCode.setFont(Global.textFont);
        txtUsrCode.setEnabled(false);
        txtUsrCode.setName("txtUsrCode"); // NOI18N

        txtName.setFont(Global.textFont);
        txtName.setEnabled(false);
        txtName.setName("txtName"); // NOI18N
        txtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameFocusGained(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Name");

        chkActive.setFont(Global.lableFont);
        chkActive.setText("Active");
        chkActive.setEnabled(false);
        chkActive.setName("chkActive"); // NOI18N

        btnSave.setBackground(ColorUtil.mainColor);
        btnSave.setFont(Global.textFont);
        btnSave.setForeground(ColorUtil.foreground);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-button-white.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setEnabled(false);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        btnClear.setBackground(ColorUtil.btnEdit);
        btnClear.setFont(Global.textFont);
        btnClear.setForeground(ColorUtil.foreground);
        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clear-button-white.png"))); // NOI18N
        btnClear.setText("Clear");
        btnClear.setEnabled(false);
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        panelMenu.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Menu Group Mapping", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        cboMenu.setEditable(true);
        cboMenu.setFont(Global.textFont);
        cboMenu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboMenuItemStateChanged(evt);
            }
        });
        cboMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMenuActionPerformed(evt);
            }
        });

        btnCreate.setFont(Global.lableFont);
        btnCreate.setText("Create");
        btnCreate.setEnabled(false);
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMenuLayout = new javax.swing.GroupLayout(panelMenu);
        panelMenu.setLayout(panelMenuLayout);
        panelMenuLayout.setHorizontalGroup(
            panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboMenu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCreate)
                .addContainerGap())
        );
        panelMenuLayout.setVerticalGroup(
            panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCreate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnImport.setBackground(ColorUtil.mainColor);
        btnImport.setFont(Global.textFont);
        btnImport.setForeground(ColorUtil.foreground);
        btnImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/import-button.png"))); // NOI18N
        btnImport.setText("Import");
        btnImport.setName("btnSave"); // NOI18N
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName)
                            .addComponent(txtUsrCode)
                            .addComponent(txtSysCode)
                            .addComponent(chkActive, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnImport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtSysCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUsrCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkActive)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnSave)
                    .addComponent(btnImport))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            save();
        } catch (Exception e) {
            log.error("Save Account Group :" + e.getMessage());
            JOptionPane.showMessageDialog(Global.parentForm, e.getMessage(), "Save Account Group", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        mainFrame.setControl(this);
        if (!isShown) {
            initMain();
        }
        txtUsrCode.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
        // TODO add your handling code here:
        log.info("FOCUS LOST");
    }//GEN-LAST:event_formFocusLost

    private void formComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_formComponentRemoved
        // TODO add your handling code here:
        log.info("COMPONENT REMOVED");
    }//GEN-LAST:event_formComponentRemoved

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
        log.info("COMPONENT RESIZED");

    }//GEN-LAST:event_formComponentResized

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        // TODO add your handling code here:
        log.info("COMPONENT MOVED");

    }//GEN-LAST:event_formComponentMoved

    private void txtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameFocusGained
        // TODO add your handling code here:
        txtName.selectAll();
    }//GEN-LAST:event_txtNameFocusGained

    private void cboMenuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboMenuItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboMenuItemStateChanged

    private void cboMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboMenuActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        saveMenu();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        // TODO add your handling code here:
        importDialog.setSize(Global.width - 400, Global.height - 400);
        importDialog.setLocationRelativeTo(null);
        importDialog.setVisible(true);

    }//GEN-LAST:event_btnImportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cboMenu;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelMenu;
    private javax.swing.JTree treeCOA;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSysCode;
    private javax.swing.JTextField txtUsrCode;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            popupmenu.show(this, e.getX(), e.getY());
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selectedNode = (DefaultMutableTreeNode) treeCOA.getLastSelectedPathComponent();
        if (selectedNode != null) {
            if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
                setCOA(coa);
            } else {
                clear();
                setEnabledControl(false);
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

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

        if (sourceObj instanceof JTree) {
            ctrlName = ((JTree) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        }
        //log.info("Control Name Key Released:" + ctrlName);
        switch (ctrlName) {
            case "txtUsrCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtName.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
                tabToTree(e);
                break;
            case "txtName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtUsrCode.requestFocus();
                }
                tabToTree(e);

                break;
            case "chkActive":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnSave.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtName.requestFocus();
                }
                tabToTree(e);

                break;
            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnClear.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    chkActive.requestFocus();
                }
                tabToTree(e);

                break;
            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtUsrCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
                tabToTree(e);
                break;
            case "treeCOA":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtUsrCode.requestFocus();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    if (selectedNode != null) {
                        if (!selectedNode.getUserObject().toString().equals(parentRootName)) {
                            ChartOfAccount coa = (ChartOfAccount) selectedNode.getUserObject();
                            setCOA(coa);
                        } else {
                            clear();
                            setEnabledControl(false);

                        }
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtUsrCode.requestFocus();
                }
        }
    }

    private void tabToTree(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            treeCOA.requestFocus();
        }
    }

    @Override
    public void delete() {
    }

    @Override
    public void newForm() {
        clear();
        isShown = false;

    }

    @Override
    public void history() {
    }

    @Override
    public void print() {
    }

    @Override
    public void save() {
        saveChartAcc();
    }

    @Override
    public void refresh() {
        initTree();
    }

}
