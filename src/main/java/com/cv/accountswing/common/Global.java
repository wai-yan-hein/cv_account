/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.common;

import com.cv.accountswing.configuration.MQTransportListener;
import com.cv.accountswing.entity.AppUser;
import com.cv.accountswing.entity.ChartOfAccount;
import com.cv.accountswing.entity.DateModel;
import com.cv.accountswing.entity.Department;
import java.awt.Font;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import com.cv.accountswing.entity.Currency;
import com.cv.accountswing.entity.Customer;
import com.cv.accountswing.entity.Region;
import com.cv.accountswing.entity.Supplier;
import com.cv.accountswing.entity.view.VDescription;
import com.cv.accountswing.entity.view.VRef;
import com.cv.inv.entity.Category;
import com.cv.inv.entity.CharacterNo;
import com.cv.inv.entity.ChargeType;
import com.cv.inv.entity.Location;
import com.cv.inv.entity.MachineInfo;
import com.cv.inv.entity.RelationKey;
import com.cv.inv.entity.SaleMan;
import com.cv.inv.entity.Stock;
import com.cv.inv.entity.StockBrand;
import com.cv.inv.entity.StockType;
import com.cv.inv.entity.StockUnit;
import com.cv.inv.entity.UnitRelation;
import com.cv.inv.entity.VouStatus;
import com.cv.inv.entry.common.StockPriceList;
import java.util.ArrayList;

/**
 *
 * @author winswe
 */
public class Global {

    public static Font lableFont = new java.awt.Font("Arial Nova Light", 1, 12);
    public static Font amtFont = new java.awt.Font("Arial Nova Light", 1, 14);
    public static Font textFont = new java.awt.Font("Zawgyi-One", 0, 12);
    public static Font menuFont = new java.awt.Font("Zawgyi-One", 1, 13);
    public static Font shortCutFont = new java.awt.Font("Arial Nova Light", 0, 12);
    public static int tblRowHeight = 24;
    public static Font tblHeaderFont = new java.awt.Font("Arial Nova Light", 1, 13);
    public static String uuid;
    public static boolean synceFinish = true;
    public static AppUser loginUser;
    public static String roleCode;
    public static String compCode;
    public static int sessionId;
    public static String sessionName;
    public static String loginDate;
    public static Integer machineId;
    public static String machineName;
    public static boolean mqConStatus = false;
    public static int x;
    public static int y;
    public static int width;
    public static int height;
    public static JFrame parentForm;
    public static ServerSocket sock;
    public static MQTransportListener mqListener;
    public static HashMap<String, ReloadData> hmReloadData = new HashMap();
    public static String companyName;
    public static List<Department> listDepartment;
    public static List<Customer> listCustomer;
    public static List<Supplier> listSupplier;
    public static List<ChartOfAccount> listCOA;
    public static List<DateModel> listDateModel;
    public static List<AppUser> listAppUser;
    public static List<Currency> listCurrency;
    public static List<Location> listLocation;
    public static List<StockType> listStockType = new ArrayList<>();
    public static List<Category> listCategory = new ArrayList<>();
    public static List<StockBrand> listStockBrand = new ArrayList<>();
    public static List<StockUnit> listStockUnit = new ArrayList<>();
    public static List<CharacterNo> listCharNo = new ArrayList<>();
    public static List<VouStatus> listVou = new ArrayList<>();
    public static List<Stock> listStock = new ArrayList<>();
    public static List<Region> listRegion = new ArrayList<>();
    public static List<SaleMan> listSaleMan;
    public static List<ChargeType> listChargeType;
    public static List<UnitRelation> listRelation = new ArrayList<>();
    public static List<MachineInfo> listMachine;
    public static List<VRef> listRef = new ArrayList<>();
    public static List<VDescription> listDesp = new ArrayList<>();
    public static HashMap<String, String> sysProperties;
    public static HashMap<String, String> hmCashFilter = new HashMap<>();
    public static HashMap<RelationKey, Float> hmRelation = new HashMap<>();
    public static HashMap<String, List<StockPriceList>> hasPrice = new HashMap<>();
    public static HashMap<String, List<StockUnit>> hasUnit = new HashMap<>();

    public static String sourceAcId;
    public static String finicialPeriodFrom;
    public static String finicialPeriodTo;
    public static String dateFormat;
    //active messaging on / off
    public static boolean useActiveMQ = false;
    //default currency
    public static Currency defalutCurrency;
    //default department
    public static Department defaultDepartment;
    //defatult location
    public static Location defaultLocation;
    //default vouStatus
    public static VouStatus defaultVouStatus;
    //default saleMan
    public static SaleMan defaultSaleMan;
    //default cusotmer
    public static Customer defaultCustomer;
    //defalut supplier
    public static Supplier defaultSupplier;

    public static HashMap<String, Float> hasQtyInSmallest = new HashMap();

}
