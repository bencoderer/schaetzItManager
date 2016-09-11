package com.bencoderer.schaetzitmanager.dto;

import com.bencoderer.schaetzitmanager.helpers.ISODateHelper;

import com.strongloop.android.loopback.Model;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import android.util.Log;
 
/**
 * 
 */
public class SchaetzerDTO extends Model { 
  
  public static final String TAG = com.bencoderer.schaetzitmanager.activities.HelloAndroidActivity.TAG;
  
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
    this.indate = ISODateHelper.toStringJSON(indate);
  }
  
  public String getIndate() {
    return this.indate;
  }
 
  public Date getIndateDate() {
    
    try
    {
      return com.bencoderer.schaetzitmanager.helpers.ISODateHelper.fromStringJSON(this.indate);
    }
    catch(ParseException ex) {
      Log.e(TAG, "can not parse indate-string:" + this.indate);
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