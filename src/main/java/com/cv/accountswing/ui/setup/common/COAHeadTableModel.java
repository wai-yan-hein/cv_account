/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.ui.setup.common;
import com.cv.accountswing.entity.ChartOfAccount;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author MyoGyi
 */
@Component
public class COAHeadTableModel extends AbstractTableModel {
    
     private static final Logger LOGGER = LoggerFactory.getLogger(COAHeadTableModel.class);
    private List<ChartOfAccount> listCoaHead = new ArrayList();
    private String[] columnNames = {"Code", "Name"};

   @Override
    public int getRowCount() {
        if (listCoaHead == null) {
            return 0;
        }
        return listCoaHead.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
     @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            //case 2:
                //return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
         try {
            ChartOfAccount coa = listCoaHead.get(row);

            switch (column) {
                case 0: //Code
                    return coa.getCoaCodeUsr();
                case 1: //Name
                    return coa.getCoaNameEng();
                
                default:
                    return null;
            }
        } catch (Exception ex) {
            LOGGER.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
        }
    @Override
    public void setValueAt(Object value, int row, int column) {

    }
    
     public void setlistCoaHead(List<ChartOfAccount> listCoa) {
        this.listCoaHead = listCoa;
        fireTableDataChanged();
    }
     
     public ChartOfAccount getChartOfAccount(int row) {
        return listCoaHead.get(row);
    }

    
    
}
