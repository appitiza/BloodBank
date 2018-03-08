package net.appitiza.android.lifedrop.webservices;



import net.appitiza.android.lifedrop.app.AppLog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Webservices {

    private static Webservices instance = null;
    private String TAG = Webservices.class.getSimpleName();

    public void callWebService(Call<BaseResponseModel> detailedResponseCall, final WebserviceCallBack mCallback, final int requestcode) {
        detailedResponseCall.enqueue(new Callback<BaseResponseModel>() {
            @Override
            public void onResponse(Call<BaseResponseModel> call, Response<BaseResponseModel> response) {
                AppLog.logString(TAG + " " + response.toString());
                if (response.isSuccessful()) {

                    mCallback.connectionSuccess(response.body(), requestcode);
                } else {
                    mCallback.connectionError(response.message(), requestcode);
                }
            }

            @Override
            public void onFailure(Call<BaseResponseModel> call, Throwable t) {
                mCallback.connectionError(t.toString(), requestcode);
                AppLog.logString(TAG + " " + t.toString());

            }
        });

    }

    public static synchronized Webservices getInstance() {

        if (instance == null) {
            instance = new Webservices();
        }
        return instance;
    }
}
