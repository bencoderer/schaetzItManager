package com.bencoderer.schaetzitmanager.data;

import java.util.Date;
import java.util.List;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;

public class Schaetzer extends BaseModel {
    public static String CLASSNAME = "Schaetzer";
  
    // This is a regular field
    @Column(name = "NameUndAdresse")
    public String nameUndAdresse;
  
    @Column(name = "Indate")
    public Date indate;

    @Column(name = "Person", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE, notNull = false)
    public Person person;
  
    // Make sure to have a default constructor for every ActiveAndroid model
    public Schaetzer(){
      super();
    }


    public Schaetzer(String nameUndAdresse){
      super();
      this.nameUndAdresse = nameUndAdresse;
    }
  
    // Used to return items from another table based on the foreign key
    public List<Schaetzung> schaetzungen() {
        return getMany(Schaetzung.class, Schaetzung.Relationships.Schaetzer);

    }
}
