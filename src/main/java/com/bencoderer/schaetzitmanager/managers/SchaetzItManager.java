package com.bencoderer.schaetzitmanager.managers;

import java.util.Date;
import java.util.List;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.bencoderer.schaetzitmanager.data.Person;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.data.Schaetzung;

public class SchaetzItManager{
  

  public SchaetzItManager() {
    
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
    
  
  public void addSchaetzer(String nameUndAdresse, List<Integer> schaetzungen, Person person){
    Schaetzer item = new Schaetzer();

    item.person = person;
    item.nameUndAdresse = nameUndAdresse;
    
    item.indate = new Date();
    item.save();
    
    Schaetzung newS;
    int sortOrder = 1;
    for(Integer s : schaetzungen) {
      newS = new Schaetzung();
      newS.schaetzwert = s;
      newS.sortorder = sortOrder++;
      newS.schaetzer = item;
      newS.save();
    }
  }
  
  public List<Schaetzer> GetAllSchaetzer() {
    return new Select().from(Schaetzer.class).orderBy("Indate desc").execute();
  }
}
