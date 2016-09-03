package com.bencoderer.schaetzitmanager.dto;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bencoderer.schaetzitmanager.dto.SchaetzerDTO;
import com.bencoderer.schaetzitmanager.dto.OperatorDTO;
import com.bencoderer.schaetzitmanager.helpers.SimpleCallback;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.callbacks.JsonArrayParser;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

public class SchaetzerDTORepository extends ModelRepository<SchaetzerDTO> {
    public SchaetzerDTORepository() {
        super("Schaetzer", SchaetzerDTO.class);
    }
  
    @Override
    public RestContract createContract() {
        RestContract contract = super.createContract();

        String className = getClassName();

        //we use PUT here 
        contract.addItem(new RestContractItem("/" + getNameForRestUrl(), "PUT"),
                className + ".prototype.save");
      
        contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/notSyncedTo/:operatorKey", "GET"),
                className + ".notSyncedTo");
      
        contract.addItem(new RestContractItem("/" + getNameForRestUrl() + "/:id/isSynced/:currentOperatorKey", "POST"),
                className + ".isSynced");
        
        return contract;
	}
  
  
    public void getNotSyncedTo(OperatorDTO operator, final ListCallback<SchaetzerDTO> callback) {
        Map<String, ? extends Object> params = new HashMap<String,String>();
        if (operator != null) {
          params = operator.toMap();
        }
        this.invokeStaticMethod("notSyncedTo",
                params,
                new JsonArrayParser<SchaetzerDTO>(this, callback));
      
	}
  
    public void setIsSynced(SchaetzerDTO schaetzer, OperatorDTO currentOperator, final SimpleCallback callback) {
        if (schaetzer == null) {
          return;
        }
      
        if (currentOperator == null) {
          return;
        }
      
        Map<String, Object> params = new HashMap<String,Object>();
        params.putAll(schaetzer.toMap());
      
       
        params.put("currentOperatorKey", currentOperator.getOperatorKey());
      
        this.invokeStaticMethod("isSynced",
                params,
                new Adapter.JsonObjectCallback() {
              @Override
              public void onError(Throwable t) {
                  callback.onError(t);
              }

              @Override
              public void onSuccess(JSONObject response) {
                  String sentDateStr = response.optString("sentToOperatorDate");
                  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                
                  Date sentDate = null;
                  try
                  {
                  	sentDate = df.parse(sentDateStr);
                  }
                  catch(ParseException ex) {
                    sentDate =  null;
                  }
                  callback.onSuccess(sentDate);
              }
			});
    }
  
  
  /*
   * https://groups.google.com/forum/#!topic/loopbackjs/1xHq5vFRayk
   * 
   Try as follows: public void filter(String[] pincode, final ListCallback<NewOrder> callback) {

        invokeStaticMethod("all",
                ImmutableMap.of("filter",
                        ImmutableMap.of("where",
                                ImmutableMap.of("and",
                                        ImmutableMap.of("0", ImmutableMap.of("orderState", "0"),"1", ImmutableMap.of("userPincode",

                                                ImmutableMap.of("inq", ImmutableList.of(pincode))
                                                ))))),
                new JsonArrayParser<NewOrder>(this, callback));
    }
   */
}
