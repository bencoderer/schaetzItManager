package com.bencoderer.schaetzitmanager.activities;

import java.util.List;
import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;
import com.bencoderer.schaetzitmanager.R;

import com.bencoderer.schaetzitmanager.managers.SchaetzItManager;
import com.bencoderer.schaetzitmanager.data.Operator;

import com.bencoderer.schaetzitmanager.managers.DownloadSchaetzungenFromServerTask;
import com.bencoderer.schaetzitmanager.managers.SyncSchaetzungenWithServerTask;

public class StatsActivity extends Activity {

    private SchaetzItManager mMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        mMgr = new SchaetzItManager();
        
        updateStats();
    }
    
    private void updateStats() {
        List<Operator> operatorList = mMgr.getAllOperator();
        
        //ADM is not relevant here
        Iterator<Operator> iter = operatorList.iterator();
        while (iter.hasNext()) {
            Operator element = iter.next();
            if ("ADM".equals(element.key)) {
                iter.remove();
            }
        }
        
        TextView operatorNameOP1 = (TextView) findViewById(R.id.statsOP1OperatorName);
        TextView schaetzerCountOP1 = (TextView) findViewById(R.id.statsOP1SchaetzerCount);
        TextView schaetzerSyncedCountOP1 = (TextView) findViewById(R.id.statsOP1SchaetzerSyncedCount);
        TextView operatorNameOP2 = (TextView) findViewById(R.id.statsOP2OperatorName);
        TextView schaetzerCountOP2 = (TextView) findViewById(R.id.statsOP2SchaetzerCount);
        TextView schaetzerSyncedCountOP2 = (TextView) findViewById(R.id.statsOP2SchaetzerSyncedCount);
        
        operatorNameOP1.setText("");
        schaetzerCountOP1.setText("");
        operatorNameOP2.setText("");
        schaetzerCountOP2.setText("");
        
        if (operatorList.size() >= 1) {
            
            updateStatsOfOperator(operatorList.get(0), operatorNameOP1, schaetzerCountOP1,schaetzerSyncedCountOP1);
        }
        
        if (operatorList.size() >= 2) {
            updateStatsOfOperator(operatorList.get(1), operatorNameOP2, schaetzerCountOP2,schaetzerSyncedCountOP2);
        }
        
        
        TextView lastUpload = (TextView) findViewById(R.id.statsLastUpload);
        TextView lastDownload = (TextView) findViewById(R.id.statsLastDownload);
        
        lastUpload.setText("");
        lastDownload.setText("");
        if (DownloadSchaetzungenFromServerTask.lastSuccessRun != null) {
            lastDownload.setText(DownloadSchaetzungenFromServerTask.lastSuccessRun.toString());
        }
        if (SyncSchaetzungenWithServerTask.lastSuccessRun != null) {
            lastUpload.setText(SyncSchaetzungenWithServerTask.lastSuccessRun.toString());
        }
    }
    
    private void updateStatsOfOperator(Operator operator, TextView operatorName, TextView schaetzerCount, TextView syncedToServerCount) {
        int count = mMgr.countSchaetzerOfOperator(operator.key);
        
        int syncedCount = mMgr.countSchaetzerOfOperatorSynced(operator.key);
        
        operatorName.setText(operator.name);
        schaetzerCount.setText(Integer.toString(count));
        syncedToServerCount.setText(Integer.toString(syncedCount));
    }
    
    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}