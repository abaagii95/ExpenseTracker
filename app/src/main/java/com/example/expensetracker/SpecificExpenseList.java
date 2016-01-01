package com.example.expensetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SpecificExpenseList extends AppCompatActivity {

    DBAdapter db = new DBAdapter(this);
    List<String> item;
    List<Integer> item_position;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_expense_list);
        getExpensesFromDB();
        renderList();
    }

    public void getExpensesFromDB() {
        item = new ArrayList<String>();
        item_position = new ArrayList<Integer>();

        db.open();
        Intent intent = getIntent();
        Cursor c = db.getExpenses(intent.getIntExtra("id", 0));
        if(c.getCount() > 0) {
            c.moveToFirst();
            for(int i = 0; i < c.getCount(); i++) {
                item.add(i, c.getString(c.getColumnIndex(DBAdapter.EXPENSES_KEY_DATE))
                        + "__" + c.getString(c.getColumnIndex(DBAdapter.EXPENSES_KEY_NAME))
                        + "__" + c.getInt(c.getColumnIndex(DBAdapter.EXPENSES_KEY_AMOUNT)));
                item_position.add(i, c.getInt(c.getColumnIndex(DBAdapter.EXPENSES_KEY_ROWID)));
                c.moveToNext();
            }
        }
        c.close();
        db.close();
    }

    public void renderList() {
        ListView lstview=(ListView)findViewById(R.id.listview);

        // Inflate header view
        ViewGroup headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.header, lstview,false);
        // Add header view to the ListView
        if(lstview.getHeaderViewsCount() < 1)
            lstview.addHeaderView(headerView);
        // Get the string array defined in strings.xml file
        items=getResources().getStringArray(R.array.list_items);
        items = new String[item.size()];
        items = item.toArray(items);
        // Create an adapter to bind data to the ListView
        LstViewAdapter adapter=new LstViewAdapter(this,R.layout.rowlayout,R.id.txtname,items);
        // Bind data to the ListView
        lstview.setAdapter(adapter);

        lstview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int positionFin = (int) id;
                AlertDialog.Builder alert = new AlertDialog.Builder(SpecificExpenseList.this);
                alert.setMessage("Зардлыг устгах эсвэл өөрчлөх");
                alert.setTitle("Зардал");

                alert.setPositiveButton("Устгах", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        AlertDialog.Builder a = new AlertDialog.Builder(SpecificExpenseList.this);
                        a.setMessage("Та итгэлтэй байна уу?");
                        a.setTitle("Устгах");
                        a.setPositiveButton("Тийм", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.open();
                                if (db.deleteExpense(item_position.get(positionFin)) > 0) {
                                    Toast.makeText(SpecificExpenseList.this, "Амжилттай устлаа", Toast.LENGTH_SHORT).show();
                                }
                                db.close();
                                getExpensesFromDB();
                                renderList();
                            }
                        });

                        a.setNegativeButton("Үгүй", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        a.show();
                    }
                });

                alert.setNegativeButton("Өөрчлөх", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(SpecificExpenseList.this, EditExpense.class);
                        intent.putExtra("id", item_position.get(positionFin));
                        startActivityForResult(intent, 1);
                    }
                });
                alert.show();

                return false;
            }
        });
    }
}
