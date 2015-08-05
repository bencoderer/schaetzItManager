package com.bencoderer.schaetzitmanager.activities;

import com.bencoderer.schaetzitmanager.R;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.bencoderer.schaetzitmanager.managers.SchaetzItManager;

public class HelloAndroidActivity extends Activity {

    private SchaetzItManager mMgr;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     *         If the activity is being re-initialized after
     *         previously being shut down then this Bundle contains the data it most
     *         recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        mMgr = new SchaetzItManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

  public void addSchaetzung(View view){
      EditText nameAndAddr = (EditText) findViewById(R.id.schaetzerRef);
      String sRef= nameAndAddr.getText().toString();
    
      List<Integer> schaetzungen = new ArrayList<Integer>();
      schaetzungen.add(Integer.parseInt(((EditText)findViewById(R.id.schaetzerRef)).getText().toString()));
    
      mMgr.addSchaetzer(sRef, schaetzungen, null);
		
        
      showToast("Neue Schätzung von: " + sRef);
	}

/**
 * 
 */
private void showToast(String msg) {
    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
}
  
}

