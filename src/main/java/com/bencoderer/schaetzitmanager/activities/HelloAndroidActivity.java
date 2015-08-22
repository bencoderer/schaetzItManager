package com.bencoderer.schaetzitmanager.activities;

import com.activeandroid.content.ContentProvider;
import com.bencoderer.schaetzitmanager.R;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.managers.SchaetzItManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;

public class HelloAndroidActivity extends Activity {

    private final int SCHAETZER_LOADER = 1;
    private final int PERSON_LOADER = 2;
  
    private SchaetzItManager mMgr;
    private ListView vSchaetzerList;

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
      
      vSchaetzerList = (ListView)this.findViewById(R.id.listNeuesteSchaetzungen);
      this.registerForContextMenu(vSchaetzerList);
      
      
      vSchaetzerList.setAdapter(new SimpleCursorAdapter(this,
        R.layout.simple_schaetzer_list_item,
        null,
        new String[] { Schaetzer.NAMEUNDADRESSE_COLUMN },
        new int[] { R.id.schaetzer_nameUndAdresse },
        0));
      
      /*
      ArrayList<String> list = new ArrayList<String>();
      list.add("text1");
      
      vSchaetzerList.setAdapter(new ArrayAdapter<String>(this,R.layout.textview_schaetzer_list_item,list));
      */
    }
  
   @Override
   public void onCreateContextMenu(ContextMenu menu, View v,
           ContextMenuInfo menuInfo) {
       super.onCreateContextMenu(menu, v, menuInfo);
       
       menu.setHeaderTitle("Context Menu");
       menu.add(0, v.getId(), 0, "Action 1");
       menu.add(0, v.getId(), 0, "Action 2");
   }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

  @Override
  public void onStart(){
      super.onStart();
      
      this.loadLatestSchaetzungen();
  }
  
  public void addSchaetzung(View view){
      try
      {
        EditText nameAndAddr = (EditText) findViewById(R.id.schaetzerRef);
        String sRef= nameAndAddr.getText().toString();

        List<Integer> schaetzungen = new ArrayList<Integer>();

        collectAllSchaetzwerte((ViewGroup)findViewById(R.id.schaetzwerte), schaetzungen);

        mMgr.addSchaetzer(sRef, schaetzungen, null);

        showToast("Neue Schätzung von: " + sRef);
        clearForm((ViewGroup)findViewById(R.id.schaetzer));
        
        loadLatestSchaetzungen();
      }
      catch(Exception e) {
        showToast("Fehlgeschlagen!!!");
        Log.e(e.getClass().getName(), "exception", e);
      }
	}

  private void loadLatestSchaetzungen() {
    //final ListView schaetzerList = (ListView)findViewById(R.id.listNeuesteSchaetzungen);
    
    final Activity myActivity = this;
    int count = mMgr.GetAllSchaetzer().size();
    Log.d(myActivity.getClass().getSimpleName(), "reloading latest schaetzungen. Count:" + count);
      
    
    
    this.getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
      @Override
      public android.content.Loader<Cursor> onCreateLoader(int arg0, Bundle cursor) {
        return new CursorLoader(myActivity,
                                ContentProvider.createUri(Schaetzer.class, null),
                                null, null, null, Schaetzer.INDATE_COLUMN+" desc"
                               );
      }

      @Override
      public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        Log.d(myActivity.getClass().getSimpleName(), "reloading latest schaetzungen - finished");
        
        ((SimpleCursorAdapter)vSchaetzerList.getAdapter()).swapCursor(cursor);
        //((ArrayAdapter<String>)vSchaetzerList.getAdapter()).add("more");
      }

      @Override
      public void onLoaderReset(Loader<Cursor> arg0) {
        Log.d(myActivity.getClass().getSimpleName(), "reloading latest schaetzungen - resetted");
        ((SimpleCursorAdapter)vSchaetzerList.getAdapter()).swapCursor(null);
      }
    });
    
  }
  
/**
 * 
 */
private void showToast(String msg) {
    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
}
  
  private void collectAllSchaetzwerte(ViewGroup group, List<Integer> schaetzungen)
  {       
    for (int i = 0, count = group.getChildCount(); i < count; ++i) {
      View view = group.getChildAt(i);
      if (view instanceof EditText) {
          String text = ((EditText)view).getText().toString();
          if (!text.isEmpty()) {
          	schaetzungen.add(Integer.parseInt(text));
          }
      }

      if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0)) {
          clearForm((ViewGroup)view);
      }
	 }
  }
  
  
  
  private void clearForm(ViewGroup group)
  {       
    for (int i = 0, count = group.getChildCount(); i < count; ++i) {
      View view = group.getChildAt(i);
      if (view instanceof EditText) {
          ((EditText)view).setText("");
      }

      if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0)) {
          clearForm((ViewGroup)view);
      }
	 }
  }
  
}

