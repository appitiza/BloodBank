package net.appitiza.android.lifedrop.observer;

import java.io.Serializable;


public class ObserverData implements Serializable {
    int from;
    Object obj;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
