package com.pl.RecruitmentApp.App;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ReportApi {
    @GET("/api/reports/{id}")
    Call<Report> getReportById(@Path("id") Long id);
}

