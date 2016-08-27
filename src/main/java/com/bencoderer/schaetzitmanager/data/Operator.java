package com.bencoderer.schaetzitmanager.data;

import java.util.Date;
import java.util.List;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import android.provider.BaseColumns;

@Table(name = "Operator", id = BaseColumns._ID)
public class Operator extends BaseModel { //add thk 20.08.2016
    public static String CLASSNAME = "Operator";
  
    /*
     * setting a custom primary key: https://github.com/codepath/android_guides/wiki/ActiveAndroid-Guide
    */
  
    //Global Key for the operator, a Schaetzer referencing an operator using this key
    @Column(name = "OperatorKey", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String key;
  
    // This is a regular field
    public static final String NAME_COLUMN = "Name";
    @Column(name = NAME_COLUMN)
    public String name;
  
    public static final String INDATE_COLUMN = "Indate";
    @Column(name = INDATE_COLUMN)
    public Date indate;
  
    public static final String USED_COLUMN = "Used";
    @Column(name = USED_COLUMN)
    public Boolean used;
  
    // Make sure to have a default constructor for every ActiveAndroid model
    public Operator(){
      super();
      this.used = false;
    }


    public Operator(String name, String key){
      super();
      this.name = name;
      this.key = key;
      this.used = false;
    }
  
    public String toDisplayText() {
      return this.name+  " ["+ this.key +  "]";
    }
}
