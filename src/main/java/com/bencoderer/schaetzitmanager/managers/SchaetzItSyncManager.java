package com.bencoderer.schaetzitmanager.managers;

import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.dto.SchaetzerDTO;
import com.bencoderer.schaetzitmanager.helpers.SimpleCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import android.util.Log;

public class SchaetzItSyncManager {
  public static final String TAG = com.bencoderer.schaetzitmanager.activities.HelloAndroidActivity.TAG;
  
  private SchaetzItManager mMgr;
  private SchaetzItServerManager mSvrMgr;
  private SimpleCallback onNotification;
  
  public SchaetzItSyncManager(SchaetzItManager mgr, SchaetzItServerManager svrMgr, SimpleCallback onNotification) {
    this.mMgr = mgr;
    this.mSvrMgr = svrMgr;
    this.onNotification = onNotification;
  }
  
  public static SchaetzerDTO convertSchaetzerToSchaetzerDTO(Schaetzer source, SchaetzerDTO result) {
    result.setId(source.operatorKey + "_" + source.getId());
    result.setOperatorKey(source.operatorKey);
    result.setNameAndAddress(source.nameUndAdresse);
    result.setIdInOperatorDb(source.getId().toString());
    result.setIndate(source.indate);
    
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    result.setSchaetzungenJSON(gson.toJson(source.schaetzungen()));
    return result;
  }
  
  public void doSyncNow() {
    Log.d(TAG, "requested Sync with server");
    syncSchaetzungenWithServer();
  }
  
  private void syncSchaetzungenWithServer() {
      final SchaetzItSyncManager myMgr = this;
      
      SyncSchaetzungenWithServerTask syncTask = new SyncSchaetzungenWithServerTask(mMgr,mSvrMgr) {
          @Override
          protected void onPostExecute(Boolean status)
          {
              if (!status){
                String errMsg = "Fehler beim Sync mit dem Server!"+"\n" + this.getLastError();
                Log.d(TAG, errMsg);
                myMgr.onNotification.onSuccess(errMsg);
              }
          }
          
        };
      
      syncTask.execute();
    }
}
