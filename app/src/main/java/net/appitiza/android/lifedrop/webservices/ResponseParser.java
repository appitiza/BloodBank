package net.appitiza.android.lifedrop.webservices;

import com.google.gson.Gson;

import net.appitiza.android.lifedrop.model.RequestListModel;
import net.appitiza.android.lifedrop.model.Requestlist;
import net.appitiza.android.lifedrop.model.SearchListModel;
import net.appitiza.android.lifedrop.model.SignInModel;

public class ResponseParser {



    public static SignInModel parseSignIn(BaseResponseModel response) {
        Gson gson = new Gson();
        return gson.fromJson(response.getData().toString(), SignInModel.class);
    }

    public static SearchListModel parseSearch(BaseResponseModel response) {
        Gson gson = new Gson();
        return gson.fromJson(response.getData().toString(), SearchListModel.class);
    }
    public static RequestListModel parseMessage(BaseResponseModel response) {
        Gson gson = new Gson();
        return gson.fromJson(response.getData().toString(), RequestListModel.class);
    }
}
