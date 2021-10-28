package com.example.yonginlife;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.yonginlife.db.DBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TimeTableActivity extends Activity {

    Button btnAdd;
    String day, start, end, className;
    JSONObject json_data;
    ListView class_list;
    DBHelper user_db;
    ArrayList<String> items;
    HashMap<String, Integer> map;
    Integer sel_item;
    ArrayAdapter<String> mArrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        user_db = new DBHelper(this, "user_db", null, 1);
        btnAdd = (Button)findViewById(R.id.add_class);
        class_list = (ListView)findViewById(R.id.class_list);
        JSONArray jArray = user_db.getResult("timetable");
        registerForContextMenu(class_list);
        items = new ArrayList<String>();
        map = new HashMap<String, Integer>();
        try {
            for(int i=0; i < jArray.length() ; i++) {
                json_data = jArray.getJSONObject(i);
                Integer id = json_data.getInt("_id");
                String day = json_data.getString("day_of_the_week");
                String start = json_data.getString("start_time");
                String end = json_data.getString("end_time");
                String class_name = json_data.getString("class_name");
                String data = day+ "요일  " + start + "~" + end + "  " + class_name;
                map.put(data, id);
                //map.get(data).toString()
                items.add(data);
            }

            mArrayAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_expandable_list_item_1, items);
            class_list.setAdapter(mArrayAdapter);

        }catch (Exception e){
            System.out.println(e);
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show1();
            }
        });
        // 아이템을 [클릭]시의 이벤트 리스너를 등록
        class_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                sel_item = map.get(item);
                Toast.makeText(TimeTableActivity.this, item, Toast.LENGTH_LONG).show();
            }
        });
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_edit:
                Toast.makeText(this, sel_item.toString(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_delete:
                try {
                    user_db.delete("timetable", sel_item);
                    Toast.makeText(this, "강의가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    System.out.println(e);
                }
                //textView.setTextColor(Color.BLUE);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    void show1()
    {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("월");
        ListItems.add("화");
        ListItems.add("수");
        ListItems.add("목");
        ListItems.add("금");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        final List SelectedItems  = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("수강 요일을 선택하세요.");
        builder.setSingleChoiceItems(items, defaultItem,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SelectedItems.clear();
                    SelectedItems.add(which);
                }
            });
        builder.setPositiveButton("다음",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String msg="";

                    if (!SelectedItems.isEmpty()) {
                        int index = (int) SelectedItems.get(0);
                        msg = ListItems.get(index);
                    }
                    day = msg;
                    Toast.makeText(getApplicationContext(),
                            "Items Selected.\n"+ msg , Toast.LENGTH_LONG)
                            .show();
                    show2();
                }
            });
        builder.setNegativeButton("취소",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        builder.show();
    }
    void show2(){

        final TimePicker TP = new TimePicker(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("강의 시작시간을 입력해주세요.");
        builder.setView(TP);
        builder.setPositiveButton("입력",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String temp1, temp2;
                    if(TP.getCurrentHour()<10){
                        temp1 = "0" + TP.getCurrentHour().toString();
                    }else{
                        temp1 = TP.getCurrentHour().toString();
                    }

                    if(TP.getCurrentMinute()<10){
                        temp2 = "0" + TP.getCurrentMinute().toString();
                    }else{
                        temp2 = TP.getCurrentMinute().toString();
                    }

                    start = temp1 + ":" + temp2;
                    Toast.makeText(getApplicationContext(),TP.getCurrentHour().toString() + TP.getCurrentMinute().toString() ,Toast.LENGTH_LONG).show();
                    show3();
                }
            });
        builder.setNegativeButton("취소",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        builder.show();
    }
    void show3(){
        final TimePicker TP = new TimePicker(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("강의 종료시간을 입력해주세요.");
        builder.setView(TP);
        builder.setPositiveButton("입력",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String temp1, temp2;
                    if(TP.getCurrentHour()<10){
                        temp1 = "0" + TP.getCurrentHour().toString();
                    }else{
                        temp1 = TP.getCurrentHour().toString();
                    }

                    if(TP.getCurrentMinute()<10){
                        temp2 = "0" + TP.getCurrentMinute().toString();
                    }else{
                        temp2 = TP.getCurrentMinute().toString();
                    }

                    end = temp1 + ":" + temp2;
                    Toast.makeText(getApplicationContext(),TP.getCurrentHour().toString() + TP.getCurrentMinute().toString() ,Toast.LENGTH_LONG).show();
                    show4();
                }
            });
        builder.setNegativeButton("취소",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        builder.show();
    }
    void show4(){
        final EditText edittext = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("강의명을 입력해주세요.");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    className = edittext.getText().toString();
                    try {
                        user_db.insert("timetable", day, start, end, className);
                    }catch (Exception e){
                        System.out.println(e);
                    }
                    Toast.makeText(getApplicationContext(),edittext.getText().toString() ,Toast.LENGTH_LONG).show();
                }
            });
        builder.setNegativeButton("취소",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        builder.show();
    }

}
