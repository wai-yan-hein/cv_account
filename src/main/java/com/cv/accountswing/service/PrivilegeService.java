/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.service;

import com.cv.accountswing.entity.Menu;
import com.cv.accountswing.entity.Privilege;
import com.cv.accountswing.entity.PrivilegeKey;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface PrivilegeService {
    public Privilege save(Privilege privilege);
    public Privilege findById(PrivilegeKey key);
    public List<Privilege> search(String roleId, String menuId);
    public int delete(String roleId, String menuId);
    
    public void save(String roleId, List<Menu> listMenu);
    public void delete(String roleId, List<Menu> listMenu);
    public void copyPrivilege(String fromRoleId, String toRoleId) throws Exception;
}
