package com.example.yonginlife.db;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.time.LocalDateTime;

public class DBHelper extends SQLiteOpenHelper {

    Context context;

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + "schedule" +
                    "(_date VARCHAR(10) PRIMARY KEY, memo VARCHAR(100));");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + "timetable" +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, day_of_the_week VARCHAR(10), start_time VARCHAR(10), end_time VARCHAR(10), class_name VARCHAR(20));");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + "bus" +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT,bus_type VARCHAR(10), _time VARCHAR(10));");
            System.out.println("table 생성");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //새로운 데이터 입력 - schedule, bus table
    public void insert(String tb_name, String str1, String str2) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            if(tb_name == "schedule"){
                values.put("_date", str1);
                values.put("memo", str2);
            }else if(tb_name == "bus"){
                values.put("bus_type", str1);
                values.put("_time", str2);
            }
            db.insert(tb_name, null, values);
            System.out.println("저장완료");
        }catch (Exception e){
            System.out.println(e);
        }
        // DB에 입력한 값으로 행 추가
        db.close();
    }
    //새로운 데이터 입력 - timetable table
    public void insert(String tb_name, String day, String start, String end, String className) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        ContentValues values = new ContentValues();
        values.put("day_of_the_week", day);
        values.put("start_time", start);
        values.put("end_time", end);
        values.put("class_name", className);
        db.insert(tb_name, null, values);
        db.close();
    }

    public void update(String tb_name, String str1, String str2) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("_date", str1);
            values.put("memo", str2);
            db.update(tb_name, values, "_date=?", new String[]{String.valueOf(str1)});
            System.out.println("저장완료");
        }catch (Exception e){
            System.out.println(e);
        }
        // DB에 입력한 값으로 행 추가
        db.close();
    }
    //새로운 데이터 입력 - timetable table
    public void update(String tb_name, int id, String day, String start, String end, String className) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put("day_of_the_week", day);
        values.put("start_time", start);
        values.put("end_time", end);
        values.put("class_name", className);
        db.update(tb_name, values, "_id=?", new String[]{String.valueOf(id)});
        db.close();
    }
    // 테이블의 모든 데이터를 json array로 얻어온다
    public JSONArray getResult(String tb_name) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM " + tb_name, null);
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }

    public JSONArray getDayClass(String tb_name, String day) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String query = "SELECT * FROM " + tb_name + " WHERE day_of_the_week = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(day)});
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }

    //특정 id를 갖는 데이터를 json으로 출력
    public JSONObject getData(String tb_name, int id) {
        Log.i("ID", String.valueOf(id));
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tb_name + " WHERE _id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        cursor.moveToPosition(0);

        int totalColumn = cursor.getColumnCount();
        JSONObject rowObject = new JSONObject();

        for (int i = 0; i < totalColumn; i++) {
            if (cursor.getColumnName(i) != null) {
                try {
                    if (cursor.getString(i) != null) {
                        rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                    } else {
                        rowObject.put(cursor.getColumnName(i), "");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        cursor.close();
        db.close();
        return rowObject;
    }
    public JSONObject getData(String tb_name, String _date) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + tb_name + " WHERE _date = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(_date)});
        cursor.moveToPosition(0);

        int totalColumn = cursor.getColumnCount();
        JSONObject rowObject = new JSONObject();

        for (int i = 0; i < totalColumn; i++) {
            if (cursor.getColumnName(i) != null) {
                try {
                    if (cursor.getString(i) != null) {
                        rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                    } else {
                        rowObject.put(cursor.getColumnName(i), "");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        cursor.close();
        db.close();
        return rowObject;
    }

    //Primary key인 id를 이용해서 검색 후 삭제
    public void delete(String tb_name, int id) throws JSONException {

        // 입력한 항목과 일치하는 행 삭제
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tb_name + " WHERE _id= " + id + ";");
        db.close();
    }
    public void delete(String tb_name, String _date) throws JSONException {

        // 입력한 항목과 일치하는 행 삭제
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tb_name + " WHERE _date= " + _date + ";");
        db.close();
    }

    public void insertBus(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            String query = "SELECT * FROM " + "bus" + " WHERE _time = ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf("8:15")});
            cursor.moveToPosition(0);

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
            cursor.close();
            System.out.println(rowObject);
            if(rowObject.isNull("_time")){
                // 처음 실행했을 때
                System.out.println("bus DB insert");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"8:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"8:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"8:35\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"8:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"8:45\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"8:50\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"8:55\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"9:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"9:10\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"9:20\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"9:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"9:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"9:50\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"10:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"10:20\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"10:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"11:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"11:20\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"11:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"12:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"12:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"12:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"12:45\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"13:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"13:20\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"13:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"14:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"14:20\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"14:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"15:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"15:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"15:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"15:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"15:50\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"16:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"16:10\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"16:20\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"16:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"16:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"16:50\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"17:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"17:10\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"17:20\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"17:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"17:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"17:50\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"18:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"18:20\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"18:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"19:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"19:20\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"19:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"19:50\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"20:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"20:20\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"20:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"21:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"21:20\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"21:40\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"1\", \"22:00\");");


                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"8:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"8:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"8:45\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"9:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"9:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"9:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"9:45\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"10:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"10:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"10:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"10:45\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"11:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"11:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"11:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"11:45\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"12:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"12:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"12:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"12:45\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"13:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"13:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"13:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"13:45\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"14:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"14:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"14:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"14:45\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"15:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"15:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"15:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"15:45\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"16:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"16:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"16:30\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"16:45\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"17:00\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"17:15\");");
                db.execSQL("insert into bus(bus_type, _time) values(\"2\", \"17:30\");");

            }else{
                //db.execSQL("DROP TABLE bus;");
            }

            db.close();
            System.out.println("table 생성");

        }catch (Exception e){
            System.out.println(e);
        }
    }
}