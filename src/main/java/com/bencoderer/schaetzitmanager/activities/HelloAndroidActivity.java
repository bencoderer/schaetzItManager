package com.bencoderer.schaetzitmanager.activities;

import com.activeandroid.content.ContentProvider;
import com.bencoderer.schaetzitmanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.bencoderer.schaetzitmanager.adapters.MatchingPersonAdapter;
import com.bencoderer.schaetzitmanager.data.Person;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.helpers.FileDialog;
import com.bencoderer.schaetzitmanager.managers.ImportPersonFromCSVTask;
import com.bencoderer.schaetzitmanager.managers.SchaetzItManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;

public class HelloAndroidActivity extends Activity {

    private final int SCHAETZER_LOADER = 1;
    private final int PERSON_LOADER = 2;
  
    private SchaetzItManager mMgr;
    private ListView vSchaetzerList;
    private EditText vSchaetzerRef;
  
    public final String TAG  = "SchaetzItManagerMain";
    private List<Person> mMatchingPersons;
    private ListView vMatchingPersons;
    private MatchingPersonAdapter mMatchingPersonAdapter;

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
      vSchaetzerRef = (EditText)this.findViewById(R.id.schaetzerRef);
      
      vSchaetzerList.setAdapter(new SimpleCursorAdapter(this,
        R.layout.simple_schaetzer_list_item,
        null,
        new String[] { Schaetzer.NAMEUNDADRESSE_COLUMN },
        new int[] { R.id.schaetzer_nameUndAdresse },
        0));
      
      
      vSchaetzerRef.addTextChangedListener(new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            lookupPersons(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            
            
        }
    });
      
       mMatchingPersons = new ArrayList<Person>();
       
       mMatchingPersonAdapter = new MatchingPersonAdapter(this,
                R.layout.listview_matchingperson_row, mMatchingPersons);
       
       
        vMatchingPersons = (ListView)findViewById(R.id.matching_persons);
        
        //View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
        //vMatchingPersons.addHeaderView(header);
       
        vMatchingPersons.setAdapter(mMatchingPersonAdapter);
    
      
      /*
      ArrayList<String> list = new ArrayList<String>();
      list.add("text1");
      
      vSchaetzerList.setAdapter(new ArrayAdapter<String>(this,R.layout.textview_schaetzer_list_item,list));
      */
    }
  
   /**
     * @param string
     */
    protected void lookupPersons(String lookupText) {
      AsyncTask<String,String,List<Person>> task = new AsyncTask<String,String,List<Person>>(){
        @Override
        protected List<Person> doInBackground(String... params) {

              Log.d(TAG, "fetch persons matching: " + params[0]);

              try{
               
                return mMgr.getPersonsMatching(params[0]);
                
              } catch (Exception e) {
                Log.e("Error", "Error for fetching person: " + e.toString());
                return null;
              }
          }

          protected void onPostExecute(List<Person> result)
          {
              mMatchingPersons.clear();
            
              if (result != null) {
                mMatchingPersons.addAll(result);
                mMatchingPersonAdapter.notifyDataSetChanged();
              }
              else {
                mMatchingPersonAdapter.notifyDataSetChanged();
                //mMatchingPersonAdapter.notifyDataSetInvalidated(); //not useful here
              }
          }
      };
      
      task.execute(lookupText);
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
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {

        case R.id.action_import_persons:
            importPersons();
            break;
        }
        return true;
    }
  


@Override
  public void onStart(){
      super.onStart();
      
      this.loadLatestSchaetzungen();
  }
  
    /**
     * 
     */
    private void importPersons() { 
      final Activity myActivity = this;
      final Context myContext = getApplicationContext();
      File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
      FileDialog fileDialog = new FileDialog(myActivity, mPath);
      fileDialog.setFileEndsWith(".csv");
      fileDialog.addFileListener(new FileDialog.FileSelectedListener() {

        @Override
        public void fileSelected(File file) {
          new ImportPersonFromCSVTask(myActivity,myActivity,file, mMgr).execute(); //execute asyncTask to import data into database from selected file.            
        }

      });
      fileDialog.showDialog();
        
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

