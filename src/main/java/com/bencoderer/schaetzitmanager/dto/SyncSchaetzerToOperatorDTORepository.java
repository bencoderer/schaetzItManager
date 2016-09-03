package com.bencoderer.schaetzitmanager.dto;

import java.util.HashMap;

import com.bencoderer.schaetzitmanager.dto.SyncSchaetzerToOperatorDTO;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.callbacks.VoidCallback;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

public class SyncSchaetzerToOperatorDTORepository extends ModelRepository<SyncSchaetzerToOperatorDTO> {
    public SyncSchaetzerToOperatorDTORepository() {
        super("SyncSchaetzerToOperator", SyncSchaetzerToOperatorDTO.class);
    }
  
    @Override
    public RestContract createContract() {
        RestContract contract = super.createContract();

        String className = getClassName();

        contract.addItem(new RestContractItem("/schaetzers/:schaetzerId/sync", "DELETE"),
                className + ".clearAllOfSchaetzer");
      
        contract.addItem(new RestContractItem("/schaetzers/:schaetzerId/sync", "POST"),
                className + ".addToSchaetzer");
        /*
        POST schaetzers/:schaetzerId/sync/clear

        POST schaetzers/:schaetzerId/sync/  
         */ 
      
        return contract;
	}
  
  
  public void clearSyncOfSchaetzer(SchaetzerDTO schaetzer, final VoidCallback callback) {
        SyncSchaetzerToOperatorDTO helper = this.createObject(new HashMap<String,String>());
        helper.setSchaetzer(schaetzer);
    
        this.invokeStaticMethod("clearAllOfSchaetzer", helper.toMap(),
                new Adapter.Callback() {

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }

          
            @Override
            public void onSuccess(String arg0) {
                callback.onSuccess();
                
            }
        });
    }
  

  public void addToSchaetzer(SyncSchaetzerToOperatorDTO sync, final VoidCallback callback) {

        this.invokeStaticMethod("addToSchaetzer", sync.toMap(),
                new Adapter.Callback() {

            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }

          
            @Override
            public void onSuccess(String arg0) {
                callback.onSuccess();
                
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
