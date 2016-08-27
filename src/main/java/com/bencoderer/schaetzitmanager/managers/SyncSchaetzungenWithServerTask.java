package com.bencoderer.schaetzitmanager.managers;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.data.Schaetzung;
import com.bencoderer.schaetzitmanager.dto.SchaetzerDTO;

public class SyncSchaetzungenWithServerTask extends AsyncTask<String,String,Boolean> {

    public static final String TAG = com.bencoderer.schaetzitmanager.activities.HelloAndroidActivity.TAG;
  
    private SchaetzItManager _mgr;
  
    private SchaetzItServerManager _mgrSvr;
    
  
    private String lastError;

    

    public SyncSchaetzungenWithServerTask(SchaetzItManager mgr, SchaetzItServerManager mgrSvr) {
      _mgr = mgr;
      _mgrSvr = mgrSvr;
    }

  
    @Override
    protected Boolean doInBackground(String... arg0) {
      Log.d(TAG, "run syncToServer in background");
      
        List<Schaetzer> curSchaetzungen;
        this.lastError = null;
      
        try
        {
          curSchaetzungen = _mgr.getSchaetzerToSyncToServer();
          
          sendSchaetzungenToServer(curSchaetzungen);
          return true;
        }
        catch(Throwable e) {
          Log.e(TAG, "SyncToServer BackgroundError: " + e.getMessage(), e);
          
          this.lastError = e.getMessage();
        }
          
        return false;
    }
  
    public String getLastError() {
      return this.lastError;
    }
  
    
  
    public void sendSchaetzungenToServer(List<Schaetzer> schaetzerList) {
      Log.i(TAG, "send schaetzerList with entries:" + schaetzerList.size());
      
      SchaetzerDTO schaetzerDTO;
      for(Schaetzer schaetzer : schaetzerList)  {
        schaetzerDTO = SchaetzItSyncManager.convertSchaetzerToSchaetzerDTO(schaetzer, _mgrSvr.createSchaetzerDTO());
        
        _mgrSvr.sendSchaetzerToServer(schaetzerDTO);
      }
    }
}
