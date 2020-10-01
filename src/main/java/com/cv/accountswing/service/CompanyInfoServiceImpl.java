/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.COADao;
import com.cv.accountswing.dao.CompanyInfoDao;
import com.cv.accountswing.entity.AppUser;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.CompanyInfo;
import com.cv.accountswing.entity.SystemProperty;
import com.cv.accountswing.entity.SystemPropertyKey;
import com.cv.accountswing.entity.UserRole;
import com.cv.accountswing.entity.UsrCompRole;
import com.cv.accountswing.entity.UsrCompRoleKey;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author WSwe
 */
@Service
@Transactional
public class CompanyInfoServiceImpl implements CompanyInfoService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyInfoServiceImpl.class);

    @Autowired
    private CompanyInfoDao dao;
    @Autowired
    private COADao coaDao;
    @Autowired
    private SeqTableService seqService;
    @Autowired
    private SystemPropertyService spService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private UsrCompRoleService usrCompRoleService;
    
    private String getCOACode(String compCode, int ttlLength) {
        int seqNo = seqService.getSequence("COA", "-", compCode);
        String coaCode = compCode + "-" + String.format("%0" + ttlLength + "d", seqNo);
        return coaCode;
    }

    @Override
    public CompanyInfo save(CompanyInfo ci, String status, String userId, String type) {
        ci = dao.save(ci);

        if (status.equals("NEW")) {
            Integer businessType = ci.getBusinessType();
            List<CompanyInfo> listCI = dao.search("-", "-", "-", "-", businessType.toString(), "-");
            String oldCompCode = "-";
            String newCompCode = ci.getCompId().toString();

            if (listCI != null) {
                if (!listCI.isEmpty()) {
                    oldCompCode = listCI.get(0).getCompId().toString();
                }
            }

            if (!oldCompCode.equals("-")) {
                SystemPropertyKey spk = new SystemPropertyKey("system.coa.code.length",
                        Integer.parseInt(oldCompCode));
                SystemProperty sp = spService.findById(spk);
                int ttlLength = Integer.parseInt(sp.getPropValue());
                List<ChartOfAccount> listLevel2COA = coaDao.getCOAWithLevel(oldCompCode, "2");

                try {
                    spService.copySystemProperty(oldCompCode, newCompCode);

                    for (ChartOfAccount tmpCOA : listLevel2COA) {
                        String newCOACode = getCOACode(newCompCode, ttlLength);
                        ChartOfAccount newCOA = new ChartOfAccount();

                        BeanUtils.copyProperties(tmpCOA, newCOA);
                        newCOA.setPrvCoaCode(newCOA.getCode());
                        newCOA.setCode(newCOACode);
                        newCOA.setCompCode(Integer.parseInt(newCompCode));
                        coaDao.save(newCOA);

                        List<SystemProperty> listSP = spService.search("-", newCompCode, tmpCOA.getCode());
                        for (SystemProperty tmpSP : listSP) {
                            tmpSP.setPropValue(newCOACode);
                            spService.save(tmpSP);
                        }

                        insertChild(oldCompCode, newCompCode, newCOA.getPrvCoaCode(),
                                newCOA.getCode(), ttlLength);
                    }

                    //Copy user role
                    spk = new SystemPropertyKey("system.default.admin.role",
                            Integer.parseInt(newCompCode));
                    String oldRoleId = spService.findById(spk).getPropValue();
                    UserRole newRole = userRoleService.copyRole(oldRoleId, newCompCode);
                    
                    ci.setRoleId(newRole.getRoleId());
                    
                    //Copy privilege
                    privilegeService.copyPrivilege(oldRoleId, newRole.getRoleId().toString());
                        
                    if (!type.equals("-")) {
                        //Assign role to user
                        UsrCompRoleKey newKey = new UsrCompRoleKey();
                        newKey.setCompCode(Integer.parseInt(newCompCode));
                        newKey.setRoleId(newRole.getRoleId());
                        newKey.setUserId(Integer.parseInt(userId));
                        
                        UsrCompRole newUserRole = new UsrCompRole();
                        newUserRole.setKey(newKey);
                        
                        usrCompRoleService.save(newUserRole);
                        
                        //Change user status to finished
                        AppUser user = accountService.findUserById(Integer.parseInt(userId));
                        user.setCreateStatus("FINISHED");
                        accountService.saveAccount(user);
                    }
                } catch (Exception ex) {
                    logger.error("save : " + ex.getMessage());
                }

            }
        }

        return ci;
    }

    private void insertChild(String oldCompCode, String newCompCode, String parent,
            String currParent, int ttlLength) {
        List<ChartOfAccount> listCOA = coaDao.getChild(oldCompCode, parent);

        for (ChartOfAccount tmpCOA : listCOA) {
            String newCOACode = getCOACode(newCompCode, ttlLength);
            ChartOfAccount newCOA = new ChartOfAccount();

            BeanUtils.copyProperties(tmpCOA, newCOA);
            newCOA.setPrvCoaCode(newCOA.getCode());
            newCOA.setCode(newCOACode);
            newCOA.setCompCode(Integer.parseInt(newCompCode));
            newCOA.setParent(currParent);
            coaDao.save(newCOA);

            insertChild(oldCompCode, newCompCode, newCOA.getPrvCoaCode(), newCOA.getCode(),
                    ttlLength);
        }
    }

    @Override
    public CompanyInfo findById(Integer code) {
        CompanyInfo ci = dao.findById(code);
        return ci;
    }

    @Override
    public List<CompanyInfo> search(String code, String name, String phone, String address,
            String businessType, String owner) {
        List<CompanyInfo> listCI = dao.search(code, name, phone, address, businessType, owner);
        return listCI;
    }

    @Override
    public int delete(String code) {
        int cnt = dao.delete(code);
        return cnt;
    }
}
