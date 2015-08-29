package com.bencoderer.schaetzitmanager.managers;

import java.io.File;
import java.io.FileReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;
import com.bencoderer.schaetzitmanager.managers.SchaetzItManager;

//Source: http://stackoverflow.com/questions/16672074/import-csv-file-to-sqlite-in-android
public class ImportPersonFromCSVTask extends AsyncTask<String, String, Boolean> {

    //private Activity activity;
    private Context context;
    private File file=null;
    private ProgressDialog dialog;
    private SchaetzItManager mgr;
    

    public ImportPersonFromCSVTask(Context context, Activity activity,File file, SchaetzItManager mgr) {
        this.context=context;
        //this.activity=activity;
        this.file=file;
        this.mgr = mgr;
    }

    @Override
    protected void onPreExecute()
    {
        dialog=new ProgressDialog(context);
        dialog.setTitle("Importing Data into SchaetzItManager.Person DataBase");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        //dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.show();
    }

@Override
protected Boolean doInBackground(String... params) {

      Log.d(getClass().getName(), file.toString());

      char separator = ';';
      int iTryCnt = 2;
  
      try{
        do {
          mgr.clearPersons();
          CSVReader reader = new CSVReader(new FileReader(file), separator );
          String [] nextLine;

          //here I am just displaying the CSV file contents, and you can store your file content into db from while loop...

          while ((nextLine = reader.readNext()) != null) {

            // nextLine[] is an array of values from the line

            String name=nextLine[0].trim();
            String adresse=nextLine[1].trim();

            mgr.addPerson(name, adresse);
          }
          iTryCnt = 0; //success, do not try again
          separator = ',';
        }
        while(iTryCnt-- > 0);

      } catch (Exception e) {
        Log.e("Error", "Error for importing file: " + e.toString());
        return false;
      }

      return true;
  }

  @Override
  protected void onPostExecute(Boolean success)
  {

      if (dialog.isShowing())
      {
          dialog.dismiss();
      }

      if (success)
      {
          Toast.makeText(context, "Import erfolgreich", Toast.LENGTH_LONG).show();
      }else{
          Toast.makeText(context, "Import fehlgeschlagen!", Toast.LENGTH_SHORT).show();
      }
  }


}