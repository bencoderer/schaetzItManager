package com.bencoderer.schaetzitmanager.dto;

import com.bencoderer.schaetzitmanager.helpers.ISODateHelper;

import com.strongloop.android.loopback.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class SyncSchaetzerToOperatorDTO extends Model {

  public static final String TAG = com.bencoderer.schaetzitmanager.activities.HelloAndroidActivity.TAG;
  
  private String schaetzerId;
   
   private String operatorKey;
   
   private String sentToOperatorDate;

    
  public void setSchaetzer(SchaetzerDTO schaetzer) {
	if (schaetzer != null) {
    this.schaetzerId = schaetzer.getId();
	}
	else {
	  this.schaetzerId = null;
	}
  }
 
  public void setOperator(OperatorDTO operator) {
	if (operator != null) {
    this.operatorKey = operator.getOperatorKey();
	}
	else {
	  this.operatorKey = null;
	}
  }
 
    
  public void setSchaetzerId(String schaetzerId) {
    this.schaetzerId = schaetzerId;
  }
 
  public String getSchaetzerId() {
    return schaetzerId;
  }
  
  public void setOperatorKey(String operatorKey) {
    this.operatorKey = operatorKey;
  }
 
  public String getOperatorKey() {
    return operatorKey;
  }
  
  
  public void setSentToOperatorDate(Date sentToOperatorDate) {
    this.sentToOperatorDate = ISODateHelper.toStringJSON(sentToOperatorDate);
  }
 
  public String getSentToOperatorDate() {
    return this.sentToOperatorDate;
  }
  
  public Date getSentToOperatorDateAsDate() {
    
    if (this.sentToOperatorDate == null){
      return null;
    }
    
    try
    {
    	return ISODateHelper.fromStringJSON(this.sentToOperatorDate);
    }
    catch(ParseException ex) {
      Log.e(TAG, "can not parse sentToOperatorDate-string:" + this.sentToOperatorDate);
      return null;
    }
  }
  
}



/*
  {"where": {"and": [{"operatorKey": {"neq": "OP1"}}, {"sentToOperatorDate": null}]}}
*/
