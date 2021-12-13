package com.landsapps.lott.olannds.owln.landsons;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SpreadsheetWebService {

    @POST("1FAIpQLSetJn4Lx5fzMx3n_dU2SD0UpY91wGKGb0DXzWN5De7tFx3Giw/formResponse")
    @FormUrlEncoded
    Call<Void> sendEmail(@Field("entry.177400936") String email);
    //com.landsapps.lott.olannds.owln.landsons
}
