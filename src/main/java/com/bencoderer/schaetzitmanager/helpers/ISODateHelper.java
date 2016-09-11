package com.bencoderer.schaetzitmanager.helpers;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ISODateHelper {
  public static String toStringJSON(Date date) {
    //TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    //df.setTimeZone(tz);
    return df.format(date);
  }
  
  public static Date fromStringJSON(String isoDate) throws ParseException{
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    return df.parse(isoDate.replace("Z", "+0000"));
  }
}
