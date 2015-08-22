package com.bencoderer.schaetzitmanager.adapters;

import java.util.List;

import com.bencoderer.schaetzitmanager.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.bencoderer.schaetzitmanager.data.Person;
import android.app.Activity;

public class MatchingPersonAdapter extends ArrayAdapter<Person>{

    Context context;
    int layoutResourceId;   
    List<Person> data = null;
   
    public MatchingPersonAdapter(Context context, int layoutResourceId, List<Person> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PersonHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new PersonHolder();
            holder.name = (TextView)row.findViewById(R.id.matchingperson_name);
            holder.adresse = (TextView)row.findViewById(R.id.matchingperson_adresse);
           
            row.setTag(holder);
        }
        else
        {
            holder = (PersonHolder)row.getTag();
        }
       
        Person person = data.get(position);
        holder.name.setText(person.name);
        holder.adresse.setText(person.adresse);
       
        return row;
    }
   
    static class PersonHolder
    {
        TextView name;
        TextView adresse;
    }
}