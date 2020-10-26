/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.dao;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.inv.entity.MachineInfo;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class MachineInfoDaoImpl extends AbstractDao<Integer, MachineInfo> implements MachineInfoDao {

    @Override
    public MachineInfo save(MachineInfo machineInfo) throws Exception {
        persist(machineInfo);
        return machineInfo;
    }

    @Override
    public int getMax(String machineName) throws Exception {
        int maxId = 0;
        Object obj = null;
        String strSQL = "select max(o.machineId) from MachineInfo o where o.machineName = '" + machineName + "'";
        obj = exeSQL(strSQL);
        if (obj == null) {
            maxId = 0;
        } else {
            maxId = Integer.parseInt(obj.toString());
        }

        return maxId;
    }

    @Override
    public List<MachineInfo> findAll() throws Exception {
        String hsql = "select o from MachineInfo o";
        return findHSQL(hsql);
    }

    @Override
    public MachineInfo findById(String id) throws Exception {
        return getByKey(Integer.parseInt(id));
    }

}
