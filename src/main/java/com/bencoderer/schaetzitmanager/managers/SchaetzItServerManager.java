package com.bencoderer.schaetzitmanager.managers;

import java.util.ArrayList;
import java.util.List;

import com.bencoderer.schaetzitmanager.dto.OperatorDTORepository;
import com.bencoderer.schaetzitmanager.dto.OperatorDTO;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import android.content.Context;
import android.util.Log;

public class SchaetzItServerManager {
  
  private RestAdapter adapter;
  private Context context;
  private OperatorDTORepository operatorRepo;
  
  public SchaetzItServerManager(Context context) {
     this.context = context;
    
     this.adapter = new RestAdapter(this.context, "http://10.0.0.9:3000/api");
    
    this.operatorRepo = adapter.createRepository(OperatorDTORepository.class);
  }
  
  
  public void getOperatorsAll(final ListCallback<OperatorDTO> callback) {
    this.operatorRepo.findAll(
      new ListCallback<OperatorDTO>() {

        @Override
        public void onSuccess(List<OperatorDTO> arg0) {
            Log.d("", "GetOperatorsAll success:  " + arg0.size());
            callback.onSuccess(arg0);
        }

        @Override
        public void onError(Throwable arg0) {
            Log.e("", "GetOperatorsAll error: " + arg0);
            callback.onError(arg0);
        }
    });
  }
}
