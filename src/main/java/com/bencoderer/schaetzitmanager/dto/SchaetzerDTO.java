package com.bencoderer.schaetzitmanager.dto;

import com.strongloop.android.loopback.Model;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
 
/**
 * 
 */
public class SchaetzerDTO extends Model { 
  
  private String id;
  private String operatorKey;
  private String nameAndAddress;
  
  private String idInOperatorDb;
  
  private String indate;
  
  private String schaetzungenJSON;
  
  
  public void setId(String id) {
    this.id = id;
  }
 
  public String getId() {
    return id;
  }
  
  

  public void setOperatorKey(String operatorKey) {
    this.operatorKey = operatorKey;
  }
 
  public String getOperatorKey() {
    return operatorKey;
  }
 
  
  public void setNameAndAddress(String nameAndAddress) {
    this.nameAndAddress = nameAndAddress;
  }
 
  public String getNameAndAddress() {
    return nameAndAddress;
  }
  
  
  public void setIdInOperatorDb(String idInOperatorDb) {
    this.idInOperatorDb = idInOperatorDb;
  }
 
  
  public String getIdInOperatorDb() {
    return idInOperatorDb;
  }
  
  public void setIndate(String indate) {
    this.indate = indate;
  }
 
  
  public void setIndate(Date indate) {
    //TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    //df.setTimeZone(tz);
    this.indate = df.format(indate);
  }
  
  public String getIndate() {
    return this.indate;
  }
 
  public Date getIndateDate() {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    try
    {
    	return df.parse(this.indate);
    }
    catch(ParseException ex) {
      return null;
    }
  }
  
  
  public void setSchaetzungenJSON(String schaetzungenJSON) {
    this.schaetzungenJSON = schaetzungenJSON;
  }
 
  public String getSchaetzungenJSON() {
    return schaetzungenJSON;
  }
   
}