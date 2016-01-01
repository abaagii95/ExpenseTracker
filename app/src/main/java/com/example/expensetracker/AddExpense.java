package com.example.expensetracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddExpense extends AppCompatActivity {

    EditText expenseName, expenseDescription, expenseAmount;
    DatePicker expenseDate;
    Spinner expenseCategories;
    DBAdapter db = new DBAdapter(this);

    List<String> cats; //Categor iin name uudiig aguulna
    List<Integer> catids; // categor iin id uudig aguulna

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        ((Button) findViewById(R.id.cancelAddExpenseToDbButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((Button) findViewById(R.id.AddExpenseToDbButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int catId = catids.get(expenseCategories.getSelectedItemPosition());
                String expName = expenseName.getText().toString();
                String expDesc = expenseDescription.getText().toString();
                String expDate = getDateFromDatePicker();
                int expAmount;
                if(expenseAmount.getText().toString().equals(""))
                    expAmount = 0;
                else
                    expAmount = Integer.parseInt(expenseAmount.getText().toString());

                ContentValues cv = new ContentValues();
                cv.put(DBAdapter.EXPENSES_KEY_NAME, expName);
                cv.put(DBAdapter.EXPENSES_KEY_AMOUNT, expAmount);
                cv.put(DBAdapter.EXPENSES_KEY_DESCRIPTION, expDesc);
                cv.put(DBAdapter.EXPENSES_KEY_DATE, expDate);
                cv.put(DBAdapter.EXPENSES_KEY_TAGID, catId);

                db.open();
                if(db.insertExpense(cv)  > 0) {
                    Toast.makeText(AddExpense.this, "Амжилттай нэмэгдлээ", Toast.LENGTH_LONG).show();
                    db.close();
                    finish();
                } else {
                    db.close();
                    Toast.makeText(AddExpense.this, "Амжилтгүй боллоо", Toast.LENGTH_LONG).show();
                }
            }
        });


        expenseName = (EditText) findViewById(R.id.inputExpenseName);
        expenseDescription = (EditText) findViewById(R.id.inputExpenseDescription);
        expenseAmount = (EditText) findViewById(R.id.inputExpenseAmount);
        expenseDate = (DatePicker) findViewById(R.id.datePicker);
        expenseCategories = (Spinner) findViewById(R.id.inputCategoryName);

        //get categories from db
        getCategoriesFromDB();
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, cats);
        expenseCategories.setAdapter(adapter);

    }

    //Sangaas categoruudiig avah
    public void getCategoriesFromDB() {
        cats = new ArrayList<String>();
        catids = new ArrayList<Integer>();

        db.open();
        Cursor cursor = db.getCategories();
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++) {
                catids.add(i, cursor.getInt(cursor.getColumnIndex(DBAdapter.CATEGORIES_KEY_ROWID)));
                cats.add(i, cursor.getString(cursor.getColumnIndex(DBAdapter.CATEGORIES_KEY_NAME)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
    }

    //Date-iig DatePicker ees avah
    public String getDateFromDatePicker(){
        int day = expenseDate.getDayOfMonth();
        int month = expenseDate.getMonth();
        int year =  expenseDate.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        //return calendar.getTime().toString();
        return year + "-" + month + "-" + day;
    }
}
