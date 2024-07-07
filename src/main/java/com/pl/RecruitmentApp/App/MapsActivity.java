package com.pl.RecruitmentApp.App;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pl.RecruitmentApp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MapsActivity extends AppCompatActivity {

    private EditText reportIdEditText;
    private Button findRouteButton;
    private ReportApi reportApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        reportIdEditText = findViewById(R.id.reportIdEditText);
        findRouteButton = findViewById(R.id.findRouteButton);

        Retrofit retrofit = ApiClient.getClient("http://localhost:8080/api/report/{id}");
        reportApi = retrofit.create(ReportApi.class);

        findRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reportIdStr = reportIdEditText.getText().toString();
                if (!reportIdStr.isEmpty()) {
                    Long reportId = Long.parseLong(reportIdStr);
                    fetchReport(reportId);
                } else {
                    Toast.makeText(MapsActivity.this, "Please enter a report ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchReport(Long reportId) {
        Call<Report> call = reportApi.getReportById(reportId);
        call.enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful()) {
                    Report report = response.body();
                    if (report != null) {
                        String reportAddress = report.getReportAddress();
                        showRouteInGoogleMaps(reportAddress);
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Report not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Error fetching report", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRouteInGoogleMaps(String address) {
        Uri gmmIntentUri = Uri.parse("https://routes.googleapis.com/directions/v2:computeRoutes?key=RecruitmentTestApi/" + address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }
}
