package net.appitiza.android.lifedrop.webservices;




public interface WebserviceCallBack {
    void connectionSuccess(BaseResponseModel response, int requestCode);
    void connectionError(String msg, int requestCode);
}
