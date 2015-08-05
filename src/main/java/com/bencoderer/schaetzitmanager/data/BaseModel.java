package com.bencoderer.schaetzitmanager.data;


import java.util.List;
import com.activeandroid.Model;
import com.activeandroid.query.Select;

public class BaseModel extends Model {
  
  protected <T extends Model> List<T> getManyThrough(Class<T> targetClass, Class<T> joinClass, String targetForeignKeyInJoin, String foreignKeyInJoin){
		return new Select()
		.from(targetClass)
		.as("target_model")
		.join(joinClass)
		.as("join_model")
		.on("join_model." + targetForeignKeyInJoin + " = " + "target_model.id")
		.where(foreignKeyInJoin + " = ?", this.getId())
		.execute();
	}
}
