package com.example.yonginlife;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yonginlife.db.DBHelper;
import com.example.yonginlife.decorators.DeleteDecorator;
import com.example.yonginlife.decorators.EventDecorator;
import com.example.yonginlife.decorators.OneDayDecorator;
import com.example.yonginlife.decorators.SaturdayDecorator;
import com.example.yonginlife.decorators.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleActivity extends Activity {
    SimpleDateFormat formatDate = new SimpleDateFormat ( "yyyyMMdd");
    MaterialCalendarView materialCalendarView;
    TextView selDate, memo_out;
    CalendarDay selDay;
    Button btnSave, btnDelete;
    EditText memo;
    DBHelper user_db;
    JSONArray jsonArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        selDate = (TextView)findViewById(R.id.sel_date);
        btnSave = (Button)findViewById(R.id.btn_save);
        btnDelete = (Button)findViewById(R.id.btn_delete);
        materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);
        memo = (EditText)findViewById(R.id.memo);
        memo_out = (TextView)findViewById(R.id.memo_out);

        // DB 사용
        user_db = new DBHelper(this, "user_db", null, 1);

        jsonArray = user_db.getResult("schedule");
        if (jsonArray != null) {
            for (int i=0;i<jsonArray.length();i++){
                try {
                    String strDay = jsonArray.getJSONObject(i).getString("_date");
                    Date date = formatDate.parse(strDay);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    materialCalendarView.addDecorator(new EventDecorator(new CalendarDay(cal),ScheduleActivity.this));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        //test.setText(jArr.getString());
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Calendar cal = date.getCalendar();
                selDay = date;
                selDate.setText(formatDate.format(cal.getTime()));
                memo_out.setText("등록된 일정이 없습니다.");
                btnDelete.setVisibility(View.INVISIBLE);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            //System.out.println(jsonArray.getJSONObject(i).getString("_date"));
                            //System.out.println(formatDate.format(cal.getTime()));
                            if(jsonArray.getJSONObject(i).getString("_date").equals(formatDate.format(cal.getTime()))){
                                memo_out.setText(jsonArray.getJSONObject(i).getString("memo"));
                                btnDelete.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // db정보를 새로 불러옴
                jsonArray = user_db.getResult("schedule");
            }
        });
        materialCalendarView.addDecorators(
            new SundayDecorator(),
            new SaturdayDecorator(),
            new OneDayDecorator()
        );
/*
        try {
            // 파일에서 읽은 데이터를 저장하기 위해서 만든 변수
            StringBuffer data = new StringBuffer();
            FileInputStream fis = openFileInput("schedule.txt");//파일명
            BufferedReader buffer = new BufferedReader
                    (new InputStreamReader(fis));
            String str = buffer.readLine(); // 파일에서 한줄을 읽어옴
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            test.setText(data);
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(selDay != null){
                materialCalendarView.addDecorator(new EventDecorator(selDay,ScheduleActivity.this));
            }
            String data = selDate.getText() + memo.getText().toString();
            user_db.insert("schedule", selDate.getText().toString(), memo.getText().toString());
            memo_out.setText(memo.getText().toString());
            memo.setText("");
            Toast.makeText(ScheduleActivity.this, "일정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 일정 삭제
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = selDay.getCalendar();
                //selDate.setText(formatDate.format(cal.getTime()));
                try {
                    user_db.delete("schedule", formatDate.format(cal.getTime()));
                    memo_out.setText("등록된 일정이 없습니다.");
                    materialCalendarView.addDecorators(
                            new DeleteDecorator(selDay,ScheduleActivity.this),
                            new SundayDecorator(),
                            new SaturdayDecorator(),
                            new OneDayDecorator()
                    );
                    Toast.makeText(ScheduleActivity.this, "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        /*btnSave.setOnClickListener(new View.OnClickListener() {
            @Override    // 입력한 데이터를 파일에 추가로 저장하기
            public void onClick(View v) {
                String data = selDate.getText() + memo.getText().toString();

                try {
                    FileOutputStream fos = openFileOutput
                            ("schedule.txt", // 파일명 지정
                                    Context.MODE_APPEND);// 저장모드
                    PrintWriter out = new PrintWriter(fos);
                    out.println(data);
                    out.close();
                    Toast.makeText(ScheduleActivity.this, "파일 저장 완료", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/
    }
}
