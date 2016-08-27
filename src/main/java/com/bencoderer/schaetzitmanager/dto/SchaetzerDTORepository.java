package com.bencoderer.schaetzitmanager.dto;

import com.bencoderer.schaetzitmanager.dto.SchaetzerDTO;
import com.strongloop.android.loopback.ModelRepository;
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

        contract.addItem(new RestContractItem("/" + getNameForRestUrl(), "PUT"),
                className + ".prototype.save");
        
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
