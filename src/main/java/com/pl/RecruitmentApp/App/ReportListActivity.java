package com.pl.RecruitmentApp.App;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pl.RecruitmentApp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportListActivity extends AppCompatActivity {

    private ListView listViewReports;
    private ArrayList<String> reportList;
    private ArrayAdapter<String> adapter;
    private String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        ListView listViewReports = findViewById(R.id.listViewReports);
        reportList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reportList);
        listViewReports.setAdapter(adapter);

        username = getIntent().getStringExtra("username");

        fetchReports();

        listViewReports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ReportListActivity.this, ReportDetailActivity.class);
                intent.putExtra("reportId", reportList.get(position).split(" ")[0]);
                startActivity(intent);
            }
        });
    }

    private void fetchReports() {
        String url = "http://localhost:8080/api/reports/user/" + username;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject report = response.getJSONObject(i);
                                String reportItem = report.getLong("id") + " - " + report.getString("content");
                                reportList.add(reportItem);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(jsonArrayRequest);
    }
}
