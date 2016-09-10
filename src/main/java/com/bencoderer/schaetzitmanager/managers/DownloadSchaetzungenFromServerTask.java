package com.bencoderer.schaetzitmanager.managers;

import java.util.Date;
import java.util.List;
import java.lang.Runnable;
import android.util.Log;
import com.bencoderer.schaetzitmanager.data.Schaetzung;
import com.bencoderer.schaetzitmanager.dto.OperatorDTO;
import com.bencoderer.schaetzitmanager.dto.SchaetzerDTO;
import com.bencoderer.schaetzitmanager.helpers.SimpleCallback;
import com.strongloop.android.loopback.callbacks.ListCallback;

public class DownloadSchaetzungenFromServerTask extends ServerSyncTask implements Runnable {

    public static final String TAG = com.bencoderer.schaetzitmanager.activities.HelloAndroidActivity.TAG;
   

    public DownloadSchaetzungenFromServerTask(SchaetzItManager mgr, SchaetzItServerManager mgrSvr) {
      super(mgr, mgrSvr);
    }

  
    @Override
    public void run() {
      Log.d(TAG, "run downloadFromServer in background");

        this.lastError = null;
        this.status = false;
      
        try
        {
           OperatorDTO curOperatorDTO = SchaetzItSyncManager.convertOperatorToOperatorDTO(_mgr.getCurrentOperator());
      
          Log.d(TAG, "begin reading not synced schaetzer for operator:" + ( (curOperatorDTO == null) ? "null" : curOperatorDTO.getOperatorKey()));
        
          _mgrSvr.getNotSyncedSchaetzer(curOperatorDTO, new ListCallback<SchaetzerDTO>() {

                @Override
                public void onSuccess(List<SchaetzerDTO> arg0) {
                  updateSchaetzerFromSchaetzerDTO(arg0);
                }

                
                @Override
                public void onError(Throwable err) {
                    Log.e(TAG, err.toString());
                }

          });

          this.status = true;
          return;
        }
        catch(Throwable e) {
          Log.e(TAG, "downloadFromServer BackgroundError: " + e.getMessage(), e);
          
          this.lastError = e.getMessage();
        }
          
        this.status = false;
        return;
    }
  
    public String getLastError() {
      return this.lastError;
    }
  
    
  
    public void updateSchaetzerFromSchaetzerDTO(List<SchaetzerDTO> schaetzerList) {
      Log.i(TAG, "update from schaetzerList with entries:" + schaetzerList.size());
      
      OperatorDTO curOperator = SchaetzItSyncManager.convertOperatorToOperatorDTO(_mgr.getCurrentOperator());
      
      for(SchaetzerDTO schaetzer : schaetzerList)  {
        final SchaetzerDTO curSchaetzer = schaetzer;
        List<Schaetzung> schaetzungen = SchaetzItSyncManager.convertSchaetzerDTOToSchaetzungen(curSchaetzer);
        
        _mgr.addOrUpdateSchaetzerByOperator(schaetzer.getOperatorKey(),schaetzer.getIdInOperatorDb(), schaetzer.getNameAndAddress(), schaetzungen,null );
        
        _mgrSvr.setSchaetzerAsSyncedTo(curSchaetzer,curOperator, new SimpleCallback() {
              @Override
              public void onSuccess(Object... objects) {
                Date sentDate = (Date)objects[0];
                Log.i(TAG, "Schaetzer " + curSchaetzer.getId() + " als gesynct mit der aktuellen Datenbank markiert um " + sentDate);
              }

              @Override
              public void onError(Throwable err) {
                  Log.e(TAG, err.toString());
              }
        });
      }
      
    }
}
