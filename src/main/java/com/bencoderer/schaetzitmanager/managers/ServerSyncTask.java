package com.bencoderer.schaetzitmanager.managers;


public class ServerSyncTask {
  
    protected SchaetzItManager _mgr;
  
    protected SchaetzItServerManager _mgrSvr;
    
  
    protected String lastError;

    protected boolean status = false;
  
    public ServerSyncTask(SchaetzItManager mgr, SchaetzItServerManager mgrSvr) {
      _mgr = mgr;
      _mgrSvr = mgrSvr;
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
