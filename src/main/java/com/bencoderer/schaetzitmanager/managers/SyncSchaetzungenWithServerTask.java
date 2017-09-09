package com.bencoderer.schaetzitmanager.managers;

import rx.subjects.ReplaySubject;
import rx.Observable;

import java.util.Date;
import java.util.List;
import java.lang.Runnable;
import android.util.Log;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.data.Schaetzung;
import com.bencoderer.schaetzitmanager.dto.SchaetzerDTO;
import com.bencoderer.schaetzitmanager.helpers.SimpleCallback;

public class SyncSchaetzungenWithServerTask extends ServerSyncTask implements Runnable {

    public static final String TAG = com.bencoderer.schaetzitmanager.activities.HelloAndroidActivity.TAG;
  
    public static Date lastSuccessRun = null;
    

    public SyncSchaetzungenWithServerTask(SchaetzItManager mgr, SchaetzItServerManager mgrSvr, ReplaySubject<Integer> syncDone) {
       super(mgr, mgrSvr,syncDone);
    }

  
    @Override
    public void run() {
      Log.d(TAG, "run syncToServer in background");
      
        List<Schaetzer> curSchaetzungen;
        this.lastError = null;
      
        try
        {
          curSchaetzungen = _mgr.getSchaetzerToSyncToServer();
          
          sendSchaetzungenToServer(curSchaetzungen);
          this.status = true;
          lastSuccessRun = new Date();
          
          return;
        }
        catch(Throwable e) {
          Log.e(TAG, "SyncToServer BackgroundError: " + e.getMessage(), e);
          
          this.lastError = e.getMessage();
        }
          
        this.status = false;
        return;
    }
  
 
    public void sendSchaetzungenToServer(List<Schaetzer> schaetzerList) {
      Log.i(TAG, "send schaetzerList with entries:" + schaetzerList.size());
      
      SchaetzerDTO schaetzerDTO;
      for(Schaetzer schaetzer : schaetzerList)  {
        final Schaetzer curSchaetzer = schaetzer;
        schaetzerDTO = SchaetzItSyncManager.convertSchaetzerToSchaetzerDTO(curSchaetzer, _mgrSvr.createSchaetzerDTO(SchaetzItSyncManager.getServerId(curSchaetzer)));
        
        _mgrSvr.sendSchaetzerToServer(schaetzerDTO, new SimpleCallback() {

              @Override
              public void onSuccess(Object... objects) {
                curSchaetzer.sentToServerDate = new Date();
                _mgr.updateSchaetzer(curSchaetzer);
              }

              @Override
              public void onError(Throwable err) {
                  Log.e(TAG, err.toString());
              }
            });
      }
    }
}
