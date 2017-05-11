package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.util.DateAxisValueFormatter;
import com.udacity.stockhawk.util.PriceAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by i57198 on 3/26/17.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String DATASET_LABEL = "Stock Price";

    String mSelectedSymbol;
    @BindView(R.id.tv_detail_error) TextView tvError;
    @BindView(R.id.chart) LineChart histChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSelectedSymbol = getIntent().getStringExtra(MainActivity.INTENT_EXTRA_SYMBOL);
        setTitle(mSelectedSymbol);
        showHistory();
    }

    private void showHistory() {
        Cursor cursor = getContentResolver().query(Contract.Quote.URI, // uri
                new String[] { Contract.Quote.COLUMN_HISTORY }, // projection
                Contract.Quote.COLUMN_SYMBOL + " = ?", // selection
                new String[] { mSelectedSymbol }, // selectionArgs
                null); // sort order

        if (null == cursor || cursor.getCount() < 1) {
            tvError.setText(R.string.error_no_stock_history);
        }
        else {
            if (cursor.moveToFirst()) {
                String strHistory = cursor.getString(0);
                String[] pairs = strHistory.split("\n");
                String[] dates = new String[pairs.length];
                List<Entry> entries = new ArrayList<Entry>();

                int entryCount = 0;
                for (int i = pairs.length - 1; i >= 0; i--) {
                    String[] data = pairs[i].split(",");
                    dates[entryCount] = data[0];
                    Float price = Float.parseFloat(data[1]);
                    entries.add(new Entry(entryCount, price));
                    entryCount++;
                }

                LineDataSet dataSet = new LineDataSet(entries, DATASET_LABEL);
                dataSet.setColor(Color.BLUE);

                LineData lineData = new LineData(dataSet);
                histChart.setData(lineData);
                histChart.setBackgroundColor(Color.WHITE);
                histChart.getLegend().setEnabled(false);
                histChart.getDescription().setEnabled(false);

                XAxis xAxis = histChart.getXAxis();
                xAxis.setValueFormatter(new DateAxisValueFormatter(dates));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                YAxis yAxis = histChart.getAxisLeft();
                yAxis.setValueFormatter(new PriceAxisValueFormatter());
                histChart.getAxisRight().setEnabled(false);

                histChart.invalidate();
            }
            cursor.close();
        }
    }
}