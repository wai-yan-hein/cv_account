/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author winswe
 */
public class DateDeSerializer implements JsonDeserializer<Date> {

    DateFormat dateFormat;

    public DateDeSerializer() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);      //This is the format I need
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            return dateFormat.parse(jsonElement.getAsString());
        } catch (ParseException e) {
            try {
                DateFormat tmpFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return tmpFmt.parse(jsonElement.getAsString());
            } catch (ParseException pe) {
                try {
                    DateFormat tmpFmt = new SimpleDateFormat("MMM dd, YYYY HH:mm:ss a");
                    return tmpFmt.parse(jsonElement.getAsString());
                } catch (ParseException ee) {
                    try {
                        DateFormat tmpFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                        return tmpFmt.parse(jsonElement.getAsString());
                    } catch (ParseException ee1) {
                        try {
                            DateFormat tmpFmt = new SimpleDateFormat("yyyy-MM-dd");
                            return tmpFmt.parse(jsonElement.getAsString());
                        } catch (ParseException ee2) {
                            try {
                                DateFormat tmpFmt = new SimpleDateFormat("MMM dd, yyyy");
                                return tmpFmt.parse(jsonElement.getAsString());
                            } catch (ParseException ee3) {
                                throw new JsonParseException(ee3);
                            }
                        }
                    }
                }
            }
        }
    }
}
