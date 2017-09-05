package com.bencoderer.schaetzitmanager.managers;

import rx.subjects.ReplaySubject;

public class ServerSyncTask {
  
    protected SchaetzItManager _mgr;
  
    protected SchaetzItServerManager _mgrSvr;
    
    protected ReplaySubject<Integer> _syncDone;
  
    protected String lastError;

    protected boolean status = false;
  
    public ServerSyncTask(SchaetzItManager mgr, SchaetzItServerManager mgrSvr, ReplaySubject<Integer> syncDone) {
      _mgr = mgr;
      _mgrSvr = mgrSvr;
      _syncDone = syncDone;
    }
  
    public void setStatus(boolean status) {
      this.status = status;
    }

    public boolean getStatus() {
      return status;
    }
    
  
    public String getLastError() {
      return this.lastError;
    }
  
}
