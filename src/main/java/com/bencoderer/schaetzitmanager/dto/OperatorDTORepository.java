package com.bencoderer.schaetzitmanager.dto;

import com.bencoderer.schaetzitmanager.dto.OperatorDTO;
import com.strongloop.android.loopback.ModelRepository;


public class OperatorDTORepository extends ModelRepository<OperatorDTO> {
    public OperatorDTORepository() {
        super("Operator", OperatorDTO.class);
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
