package com.bencoderer.schaetzitmanager.dto;

import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

public class SyncSchaetzerToOperatorDTORepository extends ModelRepository<SyncSchaetzerToOperatorDTO> {
    public SyncSchaetzerToOperatorDTORepository() {
        super("Sync", SyncSchaetzerToOperatorDTO.class);
    }
  
    @Override
    public RestContract createContract() {
        RestContract contract = super.createContract();

        String className = getClassName();

        contract.addItem(new RestContractItem("/schaetzers/:schaetzerId/sync/clear", "POST"),
                className + ".clearAllOfSchaetzer");
      
        contract.addItem(new RestContractItem("/schaetzers/:schaetzerId/sync", "POST"),
                className + ".addToSchaetzer");
        /*
        POST schaetzers/:schaetzerId/sync/clear

        POST schaetzers/:schaetzerId/sync/  
         */ 
      
        return contract;
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
