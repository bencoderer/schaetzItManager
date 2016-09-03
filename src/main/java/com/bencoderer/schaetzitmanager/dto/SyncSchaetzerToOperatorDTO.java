package com.bencoderer.schaetzitmanager.dto;

import com.strongloop.android.loopback.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncSchaetzerToOperatorDTO extends Model {

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
    //TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    //df.setTimeZone(tz);
    this.sentToOperatorDate = df.format(sentToOperatorDate);
  }
 
  public String getSentToOperatorDate() {
    return this.sentToOperatorDate;
  }
  
  public Date getSentToOperatorDateAsDate() {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    try
    {
    	return df.parse(this.sentToOperatorDate);
    }
    catch(ParseException ex) {
      return null;
    }
  }
  
}



/*
  {"where": {"and": [{"operatorKey": {"neq": "OP1"}}, {"sentToOperatorDate": null}]}}
*/
