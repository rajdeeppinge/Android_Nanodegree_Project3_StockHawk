package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.SpinnerAdapter;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by root on 6/18/16.
 */
public class StockGraphsActivity extends AppCompatActivity {

    public static final String TAG = StockGraphsActivity.class.getSimpleName();

    private ValueLineChart lineChart;

    private String stockSymbol;
    private ArrayList<String> dateList;
    private ArrayList<Float> closingValueList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        stockSymbol = getIntent().getStringExtra(getResources().getString(R.string.stock_symbol));
        getSupportActionBar().setTitle(stockSymbol);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lineChart = (ValueLineChart) findViewById(R.id.linechart);

        Spinner graphPeriodSpinner  = (Spinner) findViewById(R.id.time_period_spinner);

        ArrayList<String> timePeriod = new ArrayList<>();
        timePeriod.add(getResources().getString(R.string.one_month));
        timePeriod.add(getResources().getString(R.string.three_months));
        timePeriod.add(getResources().getString(R.string.six_months));
        timePeriod.add(getResources().getString(R.string.one_year));
        timePeriod.add(getResources().getString(R.string.two_years));
        timePeriod.add(getResources().getString(R.string.five_years));

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getApplicationContext(), timePeriod);
        graphPeriodSpinner.setAdapter(spinnerAdapter);

        graphPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosen_period = parent.getItemAtPosition(position).toString();

                if (chosen_period == getResources().getString(R.string.three_months)) {
                        lineChart.clearChart();
                        downloadStockDetails(getResources().getString(R.string.three_months_query));
                }
                else if(chosen_period == getResources().getString(R.string.six_months)) {
                    lineChart.clearChart();
                    downloadStockDetails(getResources().getString(R.string.six_months_query));
                }
                else if(chosen_period == getResources().getString(R.string.one_year)) {
                    lineChart.clearChart();
                    downloadStockDetails(getResources().getString(R.string.one_year_query));
                }
                else if(chosen_period == getResources().getString(R.string.two_years)) {
                    lineChart.clearChart();
                    downloadStockDetails(getResources().getString(R.string.two_years_query));
                }
                else if(chosen_period == getResources().getString(R.string.five_years)) {
                    lineChart.clearChart();
                    downloadStockDetails(getResources().getString(R.string.five_years_query));
                }
                else {//by default do it for 1 month
                    lineChart.clearChart();
                    downloadStockDetails(getResources().getString(R.string.one_month_query));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return false;
        }
    }

    private void downloadStockDetails(String chosen_period) {
        OkHttpClient client = new OkHttpClient();

        final String BASE_URL = getResources().getString(R.string.api_base_url);
        final String DATA_DETAILS = getResources().getString(R.string.api_data_details);
        final String DATA_TYPE_STRING = getResources().getString(R.string.api_data_type_string);
        final String DATA_TYPE = getResources().getString(R.string.api_data_type);
        final String TIME_PERIOD_RANGE_STRING = getResources().getString(R.string.api_time_period_range_string);
        final String DATA_FORMAT = getResources().getString(R.string.api_data_format);

        Request request = new Request.Builder()
                .url(BASE_URL + stockSymbol + "/" + DATA_DETAILS + ";" + DATA_TYPE_STRING + "=" + DATA_TYPE + ";" + TIME_PERIOD_RANGE_STRING + "=" + chosen_period + "/" + DATA_FORMAT)
                .build();

        final String JSON_OBJ_SERIES_TAG = getResources().getString(R.string.json_object_series_tag);
        final String SIMPLE_DATE_FORMAT = getResources().getString(R.string.simple_date_format);
        final String JSON_OBJ_SERIES_DATE_TAG = getResources().getString(R.string.json_object_series_date_tag);
        final String JSON_OBJ_SERIES_STOCK_CLOSE_PRICE_TAG = getResources().getString(R.string.json_object_series_stock_close_price_tag);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) { //on Success
                    try {
                        String result = response.body().string();

                        String rawJsonData = null;

                        // removing the unwanted content to get the JSON Object in { ... }
                        if (result.startsWith("finance_charts_json_callback")) {
                            rawJsonData = result.substring(29, result.length() - 2);
                        }

                        JSONObject graphData = new JSONObject(rawJsonData);
                        JSONArray graphDataSeries = graphData.getJSONArray(JSON_OBJ_SERIES_TAG);

                        dateList = new ArrayList<>();
                        closingValueList = new ArrayList<>();
                        for (int i = 0; i < graphDataSeries.length(); i++) {
                            JSONObject seriesItem = graphDataSeries.getJSONObject(i);

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
                            Date date = simpleDateFormat.parse(seriesItem.getString(JSON_OBJ_SERIES_DATE_TAG));
                            String dateString = android.text.format.DateFormat.getMediumDateFormat(getApplicationContext()).format(date);

                            dateList.add(dateString);
                            closingValueList.add(Float.parseFloat(seriesItem.getString(JSON_OBJ_SERIES_STOCK_CLOSE_PRICE_TAG)));
                        }

                        StockGraphsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ValueLineSeries graph = new ValueLineSeries();

                                for (int i = 0; i < dateList.size(); i++) {
                                    graph.addPoint(new ValueLinePoint(dateList.get(i), closingValueList.get(i)));
                                }

                                graph.setColor(getResources().getColor(R.color.material_blue_500));
                                lineChart.addSeries(graph);
                                lineChart.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                    catch (Exception e) {
                        downloadError();
                        e.printStackTrace();
                    }

                }
                else {
                    downloadError();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                downloadError();
            }
        });
    }

    private void downloadError() {
        StockGraphsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.download_graph_error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
