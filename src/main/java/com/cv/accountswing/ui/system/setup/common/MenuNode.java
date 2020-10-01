/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.system.setup.common;

import com.cv.accountswing.dao.AbstractDao;
import com.cv.accountswing.entity.Menu;
import com.cv.accountswing.service.MenuService;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.xbean.spring.context.SpringApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author Lenovo
 */
public class MenuNode extends AbstractDao<String, Menu> {

    static Logger log = Logger.getLogger(MenuNode.class.getName());
    Menu menu;
    Object[] children;
    boolean allow = false;

    public MenuNode() {

    }

    public void setAllow(Boolean allow) {
        this.allow = allow;
    }

    public Boolean getAllow() {
        return this.allow;
    }

    public MenuNode(Menu menu) {
        // AbstractDataAccess menuDao = new BestDataAccess();
        this.menu = menu;

        try {
          
            /*//List<Menu> menuList = menuService.search("-", "-", menu.getId().toString());
            if (menuList != null) {
            children = new MenuNode[menuList.size()];
            int i = 0;
            
            for (Menu menu1 : menuList) {
            children[i] = new MenuNode(menu1, );
            i++;
            }*/
                
            //}
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }

    @Override
    public String toString() {
        return menu.getMenuName();
    }

    public Menu getMenu() {
        return this.menu;
    }

    public Object[] getChildren() {
        /*if (children != null) {
            return children;
        }*/

        return children;
    }
}
