/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "menu")
public class Menu implements java.io.Serializable {

    private Integer id; //menu_id
    private String parent; //parent_menu_id
    private String menuName; //menu_name
    private String menuNameMM;
    private String menuUrl;
    private String menuType;
    private Integer orderBy;
    private String soureAccCode;
    private String menuClass;

    private List<Menu> child;

    public Menu() {

    }

    public Menu(Integer id, String menuName, String menuType) {
        this.id = id;
        this.menuName = menuName;
        this.menuType = menuType;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "menu_id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "parent_menu_id")
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Column(name = "menu_name", length = 50)
    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    @Column(name = "menu_name_mm", length = 500)
    public String getMenuNameMM() {
        return menuNameMM;
    }

    public void setMenuNameMM(String menuNameMM) {
        this.menuNameMM = menuNameMM;
    }

    @Column(name = "menu_url", length = 500)
    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    @Column(name = "menu_type")
    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    @Transient
    public List<Menu> getChild() {
        return child;
    }

    public void setChild(List<Menu> child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return menuName;
    }

    @Column(name = "order_by")
    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    @Column(name = "source_acc_code")
    public String getSoureAccCode() {
        return soureAccCode;
    }

    public void setSoureAccCode(String soureAccCode) {
        this.soureAccCode = soureAccCode;
    }

    @Column(name = "menu_class")

    public String getMenuClass() {
        return menuClass;
    }

    public void setMenuClass(String menuClass) {
        this.menuClass = menuClass;
    }

}
