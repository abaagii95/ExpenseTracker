package com.example.expensetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditCategories extends AppCompatActivity {

    List<String> cats; //Categor iin name uudiig aguulna
    List<Integer> catids; // categor iin id uudig aguulna
    DBAdapter db = new DBAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_categories);

        ((Button) findViewById(R.id.addCategoryButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EditCategories.this);
                final EditText edittext = new EditText(EditCategories.this);
                alert.setMessage("Нэмэх категорийнхоо нэрийг оруулна уу");
                alert.setTitle("Категори нэмэх");

                alert.setView(edittext);

                alert.setPositiveButton("Нэмэх", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String editTextValue = edittext.getText().toString();
                        addCategory(editTextValue);
                    }
                });

                alert.setNegativeButton("Болих", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }
        });
        getCategoriesFromDB();
        renderListView();
    }

    public void addCategory(String category) {
        db.open();
        long id = db.insertCategory(category);
        if(id > 0)
            Toast.makeText(EditCategories.this, "Категори амжилттай нэмэгдлээ", Toast.LENGTH_LONG).show();
        db.close();
        getCategoriesFromDB();
        renderListView();
    }

    //Listview iig uusgeh, zurah
    public void renderListView() {
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.category_item, cats);

        ListView listView = (ListView) findViewById(R.id.category_list);
        listView.setAdapter(adapter);
        listView.invalidate();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int positionFin = position;
                AlertDialog.Builder alert = new AlertDialog.Builder(EditCategories.this);
                final EditText edittext = new EditText(EditCategories.this);
                alert.setMessage("Категорын нэрийг өөрчилж <өөрчлөх> гэсэн товчийг дарна уу");
                alert.setTitle("Категорийг өөрчлөх");
                edittext.setText(cats.get(positionFin));

                alert.setView(edittext);

                alert.setPositiveButton("Өөрчлөх", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String editTextValue = edittext.getText().toString();
                        updateCategory(catids.get(positionFin), editTextValue);
                        renderListView();
                    }
                });

                alert.setNegativeButton("Болих", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EditCategories.this, SpecificExpenseList.class);
                intent.putExtra("id", catids.get(position));
                startActivity(intent);
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

    public void updateCategory(int catId, String category) {
        db.open();
        long id = db.updateCategory(catId, category);
        if(id > 0)
            Toast.makeText(EditCategories.this, "Амжилттай өөрчлөгдлөө", Toast.LENGTH_LONG).show();
        db.close();
    }
}
