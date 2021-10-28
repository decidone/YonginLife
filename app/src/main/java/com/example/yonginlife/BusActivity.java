package com.example.yonginlife;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.example.yonginlife.db.DBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BusActivity extends Activity {

    DBHelper user_db;
    JSONObject json_data;
    ListView bus_1, bus_2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        bus_1 = (ListView)findViewById(R.id.bus_1);
        bus_2 = (ListView)findViewById(R.id.bus_2);
        user_db = new DBHelper(this, "user_db", null, 1);
        user_db.insertBus();
        JSONArray jArray = user_db.getResult("bus");
        System.out.println("test");
        System.out.println(jArray);

        ArrayList<String> items_1 = new ArrayList<String>();
        ArrayList<String> items_2 = new ArrayList<String>();
        try {
            for(int i=0; i < jArray.length() ; i++) {
                json_data = jArray.getJSONObject(i);
                int id=json_data.getInt("_id");
                String time=json_data.getString("_time");
                if(json_data.getString("bus_type").equals("1")){
                    items_1.add(time);
                }else if(json_data.getString("bus_type").equals("2")){
                    items_2.add(time);
                }
            }

            ArrayAdapter<String> mArrayAdapter_1 = new ArrayAdapter<String>(
                    this, android.R.layout.simple_expandable_list_item_1, items_1);
            bus_1.setAdapter(mArrayAdapter_1);

            ArrayAdapter<String> mArrayAdapter_2 = new ArrayAdapter<String>(
                    this, android.R.layout.simple_expandable_list_item_1, items_2);
            bus_2.setAdapter(mArrayAdapter_2);
            //bus_1.setListAdapter(mArrayAdapter);
        }catch (Exception e){
            System.out.println(e);
        }
    }
}