package net.appitiza.android.lifedrop.webservices;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseResponseModel implements Serializable {
    @SerializedName("errorcode")
    private Integer errorcode;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private JsonObject data;

    public Integer getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(Integer errorcode) {
        this.errorcode = errorcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

}
