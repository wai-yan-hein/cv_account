/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.accountswing.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author winswe
 */
public class DateSerializer implements JsonSerializer<Date> {

    @Override
    public synchronized JsonElement serialize(Date date, Type typeOfSrc, JsonSerializationContext context) {
        //DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        
        if(date == null){
            return null;
        }else{
            SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        
            //TimeZone tz = TimeZone.getTimeZone( "UTC" );

            //df.setTimeZone( tz );

            String output = df.format( date );

            /*int inset0 = 9;
            int inset1 = 6;

            String s0 = output.substring( 0, output.length() - inset0 );
            String s1 = output.substring( output.length() - inset1, output.length() );

            String result = s0 + s1;

            result = result.replaceAll( "UTC", "+00:00" );*/
            
            //return new JsonPrimitive(df.format(date));
            return new JsonPrimitive(output);
        }
    }
}
