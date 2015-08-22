package com.bencoderer.schaetzitmanager.data;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import android.provider.BaseColumns;

@Table(name = "Schaetzung", id = BaseColumns._ID)
public class Schaetzung extends BaseModel {
  
    public static class Relationships {
      public static String Schaetzer = "Schaetzer";
    }
  
    // This is an association to another activeandroid model
    @Column(name = "Schaetzer", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
    public Schaetzer schaetzer;
  
    @Column(name = "Schaetzwert", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
    public int schaetzwert;
  
    @Column(name = "Sortorder", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
    public int sortorder;

}
