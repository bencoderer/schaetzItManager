package com.bencoderer.schaetzitmanager.dto;

import com.strongloop.android.loopback.Model;
 
/**
 * A user that collects schaetzungen
 */
public class OperatorDTO extends Model { 
  
  private String operatorKey;
  private String name;
  
  
  public OperatorDTO() {
    
  }
  
  public void setName(String name) {
    this.name = name;
  }
 
  public String getName() {
    return name;
  }
  
  public void setOperatorKey(String operatorKey) {
    this.operatorKey = operatorKey;
  }
 
  public String getOperatorKey() {
    return operatorKey;
  }
}