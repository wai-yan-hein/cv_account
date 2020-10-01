/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.common.LoadingObserver;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.Project;
import com.cv.accountswing.entity.view.VProjectCOAMapping;
import com.cv.accountswing.entity.view.VProjectTraderMapping;
import com.cv.accountswing.entity.view.VProjectUserMapping;
import com.cv.accountswing.service.COAService;
import com.cv.accountswing.service.DepartmentService;
import com.cv.accountswing.service.ProjectCOAMappingService;
import com.cv.accountswing.service.ProjectService;
import com.cv.accountswing.service.ProjectTraderMappingService;
import com.cv.accountswing.service.ProjectUserMappingService;
import com.cv.accountswing.service.TraderService;
import com.cv.accountswing.service.UserService;
import com.cv.accountswing.ui.cash.common.TableCellRender;
import com.cv.accountswing.ui.editor.AppUserCellEditor;
import com.cv.accountswing.ui.editor.COACellEditor;
import com.cv.accountswing.ui.editor.TraderCellEditor;
import com.cv.accountswing.ui.setup.common.ChartOfAmountTabelModel;
import com.cv.accountswing.ui.setup.common.ProjectTableModel;
import com.cv.accountswing.ui.setup.common.ProjectTraderTableModel;
import com.cv.accountswing.ui.setup.common.ProjectUserTableModel;
import com.cv.accountswing.util.BindingUtil;
import com.cv.accountswing.util.Util1;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
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
public class ManageProjectSetup extends javax.swing.JPanel implements KeyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageProjectSetup.class);

    private int selectRow = -1;
    Long projectId;
    @Autowired
    private UserService userService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectTableModel projectTableModel;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private COAService cOAService;
    @Autowired
    private ProjectCOAMappingService pcoamService;
    @Autowired
    private ChartOfAmountTabelModel cOATableModel;
    @Autowired
    private ProjectTraderMappingService projectTraderMappingService;
    @Autowired
    private ProjectUserMappingService projectUserMappingService;
    @Autowired
    private ProjectUserTableModel projectUserTableModel;
    @Autowired
    private ProjectTraderTableModel projectTraderTableModel;
    @Autowired
    private TaskExecutor taskExecutor;
    private LoadingObserver loadingObserver;
    private boolean isShown = false;

    public void setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
    }

    /**
     * Creates new form ManageProjectSetup
     */
    public ManageProjectSetup() {
        initComponents();
    }

    private void initMain() {
        loadingObserver.load(this.getName(), "Start");
        assignDefalutValue();
        initCombo();
        initKeyListener();
        initTable();
        isShown = true;
    }

    private void initCombo() {
        BindingUtil.BindComboFilter(cboDepartment, departmentService.search("-", "-", "-", "-", "-"), null, true, false);
    }

    private void initTable() {
        tblProject();
        tblTrader();
        tblCOA();
        tblUser();

    }

    private void tblUser() {
        tblUser.setModel(projectUserTableModel);
        tblUser.getTableHeader().setFont(Global.textFont);
        tblUser.getColumnModel().getColumn(0).setPreferredWidth(15);// Code
        tblUser.getColumnModel().getColumn(1).setPreferredWidth(450);// Name
        tblUser.getColumnModel().getColumn(0).setCellEditor(new AppUserCellEditor(userService));
        tblUser.setDefaultRenderer(Object.class, new TableCellRender());

    }

    private void tblCOA() {
        tblCOA.setModel(cOATableModel);
        tblCOA.getTableHeader().setFont(Global.textFont);
        tblCOA.getColumnModel().getColumn(0).setPreferredWidth(15);// Code
        tblCOA.getColumnModel().getColumn(1).setPreferredWidth(450);// Name
        tblCOA.getColumnModel().getColumn(2).setPreferredWidth(15);// Active    
        tblCOA.getColumnModel().getColumn(0).setCellEditor(new COACellEditor(cOAService));
        tblCOA.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblCOA.setDefaultRenderer(Object.class, new TableCellRender());

    }

    private void tblTrader() {
        tblTrader.setModel(projectTraderTableModel);
        tblTrader.getTableHeader().setFont(Global.textFont);
        tblTrader.getColumnModel().getColumn(0).setPreferredWidth(15);// Code
        tblTrader.getColumnModel().getColumn(1).setPreferredWidth(450);// Name
        tblTrader.getColumnModel().getColumn(0).setCellEditor(new TraderCellEditor(traderService));
        tblTrader.setDefaultRenderer(Object.class, new TableCellRender());

    }

    private void tblProject() {
        tblProject.setModel(projectTableModel);
        tblProject.getTableHeader().setFont(Global.textFont);
        tblProject.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProject.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                if (tblProject.getSelectedRow() >= 0) {
                    selectRow = tblProject.convertRowIndexToModel(tblProject.getSelectedRow());
                    Project project = projectTableModel.getProject(selectRow);
                    setProject(project);
                    projectId = project.getProjectId();
                    searchCOAMapping();
                    searchUserMapping();
                    searchTraderMapping();
                    setEnableButton(true);
                }

            }
        });
        tblProject.getTableHeader().setFont(Global.textFont);
        tblProject.getColumnModel().getColumn(0).setPreferredWidth(15);// Code
        tblProject.getColumnModel().getColumn(1).setPreferredWidth(400);// Name
        tblProject.getColumnModel().getColumn(2).setPreferredWidth(15);// Active    
        tblProject.setDefaultRenderer(Boolean.class, new TableCellRender());
        tblProject.setDefaultRenderer(Object.class, new TableCellRender());
        List<Project> listProject = projectService.search("-", "-", "-", "-", "-", "-", "-");
        projectTableModel.setlistProject(listProject);
        loadingObserver.load(this.getName(), "Stop");

    }

    private void searchUserMapping() {
        List list = projectUserMappingService.search(Util1.getString(projectId), "-");
        List<VProjectUserMapping> listUser = (List<VProjectUserMapping>) (List<?>) list;
        projectUserTableModel.setlistProject(listUser);
        projectUserTableModel.setProjectId(projectId);
        projectUserTableModel.addNewRow();

    }

    private void searchCOAMapping() {
        List list = pcoamService.search(Util1.getString(projectId), "-");
        List<VProjectCOAMapping> listCOA = (List<VProjectCOAMapping>) (List<?>) list;
        cOATableModel.setlistProject(listCOA);
        cOATableModel.setProjectId(projectId);
        cOATableModel.addNewRow();

    }

    private void searchTraderMapping() {
        List list = projectTraderMappingService.search(Util1.getString(projectId), "-");
        List<VProjectTraderMapping> listTrader = (List<VProjectTraderMapping>) (List<?>) list;
        projectTraderTableModel.setlistProject(listTrader);
        projectTraderTableModel.setProjectId(projectId);
        projectTraderTableModel.addNewRow();

    }

    private void assignDefalutValue() {
        setTodayDate();
    }

    private void setTodayDate() {
        txtStDate.setText(Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd"));
        txtEndDate.setText(Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd"));

    }

    private void saveProject() {
        Project project = new Project();
        Department dep = (Department) cboDepartment.getSelectedItem();
        project.setProjectName(txtProjectName.getText());
        project.setDeptCode(dep.getDeptCode());
        project.setStartDate(Util1.toDate(txtStDate.getText()));
        project.setEndDate(Util1.toDate(txtEndDate.getText()));
        project.setProjectStatus(chkActive.isSelected());
        if (isValidProject(project)) {
            if (project.getProjectId() == null) {
                long userId = Global.loginUser.getUserId();
                project.setCreatedBy(userId);
                project.setCreatedDate(new Date());
            }
            project.setCompCode(Global.compId);
            Project save = projectService.save(project);
            if (save != null) {
                JOptionPane.showMessageDialog(Global.parentForm, "Saved");
                clearProject();
                if (lblStatus.getText().equals("NEW")) {
                    projectTableModel.addProject(project);
                } else {
                    projectTableModel.setProject(selectRow, project);
                }
            }
        }
    }

    private boolean isValidProject(Project project) {
        boolean status = true;

        if (project == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Project.");
        } else if (project.getProjectName() == null) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Project Name.");
        } else if (project.getProjectName().trim().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Project Name.");
        }

        return status;
    }

    private void setProject(Project project) {
        txtCode.setText(project.getProjectCode());
        txtProjectName.setText(project.getProjectName());
        txtStDate.setText(Util1.toDateStr(project.getStartDate(), "yyyy-MM-dd"));
        txtEndDate.setText(Util1.toDateStr(project.getEndDate(), "yyyy-MM-dd"));
        chkActive.setSelected(project.getProjectStatus());
        lblStatus.setText("EDIT");
    }

    private void clearProject() {
        txtCode.setText(null);
        txtProjectName.setText(null);
        txtStDate.setText(null);
        txtEndDate.setText(null);
        chkActive.setSelected(Boolean.FALSE);
        setTodayDate();
        setEnableButton(false);
    }

    private void setEnableButton(boolean active) {

        btnDelete.setEnabled(active);

    }

    private void initKeyListener() {
        txtCode.addKeyListener(this);
        txtProjectName.addKeyListener(this);
        txtStDate.addKeyListener(this);
        txtEndDate.addKeyListener(this);
        chkActive.addKeyListener(this);
        btnSave.addKeyListener(this);
        btnClear.addKeyListener(this);
        btnDelete.addKeyListener(this);

        tblCOA.addKeyListener(this);
        tblTrader.addKeyListener(this);
        tblUser.addKeyListener(this);
        tblProject.addKeyListener(this);
        cboDepartment.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtStDate.requestFocus();
                }
            }

        });

    }

    public void editEntry(int selectRow) {
        Project project = projectTableModel.getProject(selectRow);
        setProject(project);
        projectId = project.getProjectId();
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

        if (sourceObj instanceof JComboBox) {
            ctrlName = ((JComboBox) sourceObj).getName();
        } else if (sourceObj instanceof JFormattedTextField) {
            ctrlName = ((JFormattedTextField) sourceObj).getName();
        } else if (sourceObj instanceof JTextField) {
            ctrlName = ((JTextField) sourceObj).getName();
        } else if (sourceObj instanceof JCheckBox) {
            ctrlName = ((JCheckBox) sourceObj).getName();
        } else if (sourceObj instanceof JButton) {
            ctrlName = ((JButton) sourceObj).getName();
        } else if (sourceObj instanceof JTable) {
            ctrlName = ((JTable) sourceObj).getName();
        }
        switch (ctrlName) {
            case "txtCode":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtProjectName.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnDelete.requestFocus();
                }
                tabToTrader(e);
                break;
            case "txtProjectName":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    cboDepartment.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtCode.requestFocus();
                }
                tabToTrader(e);

                break;
            case "cboDepartment":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (!cboDepartment.isPopupVisible()) {
                        txtStDate.requestFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtProjectName.requestFocus();
                }
                tabToTrader(e);

                break;
            case "txtStDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtEndDate.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    cboDepartment.requestFocus();
                }
                tabToTrader(e);

                break;
            case "txtEndDate":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    chkActive.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtStDate.requestFocus();
                }
                tabToTrader(e);

                break;
            case "chkActive":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnSave.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    txtEndDate.requestFocus();
                }
                tabToTrader(e);

                break;

            case "btnSave":
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    btnClear.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    chkActive.requestFocus();
                }
                tabToTrader(e);

                break;
            case "btnClear":
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (btnDelete.isEnabled()) {
                        btnDelete.requestFocus();
                    } else {
                        txtCode.requestFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnSave.requestFocus();
                }
                tabToTrader(e);

                break;
            case "btnDelete":
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    txtCode.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnClear.requestFocus();
                }
                tabToTrader(e);

                break;
            case "cboCOA":
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    btnDelete.requestFocus();
                }
                tabToTrader(e);

                break;

            case "tblCOA":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    tblTrader.requestFocus();
                    if (tblTrader.getRowCount() >= 0) {
                        tblTrader.setRowSelectionInterval(0, 0);
                    }
                }
                break;

            case "btnAddTrader":
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    tblTrader.requestFocus();
                    tblTrader.setRowSelectionInterval(0, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                }
                tabToTrader(e);

                break;

            case "tblUser":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    tblProject.requestFocus();
                    if (tblProject.getRowCount() >= 0) {
                        tblProject.setRowSelectionInterval(0, 0);
                    }
                }
                break;
            case "tblTrader":
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    tblUser.requestFocus();
                    if (tblUser.getRowCount() >= 0) {
                        tblUser.setRowSelectionInterval(0, 0);
                    }
                }

                break;
            case "tblProject":

                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    editEntry(tblProject.convertRowIndexToModel(tblProject.getSelectedRow()));
                    searchCOAMapping();
                    searchUserMapping();
                    searchTraderMapping();

                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    txtCode.requestFocus();
                }
                break;
            default:
                break;
        }

    }

    private void tabToTrader(KeyEvent e) {
        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tblCOA.requestFocus();
            if (tblCOA.getRowCount() >= 0) {
                tblCOA.setRowSelectionInterval(0, 0);
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
        jLabel2 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        txtProjectName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cboDepartment = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtStDate = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtEndDate = new javax.swing.JTextField();
        chkActive = new javax.swing.JCheckBox();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCOA = new javax.swing.JTable();
        lblStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUser = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblTrader = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProject = new javax.swing.JTable();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Code");

        txtCode.setFont(Global.textFont);
        txtCode.setName("txtCode"); // NOI18N

        txtProjectName.setFont(Global.textFont);
        txtProjectName.setName("txtProjectName"); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Project Name");

        cboDepartment.setFont(Global.textFont);
        cboDepartment.setName("cboDepartment"); // NOI18N

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Department");

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Start Date");

        txtStDate.setFont(Global.textFont);
        txtStDate.setName("txtStDate"); // NOI18N

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("End Date");

        txtEndDate.setFont(Global.textFont);
        txtEndDate.setName("txtEndDate"); // NOI18N

        chkActive.setFont(Global.lableFont);
        chkActive.setText("Active");
        chkActive.setName("chkActive"); // NOI18N

        btnSave.setFont(Global.lableFont);
        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnClear.setFont(Global.lableFont);
        btnClear.setText("Clear");
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnDelete.setFont(Global.lableFont);
        btnDelete.setText("Delete");
        btnDelete.setEnabled(false);
        btnDelete.setName("btnDelete"); // NOI18N
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chart of Amount", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        tblCOA.setFont(Global.textFont);
        tblCOA.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCOA.setName("tblCOA"); // NOI18N
        tblCOA.setRowHeight(Global.tblRowHeight);
        jScrollPane3.setViewportView(tblCOA);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
        );

        lblStatus.setFont(Global.lableFont);
        lblStatus.setText("NEW");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtProjectName)
                                    .addComponent(txtCode)
                                    .addComponent(cboDepartment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtStDate)
                                    .addComponent(txtEndDate)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(chkActive)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(254, 254, 254)
                                .addComponent(btnSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDelete)))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnClear, btnDelete, btnSave});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel3, jLabel4, jLabel5, jLabel6});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtProjectName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cboDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtStDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSave)
                        .addComponent(lblStatus))
                    .addComponent(btnClear)
                    .addComponent(btnDelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chart of Amount", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        tblUser.setFont(Global.textFont);
        tblUser.setModel(new javax.swing.table.DefaultTableModel(
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
        tblUser.setName("tblUser"); // NOI18N
        tblUser.setRowHeight(Global.tblRowHeight);
        jScrollPane1.setViewportView(tblUser);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chart of Amount", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        tblTrader.setFont(Global.textFont);
        tblTrader.setModel(new javax.swing.table.DefaultTableModel(
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
        tblTrader.setName("tblTrader"); // NOI18N
        tblTrader.setRowHeight(Global.tblRowHeight);
        jScrollPane4.setViewportView(tblTrader);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Project", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, Global.lableFont));

        tblProject.setFont(Global.textFont);
        tblProject.setModel(new javax.swing.table.DefaultTableModel(
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
        tblProject.setName("tblProject"); // NOI18N
        tblProject.setRowHeight(Global.tblRowHeight);
        jScrollPane2.setViewportView(tblProject);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        saveProject();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clearProject();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        if (!isShown) {
            initMain();
        }
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cboDepartment;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCOA;
    private javax.swing.JTable tblProject;
    private javax.swing.JTable tblTrader;
    private javax.swing.JTable tblUser;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtEndDate;
    private javax.swing.JTextField txtProjectName;
    private javax.swing.JTextField txtStDate;
    // End of variables declaration//GEN-END:variables

}
