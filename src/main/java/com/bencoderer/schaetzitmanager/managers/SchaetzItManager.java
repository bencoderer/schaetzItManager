package com.bencoderer.schaetzitmanager.managers;

import java.util.Date;
import java.util.List;

import android.util.Log;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.bencoderer.schaetzitmanager.data.Operator;
import com.bencoderer.schaetzitmanager.data.Person;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.data.Schaetzung;


public class SchaetzItManager{

  public static final String TAG = com.bencoderer.schaetzitmanager.activities.HelloAndroidActivity.TAG;
  
  private String operatorKey = null;

  public void setOperatorKey(String operatorKey) {
    this.operatorKey = operatorKey;
  }
 
  public String getOperatorKey() {
    return operatorKey;
  }
  
  
  public SchaetzItManager() {
    
  }
  
  public List<Person> getPersonsMatching(String matchingText) {
    if (matchingText == null || matchingText.length() < 3) {
      return null;
    }
    
    String[] parts = matchingText.split(" ");
    
    String name1 = "";
    String name2 = "";
    String adresse = "";
    
    if (parts.length >= 1) {
      name1 = parts[0];
    }
    
    if (parts.length >= 2) {
      name2 = parts[1];
    }
    
    if (parts.length >= 3) {
      adresse = parts[parts.length-1];
    }
    
    return new Select().from(Person.class)
      .where("Name LIKE ? and Adresse LIKE ?", "%"+name1+"%"+ (name2 == "" ? "" : name2 + "%"), adresse == "" ?"%": "%"+adresse+"%")
      .orderBy("Name")
      .execute();
  }
  
  public void clearPersons() {
      new Delete().from(Person.class).execute();
  }
  
  public void addPerson(String name, String adresse) {
    Person item = new Person();

    item.name = name;
    item.adresse = adresse;
    
    item.save();
  }
  
  /*
   * Schaetzer
  */
  
  private From getSchaetzerOfOperator() {
    From result = new Select().from(Schaetzer.class);
    
    if (this.getOperatorKey() != null) {
        result = result.where("OperatorKey = ?", this.getOperatorKey());
    }
    
    return result;
  }
   
  public void clearSchaetzer() {
    new Delete().from(Schaetzer.class).execute();
  }
  
  public Schaetzer addSchaetzer(String nameUndAdresse, List<Integer> schaetzungen, Person person){
    Schaetzer item = new Schaetzer();

    item.person = person;
    item.nameUndAdresse = nameUndAdresse;
    
    
    item.operatorKey = this.getOperatorKey();
    item.indate = new Date();
    item.save();
    item.operatorDbId = item.getId().toString();
    Log.d(TAG, "OperatorDbId of new Schaetzer will be:" + item.operatorDbId);
    item.save(); //save changed operatorDbId
    
    Schaetzung newS;
    int sortOrder = 1;
    if (schaetzungen != null) {
      for(Integer s : schaetzungen) {
        newS = new Schaetzung();
        newS.schaetzwert = s;
        newS.sortorder = sortOrder++;
        newS.schaetzer = item;
        newS.save();
      }
    }
    
    return item;
  }
  
  public void addOrUpdateSchaetzerByOperator(String operatorKey, String operatorDbId, Date indate, String nameUndAdresse, List<Schaetzung> schaetzungen, Person person){
    Schaetzer existing = getSchaetzerByOperator(operatorKey, operatorDbId);
    
    if (existing != null) {
      deleteSchaetzungenOfSchaetzer(existing);
    }
    else {
      if (operatorKey.equals(this.getOperatorKey())) {
        Log.w(TAG, "Neuer Schaetzer wird mit operatorKey " + operatorKey + " erzeugt, dies ist aber der aktuelle Operator! Nur logisch, wenn Wiederherstellung mit Serverdaten läuft!");
      }
      
      Log.i(TAG, "Neuer Schaetzer vom Operator " + operatorKey + "/" + operatorDbId + "  name:" + nameUndAdresse);
      existing = addSchaetzer(nameUndAdresse, null, null);
      existing.operatorKey = operatorKey;
      existing.operatorDbId = operatorDbId;
      if (indate != null) {
      	existing.indate = indate;
      }
      existing.save(); //store operatorKey changes
    }
    
    
    for(Schaetzung s : schaetzungen) {
      s.schaetzer = existing;
      s.save();
    }
    
  }
  
  
  /**
 * @param schaetzer
 */
public void deleteSchaetzungenOfSchaetzer(Schaetzer schaetzer) {
    for(Schaetzung s : schaetzer.schaetzungen()) {
      s.delete();
    }
}
  
  public Schaetzung createSchaetzung() {
    return new Schaetzung();
  }

public void updateSchaetzer(Schaetzer item){
    item.save();
  }
  
  
  public void resetSyncToServerState() {
    for(Schaetzer s : getAllSchaetzer()) {
      s.sentToServerDate = null;
      s.save();
    }
  }
  
  
  public List<Schaetzer> getAllSchaetzer() {
    return getAllSchaetzer(false);
  }
  
  public List<Schaetzer> getAllSchaetzer(Boolean forExport) {
    From list = getSchaetzerOfOperator();
    
    if (!forExport)
    	return list.orderBy("Indate desc").execute();
    
    list = new Select().from(Schaetzer.class);
    return list.orderBy("Indate asc").execute();
  }
  
  
  public List<Schaetzer> getSchaetzerToSyncToServer() {
    return getSchaetzerOfOperator()
          .where("SentToServerDate is NULL")
          .orderBy("Indate asc")
          .execute();
  }
  
  public Schaetzer getSchaetzerByOperator(String operatorKey, String operatorDbId) {
    return (new Select().from(Schaetzer.class))
          .where("operatorKey = ? and operatorDbId = ?", operatorKey, operatorDbId)
          //.where("", operatorDbId)
          .executeSingle();
  }
  
  
  /*
   * Operator
  */
  
  public void clearOperator() {
    new Delete().from(Operator.class).execute();
  }
  
  
  public void addOperator(String operatorKey, String name){
    Operator item = new Operator();

    item.key = operatorKey;
    item.name = name;
    
    item.indate = new Date();
    item.save();
  }
  
  
  public void updateOperator(Operator item){
    item.save();
  }
  
  public Operator getCurrentOperator() {
    return new Select().from(Operator.class)
      .where("Used = 1")
      .executeSingle();
  }
  
  public List<Operator> getAllOperator() {
    From list = new Select().from(Operator.class);
    
    return list.orderBy("Name asc").execute();
  }
  
  
}
