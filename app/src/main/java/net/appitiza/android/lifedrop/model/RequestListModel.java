package net.appitiza.android.lifedrop.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RequestListModel {

    @SerializedName("requestlist")
    @Expose
    private List<Requestlist> requestlist = null;
    @SerializedName("next")
    @Expose
    private Integer next;

    public List<Requestlist> getRequestlist() {
        return requestlist;
    }

    public void setRequestlist(List<Requestlist> requestlist) {
        this.requestlist = requestlist;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

}
