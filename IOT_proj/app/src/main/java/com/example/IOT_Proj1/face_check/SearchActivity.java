package com.example.IOT_Proj1.face_check;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.IOT_Proj1.tools.MyHelper;

import java.util.Calendar;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_search, btn_search_TI, btn_choosetime, btn_confirm;
    TextView search_sum;
    EditText IDS;
    int time_Chosen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        time_Chosen = 0; // 初始化没有选择时间
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//        (1) button: Face Info: ID + Name
        btn_search = (Button) findViewById(R.id.search_NI);
        btn_search.setOnClickListener(this);
//        (2) button: Check Info: ID + Check time
        btn_search_TI = (Button) findViewById(R.id.search_ka);
        btn_search_TI.setOnClickListener(this);
//        (3) EditText: ID : Choose ID to Search:
        IDS = (EditText) findViewById(R.id.ids);
//        (4) Button: Select Time Slot to search:
        btn_choosetime = (Button) findViewById(R.id.time);
        btn_choosetime.setOnClickListener(this);
//        (5) Button: Confirm:
        btn_confirm = (Button) findViewById(R.id.FINALLY);
        btn_confirm.setOnClickListener(this);
//        (6) TextView: show search results
        search_sum = (TextView) findViewById(R.id.tv_sum);
    }

    @Override
    public void onClick(View v) {
//        (1) Check Info button : shows ID and check time
        if (v.getId() == R.id.search_ka)
            MySearch("time_id", false, false);
//        (2) Face Info button : shows ID and name
        else if (v.getId() == R.id.search_NI)
            MySearch("name_id", false,false);
//        (3) Select Time slot Button : shows ID and name
        else if (v.getId() == R.id.time)
            Choose_Time_Slot();
//        (4) Click Confirm button
        else if (v.getId() == R.id.FINALLY)
        {
            if (time_Chosen == 1)
            {   // （1）Only Time:
                if (IDS.getText().toString().trim().equals(""))
                    MySearch("time_id", true, false);
                //（2）Time + ID:
                else MySearch("time_id", true, true);
            }
            else {
                // (3) Choose nothing:
                if (IDS.getText().toString().trim().equals(""))
                    Toast.makeText(this, "(1) ID or Time required!", Toast.LENGTH_LONG).show();
                // (4) Choose Only ID:
                else MySearch("time_id", false, true);
            }
        }
    }

    public void Choose_Time_Slot(){
//        init Calendar:
        final Calendar c = Calendar.getInstance();
        // 创建DatePickerDialog监听事件，实现onDateSet方法
        DatePickerDialog dialog = new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                btn_choosetime.setText(DateFormat.format("yyy-MM-dd", c));
            } // get year, month, Day of month:
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
        time_Chosen = 1; // flag = 1，意味着已经选好了time slot
    }

    //   @parameter table: search from which table;
    //   @parameter ChooseTime: Choose Time = true, otherwise false;
    //   @parameter ChooseID: Choose ID = true, otherwise false;
    public void MySearch(String table, boolean ChooseTime, boolean ChooseID){
        String TIMES = "", id = "", sql = "";
        Cursor cursor;
        if(ChooseID) id = IDS.getText().toString().trim();
        if(ChooseTime) TIMES = btn_choosetime.getText().toString().trim();   //日期在此.
        StringBuffer sum = new StringBuffer();
        SQLiteDatabase db;
        MyHelper mh = new MyHelper(SearchActivity.this);
        db = mh.getWritableDatabase();
        if(ChooseID){
            sql = "select * from time_id where id=\"" + id + "\"";
            cursor = db.rawQuery(sql, null);
        }
        else{
            cursor = db.query(table, null,
                    null, null, null, null, null);
        }
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String fst_col = cursor.getString(0);
                String sec_col = cursor.getString(1);
                if(ChooseTime){ // 截取前面的部分，即年月日比较。如2020-12-31
                    if (TIMES.substring(0, TIMES.length()).equals
                            (sec_col.substring(0, TIMES.length())))
                        sum.append("        " + fst_col + "        " + sec_col + "\n");
                }
                else sum.append("        " + fst_col + "        " + sec_col + "\n");
            }
        }
        cursor.close();
        db.close();
        search_sum.setText(sum.toString());
    }
}