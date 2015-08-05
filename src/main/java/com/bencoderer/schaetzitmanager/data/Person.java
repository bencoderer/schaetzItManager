package com.bencoderer.schaetzitmanager.data;

import com.activeandroid.annotation.Column;

public class Person extends BaseModel {
  
    // This is a regular field
    @Column(name = "Name")
    public String name;
  
    @Column(name = "Adresse")
    public String adresse;
 
     // Make sure to have a default constructor for every ActiveAndroid model
    public Person(){
      super();
    }

}
