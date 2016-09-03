package com.bencoderer.schaetzitmanager.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bencoderer.schaetzitmanager.dto.OperatorDTORepository;
import com.bencoderer.schaetzitmanager.dto.OperatorDTO;
import com.bencoderer.schaetzitmanager.dto.SchaetzerDTORepository;
import com.bencoderer.schaetzitmanager.dto.SchaetzerDTO;
import com.bencoderer.schaetzitmanager.dto.SyncSchaetzerToOperatorDTO;
import com.bencoderer.schaetzitmanager.dto.SyncSchaetzerToOperatorDTORepository;
import com.bencoderer.schaetzitmanager.helpers.SimpleCallback;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;
import android.content.Context;
import android.util.Log;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;

public class SchaetzItServerManager {
  
  public static final String TAG = com.bencoderer.schaetzitmanager.activities.HelloAndroidActivity.TAG;
  
  private RestAdapter adapter;
  private Context context;
  private OperatorDTORepository operatorRepo;
  private SchaetzerDTORepository schaetzerRepo;
  private SyncSchaetzerToOperatorDTORepository syncRepo;
  
  private AtomicInteger sentToServerSuccess = new AtomicInteger(0);
  private AtomicInteger sentToServerFailed = new AtomicInteger(0);

  private ArrayList<OperatorDTO> operatorListForSync;
  
  public SchaetzItServerManager(Context context) {
    this.context = context;
    
    this.adapter = new RestAdapter(this.context, "http://10.0.0.9:3000/api");
    
    this.operatorRepo = adapter.createRepository(OperatorDTORepository.class);
    
    this.schaetzerRepo = adapter.createRepository(SchaetzerDTORepository.class);
    
    this.syncRepo = adapter.createRepository(SyncSchaetzerToOperatorDTORepository.class);
  }
  
  
  public void setOperatorListForSync(ArrayList<OperatorDTO> operatorListForSync) {
    this.operatorListForSync = operatorListForSync;
  }
 
  
  
  public ArrayList<OperatorDTO> getOperatorListForSync() {
    return operatorListForSync;
  }
  
  
  
  public void getOperatorsAll(final ListCallback<OperatorDTO> callback) {
    this.operatorRepo.findAll(
      new ListCallback<OperatorDTO>() {

        @Override
        public void onSuccess(List<OperatorDTO> arg0) {
            Log.d(TAG, "GetOperatorsAll success:  " + arg0.size());
            callback.onSuccess(arg0);
        }

        @Override
        public void onError(Throwable arg0) {
            Log.e(TAG, "GetOperatorsAll error: " + arg0);
            callback.onError(arg0);
        }
    });
  }
  
  public SchaetzerDTO createSchaetzerDTO(String id) {
    HashMap<String, String> init = new HashMap<String, String>();
      
    if (id != null){
      init.put("id",id);
    }
    return schaetzerRepo.createObject(init);
  }
  
  public void sendSchaetzerToServer(final SchaetzerDTO schaetzer, final SimpleCallback whenDone) {
    final SchaetzItServerManager myMgr = this;
    
    Log.d(TAG, "sendSchaetzerToServer with id:" + schaetzer.getId());
    
    schaetzer.save(new VoidCallback() {
       
        @Override
        public void onSuccess() {
            Log.d(TAG, "sendSchaetzerToServer success for:" + schaetzer.getId());
            myMgr.sentToServerSuccess.incrementAndGet();
          
            resetSyncStateOfSchaetzer(schaetzer, whenDone);
        }

        @Override
        public void onError(Throwable arg0) {
            String details = "";
          
            if(arg0 instanceof org.apache.http.client.HttpResponseException){
              Throwable cause = ((org.apache.http.client.HttpResponseException) arg0 ).getCause();
              if (cause != null) {
             		details = cause.toString();
              }
            }
          
            Log.e(TAG, "sendSchaetzerToServer error for:" + schaetzer.getId() + " " + arg0 + " Details:" +details, arg0);
            myMgr.sentToServerFailed.incrementAndGet();
          
            whenDone.onError(arg0);
        }
    });
  }
  
  
  public void resetSyncStateOfSchaetzer(final SchaetzerDTO schaetzer, final SimpleCallback whenDone) {
    final SchaetzItServerManager myMgr = this;
    
    Log.d(TAG, "resetSyncStateOfSchaetzer with id:" + schaetzer.getId());
    
    syncRepo.clearSyncOfSchaetzer(schaetzer, new VoidCallback() {
       
        @Override
        public void onSuccess() {
            Log.d(TAG, "clearSyncOfSchaetzer success for:" + schaetzer.getId());
            
          
            createNewSyncStatesOfSchaetzer(schaetzer, whenDone);
        }

        @Override
        public void onError(Throwable arg0) {
            String details = "";
          
            if(arg0 instanceof org.apache.http.client.HttpResponseException){
              Throwable cause = ((org.apache.http.client.HttpResponseException) arg0 ).getCause();
              if (cause != null) {
             		details = cause.toString();
              }
            }
          
            Log.e(TAG, "resetSyncStateOfSchaetzer error for:" + schaetzer.getId() + " " + arg0 + " Details:" +details, arg0);
            
            whenDone.onError(arg0);
        }
    });
  }
  
  
  public void createNewSyncStatesOfSchaetzer(final SchaetzerDTO schaetzer, final SimpleCallback whenDone) {
    Log.d(TAG, "resetSyncStateOfSchaetzer with id:" + schaetzer.getId());
    
    final AtomicInteger created = new AtomicInteger(0);
    final AtomicInteger expected = new AtomicInteger(0);
    
    
    for(OperatorDTO operator: this.operatorListForSync) {
      expected.incrementAndGet();
      final OperatorDTO myOperator = operator;
      SyncSchaetzerToOperatorDTO sync = syncRepo.createObject(new HashMap<String,String>());
      sync.setSchaetzer(schaetzer);
      sync.setOperator(myOperator);
      if (isOperatorTheMasterOfSchaetzer(myOperator,schaetzer)) {
        sync.setSentToOperatorDate(new Date());
      }
      
      syncRepo.addToSchaetzer(sync, new VoidCallback() {
       
        @Override
        public void onSuccess() {
            Log.d(TAG, "createNewSyncStatesOfSchaetzer success for:" + schaetzer.getId() + " with operator " + myOperator.getOperatorKey());
            
            if (created.incrementAndGet() == expected.get()) {
              Log.d(TAG, "all syncSchaetzerToOperator created");
              whenDone.onSuccess(expected.get());
            }
        }

        @Override
        public void onError(Throwable arg0) {
            String details = "";
          
            if(arg0 instanceof org.apache.http.client.HttpResponseException){
              Throwable cause = ((org.apache.http.client.HttpResponseException) arg0 ).getCause();
              if (cause != null) {
             		details = cause.toString();
              }
            }
          
            Log.e(TAG, "createNewSyncStatesOfSchaetzer error for:" + schaetzer.getId() + " operator:" + myOperator.getOperatorKey() + " "  + arg0 + " Details:" +details, arg0);
            
            whenDone.onError(arg0);
       }
    });
    }
   
  }
  
   /**
         * @param myOperator
         * @param schaetzer
         * @return
         */
    private boolean isOperatorTheMasterOfSchaetzer(OperatorDTO myOperator,
                                                   SchaetzerDTO schaetzer) {
      
      return myOperator.getOperatorKey().equals(schaetzer.getOperatorKey());
    } 
  
}
