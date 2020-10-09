/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author WSwe
 */
public class NumberUtil {

    public static Long NZeroL(Object number) {
        try {
            if (number == null) {
                return new Long(0);
            } else {
                return Long.parseLong(number.toString());
            }
        } catch (Exception ex) {
            System.out.println("NumberUtil.NZero : " + ex.getMessage());
            return new Long(0);
        }
    }

    public static Double NZero(Object number) {
        try {
            if (number == null) {
                return new Double(0);
            } else {
                return Double.parseDouble(number.toString().replace(",", ""));
            }
        } catch (Exception ex) {
            System.out.println("NumberUtil.NZero : " + ex.getMessage());
            return new Double(0);
        }
    }

    public static Float FloatZero(Object number) {
        try {
            if (number == null) {
                return new Float(0);
            } else {
                return Float.parseFloat(number.toString().replace(",", ""));
            }
        } catch (Exception ex) {
            System.out.println("NumberUtil.NZero : " + ex.getMessage());
            return new Float(0);
        }
    }

    public static DefaultFormatterFactory getDecimalFormat() {
        return new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#,##0.00")));
    }

    public static Integer NZeroInt(Object number) {
        Integer value = new Integer(0);

        try {
            value = Integer.parseInt(number.toString());
        } catch (Exception ex) {
        }

        return value;
    }

    public static Float NZeroFloat(Object number) {
        Float value = new Float(0);

        try {
            value = Float.parseFloat(number.toString());
        } catch (Exception ex) {
        }

        return value;
    }

    public static Double getDouble(Object obj) {
        Double value;

        if (obj != null) {
            value = getDouble(obj.toString());
        } else {
            value = null;
        }

        return value;
    }

    public static Double getDouble(String number) {
        double value = 0.0;

        try {
            value = Double.parseDouble(number.replace(",", ""));
        } catch (Exception ex) {

        }
        return value;
    }

    public static boolean isNumber(Object obj) {
        boolean status;

        if (obj == null) {
            status = true;
        } else {
            status = isNumber(obj.toString());
        }

        return status;
    }

    public static boolean isPositive(Object obj) {
        boolean status = false;

        if (obj != null) {
            int parseInt = Integer.parseInt(obj.toString());
            if (parseInt > 0) {
                status = true;
            }
        }

        return status;
    }

    public static boolean isNumber(String number) {
        boolean status = false;

        try {
            if (number != null && !number.isEmpty()) {
                double tmp = Double.parseDouble(number);
                status = true;
            } else {
                status = true;
            }
        } catch (Exception ex) {
            System.out.println("NumberUtil.isNumber : " + ex.getMessage());
        }
        return status;
    }

    public static Integer getNumber(String strText) {
        Integer tmpInt = null;
        String strN = "";

        try {
            for (int i = 0; i < strText.length(); i++) {
                String tmpStr = strText.substring(i, i + 1);

                if (isNumber(tmpStr)) {
                    strN = strN + tmpStr;
                }
            }

            tmpInt = new Integer(strN);
        } catch (Exception ex) {
            System.out.println("NumberUtil.getNumber : " + ex.getMessage());
        }

        return tmpInt;
    }

    public static Double roundTo(Double value, int scale) {
        BigDecimal bd = new BigDecimal(value);

        return bd.setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static Float roundToF(Double value, int scale) {
        BigDecimal bd = new BigDecimal(value);

        return bd.setScale(scale, RoundingMode.HALF_UP).floatValue();
    }

    public static Object toDataType(Object value, Class toDataType) {
        if (value != null) {
            if (!value.getClass().equals(toDataType)) {
                value = toDataType.cast(value);
            }
        }

        return value;
    }

    public static String toChar(Object value) {
        String tmpStr = null;

        if (value != null) {
            tmpStr = value.toString();
        }

        return tmpStr;
    }
}
