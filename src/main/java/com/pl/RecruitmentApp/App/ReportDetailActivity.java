package com.pl.RecruitmentApp.App;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pl.RecruitmentApp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReportDetailActivity extends AppCompatActivity {

    private EditText editTextReportUser;
    private EditText editTextContent;
    private EditText editTextReportAddress;
    private Button buttonSave;
    private Button buttonNavigate;
    private long reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        editTextReportUser = findViewById(R.id.editTextReportUser);
        editTextContent = findViewById(R.id.editTextContent);
        editTextReportAddress = findViewById(R.id.editTextReportAddress);
        buttonSave = findViewById(R.id.buttonSave);
        buttonNavigate = findViewById(R.id.buttonNavigate);

        reportId = getIntent().getLongExtra("reportId", -1);

        fetchReportDetails();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReport();
            }
        });

        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToReport();
            }
        });
    }

    private void fetchReportDetails() {
        String url = "http://localhost:8080/api/reports" + reportId;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject report = new JSONObject(response);
                            editTextReportUser.setText(report.getString("reportUser"));
                            editTextContent.setText(report.getString("content"));
                            editTextReportAddress.setText(report.getString("reportAddress"));
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

        queue.add(stringRequest);
    }

    private void saveReport() {
        String url = "http://localhost:8080/home/api/reports/edit/{id}" + reportId;

        String reportUser = editTextReportUser.getText().toString();
        String content = editTextContent.getText().toString();
        String reportAddress = editTextReportAddress.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ReportDetailActivity.this, "Raport zaktualizowany pomyślnie", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("reportUser", reportUser);
                params.put("content", content);
                params.put("reportAddress", reportAddress);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void navigateToReport() {
        String address = editTextReportAddress.getText().toString();
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
