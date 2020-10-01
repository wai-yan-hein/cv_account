/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.entity.helper;

/**
 *
 * @author winswe
 */
public class ProfitAndLostRetObj {
    private double ttlSaleIncome;
    private double ttlOPStock;
    private double ttlPurchase;
    private double ttlCLStock;
    private double ttlOtherIncome;
    private double ttlOtherExpense;
    private double costOfSale;
    
    public ProfitAndLostRetObj(){
        ttlSaleIncome = 0.0;
        ttlOPStock = 0.0;
        ttlPurchase = 0.0;
        ttlCLStock = 0.0;
        ttlOtherIncome = 0.0;
        ttlOtherExpense = 0.0;
    }
    
    public void addSaleIncome(double value){
        ttlSaleIncome += value;
    }
    
    public void addOPStock(double value){
        ttlOPStock += value;
    }
    
    public void addPurchase(double value){
        ttlPurchase += value;
    }
    
    public void addCLStock(double value){
        ttlCLStock += value;
    }
    
    public void addOtherIncome(double value){
        ttlOtherIncome += value;
    }
    
    public void addOtherExpense(double value){
        ttlOtherExpense += value;
    }
    
    public double getGrossProfit(){
        return ttlSaleIncome - getCostOfSale();
    }

    public double getNetProfit(){
        return getGrossProfit() + ttlOtherIncome - (ttlOtherExpense*-1);
    }
    
    public double getTtlSaleIncome() {
        return ttlSaleIncome;
    }

    public double getTtlOPStock() {
        return ttlOPStock;
    }

    public double getTtlPurchase() {
        return ttlPurchase;
    }

    public double getTtlCLStock() {
        return ttlCLStock;
    }

    public double getTtlOtherIncome() {
        return ttlOtherIncome;
    }

    public double getTtlOtherExpense() {
        return ttlOtherExpense;
    }

    public double getCostOfSale() {
        costOfSale = ttlOPStock + (ttlPurchase*-1) - (ttlCLStock*-1);
        return costOfSale;
    }
}
