package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditExpense extends AppCompatActivity {
    EditText expenseName, expenseDescription, expenseAmount;
    DatePicker expenseDate;
    Spinner expenseCategories;
    DBAdapter db = new DBAdapter(this);

    List<String> cats; //Categor iin name uudiig aguulna
    List<Integer> catids; // categor iin id uudig aguulna

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        expenseName = (EditText) findViewById(R.id.inputExpenseName);
        expenseDescription = (EditText) findViewById(R.id.inputExpenseDescription);
        expenseAmount = (EditText) findViewById(R.id.inputExpenseAmount);
        expenseDate = (DatePicker) findViewById(R.id.datePicker);
        expenseCategories = (Spinner) findViewById(R.id.inputCategoryName);

        //get categories from db
        getCategoriesFromDB();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, cats);
        expenseCategories.setAdapter(adapter);
        getValuesFromDB();

        ((Button) findViewById(R.id.cancelChangeToDbButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((Button) findViewById(R.id.AddChangeToDbButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateExpense();
                Intent returnIntent = new Intent();
                setResult(EditExpense.RESULT_OK, returnIntent);
                finish();
            }
        });
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

    //Sangass busad utguudiig avch nemeh
    public void getValuesFromDB() {
        db.open();
        Intent intent = getIntent();
        Cursor c = db.getExpense(intent.getIntExtra("id", 0));
        c.moveToFirst();

        expenseName.setText(c.getString(c.getColumnIndex(DBAdapter.EXPENSES_KEY_NAME)));
        expenseDescription.setText(c.getString(c.getColumnIndex(DBAdapter.EXPENSES_KEY_DESCRIPTION)));
        String[] s = c.getString(c.getColumnIndex(DBAdapter.EXPENSES_KEY_DATE)).split("-");
        int year = Integer.parseInt(s[0]);
        int month = Integer.parseInt(s[1]);
        int day = Integer.parseInt(s[2]);
        expenseDate.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        });

        int id = c.getInt(c.getColumnIndex(DBAdapter.EXPENSES_KEY_TAGID));
        for(int i = 0; i < catids.size(); i++) {
            if(catids.get(i) == id) {
                expenseCategories.setSelection(i);
            }
        }
        expenseAmount.setText(c.getInt(c.getColumnIndex(DBAdapter.EXPENSES_KEY_AMOUNT)) + "");
        db.close();
    }

    public void updateExpense() {
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

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        db.open();
        if(db.updateExpense(id, cv)  > 0) {
            Toast.makeText(EditExpense.this, "Амжилттай өөрчлөгдлөө", Toast.LENGTH_LONG).show();
            db.close();
            Intent returnIntent = new Intent();
            setResult(EditExpense.RESULT_OK, returnIntent);
            finish();
        } else {
            db.close();
            Toast.makeText(EditExpense.this, "Амжилтгүй боллоо", Toast.LENGTH_LONG).show();
        }
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
