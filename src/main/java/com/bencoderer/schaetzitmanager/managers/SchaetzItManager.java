package com.bencoderer.schaetzitmanager.managers;

import java.util.Date;
import java.util.List;

import com.bencoderer.schaetzitmanager.data.Person;
import com.bencoderer.schaetzitmanager.data.SchaetzItDbAdapter;
import com.bencoderer.schaetzitmanager.data.Schaetzer;
import com.bencoderer.schaetzitmanager.data.Schaetzung;

public class SchaetzItManager{
  

  public SchaetzItManager() {
    
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
}
