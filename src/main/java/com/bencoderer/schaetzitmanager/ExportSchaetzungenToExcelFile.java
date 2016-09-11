package com.bencoderer.schaetzitmanager.managers;

import jxl.*;
import jxl.CellView;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.data.Schaetzung;

public class ExportSchaetzungenToExcelFile extends AsyncTask<String,String,Boolean> {

    public static final String TAG = "SchaetzItManagerExcelExport";
  
    private SchaetzItManager _mgr;
    
  
    private String lastError;

    private String _targetDir;

    private String _targetFileNameTemplate;

    private int _staffelSize;

    private String _sourceDir;

    private String _sourceFileNameTemplate;

    public ExportSchaetzungenToExcelFile(SchaetzItManager mgr, String targetDir, String targetFileNameTemplate, int staffelSize, String sourceDir, String sourceFileNameTemplate) {
      _mgr = mgr;
      _targetDir = targetDir;
      _targetFileNameTemplate = targetFileNameTemplate;
      _staffelSize = staffelSize;
    
      _sourceDir = sourceDir;
      _sourceFileNameTemplate = sourceFileNameTemplate;
    }

  
    @Override
    protected Boolean doInBackground(String... arg0) {
        List<Schaetzer> curSchaetzungen;
        this.lastError = null;
      
        try
        {
          curSchaetzungen = _mgr.getAllSchaetzer(true);
          exportSchaetzungenToExcelFile(curSchaetzungen, this._targetDir, this._targetFileNameTemplate);
          return true;
        }
        catch(Exception e) {
          Log.e(TAG, "BackgroundError: " + e.getMessage());
          
          this.lastError = e.getMessage();
        }
          
        return false;
    }
  
    public String getLastError() {
      return this.lastError;
    }
  
    protected int getSchaetzerFileStaffel(int schaetzerCount) {
      int result = schaetzerCount / _staffelSize * _staffelSize;
      if ((result % _staffelSize) >= 0) {
        result = result + _staffelSize;
      }
      
      if (result < _staffelSize) {
        return _staffelSize;
      }
      
      return result;
    }
  
    public void exportSchaetzungenToExcelFile(List<Schaetzer> schaetzerList, String targetDir, String targetFileNameTemplate) throws IOException,jxl.write.WriteException, jxl.read.biff.BiffException {
      Log.d(ExportSchaetzungenToExcelFile.TAG, "exporting schaetzerList with entries:" + schaetzerList.size());
      
      String targetfilepath = targetDir + "/" + String.format(targetFileNameTemplate, getSchaetzerFileStaffel(schaetzerList.size())) + ".xls";
      String sourcefilepath = this._sourceDir + "/" + String.format(this._sourceFileNameTemplate, getSchaetzerFileStaffel(schaetzerList.size())) + ".xls";
      
      Workbook template = Workbook.getWorkbook(new File(sourcefilepath)); 

      WritableWorkbook workbook = Workbook.createWorkbook(new File(targetfilepath), template); 
      //WritableWorkbook workbook = Workbook.createWorkbook(new File(targetfilepath)); 
      //CreationHelper createHelper = wb.getCreationHelper();
      WritableSheet sheet = workbook.getSheet("Eingabe"); 
      if (sheet == null){
      	sheet = workbook.createSheet("Eingabe", 0); 
      }

      int columnCount = 0;
      
      short iRowPos = 0;
      short iColPos = 0;
      
      Label label = new Label(iColPos, iRowPos, "Name und Adresse");
      sheet.addCell(label);
      
      for(Schaetzer schaetzer : schaetzerList)  {
        iColPos = 0;
        label = new Label(iColPos, ++iRowPos, schaetzer.nameUndAdresse + " ("+ schaetzer.operatorKey +")");
        sheet.addCell(label);
        
        for (Schaetzung schaetzung : schaetzer.schaetzungen()){
        		jxl.write.Number number = new jxl.write.Number(++iColPos, iRowPos, schaetzung.schaetzwert);
            sheet.addCell(number);   
        }
        
        if (iColPos > columnCount) {
          columnCount = iColPos;
        }
      }
      
      CellView cell;
      
      iColPos = 0;
      iRowPos = 0;
      //autosize based on content length
      for(int x=0;x<columnCount;x++)
      {
          /*add header*/
          label = new Label(++iColPos, iRowPos, "Schätzung " + iColPos);
          sheet.addCell(label);
        
          cell = sheet.getColumnView(x);
          cell.setAutosize(true);
          sheet.setColumnView(x, cell);
      }
      
      // Write the output to a file
      workbook.write();
      workbook.close(); 
    }
}
