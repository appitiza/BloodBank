package net.appitiza.android.lifedrop.webservices;

import com.google.gson.JsonObject;

import net.appitiza.android.lifedrop.app.AppLog;

import retrofit2.Call;

public class WebserviceHandler {

    private static ApiInterface getApiInterface() {
        return Connector.getInstance().getConnector().create(ApiInterface.class);
    }


    public static void signUp(String fcmid, String email, String firstname, String lastname, String password, String mobile, String latitude, String longitude, String address, String gender, String blood, String medical_issue, String lastdonation, final WebserviceCallBack mCallback, int requestcode) {


        JsonObject signUpRequest = new JsonObject();
        signUpRequest.addProperty("fcmid", fcmid);
        signUpRequest.addProperty("email_id", email);
        signUpRequest.addProperty("firstname", firstname);
        signUpRequest.addProperty("lastname", lastname);
        signUpRequest.addProperty("password", password);
        signUpRequest.addProperty("mobile", mobile);
        signUpRequest.addProperty("latitude", latitude);
        signUpRequest.addProperty("longitude", longitude);
        signUpRequest.addProperty("blood", blood);
        signUpRequest.addProperty("gender", gender);
        signUpRequest.addProperty("last_donation", lastdonation);
        signUpRequest.addProperty("medical_issue", medical_issue);
        signUpRequest.addProperty("address", address);
        AppLog.logErrorString("request " + signUpRequest.toString());

        Call<BaseResponseModel> detailedResponseCall = getApiInterface().signup(signUpRequest);
        Webservices.getInstance().callWebService(detailedResponseCall, mCallback, requestcode);
    }


    public static void signIn(String fcmid, String email, String password, final WebserviceCallBack mCallback, int requestcode) {
        JsonObject signInRequest = new JsonObject();
        signInRequest.addProperty("email_id", email);
        signInRequest.addProperty("password", password);
        signInRequest.addProperty("fcm_id", fcmid);
        AppLog.logErrorString("request " + signInRequest.toString());

        Call<BaseResponseModel> detailedResponseCall = getApiInterface().signin(signInRequest);
        Webservices.getInstance().callWebService(detailedResponseCall, mCallback, requestcode);
    }

    public static void searchAround(String latitude, String longitude, final WebserviceCallBack mCallback, int requestcode) {
        JsonObject signInRequest = new JsonObject();
        signInRequest.addProperty("latitude", latitude);
        signInRequest.addProperty("longitude", longitude);
        AppLog.logErrorString("request " + signInRequest.toString());

        Call<BaseResponseModel> detailedResponseCall = getApiInterface().searchAround(signInRequest);
        Webservices.getInstance().callWebService(detailedResponseCall, mCallback, requestcode);
    }

    public static void searchBlood(String latitude, String longitude, String pid, String blood, final WebserviceCallBack mCallback, int requestcode) {
        JsonObject signInRequest = new JsonObject();
        signInRequest.addProperty("latitude", latitude);
        signInRequest.addProperty("longitude", longitude);
        signInRequest.addProperty("pid", pid);
        signInRequest.addProperty("blood", blood);
        AppLog.logErrorString("request " + signInRequest.toString());

        Call<BaseResponseModel> detailedResponseCall = getApiInterface().searchBlood(signInRequest);
        Webservices.getInstance().callWebService(detailedResponseCall, mCallback, requestcode);
    }

    public static void searchMessage(String pid, final WebserviceCallBack mCallback, int requestcode) {
        JsonObject signInRequest = new JsonObject();
        signInRequest.addProperty("pid", pid);
        AppLog.logErrorString("request " + signInRequest.toString());

        Call<BaseResponseModel> detailedResponseCall = getApiInterface().searchMessage(signInRequest);
        Webservices.getInstance().callWebService(detailedResponseCall, mCallback, requestcode);
    }

    public static void requestBlood(String email,String blood,String message,String address,String latitude, String longitude,String req_date, final WebserviceCallBack mCallback, int requestcode) {
        JsonObject signInRequest = new JsonObject();
        signInRequest.addProperty("email", email);
        signInRequest.addProperty("blood", blood);
        signInRequest.addProperty("message", message);
        signInRequest.addProperty("address", address);
        signInRequest.addProperty("latitude", latitude);
        signInRequest.addProperty("longitude", longitude);
        signInRequest.addProperty("req_date", req_date);
        AppLog.logErrorString("request " + signInRequest.toString());

        Call<BaseResponseModel> detailedResponseCall = getApiInterface().requestBlood(signInRequest);
        Webservices.getInstance().callWebService(detailedResponseCall, mCallback, requestcode);
    }
    public static void updateProfile(String fcmid, String email, String firstname, String lastname, String password, String mobile, String latitude, String longitude, String address, String gender, String blood, String medical_issue, String lastdonation, final WebserviceCallBack mCallback, int requestcode) {


        JsonObject signUpRequest = new JsonObject();
        signUpRequest.addProperty("fcmid", fcmid);
        signUpRequest.addProperty("email_id", email);
        signUpRequest.addProperty("firstname", firstname);
        signUpRequest.addProperty("lastname", lastname);
        signUpRequest.addProperty("password", password);
        signUpRequest.addProperty("mobile", mobile);
        signUpRequest.addProperty("latitude", latitude);
        signUpRequest.addProperty("longitude", longitude);
        signUpRequest.addProperty("blood", blood);
        signUpRequest.addProperty("gender", gender);
        signUpRequest.addProperty("last_donation", lastdonation);
        signUpRequest.addProperty("medical_issue", medical_issue);
        signUpRequest.addProperty("address", address);
        AppLog.logErrorString("request " + signUpRequest.toString());

        Call<BaseResponseModel> detailedResponseCall = getApiInterface().updateProfile(signUpRequest);
        Webservices.getInstance().callWebService(detailedResponseCall, mCallback, requestcode);
    }

    public static void updatePassword(String fcmid, String email,String old_password, String password, final WebserviceCallBack mCallback, int requestcode) {


        JsonObject signUpRequest = new JsonObject();
        signUpRequest.addProperty("fcmid", fcmid);
        signUpRequest.addProperty("email_id", email);
        signUpRequest.addProperty("password", password);
        signUpRequest.addProperty("old_password", old_password);
        AppLog.logErrorString("request " + signUpRequest.toString());

        Call<BaseResponseModel> detailedResponseCall = getApiInterface().updatePassword(signUpRequest);
        Webservices.getInstance().callWebService(detailedResponseCall, mCallback, requestcode);
    }
    public static void resetPassword(String fcmid, String email,String dob, String password, final WebserviceCallBack mCallback, int requestcode) {


        JsonObject signUpRequest = new JsonObject();
        signUpRequest.addProperty("fcmid", fcmid);
        signUpRequest.addProperty("email_id", email);
        signUpRequest.addProperty("password", password);
        signUpRequest.addProperty("dob", dob);
        AppLog.logErrorString("request " + signUpRequest.toString());

        Call<BaseResponseModel> detailedResponseCall = getApiInterface().resetPassword(signUpRequest);
        Webservices.getInstance().callWebService(detailedResponseCall, mCallback, requestcode);
    }


}
