package com.bencoderer.schaetzitmanager.managers;

import java.util.Date;
import java.util.List;

import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.bencoderer.schaetzitmanager.data.Person;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.data.Schaetzung;

public class SchaetzItManager{
  

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
   
  public void clearSchaetzer() {
    new Delete().from(Schaetzer.class).execute();
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
  
  public List<Schaetzer> getAllSchaetzer() {
    return getAllSchaetzer(false);
  }
  
  public List<Schaetzer> getAllSchaetzer(Boolean forExport) {
    From list = new Select().from(Schaetzer.class);
    
    if (!forExport)
    	return list.orderBy("Indate desc").execute();
    
    return list.orderBy("Indate asc").execute();
  }
  
}
