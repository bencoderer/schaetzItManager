package com.bencoderer.schaetzitmanager.managers;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;
import rx.subjects.PublishSubject;
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
  
  private SimpleCallback onDownloadDoneCallback;
  private ReplaySubject<Integer> syncToServerSubject;
  
  //https://medium.com/@ali.muzaffar/handlerthreads-and-why-you-should-be-using-them-in-your-android-apps-dc8bf1540341#.rmy4l6k9x
  //https://blog.nikitaog.me/2014/10/18/android-looper-handler-handlerthread-ii/
  private HandlerThread mHandlerThread = null;
  private Handler mHandler;
  
  public SchaetzItSyncManager(SchaetzItManager mgr, SchaetzItServerManager svrMgr, SimpleCallback onNotification, SimpleCallback onDownloadDoneCallback) {
    this.mMgr = mgr;
    this.mSvrMgr = svrMgr;
    this.onNotification = onNotification;
    this.onDownloadDoneCallback = onDownloadDoneCallback;
    
    this.syncToServerSubject = ReplaySubject.create();
    
    
    this.createHandlerThread();
    
    this.createTimer();
  
  }
  
  public SchaetzItServerManager getServerManager() {
    return this.mSvrMgr;
  }
  
  public static SchaetzerDTO convertSchaetzerToSchaetzerDTO(Schaetzer source, SchaetzerDTO result) {
    result.setId(getServerId(source));
    result.setOperatorKey(source.operatorKey);
    result.setNameAndAddress(source.nameUndAdresse);
    result.setIdInOperatorDb(source.operatorDbId);
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
    return source.operatorKey + "_" + source.operatorDbId;
  }
  
  public void createHandlerThread() {
    if (this.mHandlerThread == null) {
      mHandlerThread = new HandlerThread("ServerSyncThread");
      mHandlerThread.start();
      Looper looper = mHandlerThread.getLooper();
      mHandler = new Handler(looper); 
    }
  }
  
  private Subscription syncInBackgroundSubscription = null;
  private PublishSubject<Long> syncNowTrigger = PublishSubject.create();
  
  public void quit() {
    mHandlerThread.quit();
    if (syncInBackgroundSubscription != null) {
    	syncInBackgroundSubscription.unsubscribe();
    }
  }
  
  private void createTimer() {
    Observable<Long> interval = Observable.interval(10, TimeUnit.SECONDS).subscribeOn(Schedulers.io());

    syncInBackgroundSubscription = Observable
                .merge(interval, syncNowTrigger)
                .debounce(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        syncSchaetzungenWithServer();
                    }
                },new Action1<Throwable>() {

                  public void call(Throwable t1) {
                      t1.printStackTrace();
                  }
              });
  }
  
  public void doSyncNow() {
    Log.d(TAG, "requested Sync with server");
    this.syncNowTrigger.onNext(-1L);
  }
  
  private void syncSchaetzungenWithServer() {
      final SchaetzItSyncManager myMgr = this;
    
      
      
      final SyncSchaetzungenWithServerTask syncTask = new SyncSchaetzungenWithServerTask(mMgr,mSvrMgr, syncToServerSubject);
    
    
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
    
    
     ReplaySubject<Integer> syncFromServerSubject = ReplaySubject.create();
     final DownloadSchaetzungenFromServerTask downloadTask = new DownloadSchaetzungenFromServerTask(mMgr,mSvrMgr, syncFromServerSubject);
    
     
    
     syncFromServerSubject
        //.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .timeout(60, TimeUnit.SECONDS) //sync mit server und speichern in der DB sollte innerhalb von 10 Sekunden abgeschlossen sein
        .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer syncedCount) {
                        onDownloadDoneCallback.onSuccess(syncedCount);
                    }
                },new Action1<Throwable>() {

                  public void call(Throwable t1) {
                      Log.d(TAG, "syncFromServerFailed: " + t1);
                      onDownloadDoneCallback.onError(new Exception("Fehler beim Herunterladen vom Server. Details:" + downloadTask.getLastError() ));
                  }
              });
    
      mHandler.post(downloadTask);
    
      //errorHandling wird mit dem syncFromServerSubject gemacht
      /*
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
      */
      
    }
}
