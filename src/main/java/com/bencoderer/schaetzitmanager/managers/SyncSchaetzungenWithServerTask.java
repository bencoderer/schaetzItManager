package com.bencoderer.schaetzitmanager.managers;

import java.util.List;
import java.lang.Runnable;
import android.os.AsyncTask;
import android.util.Log;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.data.Schaetzung;
import com.bencoderer.schaetzitmanager.dto.SchaetzerDTO;

public class SyncSchaetzungenWithServerTask implements Runnable {

    public static final String TAG = com.bencoderer.schaetzitmanager.activities.HelloAndroidActivity.TAG;
  
    private SchaetzItManager _mgr;
  
    private SchaetzItServerManager _mgrSvr;
    
  
    private String lastError;

    private boolean status = false;
  
    public void setStatus(boolean status) {
      this.status = status;
    }

    public boolean getStatus() {
      return status;
    }
    

    public SyncSchaetzungenWithServerTask(SchaetzItManager mgr, SchaetzItServerManager mgrSvr) {
      _mgr = mgr;
      _mgrSvr = mgrSvr;
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
          return;
        }
        catch(Throwable e) {
          Log.e(TAG, "SyncToServer BackgroundError: " + e.getMessage(), e);
          
          this.lastError = e.getMessage();
        }
          
        this.status = false;
        return;
    }
  
    public String getLastError() {
      return this.lastError;
    }
  
    
  
    public void sendSchaetzungenToServer(List<Schaetzer> schaetzerList) {
      Log.i(TAG, "send schaetzerList with entries:" + schaetzerList.size());
      
      SchaetzerDTO schaetzerDTO;
      for(Schaetzer schaetzer : schaetzerList)  {
        schaetzerDTO = SchaetzItSyncManager.convertSchaetzerToSchaetzerDTO(schaetzer, _mgrSvr.createSchaetzerDTO(SchaetzItSyncManager.getServerId(schaetzer)));
        
        _mgrSvr.sendSchaetzerToServer(schaetzerDTO);
      }
    }
}
