/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.ProjectTraderMapping;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class ProjectTraderMappingDaoImpl extends AbstractDao<Long, ProjectTraderMapping> implements ProjectTraderMappingDao {

    @Override
    public ProjectTraderMapping save(ProjectTraderMapping ptm) {
        persist(ptm);
        return ptm;
    }

    @Override
    public ProjectTraderMapping findById(Long id) {
        ProjectTraderMapping ptm = getByKey(id);
        return ptm;
    }

    @Override
    public List search(String projectId, String traderId) {
        String strSql = "select o from VProjectTraderMapping o where o.projectId ='" + projectId + "'";

        if (!traderId.equals("-")) {
            strSql = strSql + " and o.traderId = '" + traderId + "'";
        }

        List listPTM = findHSQL(strSql);
        return listPTM;
    }

    @Override
    public int delete(String projectId, String id) {
        String strSql = "";

        if (!projectId.equals("-")) {
            if (strSql.isEmpty()) {
                strSql = "o.projectId = " + projectId;
            } else {
                strSql = strSql + " and o.projectId = " + projectId;
            }
        }

        if (!id.equals("-")) {
            if (strSql.isEmpty()) {
                strSql = "o.id = " + id;
            } else {
                strSql = strSql + " and o.id = " + id;
            }
        }

        int cnt = 0;
        if (!strSql.isEmpty()) {
            strSql = "delete from ProjectTraderMapping o where " + strSql;
            cnt = execUpdateOrDelete(strSql);
        }

        return cnt;
    }
}
