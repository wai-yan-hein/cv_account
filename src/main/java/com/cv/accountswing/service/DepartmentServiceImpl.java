/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.common.Global;
import com.cv.accountswing.dao.DepartmentDao;
import com.cv.accountswing.entity.Department;
import com.cv.accountswing.entity.SystemProperty;
import com.cv.accountswing.entity.SystemPropertyKey;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author WSwe
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    DepartmentDao dao;
    @Autowired
    SeqTableService seqService;
    @Autowired
    SystemPropertyService spService;

    @Override
    public Department save(Department dept) {
        if (dept.getDeptCode().isEmpty()) {
            dept.setDeptCode(getDeptCode(Global.compId.toString()));
        }
        dept = dao.save(dept);
        return dept;
    }

    @Override
    public Department findById(String id) {
        Department dept = dao.findById(id);
        return dept;
    }

    @Override
    public List<Department> search(String code, String name, String compCode, String userCode, String partentId) {
        List<Department> listDept = dao.search(code, name, compCode, userCode, partentId);
        return listDept;
    }

    @Override
    public int delete(String code) {
        int cnt = dao.delete(code);
        return cnt;
    }

    private String getDeptCode(String compCode) {
        SystemPropertyKey spk = new SystemPropertyKey("system.dept.code.length",
                Integer.parseInt(compCode));
        SystemProperty sp = spService.findById(spk);
        int ttlLength = Integer.parseInt(sp.getPropValue());
        int seqNo = seqService.getSequence("DEPT", "-", compCode);
        String coaCode = compCode + "-" + String.format("%0" + ttlLength + "d", seqNo);
        return coaCode;
    }

}
