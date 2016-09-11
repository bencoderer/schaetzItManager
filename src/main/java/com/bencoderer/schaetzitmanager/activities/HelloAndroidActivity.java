package com.bencoderer.schaetzitmanager.activities;

import com.activeandroid.content.ContentProvider;
import com.bencoderer.schaetzitmanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.functions.Action1;


import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.bencoderer.schaetzitmanager.adapters.MatchingPersonAdapter;
import com.bencoderer.schaetzitmanager.data.Person;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.data.Operator;
import com.bencoderer.schaetzitmanager.dto.OperatorDTO;
import com.bencoderer.schaetzitmanager.helpers.FileDialog;
import com.bencoderer.schaetzitmanager.helpers.SimpleCallback;
import com.bencoderer.schaetzitmanager.managers.ExportSchaetzungenToExcelFile;
import com.bencoderer.schaetzitmanager.managers.ImportPersonFromCSVTask;
import com.bencoderer.schaetzitmanager.managers.SchaetzItManager;
import com.bencoderer.schaetzitmanager.managers.SchaetzItServerManager;
import com.bencoderer.schaetzitmanager.managers.SchaetzItSyncManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import com.strongloop.android.loopback.callbacks.ListCallback;

public class HelloAndroidActivity extends Activity {

    private final int SCHAETZER_LOADER = 1;
    private final int PERSON_LOADER = 2;
  
    private SchaetzItManager mMgr;
    private Operator currentOperator = null;
  
    private SchaetzItServerManager mServerMgr;
    private SchaetzItSyncManager mSyncMgr;
  
    private ListView vSchaetzerList;
    private EditText vSchaetzerRef;
  
    public static final String TAG  = "SchaetzItManagerMain";
    private List<Person> mMatchingPersons;
    private ListView vMatchingPersons;
    private MatchingPersonAdapter mMatchingPersonAdapter;

  
    private ReplaySubject<Integer> writeExcelFileSubject = ReplaySubject.create(60);
  
    private Date nextServerErrorShow = new Date(2000 - 1900, 1, 1);

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
      
        final Context myContext = getApplicationContext();
      
        final HelloAndroidActivity myActivity = this;
      
        //this pipeline does not work yet
        writeExcelFileSubject
          .debounce(2, TimeUnit.SECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer aLong) {
                      try
                        {
                        exportSchaetzungenToDefaultExcelFile(false);
                      }
                      catch(Exception ex) {
                        Log.e(TAG, "Schreiben der Excel-Datei:"+  ex);
                      }
                    }
                },new Action1<Throwable>() {

                  public void call(Throwable t1) {
                      t1.printStackTrace();
                  }
              });
      
        mMgr = new SchaetzItManager();
        mServerMgr = new SchaetzItServerManager(myContext); 
      
        mSyncMgr = new SchaetzItSyncManager(mMgr, new SchaetzItServerManager(myContext), new SimpleCallback() {

              /*onNOTIFICATION*/
          
              @Override
              public void onSuccess(Object... objects) {
                myActivity.sendNotification(objects[0].toString());
              }

              @Override
              public void onError(Throwable err) {
                  Log.e(TAG, err.toString());
              }
            },
            new SimpleCallback() {

              /*onDownloadDone*/
          
              @Override
              public void onSuccess(Object... objects) {
                int syncedCount = (Integer)objects[0];
                if (syncedCount > 0) {
                  //writeExcelFileSubject.onNext(0);
                  exportSchaetzungenToDefaultExcelFile(false);
                }
              }

              @Override
              public void onError(Throwable err) {
                  Log.e(TAG, err.toString());
                  Log.d(TAG, "nextservererrorShow:"+  nextServerErrorShow);
                  if (nextServerErrorShow.before(new Date())) {
                    myActivity.sendNotification(err.getMessage());
                     final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

                     long curTimeInMs = new Date().getTime();
                     nextServerErrorShow = new Date(curTimeInMs + (2 * ONE_MINUTE_IN_MILLIS));
                  }
              }
            }                               
            ); 
      
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
          vSchaetzerRef.setTag(null);
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
    
       vMatchingPersons.setOnItemClickListener(new OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Person selPerson = (Person)parent.getItemAtPosition(position);
              
            if (selPerson != null)  {
              fillSchaetzerWithPersonData(selPerson);
            }
          }
        });
      
       loadCurrentOperator();
          
      /*
      ArrayList<String> list = new ArrayList<String>();
      list.add("text1");
      
      vSchaetzerList.setAdapter(new ArrayAdapter<String>(this,R.layout.textview_schaetzer_list_item,list));
      */
    }
  
  
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mSyncMgr.quit();
    }
  
   /**
     * @return
     */
    private String getExcelExportDir() {
      
      File basepath = Environment.getExternalStorageDirectory();
      if (basepath == null) {
        Log.e(TAG, "Path to external storage could not be determined. Missing permission?");
        return null;
      }
      
      File exportDir = new File(basepath + "/"
                + "SchaetzItManager", "export");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
      
      return exportDir.getAbsolutePath();
    }
  
    private String getExcelImportFileTemplate() {    
        return "SchaetzItManager_Vorlage%d";
    }
  
  private String getExcelImportDir() {
      
      File basepath = Environment.getExternalStorageDirectory();
      if (basepath == null) {
        Log.e(TAG, "Path to external storage could not be determined. Missing permission?");
        return null;
      }
      
      File importDir = new File(basepath + "/"
                + "SchaetzItManager", "import");
        if (!importDir.exists()) {
            importDir.mkdirs();
        }
      
      return importDir.getAbsolutePath();
    }
  
    private String getExcelExportFileTemplate() {    
        return "SchaetzItManager_Ergebnis%d";
    }
  
  private int getExcelExportStaffelSize() {
    return 200;
  }
  
  protected int exportInterval = 1;
  
    protected void exportSchaetzungenToDefaultExcelFile(final boolean exportAndClear) {
      String postfix = "";
      if (exportAndClear) {
        postfix = "-preclean";
      }
      
      ExportSchaetzungenToExcelFile exporterTask = new ExportSchaetzungenToExcelFile(mMgr, getExcelExportDir(), getExcelExportFileTemplate() + postfix, getExcelExportStaffelSize(), getExcelImportDir(), getExcelImportFileTemplate()) {
          @Override
          protected void onPostExecute(Boolean status)
          {
              if (!status){
                sendNotification("Excel Datei wurde nicht erstellt!"+"\n" + this.getLastError());
              }
              else if (exportAndClear) {
                mMgr.clearSchaetzer();
                showToast("Sicherung erstellt und Schätzungen gelöscht.");
              }
          }
          
        };
      
      exporterTask.execute();
      
      if (!exportAndClear) {
        postfix = String.format("-V%d", this.exportInterval);
        this.exportInterval = (this.exportInterval % 5) + 1;
        
         ExportSchaetzungenToExcelFile exporterTask2 = new ExportSchaetzungenToExcelFile(mMgr, getExcelExportDir(), getExcelExportFileTemplate() + postfix, getExcelExportStaffelSize(), getExcelImportDir(), getExcelImportFileTemplate()) {
          @Override
          protected void onPostExecute(Boolean status)
          {
              if (!status){
                sendNotification("Excel Datei wurde nicht erstellt!"+"\n" + this.getLastError());
              }
          }
          
        };
      
        exporterTask2.execute();
      }
    }
  
  

protected void fillSchaetzerWithPersonData(Person person) {
     vSchaetzerRef.setTag(person);
     vSchaetzerRef.setText(person.name + ", " + person.adresse);
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

          @Override
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
       
       //menu.setHeaderTitle("Context Menu");
       menu.add(0, v.getId(), 0, "Löschen");
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
      final Activity myActivity = this;
      
        switch(item.getItemId()) {

        case R.id.action_import_persons:
            importPersons();
            break;
        case R.id.action_reset_schaetzungen:
            resetSchaetzungen();
            break;
          
        case R.id.action_choose_operator:
            loadOperatorsFromServer(new SimpleCallback() {

              @Override
              public void onSuccess(Object... objects) {
                chooseOperator();
              }

              @Override
              public void onError(Throwable err) {
                  Log.i(myActivity.getClass().getSimpleName(), "chooseOperator uebersprungen, da laden vom server fehlschlug.");
              }
            }
      		);
            break;
        case R.id.action_reset_syncstate_local:
          resetSyncStateLocalDB();
          break;
        }
      
        return true;
    }
  


@Override
  public void onStart(){
      super.onStart();
      
      //this.loadLatestSchaetzungen();
  }
  
  private class Holder<T> {
      public T value;
  }
  
  private void loadCurrentOperator() {
    //yXloadCurrentOperator
    this.currentOperator = mMgr.getCurrentOperator();
    
    ArrayList<OperatorDTO> opList = new ArrayList<OperatorDTO>();
    for(Operator op : this.mMgr.getAllOperator()) {
      opList.add(SchaetzItSyncManager.convertOperatorToOperatorDTO(op));
    }
    
    this.mSyncMgr.getServerManager().setOperatorListForSync(opList);
    
    
    TextView operatorName = (TextView) findViewById(R.id.operatorName);
    if (this.currentOperator != null) {
      operatorName.setText(this.currentOperator.toDisplayText());
      mMgr.setOperatorKey(this.currentOperator.key);
    }
    else {
      operatorName.setText("<kein Benutzer>");
      mMgr.setOperatorKey(null);
    }
    
    this.loadLatestSchaetzungen(); //maybe we changed the operator, so restart the loader
  }
  
  private void resetSyncStateLocalDB() {
    try
      {
      showToast("Sync Status zurücksetzen...");
      mMgr.resetSyncToServerState();
      showToast("Sync Status zurücksetzen...erfolgreich");
    }
    catch(Exception ex) {
      showToast("Reset SyncStatus Fehler:"+  ex);
    }
  }
  
  private void chooseOperator() {
    final List<Operator> opList = mMgr.getAllOperator();
    
    CharSequence operators[] = new CharSequence[opList.size()];

    for (int i=0; i < opList.size(); i++) {
      operators[i] = opList.get(i).name;
    }
    
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Benutzer wählen");
    builder.setItems(operators, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            for (int i=0; i < opList.size(); i++) {
              Operator curOp = opList.get(i);
              curOp.used = false;
              
              if (i == which) { curOp.used = true; };
              
              mMgr.updateOperator(curOp);
            }
          
            loadCurrentOperator();
        }
    });
    builder.show();
  }
  
  
  private void loadOperatorsFromServer(final SimpleCallback callback) {
    //xLoadOperatorsFromServer
    
    mServerMgr.getOperatorsAll(
      new ListCallback<OperatorDTO>() {

        @Override
        public void onSuccess(List<OperatorDTO> operatorList) {
          try {
            if (operatorList != null) {
              mMgr.clearOperator(); 

              for(OperatorDTO op : operatorList) {
                mMgr.addOperator(op.getOperatorKey(), op.getName());
              }
              callback.onSuccess();
            }
          }
          catch(Exception e) {
            showToast("Laden der Benutzer in die lokale Datenbank fehlgeschlagen!!!");
            Log.e(e.getClass().getName(), "exception", e);
            callback.onError(e);
          }  
        }

        @Override
        public void onError(Throwable err) {
          callback.onError(err);
        }
      }
    );
  }
  
  private void resetSchaetzungen() {
    final Holder<Boolean> doReset = new Holder<Boolean>();
    doReset.value = false;
    
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                exportSchaetzungenToDefaultExcelFile(true);
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                //No button clicked
                break;
            }
        }
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Sollen wirklich alle Schätzungen gelöscht werden?").setPositiveButton("JA", dialogClickListener)
        .setNegativeButton("Abbrechen", dialogClickListener).show();
      
  
    /*
    if (doReset.value) {
      builder.setMessage("Sind Sie sicher?").show();
      if (doReset.value) {
        
      }
    }
    */
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

        showToast("Neue Schätzung von: " + sRef + " / " + schaetzungen.size() + " Schätzwerte");
        clearForm((ViewGroup)findViewById(R.id.schaetzer));
        nameAndAddr.requestFocus();
        
        //writeExcelFileSubject.onNext(1);
        exportSchaetzungenToDefaultExcelFile(false);
        loadLatestSchaetzungen();
        
        mSyncMgr.doSyncNow();
      }
      catch(Exception e) {
        showToast("Fehlgeschlagen!!!");
        Log.e(TAG, "exception in addSchaetzung", e);
      }
	}

  private void loadLatestSchaetzungen() {
    //final ListView schaetzerList = (ListView)findViewById(R.id.listNeuesteSchaetzungen);
    
    final Activity myActivity = this;
    int count = mMgr.getAllSchaetzer().size();
    Log.d(TAG, "reloading latest schaetzungen. Count:" + count);
      
    //((TextView)findViewById(R.id.anzahl_letzte_schaetzungen)).setText(coun(type[]) collection.toArray(new type[collection.size()]).toString()); //TODO crashes
    
    
    this.getLoaderManager().restartLoader(SCHAETZER_LOADER, null, new LoaderManager.LoaderCallbacks<Cursor>() {
      @Override
      public android.content.Loader<Cursor> onCreateLoader(int arg0, Bundle cursor) {
        String filter = null;
        String[] filterValues = null;
        
        if (mMgr.getOperatorKey() != null) {
          filter = Schaetzer.OPERATORKEY_COLUMN + " = ?";
          filterValues = new String[] {mMgr.getOperatorKey()};
        }
        
        return new CursorLoader(myActivity,
                                ContentProvider.createUri(Schaetzer.class, null),
                                null,filter , filterValues, Schaetzer.INDATE_COLUMN+" desc"
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
      else if (view instanceof ViewGroup) {
        collectAllSchaetzwerte((ViewGroup)view, schaetzungen);
      }
	 }//foreach group
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
  
  
  private void sendNotification(String text)  {
    Context context = this.getApplicationContext();
    Intent notificationIntent = new Intent(this, HelloAndroidActivity.class);  
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                                      notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    
    String title = "SchätzIt Manager";

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
      .setSmallIcon(R.drawable.ic_launcher)
      .setContentTitle(title)
      .setContentText(text)
      .setContentIntent(pendingIntent);
    Notification notification = mBuilder.getNotification();
    // default phone settings for notifications
    notification.defaults |= Notification.DEFAULT_VIBRATE;
    notification.defaults |= Notification.DEFAULT_SOUND;

    // cancel notification after click
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    // show scrolling text on status bar when notification arrives
    notification.tickerText = title + "\n" + text;

    // notifiy the notification using NotificationManager
    NotificationManager notificationManager = (NotificationManager) context
      .getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(0 /*NOTIFICATION_ID*/, notification);
  }
  
}

