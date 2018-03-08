package net.appitiza.android.lifedrop.webservices;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface ApiInterface {

    @POST("newUserRegistration.php")
    Call<BaseResponseModel> signup(@Body JsonObject params);

    @POST("login.php")
    Call<BaseResponseModel> signin(@Body JsonObject params);
    @POST("getSearchListMap.php")
    Call<BaseResponseModel> searchAround(@Body JsonObject params);
    @POST("getSearchList.php")
    Call<BaseResponseModel> searchBlood(@Body JsonObject params);
    @POST("getRequestList.php")
    Call<BaseResponseModel> searchMessage(@Body JsonObject params);
    @POST("addRequest.php")
    Call<BaseResponseModel> requestBlood(@Body JsonObject params);
    @POST("updateProfile.php")
    Call<BaseResponseModel> updateProfile(@Body JsonObject params);
    @POST("updatePassword.php")
    Call<BaseResponseModel> updatePassword(@Body JsonObject params);
    @POST("resetPassword.php")
    Call<BaseResponseModel> resetPassword(@Body JsonObject params);
}
