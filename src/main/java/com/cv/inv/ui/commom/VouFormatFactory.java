/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.ui.commom;

import java.text.ParseException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author WSwe
 */
public class VouFormatFactory extends DefaultFormatterFactory{
    public VouFormatFactory() throws ParseException{
        super(new MaskFormatter("#######-##-####"));
    }
}
