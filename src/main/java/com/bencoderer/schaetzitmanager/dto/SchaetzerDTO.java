package com.bencoderer.schaetzitmanager.dto;

import com.strongloop.android.loopback.Model;
import java.util.Date;
 
/**
 * 
 */
public class SchaetzerDTO extends Model { 
  
  private String id;
  private String operatorKey;
  private String nameAndAddress;
  
  private String idInOperatorDb;
  
  private Date indate;
  
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
  
  
  public void setIndate(Date indate) {
    this.indate = indate;
  }
 
  public Date getIndate() {
    return indate;
  }
  
  
  public void setSchaetzungenJSON(String schaetzungenJSON) {
    this.schaetzungenJSON = schaetzungenJSON;
  }
 
  public String getSchaetzungenJSON() {
    return schaetzungenJSON;
  }
   
}