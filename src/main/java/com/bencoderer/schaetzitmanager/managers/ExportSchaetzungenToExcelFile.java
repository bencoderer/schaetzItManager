/*
package com.bencoderer.schaetzitmanager.managers;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.data.Schaetzung;
import com.bencoderer.schaetzitmanager.managers.SchaetzItManager;

public class ExportSchaetzungenToExcelFilePOI extends AsyncTask<String,String,Boolean> {

    public final String TAG = "SchaetzItManagerExcelExport";
  
    private SchaetzItManager _mgr;
    private String _targetfilepath;
  
    private String lastError;

    public ExportSchaetzungenToExcelFile(SchaetzItManager mgr, String targetfilepath) {
      _mgr = mgr;
      _targetfilepath = targetfilepath;
    }
  
    public void setTargetfilepath(String value) {
      this._targetfilepath = value;
    }
  
    @Override
    protected Boolean doInBackground(String... arg0) {
        List<Schaetzer> curSchaetzungen;
        this.lastError = null;
      
        try
        {
          curSchaetzungen = _mgr.getAllSchaetzer(true);
          ExportSchaetzungenToExcelFile.exportSchaetzungenToExcelFile(curSchaetzungen, this._targetfilepath);
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
  
    public static void exportSchaetzungenToExcelFile(List<Schaetzer> schaetzerList, String targetfilepath) throws IOException {
      FileOutputStream fileOut = new FileOutputStream(targetfilepath);
      
      //Workbook wb = new HSSFWorkbook();
      Workbook wb = new XSSFWorkbook();
      //CreationHelper createHelper = wb.getCreationHelper();
      Sheet sheet = wb.createSheet("Erfassung");

      // Create a row and put some cells in it. Rows are 0 based.
      short iRowPos = 0;
      for(Schaetzer schaetzer : schaetzerList)  {
        Row row = sheet.createRow((iRowPos++));
        // Create a cell and put a value in it.
        Cell cell = row.createCell(0);
        cell.setCellValue(schaetzer.nameUndAdresse);

        short iColPos = 1;
        
        for (Schaetzung schaetzung : schaetzer.schaetzungen()){
        		row.createCell(iColPos++).setCellValue(schaetzung.schaetzwert);
        }
      }
      
      // Write the output to a file
      wb.write(fileOut);
      fileOut.close();
      wb.close();
    }
}
*/
