package com.bencoderer.schaetzitmanager.managers;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.lang.Runnable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bencoderer.schaetzitmanager.data.Operator;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.data.Schaetzung;
import com.bencoderer.schaetzitmanager.dto.OperatorDTO;
import com.bencoderer.schaetzitmanager.dto.SchaetzerDTO;
import com.bencoderer.schaetzitmanager.helpers.SimpleCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
    this.createHandlerThread();
    
    this.createTimer();
  }
  
  public SchaetzItServerManager getServerManager() {
    return this.mSvrMgr;
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
  
  public static List<Schaetzung> convertSchaetzerDTOToSchaetzungen(SchaetzerDTO source) {
    
    Type listType = new TypeToken<List<Schaetzung>>(){}.getType();
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    return gson.fromJson(source.getSchaetzungenJSON(), listType);
  }
  
  public static OperatorDTO convertOperatorToOperatorDTO(Operator op) {
    if (op == null) return null;
    
    OperatorDTO result = new OperatorDTO();
    result.setName(op.name);
    result.setOperatorKey(op.key);
    return result;
  }
  
  public static String getServerId(Schaetzer source) {
    return source.operatorKey + "_" + source.getId();
  }
  
  public void createHandlerThread() {
    if (this.mHandlerThread == null) {
      mHandlerThread = new HandlerThread("ServerSyncThread");
      mHandlerThread.start();
      Looper looper = mHandlerThread.getLooper();
      mHandler = new Handler(looper); 
    }
  }
  
  private void createTimer() {
    Observable<Long> observable = Observable.interval(10, TimeUnit.SECONDS);

    Subscription syncNowSubscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        syncSchaetzungenWithServer();
                    }
                });
  }
  
  public void doSyncNow() {
    Log.d(TAG, "requested Sync with server");
    syncSchaetzungenWithServer();
  }
  
  private void syncSchaetzungenWithServer() {
      final SchaetzItSyncManager myMgr = this;
    
    
    
      final SyncSchaetzungenWithServerTask syncTask = new SyncSchaetzungenWithServerTask(mMgr,mSvrMgr);
    
      mHandler.post(syncTask);
    
      Runnable afterSyncTask = new Runnable() {
            @Override  
            public void run() {
                 if (!syncTask.getStatus()){
                String errMsg = "Fehler beim Sync mit dem Server!"+"\n" + syncTask.getLastError();
                Log.d(TAG, errMsg);
                myMgr.onNotification.onSuccess(errMsg);
              }
            } //run is executed after syncTask has finished
        }; 
		
      mHandler.post(afterSyncTask);
    
    
     final DownloadSchaetzungenFromServerTask downloadTask = new DownloadSchaetzungenFromServerTask(mMgr,mSvrMgr);
    
      mHandler.post(downloadTask);
    
      Runnable afterDownloadTask = new Runnable() {
            @Override  
            public void run() {
                 if (!downloadTask.getStatus()){
                String errMsg = "Fehler beim Download vom Server!"+"\n" + downloadTask.getLastError();
                Log.d(TAG, errMsg);
                myMgr.onNotification.onSuccess(errMsg);
              }
            } //run is executed after downloadTask has finished
        }; 
		
      mHandler.post(afterDownloadTask);
      
    }
}
