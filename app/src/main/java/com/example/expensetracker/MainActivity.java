package com.example.expensetracker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DBAdapter db = new DBAdapter(this);

    private LinearLayout mainLayout;
    private PieChart mChart;

    private float[] yData = {5, 15, 10, 30, 30, 10};
    private String[] xData = {"Sony", "Panasonic", "Dell", "Sharp", "Nokia", "Samsung"};
    private int[] ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mChart = new PieChart(this);

        //add piechart to mainlayout
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        mainLayout.addView(mChart, width, height/2);
        mainLayout.setBackgroundColor(Color.LTGRAY);

        //Configure PieChart
        mChart.setUsePercentValues(true);
        mChart.setDescription("Зардал");

        //Enable hole and configure
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(10);

        //Enable rotation of the chart by touch
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        //Set chart value selected listener
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                if(entry == null)
                    return;

                Toast.makeText(MainActivity.this, xData[entry.getXIndex()] + " = " +
                        entry.getVal() + "%, " + ids[entry.getXIndex()], Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, SpecificExpenseList.class);
                intent.putExtra("id", ids[entry.getXIndex()]);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        //Add data
        getDataFromDB();
        addData();

        //Customize legends
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        mChart.setDescription("");    // Hide the description
        l.setEnabled(false);

        /*
        *
        * Add Button Listeners
        *
        * */

        ((Button) findViewById(R.id.editCategoriesButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditCategories.class);
                startActivity(intent);
            }
        });

        ((Button) findViewById(R.id.addExpenseButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddExpense.class);
                startActivity(intent);
            }
        });

        ((Button) findViewById(R.id.expenseListButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExpenseList.class);
                startActivity(intent);
            }
        });


    }

    private void addData() {
        ArrayList <Entry> yVals1 = new ArrayList<Entry>();

        for(int i = 0; i < yData.length; i++) {
            yVals1.add(new Entry(yData[i], i));
        }

        ArrayList <String> xVals = new ArrayList<String>();

        for(int i = 0; i < xData.length; i++) {
            xVals.add(xData[i]);
        }

        //Create Pie Data Set
        PieDataSet dataSet = new PieDataSet(yVals1, "Зардлууд");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        //Add many colors
        ArrayList <Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        //instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);

        //undo all highlights
        mChart.highlightValues(null);

        //update PieChart
        mChart.invalidate();

    }

    public void getDataFromDB() {
        List<String> names = new ArrayList<String>();
        List<Integer> costs = new ArrayList<Integer>();
        List<Integer> ids_temp = new ArrayList<Integer>();
        int sumOfAllExps = 0;

        db.open();
        Cursor topExpCur = db.getTopCatExpenses();
        Cursor sumExp = db.getSumExpenses();

        if(sumExp.getCount() > 0) {
            sumExp.moveToFirst();
            sumOfAllExps = sumExp.getInt(0);
            Log.i("Taaag", sumExp.getInt(0) + "");
        }

        topExpCur.moveToFirst();
        for(int i =0; i < topExpCur.getCount(); i++) {
            String catName = topExpCur.getString(0);
            int sum = topExpCur.getInt(1);
            int id = topExpCur.getInt(2);
            names.add(i, catName);
            costs.add(i, sum);
            ids_temp.add(i, id);
            Log.i("Taaag", catName + ": " + sum);
            topExpCur.moveToNext();
        }
        sumExp.close();
        topExpCur.close();
        db.close();

        yData = new float[costs.size() + 1];
        xData = new String[names.size() + 1];
        ids = new int[ids_temp.size()+1];
        int residual = sumOfAllExps;

        for (int i = 0; i < costs.size(); i++) {
            yData[i] = (float) costs.get(i)/sumOfAllExps;
            xData[i] = names.get(i);
            ids[i] = ids_temp.get(i);
            residual = residual - costs.get(i);
        }

        if(costs.size() > 0) {
            yData[costs.size()] = (float) residual/sumOfAllExps;
            xData[names.size()] = "Бусад";
        }
    }

    @Override
    protected void onResume() {
        //Add data
        getDataFromDB();
        addData();

        //Customize legends
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        mChart.setDescription("");    // Hide the description
        l.setEnabled(false);
        
        super.onResume();
    }
}
