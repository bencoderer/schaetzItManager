package com.bencoderer.schaetzitmanager.managers;

import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.dto.SchaetzerDTO;
import com.bencoderer.schaetzitmanager.helpers.SimpleCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import android.util.Log;
import android.os.Handler;
import android.os.HandlerThread; 
import android.os.Looper; 

public class SchaetzItSyncManager {
  public static final String TAG = com.bencoderer.schaetzitmanager.activities.HelloAndroidActivity.TAG;
  
  private SchaetzItManager mMgr;
  private SchaetzItServerManager mSvrMgr;
  private SimpleCallback onNotification;
  
  //https://medium.com/@ali.muzaffar/handlerthreads-and-why-you-should-be-using-them-in-your-android-apps-dc8bf1540341#.rmy4l6k9x
  //https://blog.nikitaog.me/2014/10/18/android-looper-handler-handlerthread-ii/
  private HandlerThread mHandlerThread = null;
  private Handler mHandler;
  
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
  
  public void createHandlerThread() {
    if (this.mHandlerThread == null) {
      mHandlerThread = new HandlerThread("ServerSyncThread");
      mHandlerThread.start();
      Looper looper = mHandlerThread.getLooper();
      mHandler = new Handler(looper); 
    }
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
