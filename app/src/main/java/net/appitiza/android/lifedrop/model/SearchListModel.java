package net.appitiza.android.lifedrop.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchListModel {

    @SerializedName("parcellist")
    @Expose
    private ArrayList<SearchItem> list = null;
    @SerializedName("next")
    @Expose
    private Integer next;

    public ArrayList<SearchItem> getList() {
        return list;
    }

    public void setList(ArrayList<SearchItem> parcellist) {
        this.list = parcellist;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

}
