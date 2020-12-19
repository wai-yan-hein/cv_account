/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.dao.MenuDao;
import com.cv.accountswing.entity.Menu;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuDao dao;

    @Override
    public Menu saveMenu(Menu menu) {
        return dao.saveMenu(menu);
    }

    @Override
    public Menu findById(String id) {
        return dao.findById(id);
    }

    @Override
    public List<Menu> search(String name, String nameMM, String parentId, String coaCode) {
        return dao.search(name, nameMM, parentId, coaCode);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public List<Menu> getParentChildMenu() {
        return dao.getParentChildMenu();
    }

    @Override
    public List getParentChildMenu(String roleId, String menuType) {
        return dao.getParentChildMenu(roleId, menuType);
    }

    @Override
    public List getReports(String roleId) {
        return dao.getReports(roleId);
    }

    @Override
    public List getReportList(String roleId, String partentCode) {
        return dao.getReportList(roleId, partentCode);
    }
}
