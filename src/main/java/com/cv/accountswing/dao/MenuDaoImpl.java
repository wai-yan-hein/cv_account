/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.dao;

import com.cv.accountswing.entity.Menu;
import com.cv.accountswing.entity.view.VRoleMenu;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class MenuDaoImpl extends AbstractDao<String, Menu> implements MenuDao {

    @Override
    public Menu saveMenu(Menu menu) {
        persist(menu);
        return menu;
    }

    @Override
    public Menu findById(String id) {
        Menu menu = getByKey(id);
        return menu;
    }

    @Override
    public List<Menu> search(String name, String nameMM, String parentId, String coaCode) {
        String strSql = "select o from Menu o ";
        String strFilter = "";

        if (!name.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.text like '" + name + "%'";
            } else {
                strFilter = strFilter + " and o.text like '" + name + "%'";
            }
        }

        if (!nameMM.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.menuNameMM like '" + nameMM + "%'";
            } else {
                strFilter = strFilter + " and o.menuNameMM like '" + nameMM + "%'";
            }
        }

        if (!parentId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.parent = " + parentId;
            } else {
                strFilter = strFilter + " and o.parent = " + parentId;
            }
        }
        if (!coaCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.soureAccCode = " + coaCode;
            } else {
                strFilter = strFilter + " and o.soureAccCode = " + coaCode;
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<Menu> listMenu = findHSQL(strSql);
        return listMenu;
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from Menu o where o.id = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

    @Override
    public List<Menu> getParentChildMenu() {
        String strSql = "select o from Menu o where o.parent = '1'";
        List<Menu> listRootMenu = findHSQL(strSql);
        listRootMenu.forEach(parent -> {
            getChild(parent);
        });

        return listRootMenu;
    }

    private Menu getChild(Menu parent) {
        String strSql = "select o from Menu o where o.parent = '" + parent.getId() + "'";
        List<Menu> listChild = findHSQL(strSql);

        if (listChild != null) {
            if (listChild.size() > 0) {
                parent.setChild(listChild);
                listChild.forEach(parentMenu -> {
                    getChild(parentMenu);
                });
            }
        }

        return parent;
    }

    @Override
    public List getParentChildMenu(String roleId, String menuType) {
        String strSql = "select o from VRoleMenu o where o.key.roleId = " + roleId
                + " and o.parent = '1' order by o.orderBy";
        List listRootMenu = findHSQL(strSql);
        for (int i = 0; i < listRootMenu.size(); i++) {
            VRoleMenu parent = (VRoleMenu) listRootMenu.get(i);
            getChild(parent, roleId, menuType);
        }

        return listRootMenu;
    }

    private void getChild(VRoleMenu parent, String roleId, String menuType) {
        String strSql = "select o from VRoleMenu o where o.parent = '" + parent.getKey().getMenuId()
                + "' and o.key.roleId = " + roleId + "";
        if (!menuType.equals("-")) {
            strSql = strSql + " and o.menuType = '" + menuType + "'";
        }
        List listChild = findHSQL(strSql);

        if (listChild != null) {
            if (listChild.size() > 0) {
                parent.setChild(listChild);
                for (int i = 0; i < listChild.size(); i++) {
                    VRoleMenu child = (VRoleMenu) listChild.get(i);
                    getChild(child, roleId, menuType);
                }
            }
        }
    }

    @Override
    public List getReports(String roleId) {
        String hsql = "select o from VRoleMenu o where o.key.roleId = " + roleId + " and o.menuType = 'Reports' and o.isAllow = true";
        return findHSQL(hsql);
    }

    @Override
    public List getReportList(String roleId, String parentCode) {
        String hsql = "select o from VRoleMenu o where o.key.roleId = " + roleId + " and o.isAllow = true"
                + "  and o.parent ='" + parentCode + "'";
        return findHSQL(hsql);

    }
}
