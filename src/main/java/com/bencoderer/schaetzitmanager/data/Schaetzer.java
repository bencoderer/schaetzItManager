package com.bencoderer.schaetzitmanager.data;

import java.util.Date;
import java.util.List;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import android.provider.BaseColumns;

@Table(name = "Schaetzer", id = BaseColumns._ID)
public class Schaetzer extends BaseModel {
    public static String CLASSNAME = "Schaetzer";
  
    // This is a regular field
    public static final String NAMEUNDADRESSE_COLUMN = "NameUndAdresse";
    @Column(name = NAMEUNDADRESSE_COLUMN)
    public String nameUndAdresse;
  
    public static final String INDATE_COLUMN = "Indate";
    @Column(name = INDATE_COLUMN)
    public Date indate;

    @Column(name = "Person", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE, notNull = false)
    public Person person;
  
    public static final String OPERATORKEY_COLUMN = "OperatorKey";
    @Column(name = OPERATORKEY_COLUMN)
    public String operatorKey; //add thk 20.08.2016
  
  
    public static final String OPERATORDBID_COLUMN = "OperatorDbId";
    @Column(name = OPERATORDBID_COLUMN)
    public String operatorDbId; //add thk 03.09.2016
  
    public static final String SENTTOSERVERDATE_COLUMN = "SentToServerDate";
    @Column(name = SENTTOSERVERDATE_COLUMN)
    public Date sentToServerDate; //add thk 20.08.2016
  
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
