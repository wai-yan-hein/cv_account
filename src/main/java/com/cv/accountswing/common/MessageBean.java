/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.common;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Lenovo
 */
public class MessageBean {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String value;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String newValue, String propertyName) {
        String oldValue = value;
        value = newValue;
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

}
