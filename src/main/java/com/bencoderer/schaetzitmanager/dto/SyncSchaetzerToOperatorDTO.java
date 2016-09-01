package com.bencoderer.schaetzitmanager.dto;

import com.strongloop.android.loopback.Model;

import java.util.Date;

public class SyncSchaetzerToOperatorDTO extends Model {

  private String schaetzerId;
   
   private String operatorKey;
   
   private Date sentToOperatorDate;

}



/*
  {"where": {"and": [{"operatorKey": {"neq": "OP1"}}, {"sentToOperatorDate": null}]}}
*/
